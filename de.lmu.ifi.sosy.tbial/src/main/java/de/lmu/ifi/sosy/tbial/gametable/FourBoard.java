package de.lmu.ifi.sosy.tbial.gametable;

//import de.lmu.ifi.sosy.tbial.db.User;

import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.db.Card;
import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.util.ModelIteratorAdapter;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

@AuthenticationRequired
public class FourBoard extends GameView {
  /**
   * UID for serialization.
   */
  private static final long serialVersionUID = 1L;
  private int numPlayer = 4;

  public FourBoard() {
    super();
    
    assignLabels();

    createStackAndHeap();

    createPlayer1Area();
    createPlayer2Area();
    createPlayer3Area();
    createPlayer4Area();
  }

  /*
   * assign labels for player names
   */
  private void assignLabels() {
  	
    int size = playerList.size();
    for (int i = 0; i < size; i++) {
      if (playerList.get(i).getId() == user.getId()) {
        switch(i) {
        case 0:
        	for (int j = 0; j < size; j++) {
        		String labelId = "p" + (j + 1);
            Label player = new Label(labelId, playerList.get((j + 2) % 4).getName());
            add(player);
        	}
        	break;
        case 1:
        	for (int j = 0; j < size; j++) {
        		String labelId = "p" + (j + 1);
            Label player = new Label(labelId, playerList.get((j + 3) % 4).getName());
            add(player);
        	}
        	break;
        case 2:
        	for (int j = 0; j < size; j++) {
        		String labelId = "p" + (j + 1);
            Label player = new Label(labelId, playerList.get(j % 4).getName());
            add(player);
        	}
        	break;
        case 3:
        	for (int j = 0; j < size; j++) {
        		String labelId = "p" + (j + 1);
            Label player = new Label(labelId, playerList.get((j + 1) % 4).getName());
            add(player);
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

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container1");
    playerCardContainer.setOutputMarkupId(true);
    playerCardContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
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
    ListView<Card> cardHand = new ListView<Card>("card-hand1", p1hand) {
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
     * mental health TODO: how do we want to display the mental health? TODO:
     * show current mental health
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
    List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container2");
    playerCardContainer.setOutputMarkupId(true);
    playerCardContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
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
    ListView<Card> cardHand = new ListView<Card>("card-hand2", p2hand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p2hand.size() * 50) / (p2hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container2");
    healthRoleContainer.setOutputMarkupId(true);
    playerCardContainer.add(healthRoleContainer);

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

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container3");
    playerCardContainer.setOutputMarkupId(true);
    playerCardContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
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
    ListView<Card> cardHand = new ListView<Card>("card-hand3", p3hand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p3hand.size() * 50) / (p3hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container3");
    healthRoleContainer.setOutputMarkupId(true);
    playerCardContainer.add(healthRoleContainer);

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

    /*
     * player-card-container
     */
    WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container4");
    playerCardContainer.setOutputMarkupId(true);
    playerCardContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
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
    ListView<Card> cardHand = new ListView<Card>("card-hand4", p4hand) {
      private static final long serialVersionUID = 1L;
      int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p4hand.size() * 50) / (p4hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
      }
    };
    cardHand.setOutputMarkupId(true);
    playableCardsContainer.add(cardHand);

    /*
     * container of right side
     */
    WebMarkupContainer healthRoleContainer = new WebMarkupContainer("health-role-container4");
    healthRoleContainer.setOutputMarkupId(true);
    playerCardContainer.add(healthRoleContainer);

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