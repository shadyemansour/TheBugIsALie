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

  private static EmbeddedDataSource dataSource = new EmbeddedDataSource();

  static {
    dataSource.setDatabaseName("tbial_test");
    dataSource.setCreateDatabase("create");
  }

  @Before
  public void setUp() {
    setupApplication();
    host = database.register("testhost", "testpassword");

    attemptLogin(host.getName(), host.getPassword());

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
    game = database.createGame("testGame", host.getName(), "", "new", 4);
    game.addPlayer(player2);
    player2.setGame(game);
    game.addPlayer(player3);
    player3.setGame(game);
    game.addPlayer(player4);
    player4.setGame(game);

    joinGame(0, false);
    startingGame();
    assertTrue(game.getActivePlayers() == game.getNumPlayers());

    tester.startPage(FourBoard.class);
    tester.assertRenderedPage(FourBoard.class);
    //TODO ASSERT LABELS
//    tester.assertLabel("p1", "player1-name");
//    tester.assertLabel("p2", "player2-name");
//    tester.assertLabel("p3", "player3-name");
//    tester.assertLabel("p4", "player4-name");
  }

  @Test
  public void renderFiveBoard() {
    game = database.createGame("testGame", host.getName(), "", "new", 5);
    game.addPlayer(player2);
    player2.setGame(game);
    game.addPlayer(player3);
    player3.setGame(game);
    game.addPlayer(player4);
    player4.setGame(game);
    game.addPlayer(host);
    host.setGame(game);
    game.addPlayer(player5);
    player5.setGame(game);

    joinGame(0, false);

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
    player2.setGame(game);
    game.addPlayer(player3);
    player3.setGame(game);
    game.addPlayer(player4);
    player4.setGame(game);
    game.addPlayer(player5);
    player5.setGame(game);
    game.addPlayer(player6);
    player6.setGame(game);

    joinGame(0, false);

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
    player2.setGame(game);
    game.addPlayer(player3);
    player3.setGame(game);
    game.addPlayer(player4);
    player4.setGame(game);
    game.addPlayer(player5);
    player5.setGame(game);
    game.addPlayer(player6);
    player6.setGame(game);
    game.addPlayer(player7);
    player7.setGame(game);

    joinGame(0, false);

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
    attemptLogout();
    attemptLogin(host.getName(), host.getPassword());
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");
    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    FormTester form = tester.newFormTester("tabs:panel:boxedGameLobby:form");
    form.submit("startbutton");
  }
}