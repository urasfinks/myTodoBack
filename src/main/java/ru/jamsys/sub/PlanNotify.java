package ru.jamsys.sub;

import com.google.gson.Gson;
import ru.jamsys.JS;
import ru.jamsys.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlanNotify {
    public String data;
    public long timestamp;
    public Long interval = null; //-1 - Месяц; -12 - Год
    public int repeat = 0; // 0 - единоразрвое опощение; -1 - без остановки

    public PlanNotify(String data, long timestamp, long interval, int repeat) {
        this.data = data;
        this.timestamp = timestamp;
        this.interval = interval;
        this.repeat = repeat;
    }

    public PlanNotify(String data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public String sequence() {
        StringBuilder sb = new StringBuilder();
        if (interval != 0) {
            int c = repeat;
            if (c < 0 || c > 10) {
                c = 10;
            }
            long cur = timestamp;
            while (c > 0) {
                c--;
                //System.out.println(Util.timestampToDate(cur, "dd.MM.yyyy HH:mm"));
                sb.append(Util.timestampToDate(cur, "dd.MM.yyyy HH:mm") + ";");
                if (interval > 0) {
                    cur += interval;
                } else if (interval < 0) {
                    try {
                        cur = Util.dateToTimestamp(
                                addMonths(
                                        Util.timestampToDate(cur, "dd.MM.yyyy HH:mm"),
                                        new Double(interval * -1).intValue()
                                ),
                                "dd.MM.yyyy HH:mm"
                        );

                    } catch (Exception e) {
                    }
                }
            }
        }
        return sb.toString();
    }

    public String addMonths(String dateAsString, int nbMonths) throws ParseException {
        String format = "dd.MM.yyyy HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dateAsObj = sdf.parse(dateAsString);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateAsObj);
        cal.add(Calendar.MONTH, nbMonths);
        Date dateAsObjAfterAMonth = cal.getTime();
        return sdf.format(dateAsObjAfterAMonth);
    }

    @Override
    public String toString() {
        return "PlanNotify{" +
                "data='" + data + '\'' +
                ", timestamp=" + Util.timestampToDate(timestamp, "dd.MM.yyyy HH:mm") +
                ", interval=" + interval +
                ", count=" + repeat +
                ", sequence=" + sequence() +
                '}';
    }

    public static List<PlanNotify> parse(String str){
        Map<String, Object> x = new Gson().fromJson(str, Map.class);
        String titleTask = "Напоминаю. " + x.get("name");
        JS.TypeNotify typeNotify = JS.TypeNotify.valueOf(((String) x.get("notify")).toUpperCase());

        List<PlanNotify> listPlan = new ArrayList<>();

        String newComplexDateTime = Util.getComplexDateTime(
                (String) x.get("deadLineDate"),
                (String) x.get("deadLineTime")
        );
        if (typeNotify == JS.TypeNotify.STANDARD) {
            long ts = 0;
            try {
                ts = Util.dateToTimestamp(newComplexDateTime, x.containsKey("deadLineTime") ? "dd.MM.yyyy HH:mm" : "dd.MM.yyyy");
            } catch (Exception e) {
                e.printStackTrace();
            }
            long now = System.currentTimeMillis() / 1000;
            listPlan.addAll(Util.getStandardPlanNotify(now, ts, (String) x.get("name")));

        } else if (typeNotify == JS.TypeNotify.ONCE) {
            long ts = 0;
            try {
                ts = Util.dateToTimestamp(newComplexDateTime, x.containsKey("deadLineTime") ? "dd.MM.yyyy HH:mm" : "dd.MM.yyyy");
            } catch (Exception e) {
                e.printStackTrace();
            }
            listPlan.add(new PlanNotify(titleTask, ts));

        } else if (typeNotify == JS.TypeNotify.CUSTOM) {
            String customDate = (String) x.get("custom_date");
            if (customDate != null) {
                String[] listCustomDate = customDate.split("\n");
                for (String item : listCustomDate) {
                    if (!"".equals(item.trim())) {
                        Long t = null;
                        try {
                            t = Util.dateToTimestamp(item, "dd.MM.yyyy HH:mm");
                        } catch (Exception e) {
                        }
                        if (t == null) {
                            try {
                                t = Util.dateToTimestamp(item, "dd.MM.yyyy");
                            } catch (Exception e) {
                            }
                        }
                        if (t != null) {
                            listPlan.add(new PlanNotify(titleTask, t));
                        }
                    }
                }
            }

        } else if (typeNotify == JS.TypeNotify.CYCLE) {
            long ts = 0;
            try {
                ts = Util.dateToTimestamp(newComplexDateTime, x.containsKey("deadLineTime") ? "dd.MM.yyyy HH:mm" : "dd.MM.yyyy");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JS.TypeNotifyInterval typeNotifyInterval = JS.TypeNotifyInterval.valueOf(((String) x.get("interval")).toUpperCase());
            String value = (String) x.get("interval_" + typeNotifyInterval.toString().toLowerCase());
            int repeat = -1;
            try {
                repeat = Integer.parseInt((String) x.get("countRetry"));
            } catch (Exception e) {
            }
            int interval = 0;
            if (typeNotifyInterval == JS.TypeNotifyInterval.HOUR) {
                try {
                    String[] exp = value.split(":");
                    interval += Integer.parseInt(exp[0]) * 60 * 60;
                    interval += Integer.parseInt(exp[1]) * 60;
                } catch (Exception e) {

                }
                listPlan.add(new PlanNotify(titleTask, ts, interval, repeat));
            } else if (typeNotifyInterval == JS.TypeNotifyInterval.DAY) {
                String readValue = value.split(typeNotifyInterval.toString().toLowerCase())[0].replace("1_5", "1.5");
                double d1 = Double.parseDouble(readValue);
                interval = new Double(d1 * 24 * 60 * 60).intValue();
                listPlan.add(new PlanNotify(titleTask, ts, interval, repeat));

            } else if (typeNotifyInterval == JS.TypeNotifyInterval.WEEK) {
                String readValue = value.split(typeNotifyInterval.toString().toLowerCase())[0].replace("1_5", "1.5");
                double d1 = Double.parseDouble(readValue);
                interval = new Double(d1 * 7 * 24 * 60 * 60).intValue();
                listPlan.add(new PlanNotify(titleTask, ts, interval, repeat));

            } else if (typeNotifyInterval == JS.TypeNotifyInterval.MONTH) {
                String readValue = value.split(typeNotifyInterval.toString().toLowerCase())[0];
                int d1 = Integer.parseInt(readValue);
                listPlan.add(new PlanNotify(titleTask, ts, d1 * -1, repeat));

            } else if (typeNotifyInterval == JS.TypeNotifyInterval.YEAR) {
                listPlan.add(new PlanNotify(titleTask, ts, -12, repeat));
            }
        }
        return listPlan;
    }
}