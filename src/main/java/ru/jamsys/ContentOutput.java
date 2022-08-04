package ru.jamsys;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.*;

public class ContentOutput {

    private String patternCheck = "^[a-zA-Z0-9-]+$";
    public boolean syncSocket = false;
    private DataUtil.State st = null;

    public String getStateJson() {
        return st != null ? st.stateJson : null;
    }

    public long getRevisionState() {
        return st != null ? st.revisionState : null;
    }

    public Map getState() {
        return st != null ? st.state : null;
    }

    public Map<String, Object> widgetData = new HashMap<>();
    public Map<String, String> mapTemplate = new HashMap();
    public List<DataTemplate> listData = new ArrayList<>();
    public List<DataTemplate> listAppBarActions = new ArrayList<>();
    public List<Map> listAction = new ArrayList<>();
    public boolean separated = false;

    public void setSeparated(boolean separated) {
        this.separated = separated;
    }

    public String getMethod(String method, Map argObj) {
        Map<String, Object> arg = new HashMap<>();
        if (argObj != null) {
            arg.putAll(argObj);
        }
        return ":" + method + "(" + String.join(",", arg.keySet().toArray(new String[0])) + ")";
    }

    public String getMethodResult(String method, Map argObj) {
        Map<String, Object> arg = new HashMap<>();
        if (argObj != null) {
            arg.putAll(argObj);
        }
        return "=>" + method + "(" + String.join(",", arg.keySet().toArray(new String[0])) + ")";
    }

    public void addAction(String method, Map<String, Object> argString) {
        Map<String, Object> act = new HashMap<>();
        act.putAll(argString);
        act.put("method", "=>" + method + "(" + String.join(",", argString.keySet().toArray(new String[0])) + ")");
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

    private boolean isLoadState = false;

    public void loadState(String key) {
        if (!isLoadState) {
            isLoadState = true;
            st = DataUtil.getState(key);
        }
    }

    public void addSyncSocketDataUID(String key) {
        syncSocket = true;
        loadState(key);
    }

    public class DataTemplate {
        public Object data;
        public String template;

        public DataTemplate(String data, String template) {
            this.data = new Gson().fromJson(data, Map.class);
            this.template = template;
        }

        public DataTemplate(Map<String, Object> data, String template) {
            this.data = data;
            this.template = template;
        }
    }

    public void setWidgetData(String key, Object value) {
        this.widgetData.put(key, value);
    }

    public void addTemplate(String template) {
        if (!mapTemplate.containsKey(template)) {
            mapTemplate.put(template, null);
        }
    }

    public void addData(Map data, String template, Map<String, String> nativeData) { //Native replace on Server
        String nameTemplate = "C_" + java.util.UUID.randomUUID().toString();
        String compileTemplate = Util.template(TemplateUtil.get(template), nativeData);
        mapTemplate.put(nameTemplate, compileTemplate);
        addData(data, nameTemplate);
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
            if (Util.check(key, patternCheck) && mapTemplate.get(key) == null) {
                l.add(key);
            }
        }
        TemplateUtil.fillTemplate(l, mapTemplate);
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
        ret.put("State", getState());
        ret.put("RevisionState", getRevisionState());
        ret.put("Actions", listAction);
        if (parentPersonKey != null) {
            ret.put("ParentPersonKey", PersonUtil.getPersonKey(parentPersonKey));
        }
        return new Gson().toJson(ret);
    }

    public static ContentOutput newInstance() {
        return new ContentOutput();
    }

}
