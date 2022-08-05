package ru.jamsys.database;

import com.google.gson.Gson;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.jamsys.database.DatabaseArgumentDirection.COLUMN;

public class Database {

    private final Map<String, DatabaseScriptArgument> prepare = new HashMap<>();

    public void addArgument(String name, DatabaseArgumentType type, DatabaseArgumentDirection direction, Object value) {
        prepare.put(name, new DatabaseScriptArgumentImpl(name, type, direction, value, false));
    }

    private String sql(String sql) {
        String[] exp = sql.split("\\$\\{");
        int idx = 1;
        for (String exp_item : exp) {
            if (!exp_item.contains("}"))
                continue;
            String[] exp2 = exp_item.split("}");
            if (exp2.length <= 0)
                continue;
            String name = exp2[0];
            if (!prepare.containsKey(name))
                continue;
            DatabaseScriptArgument p = prepare.get(name);
            p.registerIndex(idx++);
            sql = sql.replace("${" + name + "}", "?");
            if (p.getDirection().isOutMode() && (p.getIndexes().size() > 1))
                throw new IllegalStateException("Параметры с режимами OUT и IN_OUT не могут встречаться более 1 раза в одном запросе.");
        }
        return sql;
    }

    private void fill(Connection conn, PreparedStatement cs, DatabaseSession session, Map<String, DatabaseScriptArgument> parameters) throws Exception {
        for (String p : parameters.keySet()) {
            DatabaseScriptArgument param = parameters.get(p);
            for (int index : param.getIndexes()) {
                switch (param.getDirection()) {
                    case IN:
                        session.setInParam(conn, cs, param.getType(), index, param.getValue());
                        break;
                    case OUT:
                        session.setOutParam((CallableStatement) cs, param.getType(), index);
                        break;
                    case IN_OUT:
                        session.setOutParam((CallableStatement) cs, param.getType(), index);
                        session.setInParam(conn, cs, param.getType(), index, param.getValue());
                        break;
                }
            }
        }
    }

    public static List<Map<String, Object>> execJson(String jndiName, String jsonParam) throws Exception {
        Database database = new Database();
        Map map = new Gson().fromJson(jsonParam, Map.class);
        if (map.containsKey("args")) {
            List<Map<String, Object>> l = (List<Map<String, Object>>) map.get("args");
            for (Map<String, Object> x : l) {
                database.addArgument(
                        (String) x.get("field"),
                        DatabaseArgumentType.valueOf((String) x.get("type")),
                        DatabaseArgumentDirection.valueOf((String) x.get("direction")),
                        x.getOrDefault("value", null)
                );
            }
        }
        return database.exec(jndiName, (String) map.get("sql"));
    }

    public static Object checkFirstRowField(List<Map<String, Object>> exec, String field)  {
        if (exec.size() > 0 && exec.get(0).get(field) != null) {
            return exec.get(0).get(field);
        }
        return null;
    }
    public List<Map<String, Object>> exec(String jndiName, String sql) throws Exception {
        DatabaseSessionPostgreSQL databaseSessionPostgreSQL = null;
        try {
            DataSource source = (DataSource) new InitialContext().lookup(jndiName);
            Connection conn = source.getConnection();
            databaseSessionPostgreSQL = new DatabaseSessionPostgreSQL(conn, 10000L, 300000L);

            String sqlExpression = sql(sql);
            PreparedStatement cs = prepare(conn, sqlExpression, true);
            fill(conn, cs, databaseSessionPostgreSQL, prepare);
            cs.execute();
            return process(cs, databaseSessionPostgreSQL, prepare);
        } catch (Exception e) {
            throw e;
        } finally {
            if (databaseSessionPostgreSQL != null) {
                databaseSessionPostgreSQL.close();
            }
        }
    }

    private PreparedStatement prepare(Connection conn, String sql, boolean selectable) throws SQLException {
        return selectable ? conn.prepareStatement(sql) : conn.prepareCall(sql);
    }

    private List<Map<String, Object>> process(PreparedStatement cs, DatabaseSession session, Map<String, DatabaseScriptArgument> prepare) throws Exception {
        List<Map<String, Object>> listRet = new ArrayList<>();

        try (ResultSet rs = cs.getResultSet()) {
            Map<String, Object> row = new HashMap<>();
            if (rs == null) {
                return listRet;
            }
            while (rs.next()) {
                for (Map.Entry<String, DatabaseScriptArgument> param : prepare.entrySet()) {
                    if (param.getValue().getDirection() == COLUMN)
                        row.put(param.getKey(), session.getColumn(rs, param.getValue().getType(), param.getKey()));
                }
                listRet.add(row);
                row = new HashMap<>();
            }
        }
        return listRet;
    }

}
