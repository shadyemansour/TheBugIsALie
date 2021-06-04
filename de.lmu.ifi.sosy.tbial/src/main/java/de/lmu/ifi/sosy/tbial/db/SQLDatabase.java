package de.lmu.ifi.sosy.tbial.db;

import de.lmu.ifi.sosy.tbial.ConfigurationException;
import de.lmu.ifi.sosy.tbial.DatabaseException;

import java.sql.*;
import java.util.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * A database using JNDI data source to connect to a real database.
 *
 * @author Andreas Schroeder, SWEP 2014 Team.
 */
public class SQLDatabase implements Database {

  public static final String JNDI_PATH_DB = "java:/comp/env/jdbc/tbial";

  private final DataSource dataSource;

  public SQLDatabase() {
    try {
      InitialContext ctx = new InitialContext();
      dataSource = (DataSource) ctx.lookup(JNDI_PATH_DB);

    } catch (NamingException e) {
      throw new ConfigurationException("Error while looking up data source in JNDI.", e);
    }
    if (dataSource == null) {
      throw new ConfigurationException("No data source registered in JNDI for " + JNDI_PATH_DB);
    }
  }

  @Override
  public User getUser(String name) {
    Objects.requireNonNull(name, "name is null");

    try (Connection connection = getConnection();
         PreparedStatement query = userByNameQuery(name, connection);
         ResultSet result = query.executeQuery()) {
      return getUserFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }

  @Override
  public boolean nameTaken(String name, String what) {
    Objects.requireNonNull(name, "name is null");
    try (Connection connection = getConnection();
         PreparedStatement query = what.equals("user") ? userByNameQuery(name, connection) : gameByNameQuery(name, connection);
         ResultSet result = query.executeQuery()) {
      boolean nameTaken = result.next();
      return nameTaken;
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }


  @Override
  public User register(String name, String password) {
    Objects.requireNonNull(name, "name is null");
    Objects.requireNonNull(password, "password is null");

    try (Connection connection = getConnection(false);
         PreparedStatement insert = insertUserStatement(name, password, connection);
         ResultSet result = executeUpdate(insert)) {

      if (result != null && result.next()) {
        int id = result.getInt(1);
        User user = new User(id, name, password, null);
        connection.commit();
        connection.close();
        return user;
      } else {
        connection.rollback();
        return null;
      }

    } catch (SQLException ex) {
      throw new DatabaseException("Error while registering user " + name, ex);
    }
  }

  public void removeGame(int id) {
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = removeGameStatement(id, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while removing Game " + id, ex);
    }
  }

  private PreparedStatement removeGameStatement(int id, Connection connection) throws SQLException {
    PreparedStatement removeGame;
    removeGame = connection.prepareStatement("DELETE FROM GAMES WHERE ID = ?");
    removeGame.setInt(1, id);
    return removeGame;
  }

  public void setUserGame(int id, String game) {
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateUserGame(id, game, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating gameState " + id, ex);
    }
  }

  @Override
  public void setGameState(int id, String gameState) {
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateGameStateStatement(id, gameState, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating gameState " + id, ex);
    }
  }

  private PreparedStatement updateGameStateStatement(int id, String gameState, Connection connection)
      throws SQLException {
    PreparedStatement updateGameStatement;
    updateGameStatement = connection.prepareStatement("UPDATE GAMES SET GAMESTATE = ? WHERE ID = ?");
    updateGameStatement.setString(1, gameState);
    updateGameStatement.setInt(2, id);
    return updateGameStatement;
  }

  @Override
  public void setGameHost(int id, String host) {
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateGameHostStatement(id, host, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating gameHost " + id, ex);
    }
  }

  private PreparedStatement updateGameHostStatement(int id, String host, Connection connection)
      throws SQLException {
    PreparedStatement updateGameStatement;
    updateGameStatement = connection.prepareStatement("UPDATE GAMES SET HOST = ? WHERE ID = ?");
    updateGameStatement.setString(1, host);
    updateGameStatement.setInt(2, id);
    return updateGameStatement;
  }

  public Game createGame(String name, String host, String password, String gamestate, int numplayers) {
    Objects.requireNonNull(name, "name is null");
    try (Connection connection = getConnection(false);
         PreparedStatement insert = insertGameStatement(name, host, password, gamestate, numplayers, connection);
         ResultSet result = executeUpdate(insert)) {

      if (result != null && result.next()) {
        int id = result.getInt(1);
        Game game = new Game(id, name, password, numplayers, gamestate, host);
        User user = getUser(host);
        game.setHost(user);
        //game.addPlayer(getUser(host));

        connection.commit();
        connection.close();
        return game;
      } else {
        connection.rollback();
        return null;
      }

    } catch (SQLException ex) {
      throw new DatabaseException("Error while creating game " + name, ex);
    }
  }

  @Override
  public Set<User> getAllUsers() {

    try (Connection connection = getConnection();
         PreparedStatement query = connection.prepareStatement("SELECT * FROM USERS");
         ResultSet result = query.executeQuery()) {
      return getAllUsersFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }

  private Set<User> getAllUsersFromResult(ResultSet result) throws SQLException {
    Set<User> users = new HashSet<>();
    while (result.next()) {
      int id = result.getInt("ID");
      String name = result.getString("NAME");
      String password = result.getString("PASSWORD");
      Game game = getGame(result.getString("GAME"));
      users.add(new User(id, name, password, game));
    }
    return users;

  }

  @Override
  public Game getGame(String name) {
    try (Connection connection = getConnection();
         PreparedStatement query = gameByNameQuery(name, connection);
         ResultSet result = query.executeQuery()) {

      return getGameFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for games in DB.", e);
    }
  }

  public List<Game> getGames() {
    try (Connection connection = getConnection();
         PreparedStatement query = getGameQuery(connection);
         ResultSet result = query.executeQuery()) {

      return getGamesFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for games in DB.", e);
    }
  }

  private User getUserFromResult(ResultSet result) throws SQLException {
    if (result.next()) {
      int id = result.getInt("ID");
      String name = result.getString("NAME");
      String password = result.getString("PASSWORD");
      Game game = getGame(result.getString("GAME"));

      return new User(id, name, password, game);
    } else {
      return null;
    }
  }

  private Game getGameFromResult(ResultSet result) throws SQLException {
    Game game = null;
    if (result.next()) {
      int id = result.getInt("ID");
      String name = result.getString("NAME");
      String host = result.getString("HOST");
      String password = result.getString("PASSWORD");
      String gamestate = result.getString("GAMESTATE");
      int numplayers = result.getInt("NUMPLAYERS");
      String plyrs = result.getString("PLAYERS");
      String[] players = null;
      if (plyrs != null) {
        players = plyrs.split(",");
      }
      game = new Game(id, name, password, numplayers, gamestate, host);
      game.setPlayerNames(players);
    }
    return game;
  }

  private List<Game> getGamesFromResult(ResultSet result) throws SQLException {
    List<Game> games = new ArrayList<>();
    while (result.next()) {
      int id = result.getInt("ID");
      String name = result.getString("NAME");
      String host = result.getString("HOST");
      String password = result.getString("PASSWORD");
      String gamestate = result.getString("GAMESTATE");
      int numplayers = result.getInt("NUMPLAYERS");
      String plyrs = result.getString("PLAYERS");
      String[] players = null;
      if (plyrs != null) {
        players = plyrs.split(",");
      }
      Game game = new Game(id, name, password, numplayers, gamestate, host);
      game.setPlayerNames(players);
      games.add(game);
    }
    return games;
  }


  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  private Connection getConnection(boolean autocommit) throws SQLException {
    Connection connection = dataSource.getConnection();
    connection.setAutoCommit(autocommit);
    return connection;
  }

  private PreparedStatement userByNameQuery(String name, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS WHERE NAME=?");
    statement.setString(1, name);
    return statement;
  }

  private PreparedStatement gameByNameQuery(String name, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT * FROM GAMES WHERE NAME=?");
    statement.setString(1, name);
    return statement;
  }

  private ResultSet executeUpdate(PreparedStatement insertUser) throws SQLException {
    try {
      insertUser.executeUpdate();
      return insertUser.getGeneratedKeys();
    } catch (SQLIntegrityConstraintViolationException ex) {
      return null;
    }
  }

  private PreparedStatement insertUserStatement(String name, String password, Connection connection)
      throws SQLException {
    PreparedStatement insertUser;
    insertUser =
        connection.prepareStatement(
            "INSERT INTO USERS (NAME, PASSWORD, GAME) VALUES (?,?,NULL)", Statement.RETURN_GENERATED_KEYS);
    insertUser.setString(1, name);
    insertUser.setString(2, password);
    return insertUser;
  }


  private PreparedStatement updateUserGame(int id, String game, Connection connection)
      throws SQLException {
    PreparedStatement updateUserGame;
    updateUserGame = connection.prepareStatement("UPDATE USERS SET GAME = ? WHERE ID = ?");
    updateUserGame.setString(1, game);
    updateUserGame.setInt(2, id);
    return updateUserGame;
  }

  private PreparedStatement insertGameStatement(String name, String host, String password, String gamestate, Integer numplayers, Connection connection)
      throws SQLException {
    PreparedStatement insertGame;
    insertGame = connection.prepareStatement("INSERT INTO GAMES (NAME, HOST, PASSWORD, GAMESTATE, NUMPLAYERS,PLAYERS) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
    insertGame.setString(1, name);
    insertGame.setObject(2, host);
    insertGame.setString(3, password);
    insertGame.setString(4, gamestate);
    insertGame.setInt(5, numplayers);
    insertGame.setString(6, host + ",");
    return insertGame;
  }

  private PreparedStatement getGameQuery(Connection connection) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT * FROM GAMES");
    return statement;
  }

  @Override
  public String getGamePlayers(int gameID) {
    Objects.requireNonNull(gameID, "id is null");

    try (Connection connection = getConnection();
         PreparedStatement query = getPlayersQuery(gameID, connection);
         ResultSet result = query.executeQuery()) {

      return getPlayersFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while getting Game Players from DB.", e);
    }
  }

  private PreparedStatement getPlayersQuery(int id, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT PLAYERS FROM GAMES WHERE ID=?");
    statement.setInt(1, id);
    return statement;
  }

  private String getPlayersFromResult(ResultSet result) throws SQLException {
    if (result.next()) {
      String players = result.getString("PLAYERS");
      return players;
    } else {
      return null;
    }
  }

  @Override
  public void addPlayerToGame(int gameID, String playerName) {
    String players = getGamePlayers(gameID);
    if (!players.contains(playerName + ",")) {
      players += (playerName + ",");
    }
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateGamePlayersStatement(gameID, players, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while adding Player to Game " + gameID, ex);
    }
  }

  @Override
  public void removePlayerFromGame(int gameID, String playerName) {
    String players = getGamePlayers(gameID);
    if (players.contains(playerName + ",")) {
      players = players.replace(playerName + ",", "");
      try {
        Connection connection = getConnection(false);
        PreparedStatement insert = updateGamePlayersStatement(gameID, players, connection);
        insert.executeUpdate();
        connection.commit();
        connection.close();
      } catch (SQLException ex) {
        throw new DatabaseException("Error while removing Player from Game" + gameID, ex);
      }
    }

    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateGamePlayersStatement(gameID, players, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while removing Player " + gameID, ex);
    }
  }

  private PreparedStatement updateGamePlayersStatement(int gameID, String players, Connection connection)
      throws SQLException {
    PreparedStatement updateUserPrestige;
    updateUserPrestige = connection.prepareStatement("UPDATE GAMES SET PLAYERS = ? WHERE ID = ?");
    updateUserPrestige.setString(1, players);
    updateUserPrestige.setInt(2, gameID);
    return updateUserPrestige;
  }

  @Override
  public void setUserPrestige(int id, int prestige) {
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateUserPrestige(id, prestige, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating userPrestige " + id, ex);
    }
  }

  private PreparedStatement updateUserPrestige(int id, int prestige, Connection connection)
      throws SQLException {
    PreparedStatement updateUserPrestige;
    updateUserPrestige = connection.prepareStatement("UPDATE USERS SET PRESTIGE = ? WHERE ID = ?");
    updateUserPrestige.setInt(1, prestige);
    updateUserPrestige.setInt(2, id);
    return updateUserPrestige;
  }

  @Override
  public int getUserPrestige(int id) {
    Objects.requireNonNull(id, "id is null");

    try (Connection connection = getConnection();
         PreparedStatement query = getPrestigeQuery(id, connection);
         ResultSet result = query.executeQuery()) {

      return getPrestigeFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }

  private PreparedStatement getPrestigeQuery(int id, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT PRESTIGE FROM USERS WHERE ID=?");
    statement.setInt(1, id);
    return statement;
  }

  private int getPrestigeFromResult(ResultSet result) throws SQLException {
    if (result.next()) {
      int prestige = result.getInt("PRESTIGE");
      return prestige;
    } else {
      throw new SQLException();
    }
  }

  @Override
  public void setUserHealth(int id, int health) {
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateUserHealth(id, health, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating userPrestige " + id, ex);
    }
  }

  private PreparedStatement updateUserHealth(int id, int health, Connection connection)
      throws SQLException {
    PreparedStatement updateUserHealth;
    updateUserHealth = connection.prepareStatement("UPDATE USERS SET HEALTH = ? WHERE ID = ?");
    updateUserHealth.setInt(1, health);
    updateUserHealth.setInt(2, id);
    return updateUserHealth;
  }

  @Override
  public int getUserHealth(int id) {
    Objects.requireNonNull(id, "id is null");

    try (Connection connection = getConnection();
         PreparedStatement query = getHealthQuery(id, connection);
         ResultSet result = query.executeQuery()) {

      return getHealthFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }

  private PreparedStatement getHealthQuery(int id, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT HEALTH FROM USERS WHERE ID=?");
    statement.setInt(1, id);
    return statement;
  }

  private int getHealthFromResult(ResultSet result) throws SQLException {
    if (result.next()) {
      int health = result.getInt("HEALTH");
      return health;
    } else {
      throw new SQLException();
    }
  }

  @Override
  public void setUserRole(int id, String role) {
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateUserRole(id, role, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating userPrestige " + id, ex);
    }
  }

  private PreparedStatement updateUserRole(int id, String role, Connection connection)
      throws SQLException {
    PreparedStatement updateUserRole;
    updateUserRole = connection.prepareStatement("UPDATE USERS SET ROLE = ? WHERE ID = ?");
    updateUserRole.setString(1, role);
    updateUserRole.setInt(2, id);
    return updateUserRole;
  }

  @Override
  public String getUserRole(int id) {
    Objects.requireNonNull(id, "id is null");

    try (Connection connection = getConnection();
         PreparedStatement query = getRoleQuery(id, connection);
         ResultSet result = query.executeQuery()) {

      return getRoleFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }

  private PreparedStatement getRoleQuery(int id, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT ROLE FROM USERS WHERE ID=?");
    statement.setInt(1, id);
    return statement;
  }

  private String getRoleFromResult(ResultSet result) throws SQLException {
    if (result.next()) {
      String role = result.getString("ROLE");
      return role;
    } else {
      throw new SQLException();
    }
  }

  @Override
  public void setUserCharacter(int id, String charachter) {
    try {
      Connection connection = getConnection(false);
      PreparedStatement insert = updateUserCharacter(id, charachter, connection);
      insert.executeUpdate();
      connection.commit();
      connection.close();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating userPrestige " + id, ex);
    }
  }

  private PreparedStatement updateUserCharacter(int id, String charachter, Connection connection)
      throws SQLException {
    PreparedStatement updateUserCharacter;
    updateUserCharacter = connection.prepareStatement("UPDATE USERS SET CHARACT = ? WHERE ID = ?");
    updateUserCharacter.setString(1, charachter);
    updateUserCharacter.setInt(2, id);
    return updateUserCharacter;
  }

  @Override
  public String getUserCharacter(int id) {
    Objects.requireNonNull(id, "id is null");

    try (Connection connection = getConnection();
         PreparedStatement query = getCharacterQuery(id, connection);
         ResultSet result = query.executeQuery()) {

      return getCharacterFromResult(result);
    } catch (SQLException e) {
      throw new DatabaseException("Error while querying for user in DB.", e);
    }
  }


  private PreparedStatement getCharacterQuery(int id, Connection connection)
      throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT CHARACT FROM USERS WHERE ID=?");
    statement.setInt(1, id);
    return statement;
  }

  private String getCharacterFromResult(ResultSet result) throws SQLException {
    if (result.next()) {
      String charachter = result.getString("CHARACT");

      return charachter;
    } else {
      throw new SQLException();
    }
  }

}
