package ru.jamsys.sub;

import java.math.BigDecimal;

public class Person {
    public BigDecimal idPerson;
    public BigDecimal idChatTelegram;
    public BigDecimal idParent;
    public String tempKeyPerson;

    public Person(BigDecimal idPerson, BigDecimal idChatTelegram, String tempPersonKey, BigDecimal idParent) {
        this.idPerson = idPerson;
        this.idChatTelegram = idChatTelegram;
        this.tempKeyPerson = tempPersonKey;
        this.idParent = idParent;
    }

    @Override
    public String toString() {
        return "Person{" +
                "idPerson=" + idPerson +
                ", idChatTelegram=" + idChatTelegram +
                ", tempKeyPerson='" + tempKeyPerson + '\'' +
                '}';
    }
}
