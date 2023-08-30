package com.intellij.jira.settings.branch;

import com.intellij.jira.util.Separator;
import com.intellij.util.xmlb.annotations.OptionTag;

import java.util.List;

public class BranchSettingsState {

    private List<String> myBranchTypes;
    private NameConfig myNameConfig = NameConfig.DEFAULT;

    public BranchSettingsState() { }

    private BranchSettingsState(List<String> branchTypes, NameConfig nameConfig) {
        myBranchTypes = branchTypes;
        myNameConfig = nameConfig;
    }

    public static BranchSettingsState getDefault(List<String> branchTypes) {
        return new BranchSettingsState(branchTypes, NameConfig.DEFAULT);
    }
    public static BranchSettingsState getCustom(List<String> branchTypes, Separator separator, List<String> fieldNames) {
       return new BranchSettingsState(branchTypes, new NameConfig(true, separator, fieldNames));
    }

    @OptionTag
    public List<String> getBranchTypes() {
        return myBranchTypes;
    }

    @OptionTag(tag = "branchNameConfig", nameAttribute = "")
    public NameConfig getNameConfig() {
        return myNameConfig;
    }

    public Separator getFieldSeparator() {
        return myNameConfig.getFieldSeparator();
    }


    public List<String> getFieldNames() {
        return myNameConfig.getFieldNames();
    }


    public boolean isCustom() {
        return myNameConfig.isCustom();
    }


    public void setBranchTypes(List<String> branchTypes) {
        myBranchTypes = branchTypes;
    }

    public void setNameConfig(NameConfig nameConfig) {
        myNameConfig = nameConfig;
    }

    public static class NameConfig {

        private static final NameConfig DEFAULT = new NameConfig(false, Separator.HYPHEN, List.of("key"));

        private boolean isCustom;

        private Separator fieldSeparator;

        private List<String> fieldNames;

        public NameConfig() { }

        public NameConfig(boolean isCustom, Separator fieldSeparator, List<String> fieldNames) {
            this.isCustom = isCustom;
            this.fieldSeparator = fieldSeparator;
            this.fieldNames = fieldNames;
        }

        @OptionTag("custom")
        public boolean isCustom() {
            return isCustom;
        }

        @OptionTag("separator")
        public Separator getFieldSeparator() {
            return fieldSeparator;
        }

        @OptionTag
        public List<String> getFieldNames() {
            return fieldNames;
        }

        public void setCustom(boolean custom) {
            isCustom = custom;
        }

        public void setFieldSeparator(Separator fieldSeparator) {
            this.fieldSeparator = fieldSeparator;
        }

        public void setFieldNames(List<String> fieldNames) {
            this.fieldNames = fieldNames;
        }
    }

}
