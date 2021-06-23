package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.InMemoryDatabase;
import de.lmu.ifi.sosy.tbial.gametable.FourBoard;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;

import static org.junit.Assert.assertTrue;

public abstract class PageTestBase {

  protected TBIALApplication application;

  protected Database database;

  protected WicketTester tester;

  protected WebMarkupContainer tabs;

  protected final void setupApplication() {
    setupApplication(RuntimeConfigurationType.DEVELOPMENT);
  }

  protected final void setupApplication(RuntimeConfigurationType configuration) {
    database = new InMemoryDatabase();
    application = new TBIALApplication(database);
    application.setConfigurationType(configuration);
    tester = new WicketTester(application);

  }

  protected void joinGame(int rowNum, boolean confirmPanel, boolean isProtected, String confirm, String password) {
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("1");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");

    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    tester.clickLink(String.format("tabs:panel:gamelist:availableGames:%s:joinGame", rowNum));
    if (confirmPanel) {
      tester.clickLink(String.format("tabs:panel:yesNoForm:%s", confirm));
    }
    if (isProtected) {
      FormTester form = tester.newFormTester("tabs:panel:passwordForm");
      form.setValue("password2", password);
      form.submit("confirm");
    }
  }


  protected void attemptLogin(String name, String password) {
    tester.startPage(Login.class);
    tester.assertRenderedPage(Login.class);

    FormTester form = tester.newFormTester("login");
    form.setValue("name", name);
    form.setValue("password", password);
    form.submit("loginbutton");
  }

  protected void attemptLogout() {
    tester.startPage(Lobby.class);
    tester.clickLink("signout");
    tester.startPage(Lobby.class);
    tester.assertRenderedPage(Login.class);
  }

  protected void createGame(String gameName) {
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");

    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    FormTester form = tester.newFormTester("tabs:panel:create");
    form.setValue("name", gameName);
    form.submit("submitgame");
    assertTrue(getSession().getUser().getGame().getName().equals(gameName));

  }

  protected void startGame() {
    createGame("startGame");
    attemptLogout();
    attemptLogin("user1", "user1");
    joinGame(0, false, false, null, null);
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();

    attemptLogin("user2", "user2");
    joinGame(0, false, false, null, null);
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();

    attemptLogin("user3", "user3");
    joinGame(0, false, false, null, null);
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();

    attemptLogin("testhost", "testpassword");
    tester.assertRenderedPage(Lobby.class);
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");
    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    FormTester form = tester.newFormTester("tabs:panel:boxedGameLobby:form");
    form.submit("startbutton");
    // tester.startPage(FourBoard.class);
    tester.assertRenderedPage(FourBoard.class);
  }

  protected void pressStartGameAfterLoggingBackIn() {
    WebMarkupContainer siteTab = (WebMarkupContainer) tabs.get("2");
    AjaxFallbackLink sitesTabLink = (AjaxFallbackLink) siteTab.get("link");
    tester.clickLink(sitesTabLink.getPageRelativePath(), true);
    FormTester form = tester.newFormTester("tabs:panel:boxedGameLobby:form");
    form.submit("startbutton");
    tester.startPage(FourBoard.class);
    tester.assertRenderedPage(FourBoard.class);
  }

  protected TBIALSession getSession() {
    return (TBIALSession) tester.getSession();
  }

  public TBIALApplication getApplication() {
    return this.application;
  }


}
