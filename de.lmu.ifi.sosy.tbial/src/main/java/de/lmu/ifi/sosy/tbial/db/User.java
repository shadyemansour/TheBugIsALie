package de.lmu.ifi.sosy.tbial.db;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

/**
 * A user with a user name and a plain-text password.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public class User implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private int id = -1;

  private String name;

  private String password;
  
  
  
  private int prestige;
  private int health;
  private String role;
  private String character;
  private List<Card> hand= new ArrayList<Card>();


  private Boolean joinedGame;

  private Game game;

  public User(String name, String password, Game game) {
    this(-1, name, password, game);
  }

  public User(int id, String name, String password,Game game) {
    this.id = id;
    this.name = requireNonNull(name);
    this.password = requireNonNull(password);
    this.joinedGame = false;
    this.game = game;
    this.prestige=-1;
    this.health=-1;
    this.role = null;
    this.character = null;
    this.hand=null;

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = requireNonNull(name);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = requireNonNull(password);
  }

  /**
   * Returns the database id of the user. This id is only used for persistence and is only set when
   * a user object is written or read form the database. The id is not included in {@link
   * #equals(Object)} and {@link #hashCode()} .
   *
   * @return the database id of the user.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the user's persistence id. This is package private so only database implementations can
   * use it.
   *
   * @param id the user's persistence id.
   */
  void setId(int id) {
    this.id = id;
  }
  
  public void setPrestige(int prestige) {
      this.prestige=prestige;
  }

  public int getPrestige() {
      return prestige;
  }
  
  public void setHealth(int health) {
      this.health = health;
  }

  public int getHealth() {
      return health;
  }
  
  public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public void setHand(List<Card> hand) {
      this.hand=hand;
  }
  public List<Card> getHand(){

      return hand;

  }
  

  @Override
  public String toString() {
    return "User(" + id + ", " + name + ", " + password + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof User)) {
      return false;
    }
    User other = (User) o;
    return name.equals(other.name) && password.equals(other.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, password);
  }

  public void setJoinedGame(Boolean joinedGame) {
    this.joinedGame = joinedGame;
  }

  public Boolean getJoinedGame() {
    return joinedGame;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }
}
