package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.db.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.model.PropertyModel;

@AuthenticationRequired
public class SevenBoard extends GameView {
  /**
   * UID for serialization.
   */
  private static final long serialVersionUID = 1L;

  User user = ((TBIALSession) getSession()).getUser();
  List<Game> appGames = ((TBIALApplication) getApplication()).getAvailableGames();
  List<User> players;
  // Game game;
  private Label p1prestige, p2prestige, p3prestige, p4prestige, p5prestige, p6prestige, p7prestige;
  private Label p1health, p2health, p3health, p4health, p5health, p6health, p7health;
  private Label p1name, p2name, p3name, p4name, p5name, p6name, p7name;
  int currenthealth1, currenthealth2, currenthealth3, currenthealth4, currenthealth5, currenthealth6, currenthealth7;

  /*
   * dummy cards
   */
  Card card1;
  Card card2;
  Card card3;
  Card card4;
  Card card5;
  Card card6;
  List<IModel<Card>> cardModels;

  public SevenBoard() {
    super();
    createPlayerAttributes();

    /*
     * dummy cards serve as example
     */
    createDummyCards();

    createStack();
    createHeap();

    createPlayer1Area();
    createPlayer2Area();
    createPlayer3Area();
    createPlayer4Area();
    createPlayer5Area();
    createPlayer6Area();
    createPlayer7Area();
  }

  protected void updatePlayerAttributes() {
  }

  protected void updateHealth() {
  }

  protected void createPlayerAttributes() {
//    for (Game g : appGames) {
//      if (g.equals(user.getGame())) {
//        game = g;
////        user.setGame(game);
//        break;
//      }
//    };

    //game.startGame(); // this is temporary since game is not initialized before starting a game
    players = game.getPlayers();
    // temporary untill game is get with wbesocket
//    players.get(0).setHealth(4);
//    players.get(0).setPrestige(0);
//    players.get(1).setHealth(4);
//    players.get(1).setPrestige(0);
//    players.get(2).setHealth(4);
//    players.get(2).setPrestige(0);
//    players.get(3).setHealth(4);
//    players.get(3).setPrestige(0);
//    players.get(4).setHealth(4);
//    players.get(4).setPrestige(0);
//    players.get(5).setHealth(4);
//    players.get(5).setPrestige(0);
//    players.get(6).setHealth(4);
//    players.get(6).setPrestige(0);

    currenthealth1 = players.get(0).getHealth();
    currenthealth2 = players.get(1).getHealth();
    currenthealth3 = players.get(2).getHealth();
    currenthealth4 = players.get(3).getHealth();
    currenthealth5 = players.get(4).getHealth();
    currenthealth6 = players.get(5).getHealth();
    currenthealth7 = players.get(6).getHealth();

    // adjust to playerlist order
    // PLAYER 1  -  ATTRIBUTES
    p1name = new Label("p1", players.get(0).getName());
    add(p1name);
    p1health = new Label("p1heal", new PropertyModel<>(this, "currenthealth1"));
    p1health.setOutputMarkupId(true);
    p1health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p1health);
    p1prestige = new Label("p1pres", players.get(0).getPrestige());
    add(p1prestige);
    // PLAYER 2  -  ATTRIBUTES
    p2name = new Label("p2", players.get(1).getName());
    add(p2name);
    p2health = new Label("p2heal", new PropertyModel<>(this, "currenthealth2"));
    p2health.setOutputMarkupId(true);
    p2health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p2health);
    p2prestige = new Label("p2pres", players.get(1).getPrestige());
    add(p2prestige);
    // PLAYER 3  -  ATTRIBUTES
    p3name = new Label("p3", players.get(2).getName());
    add(p3name);
    p3health = new Label("p3heal", new PropertyModel<>(this, "currenthealth3"));
    p3health.setOutputMarkupId(true);
    p3health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p3health);
    p3prestige = new Label("p3pres", players.get(2).getPrestige());
    add(p3prestige);
    // PLAYER 4  -  ATTRIBUTES
    p4name = new Label("p4", players.get(3).getName());
    add(p4name);
    p4health = new Label("p4heal", new PropertyModel<>(this, "currenthealth4"));
    p4health.setOutputMarkupId(true);
    p4health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p4health);
    p4prestige = new Label("p4pres", players.get(3).getPrestige());
    add(p4prestige);
    // PLAYER 5  -  ATTRIBUTES
    p5name = new Label("p5", players.get(4).getName());
    add(p5name);
    p5health = new Label("p5heal", new PropertyModel<>(this, "currenthealth5"));
    p5health.setOutputMarkupId(true);
    p5health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p5health);
    p5prestige = new Label("p5pres", players.get(4).getPrestige());
    add(p5prestige);
    // PLAYER 6  -  ATTRIBUTES
    p6name = new Label("p6", players.get(5).getName());
    add(p6name);
    p6health = new Label("p6heal", new PropertyModel<>(this, "currenthealth6"));
    p6health.setOutputMarkupId(true);
    p6health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p6health);
    p6prestige = new Label("p6pres", players.get(5).getPrestige());
    add(p6prestige);
    // PLAYER 7  -  ATTRIBUTES
    p7name = new Label("p7", players.get(6).getName());
    add(p7name);
    p7health = new Label("p7heal", new PropertyModel<>(this, "currenthealth7"));
    p7health.setOutputMarkupId(true);
    p7health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p7health);
    p7prestige = new Label("p7pres", players.get(6).getPrestige());
    add(p7prestige);
  }

  /*
   * creates dummy cards, can be removed later
   */
  private void createDummyCards() {
    card1 = new Card("Role", "Manager", "", "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, "");
    card2 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, "");
    card3 = new Card("Action", "System Integration", "", "", "My code is better than yours!", true, true, "");
    card3.setVisible(!card3.isVisible());
    card4 = new Card("Ability", "Bug Delegation", "", "", "Delegates bug report\n.25 chance to work", true, true, "");
    card5 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, "");
    card6 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, "");

    cardModels = new ArrayList<IModel<Card>>();
    cardModels.add(Model.of(card1));
    cardModels.add(Model.of(card2));
    cardModels.add(Model.of(card3));
    cardModels.add(Model.of(card4));
    cardModels.add(Model.of(card5));
    cardModels.add(Model.of(card6));
  }

  /*
   * creates stack
   * TODO: fill with real cards later, npt dummy cards => adjust Iterator
   * TODO: make sure that all cards show back side => will be automatic with real cards
   */
  private void createStack() {
    RefreshingView<Card> stack = new RefreshingView<Card>("stack") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardModels.iterator();
      }

      int posLeft = 85 - cardModels.size();
      int posTop = 90 - cardModels.size();

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px; top: " + posTop + "px;"));
        posLeft += 2;
        posTop += 2;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    stack.setOutputMarkupId(true);
    add(stack);
  }

  /*
   * creates heap
   * TODO: fill with real cards later, not dummy cards => adjust Iterator
   * make sure, that all cards show front side => will be automatic with real cards
   */
  private void createHeap() {
    RefreshingView<Card> heap = new RefreshingView<Card>("heap") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardModels.iterator();
      }

      @Override
      protected void populateItem(Item<Card> item) {
        double rotation = Math.random() * 30 + 1;
        double direction = Math.random() > 0.5 ? 1 : -1;
        item.add(new AttributeAppender("style", "transform: rotate(" + (direction * rotation) + "deg);"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    heap.setOutputMarkupId(true);
    add(heap);
  }

  /*
   * creates player area for top left player
   */
  private void createPlayer1Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
    cardDropModels.add(Model.of(card5));

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container1");
    add(playerCardContainer);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container1");
    playerCardContainer.add(playableCardsContainer);
    /*
     * drop area
     */
    RefreshingView<Card> cardDropArea = new RefreshingView<Card>("card-drop-area1") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardDropModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardDropModels.size() * 50) / (cardDropModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    // dummy card model for card hand
    List<IModel<Card>> cardHandModels = new ArrayList<IModel<Card>>();
    cardHandModels.add(Model.of(card2));
    cardHandModels.add(Model.of(card3));
    cardHandModels.add(Model.of(card4));
    cardHandModels.add(Model.of(card5));

    RefreshingView<Card> cardHand = new RefreshingView<Card>("card-hand1") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardHandModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardHandModels.size() * 50) / (cardHandModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
//				posTop += 2;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container1");
    playerCardContainer.add(healthRoleContainer);

    /*
     * mental health
     * TODO: how do we want to display the mental health?
     * TODO: show current mental health
     */
    Label health = new Label("health-player1", "mental health of player 1");
    healthRoleContainer.add(health);

    /*
     * role card
     * TODO: put real role card here
     * TODO: show or hide card depending on player and card
     */
    CardPanel roleCardPanel = new CardPanel("role-card-panel1", new Model<Card>(card1));
    healthRoleContainer.add(roleCardPanel);
  }

  /*
   * creates player area for top right player
   */
  private void createPlayer2Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
    cardDropModels.add(Model.of(card4));
    cardDropModels.add(Model.of(card4));
    cardDropModels.add(Model.of(card4));
    cardDropModels.add(Model.of(card5));

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container2");
    add(playerCardContainer);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container2");
    playerCardContainer.add(playableCardsContainer);
    /*
     * drop area
     */
    RefreshingView<Card> cardDropArea = new RefreshingView<Card>("card-drop-area2") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardDropModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardDropModels.size() * 50) / (cardDropModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    // dummy card model for card hand
    List<IModel<Card>> cardHandModels = new ArrayList<IModel<Card>>();
    cardHandModels.add(Model.of(card2));
    cardHandModels.add(Model.of(card3));
    cardHandModels.add(Model.of(card4));
    cardHandModels.add(Model.of(card5));

    RefreshingView<Card> cardHand = new RefreshingView<Card>("card-hand2") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardHandModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardHandModels.size() * 50) / (cardHandModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
//				posTop += 2;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container2");
    playerCardContainer.add(healthRoleContainer);

    /*
     * mental health
     * TODO: how do we want to display the mental health?
     * TODO: show current mental health
     */
    Label health = new Label("health-player2", "mental health of player 2");
    healthRoleContainer.add(health);

    /*
     * role card
     * TODO: put real role card here
     * TODO: show or hide card depending on player and card
     */
    CardPanel roleCardPanel = new CardPanel("role-card-panel2", new Model<Card>(card1));
    healthRoleContainer.add(roleCardPanel);
  }

  /*
   * creates player area for right top player
   */
  private void createPlayer3Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
    cardDropModels.add(Model.of(card5));

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container3");
    add(playerCardContainer);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container3");
    playerCardContainer.add(playableCardsContainer);
    /*
     * drop area
     */
    RefreshingView<Card> cardDropArea = new RefreshingView<Card>("card-drop-area3") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardDropModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardDropModels.size() * 50) / (cardDropModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    // dummy card model for card hand
    List<IModel<Card>> cardHandModels = new ArrayList<IModel<Card>>();
    cardHandModels.add(Model.of(card2));
    cardHandModels.add(Model.of(card3));
    cardHandModels.add(Model.of(card4));
    cardHandModels.add(Model.of(card5));

    RefreshingView<Card> cardHand = new RefreshingView<Card>("card-hand3") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardHandModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardHandModels.size() * 50) / (cardHandModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
//				posTop += 2;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container3");
    playerCardContainer.add(healthRoleContainer);

    /*
     * mental health
     * TODO: how do we want to display the mental health?
     * TODO: show current mental health
     */
    Label health = new Label("health-player3", "mental health of player 3");
    healthRoleContainer.add(health);

    /*
     * role card
     * TODO: put real role card here
     * TODO: show or hide card depending on player and card
     */
    CardPanel roleCardPanel = new CardPanel("role-card-panel3", new Model<Card>(card1));
    healthRoleContainer.add(roleCardPanel);
  }

  /*
   * creates player area for right bottom player
   */
  private void createPlayer4Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
    cardDropModels.add(Model.of(card5));

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container4");
    add(playerCardContainer);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container4");
    playerCardContainer.add(playableCardsContainer);
    /*
     * drop area
     */
    RefreshingView<Card> cardDropArea = new RefreshingView<Card>("card-drop-area4") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardDropModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardDropModels.size() * 50) / (cardDropModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    // dummy card model for card hand
    List<IModel<Card>> cardHandModels = new ArrayList<IModel<Card>>();
    cardHandModels.add(Model.of(card2));
    cardHandModels.add(Model.of(card3));
    cardHandModels.add(Model.of(card4));
    cardHandModels.add(Model.of(card5));

    RefreshingView<Card> cardHand = new RefreshingView<Card>("card-hand4") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardHandModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardHandModels.size() * 50) / (cardHandModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
//				posTop += 2;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container4");
    playerCardContainer.add(healthRoleContainer);

    /*
     * mental health
     * TODO: how do we want to display the mental health?
     * TODO: show current mental health
     */
    Label health = new Label("health-player4", "mental health of player 4");
    healthRoleContainer.add(health);

    /*
     * role card
     * TODO: put real role card here
     * TODO: show or hide card depending on player and card
     */
    CardPanel roleCardPanel = new CardPanel("role-card-panel4", new Model<Card>(card1));
    healthRoleContainer.add(roleCardPanel);
  }

  /*
   * creates player area for bottom player
   */
  private void createPlayer5Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
    cardDropModels.add(Model.of(card5));

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container5");
    add(playerCardContainer);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container5");
    playerCardContainer.add(playableCardsContainer);
    /*
     * drop area
     */
    RefreshingView<Card> cardDropArea = new RefreshingView<Card>("card-drop-area5") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardDropModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardDropModels.size() * 50) / (cardDropModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    // dummy card model for card hand
    List<IModel<Card>> cardHandModels = new ArrayList<IModel<Card>>();
    cardHandModels.add(Model.of(card2));
    cardHandModels.add(Model.of(card3));
    cardHandModels.add(Model.of(card4));
    cardHandModels.add(Model.of(card5));

    RefreshingView<Card> cardHand = new RefreshingView<Card>("card-hand5") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardHandModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardHandModels.size() * 50) / (cardHandModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
//				posTop += 2;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container5");
    playerCardContainer.add(healthRoleContainer);

    /*
     * mental health
     * TODO: how do we want to display the mental health?
     * TODO: show current mental health
     */
    Label health = new Label("health-player5", "mental health of player 5");
    healthRoleContainer.add(health);

    /*
     * role card
     * TODO: put real role card here
     * TODO: show or hide card depending on player and card
     */
    CardPanel roleCardPanel = new CardPanel("role-card-panel5", new Model<Card>(card1));
    healthRoleContainer.add(roleCardPanel);
  }

  /*
   * creates player area for left bottom player
   */
  private void createPlayer6Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
    cardDropModels.add(Model.of(card5));

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container6");
    add(playerCardContainer);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container6");
    playerCardContainer.add(playableCardsContainer);
    /*
     * drop area
     */
    RefreshingView<Card> cardDropArea = new RefreshingView<Card>("card-drop-area6") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardDropModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardDropModels.size() * 50) / (cardDropModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    // dummy card model for card hand
    List<IModel<Card>> cardHandModels = new ArrayList<IModel<Card>>();
    cardHandModels.add(Model.of(card2));
    cardHandModels.add(Model.of(card3));
    cardHandModels.add(Model.of(card4));
    cardHandModels.add(Model.of(card5));

    RefreshingView<Card> cardHand = new RefreshingView<Card>("card-hand6") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardHandModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardHandModels.size() * 50) / (cardHandModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
//				posTop += 2;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container6");
    playerCardContainer.add(healthRoleContainer);

    /*
     * mental health
     * TODO: how do we want to display the mental health?
     * TODO: show current mental health
     */
    Label health = new Label("health-player6", "mental health of player 6");
    healthRoleContainer.add(health);

    /*
     * role card
     * TODO: put real role card here
     * TODO: show or hide card depending on player and card
     */
    CardPanel roleCardPanel = new CardPanel("role-card-panel6", new Model<Card>(card1));
    healthRoleContainer.add(roleCardPanel);
  }

  /*
   * creates player area for left top player
   */
  private void createPlayer7Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
    cardDropModels.add(Model.of(card5));

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container7");
    add(playerCardContainer);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container7");
    playerCardContainer.add(playableCardsContainer);
    /*
     * drop area
     */
    RefreshingView<Card> cardDropArea = new RefreshingView<Card>("card-drop-area7") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardDropModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardDropModels.size() * 50) / (cardDropModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    // dummy card model for card hand
    List<IModel<Card>> cardHandModels = new ArrayList<IModel<Card>>();
    cardHandModels.add(Model.of(card2));
    cardHandModels.add(Model.of(card3));
    cardHandModels.add(Model.of(card4));
    cardHandModels.add(Model.of(card5));

    RefreshingView<Card> cardHand = new RefreshingView<Card>("card-hand7") {
      private static final long serialVersionUID = 1L;

      @Override
      protected Iterator<IModel<Card>> getItemModels() {
        return cardHandModels.iterator();
      }

      int width = 300;

      int posLeft = (width - cardHandModels.size() * 50) / (cardHandModels.size() + 1);
      int stepSize = posLeft + 50;

      @Override
      protected void populateItem(Item<Card> item) {
        item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
        posLeft += stepSize;
//				posTop += 2;
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container7");
    playerCardContainer.add(healthRoleContainer);

    /*
     * mental health
     * TODO: how do we want to display the mental health?
     * TODO: show current mental health
     */
    Label health = new Label("health-player7", "mental health of player 7");
    healthRoleContainer.add(health);

    /*
     * role card
     * TODO: put real role card here
     * TODO: show or hide card depending on player and card
     */
    CardPanel roleCardPanel = new CardPanel("role-card-panel7", new Model<Card>(card1));
    healthRoleContainer.add(roleCardPanel);
  }

  @Override
  protected void visualizeCurrentPlayer(int position) {
    // TODO Auto-generated method stub

  }
}