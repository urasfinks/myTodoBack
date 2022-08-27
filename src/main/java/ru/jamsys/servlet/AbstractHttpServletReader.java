package ru.jamsys.servlet;

import ru.jamsys.RequestContext;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class AbstractHttpServletReader extends HttpServlet {

    protected String personKey = null;
    protected String version = null;

    public boolean isAuth(HttpServletRequest request, HttpServletResponse response, RequestContext rc) throws IOException {
        String auth = request.getHeader("Authorization");
        String personKey = getPersonKey(auth);
        if (!rc.init(personKey)) {
            response.setStatus(401);
            response.setHeader("WWW-Authenticate", "Basic realm=\"JamSys\"");
            response.getWriter().print("<html><body><h1>401. Unauthorized</h1></body>");
            return false;
        }
        this.personKey = personKey;
        this.version = getApplicationVersion(auth);
        return true;
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    public static Map<String, String> parseGetParam(HttpServletRequest req) {
        String query = req.getQueryString();
        Map<String, String> ret = new HashMap<>();
        if (query != null) {
            for (String val : query.split("&")) {
                try {
                    String[] group = parseNameValue(URLDecoder.decode(val, "UTF-8"));
                    ret.put(group[0], group[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public static String[] parseFullUrl(HttpServletRequest req) {
        Vector<String> res = new Vector<>();
        try {
            String pathAfterContext = req.getRequestURI().substring(
                    req.getContextPath().length() + req.getServletPath().length() + 1);

            for (String val : pathAfterContext.split("/")) {
                res.add(URLDecoder.decode(val, "UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.toArray(new String[0]);
    }

    public static String[] parseNameValue(String qParam) throws Exception {
        int pos = qParam.indexOf("=");
        if (pos <= 0) {
            throw new Exception("can't find equals symbol between name and value");
        }
        String[] ret = new String[2];
        ret[0] = qParam.substring(0, pos);
        ret[1] = qParam.substring(pos + 1);
        return ret;
    }

    protected String getPersonKey(String auth) {
        if (auth != null && !"".equals(auth) && auth.startsWith("Basic ")) {
            String[] x = auth.split("Basic ");
            if (x.length == 2) {
                byte[] decoded = Base64.getDecoder().decode(x[1]);
                String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                if (decodedStr.startsWith("PersonKey")) {
                    String[] x2 = decodedStr.split(":");
                    if (x2.length == 2) {
                        return x2[1];
                    }
                }
            }
        }
        return null;
    }

    protected String getApplicationVersion(String auth) {
        if (auth != null && !"".equals(auth) && auth.startsWith("Basic ")) {
            String[] x = auth.split("Basic ");
            if (x.length == 2) {
                byte[] decoded = Base64.getDecoder().decode(x[1]);
                String decodedStr = new String(decoded, StandardCharsets.UTF_8);
                if (decodedStr.startsWith("PersonKey")) {
                    String[] x2 = decodedStr.split(":");
                    if (x2.length == 2) {
                        String[] x3 = x2[0].split("PersonKey_");
                        if (x3.length == 2) {
                            return  x3[1];
                        }
                    }
                }
            }
        }
        return null;
    }

    protected void writeAvatar(String pKey, HttpServletResponse response) throws IOException {
        BufferedImage image = null;
        File file = new File("/var/www/jamsys/avatarImg/" + pKey + ".jpg");
        if (file.exists() && !file.isDirectory()) {
            image = ImageIO.read(file);
        }
        if (image == null) {
            image = ImageIO.read(new File("/var/www/jamsys/avatarImg/no-avatar.jpg"));
        }
        ImageIO.write(image, "JPG", response.getOutputStream());
    }

}
