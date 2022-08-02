package ru.jamsys;

import ru.jamsys.database.Database;
import ru.jamsys.database.DatabaseArgumentDirection;
import ru.jamsys.database.DatabaseArgumentType;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@WebListener("Bootstrap")
public class BootsTrapListener implements ServletContextListener {

    AtomicBoolean run = new AtomicBoolean(true);
    private Thread thread = null;
    public static RequestContext systemRequestContext;

    class NotifyObject {

        BigDecimal idPerson;
        BigDecimal id;
        String data;
        BigDecimal idChatTelegram;

        public NotifyObject(BigDecimal idPerson, BigDecimal id, String data, BigDecimal idChatTelegram) throws Exception {
            if ("".equals(data.trim())) {
                throw new Exception("data is empty");
            }
            this.idPerson = idPerson;
            this.id = id;
            this.data = data;
            this.idChatTelegram = idChatTelegram;
        }
    }

    private BigDecimal getQueueSize() {
        try {
            Database database = new Database();
            database.addArgument("count", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "SELECT count(*) FROM notify \n" +
                    "where send_notify = 0\n" +
                    "and timestamp_notify <= now()::timestamp");
            BigDecimal bd = (BigDecimal) database.checkFirstRowField(exec, "count");
            if (bd != null) {
                return bd;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }

    private void updateNotifyAsSend(NotifyObject notifyObject) {
        try {
            Database database = new Database();
            database.addArgument("id_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.IN, notifyObject.id);
            database.exec("java:/PostgreDS", "update notify set send_notify = 1 where id_notify = ${id_notify}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NotifyObject getNotify() {
        try {
            Database database = new Database();
            database.addArgument("id_person", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("id_notify", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("data_notify", DatabaseArgumentType.VARCHAR, DatabaseArgumentDirection.COLUMN, null);
            database.addArgument("id_chat_telegram", DatabaseArgumentType.NUMBER, DatabaseArgumentDirection.COLUMN, null);
            List<Map<String, Object>> exec = database.exec("java:/PostgreDS", "SELECT p1.id_person, n1.id_notify, n1.data_notify, p1.id_chat_telegram FROM notify n1\n" +
                    "inner join person p1 on  n1.id_person_to = p1.id_person\n" +
                    "where n1.send_notify = 0\n" +
                    "and n1.timestamp_notify <= now()::timestamp\n" +
                    "and p1.id_chat_telegram is not null\n" +
                    "and n1.data_notify is not null\n" +
                    "ORDER BY n1.id_notify ASC");
            if (exec.size() > 0) {
                return new NotifyObject(
                        (BigDecimal) exec.get(0).get("id_person"),
                        (BigDecimal) exec.get(0).get("id_notify"),
                        (String) exec.get(0).get("data_notify"),
                        (BigDecimal) exec.get(0).get("id_chat_telegram")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    long nextSend = System.currentTimeMillis();

    private boolean iter() {
        //System.out.println("Iter");
        BigDecimal queueSize = getQueueSize();
        if (queueSize.intValue() > 10) {
            if (nextSend < System.currentTimeMillis()) {
                sendToTelegramSystem("Очередь на рассылку заполнилась заполнилась на: " + queueSize);
                nextSend = System.currentTimeMillis() + 60 * 1000 * 5;
            }
        }
        NotifyObject notifyObject = getNotify();
        if (notifyObject != null) {
            TelegramResponse telegramResponse = Util.syncTendTelegram(notifyObject.idChatTelegram.toString(), notifyObject.data);
            telegramResponse.checkSuccess(notifyObject.idPerson);
            updateNotifyAsSend(notifyObject);
            return true;
        } else {
            return false;
        }
    }

    public static void sendToTelegramSystem(String data){
        if(systemRequestContext != null){
            PersonUtil.syncSendTelegram(systemRequestContext, data);
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
