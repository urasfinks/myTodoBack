package ru.jamsys.database;

public interface Session {
    
    boolean isExpired();
    
    String getSessionInformation();
    
    long getDateAdd();
    
    void close() throws Exception;
    
    boolean isReady(Object[] condition);
    
    void setReadyAccess();
    
    boolean getReadyAccess();

    void free();
    
}
