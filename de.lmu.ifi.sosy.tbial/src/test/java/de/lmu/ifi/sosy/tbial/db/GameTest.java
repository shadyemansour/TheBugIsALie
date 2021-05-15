package de.lmu.ifi.sosy.tbial.db;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class GameTest {

  private String password;

  private String name;
  
  private Integer numPlayers;
  
  private User host;
  
  private User user1;
  
  private User user2;

  private Game game;

  private int id;

  @Before
  public void init() {
    password = "pass";
    name = "name";
    id = 42;
    numPlayers = 5;
    host = new User("hostName", "hostPw",null);
    user1 = new User("user1Name", "user1Pw",null);
    user2 = new User("user2Name", "user2Pw",null);
    game = new Game(name, password, numPlayers, host);
    game.addPlayer(user1);
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullNameGiven_throwsException() {
    new Game(null, password, numPlayers, host);
  }

  @Test(expected = NullPointerException.class)
  public void constructor_whenNullPasswordGiven_throwsException() {
    new Game(name, null, numPlayers, host);
  }
  
  @Test(expected = NullPointerException.class)
  public void constructor_whenNullNumPlayersGiven_throwsException() {
    new Game(name, password, null, host);
  }
  
  @Test(expected = NullPointerException.class)
  public void constructor_whenNullHostGiven_throwsException() {
    new Game(name, password, numPlayers, null);
  }

  @Test
  public void getPassword_returnsPassword() {
    game.setPassword(password);
    assertThat(game.getPassword(), is(password));
  }

  @Test
  public void getName_returnsName() {
    game.setName(name);
    assertThat(game.getName(), is(name));
  }

  @Test
  public void getId_returnsId() {
    game.setId(id);
    assertThat(game.getId(), is(id));
  }

  @Test(expected = NullPointerException.class)
  public void setName_whenNullNameGiven_throwsException() {
    game.setName(null);
  }

  @Test(expected = NullPointerException.class)
  public void setNumPlayers_whenNullNumPlayersGiven_throwsException() {
    game.setNumPlayers(null);
  }

  @Test(expected = NullPointerException.class)
  public void setHost_whenNullHostGiven_throwsException() {
    game.setHost(null);
  }
  
  @Test
  public void getPlayers_returnPlayers() {
  	List<User> expected = new ArrayList<User>(5);
  	expected.add(host);
  	expected.add(user1);
  	expected.add(null);
  	expected.add(null);
  	expected.add(null);
  	assertThat(game.getPlayers(), is(expected));
  }
  
  @Test
  public void addPlayer_addsPlayer() {
  	List<User> expected = new ArrayList<User>(5);
  	expected.add(host);
  	expected.add(user1);
  	expected.add(user2);
    expected.add(null);
    expected.add(null);
  	game.addPlayer(user2);
  	assertThat(game.getPlayers(), is(expected));
  }
  
  @Test
  public void removePlayer_removesPlayer() {
  	List<User> expected = new ArrayList<User>(5);
  	expected.add(host);
  	expected.add(null);
  	expected.add(null);
  	expected.add(null);
  	expected.add(null);
  	game.removePlayer(user1);
  	assertThat(game.getPlayers(), is(expected));
  }
}
