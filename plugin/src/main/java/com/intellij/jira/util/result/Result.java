package com.intellij.jira.util.result;

public interface Result<T> {

    boolean isValid();


    T get();

}
