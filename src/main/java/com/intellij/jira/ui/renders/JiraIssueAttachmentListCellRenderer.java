package com.intellij.jira.ui.renders;

import com.intellij.jira.rest.model.JiraIssueAttachment;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraLabelUtil;
import consulo.language.file.FileTypeManager;
import consulo.ui.ex.awt.JBLabel;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.UIUtil;
import consulo.virtualFileSystem.fileType.FileType;

import javax.swing.*;
import java.awt.*;

import static com.intellij.jira.util.JiraIssueUtil.getPrettyDateTime;
import static com.intellij.jira.util.JiraLabelUtil.*;

public class JiraIssueAttachmentListCellRenderer extends DefaultJiraListCellRender {

    private JBLabel iconAndNameLabel;
    private JBLabel authorLabel;
    private JBLabel createdLabel;

    public JiraIssueAttachmentListCellRenderer() {
        super();
        init();
    }

    private void init() {
        JPanel issueLinkpanel = new JiraPanel(new BorderLayout()).withBorder(JBUI.Borders.empty(4, 5)).andTransparent();
        iconAndNameLabel =  JiraLabelUtil.createEmptyLabel().withFont(BOLD);

        FlowLayout flowLayout = new FlowLayout(FlowLayout.RIGHT);
        flowLayout.setVgap(0);

        JPanel authorAndCreatePanel = new JiraPanel(flowLayout).andTransparent();
        createdLabel = JiraLabelUtil.createEmptyLabel().withFont(ITALIC);
        authorLabel = JiraLabelUtil.createEmptyLabel().withFont(BOLD);

        authorAndCreatePanel.add(authorLabel);
        authorAndCreatePanel.add(createdLabel);

        issueLinkpanel.add(iconAndNameLabel, BorderLayout.LINE_START);
        issueLinkpanel.add(authorAndCreatePanel, BorderLayout.LINE_END);
        add(issueLinkpanel);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JiraIssueAttachment issueAttachment = (JiraIssueAttachment) value;

        setBorder(JBUI.Borders.emptyBottom(2));

        if (!UIUtil.isUnderDarcula()) {

            if(isSelected){
                setBackground(DARK_ISSUE_ATTACHMENT_COLOR);
            }else{
                setBackground(ISSUE_ATTACHMENT_COLOR);
            }
        }

        String filename = issueAttachment.getFilename();
        FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(filename);
        iconAndNameLabel.setIcon(fileType.getIcon());
        iconAndNameLabel.setText(issueAttachment.getFilename());
        iconAndNameLabel.setForeground(JiraLabelUtil.getFgRowColor());

        authorLabel.setText(issueAttachment.getAuthorName());
        authorLabel.setForeground(JiraLabelUtil.getFgRowColor());

        createdLabel.setText(getPrettyDateTime(issueAttachment.getCreated()));
        createdLabel.setForeground(JiraLabelUtil.getFgRowColor());

        return this;
    }

}
