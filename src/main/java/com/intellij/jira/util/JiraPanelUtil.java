package com.intellij.jira.util;

import com.intellij.execution.ui.TagButton;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueComponent;
import com.intellij.jira.rest.model.JiraProjectVersion;
import com.intellij.jira.tasks.ToggleWatchIssueTask;
import com.intellij.jira.ui.panels.JiraPanel;
import consulo.project.Project;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.UIUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.jira.util.JiraLabelUtil.*;
import static java.awt.BorderLayout.LINE_START;
import static javax.swing.SwingConstants.CENTER;

public class JiraPanelUtil {

    private JiraPanelUtil() { }

    public static final Border MARGIN_BOTTOM = JiraBorders.emptyBottom(10);

    public static JiraPanel createWhiteGridPanel(int rows, int cols){
        return createWhitePanel(new GridLayout(rows, cols));
    }

    public static JiraPanel createWhiteBorderPanel(){
        return createWhitePanel(new BorderLayout())
                .withBorder(MARGIN_BOTTOM);
    }

    public static JiraPanel createWhitePanel(@NotNull LayoutManager layout){
        return new JiraPanel(layout)
                .withBackground(JBColor.WHITE);
    }

    public static JiraPanel createPlaceHolderPanel(String text) {
        JiraPanel panel = new JiraPanel(new GridBagLayout());
        JBLabel messageLabel = new JBLabel(text);
        messageLabel.setHorizontalAlignment(CENTER);
        messageLabel.setVerticalAlignment(CENTER);
        panel.add(messageLabel, new GridBagConstraints());
        return panel;
    }

    public static JPanel createPanelWithVerticalLine(){
        return createPanelWithLine(false);
    }

    public static JPanel createPanelWithHorizontalLine(){
        return createPanelWithLine(true);
    }

    public static JPanel createTypePanel(@NotNull JiraIssue issue) {
        JBLabel typeLabel = JiraLabelUtil.createBoldLabel("Type: ");
        JBLabel typeValueLabel = JiraLabelUtil.createLabel(issue.getIssuetype().getName());

        return createWhiteBorderPanel(typeLabel, typeValueLabel);
    }

    public static JPanel createStatusPanel(@NotNull JiraIssue issue) {
        JLabel statusValueLabel = JiraLabelUtil.createStatusLabel(issue.getStatus());

        return createWhiteLeftFlowPanel(statusValueLabel);
    }

    public static JPanel createPriorityPanel(@NotNull JiraIssue issue) {
        JBLabel priorityLabel = JiraLabelUtil.createBoldLabel("Priority: ");
        JBLabel priorityValueLabel = JiraLabelUtil.createPriorityLabel(issue.getPriority());

        return createWhiteBorderPanel(priorityLabel, priorityValueLabel);
    }

    public static JPanel createReporterPanel(@NotNull JiraIssue issue) {
        JBLabel reporterLabel = JiraLabelUtil.createBoldLabel("Reporter: ");
        JBLabel reporterValueLabel = JiraLabelUtil.createLabel(issue.getReporter() != null ? issue.getReporter().getDisplayName() : EMPTY_TEXT);

        return createWhiteBorderPanel(reporterLabel, reporterValueLabel);
    }

    public static JPanel createAssigneePanel(@NotNull JiraIssue issue) {
        JBLabel assigneeLabel = JiraLabelUtil.createBoldLabel("Assignee: ");
        JBLabel assigneeValueLabel = JiraLabelUtil.createLabel(issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : EMPTY_TEXT);

        return createWhiteBorderPanel(assigneeLabel, assigneeValueLabel);
    }

    public static JiraPanel createWatchesPanel(@NotNull JiraIssue issue, @NotNull Project project) {
        JiraPanel watchesPanel = JiraPanelUtil.createWhitePanel(new FlowLayout(FlowLayout.LEFT, 0, 0)).withBorder(MARGIN_BOTTOM);
        JBLabel watchesLabel = JiraLabelUtil.createBoldLabel("Watchers: ");
        JBLabel watchesValueLabel = JiraLabelUtil.createLabel(issue.getWatches().getWatchCount() + " ");
        boolean isWatching = issue.getWatches().isWatching();
        JBLabel watchLabel = JiraLabelUtil.createLabel((isWatching ? "Stop " : "Start ") + "watching this issue");
        watchLabel.setBackground(UIUtil.isUnderDarcula() ? DEFAULT_SELECTED_ISSUE_COLOR : DARCULA_TEXT_COLOR);
        watchLabel.setBorder(JBUI.Borders.empty(2, 2, 2, 3));
        watchLabel.setOpaque(true);
        watchLabel.setCursor(HAND_CURSOR);
        watchLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> new ToggleWatchIssueTask(project, issue.getKey(), isWatching).queue());
            }
        });

        watchesPanel.add(watchesLabel);
        watchesPanel.add(watchesValueLabel);
        watchesPanel.add(watchLabel);

        return watchesPanel;
    }

    public static JiraPanel createVersionsPanel(@NotNull JiraIssue issue) {
        JBLabel versionsLabel = JiraLabelUtil.createBoldLabel("Versions: ");
        JBLabel versionsValueLabel = JiraLabelUtil.createLabel(getVersionsNames(issue.getVersions()));

        return createWhiteBorderPanel(versionsLabel, versionsValueLabel);
    }

    public static JiraPanel createComponentsPanel(@NotNull JiraIssue issue) {
        JBLabel componentsLabel = JiraLabelUtil.createBoldLabel("Components: ");
        JBLabel componentsValueLabel = JiraLabelUtil.createLabel(getComponentNames(issue.getComponents()));

        return createWhiteBorderPanel(componentsLabel, componentsValueLabel);
    }

    public static JiraPanel createLabelsPanel(@NotNull JiraIssue issue) {
        JBLabel label = JiraLabelUtil.createBoldLabel("Labels: ");

        JiraPanel labelsPanel = createWhitePanel(new FlowLayout(FlowLayout.LEFT));
        labelsPanel.add(label);

        issue.getLabels().forEach(labelText -> labelsPanel.add(new Badge(labelText)));

        JiraPanel panel = createWhiteBorderPanel();
        panel.add(label, LINE_START);
        panel.add(labelsPanel, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel createPanelWithLine(boolean horizontal){
        return new JPanel(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(JBColor.border());
                if(horizontal){
                    g.drawLine(0,getHeight()/2, getWidth(), getHeight()/2);
                }else{
                    g.drawLine(getWidth()/2,0, getWidth()/2, getHeight());
                }
            }
        };
    }

    private static JiraPanel createWhiteBorderPanel(JLabel fieldName, JLabel fieldValue) {
        JiraPanel panel = createWhiteBorderPanel();
        panel.add(fieldName, LINE_START);
        panel.add(fieldValue, BorderLayout.CENTER);

        return panel;
    }

    public static JPanel createWhiteLeftFlowPanel(JLabel... labels) {
        JPanel panel = createWhitePanel(new FlowLayout(FlowLayout.LEFT, 0, 1)).withBorder(MARGIN_BOTTOM);

        for (JLabel label : labels) {
            panel.add(label);
        }

        return panel;
    }

    private static String getVersionsNames(List<JiraProjectVersion> versions){
        if(versions.isEmpty()){
            return "None";
        }

        return versions.stream().map(JiraProjectVersion::getName)
                .collect(Collectors.joining(", "));
    }

    private static String getComponentNames(List<JiraIssueComponent> components){
        return components.stream()
                .map(JiraIssueComponent::getName)
                .collect(Collectors.joining(", "));
    }


    private static class Badge extends TagButton {

        private static final int myInsets = JBUI.scale(4);

        public Badge(@Nls String text) {
            super(text, null);
            remove(myCloseButton);
            myButton.setBackground(JBColor.WHITE);
        }

        @Override
        protected void layoutButtons() {
            myButton.setMargin(JBUI.emptyInsets());
            Dimension size = myButton.getPreferredSize();
            Dimension tagSize = new Dimension(size.width - myInsets * 2, size.height);
            setPreferredSize(tagSize);
            myButton.setBounds(new Rectangle(tagSize));
        }
    }

}
