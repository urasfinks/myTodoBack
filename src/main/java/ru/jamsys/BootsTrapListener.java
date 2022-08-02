package ru.jamsys;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.atomic.AtomicBoolean;

@WebListener("Bootstrap")
public class BootsTrapListener implements ServletContextListener {

    AtomicBoolean run = new AtomicBoolean(true);
    private Thread thread = null;

    private void iter(){
        System.out.println("Hello");
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (run.get() == true) {
                    iter();
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }

                }
            }
        });
        thread.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
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
