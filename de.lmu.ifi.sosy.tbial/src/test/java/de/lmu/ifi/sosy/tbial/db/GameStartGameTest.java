package de.lmu.ifi.sosy.tbial.db;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class GameStartGameTest {
	
	private int id;
	
	private String password;

	private String name;

	private Integer numPlayers;

	private User host;
	
	private User user1;

	private User user2;

	private User user3;

	private User user4;
	  
	private User user5;
	  
	private User user6;
	
	private Game game;
	
	 @Before
	  public void init() {
		 
		password = "pass";
		name = "name";
		id = 42;
	 
	    numPlayers = 7;
	    host = new User("hostName", "hostPw", null);
	    user1 = new User("user1Name", "user1Pw", null);
	    user2 = new User("user2Name", "user2Pw", null);
	    user3 = new User("user3Name", "user3Pw", null);
	    user4 = new User("user4Name", "user4Pw", null);
	    user5 = new User("user5Name", "user5Pw", null);
	    user6 = new User("user6Name", "user6Pw", null);
	    
	    game = new Game(id, name, password, numPlayers, "", host.getName());
	    game.setHost(host);
	    game.addPlayer(host);
	    game.addPlayer(user1);
	    game.addPlayer(user2);
	    game.addPlayer(user3);
	    game.addPlayer(user4);
	    game.addPlayer(user5);
	    game.addPlayer(user6);
	  
	    game.startGame();
	 }
	 
	 
	 @Test
	 public void startGame_getsNumberManagers() {
		    int expected = 1;
		    int numManager=0;
		    
		    for (User player : game.getPlayers()){

		           if(player.getRoleCard().getTitle().equals("Manager")){
		               ++numManager;
		           }
		    }
		
		    assertThat(numManager , is(expected));
		  
		  }
	 
	 
	 @Test
	 public void startGame_getsNumberConsultant() {
		    int expected = 1;
		    int numConsultant=0;
		    
		    for (User player : game.getPlayers()){

		           if(player.getRoleCard().getTitle().equals("Consultant")){
		               ++numConsultant;
		           }
		    }
		
		    assertThat(numConsultant , is(expected));
		  
		  }
	
	 
	 @Test
	 public void startGame_getsNumberMonkey() {
		    int expected = 3;
		    int numMonkey=0;
		    
		    for (User player : game.getPlayers()){

		           if(player.getRoleCard().getTitle().equals("Evil Code Monkey")){
		               ++numMonkey;
		           }
		    }
		
		    assertThat(numMonkey, is(expected));
		  
		  }
	 

	 @Test
	 public void startGame_getsNumberHonestDev() {
		    int expected = 2;
		    int numHonestDev=0;
		    
		    for (User player : game.getPlayers()){

		           if(player.getRoleCard().getTitle().equals("Honest Developer")){
		               ++numHonestDev;
		           }
		    }
		
		    assertThat(numHonestDev, is(expected));
		  
		  }
	 
	 
	 @Test
	 public void startGame_CharacterNotNull() {
		    boolean expected = false;
		    
		    for (User player : game.getPlayers()) {
		    	
		    	assertThat(player.getCharacterCard().getTitle().equals("  "), is(expected));
		    	
		    }
		  }
	 
	 
	 
	 @Test
	 public void startGame_getsPrestige() {
		    int expected = 0;
		    
		    for (User player : game.getPlayers()) {
		    	
		    	assertThat(player.getPrestige(), is(expected));
		    	
		    }
		  }
	 
	 @Test
	 public void startGame_getsNumberOfRemainingCharacterCards() {
		    int expected = 6;
		    assertThat(game.getCharacterCards().size(), is(expected));
		  
		  }
	 

	 @Test
	 public void startGame_checksHealth() {
		    boolean expected = true;
		    
		    
		    for (User player : game.getPlayers()) {
		    	
		    	boolean x = (player.getHealth()>=3 && player.getHealth()<=5);
		    	
		    	assertThat(x, is(expected));
		    	
		    }
		  }
	 
	 
	 @Test
	 public void startGame_checksHand() {
		    boolean expected = true;
		    
		    
		    for (User player : game.getPlayers()) {
		    	
		    	boolean x = (player.getHealth()== player.getHand().size());
		    	
		    	assertThat(x, is(expected));
		    	
		    }
		  }
	 
	 @Test
	 public void startGame_getsStackSize() {
		    int expected = 80;
		    assertThat(game.getStack().size(), is(expected));
		  
		  }
	 
	 
	 
	 

	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
