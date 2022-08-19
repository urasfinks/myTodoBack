package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.database.*;
import ru.jamsys.sub.DataState;
import ru.jamsys.sub.Person;
import ru.jamsys.sub.PlanNotify;
import ru.jamsys.util.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class JS {

    public enum TypeNotify {
        STANDARD,
        ONCE,
        CYCLE,
        CUSTOM
    }

    public enum TypeNotifyInterval {
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR
    }


    public static void main(String[] args) throws Exception {
        //String str = "{\"notify\":\"once\",\"name\":\"Fff\",\"deadLineDate\":\"20.08.2022\",\"deadLineTime\":\"09:50\",\"interval\":\"hour\",\"interval_hour\":\"01:00\",\"interval_day\":\"1day\",\"interval_week\":\"1week\",\"interval_month\":\"1month\",\"countRetry\":\"\"}";
        //String str = "{\"notify\":\"custom\",\"name\":\"\",\"deadLineDate\":\"19.08.2022\",\"deadLineTime\":\"11:40\",\"interval\":\"hour\",\"interval_hour\":\"01:00\",\"interval_day\":\"1day\",\"interval_week\":\"1week\",\"interval_month\":\"1month\",\"countRetry\":\"\",\"custom_date\":\"19.08.2022 11:40\\n19.08.2022 11:40\\n19.08.2022 11:42\\n\"}";
        String str = "{\"notify\":\"cycle\",\"name\":\"\",\"deadLineDate\":\"19.08.2022\",\"deadLineTime\":\"14:00\",\"interval\":\"month\",\"interval_hour\":\"01:00\",\"interval_day\":\"1day\",\"interval_week\":\"1week\",\"interval_month\":\"1month\",\"countRetry\":\"\"}";

        System.out.println(PlanNotify.parse(str));
    }

    public static boolean isAuth(RequestContext rc) {
        return rc.isAuth();
    }

    public static void logout(RequestContext rc) {
        PersonUtil.logout(rc);
    }

    public static String getTempKeyPerson(RequestContext rc) throws NoSuchProviderException, UnsupportedEncodingException {
        Person p = PersonUtil.getPerson(rc.idPerson);
        return p != null ? p.tempKeyPerson : null;
    }

    public static boolean isDataShared(RequestContext rc, String dataUID) {
        return DataUtil.isShared(rc, dataUID);
    }

    public static String getPersonInformationWhoChangeDataState(RequestContext rc, String dataUID) throws NoSuchProviderException, UnsupportedEncodingException {
        if (dataUID != null && !"".equals(dataUID)) {
            DataState parentState = DataUtil.getParentState(dataUID);
            if (parentState.state.containsKey("person_" + dataUID)) {
                String sIdPerson = parentState.state.get("person_" + dataUID).toString();
                try {
                    BigDecimal idPerson = new BigDecimal(Double.parseDouble(sIdPerson));
                    return PersonUtil.getPersonInformation(idPerson);
                } catch (Exception e) {
                }
            }
        }
        return "";
    }

    public static String hash(String data, String hashType) throws NoSuchProviderException, UnsupportedEncodingException {
        return Util.getHashCharset(data, hashType, "utf-8");
    }

    public static String sql(String jsonParam) throws Exception {
        return new Gson().toJson(Database.execJson("java:/PostgreDSR", jsonParam));
    }

    public static void comment(RequestContext rc, String text) {
        ChatUtil.add(PersonUtil.systemPerson, rc.idPerson, text);
        BootsTrapListener.sendToTelegramSystem("From idPerson: " + rc.idPerson + "; Message: " + text);
    }

    public static void clearUnreadChatMessage(RequestContext rc) {
        ChatUtil.clearCountUnread(rc);
    }

    public static int getCountUnreadChatMessage(RequestContext rc) {
        return ChatUtil.getCountUnread(rc);
    }

    public static void updateDataState(RequestContext rc, String dataUID, String json) {
        DataUtil.updateState(rc, dataUID, json);
    }

    public static void removeSharedPerson(RequestContext rc, String tempPersonKey, String dataUID) {
        DataUtil.removeSharedPerson(rc, tempPersonKey, dataUID);
    }

    public static void addSharedPerson(RequestContext rc, String tempPersonKey, String dataUID) {
        DataUtil.addSharedPerson(rc, tempPersonKey, dataUID);
    }

    public static String getPersonInfoDataShared(RequestContext rc, String dataUID) {
        return DataUtil.getPersonInfoDataShared(rc, dataUID);
    }

    public static void removeData(RequestContext rc, String dataUID) {
        DataUtil.remove(rc, dataUID);
    }

    public static String getPersonState(RequestContext rc, String def) {
        try {
            String statePerson = PersonUtil.getPersonState(rc);
            return Util.mergeJson(def, statePerson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return def;
    }

    public static String getDataState(ContentOutput content, String dataUID, String def) {
        content.loadState(dataUID);
        if (content.getStateJson() != null) {
            return Util.mergeJson(def, content.getStateJson());
        }
        return def;
    }

    public static String getDataInformation(String dataUID) {
        return DataUtil.getInformation(dataUID);
    }

    public static String getData(RequestContext rc, String dataUID, String def) {
        return DataUtil.get(rc, dataUID, def);
    }

    public static void updatePersonState(RequestContext rc, String json) {
        PersonUtil.updatePersonState(rc, json);
    }

    public static String addData(RequestContext rc, String state, List<String> tags) {
        return DataUtil.add(rc, state, tags);
    }

}
