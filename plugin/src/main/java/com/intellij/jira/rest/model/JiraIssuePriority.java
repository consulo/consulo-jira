package com.intellij.jira.rest.model;

import consulo.util.lang.StringUtil;public class JiraIssuePriority {

    private static final String SVG_SUFFIX = ".svg";
    private static final String PNG_SUFFIX = ".png";

    private String id;
    private String self;
    private String name;
    private String iconUrl;

    public JiraIssuePriority() { }

    public String getId() {
        return id;
    }

    public String getSelf() {
        return self;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        if (!StringUtil.isEmpty(iconUrl) && StringUtil.endsWith(iconUrl, SVG_SUFFIX)) {
            iconUrl = iconUrl.replace(SVG_SUFFIX, PNG_SUFFIX);
        }

        return iconUrl;
    }

    @Override
    public String toString() {
        return name ;
    }
}
