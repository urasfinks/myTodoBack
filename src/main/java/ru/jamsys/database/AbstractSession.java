package ru.jamsys.database;

import java.sql.Connection;

public abstract class AbstractSession extends AbstractSessionImpl implements DatabaseSession {

	protected Connection connection;

	public AbstractSession(Connection connection, Long expiration, long inactivityTimeout) {
		super(expiration, inactivityTimeout);
		this.connection = connection;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public String getSessionInformation() {
		return null;
	}

	@Override
	public boolean isReady(Object[] condition) {
		return !isExpired() && busy.compareAndSet(false, true);
	}

	@Override
	public void close(){
		try {
			if(connection != null){
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
