package ru.jamsys.servlet;

import ru.jamsys.Util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Project", urlPatterns = "/project/*")
public class Project extends AbstractHttpServletReader {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String[] req = parseFullUrl(request);
        if(req.length > 0){
            String projectName = req[0];
            String projectUrl = req.length > 1 ? Util.join(Util.splice(req, 0, 1), "/") : "";
            out.println("ProjectName: "+projectName+"; ProjectUrl: "+projectUrl);
        }else{
            out.println("Empty query");
        }
    }
}
