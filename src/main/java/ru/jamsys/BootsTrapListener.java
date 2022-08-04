package ru.jamsys;

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
        NotifyUtil.NotifyObject notifyObject = NotifyUtil.getNotify();
        if (notifyObject != null) {
            TelegramResponse telegramResponse = TelegramUtil.syncSend(notifyObject.idChatTelegram.toString(), notifyObject.data);
            telegramResponse.checkSuccess(notifyObject.idPerson);
            NotifyUtil.update(notifyObject, telegramResponse);
            return true;
        } else {
            return false;
        }
    }

    public static void sendToTelegramSystem(String data){
        if(systemRequestContext != null){
            TelegramUtil.syncSend(systemRequestContext, data);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        systemRequestContext = PersonUtil.getRequestContextByIdPerson(new BigDecimal(1));
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
