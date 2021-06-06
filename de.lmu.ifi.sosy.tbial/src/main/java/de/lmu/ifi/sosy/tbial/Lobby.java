package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Card;
import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.SQLDatabase;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.gametable.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.lmu.ifi.sosy.tbial.networking.JSONMessage;
import de.lmu.ifi.sosy.tbial.networking.WebSocketManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
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
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.time.Duration;
import org.json.JSONObject;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

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
    private Game game;
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

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    private class TabPanel1 extends Panel {
        /**
         * UID for serialization.
         */
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
                        listItem.add(new Label("status", listItem.getModelObject().getJoinedGame() ? "inGame" : "free"));
                    }
                };
            Form<?> form = new Form<>("onlinePlayers");
            form.add(playerList);
            form.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
            form.setOutputMarkupId(true);
            form.add(new PagingNavigator("navigator", playerList));
            add(form);
            
//            PropertyModel<Card> cardModel = new PropertyModel<Card>(children, id);
//            CardPanel card = new CardPanel("card-panel", cardModel);
//            WebMarkupContainer cardtest = new WebMarkupContainer("cardtestcontainer");
//            add(cardtest);
            
            Card card1 = new Card("Role", "Manager", null, "Aim: Remove evil code monkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, false, null);
            CardPanel cardPanel1 = new CardPanel("card-panel1", new Model<Card>(card1));
            add(cardPanel1);
            
            Card card2 = new Card("Character", "Steve Jobs", "Founder of Apple", "(Mental Health 4)", "Gets a second chance", false, true, null);
            CardPanel cardPanel2 = new CardPanel("card-panel2", Model.of(card2));
            add(cardPanel2);
            
            Card card3 = new Card("Action", "System Integration", null, null, "My code is better than yours!", true, true, null);
            card3.setVisible(!card3.isVisible());
            CardPanel cardPanel3 = new CardPanel("card-panel3", Model.of(card3));
            add(cardPanel3);
            
            Card card4 = new Card("Ability", "Bug Delegation", null, null, "Delegates bug report\n.25 chance to work", true, true, null);
            CardPanel cardPanel4 = new CardPanel("card-panel4", new CompoundPropertyModel<Card>(card4));
            add(cardPanel4);
            
            Card card5 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
            CardPanel cardPanel5 = new CardPanel("card-panel5", Model.of(card5));
            add(cardPanel5);
            
            Card card6 = new Card("StumblingBlock", "Fortran Maintenance", "BOOM", "Stumbling Block", "Only playable on self.\nTakes 3 health points\n.85 chance to deflect to next developer", true, true, null);
            CardPanel cardPanel6 = new CardPanel("card-panel6", Model.of(card6));
            add(cardPanel6);
            
            List<IModel<Card>> cardModels = new ArrayList<IModel<Card>>();
            cardModels.add(Model.of(card1));
            cardModels.add(Model.of(card2));
            cardModels.add(Model.of(card3));
            cardModels.add(Model.of(card4));
            cardModels.add(Model.of(card5));
            cardModels.add(Model.of(card6));
            
            /*
             * heap
             */
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
            
            /*
             * stack
             */
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
      
            /*
             * card drop area
             */
            List<IModel<Card>> cardDropModels = new ArrayList<IModel<Card>>();
            cardDropModels.add(Model.of(card4));
            cardDropModels.add(Model.of(card2));
            
            /*
             * player card container includes card-drop-area and card-hand 
             */
            WebMarkupContainer playerCardContainer = new WebMarkupContainer("player-card-container");
            add(playerCardContainer);
            RefreshingView<Card> cardDropArea = new RefreshingView<Card>("card-drop-area") {
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
//								posTop += 2;
								item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
							}
            	
            };
            cardDropArea.setOutputMarkupId(true);
            playerCardContainer.add(cardDropArea);
            
            /*
             * card hand
             */
            List<IModel<Card>> cardHandModels = new ArrayList<IModel<Card>>();
            cardHandModels.add(Model.of(card1));
            cardHandModels.add(Model.of(card2));
            cardHandModels.add(Model.of(card3));
            cardHandModels.add(Model.of(card4));
            cardHandModels.add(Model.of(card5));
//            cardHandModels.add(Model.of(card6));
            
            RefreshingView<Card> cardHand = new RefreshingView<Card>("card-hand") {
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
//								posTop += 2;
								item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
							}
            	
            };
            cardHand.setOutputMarkupId(true);
            playerCardContainer.add(cardHand);
            
            /*
             * includes cards for second player-card-container
             */
            List<IModel<Card>> cardDropModels2 = new ArrayList<IModel<Card>>();
            cardDropModels2.add(Model.of(card5));
            
            /*
             * second player-card-container includes card-drop-area and card-hand 
             * this is for testing drag and drop
             */
            WebMarkupContainer playerCardContainer2 = new WebMarkupContainer("player-card-container2");
            add(playerCardContainer2);
            RefreshingView<Card> cardDropArea2 = new RefreshingView<Card>("card-drop-area2") {
							private static final long serialVersionUID = 1L;

							@Override
							protected Iterator<IModel<Card>> getItemModels() {
								return cardDropModels2.iterator();
							}
							int width = 300;
							
							int posLeft = (width - cardDropModels2.size() * 50) / (cardDropModels2.size() + 1);
							int stepSize = posLeft + 50;

							@Override
							protected void populateItem(Item<Card> item) {
								item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
								posLeft += stepSize;
//								posTop += 2;
								item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
							}
            	
            };
            cardDropArea2.setOutputMarkupId(true);
            playerCardContainer2.add(cardDropArea2);

            List<IModel<Card>> cardHandModels2 = new ArrayList<IModel<Card>>();
            cardHandModels2.add(Model.of(card2));
            cardHandModels2.add(Model.of(card3));
            cardHandModels2.add(Model.of(card4));
            cardHandModels2.add(Model.of(card5));
            
            RefreshingView<Card> cardHand2 = new RefreshingView<Card>("card-hand2") {
							private static final long serialVersionUID = 1L;

							@Override
							protected Iterator<IModel<Card>> getItemModels() {
								return cardHandModels2.iterator();
							}
							int width = 300;
							
							int posLeft = (width - cardHandModels2.size() * 50) / (cardHandModels2.size() + 1);
							int stepSize = posLeft + 50;

							@Override
							protected void populateItem(Item<Card> item) {
								item.add(new AttributeAppender("style", "left: " + posLeft + "px;"));
								posLeft += stepSize;
//								posTop += 2;
								item.add(new CardPanel("card", new CompoundPropertyModel<Card>(item.getModel())));
							}
            	
            };
            cardHand2.setOutputMarkupId(true);
            playerCardContainer2.add(cardHand2);

        }
    }

    ;


    private class TabPanel2 extends Panel {
        /**
         * UID for serialization.
         */
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
                    listItem.add(new Label("protection", !listItem.getModelObject().getPwProtected() ? "Public" : "Private"));
                    listItem.add(new Link<>("joinGame") {
                        /** UID for serialization. */
                        private static final long serialVersionUID = 1L;


                        public void onClick() {
                            User user = ((TBIALSession) getSession()).getUser();
                            if (!user.getJoinedGame()) {
                                Game game = listItem.getModelObject();
                                joinGame(game, user);
                                if (game.isGameStarted()) {
                                    int numplayers = game.getNumPlayers();
                                    if(numplayers == 4) {
                                        setResponsePage(FourBoard.class);
                                    } else if(numplayers == 5) {
                                    		setResponsePage(FiveBoard.class);
                                    } else if(numplayers == 6) {
                                        setResponsePage(SixBoard.class);
                                    } else if(numplayers == 7) {
                                        setResponsePage(SevenBoard.class);
                                    }
                                } else {
                                    listItem.setOutputMarkupId(true);
                                    tabs.remove(2);
                                    tabs.add(tab4);
                                    tabbedPanel.setSelectedTab(2);
                                }
                            } else {
                            	
                            	
                            		TabPanel2.this.replaceWith(new ConfirmCancelPanel(TabPanel2.this.getId(),
                            			"You are currently in game '" + user.getGame().getName() + "'! Do you want to leave this game and join game '" 
                            		+ listItem.getModelObject().getName() + "'?"){
                            		
                            			private static final long serialVersionUID = 1L;
                            		
                            			@Override
                            			protected void onCancel() {
                                    this.replaceWith(TabPanel2.this);
                            			}
                     
                            			@Override
                            			protected void onConfirm() {
                                	
                            				User user = ((TBIALSession) getSession()).getUser();
                            				Game newGame = listItem.getModelObject();
                            				Game currentGame = user.getGame();
                            				currentGame.removePlayer(user);
                            				user.setGame(newGame);
                            				newGame.addPlayer(user);
                            				user.setJoinedGame(true);
                            				listItem.setOutputMarkupId(true);
                               
                               	
                                    this.replaceWith(TabPanel2.this);
                            			}
                            		
                            		
                            			}
                  		
                            			);
                           

                            	}
                        }
                    });
                    User user = ((TBIALSession) getSession()).getUser();
                    if (user != null && user.getGame() != null && listItem.getModelObject().getName().equals(user.getGame().getName())) {
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
    }

    ;


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

            add(new FeedbackPanel("feedback"));

            gameName = new TextField<>("name", new Model<>(""));
            gameName.setRequired(true);
            choice = new DropDownChoice("ddc", new PropertyModel(this, "selected"), players);

            password = new PasswordTextField("password", Model.of("")) {
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
            Game game = getDatabase().createGame(name, host, pw, gamestate, numplayers);
            if (game != null) {
                LOGGER.info("New game '" + name + "' created");
                getTbialApplication().addGame(game);
                User user = ((TBIALSession) getSession()).getUser();
                joinGame(game, user);
                tabs.remove(2);
                tabs.add(tab4);
                tabbedPanel.setSelectedTab(2);

            } else {
                error("could not create game");
                LOGGER.debug("New game '" + name + "' creation failed");
            }
        }
    }

    ;


    private class TabPanel4 extends Panel {
        /**
         * UID for serialization.
         */
        private static final long serialVersionUID = 1L;

        private final AjaxButton leaveButton;
        private final AjaxButton startButton;
        private User user;

        public TabPanel4(String id) {
            super(id);

            add(new FeedbackPanel("feedback"));

            user = ((TBIALSession) getSession()).getUser();
            //mini implementation of singleton
            List<Game> appGames = getTbialApplication().getAvailableGames();
            Game uGame = user.getGame();
            for (Game g : appGames) {
                if (g.equals(uGame)) {
                    game = g;
                    game.getActivePlayers();
                    user.setGame(game);
                    break;
                }
            }
//            game.addPlayer(new User("Player 2", "pw",null));
//            game.addPlayer(new User("Player 3", "pw",null));

//    				game = getSession().getGame(gameId);

            final Label label = new Label("gamename", game.getName());
            add(label);

            /**
             * self updating game status
             */
            Label gameState = new Label("gamestate", new PropertyModel<String>(game, "gameState"));
//    				add(gameState);
            gameState.setOutputMarkupId(true);
            gameState.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));

            leaveButton = new AjaxButton("leavebutton") {

                /** UID for serialization. */
                private static final long serialVersionUID = 1;

                @Override
                public void onSubmit(AjaxRequestTarget target) {
                    System.out.println("leavebutton");
                    game.removePlayer(user);
                    user.setGame(null);
                    user.setJoinedGame(false);
                    ((Database) getDatabase()).setUserGame(user.getId(), "NULL");
                    tabs.remove(2);
                    tabs.add(tab3);
                    setResponsePage(getApplication().getHomePage());
                    tabbedPanel.setSelectedTab(1);
                }

                @Override
                protected void onError(AjaxRequestTarget target) {
                }
            };

            startButton = new AjaxButton("startbutton") {

                /** UID for serialization. */
                private static final long serialVersionUID = 1;

                @Override
                public void onSubmit(AjaxRequestTarget target) {
                    System.out.println("startbutton");
                    if(user.equals(game.getHost())) {
                        int currentplayers = game.getActivePlayers();
                        int numplayers = game.getNumPlayers();
                        if(currentplayers < numplayers) {
              		        info("the game has not enough players");
              		        //game.addPlayer(new User("new Player", "pw", null));
                        } else {
                            game.setGameState("running");
                            game.setGameStarted(true);
                            if(numplayers == 4) {
                                setResponsePage(FourBoard.class);
                            } else if(numplayers == 5) {
                                setResponsePage(FiveBoard.class);
                            } else if(numplayers == 6) {
                                setResponsePage(SixBoard.class);
                            } else if(numplayers == 7) {
                                setResponsePage(SevenBoard.class);
                            }
                            WebSocketManager.getInstance().sendMessage(gameStartedJSONMessage(((TBIALSession) getSession()).getUser().getId(), game.getId()));
                        }
                    } else {
          	            info("only the host can start the game");
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target) {
                }
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
                            User user = listItem.getModelObject();
                            user.setGame(null);
                            user.setJoinedGame(false);
                            ((Database) getDatabase()).setUserGame(user.getId(), "NULL");
                            game.removePlayer(listItem.getModelObject());
                            WebSocketManager.getInstance().sendMessage(removePlayerJSONMessage(((TBIALSession) getSession()).getUser().getId(), user.getId()));
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
                        if (user.equals(game.getHost()) && !user.equals(listItem.getModelObject())) {
                            removePlayerButton.setVisible(true);
                        }
                    }
                }
            };

            add(new WebSocketBehavior() {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onConnect(ConnectedMessage message) {
                    super.onConnect(message);

                    WebSocketManager.getInstance().addClient(message, ((TBIALSession) getSession()).getUser().getId());
                }

                @Override
                protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
                    super.onPush(handler, message);

                    if (message instanceof JSONMessage) {
                        handleMessage((JSONMessage) message);
                    }
                }
            });


            WebMarkupContainer joinedPlayerListContainer = new WebMarkupContainer("joinedPlayerListContainer");
            joinedPlayerListContainer.add(joinedPlayerList);
            joinedPlayerListContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
            joinedPlayerListContainer.setOutputMarkupId(true);


//      add(joinedPlayerListContainer);

            WebMarkupContainer boxedGameLobby = new WebMarkupContainer("boxedGameLobby");
            boxedGameLobby.add(gameState).add(form).add(joinedPlayerListContainer);
            add(boxedGameLobby);


        }

    }

    ;

    public void joinGame(Game game, User player) {
        player.setGame(game);
        player.setJoinedGame(true);
        ((Database) getDatabase()).setUserGame(player.getId(), game.getName());
        game.addPlayer(player);
    }

    private JSONMessage removePlayerJSONMessage(int userId, int targetId) {
        JSONObject msgBody = new JSONObject();
        msgBody.put("id", targetId);
        JSONObject msg = new JSONObject();
        msg.put("msgType", "PlayerRemoved");
        msg.put("msgBody", msgBody);

        JSONMessage message = new JSONMessage(msg);
        return message;
    }

    private JSONMessage gameStartedJSONMessage(int userId, int gameId) {
        JSONObject msgBody = new JSONObject();
        msgBody.put("gameID", gameId);
        JSONObject msg = new JSONObject();
        msg.put("msgType", "GameStarted");
        msg.put("msgBody", msgBody);

        JSONMessage message = new JSONMessage(msg);
        return message;
    }

    public void handleMessage(JSONMessage message) {
        JSONObject jsonMsg = message.getMessage();
        String msgType = (String) jsonMsg.get("msgType");
        JSONObject body;
        int id;
        switch (msgType) {
            case "PlayerRemoved":
                body = jsonMsg.getJSONObject("msgBody");
                id = (int) body.get("id");
                if (getSession().isSignedIn() && id == ((TBIALSession) getSession()).getUser().getId()) {
                    User user = ((TBIALSession) getSession()).getUser();
                    user.setGame(null);
                    user.setJoinedGame(false);
                    ((Database) getDatabase()).setUserGame(user.getId(), "NULL");
                    game.removePlayer(user);
                    tabs.remove(2);
                    tabs.add(tab3);
                    setResponsePage(getApplication().getHomePage());
                    tabbedPanel.setSelectedTab(1);
                }
                break;
            case "GameStarted":
                body = jsonMsg.getJSONObject("msgBody");
                id = (int) body.get("gameID");
                if (id == ((TBIALSession) getSession()).getUser().getGame().getId()) {
                    int numplayers = game.getNumPlayers();
                    if(numplayers == 4) {
                        setResponsePage(FourBoard.class);
                    } else if(numplayers == 5) {
                        setResponsePage(FiveBoard.class);
                    } else if(numplayers == 6) {
                        setResponsePage(SixBoard.class);
                    } else if(numplayers == 7) {
                        setResponsePage(SevenBoard.class);
                    }
                }

        }
    }

}

