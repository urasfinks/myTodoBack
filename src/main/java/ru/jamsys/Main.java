package ru.jamsys;

import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "main", value = "")
public class Main extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            Database database = new Database();
            database.addArgument("key_group", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> ret = database.exec("java:/PostgreDS", "SELECT * FROM \"group\"");
            System.out.println(ret);
        }catch (Exception e){
            e.printStackTrace();
        }

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Test server</h1>");
        out.println("</body></html>");
    }

}
