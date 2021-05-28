package de.lmu.ifi.sosy.tbial.networking;

import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.WebSocketResource;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class BugWebSocketResource extends WebSocketResource {
    public static final String NAME = BugWebSocketResource.class.getName();

    @Override
    protected void onConnect(ConnectedMessage message) {
        super.onConnect(message);

        Updater.start();
    }

    @Override
    protected void onMessage(WebSocketRequestHandler handler, TextMessage message) {
        String msg = message.getText();

    }
}
