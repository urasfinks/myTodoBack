package ru.jamsys;

import com.google.gson.Gson;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.util.*;

public class ContentOutput {

    public boolean syncSocket = false;
    public Map<String, Object> state = new HashMap<>();
    public long revisionState;
    public String title = null;
    public Map<String, String> mapTemplate = new HashMap();
    public List<DataTemplate> listData = new ArrayList<>();
    public String parentUI = null;

    public void setSeparated(boolean separated) {
        this.separated = separated;
    }

    public boolean separated = true;

    public void setParentUI(String parentUI) {
        if (parentUI != null && !"".equals(parentUI) && Util.check(parentUI, "^[a-zА-Я0-9-]+$")) {
            mapTemplate.put(parentUI, null);
            this.parentUI = parentUI;
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
            if (exec.size() > 0 && exec.get(0).get("state_data") != null) {
                String stateData = (String) exec.get(0).get("state_data");
                if (!"".equals(stateData)) {
                    state = new Gson().fromJson(stateData, Map.class);
                }
                String revisionStateData = (String) exec.get(0).get("revision_state_data");
                if (!"".equals(revisionStateData)) {
                    try{
                        revisionState = Long.parseLong(revisionStateData);
                    }catch (Exception e2){
                        e2.printStackTrace();
                    }
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
    }

    public void setTitle(String title) {
        this.title = title;
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
            if (Util.check(key, "^[a-zА-Я0-9-]+$")) {
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

    @Override
    public String toString() {
        Map<String, Object> ret = new HashMap<>();
        if (title != null) {
            ret.put("Title", title);
        }
        ret.put("Data", listData);
        if (listData.size() > 0) {
            fillTemplate();
        }
        ret.put("Template", mapTemplate);
        ret.put("SyncSocket", syncSocket);
        ret.put("State", state);
        ret.put("RevisionState", revisionState);
        ret.put("ParentUI", parentUI);
        ret.put("Separated", separated);
        return new Gson().toJson(ret);
    }

    public static ContentOutput newInstance() {
        return new ContentOutput();
    }

}
