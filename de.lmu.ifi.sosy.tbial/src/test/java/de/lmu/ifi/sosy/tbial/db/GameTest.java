package de.lmu.ifi.sosy.tbial.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
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
    numPlayers = 5;
    host = new User("hostName", "hostPw", null);
    user1 = new User("user1Name", "user1Pw", null);
    user2 = new User("user2Name", "user2Pw", null);
    user3 = new User("user3Name", "user3Pw", null);
    user4 = new User("user4Name", "user4Pw", null);
    game = new Game(id, name, password, numPlayers, "", host.getName());
    game.setHost(host);
    game.addPlayer(host);
    game.addPlayer(user1);
    game.startGame();
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
    game.setName(name);
    assertThat(game.getName(), is(name));
  }

  @Test
  public void getId_returnsId() {
    game.setId(id);
    assertThat(game.getId(), is(id));
  }

  @Test(expected = NullPointerException.class)
  public void setName_whenNullNameGiven_throwsException() {
    game.setName(null);
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
    List<User> expected = new ArrayList<User>(5);
    expected.add(host);
    expected.add(user1);
    expected.add(null);
    expected.add(null);
    expected.add(null);
    assertThat(game.getPlayers(), is(expected));
  }

  @Test
  public void addPlayer_addsPlayer() {
    List<User> expected = new ArrayList<User>(5);
    expected.add(host);
    expected.add(user1);
    expected.add(user2);
    expected.add(null);
    expected.add(null);
    game.addPlayer(user2);
    assertThat(game.getPlayers(), is(expected));
  }

  @Test
  public void removePlayer_removesPlayer() {
    List<User> expected = new ArrayList<User>(5);
    expected.add(host);
    expected.add(null);
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
    game.addPlayer(user3);
    assertThat(game.getGameState(), is(expected));
  }

  @Test
  public void getGameState_return1PlayerMissingAfterRemoving() {
    String expected = "1 player missing";
    game.addPlayer(user2);
    game.addPlayer(user3);
    game.addPlayer(user4);
    game.removePlayer(user3);
    assertThat(game.getGameState(), is(expected));
  }

  @Test
  public void getGameState_return2PlayersMissing() {
    String expected = "2 players missing";
    game.addPlayer(user2);
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
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("playerID", 1);

    JSONObject msg = new JSONObject();
    msg.put("msgType", "GameWon");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.gameWonMessage(1).getMessage(), true);
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

    JSONObject msg = new JSONObject();
    msg.put("msgType", "CardDefended");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.cardDefendedMessage(1, card).getMessage(), true);
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
    JSONObject body = new JSONObject();
    body.put("gameID", id);
    body.put("playerID", host.getId());

    JSONObject msg = new JSONObject();
    msg.put("msgType", "CurrentPlayer");
    msg.put("msgBody", body);
    JSONMessage expected = new JSONMessage(msg);
    JSONAssert.assertEquals(expected.getMessage(), game.currentPlayerMessage().getMessage(), true);
  }

  @Test
  public void gameStartedMessageTest() {
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
  }


}
