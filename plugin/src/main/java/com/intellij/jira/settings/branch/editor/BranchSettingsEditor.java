package com.intellij.jira.settings.branch.editor;

import com.intellij.jira.settings.branch.BranchSettings;
import com.intellij.jira.settings.branch.BranchSettingsState;
import com.intellij.jira.ui.editors.Editor;
import com.intellij.jira.util.JiraIssueField;
import com.intellij.jira.util.Separator;
import consulo.application.dumb.DumbAware;
import consulo.dataContext.DataManager;
import consulo.project.Project;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.awt.*;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.List;

public class BranchSettingsEditor implements Editor {

    private static final int TOOLBAR_WIDTH = 400;
    private static final int TOOLBAR_HEIGHT = 60;

    private final Project myProject;
    private final BranchSettings myBranchSettings;


    private JBList<String> myBranchPrefixList;
    private JRadioButton myDefaultBranchNameRadio;
    private JRadioButton myCustomBranchNameRadio;

    private JLabel myFieldSeparatorLabel;
    private ComboBox<Separator> myFieldSeparatorCombo;
    private JLabel myBranchNameLabel;
    private JBList<String> myBranchNameList;

    private JPanel myBranchNameToolbarPanel;
    private JLabel myBranchNameExample;

    public BranchSettingsEditor(@Nonnull Project project, BranchSettings branchSettings) {
        myProject = project;
        myBranchSettings = branchSettings;
    }

    @Override
    public JComponent createPanel() {

        // Prefix
        CollectionListModel<String> stringCollectionListModel = new CollectionListModel<>(myBranchSettings.getState().getBranchTypes());
        myBranchPrefixList = new JBList<>(stringCollectionListModel);
        myBranchPrefixList.setEmptyText("No prefixes");


        ToolbarDecorator prefixToolbarDecorator = ToolbarDecorator.createDecorator(myBranchPrefixList)
            .disableUpDownActions()
            .setPreferredSize(JBUI.size(TOOLBAR_WIDTH, TOOLBAR_HEIGHT))
            .setAddAction(anActionButton -> new AddPrefixDialog(myProject).show())
            .setRemoveAction(anActionButton -> {
                String prefixToRemove = myBranchPrefixList.getSelectedValue();
                ((CollectionListModel<String>) myBranchPrefixList.getModel()).remove(prefixToRemove);
                myBranchPrefixList.doLayout();
                updateBranchNameExample();
            });

        // Branch name
        myDefaultBranchNameRadio = new JBRadioButton("Creates a branch using {issueKey} as name");
        myDefaultBranchNameRadio.addActionListener(event -> toggleDefaultBranchNameRadio(myDefaultBranchNameRadio.isSelected()));

        myCustomBranchNameRadio = new JBRadioButton("Creates a branch customizing the name");
        myCustomBranchNameRadio.addActionListener(event -> toggleDefaultBranchNameRadio(!myCustomBranchNameRadio.isSelected()));

        myFieldSeparatorLabel = new JBLabel("Separator: ");
        myFieldSeparatorCombo = new ComboBox<>(Separator.values());

        myFieldSeparatorCombo.addActionListener((event) -> updateBranchNameExample());

        myBranchNameLabel = new JBLabel("Name: ");
        CollectionListModel<String> availableIssueFields = new CollectionListModel<>(myBranchSettings.getState().getFieldNames());
        myBranchNameList = new JBList<>(availableIssueFields);
        myBranchNameList.setEmptyText("No fields");


        myBranchNameToolbarPanel = ToolbarDecorator.createDecorator(myBranchNameList)
            .disableUpDownActions()
            .setPreferredSize(JBUI.size(TOOLBAR_WIDTH, TOOLBAR_HEIGHT))
            .setAddAction(anActionButton -> {
                DefaultActionGroup group = new DefaultActionGroup();
                group.add(new AddFieldAction(JiraIssueField.KEY));
                group.add(new AddFieldAction(JiraIssueField.SUMMARY));
                group.add(new AddFieldAction(JiraIssueField.PROJECT_KEY));

                JBPopupFactory.getInstance()
                        .createActionGroupPopup("Add Field", group, DataManager.getInstance().getDataContext(anActionButton.getContextComponent()), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true)
                        .show(anActionButton.getPreferredPopupPoint());})
            .setRemoveAction(anActionButton -> {
                String fieldNameToRemove = myBranchNameList.getSelectedValue();
                ((CollectionListModel<String>) myBranchNameList.getModel()).remove(fieldNameToRemove);
                myBranchNameList.doLayout();
                updateBranchNameExample();
            }).createPanel();


        myBranchNameExample = new JBLabel("e.g: " + getBranchNameExample() );

        if (!myBranchSettings.getState().isCustom()) {
            toggleDefaultBranchNameRadio(true);
        } else {
            toggleDefaultBranchNameRadio(false);
            myFieldSeparatorCombo.setSelectedItem(myBranchSettings.getState().getFieldSeparator());
        }

        return FormBuilder.createFormBuilder()
                .addComponent(SeparatorFactory.createSeparator("Prefix", null))
                .addComponent(prefixToolbarDecorator.createPanel())
                .addComponent(SeparatorFactory.createSeparator("Name", null))
                .addComponent(myDefaultBranchNameRadio)
                .addComponent(myCustomBranchNameRadio)
                .addLabeledComponent(myFieldSeparatorLabel, myFieldSeparatorCombo)
                .addLabeledComponent(myBranchNameLabel, myBranchNameToolbarPanel)
                .addComponentToRightColumn(myBranchNameExample)
                .getPanel();
    }

    @Override
    public @Nullable ValidationInfo validate() {
        if (myBranchNameList.isEmpty()) {
            return new ValidationInfo("You must select at least one field");
        }

        return null;
    }

    public BranchSettingsState getBranchSettingsState() {
        List<String> branchTypes = ((CollectionListModel<String>) myBranchPrefixList.getModel()).getItems();
        if (myDefaultBranchNameRadio.isSelected()) {
            return BranchSettingsState.getDefault(branchTypes);
        }

        Separator separator = (Separator) myFieldSeparatorCombo.getSelectedItem();
        List<String> fieldNames = ((CollectionListModel<String>) myBranchNameList.getModel()).getItems();

        return BranchSettingsState.getCustom(branchTypes, separator, fieldNames);
    }

    private String getBranchNameExample() {
        Separator separator = (Separator) myFieldSeparatorCombo.getSelectedItem();
        String prefix = myBranchPrefixList.isEmpty() ? "" : "{prefix}/";
        List<String> fieldsNames = ((CollectionListModel<String>) myBranchNameList.getModel()).getItems()
                .stream()
                .map(fieldName -> String.format("{%s}", fieldName))
                .toList();

        return prefix + StringUtil.join(fieldsNames, separator.getSeparator());
    }

    private void updateBranchNameExample() {
        myBranchNameExample.setText("e.g: " + getBranchNameExample());
    }

    private void toggleDefaultBranchNameRadio(boolean enable) {
        myDefaultBranchNameRadio.setSelected(enable);
        myCustomBranchNameRadio.setSelected(!enable);
        myFieldSeparatorLabel.setEnabled(!enable);
        myFieldSeparatorCombo.setEnabled(!enable);
        myBranchNameLabel.setEnabled(!enable);
        myBranchNameExample.setEnabled(!enable);
        myBranchNameToolbarPanel.setEnabled(!enable);
    }


    private class AddFieldAction extends AnAction implements DumbAware {

        private final String field;

        private AddFieldAction(String field) {
            super(field);
            this.field = field;
        }

        @Override
        public boolean displayTextInToolbar() {
            return true;
        }

        @Override
        public void actionPerformed(@Nonnull AnActionEvent e) {
            CollectionListModel<String> model = (CollectionListModel<String>) myBranchNameList.getModel();
            if (!model.contains(field)) {
                model.add(field);
                myBranchNameList.setSelectedIndex(myBranchNameList.getModel().getSize() - 1);
                myBranchNameList.doLayout();
                updateBranchNameExample();
            }
        }
    }


    private class AddPrefixDialog extends DialogWrapper {

        private JTextField myPrefixField;

        protected AddPrefixDialog(@Nullable Project project) {
            super(project, false);

            setTitle("Add Prefix");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            myPrefixField = new JTextField();

            return FormBuilder.createFormBuilder()
                    .addLabeledComponent("Name: ", myPrefixField)
                    .getPanel();
        }


        @Nullable
        @Override
        protected ValidationInfo doValidate() {
            if (StringUtil.trim(myPrefixField.getText()).isEmpty()) {
                return new ValidationInfo("Prefix is required");
            }

            if (myPrefixField.getText().split(" ").length > 1) {
                return new ValidationInfo("Prefix must be one word");
            }

            return null;
        }

        @Override
        protected void doOKAction() {
            String prefix = StringUtil.trim(myPrefixField.getText());
            CollectionListModel<String> model = (CollectionListModel<String>) myBranchPrefixList.getModel();
            if (!model.contains(prefix)) {
                model.add(prefix);
                myBranchPrefixList.doLayout();
            }

            super.doOKAction();
        }
    }

}
