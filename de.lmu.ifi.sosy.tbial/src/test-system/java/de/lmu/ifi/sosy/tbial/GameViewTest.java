package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.gametable.FourBoard;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GameViewTest extends PageTestBase {
  User host;
  User user1;
  Game game;


  private static EmbeddedDataSource dataSource = new EmbeddedDataSource();

  static {
    dataSource.setDatabaseName("tbial_test");
    dataSource.setCreateDatabase("create");
  }

  @Before
  public void setUp() {
    setupApplication();
    user1 = database.register("user1", "user1");
    host = database.register("testhost", "testpassword");
    database.register("user2", "user2");
    database.register("user3", "user3");
    attemptLogin("testhost", "testpassword");
    game = database.createGame("testGame", host.getName(), "", "new", 4);
    tester.assertRenderedPage(Lobby.class);
    AjaxTabbedPanel tabbedPanel = (AjaxTabbedPanel)
        tester.getComponentFromLastRenderedPage("tabs");
    tabs = (WebMarkupContainer) tabbedPanel.get("tabs-container:tabs");
    startGame();
    tester.startPage(FourBoard.class);
    tester.assertRenderedPage(FourBoard.class);
  }


  @Test
  public void playerLeaveGameWhileGameIsRunning() {
    tester.assertRenderedPage(FourBoard.class);
    leaveGame();
    tester.assertRenderedPage(Lobby.class);
  }

  @Test
  public void playerRejoinGameWhileGameIsRunning() {
    leaveGame();
    tester.assertRenderedPage(Lobby.class);
    joinGame(0, true);
    tester.assertRenderedPage(FourBoard.class);
  }

  //TODO to be changed when player is redirected to gameview on login
  // @Test
  public void gameViewRenderedAfterLoggingBackIn() {
    leaveGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("user1", "user1");
    pressStartGameAfterLoggingBackIn();
    tester.assertRenderedPage(FourBoard.class);
  }


  protected void leaveGame() {
    FormTester form = tester.newFormTester("form");
    form.submit("leaveButton");
  }

  @Test
  public void p1hand_sizeIsZero() {
    // System.out.println(gameView.playerList);
    tester.assertComponent("player-card-container1:playable-cards-container1:card-hand1", ListView.class);
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container1:playable-cards-container1:card-hand1")).getViewSize());

    //   assertEquals(gameView.p1hand.size(), 0);
  }


}
