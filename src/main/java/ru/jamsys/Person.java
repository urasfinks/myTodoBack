package ru.jamsys;

import java.math.BigDecimal;

public class Person {
    public BigDecimal idPerson;
    public BigDecimal idChatTelegram;

    public Person(BigDecimal idPerson, BigDecimal idChatTelegram) {
        this.idPerson = idPerson;
        this.idChatTelegram = idChatTelegram;
    }

    @Override
    public String toString() {
        return "Person{" +
                "idPerson=" + idPerson +
                ", idChatTelegram=" + idChatTelegram +
                '}';
    }

}
