package de.lmu.ifi.sosy.tbial;

import java.util.List;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;

public class GameLobby extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;
  
  private final AjaxButton leaveButton;
  
  private final AjaxButton startButton;
  
  private Game game;
  
  private User user;

//  public GameLobby(int gameId) {
  public GameLobby() {
  	
  	user = getSession().getUser();
  	game = new Game("newGame is the game name", "pw", 6, user);
  	game.addPlayer(new User("Player 2", "pw"));
  	game.addPlayer(new User("Player 3", "pw"));
  	
//  	game = getSession().getGame(gameId);
  		
  	final Label label = new Label("gamename", game.getName());
  	add(label);
  	
  	/**
  	 * self updating game status
  	 */
  	Label gameState = new Label("gamestate", new PropertyModel<String>(game, "gameState"));
  	add(gameState);
  	gameState.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
  	
  	leaveButton = new AjaxButton("leavebutton") {

      /** UID for serialization. */
      private static final long serialVersionUID = 1;

      @Override
      public void onSubmit(AjaxRequestTarget target) {
        System.out.println("leavebutton");
      }

      @Override
      protected void onError(AjaxRequestTarget target) {}
  	};
  	
  	startButton = new AjaxButton("startbutton") {

      /** UID for serialization. */
      private static final long serialVersionUID = 1;

      @Override
      public void onSubmit(AjaxRequestTarget target) {
        System.out.println("startbutton");
        game.addPlayer(new User("new Player", "pw"));
        System.out.println(game.getGameState());
        System.out.println(game.getPlayers().toString());
      }
      
      @Override
      protected void onError(AjaxRequestTarget target) {}
  	};
  	
  	Form<?> form = new Form<>("form");
  	form.add(leaveButton).add(startButton);
  	add(form);
  	 
    ListView<User> joinedPlayerList = new PropertyListView<>("joinedPlayers", game.getPlayers()) {
 
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(final ListItem<User> listItem) {
      	AjaxLink<Object> removePlayerButton = (new AjaxLink<Object>("removeplayerbutton") {

          /** UID for serialization. */
          private static final long serialVersionUID = 1;

          @Override
          public void onClick(AjaxRequestTarget target) {
            System.out.println("remove player button");
            game.removePlayer(listItem.getModelObject());
          }

					@Override
					public MarkupContainer setDefaultModel(IModel<?> model) {
						// TODO Auto-generated method stub
						return null;
					}
    		});
      	listItem.add(removePlayerButton);
    		removePlayerButton.setVisible(false);
      	if (listItem.getModelObject() == null) {
      		listItem.add(new Label("name", "free spot"));
      	} else {
      		listItem.add(new Label("name"));
      		if (user == game.getHost() && user != listItem.getModelObject()) {
      			removePlayerButton.setVisible(true);
      		}
      	}
      }
    };
    
    WebMarkupContainer joinedPlayerListContainer = new WebMarkupContainer("joinedPlayerListContainer");
    joinedPlayerListContainer.add(joinedPlayerList);
    joinedPlayerListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
    joinedPlayerListContainer.setOutputMarkupId(true);

    add(joinedPlayerListContainer);
  }
  
}
