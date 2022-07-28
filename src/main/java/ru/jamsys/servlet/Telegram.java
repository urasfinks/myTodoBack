package ru.jamsys.servlet;

import com.google.gson.Gson;
import ru.jamsys.Util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@WebServlet(name = "Telegram", value = "/telegram/*")
public class Telegram extends AbstractHttpServletReader{

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String dataJson = getBody(request);
            System.out.println(dataJson);
            if (dataJson != null && !"".equals(dataJson)) {
                Map data = new Gson().fromJson(dataJson, Map.class);
                Double idChat = (Double) Util.selector(data, "message.chat.id", null);
                if(idChat != null){
                    sendMessage(Util.doubleRemoveExponent(idChat), "Hello");
                }
            }
            doGet(request, response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getRequestURI());
        response.getWriter().write("Ok");
    }

    void sendMessage(String chatId, String data) throws IOException {
        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

        String apiToken = System.getProperty("TELEGRAM_TOKEN");

        urlString = String.format(urlString, apiToken, chatId, data);

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(conn.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        String response = sb.toString();
// Do what you want with response
    }

}
