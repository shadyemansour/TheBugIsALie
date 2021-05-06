package de.lmu.ifi.sosy.tbial.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

	private String name;
	private int id;
	private Boolean pwProtected;
	private String password;
	private int numPlayers; // 4 - 7
	private List<User> players;
	private User host;
	private int playersTurn; // 1 - 7
	
//	private GameState state; 
	private String gameState; // waiting for players, ready, playing, stopped, game over
//	private ArrayList<Card> stack;
//	private ArrayList<Card> heap;
	
	public Game(String name, String password, int numPlayers, User host) {
		this.name = name;
		this.password = password;
		if (password.length() == 0) {
			this.pwProtected = false;
		} else {
			this.pwProtected = true;
		}
		this.numPlayers = numPlayers;
		this.players = new ArrayList<User>(numPlayers);
		this.players.add(host);
		for (int i=1; i<numPlayers; i++) {
			this.players.add(null);
		}
		this.host = host;
	}

	public void addPlayer(User player) {
		int openSpots = 0;
		for (int i=0; i<this.players.size(); i++) {
			if (this.players.get(i) == null) {
				this.players.set(i, player);
				openSpots = this.players.size() - 1 - i;
				break;
			}
		}
		if (openSpots > 1) {
			this.setGameState(String.valueOf(openSpots) + " players missing");
		} else if (openSpots == 1) {
			this.setGameState("1 player missing");
		} else {
			this.setGameState("ready");
		}
	}
	
	public void removePlayer(User player) {
		for (int i=0; i<this.players.size(); i++) {
			if (this.players.get(i) == player) {
				this.players.set(i, null);
			}
		}
		this.setGameState("missing player(s)");
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	void setId(int id) {
		this.id = id;
	}
	
	public Boolean getPwProtected() {
		return pwProtected;
	}
	public void setPwProtected(Boolean pwProtected) {
		this.pwProtected = pwProtected;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}
	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}
	
	public User getHost() {
		return host;
	}
	public void setHost(User host) {
		this.host = host;
	}
	
	public int getPlayersTurn() {
		return playersTurn;
	}
	public void setPlayersTurn(int playersTurn) {
		this.playersTurn = playersTurn;
	}
	
	public List<User> getPlayers() {
		return players;
	}
	public void setPlayers(List<User> players) {
		this.players = players;
	}
	
	public String getGameState() {
		return gameState;
	}
	public void setGameState(String gameState) {
		this.gameState = gameState;
	}

//	public ArrayList<Card> getStack() {
//		return stack;
//	}
//	public void setStack(ArrayList<Card> stack) {
//		this.stack = stack;
//	}
//	
//	public ArrayList<Card> getHeap() {
//		return heap;
//	}
//	public void setHeap(ArrayList<Card> heap) {
//		this.heap = heap;
//	}
}
