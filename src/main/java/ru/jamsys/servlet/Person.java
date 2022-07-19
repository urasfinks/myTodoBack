package ru.jamsys.servlet;

import ru.jamsys.PersonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "Person", value = "/person/*")
public class Person extends AbstractHttpServletReader{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] req = parseFullUrl(request);
        PersonUtil.createPerson(req[0]);
        response.getWriter().write("H"); // ))) What H?)) That H!)))
    }
}
