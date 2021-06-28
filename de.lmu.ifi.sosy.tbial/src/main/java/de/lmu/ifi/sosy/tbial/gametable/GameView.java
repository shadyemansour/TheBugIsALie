package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.db.*;
import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.networking.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
  protected User user = ((TBIALSession) getSession()).getUser();
  AjaxButton leaveButton;
  protected Game game = user.getGame();
  List<Card> stackTest = game.getStack();
  protected List<User> playerList = game.getPlayers();
  
  List<Card> p1hand = new ArrayList<Card>();
  List<Card> p2hand = new ArrayList<Card>();
  List<Card> p3hand = new ArrayList<Card>();
  List<Card> p4hand = new ArrayList<Card>();
  
  List<Card> p1role = new ArrayList<Card>();
  List<Card> p2role = new ArrayList<Card>();
  List<Card> p3role = new ArrayList<Card>();
  List<Card> p4role = new ArrayList<Card>();
  
  List<Card> stackList = new ArrayList<Card>();
  
  //  ModalWindow modalWindow;
  Form<?> form;
  private GameView instance;

  public GameView() {
    this.game.addPropertyChangeListener(new GameViewListener());

    System.out.println("GameView init" + game + " " + user);
//    Card testCard = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, true, null);
//		p3handModel.add(Model.of(testCard));
//		p3hand.add(testCard);
    setupGame();


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
  
  private void setupGame() {
//  	orderPlayers();
  }

  void handleMessage(JSONMessage message) {
    JSONObject jsonMsg = message.getMessage();
    String msgType = (String) jsonMsg.get("msgType");
    Iterator<Object> iterator;
    JSONObject body = jsonMsg.getJSONObject("msgBody");
    System.out.println("-m to: " + user + " " + msgType);
//    System.out.println(body);
//    System.out.println("playerList: " + playerList);
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
//        	System.out.println("---XXX---game started message received in game view");
          int cardsInDeck = (int) body.get("cardsInDeck");
          int numPlayers = (int) body.get("numPlayers");
          //TODO USE THE DATA
          break;
        case "Roles":
        	System.out.println("roles: " + body);
        	System.out.println("playerList: " + playerList);
          JSONArray roles = (JSONArray) body.get("roles");
          List<Card> roleCards = new ArrayList<Card>();
          for (int i = 0; i < roles.length(); i++) {
          	JSONObject container = (JSONObject) roles.get(i);
          	Card roleCard = (Card) container.get("roleCard");
          	roleCards.add(roleCard);
          }
          for (int i = 0; i < playerList.size(); i++) {
          	if (playerList.get(i).getId() == user.getId()) {
          		System.out.println("assign roles for: " + user);
          		switch (i) {
          		case 0:
          			p1role.add(roleCards.get(2));
          			p2role.add(roleCards.get(3));
          			p3role.add(roleCards.get(0));
          			p4role.add(roleCards.get(1));
          			break;
          		case 1:
          			p1role.add(roleCards.get(3));
          			p2role.add(roleCards.get(0));
          			p3role.add(roleCards.get(1));
          			p4role.add(roleCards.get(2));
          			break;
          		case 2:
          			p1role.add(roleCards.get(0));
          			p2role.add(roleCards.get(1));
          			p3role.add(roleCards.get(2));
          			p4role.add(roleCards.get(3));
          			break;
          		case 3:
          			p1role.add(roleCards.get(1));
          			p2role.add(roleCards.get(2));
          			p3role.add(roleCards.get(3));
          			p4role.add(roleCards.get(0));
          			break;
          		}
          	}
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
        	
          for (int i = 0; i < cardsJSON.length(); i++) {
          	Card card = (Card) cardsJSON.get(i);
          	card.setVisible(true);
            p3hand.add(card);
          }
          break;
        case "CardsDrawn":
          int playerId = body.getInt("playerID");
          int numCards = body.getInt("cards");
          int numDeckCards = body.getInt("cardsInDeck");
          //TODO USE THE DATA
          List<Card> cardsDrawn = new ArrayList<Card>();
          for (int j = 0; j < numCards; j++) {
          	Card hiddenCard = new Card("", "Hidden Card", null, null, null, false, false, null);
          	cardsDrawn.add(hiddenCard);
          }
          int playerPos = 0;
          for (int i = 0; i < playerList.size(); i++) {
          	if (playerList.get(i).getId() == playerId) {
          		playerPos = i;
          	}
          }
          for (int i = 0; i < playerList.size(); i++) {
          	if (playerList.get(i).getId() == user.getId()) {
          		System.out.println("assign drawn cards for: " + user);
          		System.out.println("assign: i=" + i + " playerPos=" + playerPos);
          		switch (i) {
          		case 0:
          			switch (playerPos) {
          			case 1:
          				for (Card card : cardsDrawn) {
            				p4hand.add(card);
          				}
          				break;
          			case 2:
          				for (Card card : cardsDrawn) {
            				p1hand.add(card);
          				}
          				break;
          			case 3:
          				for (Card card : cardsDrawn) {
            				p2hand.add(card);
          				}
          				break;
          			}
          			break;
          		case 1:
          			switch (playerPos) {
          			case 0:
          				for (Card card : cardsDrawn) {
            				p2hand.add(card);
          				}
          				break;
          			case 2:
          				for (Card card : cardsDrawn) {
            				p4hand.add(card);
          				}
          				break;
          			case 3:
          				for (Card card : cardsDrawn) {
            				p1hand.add(card);
          				}
          				break;
          			}
          			break;
          		case 2:
          			switch (playerPos) {
          			case 0:
          				for (Card card : cardsDrawn) {
            				p1hand.add(card);
          				}
          				break;
          			case 1:
          				for (Card card : cardsDrawn) {
            				p2hand.add(card);
          				}
          				break;
          			case 3:
          				for (Card card : cardsDrawn) {
            				p4hand.add(card);
          				}
          				break;
          			}
          			break;
          		case 3:
          			switch (playerPos) {
          			case 0:
          				for (Card card : cardsDrawn) {
            				p4hand.add(card);
          				}
          				break;
          			case 1:
          				for (Card card : cardsDrawn) {
            				p1hand.add(card);
          				}
          				break;
          			case 2:
          				for (Card card : cardsDrawn) {
            				p2hand.add(card);
          				}
          				break;
          			}
          			break;
          		}
          	}
          }
          stackList.clear();
          for (int j = 0; j < numDeckCards; j++) {
          	Card stackCard = new Card("", "Stack Card", null, null, null, false, false, null);
          	stackList.add(stackCard);
          }
          System.out.println("numDeckCards: " + numDeckCards);
          System.out.println("--added to stackList: " + stackList.size());
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
  
	/*
	 * rotates players in List, so that own player is displayed at the bottom
	 */
	private void orderPlayers() {
		int size = playerList.size();
		int bottomPos = 2;
		if (size == 5 || size == 6) {
			bottomPos = 3;
		} else if (size == 7) {
			bottomPos = 4;
		}
		for (int i = 0; i < size; i++) {
			// break loop, if own player is in correct position
			if (playerList.get(bottomPos).equals(user)) {
				break;
			}
			// move players
			User tempUser = playerList.get(size - 1);
			for (int j = size - 1; j > 0; j--) {
				playerList.set(j, playerList.get(j - 1));
			}
			playerList.set(0, tempUser);
		}
		System.out.println("ordered: " + user + " --- " + playerList);
	}

}