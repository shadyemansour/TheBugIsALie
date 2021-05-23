package de.lmu.ifi.sosy.tbial.networking;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {
    public static void start(ConnectedMessage message) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        Message msg = new Message();
        UpdateTask updateTask = new UpdateTask(message.getApplication(), message.getSessionId(), message.getKey(), msg);
        scheduledExecutorService.schedule(updateTask, 1, TimeUnit.SECONDS);
    }




    private static class UpdateTask implements Runnable {
        private final String applicationName;
        private final String sessionId;
        private final IKey key;


        private final Message msg;

        private UpdateTask(Application application, String sessionId, IKey key, Message msg){
            this.applicationName = application.getName();
            this.sessionId = sessionId;
            this.key = key;
            this.msg = msg;
        }

        @Override
        public void run()
        {
            Application application = Application.get(applicationName);
            WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
            IWebSocketConnectionRegistry webSocketConnectionRegistry = webSocketSettings.getConnectionRegistry();

            while (true){//todo
                IWebSocketConnection connection = webSocketConnectionRegistry.getConnection(application, sessionId, key);
                try{
                    String json = "";

                    if (connection == null || !connection.isOpen()) {
                        return;
                    }
                    connection.sendMessage(json);

                    TimeUnit.SECONDS.sleep(1);
                }
                catch (InterruptedException x)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private static class Message
    {
        private String type;
        private String msg;
    }
}
