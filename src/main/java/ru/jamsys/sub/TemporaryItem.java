package ru.jamsys.sub;

public class TemporaryItem {
    private String idPerson;
    private long timeout = System.currentTimeMillis() + (15 * 60 * 1000);

    public TemporaryItem(String idPerson) {
        this.idPerson = idPerson;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > timeout;
    }

    public String getIdPerson(){
        return idPerson;
    }
}
