package de.lmu.ifi.sosy.tbial;

import java.util.List;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;

public class GameLobby extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;
  
  private final Button leaveButton;
  
  private final Button startButton;
  
  private Game game;
  
  private User user;

  public GameLobby() {
  	
  	game = new Game("newGame is the game name", "pw", 5, new User("host", "pw"));
  	game.addPlayer(new User("Player 2", "pw"));
  	game.addPlayer(new User("Player 3", "pw"));
  		
  	final Label label = new Label("gamename", game.getName());
  	add(label);
  	
  	leaveButton = new Button("leavebutton") {

      /** UID for serialization. */
      private static final long serialVersionUID = 1;

      public void onSubmit() {
        System.out.println("leavebutton");
      }
  	};
  	
  	startButton = new Button("startbutton") {

      /** UID for serialization. */
      private static final long serialVersionUID = 1;

      public void onSubmit() {
        System.out.println("startbutton");
      }
  	};
  	
  	Form<?> form = new Form<>("form");
  	form.add(leaveButton).add(startButton);
  	add(form);
	  
    // get joined Users / Players
		IModel<List<User>> playerModel = (IModel<List<User>>) () -> getTbialApplication().getLoggedInUsers();
	    
    ListView<User> joinedPlayerList = new PropertyListView<>("joinedPlayers", game.getPlayers()) {
 
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(final ListItem<User> listItem) {
      	if (listItem.getModelObject() == null) {
      		listItem.add(new Label("name", "free spot"));
      	} else {
      		listItem.add(new Label("name"));
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
