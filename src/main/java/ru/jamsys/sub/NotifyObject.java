package ru.jamsys.sub;

import java.math.BigDecimal;

public class NotifyObject {

    public BigDecimal idPerson;
    public BigDecimal id;
    public String data;
    public BigDecimal idChatTelegram;

    public NotifyObject(BigDecimal idPerson, BigDecimal id, String data, BigDecimal idChatTelegram) throws Exception {
        if ("".equals(data.trim())) {
            throw new Exception("data is empty");
        }
        this.idPerson = idPerson;
        this.id = id;
        this.data = data;
        this.idChatTelegram = idChatTelegram;
    }
}
