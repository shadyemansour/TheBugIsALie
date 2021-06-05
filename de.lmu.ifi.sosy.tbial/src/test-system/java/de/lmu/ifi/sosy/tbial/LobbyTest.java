package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.util.tester.WebSocketTester;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import static org.junit.Assert.*;

import org.junit.Test;


public class LobbyTest extends PageTestBase {
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
    //tester1 = new WicketTester(super.getApplication());
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
  }


  @Test
  public void getUserAttsAfterSet() {
    int id = getSession().getUser().getId();
    int prestige = 10;
    int health = 100;
    String charachter = "John Doe";
    String role = "testRole";
    database.setUserPrestige(id, prestige);
    database.setUserHealth(id, health);
    database.setUserRole(id, role);
    database.setUserCharacter(id, charachter);
    int newPrestige = database.getUserPrestige(id);
    int newHealth = database.getUserHealth(id);
    String newRole = database.getUserRole(id);
    String newCharacter = database.getUserCharacter(id);

    assertEquals(prestige, newPrestige);
    assertEquals(health, newHealth);
    assertEquals(role, newRole);
    assertEquals(charachter, newCharacter);
  }

  @Test
  public void getUserAttsReturnNullsUserFound() {
    int id = getSession().getUser().getId();
    int newPrestige = database.getUserPrestige(id);
    int newHealth = database.getUserHealth(id);
    String newRole = database.getUserRole(id);
    String newCharacter = database.getUserCharacter(id);

    assertEquals(-1, newPrestige);
    assertEquals(-1, newHealth);
    assertEquals(null, newRole);
    assertEquals(null, newCharacter);
  }

  @Test
  public void getUserAttsReturnNullsUsernotFound() {
    int id = -1;
    int newPrestige = database.getUserPrestige(id);
    int newHealth = database.getUserHealth(id);
    String newRole = database.getUserRole(id);
    String newCharacter = database.getUserCharacter(id);

    assertEquals(-1, newPrestige);
    assertEquals(-1, newHealth);
    assertEquals(null, newRole);
    assertEquals(null, newCharacter);
  }

  @Test
  public void testGameCreation() {
    createGame("test");
  }

  @Test
  public void playerJoinedGame() {
    joinGame();
    assertTrue(game.getPlayers().contains(host));
  }

  @Test
  public void playerLeaveGame() {
    joinGame();
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");

    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    FormTester form = tester.newFormTester("tabs:panel:boxedGameLobby:form");
    form.submit("leavebutton");
    assertTrue(getSession().getUser().getGame() == null);

  }

  @Test
  public void hostRemovesPlayer() {
    createGame("hostRemovesPlayerFromGame");
    attemptLogout();
    attemptLogin("user1", "user1");
    joinGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("testhost", "testpassword");
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");
    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
//    WebSocketTester webSocketTester = new WebSocketTester(tester, sitesTabLink.getPage()){
//      public void broadcastAll(Application application, IWebSocketPushMessage message) {
//        super.broadcastAll(application, message);
//      }
//    };
    // FormTester form = tester.newFormTester("tabs:panel:boxedGameLobby:joinedPlayerListContainer");
    // form.submit("tabs:panel:boxedGameLobby:joinedPlayerListContainer:joinedPlayers:1:removeplayerbutton");
    tester.executeAjaxEvent("tabs:panel:boxedGameLobby:joinedPlayerListContainer:joinedPlayers:1:removeplayerbutton", "click");
    assertTrue(getSession().getUser().getGame().getActivePlayers() == 1);
    attemptLogout();
    attemptLogin("user1", "user1");
    tester.assertRenderedPage(Lobby.class);
    siteTab = (WebMarkupContainer) tabs.get("2");
    sitesTabLink = (AjaxFallbackLink) siteTab.get("link");
    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    tester.assertComponent("tabs:panel:create", Form.class);
  }

  @Test
  public void gameStartTest() {
    startGame();

  }


}
