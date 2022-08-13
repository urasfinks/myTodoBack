package ru.jamsys.servlet;

import ru.jamsys.RequestContext;
import ru.jamsys.util.PersonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "AvatarGetTemp", value = "/avatar-get-id/*")
public class AvatarGetId extends AbstractHttpServletReader {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestContext rc = new RequestContext();

        if (!isAuth(request, response, rc)) {
            return;
        }
        String pKey = "null";
        try {
            String[] req = parseFullUrl(request);
            if (req.length > 0) {
                String x = PersonUtil.getPersonKey(new BigDecimal(req[0]));
                if (x != null) {
                    pKey = x;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        writeAvatar(pKey, response);
    }
}

