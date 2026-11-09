/*
 * Copyright 2013-2026 consulo.io
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

import org.jspecify.annotations.NullMarked;

/**
 * @author VISTALL
 * @since 2025-04-21
 */
@NullMarked
module com.intellij.jira {
    requires consulo.account.management.api;
    requires consulo.application.api;
    requires consulo.http.api;
    requires consulo.localize.api;
    requires consulo.logging.api;
    requires consulo.project.api;
    requires consulo.task.api;
    requires consulo.ui.api;
    requires consulo.util.io;
    requires consulo.util.lang;

    requires com.google.gson;

    exports consulo.jira;
    exports consulo.jira.icon;
    exports consulo.jira.localize;

    opens consulo.jira to consulo.util.xml.serializer;
}
