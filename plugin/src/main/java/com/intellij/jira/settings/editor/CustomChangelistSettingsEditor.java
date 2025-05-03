package com.intellij.jira.settings.editor;

import com.intellij.jira.settings.ChangelistSettings;
import com.intellij.jira.settings.ChangelistState;
import com.intellij.jira.util.Separator;
import consulo.application.dumb.DumbAware;
import consulo.dataContext.DataManager;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.awt.*;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.util.lang.StringUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CustomChangelistSettingsEditor extends ChangelistSettingsEditor {

    private static final JRadioButton CUSTOM_RADIO_BUTTON = new JBRadioButton("Creates a custom changelist");
    private static final int H_GPAP = 30;

    private JLabel myFieldSeparatorLabel;
    private ComboBox<Separator> myFieldSeparatorCombo;

    private JLabel myFieldNamesLabel;
    private JBList<String> myFieldNamesList;

    private JLabel myChangelistExample;

    private JCheckBox mySetActiveCheckBox;

    public CustomChangelistSettingsEditor(ChangelistSettings settings) {
        super(CUSTOM_RADIO_BUTTON);
        init(settings);
    }

    private void init(ChangelistSettings settings) {
        this.myRadioButton.setSelected(settings.isCustom());
        this.myFieldSeparatorLabel = new JBLabel("Separator: ");
        this.myFieldSeparatorCombo = new ComboBox<>(Separator.values());
        if (settings.isCustom()) {
            this.myFieldSeparatorCombo.setSelectedItem(settings.getState().getFieldSeparator());
        }
        this.myFieldSeparatorCombo.addActionListener((event) -> {
            updateChangelistName();
        });

        this.myFieldNamesLabel = new JBLabel("Name: ");
        CollectionListModel<String> stringCollectionListModel = new CollectionListModel<>(settings.isCustom() ? settings.getState().getFieldNames() : new ArrayList<>());
        this.myFieldNamesList = new JBList<>(stringCollectionListModel);
        this.myFieldNamesList.setEmptyText("No fields selected");

        this.myChangelistExample = new JBLabel("e.g: " + getChangelistName());

        this.mySetActiveCheckBox = new JCheckBox("Set Active");
        this.mySetActiveCheckBox.setBorder(JBUI.Borders.emptyRight(4));
        this.mySetActiveCheckBox.setSelected(settings.isCustom() ? settings.getState().isActive() : false);

        if (settings.isDefault()) {
            disableComponents();
        }
    }

    @Override
    public JPanel getPanel() {

        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(myFieldNamesList).disableUpDownActions();
        toolbarDecorator.setPreferredSize(JBUI.size(300, -1));
        toolbarDecorator.setAddAction(anActionButton -> {
            DefaultActionGroup group = new DefaultActionGroup();
            group.add(new AddFieldAction("issueKey"));
            group.add(new AddFieldAction("projectKey"));

            JBPopupFactory.getInstance()
                    .createActionGroupPopup("Add Field", group, DataManager.getInstance().getDataContext(anActionButton.getContextComponent()), JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, true)
                    .show(anActionButton.getPreferredPopupPoint());

        });

        toolbarDecorator.setRemoveAction(anActionButton -> {
            String fieldToRemove = myFieldNamesList.getSelectedValue();
            ((CollectionListModel<String>) myFieldNamesList.getModel()).remove(fieldToRemove);
            myFieldNamesList.doLayout();
            updateChangelistName();
        });

        return FormBuilder.createFormBuilder()
                .addComponent(myRadioButton)
                //FIXME .setFormLeftIndent(H_GPAP)
                .addLabeledComponent(this.myFieldSeparatorLabel, this.myFieldSeparatorCombo)
                .addLabeledComponent(this.myFieldNamesLabel, toolbarDecorator.createPanel())
                .addComponentToRightColumn(this.myChangelistExample)
                .addComponent(this.mySetActiveCheckBox)
                .getPanel();
    }

    @Override
    public ChangelistState getChangelistState() {
        Separator separator = (Separator) this.myFieldSeparatorCombo.getSelectedItem();
        List<String> fieldNames = ((CollectionListModel<String>) this.myFieldNamesList.getModel()).getItems();
        boolean active = this.mySetActiveCheckBox.isSelected();

        return ChangelistState.getCustom(separator, fieldNames, active);
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if (this.myFieldNamesList.isEmpty()) {
            return new ValidationInfo("You must select at least one field");
        }

        return null;
    }

    @Override
    public void enableComponents() {
        super.enableComponents();
        this.myFieldSeparatorLabel.setEnabled(true);
        this.myFieldSeparatorCombo.setEnabled(true);
        this.myFieldNamesLabel.setEnabled(true);
        this.myFieldNamesList.setEnabled(true);
        this.myChangelistExample.setEnabled(true);
        this.mySetActiveCheckBox.setEnabled(true);
    }

    @Override
    public void disableComponents() {
        super.disableComponents();
        this.myFieldSeparatorLabel.setEnabled(false);
        this.myFieldSeparatorCombo.setEnabled(false);
        this.myFieldNamesLabel.setEnabled(false);
        this.myFieldNamesList.setEnabled(false);
        this.myChangelistExample.setEnabled(false);
        this.mySetActiveCheckBox.setEnabled(false);
    }

    private String getChangelistName() {
        Separator separator = (Separator) myFieldSeparatorCombo.getSelectedItem();
        List<String> fieldsNames = ((CollectionListModel<String>) myFieldNamesList.getModel()).getItems();

        return StringUtil.join(fieldsNames, separator.getSeparator());
    }

    private void updateChangelistName() {
        this.myChangelistExample.setText("e.g: " + getChangelistName());
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
            ((CollectionListModel<String>) myFieldNamesList.getModel()).add(field);
            myFieldNamesList.setSelectedIndex(myFieldNamesList.getModel().getSize() - 1);
            myFieldNamesList.doLayout();
            updateChangelistName();
        }
    }


}
