package com.intellij.jira.ui.dialog;

import com.intellij.jira.JiraDataKeys;
import com.intellij.jira.helper.TransitionFieldHelper;
import com.intellij.jira.helper.TransitionFieldHelper.FieldEditorInfo;
import com.intellij.jira.rest.model.JiraIssue;
import com.intellij.jira.rest.model.JiraIssueFieldProperties;
import com.intellij.jira.rest.model.JiraIssueTransition;
import com.intellij.jira.tasks.TransitIssueTask;
import com.intellij.jira.ui.model.JiraIssueTransitionListModel;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.ui.panels.JiraTransitionTaskEditor;
import com.intellij.jira.ui.renders.JiraIssueTransitionListCellRenderer;
import com.intellij.jira.util.JiraLabelUtil;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.tasks.jira.JiraRepository;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.intellij.jira.helper.TransitionFieldHelper.createCommentFieldEditorInfo;
import static com.intellij.jira.util.JiraPanelUtil.createPanelWithVerticalLine;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class IssueTransitionDialog extends DialogWrapper implements DataProvider {

    private final Project myProject;
    private final JiraIssue myIssue;

    private List<JiraIssueTransition> myTransitions;
    private JiraIssueTransition mySelectedIssueTransition;

    private JPanel myTransitionsPanel;
    private JPanel myTransitionFieldsPanel;
    private JPanel myTransitionPreviewPanel;

    private JiraTransitionTaskEditor myTransitionTaskEditor;

    private Map<String, FieldEditorInfo> myTransitionFields = new HashMap<>();


    public IssueTransitionDialog(@NotNull Project project, @NotNull JiraIssue issue, List<JiraIssueTransition> transitions) {
        super(project, false);
        myProject = project;
        myIssue = issue;
        myTransitions = transitions;
        myOKAction = new TransitIssueExecuteAction().disabled();
        init();
    }

    @Override
    protected void init() {
        setTitle("Transit Issue " + myIssue.getKey());
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JiraPanel(new BorderLayout());
        myTransitionsPanel = new JiraPanel(new BorderLayout());
        JBList<JiraIssueTransition> transitionList = new JBList<>();
        transitionList.setEmptyText("No transitions");
        transitionList.setModel(new JiraIssueTransitionListModel(myTransitions));
        transitionList.setCellRenderer(new JiraIssueTransitionListCellRenderer());
        transitionList.setSelectionMode(SINGLE_SELECTION);
        transitionList.setPreferredSize(new JBDimension(100, 300));
        transitionList.setBorder(BorderFactory.createLineBorder(JBColor.border()));
        transitionList.addListSelectionListener(e ->
            ApplicationManager.getApplication().invokeLater(() -> {
                    updateTransitionFieldPanel(transitionList.getSelectedValue());
                    updateTransitionPreviewPanel(transitionList.getSelectedValue());
                })
        );

        myTransitionsPanel.add(transitionList, BorderLayout.CENTER);

        myTransitionFieldsPanel = new JiraPanel(new GridBagLayout());
        myTransitionFieldsPanel.setMinimumSize(JBUI.size(450, 300));
        myTransitionFieldsPanel.setBorder(JBUI.Borders.empty(5));
        myTransitionFieldsPanel.add(JiraPanelUtil.createPlaceHolderPanel("Select transition"), new GridBagConstraints());

        myTransitionPreviewPanel = new JiraPanel(new BorderLayout());
        myTransitionPreviewPanel.setMinimumSize(JBUI.size(100, 300));

        panel.add(myTransitionsPanel, BorderLayout.WEST);
        panel.add(ScrollPaneFactory.createScrollPane(myTransitionFieldsPanel, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        panel.add(myTransitionPreviewPanel, BorderLayout.EAST);
        panel.setMinimumSize(JBUI.size(650, 300));

        myTransitionTaskEditor = new JiraTransitionTaskEditor(myProject, myIssue);

        return FormBuilder.createFormBuilder()
                .addComponent(panel)
                .addComponent(myTransitionTaskEditor.createPanel())
                .getPanel();
    }

    @Override
    public @Nullable Object getData(@NotNull String dataId) {
        if (JiraDataKeys.ISSUE_KEY.is(dataId)) {
            return myIssue.getKey();
        } else if (JiraDataKeys.PROJECT_KEY.is(dataId)) {
            return myIssue.getProject().getKey();
        }

        return null;
    }

    private void updateTransitionFieldPanel(JiraIssueTransition transition) {
        mySelectedIssueTransition = transition;
        myOKAction.setEnabled(true);

        List<JiraIssueFieldProperties> transitionFields = transition.getFields().entrySet().stream()
                .map(entry -> JiraRepository.GSON.fromJson(entry.getValue(), JiraIssueFieldProperties.class))
                .collect(Collectors.toList());


        myTransitionFields.clear();

        myTransitionFieldsPanel.removeAll();
        myTransitionFieldsPanel.setLayout(new GridBagLayout());

        if(transitionFields.isEmpty()) {
            myTransitionFieldsPanel.add(JiraPanelUtil.createPlaceHolderPanel("No fields required"), new GridBagConstraints());
        } else {
            createTransitionFields(transitionFields);
        }


        myTransitionFieldsPanel.revalidate();
        myTransitionFieldsPanel.repaint();
    }


    private void updateTransitionPreviewPanel(JiraIssueTransition transition){
        myTransitionPreviewPanel.removeAll();

        JPanel sourceStatusPanel = new JiraPanel();
        sourceStatusPanel.setBorder(JBUI.Borders.empty(5));
        JBLabel sourceStatusLabel = JiraLabelUtil.createStatusLabel(myIssue.getStatus());
        sourceStatusPanel.add(sourceStatusLabel);

        JPanel verticalLinePanel = createPanelWithVerticalLine();

        JPanel targetStatusPanel = new JiraPanel();
        targetStatusPanel.setBorder(JBUI.Borders.empty(5));
        JBLabel targetStatusLabel = JiraLabelUtil.createStatusLabel(transition.getTo());
        targetStatusPanel.add(targetStatusLabel);

        myTransitionPreviewPanel.add(sourceStatusPanel, BorderLayout.PAGE_START);
        myTransitionPreviewPanel.add(verticalLinePanel, BorderLayout.CENTER);
        myTransitionPreviewPanel.add(targetStatusPanel, BorderLayout.PAGE_END);

        myTransitionPreviewPanel.revalidate();
        myTransitionPreviewPanel.repaint();
    }

    private void createTransitionFields(List<JiraIssueFieldProperties> transitionFields) {
        FormBuilder formBuilder = FormBuilder.createFormBuilder().setVerticalGap(10);

        transitionFields.forEach(fieldProperties -> {
            FieldEditorInfo info = TransitionFieldHelper.createFieldEditorInfo(fieldProperties, myIssue);
            myTransitionFields.put(info.getName(), info);

            formBuilder.addComponent(info.getPanel());
        });

        FieldEditorInfo commentInfo = createCommentFieldEditorInfo();
        myTransitionFields.put(commentInfo.getName(), commentInfo);
        formBuilder.addComponent(commentInfo.getPanel());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 1;


        myTransitionFieldsPanel.add(formBuilder.getPanel(), constraints);
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if(isNull(mySelectedIssueTransition)){
            return new ValidationInfo("You must select transition");
        }

        for(FieldEditorInfo info : myTransitionFields.values()){
            ValidationInfo fieldValidation = info.validateField();
            if(nonNull(fieldValidation)){
                return fieldValidation;
            }

        }

        return myTransitionTaskEditor.validate();
    }


    @Override
    protected void doOKAction() {
        if (nonNull(myProject)) {
            myTransitionTaskEditor.doTask();
            new TransitIssueTask(myProject, myIssue.getId(), mySelectedIssueTransition.getId(), myTransitionFields).queue();
        }

        close(0);
    }

    private class TransitIssueExecuteAction extends OkAction{

        public TransitIssueExecuteAction disabled(){
            setEnabled(false);
            return this;
        }
    }

}
