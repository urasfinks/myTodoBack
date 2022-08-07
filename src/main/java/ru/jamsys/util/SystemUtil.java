package ru.jamsys.util;

import ru.jamsys.RequestContext;

import java.math.BigDecimal;

public class SystemUtil {

    public static BigDecimal getIdChatTelegram(RequestContext rc) {
        return rc.getIdChatTelegram(System.getProperty("SECRET"));
    }

    public static boolean checkSecret(String secret) {
        String secretProp = System.getProperty("SECRET");
        return secretProp != null && secretProp.equals(secret);
    }

    public static boolean checkTelegramToken(String token) {
        return System.getProperty("TELEGRAM_TOKEN") != null && System.getProperty("TELEGRAM_TOKEN").equals(token);
    }

    public static String getTelegramBotToken() {
        return System.getProperty("TELEGRAM_BOT");
    }

    public static boolean isOnlyNativeNotify() {
        return System.getProperty("ONLY_NATIVE_NOTIFY") != null && System.getProperty("ONLY_NATIVE_NOTIFY").equals("true");
    }

}
