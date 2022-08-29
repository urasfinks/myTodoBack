package ru.jamsys.servlet;

import ru.jamsys.util.States;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "State", urlPatterns = "/state/*")
public class State extends AbstractHttpServletReader {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] req = parseFullUrl(request);
        PrintWriter out = response.getWriter();
        if (req.length > 0) {
            if ("get".equals(req[0])) {
                out.println(States.get(req[1]));
            } else if ("show".equals(req[0])) {
                out.println(States.show());
            }
        } else {
            out.println("SHALOM)");
        }
    }

}
