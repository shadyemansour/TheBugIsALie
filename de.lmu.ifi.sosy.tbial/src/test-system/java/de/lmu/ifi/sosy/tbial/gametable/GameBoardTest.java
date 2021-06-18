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

  WebMarkupContainer tabs;
  private static EmbeddedDataSource dataSource = new EmbeddedDataSource();

  static {
    dataSource.setDatabaseName("tbial_test");
    dataSource.setCreateDatabase("create");
  }

  @Before
  public void setUp() {
    setupApplication();
    database.register("host", "password");
    database.register("play2", "play2");
    database.register("play3", "play3");
    database.register("play4", "play4");
    database.register("play5", "play5");
    database.register("play6", "play6");
    database.register("play7", "play7");
    host = new User("host", "password", null);
    player2 = new User("play2", "play2", null);
    player3 = new User("play3", "play3", null);
    player4 = new User("play4", "play4", null);
    player5 = new User("play5", "play5", null);
    player6 = new User("play6", "play6", null);
    player7 = new User("play7", "play7", null);

    attemptLogin("host", "password");
    tester.assertRenderedPage(Lobby.class);
    AjaxTabbedPanel tabbedPanel = (AjaxTabbedPanel) tester.getComponentFromLastRenderedPage("tabs");
    tabs = (WebMarkupContainer) tabbedPanel.get("tabs-container:tabs");
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
    attemptLogin("play2", "play2");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play3", "play3");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play4", "play4");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("host", "password");
    
    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.assertRenderedPage(FourBoard.class);

    tester.assertLabel("p1", "host");
    tester.assertLabel("p1heal", "4");
    tester.assertLabel("p1pres", "0");
    
    tester.assertLabel("p2", "play2");
    tester.assertLabel("p2heal", "4");
    tester.assertLabel("p2pres", "0");
    
    tester.assertLabel("p3", "play3");
    tester.assertLabel("p3heal", "4");
    tester.assertLabel("p3pres", "0");
    
    tester.assertLabel("p4", "play4");
    tester.assertLabel("p4heal", "4");
    tester.assertLabel("p4pres", "0");
  }

  @Test
  public void renderFiveBoard() {
    tester.assertRenderedPage(Lobby.class);
    game = database.createGame("five", host.getName(), "", "new", 5);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(player5);
    game.addPlayer(host);

    creatingGame("five", 1);
    attemptLogout();
    attemptLogin("play2", "play2");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play3", "play3");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play4", "play4");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play5", "play5");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("host", "password");

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.assertRenderedPage(FiveBoard.class);
    tester.assertLabel("p1", "host");
    tester.assertLabel("p1heal", "4");
    tester.assertLabel("p1pres", "0");
    
    tester.assertLabel("p2", "play2");
    tester.assertLabel("p2heal", "4");
    tester.assertLabel("p2pres", "0");
    
    tester.assertLabel("p3", "play3");
    tester.assertLabel("p3heal", "4");
    tester.assertLabel("p3pres", "0");
    
    tester.assertLabel("p4", "play4");
    tester.assertLabel("p4heal", "4");
    tester.assertLabel("p4pres", "0");

    tester.assertLabel("p5", "play5");
    tester.assertLabel("p5heal", "4");
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
    attemptLogin("play2", "play2");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play3", "play3");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play4", "play4");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play5", "play5");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play6", "play6");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("host", "password");

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.assertRenderedPage(SixBoard.class);
    tester.assertLabel("p1", "host");
    tester.assertLabel("p1heal", "4");
    tester.assertLabel("p1pres", "0");
    
    tester.assertLabel("p2", "play2");
    tester.assertLabel("p2heal", "4");
    tester.assertLabel("p2pres", "0");
    
    tester.assertLabel("p3", "play3");
    tester.assertLabel("p3heal", "4");
    tester.assertLabel("p3pres", "0");
    
    tester.assertLabel("p4", "play4");
    tester.assertLabel("p4heal", "4");
    tester.assertLabel("p4pres", "0");

    tester.assertLabel("p5", "play5");
    tester.assertLabel("p5heal", "4");
    tester.assertLabel("p5pres", "0");

    tester.assertLabel("p6", "play6");
    tester.assertLabel("p6heal", "4");
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
    attemptLogin("play2", "play2");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play3", "play3");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play4", "play4");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play5", "play5");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play6", "play6");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("play7", "play7");
    joiningGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("host", "password");

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.assertRenderedPage(SevenBoard.class);
    tester.assertLabel("p1", "host");
    tester.assertLabel("p1heal", "4");
    tester.assertLabel("p1pres", "0");
    
    tester.assertLabel("p2", "play2");
    tester.assertLabel("p2heal", "4");
    tester.assertLabel("p2pres", "0");
    
    tester.assertLabel("p3", "play3");
    tester.assertLabel("p3heal", "4");
    tester.assertLabel("p3pres", "0");
    
    tester.assertLabel("p4", "play4");
    tester.assertLabel("p4heal", "4");
    tester.assertLabel("p4pres", "0");

    tester.assertLabel("p5", "play5");
    tester.assertLabel("p5heal", "4");
    tester.assertLabel("p5pres", "0");

    tester.assertLabel("p6", "play6");
    tester.assertLabel("p6heal", "4");
    tester.assertLabel("p6pres", "0");

    tester.assertLabel("p7", "play7");
    tester.assertLabel("p7heal", "4");
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