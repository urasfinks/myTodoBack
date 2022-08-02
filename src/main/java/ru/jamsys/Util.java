package ru.jamsys;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
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

    @SuppressWarnings("unchecked")
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

    public static String getHashCharset(String txt, String hashType, String charset) throws java.security.NoSuchProviderException, java.io.UnsupportedEncodingException {
        /* MD2, MD5, SHA1, SHA-256, SHA-384, SHA-512 */
        String ret = null;
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance(hashType);
            byte[] array = md.digest(txt.getBytes(charset));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
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

    public static TelegramResponse sendTelegram(String idChat, String data) {
        TelegramResponse tgResp = new TelegramResponse();
        try {
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

            String apiToken = System.getProperty("TELEGRAM_BOT");

            urlString = String.format(urlString, apiToken, idChat, URLEncoder.encode(data, StandardCharsets.UTF_8.toString()));

            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            StringBuilder sb = new StringBuilder();
            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            tgResp.setResponse(sb.toString());
        } catch (Exception e) {
        }
        return tgResp;
    }

    static String template(String template, Map prepare) {
        String[] exp = template.split("\\$\\{");
        int idx = 1;
        for (String exp_item : exp) {
            if (!exp_item.contains("}"))
                continue;
            String[] exp2 = exp_item.split("}");
            if (exp2.length <= 0)
                continue;
            String name = exp2[0];
            if (!prepare.containsKey(name))
                continue;
            template = template.replace("${" + name + "}", (String) prepare.get(name));
        }
        return template;
    }

}
