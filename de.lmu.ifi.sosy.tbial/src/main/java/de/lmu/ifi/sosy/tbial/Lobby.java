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
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

/**
 * Basic lobby page. It <b>should</b> show the list of currently available games. Needs to be
 * extended.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
@AuthenticationRequired
public class Lobby extends BasePage {
    protected final AbstractTab tab1;
    protected final AbstractTab tab2;
    protected final AbstractTab tab3;
    protected final AbstractTab tab4;
    protected final List<ITab> tabs;
    protected final AjaxTabbedPanel<ITab> tabbedPanel;
    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    public Lobby() {

        tabs = new ArrayList<>();
        tab1 = new AbstractTab(new Model<>("Online Players")) {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getPanel(String panelId) {
                return new TabPanel1(panelId);
            }
        };
        tab2 = new AbstractTab(new Model<>("Games")) {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getPanel(String panelId) {
                return new TabPanel2(panelId);
            }
        };
        tab3 = new AbstractTab(new Model<>("Create Game")) {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getPanel(String panelId) {
                return new TabPanel3(panelId);
            }
        };
        tab4 = new AbstractTab(new Model<>("Lobby")) {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getPanel(String panelId) {
                return new TabPanel4(panelId);
            }
        };
        tabs.add(tab1);
        tabs.add(tab2);
        if (!((TBIALSession) getSession()).getUser().getJoinedGame()) {
            tabs.add(tab3);
        } else {
            tabs.add(tab4);
        }


        tabbedPanel = new AjaxTabbedPanel<>("tabs", tabs);
        tabbedPanel.add(AttributeModifier.replace("class", Lobby.this.getDefaultModel()));
        add(tabbedPanel);
    }



    private class TabPanel1 extends Panel {
    	 	/** UID for serialization. */
      	private static final long serialVersionUID = 1L;

        public TabPanel1(String id) {
            super(id);

            add(new FeedbackPanel("feedback"));
            IModel<List<User>> playerModel =
                    (IModel<List<User>>) () -> getTbialApplication().getLoggedInUsers();
            PageableListView<User> playerList =
                    new PageableListView<User>("loggedInUsers", playerModel, 4) {

                        private static final long serialVersionUID = 1L;

                        @Override
                        protected void populateItem(final ListItem<User> listItem) {

                            listItem.add(new Label("name", new PropertyModel<>(listItem.getModel(), "name")));
                            listItem.add(new Label("status", "in game"));
                        }
                    };
            Form<?> form = new Form<>("onlinePlayers");
            form.add(playerList);
            form.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
            form.setOutputMarkupId(true);
            form.add(new PagingNavigator("navigator", playerList));
            add(form);

        }
    };


    private class TabPanel2 extends Panel {
    	 	/** UID for serialization. */
    		private static final long serialVersionUID = 1L;


        public TabPanel2(String id) {
            super(id);

            add(new FeedbackPanel("feedback"));

            IModel<List<Game>> gameModel =
                    (IModel<List<Game>>) () -> getTbialApplication().getAvailableGames();

            PageableListView<Game> gameList = new PageableListView<>("availableGames", gameModel, 4) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(final ListItem<Game> listItem) {
                    listItem.add(new Label("name", new PropertyModel<>(listItem.getModel(), "name")));
                    listItem.add(new Label("players", listItem.getModelObject().getActivePlayers() + "/" + listItem.getModelObject().getNumPlayers()));
                    listItem.add(new Label("status", listItem.getModelObject().getGameState()));
                    listItem.add(new Label("protection", !listItem.getModelObject().getPwProtected()  ? "Public" : "Private"));
                    listItem.add(new Link<>("joinGame") {
                    	 	/** UID for serialization. */
                    		private static final long serialVersionUID = 1L;


                        public void onClick() {
                            User user = ((TBIALSession) getSession()).getUser();
                            if (!user.getJoinedGame()) {
                                listItem.getModelObject().addPlayer(user);
                                user.setGame(listItem.getModelObject());
                                user.setJoinedGame(true);
                                listItem.setOutputMarkupId(true);
                                tabs.remove(2);
                                tabs.add(tab4);
                                tabbedPanel.setSelectedTab(2);

                            } else {

                            }
                        }
                    });
                    User user = ((TBIALSession) getSession()).getUser();
                    if(user.getGame()!= null && listItem.getModelObject().getName().equals(user.getGame().getName())) {
                        listItem.add(new AttributeModifier("class", Model.of("currentGame")));
                    }
                }
            };

            gameList.setOutputMarkupId(true);
            Form<?> form = new Form<>("gamelist");
            form.add(gameList);
            form.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
            form.setOutputMarkupId(true);
            form.add(new PagingNavigator("navigator", gameList));
            add(form);


        }
    };


    private class TabPanel3 extends Panel {
        /**
         * UID for serialization.
         */
        private static final long serialVersionUID = 1L;

        private final Logger LOGGER = LogManager.getLogger(Lobby.class);

        private final Button submitGame;
        private final Button cancelGame;
        private final TextField<String> gameName;
        private List players = Arrays.asList(new Integer[]{4, 5, 6, 7});
        public Integer selected = 4;
        private final CheckBox publicGame;
        private final Model<Boolean> checkboxState = Model.of(true);
        private final TextField<String> password;
        private final DropDownChoice choice;

        private TabPanel3(String id) {

            super(id);
//        Form<?> form1 = new Form<>("creating");
//        add(form1);

            add(new FeedbackPanel("feedback"));

            gameName = new TextField<>("name", new Model<>(""));
            gameName.setRequired(true);
            choice = new DropDownChoice("ddc", new PropertyModel(this, "selected"), players);

            password = new TextField<String>("password", Model.of("")) {
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
                    String host = ((TBIALSession) getSession()).getUser().getName();
                    String pw = "";
                    String gamestate = "new";
                    int numplayers = selected;
                    String pub = publicGame.getModelObject().toString();
                    if (!publicGame.getModelObject()) {
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
                getTbialApplication().addGame(game);
                User user = ((TBIALSession) getSession()).getUser();
                user.setJoinedGame(true);
                user.setGame(game);
                tabs.remove(2);
                tabs.add(tab4);
                tabbedPanel.setSelectedTab(2);

            } else {
                error("could not create game");
                LOGGER.debug("New game '" + name + "' creation failed");
            }
        }
    };


    private class TabPanel4 extends Panel {
        /** UID for serialization. */
        private static final long serialVersionUID = 1L;

        private final AjaxButton leaveButton;
        private final AjaxButton startButton;
        private Game game;
        private User user;

        public TabPanel4(String id) {
            super(id);

            user = ((TBIALSession)getSession()).getUser();
            game = user.getGame();
//            game.addPlayer(new User("Player 2", "pw",null));
//            game.addPlayer(new User("Player 3", "pw",null));

//    	game = getSession().getGame(gameId);

            final Label label = new Label("gamename", game.getName());
            add(label);

            /**
             * self updating game status
             */
            Label gameState = new Label("gamestate", new PropertyModel<String>(game, "gameState"));
//    	add(gameState);
            gameState.setOutputMarkupId(true);
            gameState.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));

            leaveButton = new AjaxButton("leavebutton") {

                /** UID for serialization. */
                private static final long serialVersionUID = 1;

                @Override
                public void onSubmit(AjaxRequestTarget target) {
                    System.out.println("leavebutton");
                    game.removePlayer(user);
                    user.setGame(null);
                    user.setJoinedGame(false);
                    tabs.remove(2);
                    tabs.add(tab3);
                    setResponsePage(getApplication().getHomePage());
                    tabbedPanel.setSelectedTab(1);
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
                    game.addPlayer(new User("new Player", "pw",null));
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
                            listItem.getModelObject().setGame(null);
                            listItem.getModelObject().setJoinedGame(false);
                            game.removePlayer(listItem.getModelObject());
                            setResponsePage(getApplication().getHomePage());
//                            setButtonInactive();
                        }

                        @Override
                        public MarkupContainer setDefaultModel(IModel<?> model) {
                            // TODO Auto-generated method stub
                            return null;
                        }
                    });
//
//                    void setButtonInactive() {
//                    	removePlayerButton.add(AttributeModifier.replace("class", "button-inactive"));
//                    };
//
                    listItem.add(removePlayerButton);
                    removePlayerButton.setVisible(false);
                    if (listItem.getModelObject() == null) {
                        listItem.add(new Label("name", "free spot"));
                    } else {
                        listItem.add(new Label("name"));
                        if (user.equals(game.getHost()) && !user.equals(listItem.getModelObject())) {
                            removePlayerButton.setVisible(true);
                        }
                    }
                }
            };

            WebMarkupContainer joinedPlayerListContainer = new WebMarkupContainer("joinedPlayerListContainer");
            joinedPlayerListContainer.add(joinedPlayerList);
            joinedPlayerListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(10)));
            joinedPlayerListContainer.setOutputMarkupId(true);

//      add(joinedPlayerListContainer);

            WebMarkupContainer boxedGameLobby = new WebMarkupContainer("boxedGameLobby");
            boxedGameLobby.add(gameState).add(form).add(joinedPlayerListContainer);
            add(boxedGameLobby);


        }

    };
}

