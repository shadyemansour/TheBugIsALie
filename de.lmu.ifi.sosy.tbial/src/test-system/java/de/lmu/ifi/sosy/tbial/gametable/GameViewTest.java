package de.lmu.ifi.sosy.tbial.gametable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.FormTester;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import de.lmu.ifi.sosy.tbial.Lobby;
import de.lmu.ifi.sosy.tbial.Login;
import de.lmu.ifi.sosy.tbial.PageTestBase;
import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;

public class GameViewTest extends PageTestBase {
	Game game;
	GameView gameView;
	
	User host;
	User player2;
	User player3;
	User player4;

  WebMarkupContainer tabs;
  private static EmbeddedDataSource dataSource = new EmbeddedDataSource();

  static {
    dataSource.setDatabaseName("tbial_test");
    dataSource.setCreateDatabase("create");
  }

  @Before
  public void init() {
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
    
    game = database.createGame("testGame", host.getName(), "", "new", 4);
    game.setHost(host);
    game.addPlayer(host);
    game.addPlayer(player2);
    game.addPlayer(player3);
    game.addPlayer(player4);
    
    startingGame();

    gameView = new FourBoard();
  }

  @Test
  public void p1hand_sizeIsZero() {
  	System.out.println(gameView.playerList);
  	assertEquals(gameView.p1hand.size(), 0);
  }
  
//	@Test
//	public void handleMessage_CardsDrawn() {
//		JSONObject msg = new JSONObject();
//		
//		gameView.handleMessage(null);
//	}
	
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