package de.lmu.ifi.sosy.tbial.gametable;

//import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.db.Card;

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

@AuthenticationRequired
public class FourBoard extends GameView {
    /** UID for serialization. */
  private static final long serialVersionUID = 1L;
  private int numPlayer = 4;
  
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
  
  public FourBoard() {
  	
  	Label player1 = new Label("p1", "player1-name");
  	add(player1);
  	Label player2 = new Label("p2", "player2-name");
  	add(player2);
  	Label player3 = new Label("p3", "player3-name");
  	add(player3);
  	Label player4 = new Label("p4", "player4-name");
  	add(player4);
  	
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

  }
  
  /*
   * creates dummy cards, can be removed later
   */
  private void createDummyCards() {
  	card1 = new Card("Role", "Manager", null, "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
    card2 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, null);
    card3 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
    card3.setVisible(!card3.isVisible());
    card4 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
    card5 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    card6 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    
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
   * creates player area for top player
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
   * creates player area for right player
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
   * creates player area for bottom player
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
   * creates player area for left player
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
}