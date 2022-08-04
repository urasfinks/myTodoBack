package ru.jamsys.sub;

import com.google.gson.Gson;

import java.util.Map;

public class DataTemplate {
    public Object data;
    public String template;

    public DataTemplate(String data, String template) {
        this.data = new Gson().fromJson(data, Map.class);
        this.template = template;
    }

    public DataTemplate(Map<String, Object> data, String template) {
        this.data = data;
        this.template = template;
    }
}