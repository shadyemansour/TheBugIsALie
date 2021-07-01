package de.lmu.ifi.sosy.tbial.db;

import static java.util.Objects.requireNonNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.net.http.WebSocket;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.protocol.ws.api.AbstractWebSocketConnection;

/**
 * A user with a user name and a plain-text password.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public class User implements Serializable {

  /**
   * UID for serialization.
   */
  private static final long serialVersionUID = 1L;

  private int id = -1;

  private String name;

  private String password;


  private int prestige;
  private int health;
  private Card characterCard;
  private Card roleCard;
  // remove String role, character later
  private String role;
  private String character;
  private List<Card> hand = new ArrayList<Card>();


  private boolean joinedGame;

  private Game game;
  private boolean fired;
  private boolean myTurn;

  protected PropertyChangeSupport propertyChangeSupport;

  public User(String name, String password, Game game) {
    this(-1, name, password, game);
  }

  public User(int id, String name, String password, Game game) {
    this.id = id;
    this.name = requireNonNull(name);
    this.password = requireNonNull(password);
    this.game = game;
    this.prestige = -1;
    this.health = -1;
    this.roleCard = null;
    this.characterCard = null;
    this.role = null;
    this.character = null;
    this.hand = null;
    this.joinedGame = game != null;
    this.fired = false;
    this.propertyChangeSupport = new PropertyChangeSupport(this);
    this.myTurn = false;

  }

  public boolean isMyTurn() {
    return myTurn;
  }

  public void setMyTurn(boolean myTurn) {
    this.myTurn = myTurn;
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
    this.prestige = prestige;
    propertyChangeSupport.firePropertyChange("PrestigeProperty", id, prestige);

  }

  public void setPrestigeInMemoryDatabase(int prestige) {
    this.prestige = prestige;
  }

  public int getPrestige() {
    return prestige;
  }

  public void setHealth(int health) {
    this.health = health;
    propertyChangeSupport.firePropertyChange("HealthProperty", id, health);
  }

  public void setHealthInMemoryDatabase(int health) {
    this.health = health;
  }

  public int getHealth() {
    return health;
  }

  public Card getRoleCard() {
    return this.roleCard;
  }

  public void setRoleCard(Card role) {
    this.roleCard = role;
  }


  public void setCharacterCard(Card character) {
    this.characterCard = character;
  }

  public Card getCharacterCard() {
    return characterCard;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
    propertyChangeSupport.firePropertyChange("RoleProperty", id, role);

  }

  public void setRoleInMemoryDatabase(String role) {
    this.role = role;

  }

  public String getCharacter() {
    return character;
  }

  public void setCharacter(String character) {
    this.character = character;
    propertyChangeSupport.firePropertyChange("CharacterProperty", id, character);

  }

  public void setCharacterInMemoryDatabase(String character) {
    this.character = character;
  }

  public void setHand(List<Card> hand) {
    this.hand = hand;
  }

  public List<Card> getHand() {
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

  public void setFired(Boolean fired) {
    this.fired = fired;
  }

  public Boolean getFired() {
    return fired;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }
}
