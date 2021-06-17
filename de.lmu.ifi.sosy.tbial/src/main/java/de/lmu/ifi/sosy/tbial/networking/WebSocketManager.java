package de.lmu.ifi.sosy.tbial.networking;

import java.util.HashMap;
import java.util.concurrent.*;

import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.request.cycle.RequestCycle;


public class WebSocketManager {

  private HashMap<Integer, ConnectedMessage> connections = new HashMap<>();
  private MyWebSocketPushBroadcaster broadcaster;
  private IWebSocketConnectionRegistry webSocketConnectionRegistry;
  private static WebSocketManager instance;

  private WebSocketManager() {
  }

  public static WebSocketManager getInstance() {
    if (instance == null) {
      instance = new WebSocketManager();
    }
    return instance;
  }

  public void addClient(ConnectedMessage msg, int userId) {
    ConnectedMessage conMsg = new ConnectedMessage(msg.getApplication(), msg.getSessionId(), msg.getKey());
    connections.put(userId, conMsg);

    if (null == broadcaster) {
      WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(msg.getApplication());
      webSocketConnectionRegistry = webSocketSettings.getConnectionRegistry();
      broadcaster = new MyWebSocketPushBroadcaster(webSocketConnectionRegistry);

    }
  }

  public void sendPrivateMessage(JSONMessage msg, int userID) {
    if (connections.keySet().contains(userID)) {
      if (null != broadcaster) {
        broadcaster.broadcast(connections.get(userID), msg);
      } else {
        //throw new RuntimeException("Unable to send message");
      }
    }
  }

  public void sendMessage(JSONMessage msg) {
    if (null != broadcaster) {
      broadcaster.broadcastAll(connections.entrySet().iterator().next().getValue().getApplication(), msg);
    } else {
      //throw new RuntimeException("Unable to send message");
    }
  }

  public static class MyWebSocketPushMessageExecutor implements org.apache.wicket.protocol.ws.concurrent.Executor {

    private final java.util.concurrent.Executor nonHttpRequestExecutor;
    private final java.util.concurrent.Executor httpRequestExecutor;

    public MyWebSocketPushMessageExecutor() {
      this(Runnable::run, new ThreadPoolExecutor(1, 20,
          60L, TimeUnit.SECONDS,
          new SynchronousQueue<>(),
          new WebSocketSettings.ThreadFactory()));
    }

    public MyWebSocketPushMessageExecutor(java.util.concurrent.Executor nonHttpRequestExecutor, java.util.concurrent.Executor httpRequestExecutor) {
      this.nonHttpRequestExecutor = nonHttpRequestExecutor;
      this.httpRequestExecutor = httpRequestExecutor;
    }

    @Override
    public void run(final Runnable command) {
      if (RequestCycle.get() != null) {
        httpRequestExecutor.execute(command);
      } else {
        nonHttpRequestExecutor.execute(command);
      }
    }
  }

}
