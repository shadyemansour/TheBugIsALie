package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.SQLDatabase;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.networking.JSONMessage;
import de.lmu.ifi.sosy.tbial.networking.WebSocketManager;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


@AuthenticationRequired
public class GameView extends BasePage {

  private static final long serialVerisonUID = 1L;
  AjaxButton leaveButton;
  Game game;
  ModalWindow modalWindow;
  private static GameView instance;
  Form<?> form;

  public GameView(Game game) {
    this.game = game;
    this.game.addPropertyChangeListener(new GameViewListener());
    this.instance = this;

    add(new WebSocketBehavior() {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onConnect(ConnectedMessage message) {
        super.onConnect(message);

        WebSocketManager.getInstance().addClient(message, ((TBIALSession) getSession()).getUser().getId());
      }

      @Override
      protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
        super.onPush(handler, message);

        if (message instanceof JSONMessage) {
          handleMessage((JSONMessage) message);
        }
      }
    });
    add(new FeedbackPanel("feedback"));

    add(modalWindow = new ModalWindow("gamePaused"));
    modalWindow.setOutputMarkupId(true);


    leaveButton = new AjaxButton("leaveButton") {
      /**
       * UID for serialization.
       */
      private static final long serialVersionUID = 1;

      public void onSubmit(AjaxRequestTarget target) {
        User user = ((TBIALSession) getSession()).getUser();
        game.removePlayer(user);
        user.setGame(null);
        user.setJoinedGame(false);
        ((Database) getDatabase()).setUserGame(user.getId(), "NULL");
        WebSocketManager.getInstance().sendMessage(gamePausedJSONMessage(((TBIALSession) getSession()).getUser().getId(), game.getId()));
        setResponsePage(Lobby.class);
      }

      @Override
      protected void onError(AjaxRequestTarget target) {
      }
    };
    form = new Form<>("form");
    modalWindow.setVisible(true);
    form.add(leaveButton).add(modalWindow);
    form.setOutputMarkupId(true);
    add(form);

//    if(!game.getGameState().equals("running")){
//      RequestCycle.get().find(IPartialPageRequestHandler.class).ifPresent(target -> {
//        game.setGamePaused(true);
//        modalWindow.setContent(new GamePausedPanel(modalWindow.getContentId()));
//        modalWindow.setVisible(true);
//        modalWindow.show(target);
//      });
//    }
  }


  private void handleMessage(JSONMessage message) {
    JSONObject jsonMsg = message.getMessage();
    String msgType = (String) jsonMsg.get("msgType");
    JSONObject body;
    int gameID;
    int userID;
    switch (msgType) {
      case "GamePaused":
        body = jsonMsg.getJSONObject("msgBody");
        gameID = (int) body.get("gameID");
        userID = (int) body.get("userID");
        if (gameID == game.getId() && userID != ((TBIALSession) getSession()).getUser().getId()) {
          RequestCycle.get().find(IPartialPageRequestHandler.class).ifPresent(target -> {
            game.setGamePaused(true);
            modalWindow.setContent(new GamePausedPanel(modalWindow.getContentId()));
            modalWindow.setVisible(true);
            modalWindow.show(target);
          });
        }
      case "ContinueGame":
        body = jsonMsg.getJSONObject("msgBody");
        gameID = (int) body.get("gameID");
        userID = (int) body.get("userID");
        if (gameID == game.getId() && userID != ((TBIALSession) getSession()).getUser().getId()) {
          RequestCycle.get().find(IPartialPageRequestHandler.class).ifPresent(target -> {
            modalWindow.close(target);
          });
        }
    }
  }

  private JSONMessage gamePausedJSONMessage(int userId, int gameId) {
    JSONObject msgBody = new JSONObject();
    msgBody.put("gameID", gameId);
    msgBody.put("userID", userId);
    JSONObject msg = new JSONObject();
    msg.put("msgType", "GamePaused");
    msg.put("msgBody", msgBody);
    return new JSONMessage(msg);
  }

  public static GameView getInstance() {
    return instance;
  }

  public class GameViewListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("PlayerAddedGameRunning")) {
        Game g = (Game) event.getOldValue();
        if (g.getName().equals(game.getName()) && game.getActivePlayers() == game.getNumPlayers()) {
          game.setGamePaused(false);
          WebSocketManager.getInstance().sendMessage(continueGameJSONMessage(((TBIALSession) getSession()).getUser().getId(), game.getId()));

        }
      }
    }
  }

  private JSONMessage continueGameJSONMessage(int userId, int gameId) {
    JSONObject msgBody = new JSONObject();
    msgBody.put("gameID", gameId);
    msgBody.put("userID", userId);
    JSONObject msg = new JSONObject();
    msg.put("msgType", "ContinueGame");
    msg.put("msgBody", msgBody);
    return new JSONMessage(msg);
  }


}