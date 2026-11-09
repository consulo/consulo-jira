/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.jira;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import consulo.http.client.HttpStatusErrorException;
import consulo.jira.localize.JiraLocalize;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.task.CustomTaskState;
import consulo.task.LocalTask;
import consulo.task.Task;
import consulo.task.TaskRepository;
import consulo.task.issue.IssueAssigneeSupport;
import consulo.task.issue.IssueAttachment;
import consulo.task.issue.IssueAttachmentSupport;
import consulo.task.issue.IssueCapability;
import consulo.task.issue.IssueComment;
import consulo.task.issue.IssueCommentSupport;
import consulo.task.issue.IssueDetails;
import consulo.task.issue.IssueField;
import consulo.task.issue.IssueLink;
import consulo.task.issue.IssueLinkSupport;
import consulo.task.issue.IssuePage;
import consulo.task.issue.IssuePrioritySupport;
import consulo.task.issue.IssueQuery;
import consulo.task.issue.IssueStateSupport;
import consulo.task.issue.IssueTracker;
import consulo.task.issue.IssueWorkLog;
import consulo.task.issue.IssueWorkLogSupport;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author VISTALL
 * @since 2026-11-09
 */
public class JiraTaskRepository extends TaskRepository {
    private static final String SEARCH_FIELDS = "summary,description,issuetype,status,priority,assignee,created,updated,resolution,project";

    private static final Pattern ISSUE_KEY = Pattern.compile("[A-Z][A-Z0-9_]*-\\d+");
    private static final Pattern JQL_OPERATOR = Pattern.compile("(?i)(=|~|!=|\\bin\\b|\\bis\\b|\\border\\s+by\\b)");
    private static final Pattern ORDER_BY = Pattern.compile("(?i)\\border\\s+by\\b");

    private final Tracker myTracker = new Tracker();

    /**
     * Jira Cloud dropped the offset search for one paged by tokens, the server and data center editions only know the
     * former. The answer of the first search says which of the two this server is.
     */
    private volatile boolean myTokenSearch;

    private final Map<String, String> myPageTokens = new ConcurrentHashMap<>();

    private final Map<String, JsonObject> myAssignableUsers = new ConcurrentHashMap<>();

    public JiraTaskRepository(JiraRepositoryType type, Project project, JiraRepositoryOptions options) {
        super(type, project, options);
    }

    @Override
    public JiraRepositoryOptions getOptions() {
        return (JiraRepositoryOptions) super.getOptions();
    }

    @Override
    public IssueTracker getIssueTracker() {
        return myTracker;
    }

    private CompletableFuture<JiraClient> client() {
        return connect().thenApply(JiraClient::create);
    }

    private boolean isCloud() {
        return myTokenSearch || getServerUrl().contains(".atlassian.net");
    }

    String buildJql(IssueQuery query) {
        List<String> clauses = new ArrayList<>();
        String order = "ORDER BY updated DESC";

        JiraRepositoryOptions options = getOptions();
        if (!StringUtil.isEmptyOrSpaces(options.projectKey)) {
            clauses.add("project = \"" + escape(options.projectKey.trim()) + "\"");
        }

        String[] filter = splitOrder(options.jql);
        if (filter[0] != null) {
            clauses.add("(" + filter[0] + ")");
        }
        if (filter[1] != null) {
            order = filter[1];
        }

        String text = StringUtil.notNullize(query.getText()).trim();
        if (!text.isEmpty()) {
            if (ISSUE_KEY.matcher(text).matches()) {
                clauses.add("key = \"" + text + "\"");
            }
            else if (JQL_OPERATOR.matcher(text).find()) {
                String[] typed = splitOrder(text);
                if (typed[0] != null) {
                    clauses.add("(" + typed[0] + ")");
                }
                if (typed[1] != null) {
                    order = typed[1];
                }
            }
            else {
                clauses.add("text ~ \"" + escape(text) + "\"");
            }
        }

        if (!query.isIncludeClosed()) {
            clauses.add("statusCategory != Done");
        }

        return String.join(" AND ", clauses) + (clauses.isEmpty() ? "" : " ") + order;
    }

    private static String[] splitOrder(@Nullable String jql) {
        String value = StringUtil.notNullize(jql).trim();
        if (value.isEmpty()) {
            return new String[]{null, null};
        }
        Matcher matcher = ORDER_BY.matcher(value);
        if (!matcher.find()) {
            return new String[]{value, null};
        }
        String condition = value.substring(0, matcher.start()).trim();
        return new String[]{condition.isEmpty() ? null : condition, value.substring(matcher.start()).trim()};
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private List<Task> toTasks(@Nullable JsonObject answer) {
        List<Task> tasks = new ArrayList<>();
        for (JsonObject issue : JiraJson.objects(answer, "issues")) {
            tasks.add(new JiraTask(this, issue));
        }
        return tasks;
    }

    private static Throwable unwrap(Throwable throwable) {
        Throwable result = throwable;
        while (result instanceof CompletionException && result.getCause() != null) {
            result = result.getCause();
        }
        return result;
    }

    private static boolean isStatus(Throwable throwable, int status) {
        return unwrap(throwable) instanceof HttpStatusErrorException error && error.getStatusCode() == status;
    }

    private static JsonObject keyRef(String key) {
        JsonObject object = new JsonObject();
        object.addProperty("key", key);
        return object;
    }

    private static JsonObject nameRef(String name) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        return object;
    }

    private static String issuePath(Task task) {
        return "issue/" + JiraClient.encode(task.getId());
    }

    private class Tracker implements IssueTracker, IssueStateSupport, IssueWorkLogSupport, IssueCommentSupport,
        IssueAttachmentSupport, IssueLinkSupport, IssueAssigneeSupport, IssuePrioritySupport {
        @Override
        public Set<IssueCapability> getCapabilities() {
            return EnumSet.allOf(IssueCapability.class);
        }

        @Override
        public CompletableFuture<IssuePage> search(IssueQuery query, int offset, int limit) {
            String jql = buildJql(query);
            return client().thenCompose(client -> {
                if (myTokenSearch) {
                    return tokenSearch(client, jql, offset, limit);
                }
                return offsetSearch(client, jql, offset, limit).exceptionallyCompose(error -> {
                    if (isStatus(error, 410) || isStatus(error, 404)) {
                        myTokenSearch = true;
                        return tokenSearch(client, jql, offset, limit);
                    }
                    return CompletableFuture.failedFuture(unwrap(error));
                });
            });
        }

        private CompletableFuture<IssuePage> offsetSearch(JiraClient client, String jql, int offset, int limit) {
            JsonObject body = new JsonObject();
            body.addProperty("jql", jql);
            body.addProperty("startAt", offset);
            body.addProperty("maxResults", limit);
            body.add("fields", fields());
            return client.post("search", body).thenApply(element -> {
                JsonObject answer = element.isJsonObject() ? element.getAsJsonObject() : null;
                List<Task> tasks = toTasks(answer);
                long total = JiraJson.number(answer, "total");
                return new IssuePage(tasks, offset + tasks.size() < total);
            });
        }

        /**
         * Pages by tokens can only be walked forward, so the token each page ends with is kept to answer the next
         * offset; an offset nothing ended at is reached by walking from the closest one that did.
         */
        private CompletableFuture<IssuePage> tokenSearch(JiraClient client, String jql, int offset, int limit) {
            int known = 0;
            for (Map.Entry<String, String> entry : myPageTokens.entrySet()) {
                String prefix = jql + "@";
                if (entry.getKey().startsWith(prefix)) {
                    int at = Integer.parseInt(entry.getKey().substring(prefix.length()));
                    if (at <= offset && at > known) {
                        known = at;
                    }
                }
            }

            if (known == offset) {
                return tokenPage(client, jql, offset, limit);
            }

            int skip = offset - known;
            return tokenPage(client, jql, known, skip).thenCompose(skipped -> {
                if (!skipped.hasMore()) {
                    return CompletableFuture.completedFuture(new IssuePage(List.of(), false));
                }
                return tokenSearch(client, jql, offset, limit);
            });
        }

        private CompletableFuture<IssuePage> tokenPage(JiraClient client, String jql, int offset, int limit) {
            JsonObject body = new JsonObject();
            body.addProperty("jql", jql);
            body.addProperty("maxResults", limit);
            body.add("fields", fields());
            String token = offset == 0 ? null : myPageTokens.get(jql + "@" + offset);
            if (token != null) {
                body.addProperty("nextPageToken", token);
            }
            return client.post("search/jql", body).thenApply(element -> {
                JsonObject answer = element.isJsonObject() ? element.getAsJsonObject() : null;
                List<Task> tasks = toTasks(answer);
                String next = JiraJson.string(answer, "nextPageToken");
                if (next != null) {
                    myPageTokens.put(jql + "@" + (offset + tasks.size()), next);
                }
                return new IssuePage(tasks, next != null && !tasks.isEmpty());
            });
        }

        private JsonArray fields() {
            JsonArray fields = new JsonArray();
            for (String field : SEARCH_FIELDS.split(",")) {
                fields.add(field);
            }
            return fields;
        }

        @Override
        public CompletableFuture<@Nullable Task> findIssue(String id) {
            return client()
                .thenCompose(client -> client.get("issue/" + JiraClient.encode(id) + "?fields=" + SEARCH_FIELDS))
                .<@Nullable Task>thenApply(element -> element.isJsonObject() ? new JiraTask(JiraTaskRepository.this, element.getAsJsonObject()) : null)
                .exceptionallyCompose(error -> isStatus(error, 404)
                    ? CompletableFuture.completedFuture(null)
                    : CompletableFuture.failedFuture(unwrap(error)));
        }

        @Override
        public CompletableFuture<IssueDetails> loadDetails(Task task) {
            return client().thenCompose(client -> client.get(issuePath(task) + "?fields=*all")).thenApply(element -> {
                JiraTask issue = new JiraTask(JiraTaskRepository.this, element.getAsJsonObject());
                JsonObject fields = issue.getFields();
                return new IssueDetails(issue, fields(fields), comments(fields), workLogs(fields), attachments(fields), links(fields), subtasks(fields));
            });
        }

        private List<IssueField> fields(JsonObject fields) {
            List<IssueField> result = new ArrayList<>();
            add(result, JiraLocalize.fieldStatus(), JiraJson.name(fields, "status"));
            add(result, JiraLocalize.fieldType(), JiraJson.name(fields, "issuetype"));
            add(result, JiraLocalize.fieldPriority(), JiraJson.name(fields, "priority"));
            String assignee = JiraJson.userName(JiraJson.object(fields, "assignee"));
            add(result, JiraLocalize.fieldAssignee(), assignee == null ? JiraLocalize.fieldUnassigned().get() : assignee);
            add(result, JiraLocalize.fieldReporter(), JiraJson.userName(JiraJson.object(fields, "reporter")));
            add(result, JiraLocalize.fieldResolution(), JiraJson.name(fields, "resolution"));
            add(result, JiraLocalize.fieldLabels(), String.join(", ", JiraJson.strings(fields, "labels")));
            add(result, JiraLocalize.fieldComponents(), String.join(", ", JiraJson.strings(fields, "components")));
            add(result, JiraLocalize.fieldFixVersions(), String.join(", ", JiraJson.strings(fields, "fixVersions")));
            add(result, JiraLocalize.fieldDueDate(), JiraJson.string(fields, "duedate"));
            return result;
        }

        private void add(List<IssueField> result, LocalizeValue name, @Nullable String value) {
            if (!StringUtil.isEmptyOrSpaces(value)) {
                result.add(new IssueField(name, value));
            }
        }

        private List<IssueComment> comments(JsonObject fields) {
            List<IssueComment> result = new ArrayList<>();
            for (JsonObject comment : JiraJson.objects(JiraJson.object(fields, "comment"), "comments")) {
                result.add(new IssueComment(
                    StringUtil.notNullize(JiraJson.string(comment, "id")),
                    JiraJson.userName(JiraJson.object(comment, "author")),
                    JiraJson.date(comment, "created"),
                    StringUtil.notNullize(JiraJson.string(comment, "body"))
                ));
            }
            return result;
        }

        private List<IssueWorkLog> workLogs(JsonObject fields) {
            List<IssueWorkLog> result = new ArrayList<>();
            for (JsonObject workLog : JiraJson.objects(JiraJson.object(fields, "worklog"), "worklogs")) {
                result.add(new IssueWorkLog(
                    StringUtil.notNullize(JiraJson.string(workLog, "id")),
                    JiraJson.userName(JiraJson.object(workLog, "author")),
                    JiraJson.date(workLog, "started"),
                    StringUtil.notNullize(JiraJson.string(workLog, "timeSpent")),
                    StringUtil.notNullize(JiraJson.string(workLog, "comment"))
                ));
            }
            return result;
        }

        private List<IssueAttachment> attachments(JsonObject fields) {
            List<IssueAttachment> result = new ArrayList<>();
            for (JsonObject attachment : JiraJson.objects(fields, "attachment")) {
                result.add(new IssueAttachment(
                    StringUtil.notNullize(JiraJson.string(attachment, "id")),
                    StringUtil.notNullize(JiraJson.string(attachment, "filename")),
                    JiraJson.number(attachment, "size"),
                    JiraJson.string(attachment, "content")
                ));
            }
            return result;
        }

        private List<IssueLink> links(JsonObject fields) {
            List<IssueLink> result = new ArrayList<>();
            for (JsonObject link : JiraJson.objects(fields, "issuelinks")) {
                JsonObject type = JiraJson.object(link, "type");
                JsonObject outward = JiraJson.object(link, "outwardIssue");
                JsonObject other = outward != null ? outward : JiraJson.object(link, "inwardIssue");
                if (other == null) {
                    continue;
                }
                String description = JiraJson.string(type, outward != null ? "outward" : "inward");
                result.add(new IssueLink(
                    StringUtil.notNullize(JiraJson.string(link, "id")),
                    StringUtil.notNullize(description, StringUtil.notNullize(JiraJson.string(type, "name"))),
                    StringUtil.notNullize(JiraJson.string(other, "key")),
                    StringUtil.notNullize(JiraJson.string(JiraJson.object(other, "fields"), "summary"))
                ));
            }
            return result;
        }

        private List<Task> subtasks(JsonObject fields) {
            List<Task> result = new ArrayList<>();
            for (JsonObject subtask : JiraJson.objects(fields, "subtasks")) {
                result.add(new JiraTask(JiraTaskRepository.this, subtask));
            }
            return result;
        }

        @Override
        public CompletableFuture<Set<CustomTaskState>> getAvailableStates(Task task) {
            return client().thenCompose(client -> client.get(issuePath(task) + "/transitions")).thenApply(element -> {
                Set<CustomTaskState> states = new LinkedHashSet<>();
                for (JsonObject transition : JiraJson.objects(element.getAsJsonObject(), "transitions")) {
                    String id = JiraJson.string(transition, "id");
                    String name = JiraJson.string(transition, "name");
                    if (id != null && name != null) {
                        states.add(new CustomTaskState(id, name));
                    }
                }
                return states;
            });
        }

        @Override
        public CompletableFuture<?> setState(Task task, CustomTaskState state) {
            JsonObject transition = new JsonObject();
            transition.addProperty("id", state.getId());
            JsonObject body = new JsonObject();
            body.add("transition", transition);
            return client().thenCompose(client -> client.post(issuePath(task) + "/transitions", body));
        }

        @Override
        public CompletableFuture<?> logWork(LocalTask task, String timeSpent, String comment) {
            return addWorkLog(task, timeSpent, comment);
        }

        @Override
        public CompletableFuture<?> addWorkLog(Task task, String timeSpent, String comment) {
            return client().thenCompose(client -> client.post(issuePath(task) + "/worklog", workLog(timeSpent, comment)));
        }

        @Override
        public CompletableFuture<?> editWorkLog(Task task, IssueWorkLog workLog, String timeSpent, String comment) {
            return client().thenCompose(client ->
                client.put(issuePath(task) + "/worklog/" + JiraClient.encode(workLog.getId()), workLog(timeSpent, comment)));
        }

        @Override
        public CompletableFuture<?> deleteWorkLog(Task task, IssueWorkLog workLog) {
            return client().thenCompose(client -> client.delete(issuePath(task) + "/worklog/" + JiraClient.encode(workLog.getId())));
        }

        private JsonObject workLog(String timeSpent, String comment) {
            JsonObject body = new JsonObject();
            body.addProperty("timeSpent", timeSpent);
            if (!comment.isEmpty()) {
                body.addProperty("comment", comment);
            }
            return body;
        }

        @Override
        public CompletableFuture<?> addComment(Task task, String text) {
            return client().thenCompose(client -> client.post(issuePath(task) + "/comment", comment(text)));
        }

        @Override
        public CompletableFuture<?> editComment(Task task, IssueComment comment, String text) {
            return client().thenCompose(client -> client.put(issuePath(task) + "/comment/" + JiraClient.encode(comment.getId()), comment(text)));
        }

        @Override
        public CompletableFuture<?> deleteComment(Task task, IssueComment comment) {
            return client().thenCompose(client -> client.delete(issuePath(task) + "/comment/" + JiraClient.encode(comment.getId())));
        }

        private JsonObject comment(String text) {
            JsonObject body = new JsonObject();
            body.addProperty("body", text);
            return body;
        }

        @Override
        public CompletableFuture<?> addAttachment(Task task, Path file) {
            return client().thenCompose(client -> client.upload(issuePath(task) + "/attachments", file));
        }

        @Override
        public CompletableFuture<?> deleteAttachment(Task task, IssueAttachment attachment) {
            return client().thenCompose(client -> client.delete("attachment/" + JiraClient.encode(attachment.getId())));
        }

        /**
         * Both directions of every link type, the way the issue reads it: "blocks" and "is blocked by".
         */
        @Override
        public CompletableFuture<List<String>> getLinkTypes(Task task) {
            return linkTypes().thenApply(types -> new ArrayList<>(types.keySet()));
        }

        private CompletableFuture<Map<String, LinkDirection>> linkTypes() {
            return client().thenCompose(client -> client.get("issueLinkType")).thenApply(element -> {
                Map<String, LinkDirection> types = new LinkedHashMap<>();
                for (JsonObject type : JiraJson.objects(element.getAsJsonObject(), "issueLinkTypes")) {
                    String name = JiraJson.string(type, "name");
                    if (name == null) {
                        continue;
                    }
                    String outward = StringUtil.notNullize(JiraJson.string(type, "outward"), name);
                    String inward = StringUtil.notNullize(JiraJson.string(type, "inward"), name);
                    types.putIfAbsent(outward, new LinkDirection(name, true));
                    types.putIfAbsent(inward, new LinkDirection(name, false));
                }
                return types;
            });
        }

        @Override
        public CompletableFuture<?> addLink(Task task, String type, String issueId) {
            return linkTypes().thenCompose(types -> {
                LinkDirection direction = types.get(type);
                if (direction == null) {
                    return CompletableFuture.failedFuture(new IllegalArgumentException("Unknown link type " + type));
                }
                JsonObject body = new JsonObject();
                body.add("type", nameRef(direction.name()));
                body.add(direction.outward() ? "inwardIssue" : "outwardIssue", keyRef(task.getId()));
                body.add(direction.outward() ? "outwardIssue" : "inwardIssue", keyRef(issueId.trim().toUpperCase(Locale.ROOT)));
                return client().thenCompose(client -> client.post("issueLink", body));
            });
        }

        @Override
        public CompletableFuture<?> deleteLink(Task task, IssueLink link) {
            return client().thenCompose(client -> client.delete("issueLink/" + JiraClient.encode(link.getId())));
        }

        @Override
        public CompletableFuture<List<String>> getAssignableUsers(Task task) {
            return client()
                .thenCompose(client -> client.get("user/assignable/search?maxResults=100&issueKey=" + JiraClient.encode(task.getId())))
                .thenApply(element -> {
                    List<String> names = new ArrayList<>();
                    if (element.isJsonArray()) {
                        for (JsonElement item : element.getAsJsonArray()) {
                            if (!item.isJsonObject()) {
                                continue;
                            }
                            JsonObject user = item.getAsJsonObject();
                            String name = JiraJson.userName(user);
                            if (name != null) {
                                myAssignableUsers.put(name, user);
                                names.add(name);
                            }
                        }
                    }
                    return names;
                });
        }

        @Override
        public CompletableFuture<?> assign(Task task, @Nullable String user) {
            JsonObject body = new JsonObject();
            JsonObject found = user == null ? null : myAssignableUsers.get(user);
            if (found == null) {
                body.add(isCloud() ? "accountId" : "name", JsonNull.INSTANCE);
            }
            else if (found.has("accountId")) {
                body.addProperty("accountId", JiraJson.string(found, "accountId"));
            }
            else {
                body.addProperty("name", JiraJson.string(found, "name"));
            }
            return client().thenCompose(client -> client.put(issuePath(task) + "/assignee", body));
        }

        @Override
        public CompletableFuture<List<String>> getPriorities(Task task) {
            return client().thenCompose(client -> client.get("priority")).thenApply(element -> {
                List<String> names = new ArrayList<>();
                if (element.isJsonArray()) {
                    for (JsonElement item : element.getAsJsonArray()) {
                        String name = item.isJsonObject() ? JiraJson.string(item.getAsJsonObject(), "name") : null;
                        if (name != null) {
                            names.add(name);
                        }
                    }
                }
                return names;
            });
        }

        @Override
        public CompletableFuture<?> setPriority(Task task, String priority) {
            JsonObject fields = new JsonObject();
            fields.add("priority", nameRef(priority));
            JsonObject body = new JsonObject();
            body.add("fields", fields);
            return client().thenCompose(client -> client.put(issuePath(task), body));
        }
    }

    private record LinkDirection(String name, boolean outward) {
    }
}
