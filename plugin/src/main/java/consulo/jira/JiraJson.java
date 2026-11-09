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
package consulo.jira;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jspecify.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author VISTALL
 * @since 2026-11-09
 */
public final class JiraJson {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private JiraJson() {
    }

    public static @Nullable JsonObject object(@Nullable JsonObject parent, String name) {
        if (parent == null) {
            return null;
        }
        JsonElement element = parent.get(name);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    public static List<JsonObject> objects(@Nullable JsonObject parent, String name) {
        List<JsonObject> result = new ArrayList<>();
        if (parent == null) {
            return result;
        }
        JsonElement element = parent.get(name);
        if (element != null && element.isJsonArray()) {
            for (JsonElement item : element.getAsJsonArray()) {
                if (item.isJsonObject()) {
                    result.add(item.getAsJsonObject());
                }
            }
        }
        return result;
    }

    public static List<String> strings(@Nullable JsonObject parent, String name) {
        List<String> result = new ArrayList<>();
        if (parent == null) {
            return result;
        }
        JsonElement element = parent.get(name);
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement item : array) {
                if (item.isJsonPrimitive()) {
                    result.add(item.getAsString());
                }
                else if (item.isJsonObject()) {
                    String value = string(item.getAsJsonObject(), "name");
                    if (value != null) {
                        result.add(value);
                    }
                }
            }
        }
        return result;
    }

    public static @Nullable String string(@Nullable JsonObject parent, String name) {
        if (parent == null) {
            return null;
        }
        JsonElement element = parent.get(name);
        return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
    }

    public static long number(@Nullable JsonObject parent, String name) {
        if (parent == null) {
            return 0;
        }
        JsonElement element = parent.get(name);
        return element != null && element.isJsonPrimitive() ? element.getAsLong() : 0;
    }

    /**
     * The name of a nested object such as {@code status}, {@code priority} or {@code issuetype}.
     */
    public static @Nullable String name(@Nullable JsonObject parent, String name) {
        return string(object(parent, name), "name");
    }

    public static @Nullable String userName(@Nullable JsonObject user) {
        if (user == null) {
            return null;
        }
        String displayName = string(user, "displayName");
        return displayName != null ? displayName : string(user, "name");
    }

    public static @Nullable Date date(@Nullable JsonObject parent, String name) {
        String value = string(parent, name);
        if (value == null) {
            return null;
        }
        try {
            return Date.from(OffsetDateTime.parse(value, DATE_FORMAT).toInstant());
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }
}
