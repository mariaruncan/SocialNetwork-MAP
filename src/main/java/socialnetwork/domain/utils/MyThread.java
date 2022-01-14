package socialnetwork.domain.utils;

import com.example.map226mariaalexandra.WelcomePageController;
import javafx.application.Platform;

public class MyThread implements Runnable{
    private boolean exit;
    private Thread thread;
    private WelcomePageController controller;

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
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.exit = true;
    }
}
