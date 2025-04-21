package com.intellij.jira.jql;

import com.intellij.jira.rest.model.jql.JQLSearcher;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.component.persist.StoragePathMacros;
import consulo.project.Project;
import consulo.util.xml.serializer.XmlSerializationException;
import consulo.util.xml.serializer.XmlSerializer;
import consulo.util.xml.serializer.XmlSerializerUtil;
import consulo.util.xml.serializer.annotation.Tag;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@State(name = "JQLSearcherProjectManager", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public class JQLSearcherProjectManager implements PersistentStateComponent<JQLSearcherProjectManager.Config> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JQLSearcherProjectManager.class);
    private final Project myProject;

    private List<JQLSearcher> projectSearchers = new ArrayList<>();
    private int mySelectedSearcher = -1;

    private Config myConfig = new Config();

    @Inject
    protected JQLSearcherProjectManager(Project project) {
        myProject = project;
    }

    public static JQLSearcherProjectManager getInstance(@NotNull Project project) {
        return project.getInstance(JQLSearcherProjectManager.class);
    }

    @Nullable
    @Override
    public Config getState() {
        myConfig.selected = mySelectedSearcher;
        myConfig.searchers = XmlSerializer.serialize(getSearchersAsArray());
        return myConfig;
    }

    @Override
    public void loadState(@NotNull Config config) {
        XmlSerializerUtil.copyBean(config, myConfig);

        projectSearchers.clear();
        Element element = config.searchers;
        List<JQLSearcher> searchers = loadSearchers(element);
        projectSearchers.addAll(searchers);

        mySelectedSearcher = config.selected;
    }

    private List<JQLSearcher> loadSearchers(Element element) {
        List<JQLSearcher> searchers = new ArrayList<>();
        if(nonNull(element)){
            for(Element o : element.getChildren()){
                try{
                    JQLSearcher searcher = XmlSerializer.deserialize(o, JQLSearcher.class);
                    searchers.add(searcher);
                }catch (XmlSerializationException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        return searchers;
    }


    public List<JQLSearcher> getSearchers() {
        return new ArrayList<>(projectSearchers);
    }

    public int getSelectedSearcherIndex(){
        return mySelectedSearcher;
    }

    private JQLSearcher[] getSearchersAsArray(){
        return getSearchers().toArray(new JQLSearcher[0]);
    }

    public boolean hasSelectedSearcher(){
        return mySelectedSearcher > -1;
    }

    public void setSelectedSearcher(int selectedSearcher) {
        if (selectedSearcher >= 0) {
            this.mySelectedSearcher = selectedSearcher;
        }
    }

    public void setSearchers(List<JQLSearcher> searcherList, int selected) {
        this.projectSearchers.clear();
        for (JQLSearcher searcher : searcherList){
            add(searcher);
        }
        this.mySelectedSearcher = selected;
    }

    private void add(JQLSearcher searcher){
        if(!searcher.isShared() && !projectSearchers.contains(searcher)){
            projectSearchers.add(searcher);
        }
    }

    public static class Config{
        @Tag("selected")
        public int selected;

        @Tag("searchers")
        public Element searchers;
    }

}
