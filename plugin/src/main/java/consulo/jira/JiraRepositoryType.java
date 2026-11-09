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

import consulo.annotation.component.ExtensionImpl;
import consulo.jira.icon.JiraIconGroup;
import consulo.jira.localize.JiraLocalize;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.task.TaskRepository;
import consulo.task.TaskRepositoryType;
import consulo.task.TaskState;
import consulo.task.ui.BaseRepositoryEditor;
import consulo.task.ui.TaskRepositoryEditor;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.image.Image;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;

import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 2026-11-09
 */
@ExtensionImpl
public class JiraRepositoryType extends TaskRepositoryType<JiraRepositoryOptions> {
    @Override
    public String getId() {
        return "JIRA";
    }

    @Override
    public LocalizeValue getPresentableName() {
        return JiraLocalize.jiraName();
    }

    @Override
    public Image getIcon() {
        return JiraIconGroup.jiraicon();
    }

    @Override
    public String getAccountTypeId() {
        return JiraAccountType.ID;
    }

    @Override
    public JiraRepositoryOptions createOptions() {
        return new JiraRepositoryOptions();
    }

    @Override
    public TaskRepository createRepository(Project project, JiraRepositoryOptions options) {
        return new JiraTaskRepository(this, project, options);
    }

    @Override
    public EnumSet<TaskState> getPossibleTaskStates() {
        return EnumSet.of(TaskState.OPEN, TaskState.IN_PROGRESS, TaskState.RESOLVED);
    }

    @Override
    public TaskRepositoryEditor createEditor(JiraRepositoryOptions options, Project project, Consumer<JiraRepositoryOptions> changeListener) {
        return new BaseRepositoryEditor<>(project, this, options, changeListener) {
            @RequiredUIAccess
            @Override
            protected void addCustomRows(FormBuilder builder) {
                TextBox projectKeyBox = TextBox.create(myOptions.projectKey);
                projectKeyBox.addValueListener(event -> {
                    myOptions.projectKey = StringUtil.notNullize(event.getValue()).trim();
                    fireChanged();
                });
                builder.addLabeled(JiraLocalize.editorProjectKey(), projectKeyBox);

                TextBox jqlBox = TextBox.create(myOptions.jql);
                jqlBox.addValueListener(event -> {
                    myOptions.jql = StringUtil.notNullize(event.getValue()).trim();
                    fireChanged();
                });
                builder.addLabeled(JiraLocalize.editorJql(), jqlBox);
            }
        };
    }
}
