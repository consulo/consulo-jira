package com.intellij.jira.util;

import com.intellij.jira.ui.JiraUi;
import com.intellij.jira.ui.panels.JiraTabPanel;
import consulo.ui.ex.content.Content;
import consulo.ui.ex.content.ContentManager;
import consulo.ui.ex.content.TabbedContent;
import consulo.util.collection.ContainerUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class JiraContentUtil {

    private JiraContentUtil() { }

    public static Content findTabbedContent(@Nonnull ContentManager manager, @Nonnull String name) {
        for (Content content : manager.getContents()) {
            if (content instanceof TabbedContent) {
                var tab = ((TabbedContent) content).getTabs().stream()
                        .filter(pair -> pair.getFirst().equals(name))
                        .findFirst();

                return tab.isPresent() ? content : null;
            }
        }

        return null;
    }

    public static void closeTab(@Nonnull ContentManager manager, @Nonnull String name) {
        for (Content content : manager.getContents()) {
            if (content instanceof TabbedContent) {
                var tab = ((TabbedContent) content).getTabs().stream()
                        .filter(pair -> pair.getFirst().equals(name))
                        .findFirst();

                tab.ifPresent(pair -> ((TabbedContent) content).removeContent(pair.getSecond()));

                if (((TabbedContent) content).getTabs().isEmpty()) {
                    manager.removeContent(content, true);
                }
            }
        }

    }

    @Nullable
    public static JiraUi getIssuesUi(@Nonnull JComponent c) {
        return ContainerUtil.getFirstItem(getIssuesUis(c));
    }

    public static List<JiraUi> getIssuesUis(@Nonnull JComponent c) {
        Set<JiraTabPanel> panels = new HashSet<>();
        collectIssuesPanelInstances(c, panels);

        return ContainerUtil.map(panels, JiraTabPanel::getUi);
    }

    public static Set<JiraUi> getAllJiraUis(@Nonnull ContentManager manager) {
        Set<JiraUi> uis = new HashSet<>();
        for (Content content : manager.getContents()) {
            if (content instanceof TabbedContent) {
                ((TabbedContent) content).getTabs().forEach(pair -> uis.addAll(getIssuesUis(pair.second)));
            } else {
                uis.addAll(getIssuesUis(content.getComponent()));
            }
        }

        return uis;
    }

    private static void collectIssuesPanelInstances(@Nonnull JComponent component, @Nonnull Set<JiraTabPanel> result) {
        if (component instanceof JiraTabPanel) {
            result.add((JiraTabPanel)component);
            return;
        }
        for (Component childComponent : component.getComponents()) {
            if (childComponent instanceof JComponent) {
                collectIssuesPanelInstances((JComponent)childComponent, result);
            }
        }
    }
}
