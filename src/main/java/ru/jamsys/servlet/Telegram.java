package ru.jamsys.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Telegram", value = "/telegram/*")
public class Telegram extends AbstractHttpServletReader{

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(getBody(request));
        doGet(request, response);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getRequestURI());
        response.getWriter().write("Hello world");
    }

}
