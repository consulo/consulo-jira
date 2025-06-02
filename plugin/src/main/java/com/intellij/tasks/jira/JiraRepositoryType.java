package com.intellij.tasks.jira;

import consulo.annotation.component.ExtensionImpl;
import consulo.jira.icon.JiraIconGroup;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.task.BaseRepositoryType;
import consulo.task.TaskState;
import consulo.task.ui.TaskRepositoryEditor;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

import java.util.EnumSet;
import java.util.function.Consumer;

/**
 * @author Dmitry Avdeev
 */
@ExtensionImpl
public class JiraRepositoryType extends BaseRepositoryType<JiraRepository> {

  public JiraRepositoryType() {
  }

  @Override
  @Nonnull
  public String getId() {
    return "JIRA";
  }

  @Nonnull
  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("JIRA");
  }

  @Override
  @Nonnull
  public Image getIcon() {
    return JiraIconGroup.jiraicon();
  }

  @Override
  @Nonnull
  public JiraRepository createRepository() {
    return new JiraRepository(this);
  }

  @Nonnull
  @Override
  public Class<JiraRepository> getRepositoryClass() {
    return JiraRepository.class;
  }

  @Nonnull
  @Override
  public TaskRepositoryEditor createEditor(JiraRepository repository,
                                           Project project,
                                           Consumer<JiraRepository> changeListener) {
    return new JiraRepositoryEditor(project, repository, changeListener);
  }

  @Override
  public EnumSet<TaskState> getPossibleTaskStates() {
    return EnumSet.of(TaskState.OPEN, TaskState.IN_PROGRESS, TaskState.REOPENED, TaskState.RESOLVED);
  }
}

