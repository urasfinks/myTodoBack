package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.math.BigDecimal;
import java.util.*;

public class ContentOutput {

    private String patternCheck = "^[a-zA-Z0-9-]+$";

    public boolean syncSocket = false;
    public Map<String, Object> state = new HashMap<>();
    public long revisionState;
    public Map<String, Object> widgetData = new HashMap<>();
    public Map<String, String> mapTemplate = new HashMap();
    public List<DataTemplate> listData = new ArrayList<>();
    public List<DataTemplate> listAppBarActions = new ArrayList<>();
    public List<Map> listAction = new ArrayList<>();
    public boolean separated = false;

    public void setSeparated(boolean separated) {
        this.separated = separated;
    }

    public String getMethod(String method, Map argObj){
        Map<String, Object> arg = new HashMap<>();
        if(argObj != null){
            arg.putAll(argObj);
        }
        return ":"+method+"(" + String.join(",", arg.keySet().toArray(new String[0])) + ")";
    }

    public String getMethodResult(String method, Map argObj){
        Map<String, Object> arg = new HashMap<>();
        if(argObj != null){
            arg.putAll(argObj);
        }
        return "=>"+method+"(" + String.join(",", arg.keySet().toArray(new String[0])) + ")";
    }

    public void addAction(String method, Map<String, Object> argString) {
        Map<String, Object> act = new HashMap<>();
        act.putAll(argString);
        act.put("method", "=>"+method+"(" + String.join(",", argString.keySet().toArray(new String[0])) + ")");
        listAction.add(act);
    }

    public void setParentUI(String parentUI) {
        if (parentUI != null && !"".equals(parentUI) && Util.check(parentUI, patternCheck)) {
            mapTemplate.put(parentUI, null);
            setWidgetData("wrapPage", parentUI);
        }
    }

    public void addAppBarAction(Map data, String template) {
        listAppBarActions.add(new DataTemplate(data, template));
        if (!mapTemplate.containsKey(template)) {
            mapTemplate.put(template, null);
        }
    }

    public void addSyncSocketDataUID(String key) {
        syncSocket = true;
        try {
            Database database = new Database();
            database.addArgument("uid_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, key);
            database.addArgument("state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("revision_state_data", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select state_data, revision_state_data from data where uid_data = ${uid_data}");
            String stateData = (String) database.checkFirstRowField(exec, "state_data");
            if (stateData != null && !"".equals(stateData)) {
                state = new Gson().fromJson(stateData, Map.class);
            }
            String revisionStateData = (String) database.checkFirstRowField(exec, "revision_state_data");
            if (revisionStateData != null && !"".equals(revisionStateData)) {
                try {
                    revisionState = Long.parseLong(revisionStateData);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DataTemplate {
        public Object data;
        public String template;

        public DataTemplate(String data, String template) {
            this.data = new Gson().fromJson(data, Map.class);
            this.template = template;
        }
        public DataTemplate(Map<String, Object> data, String template) {
            //this.data = new Gson().fromJson(data, Map.class);
            this.data = data;
            this.template = template;
        }
    }

    public void setWidgetData(String key, Object value) {
        this.widgetData.put(key, value);
    }

    public void addData(Map data, String template) {
        listData.add(new DataTemplate(data, template));
        if (!mapTemplate.containsKey(template)) {
            mapTemplate.put(template, null);
        }
    }

    public void addData(String data, String template) {
        listData.add(new DataTemplate(data, template));
        if (!mapTemplate.containsKey(template)) {
            mapTemplate.put(template, null);
        }
    }

    private void fillTemplate() {
        List<String> l = new ArrayList<>();
        for (String key : mapTemplate.keySet()) {
            if (Util.check(key, patternCheck)) {
                l.add(key);
            }
        }
        if (l.size() > 0) {
            String in = "'" + Util.join(l.toArray(new String[0]), "', '") + "'";
            try {
                Database database = new Database();
                database.addArgument("flutter_ui", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                database.addArgument("key_ui", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select flutter_ui, key_ui from ui where key_ui in (" + in + ")");
                if (exec.size() > 0) {
                    for (Map<String, Object> item : exec) {
                        if (item.containsKey("key_ui") && item.containsKey("flutter_ui") && mapTemplate.containsKey(item.get("key_ui"))) {
                            mapTemplate.put((String) item.get("key_ui"), (String) item.get("flutter_ui"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getResponse(BigDecimal parentPersonKey) {
        Map<String, Object> ret = new HashMap<>();
        widgetData.put("separated", separated);
        if (!widgetData.isEmpty()) {
            ret.put("WidgetData", widgetData);
        }
        ret.put("AppBarActions", listAppBarActions);
        ret.put("Data", listData);
        if (listData.size() > 0 || listAppBarActions.size() > 0) {
            fillTemplate();
        }
        ret.put("Template", mapTemplate);
        ret.put("SyncSocket", syncSocket);
        ret.put("State", state);
        ret.put("RevisionState", revisionState);
        ret.put("Actions", listAction);
        if(parentPersonKey != null){
            ret.put("ParentPersonKey", PersonUtil.getPersonKey(parentPersonKey));
        }
        return new Gson().toJson(ret);
    }

    public static ContentOutput newInstance() {
        return new ContentOutput();
    }

}
