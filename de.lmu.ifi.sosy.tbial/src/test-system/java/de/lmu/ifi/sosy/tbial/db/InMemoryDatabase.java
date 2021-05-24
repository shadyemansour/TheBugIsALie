package de.lmu.ifi.sosy.tbial.db;

import static java.util.Collections.synchronizedList;
import static java.util.Objects.requireNonNull;

import de.lmu.ifi.sosy.tbial.util.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple in-memory database using a list for managing users.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public class InMemoryDatabase implements Database {

  private final List<User> users;
  private final List<Game> games;

  public InMemoryDatabase() {
    users = synchronizedList(new ArrayList<User>());
    games = synchronizedList(new ArrayList<Game>());
  }

  @VisibleForTesting
  protected List<User> getUsers() {
    return users;
  }

  @Override
  public User getUser(String name) {
    requireNonNull(name);
    synchronized (users) {
      for (User user : users) {
        if (name.equals(user.getName())) {
          return user;
        }
      }
      return null;
    }
  }

  @Override
  public Game getGame(String name) {
    requireNonNull(name);
    synchronized (games) {
      for (Game game : games) {
        if (name.equals(game.getName())) {
          return game;
        }
      }
      return null;
    }
  }

  @Override
  public void setGameState(int id, String gameState) {
    synchronized (games) {
      for (Game game : games) {
        if (id == game.getId()) {
          game.setGameState(gameState);
        }
      }
    }
  }

  @Override
  public boolean nameTaken(String name, String what) {
    return getUser(name) != null;
  }

  @Override
  public User register(String name, String password) {
    synchronized (users) {
      if (nameTaken(name, "user")) {
        return null;
      }

      User user = new User(name, password, null);
      user.setId(users.size());
      users.add(user);

      return user;
    }
  }

  @Override
  public void removeGame(int id) {
    requireNonNull(id);
    synchronized (games) {
      for (int i = 0; i < games.size(); i++) {
        Game game = games.get(i);
        if (id == game.getId()) {
          games.set(i, null);
        }
      }
    }
  }

  @Override
  public void setUserPrestige(int id, int pre) {
    synchronized (users) {
      for (User user : users) {
        if (id == user.getId()) {
          user.setPrestige(pre);
        }
      }
    }

  }

  @Override
  public int getUserPrestige(int id) {
    synchronized (users) {
      for (User user : users) {
        if (id == user.getId()) {
          return user.getPrestige();
        }
      }
      return -1;
    }
  }

  @Override
  public void setUserHealth(int id, int health) {
    synchronized (users) {
      for (User user : users) {
        if (id == user.getId()) {
          user.setHealth(health);
        }
      }
    }
  }

  @Override
  public int getUserHealth(int id) {
    synchronized (users) {
      for (User user : users) {
        if (id == user.getId()) {
          return user.getHealth();
        }
      }
      return -1;
    }
  }

  @Override
  public void setUserRole(int id, String role) {
    synchronized (users) {
      for (User user : users) {
        if (id == user.getId()) {
          user.setRole(role);
        }
      }
    }

  }

  @Override
  public String getUserRole(int id) {
    synchronized (users) {
      for (User user : users) {
        if (id == user.getId()) {
          return user.getRole();
        }
      }
      return null;
    }
  }

  @Override
  public void setUserCharacter(int id, String character) {
    synchronized (users) {
      for (User user : users) {
        if (id == user.getId()) {
          user.setCharacter(character);
        }
      }
    }
  }

  @Override
  public String getUserCharacter(int id) {
    synchronized (users) {
      for (User user : users) {
        if (id == user.getId()) {
          return user.getCharacter();
        }
      }
      return null;
    }
  }
}
