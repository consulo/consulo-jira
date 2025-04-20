package com.intellij.jira.ui.dialog;

import com.intellij.jira.rest.model.JiraIssueComment;
import com.intellij.jira.rest.model.JiraVisibility;
import com.intellij.jira.tasks.EditCommentTask;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraIssueUtil;
import com.intellij.jira.util.JiraLabelUtil;
import consulo.project.Project;
import consulo.ui.ex.JBColor;
import consulo.ui.ex.awt.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.jira.util.JiraIssueUtil.escapeComment;
import static consulo.util.lang.StringUtil.isEmpty;
import static consulo.util.lang.StringUtil.trim;
import static java.util.Objects.nonNull;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class EditCommentDialog extends DialogWrapper {

    public static final String ALL_USERS = "All users";

    protected final Project myProject;
    protected String issueKey;
    protected List<String> projectRoles;

    protected ComboBox<String> myComboBox;
    protected JTextArea commentArea;

    private JiraIssueComment comment;

    public EditCommentDialog(@NotNull Project project, String issueKey, List<String> projectRoles, JiraIssueComment comment) {
        super(project, false);
        this.myProject = project;
        this.issueKey = issueKey;
        this.projectRoles = projectRoles;
        this.comment = comment;

        setTitle("Edit Comment");
        init();
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JiraPanel(new BorderLayout());
        commentArea = new JTextArea(6, 60);
        commentArea.setBorder(BorderFactory.createLineBorder(JBColor.border()));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setText(JiraIssueUtil.getPrettyBody(comment.getBody()));

        panel.add(ScrollPaneFactory.createScrollPane(commentArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        projectRoles.add(0, ALL_USERS);

        CollectionComboBoxModel<String> myComboBoxItems = new CollectionComboBoxModel(projectRoles);
        myComboBox = new ComboBox(myComboBoxItems);

        JiraVisibility visibility = comment.getVisibility();
        if(nonNull(visibility)){
            myComboBox.setSelectedIndex(projectRoles.indexOf(visibility.getValue()));
        }

        return FormBuilder.createFormBuilder()
                .addComponent(panel)
                .addLabeledComponent(JiraLabelUtil.createLabel("Viewable by"), myComboBox)
                .getPanel();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return commentArea;
    }


    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{new EditCommentExecuteAction(), myCancelAction};
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if(isEmpty(trim(commentArea.getText()))){
            return new ValidationInfo("Comment body can not be empty!", commentArea);
        }

        return null;
    }

    @Override
    protected void doOKAction() {
        if(nonNull(myProject)){
            new EditCommentTask(myProject, issueKey, comment.getId(), escapeComment(commentArea.getText()), (String) myComboBox.getSelectedItem()).queue();
        }

        close(0);
    }


    private class EditCommentExecuteAction extends OkAction{

    }

}
