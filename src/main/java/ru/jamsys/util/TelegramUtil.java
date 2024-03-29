package ru.jamsys.util;

import ru.jamsys.RequestContext;
import ru.jamsys.sub.TelegramResponse;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TelegramUtil {

    public static void asyncSend(BigDecimal idPersonTo, BigDecimal idPersonFrom, String data, long timestamp, BigDecimal idData, long interval, int count) {
        try {
            Database database = new Database();
            database.addArgument("id_person_to", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPersonTo);
            database.addArgument("data_notify", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, data);
            database.addArgument("timestamp_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, timestamp);
            database.addArgument("id_person_from", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idPersonFrom);
            database.addArgument("id_data", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, idData);
            database.addArgument("interval", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, new BigDecimal(interval));
            database.addArgument("count", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, new BigDecimal(count));
            database.exec("java:/PostgreDS", "insert into notify (id_person_to, data_notify, timestamp_notify, id_person_from, id_data, interval_notify, count_notify) values (${id_person_to}, ${data_notify}, to_timestamp(${timestamp_notify}), ${id_person_from}, ${id_data}, ${interval}, ${count})");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncSend(RequestContext rc, String data) {
        BigDecimal idChatTelegram = SystemUtil.getIdChatTelegram(rc);
        syncSend(idChatTelegram.toString(), data).checkSuccess(rc.idPerson);
    }

    public static TelegramResponse syncSend(String idChat, String data) {
        TelegramResponse tgResp = new TelegramResponse();
        if (idChat == null) {
            tgResp.setResponse("{\"status\": \"idChatTelegram is null\"}");
            return tgResp;
        }
        try {
            String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

            String apiToken = SystemUtil.getTelegramBotToken();
            if (apiToken != null) {
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
            } else {
                tgResp.setResponse("{\"status\": \"Telegram bot token is null\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tgResp;
    }

}
