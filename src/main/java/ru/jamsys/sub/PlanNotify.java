package ru.jamsys.sub;

import ru.jamsys.util.Util;

public class PlanNotify {
    String data;
    long timestamp;

    public PlanNotify(String data, long timestamp) throws Exception {
        this.data = data;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        String t = "";
        try {
            t = Util.timestampToDate(timestamp, "dd.MM.yyyy HH:mm");
        } catch (Exception e) {
        }
        return "PlanNotify{" +
                "data='" + data + '\'' +
                ", timestamp=" + t +
                '}';
    }
}