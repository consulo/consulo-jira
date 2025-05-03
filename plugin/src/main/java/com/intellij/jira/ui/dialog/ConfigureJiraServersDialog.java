package com.intellij.jira.ui.dialog;

import com.intellij.jira.server.JiraServer;
import com.intellij.jira.server.JiraServerManager;
import com.intellij.jira.server.editor.JiraServerEditor;
import com.intellij.jira.ui.panels.JiraPanel;
import com.intellij.jira.util.JiraPanelUtil;
import com.intellij.jira.util.SimpleSelectableList;
import consulo.application.ApplicationManager;
import consulo.application.util.ConcurrentFactoryMap;
import consulo.jira.icon.JiraIconGroup;
import consulo.project.Project;
import consulo.ui.ex.SimpleTextAttributes;
import consulo.ui.ex.awt.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.Objects.nonNull;

public class ConfigureJiraServersDialog extends DialogWrapper {

    private static final JPanel EMPTY_PANEL = JiraPanelUtil.createPlaceHolderPanel("No server selected.").withMinimumWidth(450).withMinimumHeight(100);
    private static final String EMPTY_PANEL_NAME = "empty.panel";

    private final Project myProject;
    private final JiraServerManager myManager;

    private SimpleSelectableList<JiraServer> myServers;
    private JBList<JiraServer> myServersList;
    private List<JiraServerEditor> myEditors = new ArrayList<>();

    private JPanel myJiraServerEditor;
    private Splitter mySplitter;

    private int count;
    private final Map<JiraServer, String> myServerNames = ConcurrentFactoryMap.createMap(server -> Integer.toString(count++));

    private BiConsumer<JiraServer, Boolean> myChangeListener;
    private Consumer<JiraServer> myChangeUrlListener;


    public ConfigureJiraServersDialog(@Nonnull Project project) {
        super(project, false);
        this.myProject = project;
        this.myManager = ApplicationManager.getApplication().getInstance(JiraServerManager.class);
        init();
    }


    @Override
    protected void init() {
        myJiraServerEditor = new JPanel(new CardLayout());
        myJiraServerEditor.add(EMPTY_PANEL, EMPTY_PANEL_NAME);

        myServers = myManager.getAllServers(myProject);

        CollectionListModel<JiraServer> listModel = new CollectionListModel(new ArrayList<>());
        for(JiraServer server : myServers.getItems()){
            JiraServer clone = server.clone();
            listModel.add(clone);
        }

        this.myChangeListener = (server, selected) -> myServers.updateSelectedItem(server, selected);
        this.myChangeUrlListener = server -> ((CollectionListModel)myServersList.getModel()).contentsChanged(server);


        for(int i = 0; i < myServers.getItems().size(); i++){
            addJiraServerEditor(myServers.getItems().get(i), i == myManager.getSelectedServerIndex(myProject));
        }


        myServersList = new JBList<>();
        myServersList.setEmptyText("No servers");
        myServersList.setModel(listModel);
        myServersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        myServersList.addListSelectionListener(e -> {
            JiraServer selectedServer = getSelectedServer();
            if(nonNull(selectedServer)) {
                String name = myServerNames.get(selectedServer);
                updateEditorPanel(name);
            }
        });

        myServersList.setCellRenderer(new ColoredListCellRenderer<>() {
            @Override
            protected void customizeCellRenderer(@Nonnull JList<? extends JiraServer> list, JiraServer value, int index, boolean selected, boolean hasFocus) {
                setIcon(JiraIconGroup.jiraicon());
                append(value.getPresentableName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            }
        });

        setTitle("Configure Servers");
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        mySplitter = new JBSplitter(true, 0.6f);
        mySplitter.setFirstComponent(createServersPanel());
        mySplitter.setSecondComponent(createDetailsServerPanel());

        return mySplitter;
    }

    @Nonnull
    @Override
    protected List<ValidationInfo> doValidateAll() {
        List<ValidationInfo> validationInfos = new ArrayList<>();
        for (JiraServerEditor editor : myEditors) {
            ValidationInfo info = editor.validate();
            if (Objects.nonNull(info)) {
                validationInfos.add(info);
            }
        }

        return validationInfos;
    }

    @Override
    protected void doOKAction() {
        myManager.setServers(myProject, myServers);

        super.doOKAction();
    }

    private JComponent createServersPanel() {

        if(myServers.hasSelectedItem()){
            myServersList.setSelectedValue(myServers.getSelectedItem(), true);
        }

        JPanel myPanel = new JiraPanel(new BorderLayout());
        myPanel.setMinimumSize(JBUI.size(-1, 200));
        myPanel.add(ToolbarDecorator.createDecorator(myServersList)
                        .setAddAction(button -> {
                            addJiraServer();
                        })
                        .setRemoveAction(button -> {
                            if (Messages.showOkCancelDialog(myProject, "You are going to delete this server, are you sure?","Delete Server", "OK", "Cancel", Messages.getQuestionIcon()) == Messages.OK) {
                                removeJiraServer();
                            }
                        })
                        .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        return myPanel;
    }


    private void addJiraServer(){
        JiraServer server = new JiraServer();
        myServers.add(server);
        ((CollectionListModel) myServersList.getModel()).add(server);
        addJiraServerEditor(server, false);
        myServersList.setSelectedIndex(myServersList.getModel().getSize() - 1);
    }

    private void addJiraServerEditor(JiraServer server, boolean selected){
        JiraServerEditor editor = new JiraServerEditor(myProject, server, selected, myChangeListener, myChangeUrlListener);
        myEditors.add(editor);
        String name = myServerNames.get(server);
        myJiraServerEditor.add(editor.createPanel(), name);
        myJiraServerEditor.doLayout();
    }

    private void removeJiraServer(){
        int selectedServer = myServersList.getSelectedIndex();
        if(selectedServer > -1){
            ((CollectionListModel) myServersList.getModel()).remove(selectedServer);
            myEditors.remove(selectedServer);
            myServers.remove(selectedServer);
            myServersList.setSelectedIndex(myServers.getSelectedItemIndex());
        }


        if(myServers.isEmpty()){
            updateEditorPanel(EMPTY_PANEL_NAME);
        }

    }

    private void updateEditorPanel(String name){
        ((CardLayout) myJiraServerEditor.getLayout()).show(myJiraServerEditor, name);
        mySplitter.doLayout();
        mySplitter.repaint();
    }

    private JComponent createDetailsServerPanel() {
        return myJiraServerEditor;
    }

    @Nullable
    private JiraServer getSelectedServer(){
        return myServersList.getSelectedValue();
    }
}
