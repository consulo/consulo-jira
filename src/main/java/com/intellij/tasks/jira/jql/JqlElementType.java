/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.tasks.jira.jql;

import consulo.language.ast.IElementType;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NonNls;
import jakarta.annotation.Nonnull;

import java.lang.reflect.Constructor;

/**
 * @author Mikhail Golubev
 */
public class JqlElementType extends IElementType
{
  private static final Class<?>[] PARAMETER_TYPES = {ASTNode.class};

  private final Class<? extends PsiElement> myClass;
  private Constructor<? extends PsiElement> myConstructor;

  public JqlElementType(@Nonnull @NonNls String debugName) {
    this(debugName, ASTWrapperPsiElement.class);
  }

  public JqlElementType(@Nonnull @NonNls String debugName, @Nonnull Class<? extends PsiElement> cls) {
    super(debugName, JqlLanguage.INSTANCE);
    myClass = cls;
  }

  @Override
  public String toString() {
    return "JQL: " + super.toString();
  }

  @Nonnull
  public PsiElement createElement(@Nonnull ASTNode node) {
    try {
      if (myConstructor == null) {
        myConstructor = myClass.getConstructor(PARAMETER_TYPES);
      }
      return myConstructor.newInstance(node);
    }
    catch (Exception e) {
      throw new AssertionError(
        String.format("Class %s must have constructor accepting single ASTNode parameter", myClass.getName()));
    }
  }
}
