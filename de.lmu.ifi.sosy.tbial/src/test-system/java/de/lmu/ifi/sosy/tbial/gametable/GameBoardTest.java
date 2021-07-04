package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.db.*;

import static de.lmu.ifi.sosy.tbial.TestUtil.hasNameAndPassword;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.util.tester.FormTester;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Before;
import org.junit.Test;

public class GameBoardTest extends PageTestBase {
  Game game;

  User host;
  User player2;
  User player3;
  User player4;
  User player5;
  User player6;
  User player7;

  WebMarkupContainer tabs; //TODO DOUBLE CHECK AFTER MERGE

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

    tester.assertLabel("p1", "test3");
    tester.assertLabel("p1heal", Integer.toString(player3.getHealth()));
    tester.assertLabel("p1pres", "0");

    tester.assertLabel("p2", "test4");
    tester.assertLabel("p2heal", Integer.toString(player4.getHealth()));
    tester.assertLabel("p2pres", "0");

    tester.assertLabel("p3", "testhost");
    tester.assertLabel("p3heal", Integer.toString(host.getHealth()));
    tester.assertLabel("p3pres", "0");

    tester.assertLabel("p4", "test2");
    tester.assertLabel("p4heal", Integer.toString(player2.getHealth()));
    tester.assertLabel("p4pres", "0");
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