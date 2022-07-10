package ru.jamsys;

import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestContext {
    public BigDecimal idProject = null;
    public BigDecimal idPerson = null;
    public String projectUrl = null;
    public String projectName = null;
    public String url = null;
    public Map<String, String> getParam = new HashMap<>();


    public boolean initPerson(String personKey){
        if(personKey != null && !"".equals(personKey)){
            try {
                Database database = new Database();
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select * from person where key_person = ${key_person}");
                BigDecimal idp = (BigDecimal) database.checkFirstRowField(exec, "id_person");
                if (idp != null) {
                    idPerson = idp;
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "idProject=" + idProject +
                ", idPerson=" + idPerson +
                ", projectUrl='" + projectUrl + '\'' +
                ", projectName='" + projectName + '\'' +
                ", url='" + url + '\'' +
                ", getParam=" + getParam +
                '}';
    }
}
