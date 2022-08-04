package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.database.*;
import ru.jamsys.sub.Person;
import ru.jamsys.util.DataUtil;
import ru.jamsys.util.PersonUtil;
import ru.jamsys.util.Util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchProviderException;
import java.util.List;

public class JS {

    public static void main(String[] args) throws Exception {

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

    public static String hash(String data, String hashType) throws NoSuchProviderException, UnsupportedEncodingException {
        return Util.getHashCharset(data, hashType, "utf-8");
    }

    public static String sql(String jsonParam) throws Exception {
        return new Gson().toJson(Database.execJson("java:/PostgreDSR", jsonParam));
    }

    public static void updateDataState(RequestContext rc, String dataUID, String json) {
        DataUtil.updateState(rc, dataUID, json);
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
