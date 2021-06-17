package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.gametable.FourBoard;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;


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
    user1 = new User("user1", "user1", null);
    host = new User("testhost", "testpassword", null);
    database.register("testhost", "testpassword");
    database.register("user1", "user1");
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
  @Test
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


}
