package de.lmu.ifi.sosy.tbial.db;

import de.lmu.ifi.sosy.tbial.Lobby;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class LobbyTest {
  Lobby lobby;
  User host;
  Game game;

  @Before
  public void init() {
    //lobby = new Lobby();
    host = new User("hostName", "hostPw", null);
    game = new Game(-1, "name", "password", 4, null, host.getName());
  }


//    @Test
//    public void playerJoinedGame(){
//      User user1 = new User("user4Name", "user4Pw",null);
//      lobby.joinGame(game,user1);
//      assertTrue(game.getPlayers().contains(user1));
//
//  }
}
