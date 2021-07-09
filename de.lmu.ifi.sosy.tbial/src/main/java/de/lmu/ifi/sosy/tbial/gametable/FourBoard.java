package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.db.*;
import de.lmu.ifi.sosy.tbial.networking.WebSocketManager;
import de.lmu.ifi.sosy.tbial.*;

import static de.lmu.ifi.sosy.tbial.TBIALApplication.getDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.model.PropertyModel;

@AuthenticationRequired
public class FourBoard extends GameView {
  /**
   * UID for serialization.
   */
  private static final long serialVersionUID = 1L;

  private int numPlayer = 4;
  WebMarkupContainer playerCardContainer, playerCardContainer2, playerCardContainer3, playerCardContainer4;
  ListView<Card> cardDropArea, cardDropArea2, cardDropArea3, cardDropArea4;
  Card selectedCard;
  boolean selectable = false;
  ListView<Card> cardHand, cardHand2, cardHand3, cardHand4;

  User user = ((TBIALSession) getSession()).getUser();
  List<Game> appGames = ((TBIALApplication) getApplication()).getAvailableGames();
  List<User> players = actualPlayerlist;
  private Label p1prestige, p2prestige, p3prestige, p4prestige;
  //  private Label p1health, p2health, p3health, p4health;
  private Label p1name, p2name, p3name, p4name;
  int currenthealth1, currenthealth2, currenthealth3, currenthealth4;

  List<String> p1turn = new ArrayList<String>();
  List<String> p2turn = new ArrayList<String>();
  List<String> p3turn = new ArrayList<String>();
  List<String> p4turn = new ArrayList<String>();

  WebMarkupContainer p1container, p2container, p3container, p4container;
  Boolean p1highlighted = false, p2highlighted = false, p3highlighted = false, p4highlighted = false;


  public FourBoard() {
    super();


    createStackAndHeap();

    createPlayer1Area();
    createPlayer2Area();
    createPlayer3Area();
    createPlayer4Area();

    createPlayerAttributes();
    updatePlayerAttributes();
    updateHealth();
    assignLabels();

    setupButton();
  }

  protected void setupButton() {
    AjaxButton endTurnButton = new AjaxButton("endTurnButton") {
      /**
       * UID for serialization.
       */
      private static final long serialVersionUID = 1;

      public void onSubmit(AjaxRequestTarget target) {
//        User user = ((TBIALSession) getSession()).getUser();
        if (user.getId() == currentPlayerId) {
          System.out.println("end game");
          endTurn();
        } else {
          System.out.println("not current user");
        }

      }

      @Override
      protected void onError(AjaxRequestTarget target) {
      }
    };
    AjaxButton testButton = new AjaxButton("testButton") {
      /**
       * UID for serialization.
       */
      private static final long serialVersionUID = 1;

      public void onSubmit(AjaxRequestTarget target) {
//        User user = ((TBIALSession) getSession()).getUser();
        System.out.println("game: " + game);
        game.updateHealth(user.getId(), 1);
      }

      @Override
      protected void onError(AjaxRequestTarget target) {
      }
    };
    form = new Form<>("controls-form");
    form.add(endTurnButton);
    form.add(testButton);
    form.setOutputMarkupId(true);
    p3container.add(form);
  }

  protected void visualizeCurrentPlayer(int position) {
    p1turn.clear();
    p2turn.clear();
    p3turn.clear();
    p4turn.clear();
    // make current player new color
    switch (position) {
      case 0:
        p4turn.add(" ");
        break;
      case 1:
        p1turn.add(" ");
        break;
      case 2:
        p2turn.add(" ");
        break;
      case 3:
        p3turn.add(" ");
        break;
    }
  }

  protected void updatePlayerAttributes() {

    System.out.println("user - " + user.getName() + " --- h1: " + currenthealth1 + " h2: " + currenthealth2 + " h3: " + currenthealth3 + " h4: " + currenthealth4);
  }

  protected void updateHealth() {
    players = game.getPlayers();
    System.out.println("Updating health on: " + players);
    int size = playerList.size();
    for (int i = 0; i < size; i++) {
      if (playerList.get(i).getId() == user.getId()) {
        switch (i) {
          case 0:
            currenthealth1 = players.get(2).getHealth();
            currenthealth2 = players.get(3).getHealth();
            currenthealth3 = players.get(0).getHealth();
            currenthealth4 = players.get(1).getHealth();
            p1health = players.get(2).getHealth();
            p2health = players.get(3).getHealth();
            p3health = players.get(0).getHealth();
            p4health = players.get(1).getHealth();
            break;
          case 1:
            currenthealth1 = players.get(3).getHealth();
            currenthealth2 = players.get(0).getHealth();
            currenthealth3 = players.get(1).getHealth();
            currenthealth4 = players.get(2).getHealth();
            p1health = players.get(3).getHealth();
            p2health = players.get(0).getHealth();
            p3health = players.get(1).getHealth();
            p4health = players.get(2).getHealth();
            break;
          case 2:
            currenthealth1 = players.get(0).getHealth();
            currenthealth2 = players.get(1).getHealth();
            currenthealth3 = players.get(2).getHealth();
            currenthealth4 = players.get(3).getHealth();
            p1health = players.get(0).getHealth();
            p2health = players.get(1).getHealth();
            p3health = players.get(2).getHealth();
            p4health = players.get(3).getHealth();
            break;
          case 3:
            currenthealth1 = players.get(1).getHealth();
            currenthealth2 = players.get(2).getHealth();
            currenthealth3 = players.get(3).getHealth();
            currenthealth4 = players.get(0).getHealth();
            p1health = players.get(1).getHealth();
            p2health = players.get(2).getHealth();
            p3health = players.get(3).getHealth();
            p4health = players.get(0).getHealth();
            break;
        }
      }
    }
    System.out.println("user - " + user.getName() + " --- h1: " + currenthealth1 + " h2: " + currenthealth2 + " h3: " + currenthealth3 + " h4: " + currenthealth4);
  }

  protected void createPlayerAttributes() {

    players = game.getPlayers();
    players.get(0).setPrestige(0);
    players.get(1).setPrestige(0);
    players.get(2).setPrestige(0);
    players.get(3).setPrestige(0);

    // adjust to playerlist order
    // PLAYER 1  -  ATTRIBUTES
////    p1name = new Label("p1", players.get(0).getName());
////    add(p1name);
//    p1health = new Label("p1heal", new PropertyModel<>(this, "currenthealth1"));
//    p1health.setOutputMarkupId(true);
//    p1health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
//    add(p1health);
//    p1prestige = new Label("p1pres", players.get(0).getPrestige());
//    add(p1prestige);
//    // PLAYER 2  -  ATTRIBUTES
////    p2name = new Label("p2", players.get(1).getName());
////    add(p2name);
//    p2health = new Label("p2heal", new PropertyModel<>(this, "currenthealth2"));
//    p2health.setOutputMarkupId(true);
//    p2health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
//    add(p2health);
//    p2prestige = new Label("p2pres", players.get(1).getPrestige());
//    add(p2prestige);
//    // PLAYER 3  -  ATTRIBUTES
////    p3name = new Label("p3", players.get(2).getName());
////    add(p3name);
//    p3health = new Label("p3heal", new PropertyModel<>(this, "currenthealth3"));
//    p3health.setOutputMarkupId(true);
//    p3health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
//    add(p3health);
//    p3prestige = new Label("p3pres", players.get(2).getPrestige());
//    add(p3prestige);
//    // PLAYER 4  -  ATTRIBUTES
////    p4name = new Label("p4", players.get(3).getName());
////    add(p4name);
//    p4health = new Label("p4heal", new PropertyModel<>(this, "currenthealth4"));
//    p4health.setOutputMarkupId(true);
//    p4health.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
//    add(p4health);
//    p4prestige = new Label("p4pres", players.get(3).getPrestige());
//    add(p4prestige);
  }

  /*
   * assign labels for player names
   */
  private void assignLabels() {

    int size = playerList.size();
    for (int i = 0; i < size; i++) {
      if (playerList.get(i).getId() == user.getId()) {
        switch (i) {
          case 0:
            for (int j = 0; j < size; j++) {
              String containerId = "attributes-container-" + (j + 1);
              WebMarkupContainer attributesContainer = new WebMarkupContainer(containerId);
              attributesContainer.setOutputMarkupId(true);
              attributesContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
              switch (j) {
                case 0:
                  p1container.add(attributesContainer);
                  break;
                case 1:
                  p2container.add(attributesContainer);
                  break;
                case 2:
                  p3container.add(attributesContainer);
                  break;
                case 3:
                  p4container.add(attributesContainer);
                  break;
              }

              String labelId = "p" + (j + 1);
              Label player = new Label(labelId, playerList.get((j + 2) % 4).getName());
              attributesContainer.add(player);
              String healthId = "p" + (j + 1) + "heal";
//            Label health = new Label(healthId, new PropertyModel<>(playerList.get((j + 2) % 4), "health"));
              String currentId = "p" + (j + 1) + "health";
              Label health = new Label(healthId, new PropertyModel<>(this, currentId));
              health.setOutputMarkupId(true);
              attributesContainer.add(health);
              String prestigeId = "p" + (j + 1) + "pres";
              Label prestige = new Label(prestigeId, playerList.get((j + 2) % 4).getPrestige());
              attributesContainer.add(prestige);
            }
            break;
          case 1:
            for (int j = 0; j < size; j++) {
              String containerId = "attributes-container-" + (j + 1);
              WebMarkupContainer attributesContainer = new WebMarkupContainer(containerId);
              attributesContainer.setOutputMarkupId(true);
              attributesContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
              switch (j) {
                case 0:
                  p1container.add(attributesContainer);
                  break;
                case 1:
                  p2container.add(attributesContainer);
                  break;
                case 2:
                  p3container.add(attributesContainer);
                  break;
                case 3:
                  p4container.add(attributesContainer);
                  break;
              }


              String labelId = "p" + (j + 1);
              Label player = new Label(labelId, playerList.get((j + 3) % 4).getName());
              attributesContainer.add(player);
              String healthId = "p" + (j + 1) + "heal";
//            Label health = new Label(healthId, new PropertyModel<>(this, Integer.toString(playerList.get((j + 3) % 4).getHealth())));
//            Label health = new Label(healthId, new PropertyModel<>(playerList.get((j + 3) % 4), "health"));
              String currentId = "p" + (j + 1) + "health";
              Label health = new Label(healthId, new PropertyModel<>(this, currentId));
              health.setOutputMarkupId(true);
              attributesContainer.add(health);
              String prestigeId = "p" + (j + 1) + "pres";
              Label prestige = new Label(prestigeId, playerList.get((j + 3) % 4).getPrestige());
              attributesContainer.add(prestige);
            }
            break;
          case 2:
            for (int j = 0; j < size; j++) {
              String containerId = "attributes-container-" + (j + 1);
              WebMarkupContainer attributesContainer = new WebMarkupContainer(containerId);
              attributesContainer.setOutputMarkupId(true);
              attributesContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
              switch (j) {
                case 0:
                  p1container.add(attributesContainer);
                  break;
                case 1:
                  p2container.add(attributesContainer);
                  break;
                case 2:
                  p3container.add(attributesContainer);
                  break;
                case 3:
                  p4container.add(attributesContainer);
                  break;
              }


              String labelId = "p" + (j + 1);
              Label player = new Label(labelId, playerList.get(j % 4).getName());
              attributesContainer.add(player);
              String healthId = "p" + (j + 1) + "heal";
//            Label health = new Label(healthId, new PropertyModel<>(this, Integer.toString(playerList.get(j % 4).getHealth())));
//            Label health = new Label(healthId, new PropertyModel<>(playerList.get(j % 4), "health"));
              String currentId = "p" + (j + 1) + "health";
              Label health = new Label(healthId, new PropertyModel<>(this, currentId));
              health.setOutputMarkupId(true);
              attributesContainer.add(health);
              String prestigeId = "p" + (j + 1) + "pres";
              Label prestige = new Label(prestigeId, playerList.get(j % 4).getPrestige());
              attributesContainer.add(prestige);
            }
            break;
          case 3:
            for (int j = 0; j < size; j++) {
              String containerId = "attributes-container-" + (j + 1);
              WebMarkupContainer attributesContainer = new WebMarkupContainer(containerId);
              attributesContainer.setOutputMarkupId(true);
              attributesContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
              switch (j) {
                case 0:
                  p1container.add(attributesContainer);
                  break;
                case 1:
                  p2container.add(attributesContainer);
                  break;
                case 2:
                  p3container.add(attributesContainer);
                  break;
                case 3:
                  p4container.add(attributesContainer);
                  break;
              }


              String labelId = "p" + (j + 1);
              Label player = new Label(labelId, playerList.get((j + 1) % 4).getName());
              attributesContainer.add(player);
              String healthId = "p" + (j + 1) + "heal";
//            Label health = new Label(healthId, new PropertyModel<>(this, Integer.toString(playerList.get((j + 1) % 4).getHealth())));
//            Label health = new Label(healthId, new PropertyModel<>(playerList.get((j + 1) % 4), "health"));
              String currentId = "p" + (j + 1) + "health";
              Label health = new Label(healthId, new PropertyModel<>(this, currentId));
              health.setOutputMarkupId(true);
              attributesContainer.add(health);
              String prestigeId = "p" + (j + 1) + "pres";
              Label prestige = new Label(prestigeId, playerList.get((j + 1) % 4).getPrestige());
              attributesContainer.add(prestige);
            }
            break;
        }
      }
    }
  }

  /*
   * creates stack
   */
  private void createStackAndHeap() {
    WebMarkupContainer middleTableContainer = new WebMarkupContainer("middle-table-container");
    middleTableContainer.setOutputMarkupId(true);
    middleTableContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
    add(middleTableContainer);

    ListView<Card> stack = new ListView<Card>("stack", stackList) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = 85 - stackList.size();
        int posTop = 90 - stackList.size();
        item.add(new AttributeAppender("style", "left: " + (posLeft + 2 * item.getIndex()) + "px; top: " + (posTop + 2 * item.getIndex()) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    stack.setOutputMarkupId(true);
    middleTableContainer.add(stack);

    ListView<Card> heap = new ListView<Card>("heap", heapList) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        double rotation = 30 - 3 * item.getIndex();
        double direction = item.getIndex() % 2 == 0 ? 1 : -1;
        item.add(new AttributeAppender("style", "transform: rotate(" + (direction * rotation) + "deg);"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    heap.setOutputMarkupId(true);
    middleTableContainer.add(heap);
  }

  /*
   * creates player area for top player
   */
  private void createPlayer1Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
//    cardDropModels.add(Model.of(card5));

    p1container = new WebMarkupContainer("p1-container");
    p1container.setOutputMarkupId(true);
    p1container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p1container);
    ListView<String> turn = new ListView<String>("turn", p1turn) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<String> item) {
        item.add(new Label("item", item.getModelObject()));
      }
    };
    turn.setOutputMarkupId(true);
    p1container.add(turn);
    /*
     * player-card-container
     */
    playerCardContainer = new WebMarkupContainer("player-card-container1");
    playerCardContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
    playerCardContainer.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        System.out.println("drophand-1: " + players.get(0).getName());
        if (selectedCard != null && selectedCard.getSubTitle() == "--bug--") {
          playCard(players.get(0).getId(), selectedCard);
          selectedCard = null;
        }
//      if (user.isBeingAttacked() && selectedCard != null && selectedCard.getSubTitle() == "--lame excuse--") {
//        defendCard(selectedCard);
//        discardCard(p1drophand.get(0));
//        p1drophand.remove(0);
//        selectedCard = null;
//      }
      }
    });
    playerCardContainer.setOutputMarkupId(true);
    p1container.add(playerCardContainer);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container1");
    playerCardContainer.add(playableCardsContainer);
    /*
     * drop area
     */
    cardDropArea = new ListView<Card>("card-drop-area1", p1drophand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p1drophand.size() * 50) / (p1drophand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    cardHand = new ListView<Card>("card-hand1", p1hand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p1hand.size() * 50) / (p1hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container1");
    healthRoleContainer.setOutputMarkupId(true);
    playerCardContainer.add(healthRoleContainer);

    /*
     * mental health
     * TODO: how do we want to display the mental health?
     * TODO: show current mental health
     */
    Label health = new Label("health-player1", "mental health of player 1");
    healthRoleContainer.add(health);

    /*
     * role card TODO: put real role card here TODO: show or hide card depending
     * on player and card
     */
    ListView<Card> roleCard = new ListView<Card>("role-card-panel1", p1role) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    roleCard.setOutputMarkupId(true);
    healthRoleContainer.add(roleCard);
  }

  /*
   * creates player area for right player
   */
  private void createPlayer2Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>(); //TODO REMOVE?

    p2container = new WebMarkupContainer("p2-container");
    p2container.setOutputMarkupId(true);
    p2container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p2container);
    ListView<String> turn = new ListView<String>("turn", p2turn) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<String> item) {
        item.add(new Label("item", item.getModelObject()));
      }
    };
    turn.setOutputMarkupId(true);
    p2container.add(turn);
    /*
     * player-card-container
     */
    playerCardContainer2 = new WebMarkupContainer("player-card-container2");
    playerCardContainer2.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
    playerCardContainer2.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        System.out.println("drophand-2: " + players.get(1).getName());
        if (selectedCard != null && selectedCard.getSubTitle() == "--bug--") {
          playCard(players.get(1).getId(), selectedCard);
          selectedCard = null;
        }
//      if (user.isBeingAttacked() && selectedCard != null && selectedCard.getSubTitle() == "--lame excuse--") {
//        defendCard(selectedCard);
//        discardCard(p2drophand.get(0));
//        p2drophand.remove(0);
//        selectedCard = null;
//      }
      }
    });
    playerCardContainer2.setOutputMarkupId(true);
    p2container.add(playerCardContainer2);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container2");
    playerCardContainer2.add(playableCardsContainer);
    /*
     * drop area
     */
    cardDropArea2 = new ListView<Card>("card-drop-area2", p2drophand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p2drophand.size() * 50) / (p2drophand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea2.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea2);

    /*
     * card hand
     */
    cardHand2 = new ListView<Card>("card-hand2", p2hand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p2hand.size() * 50) / (p2hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    cardHand2.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand2);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container2");
    healthRoleContainer.setOutputMarkupId(true);
    playerCardContainer2.add(healthRoleContainer);

    /*
     * mental health TODO: how do we want to display the mental health? TODO:
     * show current mental health
     */
    Label health = new Label("health-player2", "mental health of player 2");
    healthRoleContainer.add(health);

    /*
     * role card TODO: put real role card here TODO: show or hide card depending
     * on player and card
     */
    ListView<Card> roleCard = new ListView<Card>("role-card-panel2", p2role) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    roleCard.setOutputMarkupId(true);
    healthRoleContainer.add(roleCard);
  }

  /*
   * creates player area for bottom player
   */
  private void createPlayer3Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
//    cardDropModels.add(Model.of(card5));

    p3container = new WebMarkupContainer("p3-container");
    p3container.setOutputMarkupId(true);
    p3container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p3container);
    ListView<String> turn = new ListView<String>("turn", p3turn) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<String> item) {
        item.add(new Label("item", item.getModelObject()));
      }
    };
    turn.setOutputMarkupId(true);
    p3container.add(turn);
    /*
     * player-card-container
     */
    playerCardContainer3 = new WebMarkupContainer("player-card-container3");
    playerCardContainer3.setOutputMarkupId(true);
    playerCardContainer3.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
    playerCardContainer3.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1l;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        System.out.println("drophand-3: " + players.get(2).getName());
        if (selectedCard != null) {
          // TODO bug delegation cards
        }
      }
    });
    p3container.add(playerCardContainer3);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container3");
    playerCardContainer3.add(playableCardsContainer);
    /*
     * drop area
     */
    cardDropArea3 = new ListView<Card>("card-drop-area3", p3drophand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p3drophand.size() * 50) / (p3drophand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
        item.add(new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            System.out.println("card: " + item.getModelObject());
          }
        });
      }
    };
    cardDropArea3.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea3);

    /*
     * card hand
     */
    cardHand3 = new ListView<Card>("card-hand3", p3hand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p3hand.size() * 50) / (p3hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
        item.add(new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            System.out.println("card: " + item.getModelObject());
            selectedCard = item.getModelObject();
          }
        });
      }
    };
    cardHand3.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand3);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container3");
    healthRoleContainer.setOutputMarkupId(true);
    playerCardContainer3.add(healthRoleContainer);

    /*
     * mental health TODO: how do we want to display the mental health? TODO:
     * show current mental health
     */
    Label health = new Label("health-player3", "mental health of player 3");
    healthRoleContainer.add(health);

    /*
     * role card TODO: put real role card here TODO: show or hide card depending
     * on player and card
     */
    ListView<Card> roleCard = new ListView<Card>("role-card-panel3", p3role) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        Card roleCard = item.getModelObject();
        roleCard.setVisible(true);
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(Model.of(roleCard))));
      }
    };
    roleCard.setOutputMarkupId(true);
    healthRoleContainer.add(roleCard);
  }

  /*
   * creates player area for left player
   */
  private void createPlayer4Area() {
    /*
     * create dummy card-model for player-card-container4
     */
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
//    cardDropModels.add(Model.of(card5));

    p4container = new WebMarkupContainer("p4-container");
    p4container.setOutputMarkupId(true);
    p4container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(p4container);
    ListView<String> turn = new ListView<String>("turn", p4turn) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<String> item) {
        item.add(new Label("item", item.getModelObject()));
      }
    };
    turn.setOutputMarkupId(true);
    p4container.add(turn);
    /*
     * player-card-container
     */
    playerCardContainer4 = new WebMarkupContainer("player-card-container4");
    playerCardContainer4.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
    playerCardContainer4.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        System.out.println("drophand-4: " + players.get(3).getName());
        if (selectedCard != null && selectedCard.getSubTitle() == "--bug--") {
          playCard(players.get(3).getId(), selectedCard);
          selectedCard = null;
        }
//      if (user.isBeingAttacked() && selectedCard != null && selectedCard.getSubTitle() == "--lame excuse--") {
//        defendCard(selectedCard);
//        discardCard(p4drophand.get(0));
//        p4drophand.remove(0);
//        selectedCard = null;
//      }
      }
    });
    playerCardContainer4.setOutputMarkupId(true);
    p4container.add(playerCardContainer4);

    /*
     * left side container includes card-drop-area and card-hand
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container4");
    playerCardContainer4.add(playableCardsContainer);
    /*
     * drop area
     */
    cardDropArea4 = new ListView<Card>("card-drop-area4", p4drophand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p4drophand.size() * 50) / (p4drophand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }

    };
    cardDropArea4.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea4);

    /*
     * card hand
     */
    cardHand4 = new ListView<Card>("card-hand4", p4hand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p4hand.size() * 50) / (p4hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    cardHand4.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand4);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container4");
    healthRoleContainer.setOutputMarkupId(true);
    playerCardContainer4.add(healthRoleContainer);

    /*
     * mental health TODO: how do we want to display the mental health? TODO:
     * show current mental health
     */
    Label health = new Label("health-player4", "mental health of player 4");
    healthRoleContainer.add(health);

    /*
     * role card TODO: put real role card here TODO: show or hide card depending
     * on player and card
     */
    ListView<Card> roleCard = new ListView<Card>("role-card-panel4", p4role) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    roleCard.setOutputMarkupId(true);
    healthRoleContainer.add(roleCard);
  }
}