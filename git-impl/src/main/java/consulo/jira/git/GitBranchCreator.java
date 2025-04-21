package consulo.jira.git;

import consulo.annotation.component.ExtensionImpl;
import consulo.dataContext.DataContext;
import consulo.jira.vcs.BranchCreator;
import consulo.project.Project;
import jakarta.inject.Inject;

/**
 * @author VISTALL
 * @since 2025-04-20
 */
@ExtensionImpl
public class GitBranchCreator implements BranchCreator {
    private final Project myProject;

    @Inject
    public GitBranchCreator(Project project) {
        myProject = project;
    }

    @Override
    public boolean createBranch(String branchName, DataContext dataProvider) {
        // TODO !
        //        GitRepository gitRepository = GitBranchUtil.guessRepositoryForOperation(project, e.getDataContext());
//        if (Objects.nonNull(gitRepository)) {
//            myProject.getInstance(GitBrancher.class).createBranch(branchName, Map.of(gitRepository, GitUtil.HEAD));
//            return true;
//        }
        return false;
    }
}
