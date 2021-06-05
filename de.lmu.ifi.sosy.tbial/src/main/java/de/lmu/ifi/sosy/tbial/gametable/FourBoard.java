package de.lmu.ifi.sosy.tbial.gametable;

//import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.*;
import de.lmu.ifi.sosy.tbial.db.Card;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
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
   

  }
  
  /*
   * creates dummy cards, can be removed later
   */
  private void createDummyCards() {
  	card1 = new Card("Role", "Manager", null, "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, false, null);
//    CardPanel cardPanel1 = new CardPanel("card-panel1", new Model<Card>(card1));
//    add(cardPanel1);
    
    card2 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, null);
//    CardPanel cardPanel2 = new CardPanel("card-panel2", Model.of(card2));
//    add(cardPanel2);
    
    card3 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
    card3.setVisible(!card3.isVisible());
//    CardPanel cardPanel3 = new CardPanel("card-panel3", Model.of(card3));
//    add(cardPanel3);
    
    card4 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
//    CardPanel cardPanel4 = new CardPanel("card-panel4", new CompoundPropertyModel<Card>(card4));
//    add(cardPanel4);
    
    card5 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
//    CardPanel cardPanel5 = new CardPanel("card-panel5", Model.of(card5));
//    add(cardPanel5);
    
    card6 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
//    CardPanel cardPanel6 = new CardPanel("card-panel6", Model.of(card6));
//    add(cardPanel6);
    
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
   * TODO: make sure that all cards show back side
   */
  private void createStack() {
    RefreshingView<Card> stack = new RefreshingView<Card>("stack") {
			private static final long serialVersionUID = 1L;

			@Override
			protected Iterator<IModel<Card>> getItemModels() {
				return cardModels.iterator();
			}
			
			int posLeft = 35 - cardModels.size();
			int posTop = 45 - cardModels.size();

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
   * make sure, that all cards show front side
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
}