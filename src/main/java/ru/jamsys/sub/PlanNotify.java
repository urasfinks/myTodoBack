package ru.jamsys.sub;

import com.google.gson.Gson;
import ru.jamsys.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlanNotify {

    public enum TypeNotify {
        STANDARD,
        ONCE,
        CYCLE,
        CUSTOM,
        NONE
    }

    public enum TypeNotifyInterval {
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }

    public String data;
    public long timestamp;
    public long interval = 0; //-1 - Месяц; -12 - Год
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

    public boolean next() {
        if (repeat > 1) {
            repeat--;
            timestamp = getNext(timestamp);
            return true;
        } else if (repeat < 0) {
            timestamp = getNext(timestamp);
            return true;
        }
        return false;
    }

    private static long getNextStatic(long cur, long interval) {
        if (interval > 0) {
            return cur + interval;
        } else if (interval < 0) {
            try {
                return Util.dateToTimestamp(
                        addMonths(
                                Util.timestampToDate(cur, "dd.MM.yyyy HH:mm"),
                                new Double(interval * -1).intValue()
                        ),
                        "dd.MM.yyyy HH:mm"
                );
            } catch (Exception e) {
            }
        }
        return cur;
    }

    private long getNext(long cur) {
        return getNextStatic(cur, interval);
    }

    public List<Map<String, Object>> getPreviewSequence() {
        List<Map<String, Object>> ret = new ArrayList<>();
        if (interval != 0) {
            int c = repeat;
            if (c < 0 || c > 10) {
                c = 10;
            }
            long cur = timestamp;
            while (c > 0) {
                c--;
                Map<String, Object> a = new HashMap<>();
                a.put("date", Util.timestampToDate(cur, "dd.MM.yyyy HH:mm"));
                a.put("timestamp", cur);
                a.put("data", data);
                ret.add(a);
                cur = getNext(cur);
            }
        } else {
            Map<String, Object> a = new HashMap<>();
            a.put("date", Util.timestampToDate(timestamp, "dd.MM.yyyy HH:mm"));
            a.put("timestamp", timestamp);
            a.put("data", data);
            ret.add(a);
        }
        Collections.sort(ret, new Comparator<Map>() {
            public int compare(Map s1, Map s2) {
                return ((long) s1.get("timestamp") > (long) s2.get("timestamp")) ? 1 : -1;
            }
        });
        return ret;
    }

    private static String addMonths(String dateAsString, int nbMonths) throws ParseException {
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
                ", sequence=" + getPreviewSequence() +
                '}';
    }

    public static List<PlanNotify> parse(String str) {
        List<PlanNotify> listPlan = new ArrayList<>();
        Map<String, Object> x = new Gson().fromJson(str, Map.class);
        if (x.containsKey("notify")) {
            String deadLineTime = (String) x.get("deadLineTime");
            if (deadLineTime == null || "".equals(deadLineTime.trim())) {
                x.remove("deadLineTime");
            }
            String titleTask = "Напоминаю. " + x.get("name");
            TypeNotify typeNotify = TypeNotify.valueOf(((String) x.get("notify")).toUpperCase());

            if(typeNotify == TypeNotify.NONE){
                return listPlan;
            }

            String newComplexDateTime = Util.getComplexDateTime(
                    (String) x.get("deadLineDate"),
                    (String) x.get("deadLineTime")
            );
            if (typeNotify == TypeNotify.STANDARD) {
                long ts = 0;
                try {
                    ts = Util.dateToTimestamp(newComplexDateTime, x.containsKey("deadLineTime") ? "dd.MM.yyyy HH:mm" : "dd.MM.yyyy");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long now = System.currentTimeMillis() / 1000;
                listPlan.addAll(getStandardPlanNotify(now, ts, (String) x.get("name")));

            } else if (typeNotify == TypeNotify.ONCE) {
                long ts = 0;
                try {
                    ts = Util.dateToTimestamp(newComplexDateTime, x.containsKey("deadLineTime") ? "dd.MM.yyyy HH:mm" : "dd.MM.yyyy");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listPlan.add(new PlanNotify(titleTask, ts));

            } else if (typeNotify == TypeNotify.CUSTOM) {
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
            } else if (typeNotify == TypeNotify.CYCLE) {
                long ts = 0;
                try {
                    ts = Util.dateToTimestamp(newComplexDateTime, x.containsKey("deadLineTime") ? "dd.MM.yyyy HH:mm" : "dd.MM.yyyy");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                TypeNotifyInterval typeNotifyInterval = TypeNotifyInterval.valueOf(((String) x.get("interval")).toUpperCase());
                String value = (String) x.get("interval_" + typeNotifyInterval.toString().toLowerCase());
                int repeat = -1;
                try {
                    repeat = Integer.parseInt((String) x.get("countRetry"));
                } catch (Exception e) {
                }
                int interval = 0;
                if (typeNotifyInterval == TypeNotifyInterval.HOUR) {
                    try {
                        String[] exp = value.split(":");
                        interval += Integer.parseInt(exp[0]) * 60 * 60;
                        interval += Integer.parseInt(exp[1]) * 60;
                    } catch (Exception e) {

                    }
                    listPlan.add(new PlanNotify(titleTask, ruleNextNotify(ts, interval), interval, repeat));
                } else if (typeNotifyInterval == TypeNotifyInterval.DAY) {
                    String readValue = value.split(typeNotifyInterval.toString().toLowerCase())[0].replace("1_5", "1.5");
                    double d1 = Double.parseDouble(readValue);
                    interval = new Double(d1 * 24 * 60 * 60).intValue();
                    listPlan.add(new PlanNotify(titleTask, ruleNextNotify(ts, interval), interval, repeat));

                } else if (typeNotifyInterval == TypeNotifyInterval.WEEK) {
                    String readValue = value.split(typeNotifyInterval.toString().toLowerCase())[0].replace("1_5", "1.5");
                    double d1 = Double.parseDouble(readValue);
                    interval = new Double(d1 * 7 * 24 * 60 * 60).intValue();
                    listPlan.add(new PlanNotify(titleTask, ruleNextNotify(ts, interval), interval, repeat));

                } else if (typeNotifyInterval == TypeNotifyInterval.MONTH) {
                    String readValue = value.split(typeNotifyInterval.toString().toLowerCase())[0];
                    int d1 = Integer.parseInt(readValue);
                    listPlan.add(new PlanNotify(titleTask, ruleNextNotify(ts, d1 * -1), d1 * -1, repeat));

                } else if (typeNotifyInterval == TypeNotifyInterval.YEAR) {
                    listPlan.add(new PlanNotify(titleTask, ruleNextNotify(ts, -12), -12, repeat));
                }
            }
            Collections.sort(listPlan, new Comparator<PlanNotify>() {
                public int compare(PlanNotify s1, PlanNotify s2) {
                    return (s1.timestamp > s2.timestamp) ? 1 : -1;
                }
            });
        }
        return listPlan;
    }

    private static long ruleNextNotify(long ts, long interval) {
        long now = System.currentTimeMillis() / 1000;
        if (ts > now) {
            return ts;
        } else {
            int count = 0;
            while (ts < now){
                ts = getNextStatic(ts, interval);
                count++;
                if(count > 20){
                    ts = getNextStatic(now + 60, interval); //В дикой ситуации надо действовать дико)))
                    break;
                }
            }
            return ts;
        }
    }

    private static List<PlanNotify> getStandardPlanNotify(long from, long to, String task) {
        List<PlanNotify> list = new ArrayList<>();
        try {
            long diff = to - from;
            if (diff > 0) {
                boolean now = false;
                boolean today = false;
                boolean tomorrow = false;
                boolean nextWeek = false;
                boolean month = false;

                if (diff < 4 * 60 * 60) { //0ч - 3.59ч: в момент исполнения
                    now = true;
                } else if (diff < 2 * 24 * 60 * 60) { //4ч - 2д: за 2 часа
                    today = true;
                } else if (diff < 14 * 24 * 60 * 60) { //2д - 2н день: за сутки, за 2 часа
                    tomorrow = true;
                    today = true;
                } else if (diff < 30 * 24 * 60 * 60) { //2н и больше: за неделю, за сутки, за 2 часа
                    nextWeek = true;
                    tomorrow = true;
                    today = true;
                } else {
                    month = true;
                    nextWeek = true;
                    tomorrow = true;
                    today = true;
                }
                if (now) {
                    list.add(new PlanNotify("Напоминаю. " + task, to));
                }
                if (today) {
                    list.add(new PlanNotify("Напоминаю. Через 2 часа: " + task, to - 2 * 60 * 60));
                }
                if (tomorrow) {
                    list.add(new PlanNotify("Напоминаю. Завтра " + Util.timestampToDate(to, "dd.MM.yyyy HH:mm") + ": " + task, to - 24 * 60 * 60));
                }
                if (nextWeek) {
                    list.add(new PlanNotify("Напоминаю. Через неделю " + Util.timestampToDate(to, "dd.MM.yyyy HH:mm") + ": " + task, to - 7 * 24 * 60 * 60));
                }
                if (month) {
                    list.add(new PlanNotify("Напоминаю. Через месяц " + Util.timestampToDate(to, "dd.MM.yyyy HH:mm") + ": " + task, to - 7 * 24 * 60 * 60));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}