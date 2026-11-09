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

import com.google.gson.JsonObject;
import consulo.task.Comment;
import consulo.task.Task;
import consulo.task.TaskRepository;
import consulo.task.TaskState;
import consulo.task.TaskType;
import consulo.task.icon.TaskIconGroup;
import consulo.ui.image.Image;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * An issue as the REST API returns it, {@code key} and {@code fields}.
 *
 * @author VISTALL
 * @since 2026-11-09
 */
public class JiraTask extends Task {
    private final JiraTaskRepository myRepository;
    private final String myKey;
    private final JsonObject myFields;

    public JiraTask(JiraTaskRepository repository, JsonObject issue) {
        myRepository = repository;
        myKey = StringUtil.notNullize(JiraJson.string(issue, "key"));
        JsonObject fields = JiraJson.object(issue, "fields");
        myFields = fields == null ? new JsonObject() : fields;
    }

    public JsonObject getFields() {
        return myFields;
    }

    @Override
    public String getId() {
        return myKey;
    }

    @Override
    public String getSummary() {
        return StringUtil.notNullize(JiraJson.string(myFields, "summary"));
    }

    @Override
    public @Nullable String getDescription() {
        return JiraJson.string(myFields, "description");
    }

    @Override
    public Comment[] getComments() {
        List<Comment> comments = new ArrayList<>();
        for (JsonObject comment : JiraJson.objects(JiraJson.object(myFields, "comment"), "comments")) {
            comments.add(new JiraComment(
                StringUtil.notNullize(JiraJson.string(comment, "body")),
                JiraJson.userName(JiraJson.object(comment, "author")),
                JiraJson.date(comment, "created")
            ));
        }
        return comments.toArray(Comment.EMPTY_ARRAY);
    }

    @Override
    public Image getIcon() {
        return getType() == TaskType.BUG ? TaskIconGroup.bug() : TaskIconGroup.task();
    }

    @Override
    public TaskType getType() {
        String type = JiraJson.name(myFields, "issuetype");
        if (type == null) {
            return TaskType.OTHER;
        }
        String lower = type.toLowerCase(Locale.ROOT);
        if (lower.contains("bug") || lower.contains("defect")) {
            return TaskType.BUG;
        }
        if (lower.contains("feature") || lower.contains("story") || lower.contains("improvement") || lower.contains("epic")) {
            return TaskType.FEATURE;
        }
        return TaskType.OTHER;
    }

    @Override
    public @Nullable Date getUpdated() {
        return JiraJson.date(myFields, "updated");
    }

    @Override
    public @Nullable Date getCreated() {
        return JiraJson.date(myFields, "created");
    }

    @Override
    public boolean isClosed() {
        return getState() == TaskState.RESOLVED || JiraJson.object(myFields, "resolution") != null;
    }

    @Override
    public boolean isIssue() {
        return true;
    }

    @Override
    public @Nullable String getIssueUrl() {
        return StringUtil.trimEnd(myRepository.getServerUrl(), "/") + "/browse/" + myKey;
    }

    @Override
    public TaskRepository getRepository() {
        return myRepository;
    }

    @Override
    public @Nullable TaskState getState() {
        String category = JiraJson.string(JiraJson.object(JiraJson.object(myFields, "status"), "statusCategory"), "key");
        if (category == null) {
            return null;
        }
        return switch (category) {
            case "new" -> TaskState.OPEN;
            case "indeterminate" -> TaskState.IN_PROGRESS;
            case "done" -> TaskState.RESOLVED;
            default -> TaskState.OTHER;
        };
    }

    @Override
    public @Nullable String getPriority() {
        return JiraJson.name(myFields, "priority");
    }

    @Override
    public @Nullable String getAssignee() {
        return JiraJson.userName(JiraJson.object(myFields, "assignee"));
    }

    @Override
    public @Nullable String getProject() {
        String project = JiraJson.string(JiraJson.object(myFields, "project"), "key");
        return project != null ? project : super.getProject();
    }
}
