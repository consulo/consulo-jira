package com.intellij.jira.util;

import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.settings.ChangelistState;
import consulo.util.lang.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ChangelistUtil {

    private ChangelistUtil() { }

    public static String getChangelistName(JiraIssue issue, ChangelistSettings settings) {
        if (settings.isDefault()) {
            return issue.getKey();
        }

        ChangelistState state = settings.getState();
        List<String> issueFieldValues = new ArrayList<>();
        for (String fieldName : state.getFieldNames()) {
            String value = null;
            if ("issueKey".endsWith(fieldName)) {
                value = issue.getKey();
            } else if ("projectKey".equals(fieldName)) {
                value = issue.getProject().getKey();
            }

            if (Objects.nonNull(value)) {
                issueFieldValues.add(value);
            }
        }

        return StringUtil.join(issueFieldValues, state.getFieldSeparator().getSeparator());
    }

}
