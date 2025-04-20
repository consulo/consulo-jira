package com.intellij.tasks.jira.jql;

import consulo.language.file.FileViewProvider;
import consulo.language.impl.psi.PsiFileBase;
import consulo.virtualFileSystem.fileType.FileType;

import jakarta.annotation.Nonnull;

/**
 * @author Mikhail Golubev
 */
public class JqlFile extends PsiFileBase
{
  public JqlFile(@Nonnull FileViewProvider viewProvider) {
    super(viewProvider, JqlLanguage.INSTANCE);
  }

  @Nonnull
  @Override
  public FileType getFileType() {
    return JqlFileType.INSTANCE;
  }
}
