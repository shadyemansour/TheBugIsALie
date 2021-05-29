package de.lmu.ifi.sosy.tbial.db;

import de.lmu.ifi.sosy.tbial.*;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;

import static org.junit.Assert.*;

import org.junit.Test;


public class LobbyTest extends PageTestBase {
  User host;
  Game game;
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
    game = database.createGame("testGame", host.getName(), "", "new", 4);
    attemptLogin("testhost", "testpassword");
    attemptLogin("testhost", "testpassword");
    tester.assertRenderedPage(Lobby.class);
    AjaxTabbedPanel tabbedPanel = (AjaxTabbedPanel)
        tester.getComponentFromLastRenderedPage("tabs");
    tabs = (WebMarkupContainer)
        tabbedPanel.get("tabs-container:tabs");


  }

  @Test
  public void testGameCreation() {
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");

    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    FormTester form = tester.newFormTester("tabs:panel:create");
    form.setValue("name", "testGame");
    form.submit("submitgame");
    assertTrue(getSession().getUser().getGame().getName().equals("testGame"));

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

  private void attemptLogin(String name, String password) {
    tester.startPage(Login.class);
    tester.assertRenderedPage(Login.class);

    FormTester form = tester.newFormTester("login");
    form.setValue("name", name);
    form.setValue("password", password);
    form.submit("loginbutton");
  }

  private void joinGame() {
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("1");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");

    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    tester.clickLink("tabs:panel:gamelist:availableGames:0:joinGame");

  }
}
