package ru.jamsys.sub;

import java.util.HashMap;
import java.util.Map;

public class DataState {
    public long revisionState = 0;
    public Map<String, Object> state = new HashMap<>();
    public String stateJson = null;
}