package com.intellij.tasks.jira;

import consulo.annotation.component.ExtensionImpl;
import consulo.jira.icon.JiraIconGroup;
import consulo.localize.LocalizeValue;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.awt.Messages;
import consulo.ui.image.Image;
import consulo.versionControlSystem.IssueNavigationLink;
import consulo.versionControlSystem.IssueNavigationLinkProvider;
import jakarta.annotation.Nonnull;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author VISTALL
 * @since 2024-12-12
 */
@ExtensionImpl
public class JiraIssueNavigationLinkProvider implements IssueNavigationLinkProvider {
  @Nonnull
  @Override
  public LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO("JIRA");
  }

  @Nonnull
  @Override
  public Image getIcon() {
    return JiraIconGroup.jiraicon();
  }

  @RequiredUIAccess
  @Nonnull
  @Override
  public CompletableFuture<IssueNavigationLink> ask(@Nonnull JComponent jComponent) {
    UIAccess uiAccess = UIAccess.current();
    return uiAccess.giveAsync(() -> {
      String s = Messages.showInputDialog(
        jComponent,
        "Enter JIRA installation URL:",
        "Add JIRA Issue Navigation Pattern",
        Messages.getQuestionIcon()
      );
      
      if (s == null) {
        return null;
      }
      if (!s.endsWith("/")) {
        s += "/";
      }
      return new IssueNavigationLink("[A-Z]+\\-\\d+", s + "browse/$0");
    });
  }
}
