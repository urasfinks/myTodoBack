package ru.jamsys;

import ru.jamsys.sub.Person;
import ru.jamsys.util.PersonUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    public BigDecimal idProject = null;
    public BigDecimal idPerson = null;

    public BigDecimal getIdChatTelegram(String secret) {
        if(System.getProperty("SECRET").equals(secret)){
            return idChatTelegram;
        }
        return null;
    }

    private BigDecimal idChatTelegram = null;
    public BigDecimal idParent = null;
    public String tempKeyPerson = null;
    public String projectUrl = null;
    public String projectName = null;
    public String url = null;
    public Map<String, String> getParam = new HashMap<>();

    public void setIdChatTelegram(BigDecimal idChatTelegram) {
        this.idChatTelegram = idChatTelegram;
    }

    public boolean isAuth(){
        return idChatTelegram != null;
    }

    private void upd(Person person){
        if(person != null){
            idPerson = person.idPerson;
            idChatTelegram = person.idChatTelegram;
            tempKeyPerson = person.tempKeyPerson;
            idParent = person.idParent;
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