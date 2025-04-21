package consulo.jira.vcs;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.dataContext.DataContext;

/**
 * @author VISTALL
 * @since 2025-04-20
 */
@ExtensionAPI(ComponentScope.PROJECT)
public interface BranchCreator {
    boolean createBranch(String branchName, DataContext dataProvider);
}
