package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Card;
import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.gametable.FourBoard;
import de.lmu.ifi.sosy.tbial.gametable.GameView;
import de.lmu.ifi.sosy.tbial.networking.JSONMessage;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.tester.FormTester;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class GameViewTest extends PageTestBase {
  User host;
  User user1;
  User user2;
  User user3;
  Game game;
  GameView gameView;

  @Before
  public void setUp() {
    setupApplication();
    user1 = database.register("user1", "user1");
    host = database.register("testhost", "testpassword");
    user2 = database.register("user2", "user2");
    user3 = database.register("user3", "user3");
    attemptLogin("testhost", "testpassword");
    game = database.createGame("testGame", host.getName(), "", "new", 4);
    tester.assertRenderedPage(Lobby.class);
    AjaxTabbedPanel tabbedPanel = (AjaxTabbedPanel)
        tester.getComponentFromLastRenderedPage("tabs");
    tabs = (WebMarkupContainer) tabbedPanel.get("tabs-container:tabs");

    startGame();
    tester.startPage(FourBoard.class);
    tester.assertRenderedPage(FourBoard.class);
    gameView = (GameView) tester.getLastRenderedPage();
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
    joinGame(0, false, false, null, null);
    tester.assertRenderedPage(FourBoard.class);
  }

  //TODO to be changed when player is redirected to gameview on login
  //@Test
  public void gameViewRenderedAfterLoggingBackIn() {
    leaveGame();
    tester.assertRenderedPage(Lobby.class);
    attemptLogout();
    attemptLogin("testhost", "testpassword");
    tester.assertRenderedPage(Lobby.class);
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
//    GameView gameView = (GameView) tester.getLastRenderedPage();
//    gameView.handleMessage(gameView.getGame().getRoleCardsHostMessage());
  }

  @Test
  public void p1hand_sizeIsFour_CardsDrawn() {
  	JSONObject msgBody = new JSONObject();
  	msgBody.put("gameID", 1);
  	msgBody.put("playerID", user2.getId());
  	msgBody.put("cards", 4);
  	msgBody.put("cardsInDeck", 30);
  	JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "CardsDrawn");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);

    assertEquals(4, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container1:playable-cards-container1:card-hand1")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container2:playable-cards-container2:card-hand2")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container3:playable-cards-container3:card-hand3")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container4:playable-cards-container4:card-hand4")).getViewSize());
  }

  @Test
  public void p2hand_sizeIsFour_CardsDrawn() {
  	JSONObject msgBody = new JSONObject();
  	msgBody.put("gameID", 1);
  	msgBody.put("playerID", user3.getId());
  	msgBody.put("cards", 4);
  	msgBody.put("cardsInDeck", 30);
  	JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "CardsDrawn");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);

    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container1:playable-cards-container1:card-hand1")).getViewSize());
    assertEquals(4, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container2:playable-cards-container2:card-hand2")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container3:playable-cards-container3:card-hand3")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container4:playable-cards-container4:card-hand4")).getViewSize());
  }

  @Test
  public void p3hand_sizeIsFour_YourCards() {
  	JSONArray jsonArray = new JSONArray();
  	jsonArray.put(new Card("", "Hidden Card", null, null, null, false, false, null));
  	jsonArray.put(new Card("", "Hidden Card", null, null, null, false, false, null));
  	jsonArray.put(new Card("", "Hidden Card", null, null, null, false, false, null));
  	jsonArray.put(new Card("", "Hidden Card", null, null, null, false, false, null));
  	JSONObject msgBody = new JSONObject();
  	msgBody.put("gameID", 1);
  	msgBody.put("cards", jsonArray);
  	JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "YourCards");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);

    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container1:playable-cards-container1:card-hand1")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container2:playable-cards-container2:card-hand2")).getViewSize());
    assertEquals(4, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container3:playable-cards-container3:card-hand3")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container4:playable-cards-container4:card-hand4")).getViewSize());
  }

  @Test
  public void p4hand_sizeIsFour_CardsDrawn() {
  	JSONObject msgBody = new JSONObject();
  	msgBody.put("gameID", 1);
  	msgBody.put("playerID", user1.getId());
  	msgBody.put("cards", 4);
  	msgBody.put("cardsInDeck", 30);
  	JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "CardsDrawn");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);

    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container1:playable-cards-container1:card-hand1")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container2:playable-cards-container2:card-hand2")).getViewSize());
    assertEquals(0, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container3:playable-cards-container3:card-hand3")).getViewSize());
    assertEquals(4, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container4:playable-cards-container4:card-hand4")).getViewSize());
  }

  @Test
  public void stackList_sizeIs30_CardsDrawn() {
  	JSONObject msgBody = new JSONObject();
  	msgBody.put("gameID", 1);
  	msgBody.put("playerID", user1.getId());
  	msgBody.put("cards", 4);
  	msgBody.put("cardsInDeck", 30);
  	JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "CardsDrawn");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);

    assertEquals(30, ((ListView) tester.getComponentFromLastRenderedPage("middle-table-container:stack")).getViewSize());
  }

  @Test
  public void heapList_containsCorrectCard_afterDiscardCard() {
  	Card card = new Card("Action", "Nullpointer!", "--bug--", null, "-1 mental health", true, false, null);
  	JSONObject msgBody = new JSONObject();
  	msgBody.put("gameID", 1);
  	msgBody.put("playerID", user1.getId());
  	msgBody.put("card", card);
  	JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "CardDiscarded");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);

		assertEquals(card, ((ListView) tester.getComponentFromLastRenderedPage("middle-table-container:heap")).getModelObject().get(0));
  }

  @Test
  public void roles_assignedCorrect() {
  	JSONArray jsonArray = new JSONArray();

  	JSONObject role1 = new JSONObject();
  	Card role1card = new Card("Role", "Consultant", null, "Aim: Get everyone else \nfired; Manager last!", "Tries to take over the \ncompany", false, false, null);
  	role1.put("playerID", host.getId());
  	role1.put("role", role1card.getTitle());
  	role1.put("roleCard", role1card);
  	jsonArray.put(role1);

  	JSONObject role2 = new JSONObject();
  	Card role2card = new Card("Role", "Manager", null, "Aim: Remove evil code \nmonkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
  	role2.put("playerID", host.getId());
  	role2.put("role", role2card.getTitle());
  	role2.put("roleCard", role2card);
  	jsonArray.put(role2);

  	JSONObject role3 = new JSONObject();
  	Card role3card = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null);
  	role3.put("playerID", host.getId());
  	role3.put("role", role3card.getTitle());
  	role3.put("roleCard", role3card);
  	jsonArray.put(role3);

  	JSONObject role4 = new JSONObject();
  	Card role4card = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null);
  	role4.put("playerID", host.getId());
  	role4.put("role", role4card.getTitle());
  	role4.put("roleCard", role4card);
  	jsonArray.put(role4);

  	JSONObject msgBody = new JSONObject();
  	msgBody.put("gameID", 1);
  	msgBody.put("roles", jsonArray);
  	JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "Roles");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);

		assertEquals("Hidden Role", ((Card) ((ListView) tester.getComponentFromLastRenderedPage("player-card-container1:health-role-container1:role-card-panel1")).getModelObject().get(0)).getTitle());
		assertEquals("Hidden Role", ((Card) ((ListView) tester.getComponentFromLastRenderedPage("player-card-container2:health-role-container2:role-card-panel2")).getModelObject().get(0)).getTitle());
		assertEquals(role1card, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container3:health-role-container3:role-card-panel3")).getModelObject().get(0));
		assertEquals(role2card, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container4:health-role-container4:role-card-panel4")).getModelObject().get(0));
  }
  
  @Test
  public void roles_allVisible_afterGameWon() {
  	Card role1card = new Card("Role", "Consultant", null, "Aim: Get everyone else \nfired; Manager last!", "Tries to take over the \ncompany", false, false, null);
  	Card role2card = new Card("Role", "Manager", null, "Aim: Remove evil code \nmonkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
  	Card role3card = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null);
  	Card role4card = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null);
  	
  	JSONArray cardsArray = new JSONArray();
		cardsArray.put(role1card);
		cardsArray.put(role2card);
		cardsArray.put(role3card);
		cardsArray.put(role4card);
		System.out.println("gameWon roleCards: " + cardsArray);
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", 1);
		msgBody.put("playerID", host.getId());
		msgBody.put("roleCards", cardsArray);
		JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "GameWon");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);
		
		assertEquals(role3card, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container1:health-role-container1:role-card-panel1")).getModelObject().get(0));
		assertEquals(role4card, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container2:health-role-container2:role-card-panel2")).getModelObject().get(0));
		assertEquals(role1card, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container3:health-role-container3:role-card-panel3")).getModelObject().get(0));
		assertEquals(role2card, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container4:health-role-container4:role-card-panel4")).getModelObject().get(0));
  }
  
  @Test
  public void role_visible_afterPlayerFired() {
  	Card role3card = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null);
  	
  	JSONObject msgBody = new JSONObject();
    msgBody.put("gameID", 1);
    msgBody.put("playerID", user2.getId());
    msgBody.put("role", role3card);
		JSONObject msgObject = new JSONObject();
  	msgObject.put("msgType", "PlayerFired");
  	msgObject.put("msgBody", msgBody);
  	JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);
		
		assertEquals(role3card, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container1:health-role-container1:role-card-panel1")).getModelObject().get(0));
  }

  
  @Test
  public void characters_assignedCorrect() {
	  
		JSONArray jsonArray = new JSONArray();

		JSONObject char1 = new JSONObject();
		Card char1card = new Card("Character", "Mark Zuckerberg", "Founder of Facebook", "(Mental Health 3)",
				"If you lose mental health, \ntake one card from the causer", false, true, null);
		char1.put("playerID", host.getId());
		char1.put("character", char1card.getTitle());
		char1.put("characterCard", char1card);
		char1.put("health", 3);
		jsonArray.put(char1);

		JSONObject char2 = new JSONObject();
		Card char2card = new Card("Character", "Tom Anderson", "Founder of MySpace", "(Mental Health 4)",
				"If you lose mental health, \nyou may take a card from the stack", false, true, null);
		char2.put("playerID", host.getId());
		char2.put("character", char2card.getTitle());
		char2.put("characterCard", char2card);
		char2.put("health", 4);
		jsonArray.put(char2);

		JSONObject char3 = new JSONObject();
		Card char3card = new Card("Character", "Jeff Taylor", "Founder of \nmonster.com", "(Mental Health 4)",
				"No cards left? \nTake one from the stack", false, true, null);
		char3.put("playerID", host.getId());
		char3.put("character", char3card.getTitle());
		char3.put("characterCard", char3card);
		char3.put("health", 4);
		jsonArray.put(char3);

		JSONObject char4 = new JSONObject();
		Card char4card = new Card("Character", "Larry Page", "Founder of Google", "(Mental Health 4)",
				"When somebody gets fired, \nyou take the cards", false, true, null);
		char4.put("playerID", host.getId());
		char4.put("character", char4card.getTitle());
		char4.put("characterCard", char4card);
		char4.put("health", 4);
		jsonArray.put(char4);

		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", 1);
		msgBody.put("playerID", host.getId());
		msgBody.put("characters", jsonArray);
		JSONObject msgObject = new JSONObject();
		msgObject.put("msgType", "Characters");
		msgObject.put("msgBody", msgBody);
		JSONMessage msg = new JSONMessage(msgObject);
		gameView.handleMessage(msg);

		assertEquals(char3card,
				((ListView) tester
						.getComponentFromLastRenderedPage("player-card-container1:health-role-container1:character-card-panel1"))
								.getModelObject().get(0));
		assertEquals(char4card,
				((ListView) tester
						.getComponentFromLastRenderedPage("player-card-container2:health-role-container2:character-card-panel2"))
								.getModelObject().get(0));
		assertEquals(char1card,
				((ListView) tester
						.getComponentFromLastRenderedPage("player-card-container3:health-role-container3:character-card-panel3"))
								.getModelObject().get(0));
		assertEquals(char2card, ((ListView) tester.getComponentFromLastRenderedPage("player-card-container4:health-role-container4:character-card-panel4")).getModelObject().get(0));
 
  	
  	
  }
  
  
}
