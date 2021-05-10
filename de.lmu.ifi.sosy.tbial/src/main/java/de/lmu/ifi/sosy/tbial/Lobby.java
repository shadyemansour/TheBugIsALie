package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.User;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.markup.html.form.Form;

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
    
    Form<?> form = new Form<>("creating");
    add(form);
  private static class TabPanel3 extends Panel {
    public TabPanel3(String id) {
      super(id);
    }
  }
  ;

}
