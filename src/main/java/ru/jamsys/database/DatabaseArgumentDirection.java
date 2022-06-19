package ru.jamsys.database;

public enum DatabaseArgumentDirection {
    
    IN,
    OUT,
    IN_OUT,
    COLUMN;

    public boolean isOutMode() {
        return (this == OUT) || (this == IN_OUT);
    }
    
}
