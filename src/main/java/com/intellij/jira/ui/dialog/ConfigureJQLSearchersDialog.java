package com.intellij.jira.ui.dialog;

import com.intellij.jira.jql.JQLSearcherManager;
import com.intellij.jira.rest.model.jql.JQLSearcher;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.IssueSearcherListComparator;
import com.intellij.jira.util.ListComparator;
import com.intellij.jira.util.SimpleSelectableList;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.jira.util.JiraLabelUtil.getBgRowColor;
import static com.intellij.jira.util.JiraLabelUtil.getFgRowColor;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class ConfigureJQLSearchersDialog extends DialogWrapper {

    private final Project myProject;
    private final JQLSearcherManager myManager;

    private List<JQLSearcher> myOldSearchers = new ArrayList<>();
    private SimpleSelectableList<JQLSearcher> mySearchers;

    private final ColumnInfo<JQLSearcher, String> ALIAS_COLUMN = new AliasColumn();
    private final ColumnInfo<JQLSearcher, String> JQL_COLUMN = new JQLColumn();
    private final ColumnInfo<JQLSearcher, String> SHARED_COLUMN = new SharedColumn();


    private TableView<JQLSearcher> myTable;
    private ListTableModel<JQLSearcher> myModel;

    public ConfigureJQLSearchersDialog(@NotNull Project project) {
        super(project, false);
        this.myProject = project;
        this.myManager = JQLSearcherManager.getInstance();

        init();
    }


    @Override
    protected void init() {
        mySearchers = SimpleSelectableList.empty();

        myModel = new JQLSearcherListTableModel();

        List<JQLSearcher> searchers = myManager.getSearchers(myProject);
        myOldSearchers.addAll(new ArrayList<>(searchers));

        for(JQLSearcher searcher : searchers){
            JQLSearcher clone = searcher.clone();
            mySearchers.add(clone);
            myModel.addRow(clone);
        }

        mySearchers.selectItem(myManager.getSelectedSearcherIndex(myProject));
        myTable = new JQLSearcherTable(myModel);


        setTitle("Configure JQL Searcher");
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel myPanel = new JiraPanel(new BorderLayout());
        myPanel.setMinimumSize(JBUI.size(500, 300));
        myPanel.add(ToolbarDecorator.createDecorator(myTable)
                        .setAddAction(button -> {
                            NewJQLSearcherDialog dlg = new NewJQLSearcherDialog(myProject, false);
                            if (dlg.showAndGet()) {
                                JQLSearcher newJqlSearcher = dlg.getJqlSearcher();
                                newJqlSearcher.setShared(dlg.isSharedSearcher());
                                mySearchers.add(newJqlSearcher, dlg.isSelectedSearcher());
                                myModel.addRow(newJqlSearcher);
                                myModel.fireTableDataChanged();
                            }
                        })
                        .setEditAction(button -> {
                            int selRow = myTable.getSelectedRow();
                            boolean isDefaultSearcher = (selRow == mySearchers.getSelectedItemIndex());
                            JQLSearcher selectedSearcher = getSelectedJQLSearcher();
                            EditJQLSearcherDialog dlg = new EditJQLSearcherDialog(myProject, selectedSearcher, isDefaultSearcher, false);

                            if (dlg.showAndGet()) {
                                selectedSearcher.setShared(dlg.isSharedSearcher());
                                mySearchers.update(selRow, selectedSearcher, dlg.isSelectedSearcher());
                                myModel.fireTableDataChanged();
                            }
                        })
                        .setRemoveAction(button -> {
                            if (Messages.showOkCancelDialog(myProject, "You are going to delete this searcher, are you sure?","Delete Searcher", Messages.getOkButton(), Messages.getCancelButton(), Messages.getQuestionIcon()) == Messages.OK) {

                                mySearchers.remove(myTable.getSelectedRow());
                                myModel.removeRow(myTable.getSelectedRow());
                                myModel.fireTableDataChanged();
                            }
                        })
                        .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        return myPanel;
    }



    private JQLSearcher getSelectedJQLSearcher(){
        return myModel.getItem(myTable.getSelectedRow());
    }

    @Override
    protected void doOKAction() {

        ListComparator.Result<JQLSearcher> result = new IssueSearcherListComparator().compare(myOldSearchers, new ArrayList<>(mySearchers.getItems()));

        myManager.setSearchers(myProject, result);

        super.doOKAction();
    }


    private class JQLSearcherListTableModel extends ListTableModel<JQLSearcher> {

        public JQLSearcherListTableModel() {
            super(ALIAS_COLUMN, JQL_COLUMN, SHARED_COLUMN);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    private class JQLSearcherTable extends TableView<JQLSearcher> {

        public JQLSearcherTable(ListTableModel<JQLSearcher> model) {
            super(model);
            setShowGrid(false);
            setSelectionMode(SINGLE_SELECTION);
            setIntercellSpacing(JBUI.emptySize());
        }
    }

    private class JQLSearcherTableCellRenderer extends DefaultTableCellRenderer{

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if(row == mySearchers.getSelectedItemIndex()){
                setBackground(getBgRowColor(isSelected));
                setForeground(getFgRowColor(isSelected));
            }
            else{
                setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            }

            return this;
        }
    }

    private abstract class BaseColumn extends ColumnInfo<JQLSearcher, String>{

        private final JQLSearcherTableCellRenderer JQL_SEARCHER_RENDERER = new JQLSearcherTableCellRenderer();

        public BaseColumn(String name) {
            super(name);
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(JQLSearcher jqlSearcher) {
            return JQL_SEARCHER_RENDERER;
        }
    }

    private class AliasColumn extends BaseColumn {

        public AliasColumn() {
            super("Alias");
        }

        @Nullable
        @Override
        public String valueOf(JQLSearcher jqlSearcher) {
            return jqlSearcher.getAlias();
        }
    }

    private class JQLColumn extends BaseColumn {

        public JQLColumn() {
            super("JQL");
        }

        @Nullable
        @Override
        public String valueOf(JQLSearcher jqlSearcher) {
            return jqlSearcher.getJql();
        }
    }

    private class SharedColumn extends BaseColumn {

        public SharedColumn() {
            super("Shared");
        }

        @Nullable
        @Override
        public String valueOf(JQLSearcher jqlSearcher) {
            return jqlSearcher.isShared() ? "Yes" : "No";
        }
    }

}
