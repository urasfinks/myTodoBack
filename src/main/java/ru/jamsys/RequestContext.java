package ru.jamsys;

import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class RequestContext {
    public BigDecimal idProject = null;
    public BigDecimal idPerson = null;
    public String projectUrl = null;
    public String projectName = null;
    public String url = null;

    public boolean initPerson(String personKey){
        if(personKey != null && !"".equals(personKey) && personKey.startsWith("Basic ")){

            String[] x = personKey.split("Basic ");
            if(x.length == 2){
                byte[] decoded = Base64.getDecoder().decode(x[1]);
                String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                if(decodedStr.startsWith("PersonKey:")){
                    String[] x2 = decodedStr.split("PersonKey:");
                    if(x2.length == 2){
                        try {
                            Database database = new Database();
                            database.addArgument("key_person", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, x2[1]);
                            database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select * from person where key_person = ${key_person}");
                            if (exec.size() > 0 && exec.get(0).get("id_person") != null) {
                                idPerson = (BigDecimal) exec.get(0).get("id_person");
                                return true;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
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
                '}';
    }
}
