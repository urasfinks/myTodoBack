package ru.jamsys.servlet;

import ru.jamsys.JS;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "RunJS", value = "/run-js")
public class RunJS extends AbstractHttpServletReader {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        try {
            String code = getBody(request);
            out.println(!"".equals(code) ? JS.runJS(code, getBody(request), request.getHeader("Authorization")) : "JavaScript code empty");
        }catch (Exception e){
            out.println(e.toString());
        }
    }

}