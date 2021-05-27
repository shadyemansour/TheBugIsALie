package de.lmu.ifi.sosy.tbial.networking;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {
  private static final int MAX_WAITING_TIME = 1;  // in seconds
  public static final int MAX_NUMBER = 10000;     // a random number

  private static Updater instance;

  private UpdateTask updateTask = null;
  private int number = 0;
  private ScheduledExecutorService threadPool = null;
  private Boolean increase = null;
  public Integer DELTA_T = 100; // in miliseconds


  public static Updater getInstance() {
    if (instance == null) {
      instance = new Updater();
    }
    return instance;
  }

  public static void start() {
    System.out.println("updater started");
  }

  public static void stop() {
    System.out.println("updater stopped");
  }

  public void reverse() {
    this.increase = false;
  }

  public void join() {
    if (updateTask == null) {
      updateTask = new UpdateTask(this);
      threadPool = Executors.newSingleThreadScheduledExecutor();
    }
    threadPool.schedule(updateTask, MAX_WAITING_TIME, TimeUnit.SECONDS);
  }

  public void doStep() {
    if (increase != null) {
      number = (increase == true) ? ++number : --number;
      WebSocketService.getInstance().sendMessage(number);
    }
  }

  public boolean isRunning() {
    return number < MAX_NUMBER && number > -MAX_NUMBER;
  }

  public int getDelta() {
    return this.DELTA_T;
  }


  private static class UpdateTask implements Runnable {
    private Updater updater;

    public UpdateTask(Updater updater) {
      this.updater = updater;
    }

    @Override
    public void run() {
      try {
        while (updater.isRunning()) {
          updater.doStep();
          TimeUnit.MILLISECONDS.sleep((long) (updater.getDelta()));
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }


  public static class Message implements IWebSocketPushMessage, Serializable {
    private String type;
    private String msg;
  }
}
