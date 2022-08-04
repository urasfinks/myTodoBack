package ru.jamsys.util;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.google.gson.Gson;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import ru.jamsys.ContentOutput;
import ru.jamsys.RequestContext;
import ru.jamsys.sub.PlanNotify;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Util {

    public static <T> T[] splice(final T[] array, int start) {
        if (start < 0)
            start += array.length;

        return splice(array, start, array.length - start);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] splice(final T[] array, int start, final int deleteCount) {
        if (start < 0)
            start += array.length;

        final T[] spliced = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount);
        if (start != 0)
            System.arraycopy(array, 0, spliced, 0, start);

        if (start + deleteCount != array.length)
            System.arraycopy(array, start + deleteCount, spliced, start, array.length - start - deleteCount);

        return spliced;
    }

    public static <T> T[] splice(final T[] array, int start, final int deleteCount, final T... items) {
        if (start < 0)
            start += array.length;

        final T[] spliced = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length - deleteCount + items.length);
        if (start != 0)
            System.arraycopy(array, 0, spliced, 0, start);

        if (items.length > 0)
            System.arraycopy(items, 0, spliced, start, items.length);

        if (start + deleteCount != array.length)
            System.arraycopy(array, start + deleteCount, spliced, start + items.length, array.length - start - deleteCount);

        return spliced;
    }

    public static String join(final String[] elements, String delimiter) {
        return String.join(delimiter, elements);
    }

    public static boolean check(String value, String pattern) {
        try {
            return Pattern.matches(pattern, value);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isUUID(String uuid) {
        if (uuid == null) {
            return false;
        }
        String formattedUUID = formatUUID(uuid);
        try {
            return java.util.UUID.fromString(formattedUUID).toString().equals(formattedUUID.toLowerCase());
        } catch (Exception e) {
            // Не требуется
        }
        return false;
    }

    static String formatUUID(String uuid) {
        if (uuid == null) {
            return null;
        }
        if (!uuid.contains("-") && uuid.length() == 32) {
            return uuid.substring(0, 8) + '-' +
                    uuid.substring(8, 12) + '-' +
                    uuid.substring(12, 16) + '-' +
                    uuid.substring(16, 20) + '-' +
                    uuid.substring(20);
        } else {
            return uuid;
        }
    }

    public static String getHashCharset(String txt, String hashType, String charset) throws java.io.UnsupportedEncodingException {
        /* MD2, MD5, SHA1, SHA-256, SHA-384, SHA-512 */
        String ret = null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(hashType);
            byte[] array = md.digest(txt.getBytes(charset));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            ret = e.toString();
        }
        return ret;
    }

    public static String mergeJson(String defJson, String overlayJson) {
        if (defJson == null || "".equals(defJson) || "{}".equals(defJson)) {
            return overlayJson;
        }
        if (overlayJson == null || "".equals(overlayJson) || "{}".equals(overlayJson)) {
            return defJson;
        }
        return new Gson().toJson(mergeJson(new Gson().fromJson(defJson, Map.class), new Gson().fromJson(overlayJson, Map.class)));
    }

    public static Map<String, Object> mergeJson(Map<String, Object> def, Map<String, Object> overlay) {
        for (String key : overlay.keySet()) {
            def.put(key, overlay.get(key));
        }
        return def;
    }

    public static Object selector(Map map, String query, Object def) {
        Object ret = map;
        String[] keys = query.split("\\.");
        for (String key : keys) {
            if (ret instanceof Map) {
                if (((Map) ret).containsKey(key)) {
                    ret = ((Map) ret).get(key);
                } else {
                    return def;
                }
            } else {
                return def;
            }
        }
        return ret;
    }

    public static String doubleRemoveExponent(Double dig) {
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(0);
        return df.format(dig);
    }

    public static String template(String template, Map<String, String> prepare) {
        String[] exp = template.split("\\$\\{");
        for (String exp_item : exp) {
            if (!exp_item.contains("}"))
                continue;
            String[] exp2 = exp_item.split("}");
            if (exp2.length <= 0)
                continue;
            String name = exp2[0];
            if (!prepare.containsKey(name))
                continue;
            template = template.replace("${" + name + "}", prepare.get(name));
        }
        return template;
    }

    public static String timestampToDate(long timestamp, String format) {
        Timestamp stamp = new Timestamp(timestamp * 1000);
        Date date = new Date(stamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);

    }

    public static long dateToTimestamp(String data, String format) throws Exception {
        //03.08.2022 14:20 dd.MM.yyyy hh:mm
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date parsedDate = dateFormat.parse(data);
        Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());
        return ts.getTime() / 1000;
    }



    public static List<PlanNotify> getPlanNotify(long from, long to, String task) {
        List<PlanNotify> list = new ArrayList<>();
        try {
            long diff = to - from;
            if (diff > 0) {
                boolean now = false;
                boolean today = false;
                boolean tomorrow = false;
                boolean nextWeek = false;

                if (diff < 4 * 60 * 60) { //0ч - 3.59ч: в момент исполнения
                    now = true;
                } else if (diff < 2 * 24 * 60 * 60) { //4ч - 2д: за 2 часа
                    today = true;
                } else if (diff < 14 * 24 * 60 * 60) { //2д - 2н день: за сутки, за 2 часа
                    tomorrow = true;
                    today = true;
                } else { //2н и больше: за неделю, за сутки, за 2 часа
                    nextWeek = true;
                    tomorrow = true;
                    today = true;
                }
                if (now) {
                    list.add(new PlanNotify("Напоминаю. Закончилось время: " + task, to));
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String getComplexDateTime(String date, String time) {
        StringBuilder sb = new StringBuilder();
        if (date != null) {
            sb.append(date);
        }
        if (time != null) {
            sb.append(" " + time);
        }
        return sb.toString();
    }

    public static String runJS(String javaScriptCode, String state, RequestContext rc) throws Exception {

        class MyCF implements ClassFilter {
            @Override
            public boolean exposeToScripts(String className) {
                return className.compareTo("ru.jamsys.JS") == 0 || className.compareTo("ru.jamsys.ContentOutput") == 0;
            }
        }
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new MyCF());
        engine.eval(new StringReader(javaScriptCode));
        Invocable invocable = (Invocable) engine;
        ContentOutput response = new ContentOutput();
        invocable.invokeFunction("main", state, rc, response);
        return response.getResponse(rc.idParent);
    }

    //System.out.println(template("{\"subject\": \"World\", \"test\":{\"x\":\"y\", \"x2\":\"y\"}}", "Hello {{subject}}! {{#each test}} {{@key}} = {{this}} {{/each}}"));
    public static String template2(String json, String handlebarsTemplate) {
        try {
            Map<String, Object> map = new Gson().fromJson(json, Map.class);
            Template template = new Handlebars().compileInline(handlebarsTemplate);
            Context context = Context.newBuilder(map).build();
            return template.apply(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
