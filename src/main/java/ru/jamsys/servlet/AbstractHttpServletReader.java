package ru.jamsys.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Vector;

public class AbstractHttpServletReader extends HttpServlet {

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

    public static String[] parseFullUrl(HttpServletRequest req) {
        Vector res = new Vector();
        try {
            String pathAfterContext = req.getRequestURI().substring(
                    req.getContextPath().length() + req.getServletPath().length() + 1);

            for (String val : pathAfterContext.split("/")) {
                res.add(URLDecoder.decode(val, "UTF-8"));
            }
            String query = req.getQueryString();
            if (query != null) {
                for (String val : query.split("&")) {
                    res.add(URLDecoder.decode(val, "UTF-8"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String[]) res.toArray(new String[0]);
    }

    public static String[] parseNameValue(String qParam) throws Exception {
        int pos = qParam.indexOf("=");
        if (pos<=0) {
            throw new Exception("can't find equals symbol between name and value");
        }
        String[] ret = new String[2];
        ret[0] = qParam.substring(0,pos);
        ret[1] = qParam.substring(pos+1);
        return ret;
    }


}
