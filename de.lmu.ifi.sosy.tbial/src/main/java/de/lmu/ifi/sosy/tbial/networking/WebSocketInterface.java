package de.lmu.ifi.sosy.tbial.networking;

import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;

public interface WebSocketInterface {

  void addClient(ConnectedMessage message);

  void sendMessage(int number);


}
