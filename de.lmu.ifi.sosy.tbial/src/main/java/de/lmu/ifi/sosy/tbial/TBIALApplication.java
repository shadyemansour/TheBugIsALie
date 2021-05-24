package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.SQLDatabase;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.networking.BugWebSocketResource;
import de.lmu.ifi.sosy.tbial.util.VisibleForTesting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import org.apache.wicket.*;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;


/**
 * The web application "The Bug Is A Lie".
 *
 * @author Andreas Schroeder, Christian Kroiß SWEP 2013 Team.
 */
public class TBIALApplication extends WebApplication {

  private final Database database;

  // Use LinkedHashSet to keep iteration order over current users always the same
  private final Set<User> loggedInUsers = Collections.synchronizedSet(new LinkedHashSet<>());
  private final Set<Game> availableGames = Collections.synchronizedSet(new LinkedHashSet<>());

  public static Database getDatabase() {
    return ((TBIALApplication) get()).database;
  }

  public TBIALApplication() {
    this(new SQLDatabase());
  }

  @VisibleForTesting
  TBIALApplication(Database database) {
    super();
    this.database = database;
  }

  @Override
  public Class<Lobby> getHomePage() {
    return Lobby.class;
  }

  /**
   * Returns a new {@link TBIALSession} instead of a default Wicket
   * {@link Session}.
   */
  @Override
  public TBIALSession newSession(Request request, Response response) {
    return new TBIALSession(request);
  }

  @Override
  protected void init() {
    initMarkupSettings();
    initPageMounts();
    initAuthorization();
    // initExceptionHandling();
    getSharedResources().add(BugWebSocketResource.NAME, new BugWebSocketResource());
  }

  private void initMarkupSettings() {
    if (getConfigurationType().equals(RuntimeConfigurationType.DEPLOYMENT)) {
      getMarkupSettings().setStripWicketTags(true);
      getMarkupSettings().setStripComments(true);
      getMarkupSettings().setCompressWhitespace(true);
    }
  }

  private void initPageMounts() {
    mountPage("home", getHomePage());
    mountPage("login", Login.class);
    mountPage("register", Register.class);
    mountPage("lobby", Lobby.class);
  }

  /**
   * Initializes authorization so that pages annotated with {@link AuthenticationRequired} require a
   * valid, signed-in user.
   */
  private void initAuthorization() {
    getSecuritySettings()
        .setAuthorizationStrategy(
            new IAuthorizationStrategy() {

              @Override
              public <T extends IRequestableComponent> boolean isInstantiationAuthorized(
                  Class<T> componentClass) {
                boolean requiresAuthentication =
                    componentClass.isAnnotationPresent(AuthenticationRequired.class);
                boolean isSignedIn = ((TBIALSession) Session.get()).isSignedIn();

                if (requiresAuthentication && !isSignedIn) {
                  // redirect the user to the login page.
                  throw new RestartResponseAtInterceptPageException(Login.class);
                }

                // continue.
                return true;
              }

              @Override
              public boolean isActionAuthorized(Component component, Action action) {
                // all actions are authorized.
                return true;
              }

              @Override
              public boolean isResourceAuthorized(IResource arg0, PageParameters arg1) {
                // all resources are authorized
                return true;
              }
            });
  }

  public int getUsersLoggedInCount() {
    return loggedInUsers.size();
  }

  public List<User> getLoggedInUsers() {
    return new ArrayList<>(loggedInUsers);
  }

  public void userLoggedIn(final User pUser) {
    UserListener listener = new UserListener();
    pUser.addPropertyChangeListener(listener);
    loggedInUsers.add(pUser);
  }

  public void userLoggedOut(final User pUser) {
    loggedInUsers.remove(pUser);
  }

  /* public List<Game> getAvailableGames(){
     return new ArrayList<>(availableGames);
   }  */
  public List<Game> getAvailableGames() {
    if (availableGames.isEmpty()) {
      List<Game> games = ((SQLDatabase) database).getGames();
      for (Game g : games) {
        GameListener listener = new GameListener();
        g.addPropertyChangeListener(listener);
        availableGames.add(g);
      }
    }
    return new ArrayList<>(availableGames);

  }

  public int getAvailableGamesCount() {
    return availableGames.size();
  }

  public void addGame(final Game game) {
    GameListener listener = new GameListener();
    game.addPropertyChangeListener(listener);
    availableGames.add(game);
  }

  public void removeGame(final Game game) {
    availableGames.remove(game);
  }

  public class GameListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("GameStateProperty")) {
        ((SQLDatabase) database).setGameState(Integer.parseInt(event.getOldValue().toString()), event.getNewValue().toString());
      } else if (event.getPropertyName().equals("GameHostProperty")) {
        for (User u : loggedInUsers) {
          if (u.getName().equals(event.getNewValue().toString())) {
            Game g = ((Game) event.getOldValue());
            ((SQLDatabase) database).setGameHost(((Game) event.getOldValue()).getId(), event.getNewValue().toString());
            g.setHost(u);
            g.setHostName(u.getName());
            break;
            //TODO add user not found
          }
        }
      } else if (event.getPropertyName().equals("LastPlayerRemovedProperty")) {
        Game game = (Game) event.getNewValue();
        removeGame(game);
        ((SQLDatabase) database).removeGame(game.getId());
      }
    }
  }

  public class UserListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      int id = Integer.parseInt(event.getOldValue().toString());

      if (event.getPropertyName().equals("PrestigeProperty")) {
        int prestige = Integer.parseInt(event.getNewValue().toString());
        ((SQLDatabase) database).setUserPrestige(id, prestige);
      } else if (event.getPropertyName().equals("HealthProperty")) {
        int health = Integer.parseInt(event.getNewValue().toString());
        ((SQLDatabase) database).setUserHealth(id, health);
      } else if (event.getPropertyName().equals("RoleProperty")) {
        String role = event.getNewValue().toString();
        ((SQLDatabase) database).setUserRole(id, role);
      } else if (event.getPropertyName().equals("CharacterProperty")) {
        String character = event.getNewValue().toString();
        ((SQLDatabase) database).setUserCharacter(id, character);
      }
    }
  }
}
