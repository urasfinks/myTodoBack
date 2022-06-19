package ru.jamsys.database;

import java.sql.*;
import java.util.Map;

public class DatabaseSessionPostgreSQL extends AbstractSession {
	
	public DatabaseSessionPostgreSQL(Connection conn, Long expiration, long inactivityTimeout) {
		super(conn, expiration, inactivityTimeout);
	}
	
	@Override
	public void setInClob(Connection connection, PreparedStatement cs, int index, String value) throws SQLException {
		cs.setString(index, value);
	}
	
	@Override
	public void setOutClob(CallableStatement cs, int index) throws SQLException {
		cs.registerOutParameter(index, Types.VARCHAR);
	}
	
	@Override
	public void setOutAssocRecordXml(CallableStatement cs, int index) throws SQLException {
		cs.registerOutParameter(index, Types.VARCHAR);
	}
	
	@Override
	public Object getOutClob(CallableStatement cs, int index) throws SQLException {
		return cs.getString(index);
	}

	@Override
	public Map<String, Object> getOutAssocRecordXml(CallableStatement cs, int index){
		return null;
	}


	@Override
	public Object getColumnClob(ResultSet cs, String name) throws SQLException {
		return cs.getString(name);
	}

	@Override
	public Map<String, Object> getColumnAssocRecordXml(ResultSet rs, String name) {
		return null;
	}


	@Override
	public void checkException(Exception e) {
		//PostgreSQL не обладает пулом коннектов, с учётом обилия различных exception, которые говорят о том, что коннекта нет, при любом exception помечаем сессию как протухшую
		//KeepAlive сервиса оценит кол-во активных сессии и если их не будет, то самостоятельно перезагрузит ресурс
		expiration = -1L;
	}
	
}