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
package com.intellij.tasks.jira.model;

import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;

import java.util.Date;

/**
 * @author Mikhail Golubev
 */
public class JiraComment {
  private JiraUser author;
  private JiraUser updateAuthor;
  private Date updated;
  private Date created;
  private String self;
  private String body;

  @Nonnull
  public JiraUser getAuthor() {
    return author;
  }

  @Nonnull
  public JiraUser getUpdateAuthor() {
    return updateAuthor;
  }

  @Nonnull
  public Date getUpdated() {
    return updated;
  }

  @Nonnull
  public Date getCreated() {
    return created;
  }

  @Nonnull
  public String getCommentUrl() {
    return self;
  }

  @Nonnull
  public String getBody() {
    return body;
  }

  @Override
  public String toString() {
    return String.format("JiraComment(text='%s')", StringUtil.first(body, 30, true));
  }
}
