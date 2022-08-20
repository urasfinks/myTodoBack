package ru.jamsys.sub;

import java.math.BigDecimal;

public class NotifyObject {

    public BigDecimal idPerson;
    public BigDecimal id;
    public String data;
    public BigDecimal idChatTelegram;
    public BigDecimal idData;
    public long timestamp;
    public BigDecimal interval;
    public BigDecimal count;

    public NotifyObject(BigDecimal idPerson, BigDecimal id, String data, BigDecimal idChatTelegram, BigDecimal idData, BigDecimal interval, BigDecimal count, long timestamp) throws Exception {
        if ("".equals(data.trim())) {
            throw new Exception("data is empty");
        }
        this.idPerson = idPerson;
        this.id = id;
        this.data = data;
        this.idChatTelegram = idChatTelegram;
        this.idData = idData;
        this.interval = interval;
        this.count = count;
        this.timestamp = timestamp;
    }
}
