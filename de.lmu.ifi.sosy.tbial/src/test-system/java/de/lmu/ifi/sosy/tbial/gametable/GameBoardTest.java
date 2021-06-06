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
    host = new User("testhost", "testpassword", null);
    database.register("testhost", "testpassword");
    loginUser();

    AjaxTabbedPanel tabbedPanel = (AjaxTabbedPanel) tester.getComponentFromLastRenderedPage("tabs");
    tabs = (WebMarkupContainer) tabbedPanel.get("tabs-container:tabs");

    player2 = new User("test2", "test2pw", null);
    database.register("test2", "test2pw");
    player3 = new User("test3", "test3pw", null);
    database.register("test3", "test3pw");
    player4 = new User("test4", "test4pw", null);
    database.register("test4", "test4pw");
    player5 = new User("test5", "test5pw", null);
    database.register("test5", "test5pw");
    player6 = new User("test6", "test6pw", null);
    database.register("test6", "test6pw");
    player7 = new User("test7", "test7pw", null);
    database.register("test7", "test7pw");
  }

  @Test
  public void renderFourBoard() {
    game = database.createGame("testGame", host.getName(), "", "new", 4);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(host);

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.startPage(FourBoard.class);
    tester.assertRenderedPage(FourBoard.class);
    tester.assertLabel("p1", "player1-name");
    tester.assertLabel("p2", "player2-name");
    tester.assertLabel("p3", "player3-name");
    tester.assertLabel("p4", "player4-name");
  }

  @Test
  public void renderFiveBoard() {
    game = database.createGame("testGame", host.getName(), "", "new", 5);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(host);
    game.addPlayer(player5);

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.startPage(FiveBoard.class);
    tester.assertRenderedPage(FiveBoard.class);
    tester.assertLabel("p1", "player1-name");
    tester.assertLabel("p2", "player2-name");
    tester.assertLabel("p3", "player3-name");
    tester.assertLabel("p4", "player4-name");
    tester.assertLabel("p5", "player5-name");
  }

  @Test
  public void renderSixBoard() {
    game = database.createGame("testGame", host.getName(), "", "new", 6);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(host);
    game.addPlayer(player5);
    game.addPlayer(player6);

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.startPage(SixBoard.class);
    tester.assertRenderedPage(SixBoard.class);
    tester.assertLabel("p1", "player1-name");
    tester.assertLabel("p2", "player2-name");
    tester.assertLabel("p3", "player3-name");
    tester.assertLabel("p4", "player4-name");
    tester.assertLabel("p5", "player5-name");
    tester.assertLabel("p6", "player6-name");
  }

  @Test
  public void renderSevenBoard() {
    game = database.createGame("testGame", host.getName(), "", "new", 7);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    game.addPlayer(host);
    game.addPlayer(player5);
    game.addPlayer(player6);
    game.addPlayer(player7);

    assertTrue(game.getActivePlayers() == game.getNumPlayers());
    startingGame();

    tester.startPage(SevenBoard.class);
    tester.assertRenderedPage(SevenBoard.class);
    tester.assertLabel("p1", "player1-name");
    tester.assertLabel("p2", "player2-name");
    tester.assertLabel("p3", "player3-name");
    tester.assertLabel("p4", "player4-name");
    tester.assertLabel("p5", "player5-name");
    tester.assertLabel("p6", "player6-name");
    tester.assertLabel("p7", "player7-name");
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
    FormTester form = tester.newFormTester("tabs:panel:create");
    form.setValue("name", "testGame");
    form.submit("submitgame");

    form = tester.newFormTester("tabs:panel:boxedGameLobby:form");
    form.submit("startbutton");
  }
}