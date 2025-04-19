package com.intellij.jira.util;

import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.awt.Image.SCALE_SMOOTH;

public class JiraIconUtil {

    private static final int ICON_HEIGHT = 16;
    private static final int ICON_WIDTH = 16;
    private static final int SUCCESS = 8;

    private static final Map<String, Icon> myIconsCache = new ConcurrentHashMap<>();

    private JiraIconUtil() { }

    public static consulo.ui.image.Image getIcon(@Nullable String iconUrl){
        if(StringUtil.isEmpty(iconUrl)){
            return null;
        }

        Icon icon = myIconsCache.get(iconUrl);
        if (Objects.nonNull(icon)) {
            return icon;
        }

        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon(new URL(iconUrl));
            if (imageIcon.getImageLoadStatus() == SUCCESS) {
                Image img = imageIcon.getImage();
                if (img != null) {
                    Image scaledImage = img.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, SCALE_SMOOTH);
                    imageIcon = new ImageIcon(scaledImage);

                    myIconsCache.put(iconUrl, imageIcon);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return imageIcon;
    }

}
