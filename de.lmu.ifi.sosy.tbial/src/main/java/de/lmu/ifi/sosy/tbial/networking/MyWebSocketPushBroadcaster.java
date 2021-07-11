package de.lmu.ifi.sosy.tbial.networking;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.concurrent.Executor;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Args;

import java.util.Collection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import static java.util.Collections.singletonList;

public class MyWebSocketPushBroadcaster {

  private final IWebSocketConnectionRegistry registry;
  private final Executor webSocketPushMessageExecutor;

  public MyWebSocketPushBroadcaster(IWebSocketConnectionRegistry registry) {

    this.registry = registry;
    webSocketPushMessageExecutor = new MyWebSocketPushMessageExecutor();
  }

  public void broadcastAll(Application application, IWebSocketPushMessage message) {
    Args.notNull(application, "application");
    Args.notNull(message, "message");

    Collection<IWebSocketConnection> wsConnections = registry.getConnections(application);
    if (wsConnections == null) {
      return;
    }
    process(wsConnections, message);
  }

  public void broadcast(ConnectedMessage connection, IWebSocketPushMessage message) {
    Args.notNull(connection, "connection");
    Args.notNull(message, "message");

    Application application = connection.getApplication();
    String sessionId = connection.getSessionId();
    IKey key = connection.getKey();
    IWebSocketConnection wsConnection = registry.getConnection(application, sessionId, key);
    if (wsConnection == null) {
      return;
    }
    process(singletonList(wsConnection), message);
  }

  private void process(final Collection<IWebSocketConnection> wsConnections,
                       final IWebSocketPushMessage message) {
    Executor executor = webSocketPushMessageExecutor;

    for (final IWebSocketConnection wsConnection : wsConnections) {
      executor.run(new Runnable() {
        @Override
        public void run() {
          wsConnection.sendMessage(message);
        }
      });
    }
  }

  public static class MyWebSocketPushMessageExecutor implements Executor {

    private final java.util.concurrent.Executor nonHttpRequestExecutor;
    private final java.util.concurrent.Executor httpRequestExecutor;

    public MyWebSocketPushMessageExecutor() {
      this(Runnable::run, new ThreadPoolExecutor(1, 100,
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
