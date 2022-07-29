package ru.jamsys.servlet;

import com.google.gson.Gson;
import ru.jamsys.PersonUtil;
import ru.jamsys.RequestContext;
import ru.jamsys.Temporary;
import ru.jamsys.Util;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@WebServlet(name = "Telegram", value = "/telegram/*")
public class Telegram extends AbstractHttpServletReader {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String[] path = parseFullUrl(request);
            if (path.length == 1 && System.getProperty("TELEGRAM_TOKEN") != null && System.getProperty("TELEGRAM_TOKEN").equals(path[0])) {
                //System.out.println(Arrays.toString(path));
                String dataJson = getBody(request);
                System.out.println(dataJson);
                if (dataJson != null && !"".equals(dataJson)) {
                    Map data = new Gson().fromJson(dataJson, Map.class);
                    Double idChat = (Double) Util.selector(data, "message.chat.id", null);
                    if (idChat != null) {
                        String text = (String) Util.selector(data, "message.text", null);
                        String ret = "Спасибо, вы успешно авторизованы. Вернитесь в приложение для продолжения работы";
                        if (text != null) {
                            String[] exp = text.split(" ");
                            if (exp.length == 2) {
                                String idPerson = Temporary.getInstance().get(exp[1]);
                                if (idPerson != null) {
                                    RequestContext requestContext = new RequestContext();
                                    requestContext.idPerson = new BigDecimal(idPerson);
                                    requestContext.idChatTelegram = new BigDecimal(Util.doubleRemoveExponent(idChat));
                                    PersonUtil.addIdChatTelegram(requestContext);
                                } else {
                                    ret = "Скорее всего, время авторизации превысило 15 минут, вернитесь в приложение и повторите попытку";
                                }
                            } else {
                                ret = "Что-то пошло не так, сервер должен был получить временный код, а получил ничего, дальнейшая авторизация невозможна";
                            }
                        } else {
                            ret = "Сервер не вернул предполагаемый ответ";
                        }
                        Util.sendTelegram(Util.doubleRemoveExponent(idChat), ret);
                    }
                }
            } else {
                System.out.println("TELEGRAM_TOKEN not equals");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getRequestURI());
        response.getWriter().write("Ok");
    }

}
