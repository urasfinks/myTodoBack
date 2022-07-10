package ru.jamsys;

import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class RequestContext {
    public BigDecimal selectedProject = null;
    public BigDecimal idPerson = null;

    public void initPerson(String personKey){
        if(personKey != null && !"".equals(personKey)){
            try {
                Database database = new Database();
                database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, personKey);
                database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select * from person where key_person = ${key_person}");
                if (exec.size() > 0 && exec.get(0).get("id_person") != null) {
                    idPerson = (BigDecimal) exec.get(0).get("id_person");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "selectedProject=" + selectedProject +
                ", idPerson=" + idPerson +
                '}';
    }
}
