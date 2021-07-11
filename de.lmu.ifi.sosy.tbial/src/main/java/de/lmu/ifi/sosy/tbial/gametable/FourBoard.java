package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.db.*;
import de.lmu.ifi.sosy.tbial.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.model.PropertyModel;

@AuthenticationRequired
public class FourBoard extends GameView {
  /**
   * UID for serialization.
   */
  private static final long serialVersionUID = 1L;

  protected WebMarkupContainer playerCardContainer, playerCardContainer2, playerCardContainer3, playerCardContainer4;
  protected ListView<Card> cardDropArea, cardDropArea2, cardDropArea3, cardDropArea4;
  protected Card selectedCard;
  protected Card selectedDropCard;
  protected boolean selectable;
  protected boolean bugPlayed;
  protected ListView<Card> cardHand, cardHand2, cardHand3, cardHand4;

  protected User user;

  protected List<User> players;
  protected int currenthealth1, currenthealth2, currenthealth3, currenthealth4;

  protected List<String> p1turn;
  protected List<String> p2turn;
  protected List<String> p3turn;
  protected List<String> p4turn;

  protected WebMarkupContainer p1container, p2container, p3container, p4container;


  public FourBoard() {
    super();

    user = ((TBIALSession) getSession()).getUser();
    p1turn = new ArrayList<>();
    p2turn = new ArrayList<>();
    p3turn = new ArrayList<>();
    p4turn = new ArrayList<>();
    selectable = false;
    bugPlayed = false;
    players = actualPlayerlist;

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
        if (user.getId() == currentPlayerId) {
          bugPlayed = false;
          selectable = false;
          selectedCard = null;
          selectedDropCard = null;
          endTurn();
        }

      }

      @Override
      protected void onError(AjaxRequestTarget target) {
      }
    };

    form = new Form<>("controls-form");
    form.add(endTurnButton);
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
  }

  protected void updateHealth() {
    players = game.getPlayers();
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
  }

  protected void createPlayerAttributes() {
    players.get(0).setPrestige(0);
    players.get(1).setPrestige(0);
    players.get(2).setPrestige(0);
    players.get(3).setPrestige(0);
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
              attributesContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
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
              attributesContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
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
              attributesContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
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
              attributesContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
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
    middleTableContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    middleTableContainer.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        boolean selectedCardCheck = selectedCard == null || (!selectedCard.getTitle().equals("Bug Delegation"));
        if (user.isBeingAttacked() && selectedDropCard != null && selectedCardCheck) {
          // can't defend
          discardCard(selectedDropCard, "drop");
          user.setBeingAttacked(false);
          selectedDropCard = null;
          game.updateHealth(user.getId(), user.getHealth() - 1);
          return;
        }

        if (selectedCard != null) {
          discardCard(selectedCard, "hand");
          selectedCard = null;
        }
      }
    });
    add(middleTableContainer);

    ListView<Card> stack = new ListView<>("stack", stackList) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = 85 - stackList.size();
        int posTop = 90 - stackList.size();
        item.add(new AttributeAppender("style", "left: " + (posLeft + 2 * item.getIndex()) + "px; top: " + (posTop + 2 * item.getIndex()) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
      }
    };
    stack.setOutputMarkupId(true);
    middleTableContainer.add(stack);

    ListView<Card> heap = new ListView<>("heap", heapList) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        double rotation = 30 - 3 * item.getIndex();
        double direction = item.getIndex() % 2 == 0 ? 1 : -1;
        item.add(new AttributeAppender("style", "transform: rotate(" + (direction * rotation) + "deg);"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
      }
    };
    heap.setOutputMarkupId(true);
    middleTableContainer.add(heap);
  }

  /*
   * creates player area for top player
   */
  private void createPlayer1Area() {

    p1container = new WebMarkupContainer("p1-container");
    p1container.setOutputMarkupId(true);
    p1container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    add(p1container);
    ListView<String> turn = new ListView<>("turn", p1turn) {
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
    playerCardContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    playerCardContainer.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        if (selectedCard != null && selectedCard.getSubTitle().equals("--bug--") && !selectable && !bugPlayed) {
          if (!actualPlayerlist.get(0).isFired()) {
            playCard(actualPlayerlist.get(0).getId(), selectedCard);
            selectedCard = null;
            bugPlayed = true;
          }
        }
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
    cardDropArea = new ListView<>("card-drop-area1", p1drophand) {
      private static final long serialVersionUID = 1L;
      final int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p1drophand.size() * 50) / (p1drophand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
      }

    };
    cardDropArea.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea);

    /*
     * card hand
     */
    cardHand = new ListView<>("card-hand1", p1hand) {
      private static final long serialVersionUID = 1L;
      final int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p1hand.size() * 50) / (p1hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
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

     * show character card
     */

    ListView<Card> characterCard = new ListView<>("character-card-panel1", p1character) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        Card characterCard = item.getModelObject();
        characterCard.setVisible(true);
        item.add(new CardPanel("card", new CompoundPropertyModel<>(Model.of(characterCard))));
      }
    };
    characterCard.setOutputMarkupId(true);
    healthRoleContainer.add(characterCard);

    /*
     * role card
     * on player and card
     */
    ListView<Card> roleCard = new ListView<>("role-card-panel1", p1role) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
      }
    };
    roleCard.setOutputMarkupId(true);
    healthRoleContainer.add(roleCard);
  }

  /*
   * creates player area for right player
   */
  private void createPlayer2Area() {

    p2container = new WebMarkupContainer("p2-container");
    p2container.setOutputMarkupId(true);
    p2container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    add(p2container);
    ListView<String> turn = new ListView<>("turn", p2turn) {
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
    playerCardContainer2.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    playerCardContainer2.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        if (selectedCard != null && selectedCard.getSubTitle().equals("--bug--") && !selectable && !bugPlayed) {
          if (!actualPlayerlist.get(1).isFired()) {
            playCard(actualPlayerlist.get(1).getId(), selectedCard);
            selectedCard = null;
            bugPlayed = true;
          }
        }
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
    cardDropArea2 = new ListView<>("card-drop-area2", p2drophand) {
      private static final long serialVersionUID = 1L;
      final int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p2drophand.size() * 50) / (p2drophand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
      }

    };
    cardDropArea2.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea2);

    /*
     * card hand
     */
    cardHand2 = new ListView<>("card-hand2", p2hand) {
      private static final long serialVersionUID = 1L;
      final int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p2hand.size() * 50) / (p2hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
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
     * show character card
     */

    ListView<Card> characterCard = new ListView<>("character-card-panel2", p2character) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        Card characterCard = item.getModelObject();
        characterCard.setVisible(true);
        item.add(new CardPanel("card", new CompoundPropertyModel<>(Model.of(characterCard))));
      }
    };
    characterCard.setOutputMarkupId(true);
    healthRoleContainer.add(characterCard);

    /*
     * role card TODO: put real role card here TODO: show or hide card depending
     * on player and card
     */
    ListView<Card> roleCard = new ListView<>("role-card-panel2", p2role) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
      }
    };
    roleCard.setOutputMarkupId(true);
    healthRoleContainer.add(roleCard);
  }

  /*
   * creates player area for bottom player
   */
  private void createPlayer3Area() {

    p3container = new WebMarkupContainer("p3-container");
    p3container.setOutputMarkupId(true);
    p3container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    add(p3container);
    ListView<String> turn = new ListView<>("turn", p3turn) {
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
    playerCardContainer3.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    playerCardContainer3.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1l;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
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
    cardDropArea3 = new ListView<>("card-drop-area3", p3drophand) {
      private static final long serialVersionUID = 1L;
      final int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p3drophand.size() * 50) / (p3drophand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
        item.add(new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            selectedDropCard = item.getModelObject();
          }
        });
      }
    };
    cardDropArea3.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea3);

    /*
     * card hand
     */
    cardHand3 = new ListView<>("card-hand3", p3hand) {
      private static final long serialVersionUID = 1L;
      final int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p3hand.size() * 50) / (p3hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
        item.add(new AjaxEventBehavior("click") {
          private static final long serialVersionUID = 1L;

          @Override
          protected void onEvent(AjaxRequestTarget target) {
            if (user.isMyTurn() || user.isBeingAttacked()) {
              selectedCard = item.getModelObject();
              if (selectedDropCard != null && selectedDropCard.getSubTitle().equals("--bug--")) {
                if (selectedCard.getSubTitle().equals("--lame excuse--") || selectedCard.getSubTitle().equals("--Solution--")) {
                  defendCard(selectedCard, selectedDropCard);
                  selectedCard = null;
                  selectedDropCard = null;
                }
              }
            }
            if (selectedCard != null && selectedCard.getTitle().equals("Bug Delegation")) {
              playCard(user.getId(), selectedCard);
              user.setHasDelegation(true);
            }

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
     * show character card
     */

    ListView<Card> characterCard = new ListView<>("character-card-panel3", p3character) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        Card characterCard = item.getModelObject();
        characterCard.setVisible(true);
        item.add(new CardPanel("card", new CompoundPropertyModel<>(Model.of(characterCard))));
      }
    };
    characterCard.setOutputMarkupId(true);
    healthRoleContainer.add(characterCard);


    /*
     * role card
     * on player and card
     */
    ListView<Card> roleCard = new ListView<>("role-card-panel3", p3role) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        Card roleCard = item.getModelObject();
        roleCard.setVisible(true);
        item.add(new CardPanel("card", new CompoundPropertyModel<>(Model.of(roleCard))));
      }
    };
    roleCard.setOutputMarkupId(true);
    healthRoleContainer.add(roleCard);
  }

  /*
   * creates player area for left player
   */
  private void createPlayer4Area() {

    p4container = new WebMarkupContainer("p4-container");
    p4container.setOutputMarkupId(true);
    p4container.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    add(p4container);
    ListView<String> turn = new ListView<>("turn", p4turn) {
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
    playerCardContainer4.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)));
    playerCardContainer4.add(new AjaxEventBehavior("click") {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
        if (selectedCard != null && selectedCard.getSubTitle().equals("--bug--") && !selectable && !bugPlayed) {
          if (!actualPlayerlist.get(3).isFired()) {
            playCard(actualPlayerlist.get(3).getId(), selectedCard);
            selectedCard = null;
            bugPlayed = true;
          }
        }
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
    cardDropArea4 = new ListView<>("card-drop-area4", p4drophand) {
      private static final long serialVersionUID = 1L;
      final int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p4drophand.size() * 50) / (p4drophand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
      }

    };
    cardDropArea4.setOutputMarkupId(true);
    playableCardsContainer.add(cardDropArea4);

    /*
     * card hand
     */
    cardHand4 = new ListView<>("card-hand4", p4hand) {
      private static final long serialVersionUID = 1L;
      final int width = 300;

      @Override
      protected void populateItem(ListItem<Card> item) {
        int posLeft = (width - p4hand.size() * 50) / (p4hand.size() + 1);
        item.add(new AttributeAppender("style", "left: " + (posLeft + item.getIndex() * (posLeft + 50)) + "px;"));
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
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
     * show character card
     */

    ListView<Card> characterCard = new ListView<>("character-card-panel4", p4character) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        Card characterCard = item.getModelObject();
        characterCard.setVisible(true);
        item.add(new CardPanel("card", new CompoundPropertyModel<>(Model.of(characterCard))));
      }
    };
    characterCard.setOutputMarkupId(true);
    healthRoleContainer.add(characterCard);

    /*
     * role card
     * on player and card
     */
    ListView<Card> roleCard = new ListView<>("role-card-panel4", p4role) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Card> item) {
        item.add(new CardPanel("card", new CompoundPropertyModel<>(item.getModel())));
      }
    };
    roleCard.setOutputMarkupId(true);
    healthRoleContainer.add(roleCard);
  }

}