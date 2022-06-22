package ru.jamsys.servlet;

import ru.jamsys.JS;
import ru.jamsys.Util;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet(name = "Project", urlPatterns = "/project/*")
public class Project extends AbstractHttpServletReader {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String[] req = parseFullUrl(request);
        String projectName = "";
        String projectUrl = "/";
        String extra = "";
        if(req.length > 0){
            projectName = req[0];
            if(req.length > 1){
                projectUrl = Util.join(Util.splice(req, 0, 1), "/");
            }
            if(!"".equals(projectName)){
                try {
                    Database database = new Database();
                    database.addArgument("key_prj", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, projectName);
                    database.addArgument("url_request", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, projectUrl);
                    database.addArgument("code_request", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                    database.addArgument("schema_struct", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                    List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select \n" +
                            "    r1.code_request,\n" +
                            "    st1.schema_struct\n" +
                            "from prj p1\n" +
                            "inner join sys s1 on s1.id_sys = p1.id_sys\n" +
                            "inner join request r1 on r1.id_sys = s1.id_sys\n" +
                            "inner join struct st1 on st1.id_struct = r1.id_struct\n" +
                            "where 1 = 1 \n" +
                            "and p1.key_prj = ${key_prj}\n" +
                            "and r1.url_request = ${url_request}");
                    extra = exec.toString();
                    if(exec.size() > 0 && exec.get(0).get("code_request") != null){
                        String code = (String) exec.get(0).get("code_request");
                        out.println(!"".equals(code) ? JS.runJS(code, getBody(request)) : "JavaScript code empty");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            out.println("ProjectName: "+projectName+"; ProjectUrl: "+projectUrl+"; Extra: "+extra);
        }else{
            out.println("Empty query");
        }
    }
}
