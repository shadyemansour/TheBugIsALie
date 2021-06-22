package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.db.*;
import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.networking.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static de.lmu.ifi.sosy.tbial.TBIALApplication.getDatabase;

public abstract class GameView extends WebPage {
  /**
   * UID for serialization.
   */
  private static final long serialVersionUID = 1L;
  User user = ((TBIALSession) getSession()).getUser();
  AjaxButton leaveButton;
  Game game = user.getGame();
  //  ModalWindow modalWindow;
  Form<?> form;
  private GameView instance;

  public GameView() {
    this.game.addPropertyChangeListener(new GameViewListener());


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

//    add(modalWindow = new ModalWindow("gamePaused"));
//    modalWindow.setOutputMarkupId(true);

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
//    modalWindow.setVisible(true);
    form.add(leaveButton);
//    form..add(modalWindow);
    form.setOutputMarkupId(true);
    add(form);

    if (!game.isGameStarted() && ((TBIALSession) getSession()).getUser().getId() == game.getHost().getId()) {
      try {
        Thread.sleep(1000); //TODO delay this till all other gameviews have started
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      game.startGame();
    }
  }

  private void handleMessage(JSONMessage message) {
    JSONObject jsonMsg = message.getMessage();
    String msgType = (String) jsonMsg.get("msgType");
    Iterator<Object> iterator;
    JSONObject body = jsonMsg.getJSONObject("msgBody");
    System.out.print(user.getId() + ": ");
    System.out.println(body);
    int gameID = (int) body.get("gameID");
    int userID;

    if (gameID == game.getId()) {
      switch (msgType) {
        case "GamePaused":
          userID = (int) body.get("userID");
          if (userID != ((TBIALSession) getSession()).getUser().getId()) {
            RequestCycle.get().find(IPartialPageRequestHandler.class).ifPresent(target -> {
              game.setGamePaused(true);
//            modalWindow.setContent(new GamePausedPanel(modalWindow.getContentId()));
//            modalWindow.setVisible(true);
//            modalWindow.show(target);
            });
          }
          break;
        case "ContinueGame":
          userID = (int) body.get("userID");
          if (userID != ((TBIALSession) getSession()).getUser().getId()) {
            RequestCycle.get().find(IPartialPageRequestHandler.class).ifPresent(target -> {
              game.setGamePaused(false);
//            modalWindow.close(target);
            });
          }
          break;
        case "GameStarted":
          int cardsInDeck = (int) body.get("cardsInDeck");
          int numPlayers = (int) body.get("numPlayers");
          //TODO USE THE DATA
          break;
        case "Roles":
          JSONArray roles = (JSONArray) body.get("roles");
          for (int i = 0; i < roles.length(); i++) {
            JSONObject container = (JSONObject) roles.get(i);
            int playerID = container.getInt("playerID");
            String role = container.getString("role");
            //TODO USE THE DATA
          }

          break;
        case "Characters":
          JSONArray characters = (JSONArray) body.get("characters");
          for (int i = 0; i < characters.length(); i++) {
            JSONObject container = (JSONObject) characters.get(i);
            int playerID = container.getInt("playerID");
            String character = container.getString("character");
            int health = container.getInt("health");
            //TODO USE THE DATA
          }

          break;
        case "CurrentPlayer":
          int currentPlayerID = (int) body.get("playerID");
          //TODO USE THE DATA
          break;
        case "Shuffle":
          int numCardsInDeck = (int) body.get("cardsInDeck");
          int numCardsInHeap = (int) body.get("cardsInHeap");
          //TODO USE THE DATA
          break;
        case "GameWon":
          int playerID = (int) body.get("playerID");
          //TODO USE THE DATA
          break;

        case "YourCards":
          JSONArray cardsJSON = (JSONArray) body.get("cards");
          List<Card> cards = new ArrayList<>();
          for (int i = 0; i < cardsJSON.length(); i++) {
            cards.add((Card) cardsJSON.get(i));
          }
          //TODO USE THE DATA]
          break;
        case "CardsDrawn":
          int playerId = body.getInt("playerID");
          int numCards = body.getInt("cards");
          int numDeckCards = body.getInt("cardsInDeck");
          //TODO USE THE DATA
          break;
        case "CardPlayed":
          int from = body.getInt("from");
          int to = body.getInt("to");
          Card car = (Card) body.get("card");
          //TODO USE THE DATA
          break;
        case "CardDiscarded":
          int player = body.getInt("playerID");
          Card card = (Card) body.get("card");
          //TODO USE THE DATA
          break;

        case "CardDefended":
          int pl = body.getInt("playerID");
          Card ca = (Card) body.get("card");
          //TODO USE THE DATA
          break;
        case "Health":
          int p = body.getInt("playerID");
          int health = body.getInt("health");
          //TODO USE THE DATA
          break;

      }

    }
  }

  public void drawCards() {
    game.drawCards(((TBIALSession) getSession()).getUser().getId(), 2 /*TODO CHANGE TO VARIABLE*/);
  }

  public void discardCard(Card card) {
    //TODO implementation
    game.discardCard(((TBIALSession) getSession()).getUser().getId(), card);
  }

  public void playCard(int to, Card card) {
    //TODO implementation
    game.playCard(((TBIALSession) getSession()).getUser().getId(), to, card);
  }

  public void defendCard(Card card) {
    //TODO implementation
    game.defendCard(((TBIALSession) getSession()).getUser().getId(), card);
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


  public class GameViewListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("PlayerAdded")) {
        Game g = (Game) event.getOldValue();
        if (g.getName().equals(game.getName()) && game.isGamePaused() && game.getActivePlayers() == game.getNumPlayers()) {
          game.setGamePaused(false);
          WebSocketManager.getInstance().sendMessage(continueGameJSONMessage(((TBIALSession) getSession()).getUser().getId(), game.getId()));
        }
      } else if (event.getPropertyName().equals("SendMessage") && user.getId() == game.getHost().getId()) {
        JSONMessage message = (JSONMessage) event.getOldValue();
        List<User> players = (List<User>) event.getNewValue();
        sendMessage(message, players);

      } else if (event.getPropertyName().equals("SendPrivateMessage")) {
        JSONMessage message = (JSONMessage) event.getOldValue();
        int playerID = (int) event.getNewValue();
        if (user.getId() == playerID)
          sendPrivateMessage(message, playerID);
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

  public static void sendPrivateMessage(JSONMessage message, int playerID) {
    WebSocketManager.getInstance().sendPrivateMessage(message, playerID);
  }

  public static void sendMessage(JSONMessage message, List<User> users) {
    for (User user : users) {
      if (user != null) {
        WebSocketManager.getInstance().sendPrivateMessage(message, user.getId());
      }
    }
  }

}