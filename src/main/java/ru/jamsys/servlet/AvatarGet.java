package ru.jamsys.servlet;

import ru.jamsys.RequestContext;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AvatarGet", value = "/avatar-get")
public class AvatarGet extends AbstractHttpServletReader {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestContext rc = new RequestContext();

        if (!isAuth(request, response, rc)) {
            return;
        }

        writeAvatar(personKey, response);
    }
}
