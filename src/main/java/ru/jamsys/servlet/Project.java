package ru.jamsys.servlet;

import ru.jamsys.RequestContext;
import ru.jamsys.util.Util;
import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet(name = "Project", urlPatterns = "/project/*")
public class Project extends AbstractHttpServletReader {

    public static Map<BigDecimal, String> map = new ConcurrentHashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestContext rc = new RequestContext();

        if(!isAuth(request, response, rc)){
            return;
        }
        String platform = request.getHeader("Platform");
        if(platform != null){
            rc.setPlatformName(platform);
        }
        try {
            rc.version = Integer.parseInt(version);
        }catch (Exception e){

        }
        map.put(rc.idPerson, personKey);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String[] req = parseFullUrl(request);

        rc.projectName = "";
        rc.projectUrl = "/";
        rc.host = request.getServerName();
        rc.url = request.getRequestURI();
        rc.getParam = parseGetParam(request);
        String extra = "";
        if (req.length > 0) {
            rc.projectName = req[0];
            if (req.length > 1) {
                rc.projectUrl = "/" + Util.join(Util.splice(req, 0, 1), "/");
            }
            if (!"".equals(rc.projectName)) {
                try {
                    Database database = new Database();
                    database.addArgument("key_prj", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, rc.projectName);
                    database.addArgument("url_request", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.IN, rc.projectUrl);
                    database.addArgument("code_request", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                    database.addArgument("schema_struct", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
                    database.addArgument("id_prj", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
                    List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "select \n" +
                            "    r1.code_request,\n" +
                            "    st1.schema_struct,\n" +
                            "    p1.id_prj\n" +
                            "from prj p1\n" +
                            "inner join sys s1 on s1.id_sys = p1.id_sys\n" +
                            "inner join request r1 on r1.id_sys = s1.id_sys\n" +
                            "inner join struct st1 on st1.id_struct = r1.id_struct\n" +
                            "where 1 = 1 \n" +
                            "and p1.key_prj = ${key_prj}\n" +
                            "and r1.url_request = ${url_request}");
                    extra = exec.toString();
                    String code = (String) database.checkFirstRowField(exec, "code_request");
                    rc.idProject = (BigDecimal) database.checkFirstRowField(exec, "id_prj");
                    if (code != null) {
                        String x = !"".equals(code) ? Util.runJS(code, getBody(request), rc) : "JavaScript code empty";
                        response.setContentType("application/json;charset=UTF-8");
                        out.println(x);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace(out);
                }
            }
            response.setStatus(500);
            //out.println("ProjectName: " + rc.projectName + "; ProjectUrl: " + rc.projectUrl + "; Extra: " + extra);
            out.println(rc.toString() + "; Extra: " + extra);
        } else {
            out.println("Empty query");
        }
    }

}
