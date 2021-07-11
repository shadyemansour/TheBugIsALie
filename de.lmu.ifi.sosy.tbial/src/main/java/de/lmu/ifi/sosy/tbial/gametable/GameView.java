package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.db.*;
import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.networking.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static de.lmu.ifi.sosy.tbial.TBIALApplication.getDatabase;

public abstract class GameView extends WebPage {
  /**
   * UID for serialization.
   */
  private static final long serialVersionUID = 1L;
  public User user = ((TBIALSession) getSession()).getUser();
  AjaxButton leaveButton;
  protected Game game = user.getGame();
  int currentPlayerId = -1;
  List<Card> stackTest = game.getStack();
  public List<User> playerList = game.getPlayers();
  public List<User> actualPlayerlist = new ArrayList<User>();

  public List<Integer> healthList = new ArrayList<Integer>(playerList.size());

  Model<String> p1healthModel = Model.of("4");
  int p1health = -1, p2health = -1, p3health = -1, p4health = -1;

  public List<Card> p1hand = new ArrayList<Card>();
  public List<Card> p2hand = new ArrayList<Card>();
  public List<Card> p3hand = new ArrayList<Card>();
  public List<Card> p4hand = new ArrayList<Card>();

  public List<Card> p1drophand = new ArrayList<Card>();
  public List<Card> p2drophand = new ArrayList<Card>();
  public List<Card> p3drophand = new ArrayList<Card>();
  public List<Card> p4drophand = new ArrayList<Card>();

  List<Card> p1role = new ArrayList<Card>();
  List<Card> p2role = new ArrayList<Card>();
  List<Card> p3role = new ArrayList<Card>();
  List<Card> p4role = new ArrayList<Card>();
  
  List<Card> p1character = new ArrayList<Card>();
  List<Card> p2character = new ArrayList<Card>();
  List<Card> p3character = new ArrayList<Card>();
  List<Card> p4character = new ArrayList<Card>();
  

  List<Card> stackList = new ArrayList<Card>();
  List<Card> heapList = new ArrayList<Card>();

  //  ModalWindow modalWindow;
  Form<?> form;

  public GameView() {
    setPlayerList();
    this.game.addPropertyChangeListener(new GameViewListener());

    //   System.out.println("GameView init" + game + " " + user);
//    Card testCard = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, true, null);

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
        WebSocketManager.getInstance().sendMessage(gamePausedJSONMessage(((TBIALSession) getSession()).getUser().getId(), game.getGameId()));
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
      game.start();
    }
  }

  protected void setPlayerList() {
    int pos = 2;
    for (int i = 0; i < playerList.size(); i++) {
      if (playerList.get(i).getId() == user.getId()) {
        pos = i;
      }
    }
    switch (pos) {
      case 0:
        actualPlayerlist.add(playerList.get(2));
        actualPlayerlist.add(playerList.get(3));
        actualPlayerlist.add(playerList.get(0));
        actualPlayerlist.add(playerList.get(1));
        break;
      case 1:
        actualPlayerlist.add(playerList.get(3));
        actualPlayerlist.add(playerList.get(0));
        actualPlayerlist.add(playerList.get(1));
        actualPlayerlist.add(playerList.get(2));
        break;
      case 2:
        actualPlayerlist = playerList;
        break;
      case 3:
        actualPlayerlist.add(playerList.get(1));
        actualPlayerlist.add(playerList.get(2));
        actualPlayerlist.add(playerList.get(3));
        actualPlayerlist.add(playerList.get(0));
        break;
    }
  }

  public void handleMessage(JSONMessage message) {
    JSONObject jsonMsg = message.getMessage();
    String msgType = (String) jsonMsg.get("msgType");
    Iterator<Object> iterator;
    JSONObject body = jsonMsg.getJSONObject("msgBody");
//    System.out.println(body);
//    System.out.println("playerList: " + playerList);
    int gameID = (int) body.get("gameID");
    int userID;
    System.out.println("-m to: " + user + " " + msgType + " with gameId: " + gameID);
    System.out.println(game.getGameId());
    int playerId = -1;
    try {
      playerId = body.getInt("playerID");
    } catch (JSONException e) {
//    	e.printStackTrace();
    }
    int playerPos = 0;

    if (gameID == game.getGameId()) {
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
                  p1role.add(roleCards.get(2).getTitle().equals("Manager") ? roleCards.get(2) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  p2role.add(roleCards.get(3).getTitle().equals("Manager") ? roleCards.get(3) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  p3role.add(roleCards.get(0));
                  p4role.add(roleCards.get(1).getTitle().equals("Manager") ? roleCards.get(1) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  break;
                case 1:
                  p1role.add(roleCards.get(3).getTitle().equals("Manager") ? roleCards.get(3) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  p2role.add(roleCards.get(0).getTitle().equals("Manager") ? roleCards.get(0) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  p3role.add(roleCards.get(1));
                  p4role.add(roleCards.get(2).getTitle().equals("Manager") ? roleCards.get(2) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  break;
                case 2:
                  p1role.add(roleCards.get(0).getTitle().equals("Manager") ? roleCards.get(0) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  p2role.add(roleCards.get(1).getTitle().equals("Manager") ? roleCards.get(1) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  p3role.add(roleCards.get(2));
                  p4role.add(roleCards.get(3).getTitle().equals("Manager") ? roleCards.get(3) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  break;
                case 3:
                  p1role.add(roleCards.get(1).getTitle().equals("Manager") ? roleCards.get(1) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  p2role.add(roleCards.get(2).getTitle().equals("Manager") ? roleCards.get(2) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  p3role.add(roleCards.get(3));
                  p4role.add(roleCards.get(0).getTitle().equals("Manager") ? roleCards.get(0) : new Card("", "Hidden Role", "", "", "", false, false, ""));
                  break;
              }
            }
          }
          break;
        case "Characters":
          JSONArray characters = (JSONArray) body.get("characters");
          List<Card> characterCards = new ArrayList<Card>();
          for (int i = 0; i < characters.length(); i++) {
            JSONObject container = (JSONObject) characters.get(i);
            
            Card characterCard = (Card) container.get("characterCard");
            characterCards.add(characterCard);
            
            int playerID = container.getInt("playerID");
            if (playerID == user.getId()) {
              String character = container.getString("character");
              int health = container.getInt("health");
              user.setHealth(health);
              user.setCharacter(character);
              //TODO USE THE DATA
            } else if (playerID == game.getPlayers().get(i).getId()) {
              int health = container.getInt("health");
              game.getPlayers().get(i).setHealth(health);
            }
          }
          if (allUsersHaveHealth()) {
            updatePlayerAttributes();
          }
          
          for (int i = 0; i < playerList.size(); i++) {
        	  if (playerList.get(i).getId() == user.getId()) {
        		  switch (i) {
        		  case 0:
                      p1character.add(characterCards.get(2));
                      p2character.add(characterCards.get(3));
                      p3character.add(characterCards.get(0));
                      p4character.add(characterCards.get(1));
                      break;
        		  case 1:
                      p1character.add(characterCards.get(3));
                      p2character.add(characterCards.get(0));
                      p3character.add(characterCards.get(1));
                      p4character.add(characterCards.get(2));
                      break;
        		  case 2:
                      p1character.add(characterCards.get(0));
                      p2character.add(characterCards.get(1));
                      p3character.add(characterCards.get(2));
                      p4character.add(characterCards.get(3));
                      break;
        		  case 3:
                      p1character.add(characterCards.get(1));
                      p2character.add(characterCards.get(2));
                      p3character.add(characterCards.get(3));
                      p4character.add(characterCards.get(0));
                      break;
        		  }
        	  }
          }
          break;
        case "CurrentPlayer":
          currentPlayerId = playerId;
          //TODO USE THE DATA
          for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getId() == playerId) {
              playerPos = i;
            }
          }
          for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getId() == user.getId()) {
              switch (i) {
                case 0:
                  visualizeCurrentPlayer((playerPos + 3) % 4);
                  break;
                case 1:
                  visualizeCurrentPlayer((playerPos + 2) % 4);
                  break;
                case 2:
                  visualizeCurrentPlayer((playerPos + 1) % 4);
                  break;
                case 3:
                  visualizeCurrentPlayer(playerPos % 4);
                  break;
              }
            }
          }
          break;
        case "Shuffle":
          int numCardsInDeck = (int) body.get("cardsInDeck");
          int numCardsInHeap = (int) body.get("cardsInHeap");
          heapList.clear();
          for (int j = 0; j < numCardsInDeck; j++) {
            Card stackCard = new Card("", "Stack Card", "", "", "", false, false, "");
            stackList.add(stackCard);
          }
          break;
        case "GameWon":
          JSONArray idsJSON = (JSONArray) body.get("playerIDs");
          for (int i = 0; i < idsJSON.length(); i++) {
            int winner = (int) idsJSON.get(i);
            //TODO USE THE DATA
          }
          List<Card> roleCardsWon = new ArrayList<Card>();
          JSONArray roleCardsJSON = (JSONArray) body.get("roleCards");
          for (int i = 0; i < roleCardsJSON.length(); i++) {
            JSONObject container = (JSONObject) roleCardsJSON.get(i);
            Card roleCard = (Card) container.get("roleCard");
            roleCard.setVisible(true);
            roleCardsWon.add(roleCard);
          }
          System.out.println("roleJSON" + roleCardsJSON);
          System.out.println("roleCards" + roleCardsWon);
          for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getId() == user.getId()) {
              System.out.println("assign roles for: " + user);
              p1role.clear();
              p2role.clear();
              p3role.clear();
              p4role.clear();
              switch (i) {
                case 0:
                  p1role.add(roleCardsWon.get(2));
                  p2role.add(roleCardsWon.get(3));
                  p3role.add(roleCardsWon.get(0));
                  p4role.add(roleCardsWon.get(1));
                  break;
                case 1:
                  p1role.add(roleCardsWon.get(3));
                  p2role.add(roleCardsWon.get(0));
                  p3role.add(roleCardsWon.get(1));
                  p4role.add(roleCardsWon.get(2));
                  break;
                case 2:
                  p1role.add(roleCardsWon.get(0));
                  p2role.add(roleCardsWon.get(1));
                  p3role.add(roleCardsWon.get(2));
                  p4role.add(roleCardsWon.get(3));
                  break;
                case 3:
                  p1role.add(roleCardsWon.get(1));
                  p2role.add(roleCardsWon.get(2));
                  p3role.add(roleCardsWon.get(3));
                  p4role.add(roleCardsWon.get(0));
                  break;
              }
            }
          }
          // all roles are visible for all players
          //TODO display a message with the winner and that the game has ended
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
          int numCards = body.getInt("cards");
          int numDeckCards = body.getInt("cardsInDeck");
          //TODO USE THE DATA
          List<Card> cardsDrawn = new ArrayList<Card>();
          for (int j = 0; j < numCards; j++) {
            Card hiddenCard = new Card("", "Hidden Card", "", "", "", false, false, "");
            cardsDrawn.add(hiddenCard);
          }
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
            Card stackCard = new Card("", "Stack Card", "", "", "", false, false, "");
            stackList.add(stackCard);
          }
          break;
        case "CardPlayed":
          int from = body.getInt("from");
          int to = body.getInt("to");
          Card car = (Card) body.get("card");
          if (to != from && to == user.getId()) {
            user.setBeingAttacked(true);
          }
          updatePlayerHands(from, car);
          for (int i = 0; i < actualPlayerlist.size(); i++) {
            if (to == actualPlayerlist.get(i).getId()) {
              switch (i) {
                case 0:
                  p1drophand.add(car);
                  break;
                case 1:
                  p2drophand.add(car);
                  break;
                case 2:
                  p3drophand.add(car);
                  break;
                case 3:
                  p4drophand.add(car);
                  break;
              }
            }
          }
          if (to != from && to == user.getId() && user.hasDelegation()) {
            Card delegation = p3drophand.get(0);
            Random random = new Random();
            double r = random.nextDouble();
            System.out.println("random number: " + r);
            if (r < 0.25) {
              cardDelegated(car, delegation);
              user.setBeingAttacked(false);
            } else {
              user.setHasDelegation(false);
              discardCard(delegation, "drop");
            }

          }
          break;
        case "CardDelegated":
          int playerr = body.getInt("playerID");
          System.out.println(playerr);
          Card c = (Card) body.get("card");
          Card delegation = (Card) body.get("delegationCard");
          System.out.println(c.getTitle());
          updatePlayersDropAreas(playerr, delegation);
          updatePlayersDropAreas(playerr, c);
          heapList.add(delegation);
          heapList.add(c);
          break;
        case "CardDiscarded":
          int player = body.getInt("playerID");
          Card card = (Card) body.get("card");
          boolean discardedFromHand = ((String) body.get("discardedFrom")).equals("hand");
          if (discardedFromHand) {
            updatePlayerHands(player, card);
          } else {
            updatePlayersDropAreas(player, card);
          }
          heapList.add(card);
          break;
        case "CardDefended":
          int pl = body.getInt("playerID");
          Card excuseCard = (Card) body.get("defendedWith");
          Card ca = (Card) body.get("card");
          updatePlayerHands(pl, excuseCard);
          updatePlayersDropAreas(pl, ca);
          heapList.add(excuseCard);
          heapList.add(ca);

          break;
        case "Health":
          int p = body.getInt("playerID");
          int health = body.getInt("health");
          updateHealth();
          break;

        case "PlayerFired":
          int playID = (int) body.get("playerID");
          Card role = (Card) body.get("role");
          // player card is set visible
          int playerPosition = 0;
          for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getId() == playID) {
              playerPosition = i;
            }
          }
          for (int i = 0; i < playerList.size(); i++) {
            if (playerList.get(i).getId() == user.getId()) {
              switch (i) {
                case 0:
                  switch (playerPosition) {
                    case 1:
                      p4role.clear();
                      p4role.add(role);
                      p4hand.clear();
                      playerList.get(1).setHand(p4hand);
                      p4drophand.clear();
                      break;
                    case 2:
                      p1role.clear();
                      p1role.add(role);
                      p1hand.clear();
                      playerList.get(2).setHand(p1hand);
                      p1drophand.clear();
                      break;
                    case 3:
                      p2role.clear();
                      p2role.add(role);
                      p2hand.clear();
                      playerList.get(3).setHand(p2hand);
                      p2drophand.clear();
                      break;
                  }
                  break;
                case 1:
                  switch (playerPosition) {
                    case 0:
                      p2role.clear();
                      p2role.add(role);
                      p2hand.clear();
                      playerList.get(0).setHand(p2hand);
                      p2drophand.clear();
                      break;
                    case 2:
                      p4role.clear();
                      p4role.add(role);
                      p4hand.clear();
                      playerList.get(2).setHand(p4hand);
                      p4drophand.clear();
                      break;
                    case 3:
                      p1role.clear();
                      p1role.add(role);
                      p1hand.clear();
                      playerList.get(3).setHand(p1hand);
                      p1drophand.clear();
                      break;
                  }
                  break;
                case 2:
                  switch (playerPosition) {
                    case 0:
                      p1role.clear();
                      p1role.add(role);
                      p1hand.clear();
                      playerList.get(0).setHand(p1hand);
                      p1drophand.clear();
                      break;
                    case 1:
                      p2role.clear();
                      p2role.add(role);
                      p2hand.clear();
                      playerList.get(1).setHand(p2hand);
                      p2drophand.clear();
                      break;
                    case 3:
                      p4role.clear();
                      p4role.add(role);
                      p4hand.clear();
                      playerList.get(3).setHand(p4hand);
                      p4drophand.clear();
                      break;
                  }
                  break;
                case 3:
                  switch (playerPosition) {
                    case 0:
                      p4role.clear();
                      p4role.add(role);
                      p4hand.clear();
                      playerList.get(0).setHand(p4hand);
                      p4drophand.clear();
                      break;
                    case 1:
                      p1role.clear();
                      p1role.add(role);
                      p1hand.clear();
                      playerList.get(1).setHand(p1hand);
                      p1drophand.clear();
                      break;
                    case 2:
                      p2role.clear();
                      p2role.add(role);
                      p2hand.clear();
                      playerList.get(2).setHand(p2hand);
                      p2drophand.clear();
                      break;
                  }
                  break;
              }
            }
          }
          if (playID == user.getId()) {
            p3hand.clear();
            user.setHand(p3hand);
          }
          break;
      }

    }
  }

  private void updatePlayersDropAreas(int pl, Card ca) {  //TODO BUG?
    for (int i = 0; i < actualPlayerlist.size(); i++) {
      if (pl == actualPlayerlist.get(i).getId()) {
        switch (i) {
          case 0:
            System.out.println("p1 drophand before remove: " + p1drophand);
            p1drophand.remove(ca);
            break;
          case 1:
            System.out.println("p2 drophand before remove: " + p2drophand);
            p2drophand.remove(ca);
            break;
          case 2:
            System.out.println("p3 drophand before remove: " + p3drophand);
            p3drophand.remove(ca);
            break;
          case 3:
            System.out.println("p4 drophand before remove: " + p4drophand);
            p4drophand.remove(ca);
            break;
        }
      }
    }
  }

  private void updatePlayerHands(int from, Card car) {
    for (int i = 0; i < actualPlayerlist.size(); i++) {
      if (from == actualPlayerlist.get(i).getId()) {
        switch (i) {
          case 0:
            if (from == user.getId()) {
              for (int j = 0; j < p1hand.size(); j++) {
                if (p1hand.get(j) == car) {
                  p1hand.remove(j);
                }
              }
            } else {
              p1hand.remove(0);
            }
            removeCardFromHand(from, car);
            break;
          case 1:
            if (from == user.getId()) {
              for (int j = 0; j < p2hand.size(); j++) {
                if (p2hand.get(j) == car) {
                  p2hand.remove(j);
                }
              }
            } else {
              p2hand.remove(0);
            }
            removeCardFromHand(from, car);
            break;
          case 2:
            if (from == user.getId()) {
              for (int j = 0; j < p3hand.size(); j++) {
                if (p3hand.get(j) == car) {
                  p3hand.remove(j);
                }
              }
            } else {
              p3hand.remove(0);
            }
            removeCardFromHand(from, car);
            break;
          case 3:
            if (from == user.getId()) {
              for (int j = 0; j < p4hand.size(); j++) {
                if (p4hand.get(j) == car) {
                  p4hand.remove(j);
                }
              }
            } else {
              p4hand.remove(0);
            }
            removeCardFromHand(from, car);
            break;
        }
      }
    }
  }

  public void removeCardFromHand(int from, Card car) {
    for (int k = 0; k < playerList.size(); k++) {
      if (from == playerList.get(k).getId()) {
        List<Card> tmp = playerList.get(k).getHand();
        for (int l = 0; l < tmp.size(); l++) {
          if (tmp.get(l) == car) {
            tmp.remove(l);
            playerList.get(k).setHand(tmp);
          }
        }
      }
    }
  }

  private boolean allUsersHaveHealth() {
    for (User user : game.getPlayers()) {
      if (user.getHealth() == -1) {
        System.out.println(((TBIALSession) getSession()).getUser().getId() + ": false");
        return false;
      }
    }
    System.out.println(((TBIALSession) getSession()).getUser().getId() + ": true");
    return true;
  }

  protected abstract void updatePlayerAttributes();

  protected abstract void visualizeCurrentPlayer(int position);

  protected abstract void updateHealth();

  public void drawCards() {
    game.drawCards(((TBIALSession) getSession()).getUser().getId(), 2 /*TODO CHANGE TO VARIABLE*/);
  }

  public void discardCard(Card card, String discardFrom) {
    //TODO
    if (user.isMyTurn() || user.isBeingAttacked()) {
//      if (discardFrom.equals("hand")) {
//        user.getHand().remove(card);
//      }
      game.discardCard(((TBIALSession) getSession()).getUser().getId(), card, discardFrom);
    } else {
      //TODO message on ui?
    }
  }

  public void cardDelegated(Card card, Card delegation) {
    game.cardDelegated(card, delegation, user.getId());

  }

  public void playCard(int to, Card card) {
    //TODO implementation
    game.playCard(((TBIALSession) getSession()).getUser().getId(), to, card);
  }

  public void defendCard(Card excuseCard, Card bugCard) {
    if (user.isBeingAttacked()) {
      game.defendCard(((TBIALSession) getSession()).getUser().getId(), excuseCard, bugCard);
      user.getHand().remove(excuseCard);
      user.setBeingAttacked(false);
      //game.discardCard(((TBIALSession) getSession()).getUser().getId(), p3drophand.get(0));
      //TODO
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


  public class GameViewListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("PlayerAdded")) {
        Game g = (Game) event.getOldValue();
        if (g.getGameName().equals(game.getGameName()) && game.isGamePaused() && game.getActivePlayers() == game.getNumPlayers()) {
          game.setGamePaused(false);
          WebSocketManager.getInstance().sendMessage(continueGameJSONMessage(((TBIALSession) getSession()).getUser().getId(), game.getGameId()));
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
      } else if (event.getPropertyName().equals("UpdatePlayerAttributes")) {
        int gameId = (int) event.getOldValue();
        if (user.getGame().getGameId() == gameId && user.getId() != game.getHost().getId())
          updateHealth();
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

  void endTurn() {
    System.out.println(user.getHand().size());
    if (user.getHealth() >= user.getHand().size()) {
      game.endTurn();

    }
  }

  public Game getGame() {
    return game;
  }
}