package ru.jamsys;

import ru.jamsys.sub.Person;
import ru.jamsys.util.PersonUtil;
import ru.jamsys.util.SystemUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    public BigDecimal idProject = null;
    public BigDecimal idPerson = null;
    public String host;

    public String platformName = "undefined";

    public void setPlatformName(String name) {
        this.platformName = name;
    }

    public String getPlatform() {
        return this.platformName;
    }

    public BigDecimal getIdChatTelegram(String secret) {
        return SystemUtil.checkSecret(secret) ? idChatTelegram : null;
    }

    private BigDecimal idChatTelegram = null;
    public BigDecimal idParent = null;
    public String tempKeyPerson = null;
    public String projectUrl = null;
    public String projectName = null;
    public String url = null;
    public Map<String, String> getParam = new HashMap<>();
    public int version = 0;

    public void setIdChatTelegram(BigDecimal idChatTelegram) {
        this.idChatTelegram = idChatTelegram;
    }

    public boolean isAuth() {
        return idChatTelegram != null;
    }

    private void upd(Person person) {
        if (person != null) {
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
        return idPerson != null;
    }

    public boolean init(BigDecimal personKey) {
        if (idPerson == null) {
            upd(PersonUtil.getPerson(personKey));
        }
        return idPerson != null;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "idProject=" + idProject +
                ", idPerson=" + idPerson +
                ", projectUrl='" + projectUrl + '\'' +
                ", projectName='" + projectName + '\'' +
                ", url='" + url + '\'' +
                ", host='" + host + '\'' +
                ", getParam=" + getParam +
                ", version=" + version +
                '}';
    }
}