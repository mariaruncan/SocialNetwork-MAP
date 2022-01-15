package socialnetwork.domain.utils;

import com.example.map226mariaalexandra.WelcomePageController;
import javafx.application.Platform;

import java.util.ConcurrentModificationException;

public class MyThread implements Runnable{
    private boolean exit;
    private final Thread thread;
    private final WelcomePageController controller;

    public MyThread(WelcomePageController controller){
        this.thread = new Thread(this);
        this.exit = false;
        this.controller = controller;
        this.thread.start();
    }

    @Override
    public void run() {
        while(!exit){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    controller.notifyEvents();
                }
            });
            try{
                Thread.sleep(180000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.exit = true;
    }
}
