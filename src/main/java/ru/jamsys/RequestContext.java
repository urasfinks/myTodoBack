package ru.jamsys;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    public BigDecimal idProject = null;
    public BigDecimal idPerson = null;
    public BigDecimal idChatTelegram = null;
    public String projectUrl = null;
    public String projectName = null;
    public String url = null;
    public Map<String, String> getParam = new HashMap<>();

    private void upd(Person person){
        if(person != null){
            idPerson = person.idPerson;
            idChatTelegram = person.idChatTelegram;
        }
    }


    public boolean init(String personKey) {
        if (idPerson == null) {
            upd(PersonUtil.getPerson(personKey));
        }
        return idPerson != null ? true : false;
    }

    public boolean init(BigDecimal personKey) {
        if (idPerson == null) {
            upd(PersonUtil.getPerson(personKey));
        }
        return idPerson != null ? true : false;
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