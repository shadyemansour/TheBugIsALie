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

  public InMemoryDatabase() {
    users = synchronizedList(new ArrayList<User>());
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
  public boolean nameTaken(String name, String what) {
    return getUser(name) != null;
  }

  @Override
  public User register(String name, String password) {
    synchronized (users) {
      if (nameTaken(name,"user")) {
        return null;
      }

      User user = new User(name, password,null);
      user.setId(users.size());
      users.add(user);

      return user;
    }
  }
}
