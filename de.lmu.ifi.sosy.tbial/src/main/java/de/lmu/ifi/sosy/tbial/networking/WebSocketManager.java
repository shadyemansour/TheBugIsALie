package de.lmu.ifi.sosy.tbial.networking;

import java.util.HashMap;

import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;


public class WebSocketManager {

  private HashMap<Integer, ConnectedMessage> connections = new HashMap<>();
  private WebSocketPushBroadcaster broadcaster;

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
      IWebSocketConnectionRegistry webSocketConnectionRegistry = webSocketSettings.getConnectionRegistry();
      broadcaster = new WebSocketPushBroadcaster(webSocketConnectionRegistry);
    }
  }

  public void sendMessage(JSONMessage msg) {
    if (null != broadcaster) {
      broadcaster.broadcastAll(connections.entrySet().iterator().next().getValue().getApplication(), msg);
    } else {
      //throw new RuntimeException("Unable to send message");
    }
  }

}
