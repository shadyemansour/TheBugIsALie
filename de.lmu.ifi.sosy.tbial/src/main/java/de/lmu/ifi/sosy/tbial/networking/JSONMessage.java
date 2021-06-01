package de.lmu.ifi.sosy.tbial.networking;

import java.io.Serializable;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.json.JSONObject;

public class JSONMessage implements IWebSocketPushMessage, Serializable {
	private static final long serialVersionUID = 1L;

	private final JSONObject message;

	public JSONMessage(JSONObject message) {
		this.message = message;
	}

	public JSONObject getMessage() {
		return this.message;
	}

}
