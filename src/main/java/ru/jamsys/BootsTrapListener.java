package ru.jamsys;

import ru.jamsys.sub.DataState;
import ru.jamsys.sub.NotifyObject;
import ru.jamsys.sub.TelegramResponse;
import ru.jamsys.util.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

@WebListener("Bootstrap")
public class BootsTrapListener implements ServletContextListener {

    AtomicBoolean run = new AtomicBoolean(true);
    private Thread thread = null;
    public static RequestContext systemRequestContext;

    long nextSend = System.currentTimeMillis();

    private boolean iter() {
        BigDecimal queueSize = NotifyUtil.getQueueSize();
        if (queueSize.intValue() > 10) {
            if (nextSend < System.currentTimeMillis()) {
                sendToTelegramSystem("Очередь на рассылку заполнилась заполнилась на: " + queueSize);
                nextSend = System.currentTimeMillis() + 60 * 1000 * 5;
            }
        }
        NotifyObject notifyObject = NotifyUtil.getNotify();
        if (notifyObject != null) {
            boolean needSend = true;
            try {
                if (notifyObject.idData != null) {
                    String curDataUID = DataUtil.getUIDById(notifyObject.idData);
                    DataState parentState = DataUtil.getParentState(curDataUID);
                    if (parentState != null && parentState.state.containsKey(curDataUID)) {
                        if ((boolean) parentState.state.get(curDataUID) == true) {
                            needSend = false;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (needSend == true) {
                if (SystemUtil.isOnlyNativeNotify() || notifyObject.idChatTelegram == null) {
                    ChatUtil.add(notifyObject.idPerson, PersonUtil.systemPerson, notifyObject.data);
                    NotifyUtil.update(notifyObject, "{\"status\": \"Send offline mode\"}");
                } else {
                    TelegramResponse telegramResponse = TelegramUtil.syncSend(notifyObject.idChatTelegram.toString(), notifyObject.data);
                    telegramResponse.checkSuccess(notifyObject.idPerson);
                    NotifyUtil.update(notifyObject, telegramResponse.resp);
                }
            } else {
                NotifyUtil.update(notifyObject, "{\"status\", \"Data checked as completed\"}");
            }
            return true;
        } else {
            return false;
        }
    }

    public static void sendToTelegramSystem(String data) {
        if (systemRequestContext != null) {
            TelegramUtil.syncSend(systemRequestContext, data);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        systemRequestContext = PersonUtil.getRequestContextByIdPerson(PersonUtil.systemPerson);
        sendToTelegramSystem("Start Java");
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (run.get() == true) {
                    boolean more = iter();
                    try {
                        Thread.sleep(more ? 1000 : 30000);
                    } catch (Exception e) {
                    }

                }
            }
        });
        thread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sendToTelegramSystem("Stop Java");
        run.set(false);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {

        }
        try {
            thread.interrupt();
        } catch (Exception e) {
        }
    }
}
