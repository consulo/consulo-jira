package com.intellij.tasks.jira;

import com.intellij.tasks.jira.model.JiraIssue;
import com.intellij.tasks.jira.model.api2.JiraRestApi2;
import com.intellij.tasks.jira.model.api20alpha1.JiraRestApi20Alpha1;
import consulo.logging.Logger;
import consulo.task.Task;
import consulo.task.TaskState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;

import static com.intellij.tasks.jira.JiraRepository.REST_API_PATH_SUFFIX;

/**
 * @author Mikhail Golubev
 */
public abstract class JiraRestApi {
  private static final Logger LOG = Logger.getInstance(JiraRestApi.class);
  protected final JiraRepository myRepository;

  public static JiraRestApi fromJiraVersion(@Nonnull JiraVersion jiraVersion, @Nonnull JiraRepository repository) {
    LOG.debug("JIRA version is " + jiraVersion);
    if (jiraVersion.getMajorNumber() == 4) {
      return new JiraRestApi20Alpha1(repository);
    }
    else if (jiraVersion.getMajorNumber() >= 5) {
      return new JiraRestApi2(repository);
    }
    else {
      LOG.warn("JIRA below 4.0.0 doesn't support REST API (" + jiraVersion + " used)");
      return null;
    }
  }

  public static JiraRestApi fromJiraVersion(@Nonnull String version, @Nonnull JiraRepository repository) {
    return fromJiraVersion(new JiraVersion(version), repository);
  }

  protected JiraRestApi(JiraRepository repository) {
    myRepository = repository;
  }

  @Nonnull
  public final List<JiraIssue> findIssues(String jql, int max) throws Exception {
    GetMethod method = getMultipleIssuesSearchMethod(jql, max);
    String response = myRepository.executeMethod(method);
    List<JiraIssue> issues = parseIssues(response);
    LOG.debug("Total " + issues.size() + " downloaded");
    return issues;
  }

  @Nullable
  public final JiraIssue findIssue(String key) throws Exception {
    GetMethod method = getSingleIssueSearchMethod(key);
    return parseIssue(myRepository.executeMethod(method));
  }

  @Nonnull
  protected GetMethod getSingleIssueSearchMethod(String key) {
    return new GetMethod(myRepository.getUrl() + REST_API_PATH_SUFFIX + "/issue/" + key);
  }

  @Nonnull
  protected GetMethod getMultipleIssuesSearchMethod(String jql, int max) {
    GetMethod method = new GetMethod(myRepository.getUrl() + REST_API_PATH_SUFFIX + "/search");
    method.setQueryString(new NameValuePair[]{
      new NameValuePair("jql", jql),
      new NameValuePair("maxResults", String.valueOf(max))
    });
    return method;
  }

  @Nonnull
  protected abstract List<JiraIssue> parseIssues(String response);

  @Nullable
  protected abstract JiraIssue parseIssue(String response);

  @Override
  public String toString() {
    return String.format("JiraRestAPI(%s)", getVersionName());
  }

  @Nonnull
  public abstract String getVersionName();

  public void setTaskState(Task task, TaskState state) throws Exception {
    String requestBody;
    switch (state) {
      case IN_PROGRESS:
        requestBody = "{\"transition\": \"4\"}";
        break;
      case RESOLVED:
        // 5 for "Resolved", 2 for "Close"
        requestBody = "{\"transition\": \"5\", \"resolution\": \"Fixed\"}";
        break;
      case REOPENED:
        requestBody = "{\"transition\": \"3\"}";
        break;
      default:
        return;
    }
    // REST API 2.0 require double quotes both around field names and values (even numbers)
    // REST API 2.0alpha1 permits to omit them, but handles both variants
    final String transitionsUrl = myRepository.getUrl() + REST_API_PATH_SUFFIX + "/issue/" + task.getId() + "/transitions";
    final PostMethod method = new PostMethod(transitionsUrl);
    method.setRequestEntity(new StringRequestEntity(requestBody, "application/json", "utf-8"));
    myRepository.executeMethod(method);
  }
}
