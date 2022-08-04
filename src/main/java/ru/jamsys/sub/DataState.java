package ru.jamsys.sub;

import java.util.Map;

public class DataState {
    public long revisionState = 0;
    public Map<String, Object> state;
    public String stateJson;
}