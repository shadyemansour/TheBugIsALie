package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.db.Game;
import de.lmu.ifi.sosy.tbial.db.SQLDatabase;
import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.util.VisibleForTesting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import org.apache.wicket.*;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.protocol.http.WebApplication;
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
  private Set<User> allUsers = new HashSet<>();

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
    this.allUsers = database.getAllUsers();
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
    super.init();
    initMarkupSettings();
    initPageMounts();
    initAuthorization();
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

  public List<User> getAllUsers() {
    return new ArrayList<>(allUsers);
  }

  public void userLoggedIn(final User pUser) {
    UserListener listener = new UserListener();
    pUser.addPropertyChangeListener(listener);
    loggedInUsers.add(pUser);
    allUsers.add(pUser);
  }

  public void userLoggedOut(final User pUser) {
    loggedInUsers.remove(pUser);
  }

  /* public List<Game> getAvailableGames(){
     return new ArrayList<>(availableGames);
   }  */
  public List<Game> getAvailableGames() {
    List<Game> games = database.getGames();
    if (games.size() != availableGames.size()) {
      for (Game g : games) {
        if (!availableGames.contains(g)) {
          GameListener listener = new GameListener();
          g.addPropertyChangeListener(listener);
          availableGames.add(g);
        }
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

  public Game getGame(String gameName) {
    Game game = null;
    for (Game g : availableGames) {
      if (g.getGameName().equals(gameName)) {
        game = g;
        break;
      }
    }
    return game;
  }


  public class GameListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      if (event.getPropertyName().equals("GameStateProperty")) {
        database.setGameState(Integer.parseInt(event.getOldValue().toString()), event.getNewValue().toString());
      } else if (event.getPropertyName().equals("GameHostProperty")) {
        for (User u : allUsers) {
          if (u.getName().equals(event.getNewValue().toString())) {
            Game g = ((Game) event.getOldValue());
            database.setGameHost(((Game) event.getOldValue()).getGameId(), event.getNewValue().toString());
            g.setHost(u);
            g.setHostName(u.getName());
            break;
          }
        }
      } else if (event.getPropertyName().equals("LastPlayerRemovedProperty")) {
        Game game = (Game) event.getNewValue();
        removeGame(game);
        database.removeGame(game.getGameId());
      } else if (event.getPropertyName().equals("PlayerAdded")) {
        Game game = (Game) event.getOldValue();
        database.addPlayerToGame(game.getGameId(), event.getNewValue().toString());
      } else if (event.getPropertyName().equals("PlayerAddedGameRunning")) {
        Game game = (Game) event.getOldValue();
        database.addPlayerToGame(game.getGameId(), event.getNewValue().toString());
        if (game.getActivePlayers() == game.getNumPlayers()) {

        }
      } else if (event.getPropertyName().equals("PlayerRemoved")) {
        Game game = (Game) event.getOldValue();
        database.removePlayerFromGame(game.getGameId(), event.getNewValue().toString());
      } else if (event.getPropertyName().equals("PlayerRemovedGameRunning")) {
        Game game = (Game) event.getOldValue();
        database.removePlayerFromGame(game.getGameId(), event.getNewValue().toString());

      } else if (event.getPropertyName().equals("GameIsNewAddPlayers")) {
        Game game = (Game) event.getOldValue();
        String[] players = (String[]) event.getNewValue();
        for (int i = 0; i < players.length; i++) {
          if (!players[i].equals(game.getHostName())) {
            game.addPlayerCreate(database.getUser(players[i]));
          }
        }
      }
    }
  }

  public class UserListener implements PropertyChangeListener {
    @Override
    public void propertyChange(PropertyChangeEvent event) {
      int id = Integer.parseInt(event.getOldValue().toString());

      if (event.getPropertyName().equals("PrestigeProperty")) {
        int prestige = Integer.parseInt(event.getNewValue().toString());
        database.setUserPrestige(id, prestige);
      } else if (event.getPropertyName().equals("HealthProperty")) {
        int health = Integer.parseInt(event.getNewValue().toString());
        database.setUserHealth(id, health);
      } else if (event.getPropertyName().equals("RoleProperty")) {
        String role = event.getNewValue().toString();
        database.setUserRole(id, role);
      } else if (event.getPropertyName().equals("CharacterProperty")) {
        String character = event.getNewValue().toString();
        database.setUserCharacter(id, character);
      }
    }
  }
}
