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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@AuthenticationRequired
public class FourBoard extends GameView {
    /** UID for serialization. */
  private static final long serialVersionUID = 1L;
  private int numPlayer = 4;
  private AjaxButton testsetcards;
  
  /*
   * dummy cards
   */
  Card card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12;
  List<IModel<Card>> cardModels;
  
  Card p1Role, p2Role, p3Role, p4Role;
  
  Card p3Card1, p3Card2, p3Card3;
  List<Card> p3CardModel;
  
  Card otherCard1, otherCard2, otherCard3;
  List<Card> otherCardModel;
  
  Card stackCard1, stackCard2, stackCard3, stackCard4, stackCard5, stackCard6, stackCard7, stackCard8, stackCard9, stackCard10, stackCard11, stackCard12;
  List<IModel<Card>> stackModel;
  
  Card heapCard1, heapCard2, heapCard3, heapCard4, heapCard5, heapCard6, heapCard7, heapCard8, heapCard9, heapCard10, heapCard11, heapCard12;
  List<IModel<Card>> heapModel;
  
  WebMarkupContainer playerCardContainer, playerCardContainer2;
  List<Card> cardDropModels, cardDropModels2, cardDropModels3, cardDropModels4;
  ListView<Card> cardDropArea, cardDropArea2, cardDropArea3, cardDropArea4;
  Card selected1;
  ListView<Card> cardHand, cardHand2, cardHand3, cardHand4;
  
  public FourBoard() {
	testsetcards = new AjaxButton("button1") {
		/** UID for serialization. */
	    private static final long serialVersionUID = 1;

	    @Override
	    public void onSubmit(AjaxRequestTarget target) {
        System.out.println("add card");
        cardDropModels.add(card5);
        cardDropModels2.add(card5);
        cardDropModels3.add(card5);
        cardDropModels4.add(card5);
	    cardDropArea.setOutputMarkupId(true);
	    }
	};
	Form<?> formbutton = new Form<>("testbutton");
	formbutton.add(testsetcards);
	add(formbutton);
  	
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
    card7 = new Card("Role", "Manager", null, "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
    card8 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, null);
    card9 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
    card9.setVisible(!card9.isVisible());
    card10 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
    card11 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    card12 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    
    cardModels = new ArrayList<IModel<Card>>();
    cardModels.add(Model.of(card1));
    cardModels.add(Model.of(card2));
    cardModels.add(Model.of(card3));
    cardModels.add(Model.of(card4));
    cardModels.add(Model.of(card5));
    cardModels.add(Model.of(card6));
    cardModels.add(Model.of(card7));
    cardModels.add(Model.of(card8));
    cardModels.add(Model.of(card9));
    cardModels.add(Model.of(card10));
    cardModels.add(Model.of(card11));
    cardModels.add(Model.of(card12));
    
    p1Role = new Card("Role", "Manager", null, "Aim: Remove evil code \nmonkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
    p2Role = new Card("Role", "Consultant", null, "Aim: Get everyone else \nfired; Manager last!", "Tries to take over the \ncompany", false, false, null);
    p3Role = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, true, null);
    p4Role = new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null);
    
    p3Card1 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
    p3Card2 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
    p3Card3 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    p3CardModel = new ArrayList<Card>();
    p3CardModel.add(p3Card1);
    p3CardModel.add(p3Card2);
    p3CardModel.add(p3Card3);
    
    otherCard1 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, false, null);
    otherCard2 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, false, null);
    otherCard3 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, false, null);
    otherCardModel = new ArrayList<Card>();
    otherCardModel.add(otherCard1);
    otherCardModel.add(otherCard2);
    otherCardModel.add(otherCard3);
    
    stackCard1 = new Card("Role", "Manager", null, "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
    stackCard2 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, null);
    stackCard3 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
    stackCard4 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
    stackCard5 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    stackCard6 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    stackCard7 = new Card("Role", "Manager", null, "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
    stackCard8 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, null);
    stackCard9 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
    stackCard10 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
    stackCard11 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    stackCard12 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    
    stackModel = new ArrayList<IModel<Card>>();
  	stackModel.add(Model.of(stackCard1));
  	stackModel.add(Model.of(stackCard2));
  	stackModel.add(Model.of(stackCard3));
  	stackModel.add(Model.of(stackCard4));
  	stackModel.add(Model.of(stackCard5));
  	stackModel.add(Model.of(stackCard6));
  	stackModel.add(Model.of(stackCard7));
  	stackModel.add(Model.of(stackCard8));
  	stackModel.add(Model.of(stackCard9));
  	stackModel.add(Model.of(stackCard10));
  	stackModel.add(Model.of(stackCard11));
  	stackModel.add(Model.of(stackCard12));
  	stackModel.add(Model.of(stackCard1));
  	stackModel.add(Model.of(stackCard2));
  	stackModel.add(Model.of(stackCard3));
  	stackModel.add(Model.of(stackCard4));
  	stackModel.add(Model.of(stackCard5));
  	stackModel.add(Model.of(stackCard6));
  	stackModel.add(Model.of(stackCard7));
  	stackModel.add(Model.of(stackCard8));
  	stackModel.add(Model.of(stackCard9));
  	stackModel.add(Model.of(stackCard10));
  	stackModel.add(Model.of(stackCard11));
  	stackModel.add(Model.of(stackCard12));
  	
  	for (IModel<Card> cardModel: this.stackModel) {
  		cardModel.getObject().setVisible(false);
  	}
  	
  	heapCard1 = new Card("Role", "Manager", null, "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
    heapCard2 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, null);
    heapCard3 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
    heapCard4 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
    heapCard5 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    heapCard6 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    heapCard7 = new Card("Role", "Manager", null, "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null);
    heapCard8 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, null);
    heapCard9 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
    heapCard10 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
    heapCard11 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    heapCard12 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
    
    heapModel = new ArrayList<IModel<Card>>();
  	heapModel.add(Model.of(heapCard1));
  	heapModel.add(Model.of(heapCard2));
  	heapModel.add(Model.of(heapCard3));
  	heapModel.add(Model.of(heapCard4));
  	heapModel.add(Model.of(heapCard5));
  	heapModel.add(Model.of(heapCard6));
  	heapModel.add(Model.of(heapCard7));
  	heapModel.add(Model.of(heapCard8));
  	heapModel.add(Model.of(heapCard9));
  	heapModel.add(Model.of(heapCard10));
  	heapModel.add(Model.of(heapCard11));
  	heapModel.add(Model.of(heapCard12));
  	heapModel.add(Model.of(heapCard1));
  	heapModel.add(Model.of(heapCard2));
  	heapModel.add(Model.of(heapCard3));
  	heapModel.add(Model.of(heapCard4));
  	heapModel.add(Model.of(heapCard5));
  	heapModel.add(Model.of(heapCard6));
  	heapModel.add(Model.of(heapCard7));
  	heapModel.add(Model.of(heapCard8));
  	heapModel.add(Model.of(heapCard9));
  	heapModel.add(Model.of(heapCard10));
  	heapModel.add(Model.of(heapCard11));
  	heapModel.add(Model.of(heapCard12));
  	
  	for (IModel<Card> cardModel: this.heapModel) {
  		cardModel.getObject().setVisible(true);
  	}
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
				return stackModel.iterator();
			}
			
			int posLeft = 85 - stackModel.size();
			int posTop = 90 - stackModel.size();

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
				return heapModel.iterator();
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
    cardDropModels = new ArrayList<Card>();
    
    /*
     * player-card-container 
     */
    playerCardContainer = new WebMarkupContainer("player-card-container1");
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
    cardDropArea = new ListView<Card>("card-drop-area1", cardDropModels) {
			private static final long serialVersionUID = 1L;
			int width = 300;
			@Override
			protected void populateItem(ListItem<Card> item) {
				int posLeft = (width - cardDropModels.size() * 50) / (cardDropModels.size() + 1);
				item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
				item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
			}
    	
    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    cardHand = new ListView<Card>("card-hand1", otherCardModel) {
			private static final long serialVersionUID = 1L;
			int width = 300;
			@Override
			protected void populateItem(ListItem<Card> item) {
				int posLeft = (width - otherCardModel.size() * 50) / (otherCardModel.size() + 1);
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
    CardPanel roleCardPanel = new CardPanel("role-card-panel1", new Model<Card>(p1Role));
    healthRoleContainer.add(roleCardPanel);
  }

  /*
   * creates player area for right player
   */
  private void createPlayer2Area() {
  	/*
     * create dummy card-model for player-card-container4
     */
    cardDropModels2 = new ArrayList<Card>();
    
    /*
     * player-card-container 
     */
    playerCardContainer2 = new WebMarkupContainer("player-card-container2");
    playerCardContainer2.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
    playerCardContainer2.add(new AjaxEventBehavior("click") {
    	private static final long serialVersionUID = 1L;
		@Override
		protected void onEvent(AjaxRequestTarget target) {
			System.out.println("droparea2");
			if (selected1 != null) {
				cardDropModels2.add(selected1);
				selected1 = null;
			} else {}
		}
	});
    add(playerCardContainer2);
    
    /*
     * left side container includes card-drop-area and card-hand 
     */
    WebMarkupContainer playableCardsContainer = new WebMarkupContainer("playable-cards-container2");
    playerCardContainer2.add(playableCardsContainer);
    /*
     * drop area
     */
    cardDropArea2 = new ListView<Card>("card-drop-area2", cardDropModels2) {
			private static final long serialVersionUID = 1L;
			int width = 300;
			@Override
			protected void populateItem(ListItem<Card> item) {
				int posLeft = (width - cardDropModels2.size() * 50) / (cardDropModels2.size() + 1);
				item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
				item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
			}
    	
    };
    cardDropArea2.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea2);

    /*
     * card hand
     */
     cardHand = new ListView<Card>("card-hand2", otherCardModel) {
			private static final long serialVersionUID = 1L;
			int width = 300;
			@Override
			protected void populateItem(ListItem<Card> item) {
				int posLeft = (width - otherCardModel.size() * 50) / (otherCardModel.size() + 1);
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
    playerCardContainer2.add(healthRoleContainer);
    
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
    CardPanel roleCardPanel = new CardPanel("role-card-panel2", new Model<Card>(p2Role));
    healthRoleContainer.add(roleCardPanel);
  }

  /*
   * creates player area for bottom player
   */
  private void createPlayer3Area() {
  	/*
     * create dummy card-model for player-card-container4
     */
    cardDropModels3 = new ArrayList<Card>();
    
    /*
     * player-card-container 
     */
    playerCardContainer = new WebMarkupContainer("player-card-container3");
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
    cardDropArea3 = new ListView<Card>("card-drop-area3", cardDropModels3) {
			private static final long serialVersionUID = 1L;
			int width = 300;
			@Override
			protected void populateItem(ListItem<Card> item) {
				int posLeft = (width - cardDropModels3.size() * 50) / (cardDropModels3.size() + 1);
				item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
				item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
			}
    };
    cardDropArea3.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea3);

    /*
     * card hand
     */
    cardHand = new ListView<Card>("card-hand3", p3CardModel) {
			private static final long serialVersionUID = 1L;
			int width = 300;
			@Override
			protected void populateItem(ListItem<Card> item) {
				int posLeft = (width - p3CardModel.size() * 50) / (p3CardModel.size() + 1);
				item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
				item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
				item.add(new AjaxEventBehavior("click") {
					private static final long serialVersionUID = 1L;
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						System.out.println("card: " + item.getModelObject());
						selected1 = item.getModelObject();
						//cardDropModels2.add(selected1);
					}
				});
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
    CardPanel roleCardPanel = new CardPanel("role-card-panel3", new Model<Card>(p3Role));
    healthRoleContainer.add(roleCardPanel);
  }
  
  /*
   * creates player area for left player
   */
  private void createPlayer4Area() {
  	/*
     * create dummy card-model for player-card-container4
     */
    cardDropModels4 = new ArrayList<Card>();
    
    /*
     * player-card-container 
     */
    playerCardContainer = new WebMarkupContainer("player-card-container4");
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
    cardDropArea = new ListView<Card>("card-drop-area4", cardDropModels4) {
			private static final long serialVersionUID = 1L;
			int width = 300;
			@Override
			protected void populateItem(ListItem<Card> item) {
				int posLeft = (width - cardDropModels4.size() * 50) / (cardDropModels4.size() + 1);
				item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
				item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
			}
    	
    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    cardHand = new ListView<Card>("card-hand4", otherCardModel) {
			private static final long serialVersionUID = 1L;
			int width = 300;
			@Override
			protected void populateItem(ListItem<Card> item) {
				int posLeft = (width - otherCardModel.size() * 50) / (otherCardModel.size() + 1);
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
    CardPanel roleCardPanel = new CardPanel("role-card-panel4", new Model<Card>(p4Role));
    healthRoleContainer.add(roleCardPanel);
  }
}