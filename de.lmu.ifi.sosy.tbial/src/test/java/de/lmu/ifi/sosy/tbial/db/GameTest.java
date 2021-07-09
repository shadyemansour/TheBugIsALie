package de.lmu.ifi.sosy.tbial.db;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.lmu.ifi.sosy.tbial.networking.JSONMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class GameTest {

  private String password;

  private String name;

  private Integer numPlayers;

  private User host;

  private User user1;

  private User user2;

  private User user3;

  private User user4;

  private Game game;

  private int id;

  @Before
  public void init() {
    password = "pass";
    name = "name";
    id = 42;
    numPlayers = 4;
    host = new User(1, "hostName", "hostPw", null);
    user1 = new User(2, "user1Name", "user1Pw", null);
    user2 = new User(3, "user2Name", "user2Pw", null);
    user3 = new User(4, "user3Name", "user3Pw", null);
    user4 = new User(5, "user4Name", "user4Pw", null);
    game = new Game(id, name, password, numPlayers, "", host.getName());
    game.setHost(host);
    game.addPlayer(host);
    game.addPlayer(user1);
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullNameGiven_throwsException() {
    new Game(id, null, password, numPlayers, null, host.getName());
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullPasswordGiven_throwsException() {
    new Game(id, name, null, numPlayers, null, host.getName());
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullNumPlayersGiven_throwsException() {
    new Game(id, name, password, null, null, host.getName());
  }

//  @Test(expected = NullPointerException.class)
//  public void constructor_whenNullHostGiven_throwsException() {
//    new Game(id, name, password, numPlayers, null);
//  }

  @Test
  public void getPassword_returnsPassword() {
    game.setPassword(password);
    assertThat(game.getPassword(), is(password));
  }

  @Test
  public void getName_returnsName() {
    game.setGameName(name);
    assertThat(game.getGameName(), is(name));
  }

  @Test
  public void getId_returnsId() {
    game.setGameId(id);
    assertThat(game.getGameId(), is(id));
  }

  @Test(expected = NullPointerException.class)
  public void setName_whenNullNameGiven_throwsException() {
    game.setGameName(null);
  }

  @Test(expected = NullPointerException.class)
  public void setNumPlayers_whenNullNumPlayersGiven_throwsException() {
    game.setNumPlayers(null);
  }

  @Test(expected = NullPointerException.class)
  public void setHostName_whenNullHostNameGiven_throwsException() {
    game.setHostName(null);
  }

  @Test
  public void getPlayers_returnPlayers() {
    List<User> expected = new ArrayList<User>(4);
    expected.add(host);
    expected.add(user1);
    expected.add(null);
    expected.add(null);
    assertThat(game.getPlayers(), is(expected));
  }

  @Test
  public void addPlayer_addsPlayer() {
    List<User> expected = new ArrayList<User>(4);
    expected.add(host);
    expected.add(user1);
    expected.add(user2);
    expected.add(null);
    game.addPlayer(user2);
    assertThat(game.getPlayers(), is(expected));
  }

  @Test
  public void removePlayer_removesPlayer() {
    List<User> expected = new ArrayList<User>(4);
    expected.add(host);
    expected.add(null);
    expected.add(null);
    expected.add(null);
    game.removePlayer(user1);
    assertThat(game.getPlayers(), is(expected));
  }

  @Test
  public void getGameState_returnReady() {
    String expected = "ready";
    game.addPlayer(user2);
    game.addPlayer(user3);
    game.addPlayer(user4);
    assertThat(game.getGameState(), is(expected));
  }

  @Test
  public void getGameState_return1PlayerMissing() {
    String expected = "1 player missing";
    game.addPlayer(user2);
    assertThat(game.getGameState(), is(expected));
  }

  @Test
  public void getGameState_return1PlayerMissingAfterRemoving() {
    String expected = "1 player missing";
    game.addPlayer(user2);
    game.addPlayer(user3);
    game.removePlayer(user3);
    assertThat(game.getGameState(), is(expected));
  }

  @Test
  public void getGameState_return2PlayersMissing() {
    String expected = "2 players missing";
    assertThat(game.getGameState(), is(expected));
  }

  @Test
  public void getActivePlayers_returns2() {
    int expected = 2;
    assertEquals(game.getActivePlayers(), expected);
  }

  @Test
  public void getActivePlayers_returns4() {
    int expected = 4;
    game.addPlayer(user2);
    game.addPlayer(user3);
    assertEquals(game.getActivePlayers(), expected);
  }

  @Test
  public void decksShuffledMessageTest() {
    game.startGame();
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("cardsInDeck", game.getStack().size());
    body.put("cardsInHeap", 0);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "Shuffle");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.decksShuffledMessage(game.getStack().size(), 0).getMessage(), true);
  }

  @Test
  public void updateHealthMessageTest() {
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("playerID", 1);
    body.put("health", 4);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "Health");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.updateHealthMessage(1, 4).getMessage(), true);
  }

  @Test
  public void gameWonMessageTest() {
    game.startGame();
    JSONArray cardsArray = (JSONArray) game.getRoleCardsHostMessage().getMessage().getJSONObject("msgBody").get("roles");
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("playerIDs", new JSONArray(Collections.singletonList(1)));
    body.put("roleCards", cardsArray);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "GameWon");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    System.out.println(game.gameWonMessage(new JSONArray(Collections.singletonList(1))).getMessage());
    JSONAssert.assertEquals(expected.getMessage(), game.gameWonMessage(new JSONArray(Collections.singletonList(1))).getMessage(), true);
  }

  @Test
  public void cardDefendedMessageTest() {
    Card card = new Card("StumblingBlock", "Fortran \nMaintenance", "BOOM", "Stumbling Block",
        "Only playable on self. \nTakes 3 health points. \n.85 chance to deflect to \nnext developer",
        false, false, null);
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("playerID", 1);
    body.put("card", card);
    body.put("card", card);
    body.put("defendedWith", card);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "CardDefended");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.cardDefendedMessage(1, card, card).getMessage(), true);
  }

  @Test
  public void cardPlayedMessageTest() {
    Card card = new Card("StumblingBlock", "Fortran \nMaintenance", "BOOM", "Stumbling Block",
        "Only playable on self. \nTakes 3 health points. \n.85 chance to deflect to \nnext developer",
        false, false, null);
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("from", 1);
    body.put("to", 2);
    body.put("card", card);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "CardPlayed");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.cardPlayedMessage(1, 2, card).getMessage(), true);
  }

  @Test
  public void discardCardMessageTest() {
    Card card = new Card("StumblingBlock", "Fortran \nMaintenance", "BOOM", "Stumbling Block",
        "Only playable on self. \nTakes 3 health points. \n.85 chance to deflect to \nnext developer",
        false, false, null);
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("playerID", 1);
    body.put("card", card);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "CardDiscarded");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.discardCardMessage(1, card).getMessage(), true);
  }

  @Test
  public void drawCardsMessageTest() {
    game.startGame();
    JSONArray cards = new JSONArray();
    for (int n = 0; n < 2; n++) {
      //TODO Draw cards from stack and save in Array
      cards.put(game.getStack().get(n));
    }

    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("cards", cards);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "YourCards");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONMessage[] actual = game.drawCardsMessage(1, cards);
    JSONAssert.assertEquals(expected.getMessage(), actual[0].getMessage(), true);
    body.put("playerID", 1);
    body.put("cards", 2);
    body.put("cardsInDeck", game.getStack().size());
    msg.put("msgType", "CardsDrawn");
    JSONAssert.assertEquals(expected.getMessage(), actual[1].getMessage(), true);
  }

  @Test
  public void currentPlayerMessageTest() {
    game.addPlayer(user2);
    game.addPlayer(user3);
    game.startGame();
    int expected = -1;
    List<User> players = game.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      User player = players.get(i);
      if (player.getRoleCard().getTitle().equals("Manager")) {
        expected = game.getCurrentID();
        break;
      }
    }
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("playerID", expected);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "CurrentPlayer");
    msg.put("msgBody", body);
    JSONMessage expectedMessage = new JSONMessage(msg);
    JSONAssert.assertEquals(expectedMessage.getMessage(), game.currentPlayerMessage().getMessage(), true);
  }

  @Test
  public void gameStartedMessageTest() {
    game.startGame();
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("cardsInDeck", game.getStack().size());
    body.put("numPlayers", game.getNumPlayers());

    JSONObject msg = new JSONObject();
    msg.put("msgType", "GameStarted");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.gameStartedMessage().getMessage(), true);
  }

  @Test
  public void playersHaveRolesAndCharactersTest() {
    game.addPlayer(user2);
    game.addPlayer(user3);

    game.setRoleCards();
    assertEquals(4, game.getRoleCards().size());
    game.unsetRoleCards();
    game.startGame();
    boolean actual = true;
    List<User> players = game.getPlayers();
    for (User player : players) {
      if (player == null)
        continue;
      if (player.getCharacterCard() == null || player.getRoleCard() == null) {
        actual = false;
        break;
      }
    }
    assertTrue(actual);
    assertEquals(0, game.getRoleCards().size());
  }

  @Test
  public void managerStartsFirst() {
    boolean expected = false;
    game.addPlayer(user2);
    game.addPlayer(user3);

    game.startGame();
    List<User> players = game.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      User player = players.get(i);
      if (player.getRoleCard().getTitle().equals("Manager")) {
        expected = game.getCurrentID() == player.getId() && game.getCurrentPlayer() == i;
        break;
      }
    }
    assertTrue(expected);
  }

  @Test
  public void endTurnTest() {
    game.addPlayer(user2);
    game.addPlayer(user3);

    game.startGame();
    game.start();
    List<User> players = game.getPlayers();
    int first = 0;
    for (int i = 0; i < players.size(); i++) {
      User player = players.get(i);
      if (player.getRoleCard().getTitle().equals("Manager")) {
        first = i;
        break;
      }
    }

    int next = (first == 3 ? 0 : first + 1);
    await().until(() -> game.getIsPlaying());

    game.endTurn();
    await().until(() -> game.getTurn() == 2);
    assertEquals(game.getPlayers().get(next).getId(), game.getCurrentID());
    assertEquals(2, game.getTurn());
    assertEquals(next, game.getCurrentPlayer());
  }

  @Test
  public void waitsForStumblingCardsTest() {
    game.addPlayer(user2);
    game.addPlayer(user3);

    game.startGame();
    List<User> players = game.getPlayers();
    int manager = 0;
    for (int i = 0; i < players.size(); i++) {
      User player = players.get(i);
      if (player.getRoleCard().getTitle().equals("Manager")) {
        manager = i;
        break;
      }
    }
    players.get(manager).setHasStumblingCards(true);
    game.start();

    await().until(() -> game.getIsPlaying());
    players.get(manager).setHasStumblingCards(false);
    game.setIsPlaying(false);
    assertFalse(game.getIsPlaying());
    await().until(() -> game.getIsPlaying());
    assertTrue(game.getIsPlaying());
  }

  @Test
  public void FireManagerAndGameWonTest() {
    game.addPlayer(user2);
    game.addPlayer(user3);

    game.startGame();
    game.start();
    List<User> players = game.getPlayers();
    int managerID = 0;
    for (int i = 0; i < players.size(); i++) {
      User player = players.get(i);
      if (player.getRoleCard().getTitle().equals("Manager")) {
        managerID = player.getId();
        break;
      }
    }
    await().until(() -> game.getIsPlaying());
    game.playerFired(managerID);

    game.endTurn();
    await().until(() -> game.isGameWon());
    assertTrue(game.isManagerFired());
    assertTrue(game.isGameWon());
  }

  @Test
  public void FireEvilMonkeysAndConsultantAndGameWonTest() {
    game.addPlayer(user2);
    game.addPlayer(user3);

    game.startGame();
    game.start();
    List<User> players = game.getPlayers();
    List<Integer> monkeysIDs = new ArrayList<>();
    for (int i = 0; i < players.size(); i++) {
      User player = players.get(i);
      if (player.getRoleCard().getTitle().equals("Evil Code Monkey") || player.getRoleCard().getTitle().equals("Consultant")) {
        monkeysIDs.add(player.getId());
      }
    }
    await().until(() -> game.getIsPlaying());
    for (int id : monkeysIDs) {
      game.playerFired(id);
    }

    game.endTurn();
    await().until(() -> game.isGameWon());
    assertTrue(game.isGameWon());
  }

  @Test
  public void FireEveryoneExceptConsultantAndGameWonTest() {
    game.addPlayer(user2);
    game.addPlayer(user3);

    game.startGame();
    game.start();
    List<User> players = game.getPlayers();
    List<Integer> IDs = new ArrayList<>();
    for (int i = 0; i < players.size(); i++) {
      User player = players.get(i);
      if (!player.getRoleCard().getTitle().equals("Consultant")) {
        IDs.add(player.getId());
      }
    }
    await().until(() -> game.getIsPlaying());
    for (int id : IDs) {
      game.playerFired(id);
    }

    game.endTurn();
    await().until(() -> game.isGameWon());
    assertTrue(game.isGameWon());
  }

  @Test
  public void FireEveryoneExceptHonestAndConsultantEvilMonkeysWinTest() {
    game.addPlayer(user2);
    game.addPlayer(user3);

    game.startGame();
    game.start();
    List<User> players = game.getPlayers();
    List<Integer> IDs = new ArrayList<>();
    for (int i = 0; i < players.size(); i++) {
      User player = players.get(i);
      if (!player.getRoleCard().getTitle().equals("Consultant") || !player.getRoleCard().getTitle().equals("Honest Developer")) {
        IDs.add(player.getId());
      }
    }
    await().until(() -> game.getIsPlaying());
    for (int id : IDs) {
      game.playerFired(id);
    }

    game.endTurn();
    await().until(() -> game.isGameWon());
    assertTrue(game.isGameWon());
  }


}
