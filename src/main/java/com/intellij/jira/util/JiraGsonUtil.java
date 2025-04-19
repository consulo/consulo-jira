package com.intellij.jira.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.*;

import static com.intellij.tasks.jira.JiraUtil.GSON;
import static consulo.util.lang.StringUtil.trim;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class JiraGsonUtil {

    public static JsonObject createNameObject(String value){
        return createObject("name", value);
    }

    public static JsonArray createArrayNameObject(String value){
        return createArrayNameObjects(Collections.singletonList(value));
    }

    public static JsonArray createArrayNameObjects(List<String> values){
        JsonArray array = new JsonArray();

        for(String value : values){
            JsonObject name = new JsonObject();
            name.add("name", createPrimitive(value));
            array.add(name);
        }

        return array;
    }

    public static JsonElement createNameObject(String value, boolean isArray){
        return isArray ? createArrayNameObject(value) : createNameObject(value);
    }

    public static JsonObject createIdObject(String value){
        return createObject("id", value);
    }

    public static JsonObject createObject(String property, String value){
        JsonObject name = new JsonObject();
        name.add(property, createPrimitive(value));

        return name;
    }

    public static JsonArray createArrayObject(String property, List<String> values){
        JsonArray array = new JsonArray();
        JsonObject o = new JsonObject();
        for(String value : values){
            o.add(property, createPrimitive(value));
        }

        array.add(o);
        return array;
    }

    public static JsonArray createArray(List<String> values) {
        JsonArray array = new JsonArray();
        for(String value : values){
            array.add(value);
        }

        return array;
    }

    public static boolean isEmpty(JsonArray jsonArray){
        return nonNull(jsonArray) && jsonArray.size() == 0;
    }

    public static JsonElement createPrimitive(String value){
        return new JsonPrimitive(trim(value));
    }

    public static JsonElement createPrimitive(Double value){
        return new JsonPrimitive(value);
    }

    public static JsonElement createPrimitive(Integer value){
        return new JsonPrimitive(value);
    }

    public static String getAsString(JsonElement element) {
        return getAs(element, String.class);
    }

    public static Date getAsDate(JsonElement element) {
        return getAs(element, Date.class);
    }

    public static <T> List<T> getAsList(JsonElement element, Class<T[]> clazz) {
        T[] values = getAs(element, clazz);
        if (isNull(values)) {
            return new ArrayList<>();
        }

        return Arrays.asList(values);
    }

    public static <T> T getAs(JsonElement element, Class<T> clazz) {
        return GSON.fromJson(element, clazz);
    }

    public static <T> T getAs(String response, Class<T> clazz) {
        return GSON.fromJson(response, clazz);
    }

    public static <T> List<T> getAsList(String response, Class<T[]> clazz) {
        T[] values = getAs(response, clazz);
        if (isNull(values)) {
            return new ArrayList<>();
        }

        return Arrays.asList(values);
    }

}
