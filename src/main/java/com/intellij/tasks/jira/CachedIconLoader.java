/**
 * Copyright (C) 2008 Atlassian
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intellij.tasks.jira;

import consulo.ui.image.Image;
import consulo.ui.image.ImageEffects;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class CachedIconLoader {
  private static Map<String, Image> icons = new HashMap<>();
  private static Map<String, Image> disabledIcons = new HashMap<>();

  private CachedIconLoader() {
  }

  public static Image getDisabledIcon(String urlString) {
    return disabledIcons.get(urlString);
  }

  private static void addDisabledIcon(String urlString, Image icon) {
    disabledIcons.put(urlString, icon);
  }

  private static Image generateDisabledIcon(Image icon) {
    return ImageEffects.grayed(icon);
  }

  private static void maybeGenerateDisabledIcon(String urlString, Image icon) {
    if (disabledIcons.containsKey(urlString) || icon == null) {
      return;
    }
    addDisabledIcon(urlString, generateDisabledIcon(icon));
  }

  public static Image getIcon(URL url) {
    if (url != null) {
      String key = url.toString();
      if (!icons.containsKey(key)) {
        try {
          Image i = Image.fromUrl(url);

          icons.put(key, i);
          maybeGenerateDisabledIcon(key, i);
        }
        catch (IOException ignored) {
        }
      }
      return icons.get(key);
    }
    else {
      return null;
    }
  }

  public static Image getIcon(String urlString) {
    if (urlString != null) {
      if (!icons.containsKey(urlString)) {
        try {
          URL url = new URL(urlString);
          Image i = Image.fromUrl(url);
          icons.put(urlString, i);
          maybeGenerateDisabledIcon(urlString, i);
        }
        catch (IOException ignored) {
          return null;
        }
      }
      return icons.get(urlString);
    }
    else {
      return null;
    }
  }

}
