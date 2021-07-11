package de.lmu.ifi.sosy.tbial.db;

import java.util.List;
import java.util.Set;

/**
 * The interface offered by the database (or, more precisely, the data access layer).
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public interface Database {

  /**
   * Retrieves the user with the given name.
   *
   * @param name should not be null.
   * @return the user with the given name.
   */
  User getUser(String name);

  /**
   * Returns whether a user or a game with the given name exits.
   *
   * @param name should not be null.
   * @param what user or game
   * @return {@code true} if the name is already taken, {@code false} otherwise.
   */
  boolean nameTaken(String name, String what);

  /**
   * Registers a new user with the given name and password.
   *
   * @param name     should not be null
   * @param password should not be null
   * @return a new user object or {@code null}, if a user with the given name already exists in the
   * database.
   */
  User register(String name, String password);

  void setGameHost(int id, String host);

  Game getGame(String name);

  void setGameState(int id, String gameState);

  void removeGame(int id);

  void setUserPrestige(int id, int pre);

  int getUserPrestige(int id);

  void setUserHealth(int id, int health);

  int getUserHealth(int id);

  void setUserRole(int id, String role);

  String getUserRole(int id);

  void setUserCharacter(int id, String character);

  String getUserCharacter(int id);

  void removePlayerFromGame(int gameID, String playerName);

  void addPlayerToGame(int gameID, String playerName);

  String getGamePlayers(int gameID);


  void setUserGame(int id, String name);

  List<Game> getGames();

  Set<User> getAllUsers();


  Game createGame(String name, String host, String password, String gamestate, int numplayers);


}
