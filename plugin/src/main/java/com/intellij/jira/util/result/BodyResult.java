package com.intellij.jira.util.result;

import static java.util.Objects.nonNull;

public class BodyResult implements Result {

    private Object body;

    private BodyResult(Object body) {
        this.body = body;
    }

    public static <T> BodyResult ok(T body){
        return new BodyResult(body);
    }

    public static BodyResult error(){
        return new BodyResult(null);
    }

    @Override
    public boolean isValid() {
        return nonNull(body);
    }

    @Override
    public Object get() {
        return body;
    }
}
