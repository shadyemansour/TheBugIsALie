package de.lmu.ifi.sosy.tbial.db;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

	private String name;
	private int id;
	private Boolean pwProtected;
	private String password;
	private Integer numPlayers; // 4 - 7
	private List<User> players;
	private User host;
	private int playersTurn; // 1 - 7
	
//	private GameState state; 
	private String gameState; // waiting for players, ready, playing, stopped, game over
//	private ArrayList<Card> stack;
//	private ArrayList<Card> heap;
	
	public Game(String name, String password, Integer numPlayers, User host) {
		this.name = requireNonNull(name);
		this.password = password;
		if (password.length() == 0) {
			this.pwProtected = false;
		} else {
			this.pwProtected = true;
		}
		this.numPlayers = requireNonNull(numPlayers);
		this.players = new ArrayList<User>(numPlayers);
		this.players.add(host);
		for (int i=1; i<numPlayers; i++) {
			this.players.add(null);
		}
		this.host = requireNonNull(host);
	}

	public void addPlayer(User player) {
		for (int i=0; i<this.players.size(); i++) {
			if (this.players.get(i) == null) {
				this.players.set(i, player);
				break;
			}
		}
		this.gameLobbyGameState();
	}
	
	public void removePlayer(User player) {
		for (int i=0; i<this.players.size(); i++) {
			if (this.players.get(i) == player) {
				this.players.set(i, null);
			}
		}
		this.gameLobbyGameState();
	}
	
	private Integer calcOpenSpots() {
		int openSpots = 0;
		for (int i=0; i<this.players.size(); i++) {
			if (this.players.get(i) == null) {
				openSpots++;
			}
		}
		return openSpots;
	}
	
	private void gameLobbyGameState() {
		int openSpots = this.calcOpenSpots();
		if (openSpots > 1) {
			this.setGameState(String.valueOf(openSpots) + " players missing");
		} else if (openSpots == 1) {
			this.setGameState("1 player missing");
		} else {
			this.setGameState("ready");
		}
	}

	@Override
	public String toString() {
		return "Game(" + id + ", " + name + ", " + password + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Game)) {
			return false;
		}
		Game other = (Game) o;
		return name.equals(other.name) && password.equals(other.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, password);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = requireNonNull(name);
	}
	
	public int getId() {
		return id;
	}
	void setId(int id) {
		this.id = requireNonNull(id);
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
	
	public Integer getNumPlayers() {
		return numPlayers;
	}
	public void setNumPlayers(Integer numPlayers) {
		this.numPlayers = requireNonNull(numPlayers);
	}
	
	public User getHost() {
		return host;
	}
	public void setHost(User host) {
		this.host = requireNonNull(host);
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
