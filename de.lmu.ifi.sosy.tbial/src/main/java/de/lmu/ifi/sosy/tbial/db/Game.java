package de.lmu.ifi.sosy.tbial.db;

import org.apache.wicket.model.IModel;

import static java.util.Objects.requireNonNull;

import de.lmu.ifi.sosy.tbial.networking.JSONMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.*;


public class Game extends Thread implements Serializable {

	/**
	 * UID for serialization.
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private int id;
	private Boolean pwProtected;
	private String password;
	private Integer numPlayers; // 4 - 7
	private List<User> players;
	private String[] playerNames;
	private String hostName;
	private User host;
	private boolean gamePaused;
	private boolean gameStarted;
	private boolean addedPlayers;
	private int currentPlayer;
	private int currentID;
	private transient Timer timer;
	private boolean gameInitiated;
	private boolean gameWon;
	private List<User> firedPlayers;
	private boolean isPlaying;
	private boolean managerFired;
	private boolean consultantFired;
	private int numEvilMonkeys;
	private int turn;


	//   private  ArrayList<Card> charakterCards = new  ArrayList<Card>(); TODO later (US37)

	private List<Card> roleCards;
	private List<Card> characterCards;
	private List<Card> stack; // all action, ability, and stumbling blocks cards
	//	private List<Card> playableStack; // only bugs, exuses, solutions playable
	private transient JSONMessage roleCardsHostMessage;
	private transient JSONMessage characterCardsHostMessage;
	private transient JSONMessage gameStartedMessageHost;
	private transient List<JSONMessage> cardsHostMessages;

	private List<Card> heap = new ArrayList<Card>();

	//	private GameState state;
	private volatile String gameState; // waiting for players, ready, playing, stopped, game over
	protected PropertyChangeSupport propertyChangeSupport;

	public Game(int id, String name, String password, Integer numPlayers, String gameState, String hostName) {
		this.id = id;
		this.name = requireNonNull(name);
		this.password = password;
		this.gameState = gameState;
		this.gameStarted = gameState.equals("running");

		if (password.length() == 0) {
			this.pwProtected = false;
		} else {
			this.pwProtected = true;
		}
		this.gamePaused = false;
		this.numPlayers = requireNonNull(numPlayers);
		this.players = new ArrayList<User>();
		this.hostName = requireNonNull(hostName);
		for (int i = 0; i < numPlayers; i++) {
			this.players.add(null);
		}
		//	this.generatePlayerAttributes();
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.addedPlayers = false;

		this.roleCards = new ArrayList<Card>();
		this.characterCards = new ArrayList<Card>();
		this.stack = new ArrayList<Card>();
		this.timer = new Timer();
		this.cardsHostMessages = new ArrayList<>();
		this.gameInitiated = false;
		this.gameWon = false;
		this.firedPlayers = new ArrayList<>();
		this.managerFired = false;
		this.consultantFired = false;
		this.numEvilMonkeys = numPlayers == 4 || numPlayers == 5 ? 2 : 3;
		this.turn = 1;
		this.isPlaying = false;


	}

	/**
	 * method created for setup of us7
	 * player attributes should be generated random for real game
	 */


//	public void generatePlayerAttributes() {
//
//		String managerRole = "Manager"; // only 1 card exists
//		String consultantRole = "Consultant"; // only 1 card exists
//		String honestDeveloperRole = "Honest Developer"; // 2 cards exist
//		String evilCodeMonkeyRole = "Evil Code Monkey"; // 3 cards exist
//
//		String markZuckerbergCharacter = "Mark Zuckerberg";
//		String tomAndersonCharacter = "Tom Anderson";
//		String jeffTaylorCharacter = "Jeff Taylor";
//		String larryPageCharacter = "Larry Page";
//		String larryEllisonCharacter = "Larry Ellison";
//		String kentBeckCharacter = "Kent Beck";
//		String steveJobsCharacter = "Steve Jobs";
//
//		for (int i = 0; i < this.players.size(); i++) {
//			if (this.players.get(i) != null) {
//				if (i == 0) {
//					this.players.get(i).setRole(managerRole);
//					this.players.get(i).setCharacter(markZuckerbergCharacter);
//				} else if (i == 1) {
//					this.players.get(i).setRole(consultantRole);
//					this.players.get(i).setCharacter(tomAndersonCharacter);
//				} else if (i == 2) {
//					this.players.get(i).setRole(honestDeveloperRole);
//					this.players.get(i).setCharacter(jeffTaylorCharacter);
//				} else if (i == 3) {
//					this.players.get(i).setRole(evilCodeMonkeyRole);
//					this.players.get(i).setCharacter(larryPageCharacter);
//				} else if (i == 4) {
//					this.players.get(i).setRole(honestDeveloperRole);
//					this.players.get(i).setCharacter(larryEllisonCharacter);
//				} else if (i == 5) {
//					this.players.get(i).setRole(evilCodeMonkeyRole);
//					this.players.get(i).setCharacter(kentBeckCharacter);
//				} else if (i == 6) {
//					this.players.get(i).setRole(evilCodeMonkeyRole);
//					this.players.get(i).setCharacter(steveJobsCharacter);
//				}
//				this.players.get(i).setHealth(3);
//				this.players.get(i).setPrestige(1);
//			}
//		}
//	}
	public void addPlayer(User player) {
		for (int i = 0; i < players.size(); i++) {
			if (this.players.get(i) == null) {
				players.set(i, player);
				if (!player.getName().equals(hostName)) {
					if (!gameStarted) {
						propertyChangeSupport.firePropertyChange("PlayerAdded", this, this.players.get(i).getName());
					} else {
						propertyChangeSupport.firePropertyChange("PlayerAddedGameRunning", this, this.players.get(i).getName());
					}
				}
				break;
			}
		}
//		if (!players.contains(player)){
//			players.add(player);
//		}
		this.gameLobbyGameState();
		//	this.generatePlayerAttributes(); // only necessary for debug
	}

	public void startGame() {
		gameStarted = true;
		//	playableStack = new ArrayList<Card>();


		setRoleCards();
		Collections.shuffle(roleCards);

		setCharacterCards();
		Collections.shuffle(characterCards);

		setStack();
		Collections.shuffle(stack);

		//	setPlayableStack(stack);
		//Collections.shuffle(playableStack);

		gameStartedMessage();

		JSONArray rolesArray = new JSONArray();
		JSONArray charactersArray = new JSONArray();
		Card roleCard;
		Card characterCard;
		for (int i = 0; i < players.size(); i++) {
			User player = players.get(i);

			if (player != null) {
				roleCard = roleCards.get(0);
				String title = roleCard.getTitle();

				JSONObject role = new JSONObject();
				role.put("playerID", player.getId());
				role.put("role", roleCard.getTitle());
				role.put("roleCard", roleCard);
				rolesArray.put(role);

				if (title.equals("Manager")) {
					currentPlayer = i;
					currentID = player.getId();
				}

				player.setRoleCard(roleCard);
				roleCards.remove(0);

				characterCard = characterCards.get(0);
				JSONObject character = new JSONObject();
				character.put("playerID", player.getId());
				character.put("character", characterCard.getTitle());

				player.setCharacterCard(characterCard);

				player.setPrestige(0);


				int health;
				if (player.getCharacterCard().getMiddleDesc().equals("(Mental Health 3)")) {
					health = 3;
				} else {
					health = 4;
				}

				characterCards.remove(0);

				if (player.getRoleCard().getTitle().equals("Manager")) {
					health += 1;
				}

				player.setHealth(health);
				character.put("health", health);
				charactersArray.put(character);


				List<Card> hand = new ArrayList<Card>();
				JSONArray handArray = new JSONArray();

				for (int j = 0; j < player.getHealth(); j++) {
					hand.add(stack.get(0));
					handArray.put(stack.get(0));
					stack.remove(0);
				}
				player.setHand(hand);
				drawCardsMessage(player.getId(), handArray);
			}
		}
		System.out.println(rolesArray);
		rolesAndCharactersMessage("Roles", rolesArray);
		rolesAndCharactersMessage("Characters", charactersArray);


		propertyChangeSupport.firePropertyChange("UpdatePlayerAttributes", id, null);
		timer.schedule(new RemindTask(), 1000);
		this.gameInitiated = true;
	}

	@Override
	public void run() {
		while (!gameWon) {
			User player = players.get(currentPlayer);
			if (!player.getFired()) {
				player.setMyTurn(true);
				currentPlayerMessage();
				if (player.hasStumblingCards()) {
					isPlaying = true;
					//todo deal with them
					synchronized (this) {
						while (isPlaying) {
							try {
								this.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}

				drawCards(currentID, 2);
				isPlaying = true;
				// TODO turn logic
				synchronized (this) {
					while (isPlaying) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				player.setMyTurn(false);
			}
			nextPlayer();
			turn++;
			isTheGameOver();

		}

	}

	public void isTheGameOver() {
		List<Integer> ids = new ArrayList<>();
		if (managerFired) {
			if (numEvilMonkeys > 0) {
				for (User winner : players) {
					if (winner.getRole().equals("Evil Code Monkey")) {
						ids.add(winner.getId());
					}
				}
			} else {
				if (firedPlayers.size() == numPlayers - 1) {
					for (User winner : players) {
						if (winner.getRole().equals("Consultant")) {
							ids.add(winner.getId());
							break;
						}
					}
				} else {
					for (User winner : players) {
						if (winner.getRole().equals("Evil Code Monkey")) {
							ids.add(winner.getId());
						}
					}
				}
			}
			gameWon = true;
			gameWon(ids);
			return;
		}

		if (numEvilMonkeys == 0 && consultantFired) {
			gameWon = true;
			for (User winner : players) {
				if (winner.getRole().equals("Manager") || winner.getRole().equals("Honest Developer")) {
					ids.add(winner.getId());
				}
			}
			gameWon(ids);
		}

	}

	public void endTurn() {
		System.out.println(noOneIsDefending());
		if (noOneIsDefending()) {
			setIsPlaying(false);
		}

	}

	public boolean noOneIsDefending() {
		for (User user : players) {
			if (user.isBeingAttacked()) {
				return false;
			}
		}
		return true;
	}

	public void playerFired(int playerID) {
		//TODO implementation
		Card role = null;
		for (User player : players) {
			if (player.getId() == playerID) {
				role = player.getRoleCard();
				player.setFired(true);
				firedPlayers.add(player);
				if (player.getRole().equals("Manager")) {
					managerFired = true;
				} else if (player.getRole().equals("Evil Code Monkey")) {
					numEvilMonkeys--;
				} else if (player.getRole().equals("Consultant")) {
					consultantFired = true;
				}
				for (Card card : player.getHand()) {
					discardCard(player.getId(), card, "hand");
				}
				break;
			}
		}
		playerFiredMessage(playerID, role);
	}

	/**
	 * sends playerFired Message
	 */
	protected JSONMessage playerFiredMessage(int playerID, Card role) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", playerID);
		msgBody.put("role", role);
		JSONMessage msg = createJSONMessage("PlayerFired", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	public void decksShuffled() {
		//TODO implementation
		decksShuffledMessage(stack.size(), 0);
	}

	/**
	 * sends Shuffle Message
	 */
	protected JSONMessage decksShuffledMessage(int cardsInDeck, int cardsInHeap) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("cardsInDeck", cardsInDeck);
		msgBody.put("cardsInHeap", cardsInHeap);
		JSONMessage msg = createJSONMessage("Shuffle", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	/**
	 * should be called to change player's health
	 *
	 * @param playerID
	 * @param health
	 */
	public void updateHealth(int playerID, int health) {
		for (User player : players) {
			if (player.getId() == playerID) {
				player.setHealth(health);
				if (health == 0) {
					playerFired(playerID);
				}
			}
		}
		updateHealthMessage(playerID, health);
	}

	/**
	 * sends Health Message
	 */
	protected JSONMessage updateHealthMessage(int playerID, int health) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", playerID);
		msgBody.put("health", health);
		JSONMessage msg = createJSONMessage("Health", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	public void gameWon(List<Integer> playerIDs) {
		//TODO implementation
		JSONArray ids = new JSONArray(playerIDs);
		gameWonMessage(ids);
	}

	/**
	 * sends GameWon Message
	 */
	protected JSONMessage gameWonMessage(JSONArray playerIDs) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerIDs", playerIDs);
		msgBody.put("roleCards", roleCardsHostMessage.getMessage().getJSONObject("msgBody").get("roles"));
		System.out.println("gameWon roleCards: " + roleCardsHostMessage.getMessage().getJSONObject("msgBody").get("roles"));

		JSONMessage msg = createJSONMessage("GameWon", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	public void defendCard(int playerID, Card excuseCard, Card bugCard) {
		heap.add(excuseCard);
		heap.add(bugCard);
		//TODO implementation
		cardDefendedMessage(playerID, excuseCard, bugCard);
	}

	/**
	 * sends CardDefended Message
	 */
	protected JSONMessage cardDefendedMessage(int playerID, Card excuseCard, Card bugCard) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", playerID);
		msgBody.put("card", bugCard);
		msgBody.put("defendedWith", excuseCard);
		JSONMessage msg = createJSONMessage("CardDefended", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	public void playCard(int from, int to, Card card) {
		//TODO implementation
		cardPlayedMessage(from, to, card);
	}

	/**
	 * sends CardPlayed Message
	 */
	protected JSONMessage cardPlayedMessage(int from, int to, Card card) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("from", from);
		msgBody.put("to", to);
		msgBody.put("card", card);
		JSONMessage msg = createJSONMessage("CardPlayed", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}


	public void discardCard(int playerID, Card card, String discardedFrom) {
		heap.add(card);
		//TODO implementation add to heap?
		discardCardMessage(playerID, card, discardedFrom);
	}

	/**
	 * sends CardDiscarded Message
	 */
	protected JSONMessage discardCardMessage(int playerID, Card card, String discardedFrom) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", playerID);
		msgBody.put("card", card);
		msgBody.put("discardedFrom", discardedFrom);
		JSONMessage msg = createJSONMessage("CardDiscarded", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	public void drawCards(int playerID, int numCards) {
		JSONArray cards = new JSONArray();

		for (int n = 0; n < numCards; n++) {
			if (stack.size() == 0) {
				decksShuffled();
			}
			for (User player : players) {
				if (player != null && player.getId() == playerID) {
					player.getHand().add(stack.get(0));
					break;
				}
			}
			//TODO Draw cards from stack and save in Array
			cards.put(stack.get(0));
			stack.remove(0);
		}
		drawCardsMessage(playerID, cards);
	}

	/**
	 * sends YourCards and CardsDrawn Messages
	 */
	protected JSONMessage[] drawCardsMessage(int playerID, JSONArray array) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("cards", array);
		JSONMessage[] msg = new JSONMessage[2];
		msg[0] = createJSONMessage("YourCards", msgBody);
		if (gameInitiated) {
			propertyChangeSupport.firePropertyChange("SendPrivateMessage", msg[0], playerID);
			if (turn == 1 && playerID == host.getId()) {
				cardsHostMessages.add(msg[0].copy());
			}
		} else if (playerID == host.getId()) {
			cardsHostMessages.add(msg[0].copy());
		} else {
			propertyChangeSupport.firePropertyChange("SendPrivateMessage", msg[0], playerID);
		}

		JSONObject msgBodyBroadcast = new JSONObject();
		msgBodyBroadcast.put("gameID", id);
		msgBodyBroadcast.put("playerID", playerID);
		msgBodyBroadcast.put("cards", array.length());
		msgBodyBroadcast.put("cardsInDeck", stack.size());
		msg[1] = createJSONMessage("CardsDrawn", msgBodyBroadcast);
		propertyChangeSupport.firePropertyChange("SendMessage", msg[1], players);
		if (!gameInitiated && playerID != host.getId()) {
			cardsHostMessages.add(msg[1].copy());
		} else if (gameInitiated && turn == 1) {
			cardsHostMessages.add(msg[1].copy());
		}
		return msg;
	}

	/**
	 * gets the next player and sets currentPlayer and currentID
	 *
	 * @return next player's id
	 */
	public int nextPlayer() {
		//TODO null
		if (currentPlayer == numPlayers - 1)
			currentPlayer = -1;
		currentPlayer++;
		currentID = players.get(currentPlayer).getId();
		return currentID;
	}

	/**
	 * sends the CurrentPlayer Message
	 * the playerID is the ID saved in currentID
	 */
	protected JSONMessage currentPlayerMessage() {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", currentID);
		JSONMessage msg = createJSONMessage("CurrentPlayer", msgBody);
		if (turn == 1) {
			cardsHostMessages.add(msg.copy());
		}
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;

	}

	/**
	 * sends the Roles and Characters Messages
	 */
	protected JSONMessage rolesAndCharactersMessage(String type, JSONArray array) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put(type.toLowerCase(), array);
		JSONMessage msg = createJSONMessage(type, msgBody);
		if (type.equals("Roles")) {
			roleCardsHostMessage = msg.copy();
		} else {
			characterCardsHostMessage = msg.copy();
		}
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	/**
	 * sends the gameStarted Message
	 */
	protected JSONMessage gameStartedMessage() {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("cardsInDeck", stack.size());
		msgBody.put("numPlayers", numPlayers);
		JSONMessage msg = createJSONMessage("GameStarted", msgBody);
		gameStartedMessageHost = msg;
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	public JSONMessage cardDelegated(Card card, int id) {
		heap.add(card);
		heap.add(new Card("Ability", "Bug Delegation", "", "",
				"Delegates bug report. \n.25 chance to work", false, false, ""));
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", id);
		msgBody.put("card", card);
		JSONMessage msg = createJSONMessage("CardDelegated", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	private JSONMessage createJSONMessage(String type, JSONObject body) {
		JSONObject msg = new JSONObject();
		msg.put("msgType", type);
		msg.put("msgBody", body);
		return new JSONMessage(msg);
	}

	public void sendMessagesToHostOnStart() {
		propertyChangeSupport.firePropertyChange("SendPrivateMessage", gameStartedMessageHost, host.getId());
		propertyChangeSupport.firePropertyChange("SendPrivateMessage", roleCardsHostMessage, host.getId());
		propertyChangeSupport.firePropertyChange("SendPrivateMessage", characterCardsHostMessage, host.getId());
		for (JSONMessage msg : cardsHostMessages) {
			propertyChangeSupport.firePropertyChange("SendPrivateMessage", msg, host.getId());
		}
	}


	public void addPlayerCreate(User player) {
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i) == null) {
				this.players.set(i, player);
				break;
			}
		}
//		if (!players.contains(player)){
//			players.add(player);
//		}
		//	this.generatePlayerAttributes(); // only necessary for debug
	}

	public void removePlayer(User player) {
		for (int i = 0; i < this.players.size(); i++) {
			if (player.equals(players.get(i))) {
				this.players.set(i, null);
				if (!gameStarted) {
					propertyChangeSupport.firePropertyChange("PlayerRemoved", this, player.getName());
					break;
				} else {
					propertyChangeSupport.firePropertyChange("PlayerRemovedGameRunning", this, player.getName());
					break;
				}
			}
		}
//		this.players.remove(player);
		this.gameLobbyGameState();

		// handle if host leaves the game
		if (player.equals(host)) {
			if (this.getActivePlayers() > 0) {
				for (int i = 0; i < this.players.size(); i++) {
					if (this.players.get(i) != null) {
						propertyChangeSupport.firePropertyChange("GameHostProperty", this, this.players.get(i).getName());
						break;
					}
				}
			} else {
				// TODO: delete game
				propertyChangeSupport.firePropertyChange("LastPlayerRemovedProperty", null, this);
			}
		}
	}

	private Integer calcOpenSpots() {
		int openSpots = 0;
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i) == null) {
				openSpots++;
			}
		}
//		return numPlayers-players.size();
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

//	@Override
//	public String toString() {
//		return "Game(" + id + ", " + name + ", " + password + ")";
//	}

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

	public boolean isGameWon() {
		return gameWon;
	}

	public boolean isManagerFired() {
		return managerFired;
	}

	public String getGameName() {
		return name;
	}

	public void setGameName(String name) {
		this.name = requireNonNull(name);
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	public int getGameId() {
		return id;
	}

	void setGameId(int id) {
		this.id = requireNonNull(id);
	}

	public String[] getPlayerNames() {
		return playerNames;
	}

	public void setPlayerNames(String[] playerNames) {
		this.playerNames = playerNames;
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

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = requireNonNull(hostName);
	}

	public User getHost() {
		return host;
	}

	public void setHost(User host) {
		this.host = host;
		setHostName(host.getName());
	}

	public int getTurn() {
		return turn;
	}

	public JSONMessage getRoleCardsHostMessage() {
		return roleCardsHostMessage;
	}


	public JSONMessage getCharacterCardsHostMessage() {
		return characterCardsHostMessage;
	}


	public List<User> getPlayers() {
		if (host == null) {
			propertyChangeSupport.firePropertyChange("GameHostProperty", this, hostName);
			addPlayer(host);
		}
		return players;

	}

	public void setPlayers(List<User> players) {
		this.players = players;
	}

	public boolean getIsPlaying() {
		return isPlaying;
	}

	public void setIsPlaying(boolean playing) {
		isPlaying = playing;
		synchronized (this) {
			this.notifyAll();
		}
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public int getCurrentID() {
		return currentID;
	}

	public String getGameState() {
		return gameState;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	public boolean isGamePaused() {
		return gamePaused;
	}

	public void setGamePaused(boolean gamePaused) {
		this.gamePaused = gamePaused;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void setGameState(String gameState) {
		this.gameState = gameState;
		propertyChangeSupport.firePropertyChange("GameStateProperty", id, gameState);
	}

	public synchronized void setGameStateInMemoryDatabase(String gameState) {
		this.gameState = gameState;
	}

	public int getActivePlayers() {
		int ap = 0;
		if (host == null) {
			propertyChangeSupport.firePropertyChange("GameHostProperty", this, hostName);
			addPlayer(host);
		}

		for (User player : players) {
			if (player != null) {
				ap++;
			}
			if (!addedPlayers && playerNames != null && playerNames.length > ap) {
				propertyChangeSupport.firePropertyChange("GameIsNewAddPlayers", this, playerNames);
				addedPlayers = true;
			}
		}
		return ap;
	}

	public void setRoleCards() {

		List<Card> roleCardsFour = new ArrayList<Card>();
		List<Card> roleCardsFive = new ArrayList<Card>();
		List<Card> roleCardsSix = new ArrayList<Card>();
		List<Card> roleCardsSeven = new ArrayList<Card>();

		roleCardsFour.add(new Card("Role", "Manager", "", "Aim: Remove evil code \nmonkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, ""));
		roleCardsFour.add(new Card("Role", "Consultant", "", "Aim: Get everyone else \nfired; Manager last!", "Tries to take over the \ncompany", false, false, ""));
		roleCardsFour.add(new Card("Role", "Evil Code Monkey", "", "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, ""));
		roleCardsFour.add(new Card("Role", "Evil Code Monkey", "", "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, ""));

		roleCardsFive.addAll(roleCardsFour);
		roleCardsFive.add(new Card("Role", "Honest Developer", "", "Aim: Get evil code monkeys \nand consultant fired", "Writes good code \nTries to ship \nHelps the manager", false, false, ""));

		roleCardsSix.addAll(roleCardsFive);
		roleCardsSix.add(new Card("Role", "Evil Code Monkey", "", "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, ""));

		roleCardsSeven.addAll(roleCardsSix);
		roleCardsSeven.add(new Card("Role", "Honest Developer", "", "Aim: Get evil code monkeys \nand consultant fired", "Writes good code \nTries to ship \nHelps the manager", false, false, ""));

		if (this.players.size() == 4) {
			roleCards.addAll(roleCardsFour);
		}

		if (this.players.size() == 5) {
			roleCards.addAll(roleCardsFive);
		}

		if (this.players.size() == 6) {
			roleCards.addAll(roleCardsSix);
		}

		if (this.players.size() == 7) {
			roleCards.addAll(roleCardsSeven);
		}
	}

	public void setCharacterCards() {

		characterCards.add(new Card("Character",
				"Mark Zuckerberg", "Founder of Facebook", "(Mental Health 3)",
				"If you lose mental health, \ntake one card from the causer",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Tom Anderson", "Founder of MySpace", "(Mental Health 4)",
				"If you lose mental health, \nyou may take a card from the stack",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Jeff Taylor", "Founder of \nmonster.com", "(Mental Health 4)",
				"No cards left? \nTake one from the stack",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Larry Page", "Founder of Google", "(Mental Health 4)",
				"When somebody gets fired, \nyou take the cards",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Larry Ellison", "Founder of Oracle", "(Mental Health 4)",
				"May take three instead of \ntwo cards from the stack, \nbut has to put one back",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Kent Beck", "Inventor of Extreme \nProgramming", "(Mental Health 4)",
				"Drop two cards and \ngain mental health",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Steve Jobs", "Founder of Apple", "(Mental Health 4)",
				"Gets a second chance",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Steve Ballmer", "Chief Executive Officer \nof Microsoft", "(Mental Health 4)",
				"May use bugs as exuses \nand the other way round",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Linus Torvalds", "Linus Inventor", "(Mental Health 4)",
				"Bugs found can only be \ndeflected with two exuses",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Holier than Thou", "Found Everywhere", "(Mental Health 4)",
				"Sees everyone with -1 prestige",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Konrad Zuse", "Built first programmable \ncomputer", "(Mental Health 3)",
				"Is seen with +1 prestige by everybody",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Bruce Schneier", "Security guru", "(Mental Health 4)",
				"May report an arbitrary \nnumber of bugs",
				false, true, ""));

		characterCards.add(new Card("Character",
				"Terry Weissman", "Founder of Bugzilla", "(Mental Health 4)",
				"Got a bug? .25 percent \nchance for delegation",
				false, true, ""));


	}


	public void setStack() {

		for (int i = 0; i < 4; i++) {

			stack.add(new Card("Action", "Nullpointer!", "--bug--", "",
					"-1 mental health", true, false, ""));
			stack.add(new Card("Action ", "Off By One!", "--bug--", "",
					"-1 mental health", true, false, ""));
			stack.add(new Card("Action", "Class Not Found!", "--bug--", "",
					"-1 mental health", true, false, ""));
			stack.add(new Card("Action", "System Hangs!", "--bug--", "",
					"-1 mental health", true, false, ""));
			stack.add(new Card("Action", "Core Damp!", "--bug--", "",
					"-1 mental health", true, false, ""));
			stack.add(new Card("Action", "Customer Hates \nUI!", "--bug--", "",
					"-1 mental health", true, false, ""));

			stack.add(new Card("Action", "Works For Me!", "--lame excuse--", "",
					"Fends of bug report", true, false, ""));
			stack.add(new Card("Action", "It's a Feature!", "--lame excuse--", "",
					"Fends of bug report", true, false, ""));
			stack.add(new Card("Action", "I'm not Responsible!", "--lame excuse--", "",
					"Fends of bug report", true, false, ""));

			stack.add(new Card("Action", "I refactored \nyour code. \nAway", "", "",
					"Ignors prestige. \nDrop one card", false, false, ""));
			stack.add(new Card("Action", "Pwnd.", "", "",
					"Cede one card. Same or \nlower prestige required", false, false, ""));
		}

		for (int i = 0; i < 3; i++) {
			stack.add(new Card("StumblingBlock", "Off-The-Job \nTraining", "", "Stumbling Block",
					"Not for manager. \nCannot play this turn. \n0.25 chance to deflect", false, false, ""));
			stack.add(new Card("Ability", "Microsoft", "(Previous Job)", "",
					"1 prestige", false, false, ""));
			stack.add(new Card("Action", "System Integration", "", "",
					"My code is better than \nyours!", false, false, ""));  //should be 9?
		}
		for (int i = 0; i < 2; i++) {
			stack.add(new Card("Action", "Coffee", "--Solution--", "",
					"+1 mental health", true, false, ""));
			stack.add(new Card("Action", "Code+Fix \nSession", "--Solution--", "",
					"+1 mental health", true, false, ""));
			stack.add(new Card("Action", "I know regular \nexpressions", "--Solution--", "",
					"+1 mental health", true, false, ""));

			stack.add(new Card("Action", "Standup \nMeeting", "", "",
					"The cards are on the \ntable", false, false, ""));
			stack.add(new Card("Action", "Personal Coffee \nMachine", "", "",
					"Takes 2 cards", false, false, ""));
			stack.add(new Card("Action", "Boring Meeting", "", "",
					"Play bug or lose \nmental health", false, false, ""));

			stack.add(new Card("Ability", "Bug Delegation", "", "",
					"Delegates bug report. \n.25 chance to work", false, false, ""));
			stack.add(new Card("Ability", "Wears Tie at \nWork", "", "",
					"Is seen with +1 prestige by \neverybody", false, false, ""));
			stack.add(new Card("Ability", "Accenture", "-previous job-", "",
					"May report several bugs in \none round", false, false, ""));
			stack.add(new Card("Ability", "Google", "-previous job-", "",
					"2 prestige", false, false, ""));

		}

		stack.add(new Card("Action", "BUG", "-bug-", "",
				"-1 mental health", true, false, ""));


		stack.add(new Card("Action", "Red Bull \nDispenser", "", "",
				"Take 3 Cards", false, false, ""));

		stack.add(new Card("Action", "Heisenbug", "", "",
				"Bugs for everybody!", false, false, ""));

		stack.add(new Card("Action", "LAN Party", "", "",
				"Mental health for \neverybody!", false, false, ""));

		stack.add(new Card("Ability", "Wears \nSunglasses at \nWork", "", "",
				"Sees everybody with -1 \nprestige", false, false, ""));

		stack.add(new Card("Ability", "NASA", "-previous job-", "",
				"3 prestige", false, false, ""));

		stack.add(new Card("StumblingBlock", "Fortran \nMaintenance", "BOOM", "Stumbling Block",
				"Only playable on self. \nTakes 3 health points. \n.85 chance to deflect to \nnext developer",
				false, false, ""));

	}

//	public void setPlayableStack(List<Card> stack) {
//		for (Card card : this.stack) {
//			if (card.isPlayable())
//				this.playableStack.add(card);
//
//		}
//
//	}

	public List<Card> getStack() {
		return stack;
	}

//	public List<Card> getPlayableStack() {
//		return playableStack;
//	}

	public List<Card> getRoleCards() {
		return roleCards;
	}

	public void unsetRoleCards() {
		roleCards = new ArrayList<>();
	}

	public List<Card> getCharacterCards() {
		return characterCards;
	}


	class RemindTask extends TimerTask {

		public void run() {
			sendMessagesToHostOnStart();
			//	drawCards(getCurrentID(),2);
		}
	}


//
//	public List<Card> getHeap() {
//		return heap;
//	}
//	public void setHeap(List<Card> heap) {
//		this.heap = heap;
//	}

}
