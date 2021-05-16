package de.lmu.ifi.sosy.tbial.db;

import de.lmu.ifi.sosy.tbial.ConfigurationException;
import de.lmu.ifi.sosy.tbial.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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

      return result.next();
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
        User user = new User(id, name, password,null);
        connection.commit();
        return user;
      } else {
        connection.rollback();
        return null;
      }

    } catch (SQLException ex) {
      throw new DatabaseException("Error while registering user " + name, ex);
    }
  }
  public void setUserGame(int id, String game){
    try{
      Connection connection = getConnection(false);
      PreparedStatement insert = updateUserGame(id, game, connection);
      insert.executeUpdate();
      connection.commit();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating gameState " + id, ex);
    }
  }
  public void setGameState(int id, String gameState){
    try{
      Connection connection = getConnection(false);
      PreparedStatement insert = updateGameState(id, gameState, connection);
      insert.executeUpdate();
      connection.commit();
    } catch (SQLException ex) {
      throw new DatabaseException("Error while updating gameState " + id, ex);
    }
  }

  public Game createGame(String name, String host, String password, String gamestate, int numplayers) {
    Objects.requireNonNull(name, "name is null");
    try (Connection connection = getConnection(false);
         PreparedStatement insert = insertGameStatement(name, host, password, gamestate, numplayers, connection);
         ResultSet result = executeUpdate(insert)) {

      if (result != null && result.next()) {
        int id = result.getInt(1);
        Game game = new Game(id,name, password, numplayers,gamestate,host);
        game.setHost(getUser(host));
        connection.commit();

        return game;
      } else {
        connection.rollback();
        return null;
      }

    } catch (SQLException ex) {
      throw new DatabaseException("Error while creating game " + name, ex);
    }
  }

  public Game getGame(String name) {
    try (Connection connection = getConnection();
         PreparedStatement query = gameByNameQuery(name,connection);
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
      return new User(id, name, password,game);
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
	      game = new Game(id, name, password, numplayers, gamestate, host);
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
	      Game game = new Game(id, name, password, numplayers, gamestate, host);
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
  private PreparedStatement updateGameState(int id, String gameState, Connection connection)
          throws SQLException {
    PreparedStatement updateGameState;
    updateGameState = connection.prepareStatement("UPDATE GAMES SET GAMESTATE = ? WHERE ID = ?");
    updateGameState.setString(1, gameState);
    updateGameState.setInt(2, id);
    return updateGameState;
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
    insertGame = connection.prepareStatement("INSERT INTO GAMES (NAME, HOST, PASSWORD, GAMESTATE, NUMPLAYERS) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
    insertGame.setString(1, name);
    insertGame.setObject(2, host);
    insertGame.setString(3, password);
    insertGame.setString(4, gamestate);
    insertGame.setInt(5, numplayers);
    return insertGame;
  }



  private PreparedStatement getGameQuery(Connection connection) throws SQLException {
    PreparedStatement statement = connection.prepareStatement("SELECT * FROM GAMES");
    return statement;
  }
}
