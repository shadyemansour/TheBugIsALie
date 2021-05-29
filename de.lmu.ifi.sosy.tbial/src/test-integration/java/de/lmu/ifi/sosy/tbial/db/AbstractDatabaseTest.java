package de.lmu.ifi.sosy.tbial.db;

import static de.lmu.ifi.sosy.tbial.TestUtil.hasNameAndPassword;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import de.lmu.ifi.sosy.tbial.DatabaseException;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public abstract class AbstractDatabaseTest {

  protected Database database;

  protected String name;

  protected String password;

  protected User user;

  protected Game game;

  protected int id;
  protected int prestige;
  protected int health;
  protected String charachter;
  protected String role;

  protected String gameState;

  @Before
  public void initGeneral() {
    name = "name";
    password = "pass";
    id = 1;
    gameState = "newState";
    game = new Game(id, "name", "", 5, null, name);
    user = new User(name, password, game);
    prestige = 10;
    health = 100;
    role = "testRole";
    charachter = "John Doe";

  }

  protected void addUser() {
    addUser(user);
  }

  protected void addGame() {
    addGame(game);
  }

  protected void setGameState() {
    setGameState(id, gameState);
  }

  protected void removeGame() {
    removeGame(id);
  }

  protected abstract void addUser(User user);

  protected abstract void addGame(Game game);

  protected abstract void removeGame(int id);

  protected abstract void setGameState(int id, String gameState);

  protected abstract void setUserPrestige(int id, int pre);

  protected abstract void setUserHealth(int id, int health);

  protected abstract void setUserRole(int id, String role);

  protected abstract void setUserCharacter(int id, String character);

  @Test(expected = NullPointerException.class)
  public void registerUserWhenNullNameGivenThrowsException() {
    database.register(null, password);
  }

  @Test(expected = NullPointerException.class)
  public void registerUserWhenNullPasswordGivenThrowsException() {
    database.register(name, null);
  }

  @Test
  public void hasUserWithNameWhenUserNotRegisteredReturnsFalse() {
    assertThat(database.nameTaken(name, "user"), is(false));
  }

  @Test
  public void hasUserWithNameWhenUserRegisteredReturnsTrue() {
    addUser();
    assertThat(database.nameTaken(name, "user"), is(true));
  }

  @Test
  public void registerUserWhenUserExistsReturnsNull() {
    addUser();
    User user = database.register(name, password);
    assertThat(user, is(nullValue()));
  }

  @Test
  public void registerUserWhenUserDoesNotExistReturnsUser() {
    User user = database.register(name, password);

    assertThat(user, hasNameAndPassword(name, password));
  }

  @Test(expected = NullPointerException.class)
  public void getUserWhenNullGivenThrowsException() {
    database.getUser(null);
  }

  @Test
  public void getUserWhenUserDoesNotExistReturnsNull() {
    addUser(new User("someoneelse", "withsomepassword", null));
    User user = database.getUser(name);

    assertThat(user, is(nullValue()));
  }

  @Test
  public void getGameWhenGameDoesNotExistReturnsNull() {
    addUser();
    addGame(new Game(-1, "name", "", 4, "", name));
    Game game = database.getGame("hi");

    assertThat(game, is(nullValue()));
  }

  @Test
  public void getGameStateAfterSet() {
    addUser();
    addGame(new Game(1, "name", "", 4, "", name));
    Game game = database.getGame("name");
    game.setGameState("newState");

    assertThat(game.getGameState(), is("newState"));
  }

  @Test
  public void getUserWhenNoUserExistsReturnsNull() {
    User user = database.getUser(name);

    assertThat(user, is(nullValue()));
  }

  @Test
  public void getUserWhenUserExistsReturnsUser() {
    addUser();

    User user = database.getUser(name);

    assertThat(user, hasNameAndPassword(name, password));
  }

  @Test
  public void getUserWhenMultipleUsersExistsReturnsCorrectUser() {
    addUser();
    addUser(new User("AnotherName", "AnotherPass", null));

    User user = database.getUser(name);

    assertThat(user, hasNameAndPassword(name, password));
  }

  @Test
  public void removeGameWhenGameIsRemovedThrows() {
    addUser();
    addGame();
    removeGame(id);
    Game game = database.getGame("name");
    assertThat(game, is(nullValue()));

  }


  @Test
  public void getUserGameAndUserExistsInGame() {
    addUser();
    addGame();
    User user = database.getUser(name);
    Game game = database.getGame(name);

    assertEquals(user.getGame(), game);
  }

  @Test
  public void getUserAttsAfterSet() {
    addUser();
    database.setUserPrestige(id, prestige);
    database.setUserHealth(id, health);
    database.setUserRole(id, role);
    database.setUserCharacter(id, charachter);
    int newPrestige = database.getUserPrestige(id);
    int newHealth = database.getUserHealth(id);
    String newRole = database.getUserRole(id);
    String newCharacter = database.getUserCharacter(id);

    assertEquals(prestige, newPrestige);
    assertEquals(health, newHealth);
    assertEquals(role, newRole);
    assertEquals(charachter, newCharacter);
  }

  @Test(expected = DatabaseException.class)
  public void getUserCharachterUsernotFound() {
    database.getUserCharacter(-1);
  }

  @Test(expected = DatabaseException.class)
  public void getUserRoleUsernotFound() {
    database.getUserRole(-1);
  }

  @Test(expected = DatabaseException.class)
  public void getUserHealthUsernotFound() {
    database.getUserCharacter(-1);
  }

  @Test(expected = DatabaseException.class)
  public void getUserPrestigeUsernotFound() {
    database.getUserCharacter(-1);
  }

}
