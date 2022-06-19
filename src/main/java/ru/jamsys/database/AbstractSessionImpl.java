package ru.jamsys.database;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSessionImpl implements Session {

    public final AtomicBoolean busy = new AtomicBoolean(false);
    protected Long expiration;
    protected long inactivityTimeout;
    protected long lastTimeUse = System.currentTimeMillis();
    protected final long dateAdd = System.currentTimeMillis();

    public AbstractSessionImpl(Long expiration, long inactivityTimeout){
        this.expiration = expiration;
        this.inactivityTimeout = inactivityTimeout;
    }

    @Override
    public void free() {
        // Не требуется
    }

    @Override
    public void setReadyAccess() {
        busy.set(false);
    }

    @Override
    public long getDateAdd() {
        return dateAdd;
    }

    public boolean isExpired(){
        long now = System.currentTimeMillis();
        return (this.expiration != 0 && now > this.expiration) || (now > lastTimeUse + inactivityTimeout);
    }

    @Override
    public boolean getReadyAccess() {
        return busy.compareAndSet(false, true);
    }

}
