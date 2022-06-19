package ru.jamsys.database;

import java.sql.Connection;

public interface DatabaseSession extends GenericParameters {

    Connection getConnection();

    void checkException(Exception e);
    
}
