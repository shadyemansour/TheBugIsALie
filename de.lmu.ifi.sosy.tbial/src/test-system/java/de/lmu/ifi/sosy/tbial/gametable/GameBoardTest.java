package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.db.*;

import de.lmu.ifi.sosy.tbial.networking.JSONMessage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

public class GameBoardTest extends PageTestBase {
  Game game;
  GameView gameView;

  User host;
  User player2;
  User player3;
  User player4;
  User player5;
  User player6;
  User player7;

  WebMarkupContainer tabs; //TODO DOUBLE CHECK AFTER MERGE

  int hostPosition = 0;
  int p2Position = 0;
  int p3Position = 0;
  int p4Position = 0;

  @Before
  public void setUp() {
    setupApplication();
    host = database.register("testhost", "testpassword");

    loginUser();

    AjaxTabbedPanel tabbedPanel = (AjaxTabbedPanel) tester.getComponentFromLastRenderedPage("tabs");
    tabs = (WebMarkupContainer) tabbedPanel.get("tabs-container:tabs");


    player2 = database.register("test2", "test2pw");
    player3 = database.register("test3", "test3pw");
    player4 = database.register("test4", "test4pw");
    player5 = database.register("test5", "test5pw");
    player6 = database.register("test6", "test6pw");
    player7 = database.register("test7", "test7pw");

  }

  @Test
  public void renderFourBoard() {
    game = database.createGame("four", host.getName(), "", "new", 4);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(host);

    creatingGame("four", 0);
    attemptLogout();
    attemptLogin("test2", "test2pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test3", "test3pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test4", "test4pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("testhost", "testpassword");

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.startPage(FourBoard.class);
    tester.assertRenderedPage(FourBoard.class);
    gameView = (GameView) tester.getLastRenderedPage();
//
//    tester.assertLabel("p1", "test3");
//    tester.assertLabel("p1heal", Integer.toString(player3.getHealth()));
//    tester.assertLabel("p1pres", "0");
//
//    tester.assertLabel("p2", "test4");
//    tester.assertLabel("p2heal", Integer.toString(player4.getHealth()));
//    tester.assertLabel("p2pres", "0");
//
//    tester.assertLabel("p3", "testhost");
//    tester.assertLabel("p3heal", Integer.toString(host.getHealth()));
//    tester.assertLabel("p3pres", "0");
//
//    tester.assertLabel("p4", "test2");
//    tester.assertLabel("p4heal", Integer.toString(player2.getHealth()));
//    tester.assertLabel("p4pres", "0");

  }

  @Test
  public void dropCard() {
    renderFourBoard();
    game.startGame();

    JSONArray jsonArray = new JSONArray();
    for (int a = 0; a < host.getHand().size(); a++) {
      jsonArray.put(host.getHand().get(a));
    }
    JSONObject msgBodyHost = new JSONObject();
    msgBodyHost.put("gameID", 1);
    msgBodyHost.put("cards", jsonArray);
    JSONObject msgObjectHost = new JSONObject();
    msgObjectHost.put("msgType", "YourCards");
    msgObjectHost.put("msgBody", msgBodyHost);
    JSONMessage msgHost = new JSONMessage(msgObjectHost);
    gameView.handleMessage(msgHost);

    String cardhand3 = "p3-container:player-card-container3:playable-cards-container3:card-hand3";
    assertEquals(host.getHand().size(), ((ListView) tester.getComponentFromLastRenderedPage(cardhand3)).getViewSize());

    Card bugCard = null;
    int bugPosition = 0;
    for (int i = 0; i < host.getHand().size(); i++) {
      if (host.getHand().get(i).getSubTitle() == "--bug--") {
        bugCard = host.getHand().get(i);
        bugPosition = i;
        break;
      }
    }
    List<Card> cardHand = ((ListView) tester.getComponentFromLastRenderedPage("p3-container:player-card-container3:playable-cards-container3:card-hand3")).getList();
    int hostHandSize = host.getHand().size();
    if (bugCard != null) {
      assertEquals(bugCard, cardHand.get(bugPosition));
      JSONObject msgBody = new JSONObject();
      msgBody.put("gameID", 1);
      msgBody.put("from", host.getId());
      msgBody.put("to", player3.getId());
      msgBody.put("card", bugCard);
      JSONObject msgObject = new JSONObject();
      msgObject.put("msgType", "CardPlayed");
      msgObject.put("msgBody", msgBody);
      JSONMessage msg = new JSONMessage(msgObject);
      gameView.handleMessage(msg);

      assertThat(host.getHand().size(), is(hostHandSize - 1));
      assertEquals(host.getHand().size(), ((ListView) tester.getComponentFromLastRenderedPage(cardhand3)).getViewSize());

      String droparea1 = "p1-container:player-card-container1:playable-cards-container1:card-drop-area1";
      assertEquals(1, ((ListView) tester.getComponentFromLastRenderedPage(droparea1)).getViewSize());

      msgBody.put("to", player2.getId());
      gameView.handleMessage(msg);
      String droparea4 = "p4-container:player-card-container4:playable-cards-container4:card-drop-area4";
      assertEquals(1, ((ListView) tester.getComponentFromLastRenderedPage(droparea4)).getViewSize());

      msgBody.put("to", player4.getId());
      gameView.handleMessage(msg);
      String droparea2 = "p2-container:player-card-container2:playable-cards-container2:card-drop-area2";
      assertEquals(1, ((ListView) tester.getComponentFromLastRenderedPage(droparea2)).getViewSize());
    } else {
      assertThat(host.getHand().size(), is(hostHandSize));
      assertEquals(host.getHand().size(), ((ListView) tester.getComponentFromLastRenderedPage(cardhand3)).getViewSize());

      String droparea1 = "p1-container:player-card-container1:playable-cards-container1:card-drop-area1";
      assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage(droparea1)).getViewSize());
    }
  }

  @Test
  public void defendCard() {
	renderFourBoard();
	game.startGame();

	Card bugCard = new Card("Action", "Nullpointer!", "--bug--", null, "-1 mental health", true, false, null);
	gameView.p3drophand.add(bugCard);

	Card defendCard = null;
	for (int i = 0; i < host.getHand().size(); i++) {
	  if (host.getHand().get(i).getSubTitle() == "--lame excuse--" || host.getHand().get(i).getSubTitle() == "--Solution--") {
		defendCard = host.getHand().get(i);
	  }
	}

	assertEquals(0, gameView.heapList.size());
	int hostHandSize = host.getHand().size();

	if (defendCard != null) {
	  JSONObject body = new JSONObject();
	  body.put("gameID", 1);
	  body.put("playerID", host.getId());
	  body.put("card", bugCard);
	  body.put("defendedWith", defendCard);
	  JSONObject msg = new JSONObject();
	  msg.put("msgType", "CardDefended");
	  msg.put("msgBody", body);
	  JSONMessage expected = new JSONMessage(msg);
	  gameView.handleMessage(expected);

	  assertEquals(2, gameView.heapList.size());
	  assertEquals(host.getHand().size(), hostHandSize-1);
	  assertEquals(0, gameView.p3drophand.size());
	}
  }

  @Test
  public void delegateCard() {
	renderFourBoard();
	game.startGame();

	Card bugCard = new Card("Action", "Nullpointer!", "--bug--", null, "-1 mental health", true, false, null);
	Card delCard = new Card("Ability", "Bug Delegation", "", "", "Delegates bug report. \n.25 chance to work", false, false, "");
	gameView.p3drophand.add(delCard);
	assertEquals(1, gameView.p3drophand.size());
	host.setHasDelegation(true);
	gameView.p3drophand.add(bugCard);
	assertEquals(2, gameView.p3drophand.size());

	JSONObject body = new JSONObject();
	body.put("gameID", 1);
	body.put("playerID", host.getId());
	body.put("card", bugCard);
	body.put("delegationCard", delCard);
	JSONObject msg = new JSONObject();
	msg.put("msgType", "CardDelegated");
	msg.put("msgBody", body);
	JSONMessage expected = new JSONMessage(msg);
	gameView.handleMessage(expected);

	assertEquals(2, gameView.heapList.size());
	assertEquals(0, gameView.p3drophand.size());
  }

  @Test
  public void samedroppedCard() {
    renderFourBoard();
    game.startGame();

    Card bugCard = null;
    for (int i = 0; i < host.getHand().size(); i++) {
      if (host.getHand().get(i).getSubTitle() == "--bug--") {
        bugCard = host.getHand().get(i);
        break;
      }
    }

    if (bugCard != null) {
      JSONObject msgBody = new JSONObject();
      msgBody.put("gameID", 1);
      msgBody.put("from", host.getId());
      msgBody.put("to", player3.getId());
      msgBody.put("card", bugCard);
      JSONObject msgObject = new JSONObject();
      msgObject.put("msgType", "CardPlayed");
      msgObject.put("msgBody", msgBody);
      JSONMessage msg = new JSONMessage(msgObject);
      gameView.handleMessage(msg);

      String droparea1 = "p1-container:player-card-container1:playable-cards-container1:card-drop-area1";
      assertEquals(bugCard, ((ListView) tester.getComponentFromLastRenderedPage(droparea1)).getModelObject().get(0));
    }

  }

  @Test
  public void cardClickable() {
    renderFourBoard();
    game.startGame();

    tester.executeAjaxEvent("p3-container:player-card-container3:playable-cards-container3:card-hand3", "click");
    tester.executeAjaxEvent("p3-container:player-card-container3:playable-cards-container3:card-drop-area3", "click");
    tester.executeAjaxEvent("p1-container:player-card-container1:playable-cards-container1", "click");
    tester.executeAjaxEvent("p2-container:player-card-container2:playable-cards-container2", "click");
    tester.executeAjaxEvent("p3-container:player-card-container3:playable-cards-container3", "click");
    tester.executeAjaxEvent("p4-container:player-card-container4:playable-cards-container4", "click");
  }

  @Test
  public void testActualPlayerList() {
    renderFourBoard();

    setPositionForList();

    assertThat(p3Position, is(0));
    assertThat(p4Position, is(1));
    assertThat(hostPosition, is(2));
    assertThat(p2Position, is(3));

    resetPosition();
    setListOption1();
    setPositionForList();

    assertThat(p3Position, is(0));
    assertThat(p4Position, is(1));
    assertThat(hostPosition, is(2));
    assertThat(p2Position, is(3));

    resetPosition();
    setListOption2();
    setPositionForList();

    assertThat(p3Position, is(0));
    assertThat(p4Position, is(1));
    assertThat(hostPosition, is(2));
    assertThat(p2Position, is(3));

    resetPosition();
    setListOption3();
    setPositionForList();

    assertThat(p3Position, is(0));
    assertThat(p4Position, is(1));
    assertThat(hostPosition, is(2));
    assertThat(p2Position, is(3));
  }

  public void setPositionForList() {
	for (int i = 0; i < gameView.actualPlayerlist.size(); i++) {
	  if (gameView.actualPlayerlist.get(i).getId() == host.getId()) {
	    hostPosition = i;
	  } else if (gameView.actualPlayerlist.get(i).getId() == player2.getId()) {
	    p2Position = i;
	  } else if (gameView.actualPlayerlist.get(i).getId() == player3.getId()) {
	    p3Position = i;
	  } else {
	    p4Position = i;
	  }
	}
  }

  public void resetPosition() {
	hostPosition = 0;
	p2Position = 0;
	p3Position = 0;
	p4Position = 0;
  }

  public void setListOption1() {
	List<User> newlist = new ArrayList<User>();
	newlist.add(game.getPlayers().get(1));
	newlist.add(game.getPlayers().get(2));
	newlist.add(game.getPlayers().get(3));
	newlist.add(game.getPlayers().get(0));
	game.setPlayers(newlist);
	gameView.playerList = game.getPlayers();
	gameView.actualPlayerlist = new ArrayList<User>();
	gameView.setPlayerList();
	System.out.println("LIST-OPTION-1");
	System.out.println(gameView.playerList);
  }

  public void setListOption2() {
	List<User> newlist = new ArrayList<User>();
	newlist.add(game.getPlayers().get(2));
	newlist.add(game.getPlayers().get(3));
	newlist.add(game.getPlayers().get(0));
	newlist.add(game.getPlayers().get(1));
	game.setPlayers(newlist);
	gameView.playerList = game.getPlayers();
	gameView.actualPlayerlist = new ArrayList<User>();
	gameView.setPlayerList();
	System.out.println("LIST-OPTION-2");
	System.out.println(gameView.playerList);
  }

  public void setListOption3() {
	List<User> newlist = new ArrayList<User>();
	newlist.add(game.getPlayers().get(3));
	newlist.add(game.getPlayers().get(0));
	newlist.add(game.getPlayers().get(1));
	newlist.add(game.getPlayers().get(2));
	game.setPlayers(newlist);
	gameView.playerList = game.getPlayers();
	gameView.actualPlayerlist = new ArrayList<User>();
	gameView.setPlayerList();
	System.out.println("LIST-OPTION-3");
	System.out.println(gameView.playerList);
  }

  @Test
  public void emptyHandAfterFire() {
	renderFourBoard();
	game.startGame();
    Card role3card = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null);

	JSONObject msgBody = new JSONObject();
	msgBody.put("gameID", 1);
	msgBody.put("playerID", player3.getId());
	msgBody.put("role", role3card);
	JSONObject msgObject = new JSONObject();
	msgObject.put("msgType", "PlayerFired");
	msgObject.put("msgBody", msgBody);
	JSONMessage msg = new JSONMessage(msgObject);
	gameView.handleMessage(msg);

    assertEquals(0, player3.getHand().size());
  }

  @Test
  public void cardDropHandSize() {
	renderFourBoard();
	game.startGame();

	JSONArray jsonArray = new JSONArray();
	for (int i = 0; i < host.getHand().size(); i++) {
      jsonArray.put(host.getHand().get(i));
	}
	JSONObject msgBody = new JSONObject();
	msgBody.put("gameID", 1);
	msgBody.put("cards", jsonArray);
	JSONObject msgObject = new JSONObject();
	msgObject.put("msgType", "YourCards");
	msgObject.put("msgBody", msgBody);
	JSONMessage msg = new JSONMessage(msgObject);
	gameView.handleMessage(msg);

	String cardhand3 = "p3-container:player-card-container3:playable-cards-container3:card-hand3";
	assertEquals(host.getHand().size(), ((ListView) tester.getComponentFromLastRenderedPage(cardhand3)).getViewSize());
	assertEquals(host.getHand(), ((ListView) tester.getComponentFromLastRenderedPage(cardhand3)).getModelObject());

	String droparea1 = "p1-container:player-card-container1:playable-cards-container1:card-drop-area1";
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage(droparea1)).getViewSize());
    String droparea2 = "p2-container:player-card-container2:playable-cards-container2:card-drop-area2";
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage(droparea2)).getViewSize());
    String droparea3 = "p3-container:player-card-container3:playable-cards-container3:card-drop-area3";
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage(droparea3)).getViewSize());
    String droparea4 = "p4-container:player-card-container4:playable-cards-container4:card-drop-area4";
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage(droparea4)).getViewSize());
  }

  @Test
  public void renderFiveBoard() {
    game = database.createGame("five", host.getName(), "", "new", 5);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(host);
    game.addPlayer(player5);

    creatingGame("five", 1);
    attemptLogout();
    attemptLogin("test2", "test2pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test3", "test3pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test4", "test4pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test5", "test5pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("testhost", "testpassword");

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.startPage(FiveBoard.class);
    tester.assertRenderedPage(FiveBoard.class);

    tester.assertLabel("p1", "testhost");
    tester.assertLabel("p1heal", Integer.toString(host.getHealth()));
    tester.assertLabel("p1pres", "0");

    tester.assertLabel("p2", "test2");
    tester.assertLabel("p2heal", Integer.toString(player2.getHealth()));
    tester.assertLabel("p2pres", "0");

    tester.assertLabel("p3", "test3");
    tester.assertLabel("p3heal", Integer.toString(player3.getHealth()));
    tester.assertLabel("p3pres", "0");

    tester.assertLabel("p4", "test4");
    tester.assertLabel("p4heal", Integer.toString(player4.getHealth()));
    tester.assertLabel("p4pres", "0");

    tester.assertLabel("p5", "test5");

    tester.assertLabel("p5heal", Integer.toString(player5.getHealth()));
    tester.assertLabel("p5pres", "0");
  }

  @Test
  public void renderSixBoard() {
    game = database.createGame("six", host.getName(), "", "new", 6);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(player5);
    game.addPlayer(player6);
    game.addPlayer(host);

    creatingGame("six", 2);
    attemptLogout();
    attemptLogin("test2", "test2pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test3", "test3pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test4", "test4pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test5", "test5pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test6", "test6pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("testhost", "testpassword");
    joiningGame();

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.startPage(SixBoard.class);
    tester.assertRenderedPage(SixBoard.class);
    tester.assertLabel("p1", "testhost");
    tester.assertLabel("p1heal", Integer.toString(host.getHealth()));
    tester.assertLabel("p1pres", "0");

    tester.assertLabel("p2", "test2");
    tester.assertLabel("p2heal", Integer.toString(player2.getHealth()));
    tester.assertLabel("p2pres", "0");

    tester.assertLabel("p3", "test3");
    tester.assertLabel("p3heal", Integer.toString(player3.getHealth()));
    tester.assertLabel("p3pres", "0");

    tester.assertLabel("p4", "test4");
    tester.assertLabel("p4heal", Integer.toString(player4.getHealth()));
    tester.assertLabel("p4pres", "0");

    tester.assertLabel("p5", "test5");
    tester.assertLabel("p5heal", Integer.toString(player5.getHealth()));
    tester.assertLabel("p5pres", "0");

    tester.assertLabel("p6", "test6");
    tester.assertLabel("p6heal", Integer.toString(player6.getHealth()));
    tester.assertLabel("p6pres", "0");
  }

  @Test
  public void renderSevenBoard() {
    game = database.createGame("seven", host.getName(), "", "new", 7);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(player5);
    game.addPlayer(player6);
    game.addPlayer(player7);
    game.addPlayer(host);

    creatingGame("seven", 3);
    attemptLogout();
    attemptLogin("test2", "test2pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test3", "test3pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test4", "test4pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test5", "test5pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test6", "test6pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("test7", "test7pw");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("testhost", "testpassword");

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.startPage(SevenBoard.class);
    tester.assertRenderedPage(SevenBoard.class);
    tester.assertLabel("p1", "testhost");
    tester.assertLabel("p1heal", Integer.toString(host.getHealth()));
    tester.assertLabel("p1pres", "0");

    tester.assertLabel("p2", "test2");
    tester.assertLabel("p2heal", Integer.toString(player2.getHealth()));
    tester.assertLabel("p2pres", "0");

    tester.assertLabel("p3", "test3");
    tester.assertLabel("p3heal", Integer.toString(player3.getHealth()));
    tester.assertLabel("p3pres", "0");

    tester.assertLabel("p4", "test4");
    tester.assertLabel("p4heal", Integer.toString(player4.getHealth()));
    tester.assertLabel("p4pres", "0");

    tester.assertLabel("p5", "test5");
    tester.assertLabel("p5heal", Integer.toString(player5.getHealth()));
    tester.assertLabel("p5pres", "0");

    tester.assertLabel("p6", "test6");
    tester.assertLabel("p6heal", Integer.toString(player6.getHealth()));
    tester.assertLabel("p6pres", "0");

    tester.assertLabel("p7", "test7");
    tester.assertLabel("p7heal", Integer.toString(player7.getHealth()));
    tester.assertLabel("p7pres", "0");
  }

  public void loginUser() {
    tester.startPage(Login.class);
    tester.assertRenderedPage(Login.class);
    FormTester form = tester.newFormTester("login");
    form.setValue("name", "testhost");
    form.setValue("password", "testpassword");
    form.submit("loginbutton");

    tester.assertRenderedPage(Lobby.class);
  }

  public void startingGame() {
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");
    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    FormTester form = tester.newFormTester("tabs:panel:boxedGameLobby:form");
    form.submit("startbutton");
  }

  public void creatingGame(String name, int num) {
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");
    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    FormTester form = tester.newFormTester("tabs:panel:create");
    form.setValue("name", name);
    form.select("ddc", num);
    form.submit("submitgame");
  }

  public void joiningGame() {
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("1");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");

    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    tester.clickLink("tabs:panel:gamelist:availableGames:0:joinGame");
  }
}