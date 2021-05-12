package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.SQLDatabase;
import de.lmu.ifi.sosy.tbial.db.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.time.Duration;

/**
 * Basic lobby page. It <b>should</b> show the list of currently available games. Needs to be
 * extended.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
@AuthenticationRequired
public class Lobby extends BasePage {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  public Lobby() {
    //setDefaultModel(new Model<>("tabpanel"));


    final List<ITab> tabs = new ArrayList<>();
    tabs.add(
        new AbstractTab(new Model<>("Online Players")) {
          private static final long serialVersionUID = 1L;

          @Override
          public Panel getPanel(String panelId) {
            return new TabPanel1(panelId);
          }
        });

    tabs.add(
        new AbstractTab(new Model<>("Games")) {
          private static final long serialVersionUID = 1L;

          @Override
          public Panel getPanel(String panelId) {
            return new TabPanel2(panelId);
          }
        });

    tabs.add(
        new AbstractTab(new Model<>("Create Game")) {
          private static final long serialVersionUID = 1L;

          @Override
          public Panel getPanel(String panelId) {
            return new TabPanel3(panelId);
          }
        });

    final TabbedPanel<ITab> tabbedPanel = new TabbedPanel<>("tabs", tabs);
    tabbedPanel.add(AttributeModifier.replace("class", Lobby.this.getDefaultModel()));
    add(tabbedPanel);


  }

  private class TabPanel1 extends Panel {

    public TabPanel1(String id) {
      super(id);
      IModel<List<User>> playerModel =
          (IModel<List<User>>) () -> getTbialApplication().getLoggedInUsers();
      ListView<User> playerList =
          new PropertyListView<>("loggedInUsers", playerModel) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<User> listItem) {
              listItem.add(new Label("name"));
            }
          };
      WebMarkupContainer playerListContainer = new WebMarkupContainer("playerlistContainer");
      playerListContainer.add(playerList);
      playerListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
      playerListContainer.setOutputMarkupId(true);

      add(playerListContainer);

    }
  }
  ;

  private class TabPanel2 extends Panel {
    public TabPanel2(String id) {
      super(id);

      IModel<List<Game>> gameModel =
          (IModel<List<Game>>) () -> getTbialApplication().getAvailableGames();
      ListView<Game> gameList = new PropertyListView<>("availableGames", gameModel) {
          private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<Game> listItem) {
              listItem.add(new Label("name"));
            }
          };
      WebMarkupContainer gameListContainer = new WebMarkupContainer("gamelistContainer");
      gameListContainer.add(gameList);
      gameListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
      gameListContainer.setOutputMarkupId(true);

      add(gameListContainer);
    }
  }
  ;
    
  
  private class TabPanel3 extends Panel {
      /** UID for serialization. */
      private static final long serialVersionUID = 1L;

      private final Logger LOGGER = LogManager.getLogger(Lobby.class);

      private final Button submitGame;
      private final Button cancelGame;
      private final TextField<String> gameName;
      private List players = Arrays.asList(new Integer[] {4, 5, 6, 7});
      public Integer selected = 4;
      private final CheckBox publicGame;
      private final Model<Boolean> checkboxState = Model.of(true);
      private final TextField<String> password;
      private final DropDownChoice choice;

    public TabPanel3(String id) {

      super(id);
//        Form<?> form1 = new Form<>("creating");
//        add(form1);

        add(new FeedbackPanel("feedback"));

        gameName = new TextField<>("name", new Model<>(""));
        gameName.setRequired(true);
        choice = new DropDownChoice("ddc", new PropertyModel(this, "selected"), players);

        password = new TextField<String>("password", Model.of("")){
            @Override
            public boolean isEnabled() {
                return !checkboxState.getObject();
            }
        };
        password.setOutputMarkupId(true);
        password.setRequired(true);
        publicGame = new AjaxCheckBox("publicGame", checkboxState) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(password);
            }
        };

        submitGame = new Button("submitgame") {
            /** UID for serialization. */
            private static final long serialVersionUID = 1;

            public void onSubmit() {
                String name = gameName.getModelObject();
                String host = ((TBIALSession)getSession()).getUser().getName();
                String pw = "";
                String gamestate = "new";
                int numplayers = selected;
                String pub = publicGame.getModelObject().toString();
                if(!publicGame.getModelObject()) {
                    pw = password.getModelObject();
                }
                //info("name: " + name + " pub: " + pub + " pw: " + pw + " host: " + host + " player: " + numplayers);
                performCreation(name, host, pw, gamestate, numplayers);
            }
        };
        cancelGame = new Button("cancelgame") {
            /** UID for serialization. */
            private static final long serialVersionUID = 1;

            public void onSubmit() {
                setResponsePage(getApplication().getHomePage());
            }
        };
        cancelGame.setDefaultFormProcessing(false);


        Form<?> form = new Form<>("create");
        form.add(gameName).add(submitGame).add(choice).add(cancelGame).add(password, publicGame);
        add(form);
    }
      public void performCreation(String name, String host, String pw, String gamestate, int numplayers) {
          Game game = ((SQLDatabase) getDatabase()).createGame(name, host, pw, gamestate, numplayers);
          if (game != null) {
              setResponsePage(getApplication().getHomePage());
              info("Registration successful! You are now logged in.");
              LOGGER.info("New game '" + name + "' created");
          } else {
              error("could not create game");
              LOGGER.debug("New game '" + name + "' creation failed");
          }
      }
  }
  ;

}
