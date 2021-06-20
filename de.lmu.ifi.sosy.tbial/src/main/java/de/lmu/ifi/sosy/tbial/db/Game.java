package de.lmu.ifi.sosy.tbial.db;


import static java.util.Objects.requireNonNull;

import de.lmu.ifi.sosy.tbial.networking.JSONMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.*;


public class Game implements Serializable {

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
	private int playersTurn; // 1 - 7
	private boolean gamePaused;
	private boolean gameStarted;
	private boolean addedPlayers;
	private int currentPlayer;
	private int currentID;


	//   private  ArrayList<Card> charakterCards = new  ArrayList<Card>(); TODO later (US37)

	private List<Card> roleCards;
	private List<Card> characterCards;
	private List<Card> stack; // all action, ability, and stumbling blocks cards
	private List<Card> playableStack; // only bugs, exuses, solutions playable


	//    private  List<Card> heap = new  ArrayList<Card>(); TODO later

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
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i) == null) {
				this.players.set(i, player);
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
		System.out.println("game " + this);
		gameStarted = true;
		roleCards = new ArrayList<Card>();
		characterCards = new ArrayList<Card>();
		stack = new ArrayList<Card>();
		playableStack = new ArrayList<Card>();


		setRoleCards();
		Collections.shuffle(roleCards);
		setCharacterCards();
		Collections.shuffle(characterCards);
		setStack();
		Collections.shuffle(stack);
		setPlayableStack(stack);
		Collections.shuffle(playableStack);
		gameStartedMessage();


		JSONArray rolesArray = new JSONArray();
		JSONArray charactersArray = new JSONArray();
		Card roleCard;
		Card characterCard;
		for (User player : this.players) {
			if (player != null) {
				roleCard = roleCards.get(0);

				JSONObject role = new JSONObject();
				role.put("playerID", player.getId());
				role.put("role", roleCard.getTitle());
				rolesArray.put(role);

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

				for (int i = 0; i < player.getHealth(); i++) {
					hand.add(stack.get(i));
					stack.remove(i);
				}
				player.setHand(hand);
			}
		}
		rolesAndCharactersMessage("Roles", rolesArray);
		rolesAndCharactersMessage("Characters", charactersArray);

		currentPlayer = 0;
		currentID = players.get(currentPlayer).getId();
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
		//TODO implementation
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

	public void gameWon(int playerID) {
		//TODO implementation
		gameWonMessage(playerID);
	}

	/**
	 * sends GameWon Message
	 */
	protected JSONMessage gameWonMessage(int playerID) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", playerID);
		JSONMessage msg = createJSONMessage("GameWon", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	public void defendCard(int playerID, Card card) {
		//TODO implementation
		cardDefendedMessage(playerID, card);
	}

	/**
	 * sends CardDefended Message
	 */
	protected JSONMessage cardDefendedMessage(int playerID, Card card) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", playerID);
		msgBody.put("card", card);
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


	public void discardCard(int playerID, Card card) {
		//TODO implementation add to heap?
		discardCardMessage(playerID, card);
	}

	/**
	 * sends CardDiscarded Message
	 */
	protected JSONMessage discardCardMessage(int playerID, Card card) {
		JSONObject msgBody = new JSONObject();
		msgBody.put("gameID", id);
		msgBody.put("playerID", playerID);
		msgBody.put("card", card);
		JSONMessage msg = createJSONMessage("CardDiscarded", msgBody);
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	public void drawCards(int playerID, int numCards) {
		JSONArray cards = new JSONArray();

		for (int n = 0; n < numCards; n++) {
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
		propertyChangeSupport.firePropertyChange("SendPrivateMessage", msg[0], playerID);

		JSONObject msgBodyBroadcast = new JSONObject();
		msgBodyBroadcast.put("gameID", id);
		msgBodyBroadcast.put("playerID", playerID);
		msgBodyBroadcast.put("cards", array.length());
		msgBodyBroadcast.put("cardsInDeck", stack.size());
		msg[1] = createJSONMessage("CardsDrawn", msgBodyBroadcast);
		propertyChangeSupport.firePropertyChange("SendMessage", msg[1], players);
		return msg;
	}

	/**
	 * gets the next player and sets currentPlayer and currentID
	 * calls currentPlayerMessage()
	 *
	 * @return next player's id
	 */
	public int nextPlayer() {
		if (currentPlayer == numPlayers - 1)
			currentPlayer = -1;
		currentPlayer++;
		currentID = players.get(currentPlayer).getId();
		currentPlayerMessage();
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
		propertyChangeSupport.firePropertyChange("SendMessage", msg, players);
		return msg;
	}

	private JSONMessage createJSONMessage(String type, JSONObject body) {
		JSONObject msg = new JSONObject();
		msg.put("msgType", type);
		msg.put("msgBody", body);
		return new JSONMessage(msg);
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

	public int getPlayersTurn() {
		return playersTurn;
	}

	public void setPlayersTurn(int playersTurn) {
		this.playersTurn = playersTurn;
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

		roleCardsFour.add(new Card("Role", "Manager", null, "Aim: Remove evil code \nmonkies and consultant", "Tries to ship\nTries to stay in charge\nMental Health: +1", false, true, null));
		roleCardsFour.add(new Card("Role", "Consultant", null, "Aim: Get everyone else \nfired; Manager last!", "Tries to take over the \ncompany", false, false, null));
		roleCardsFour.add(new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null));
		roleCardsFour.add(new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null));

		roleCardsFive.addAll(roleCardsFour);
		roleCardsFive.add(new Card("Role", "Honest Developer", null, "Aim: Get evil code monkeys \nand consultant fired", "Writes good code \nTries to ship \nHelps the manager", false, false, null));

		roleCardsSix.addAll(roleCardsFive);
		roleCardsSix.add(new Card("Role", "Evil Code Monkey", null, "Aim: Get the Manager \nfired.", "Has no skills in \ncoding, testing, \nand design.", false, false, null));

		roleCardsSeven.addAll(roleCardsSix);
		roleCardsSeven.add(new Card("Role", "Honest Developer", null, "Aim: Get evil code monkeys \nand consultant fired", "Writes good code \nTries to ship \nHelps the manager", false, false, null));

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
				false, true, null));

		characterCards.add(new Card("Character",
				"Tom Anderson", "Founder of MySpace", "(Mental Health 4)",
				"If you lose mental health, \nyou may take a card from the stack",
				false, true, null));

		characterCards.add(new Card("Character",
				"Jeff Taylor", "Founder of \nmonster.com", "(Mental Health 4)",
				"No cards left? \nTake one from the stack",
				false, true, null));

		characterCards.add(new Card("Character",
				"Larry Page", "Founder of Google", "(Mental Health 4)",
				"When somebody gets fired, \nyou take the cards",
				false, true, null));

		characterCards.add(new Card("Character",
				"Larry Ellison", "Founder of Oracle", "(Mental Health 4)",
				"May take three instead of \ntwo cards from the stack, \nbut has to put one back",
				false, true, null));

		characterCards.add(new Card("Character",
				"Kent Beck", "Inventor of Extreme \nProgramming", "(Mental Health 4)",
				"Drop two cards and \ngain mental health",
				false, true, null));

		characterCards.add(new Card("Character",
				"Steve Jobs", "Founder of Apple", "(Mental Health 4)",
				"Gets a second chance",
				false, true, null));

		characterCards.add(new Card("Character",
				"Steve Ballmer", "Chief Executive Officer \nof Microsoft", "(Mental Health 4)",
				"May use bugs as exuses \nand the other way round",
				false, true, null));

		characterCards.add(new Card("Character",
				"Linus Torvalds", "Linus Inventor", "(Mental Health 4)",
				"Bugs found can only be \ndeflected with two exuses",
				false, true, null));

		characterCards.add(new Card("Character",
				"Holier than Thou", "Found Everywhere", "(Mental Health 4)",
				"Sees everyone with -1 prestige",
				false, true, null));

		characterCards.add(new Card("Character",
				"Konrad Zuse", "Built first programmable \ncomputer", "(Mental Health 3)",
				"Is seen with +1 prestige by everybody",
				false, true, null));

		characterCards.add(new Card("Character",
				"Bruce Schneier", "Security guru", "(Mental Health 4)",
				"May report an arbitrary \nnumber of bugs",
				false, true, null));

		characterCards.add(new Card("Character",
				"Terry Weissman", "Founder of Bugzilla", "(Mental Health 4)",
				"Got a bug? .25 percent \nchance for delegation",
				false, true, null));


	}


	public void setStack() {

		for (int i = 1; i <= 4; i++) {

			stack.add(new Card("Action", "Nullpointer!", "--bug--", null,
					"-1 mental health", true, false, null));
			stack.add(new Card("Action ", "Off By One!", "--bug--", null,
					"-1 mental health", true, false, null));
			stack.add(new Card("Action", "Class Not Found!", "--bug--", null,
					"-1 mental health", true, false, null));
			stack.add(new Card("Action", "System Hangs!", "--bug--", null,
					"-1 mental health", true, false, null));
			stack.add(new Card("Action", "Core Damp!", "--bug--", null,
					"-1 mental health", true, false, null));
			stack.add(new Card("Action", "Customer Hates \nUI!", "--bug--", null,
					"-1 mental health", true, false, null));

			stack.add(new Card("Action", "Works For Me!", "--lame excuse--", null,
					"Fends of bug report", true, false, null));
			stack.add(new Card("Action", "It's a Feature!", "--lame excuse--", null,
					"Fends of bug report", true, false, null));
			stack.add(new Card("Action", "I'm not Responsible!", "--lame excuse--", null,
					"Fends of bug report", true, false, null));

			stack.add(new Card("Action", "I refactored \nyour code. \nAway", null, null,
					"Ignors prestige. \nDrop one card", false, false, null));
			stack.add(new Card("Action", "Pwnd.", null, null,
					"Cede one card. Same or \nlower prestige required", false, false, null));
			stack.add(new Card("Action", "System Integration", null, null,
					"My code is better than \nyours!", false, false, null));
			stack.add(new Card("Ability", "Microsoft", "(Previous Job)", null,
					"1 prestige", false, false, null));
			stack.add(new Card("StumblingBlock", "Off-The-Job \nTraining", null, "Stumbling Block",
					"Not for manager. \nCannot play this turn. \n0.25 chance to deflect", false, false, null));
		}
		System.out.println(stack);

		for (int i = 1; i <= 3; i++) {
			stack.add(new Card("Action", "Coffee", "--Solution--", null,
					"+1 mental health", true, false, null));
			stack.add(new Card("Action", "Code+Fix \nSession", "--Solution--", null,
					"+1 mental health", true, false, null));
			stack.add(new Card("Action", "I know regular \nexpressions", "--Solution--", null,
					"+1 mental health", true, false, null));

			stack.add(new Card("Action", "Standup \nMeeting", null, null,
					"The cards are on the \ntable", false, false, null));
			stack.add(new Card("Action", "Personal Coffee \nMachine", null, null,
					"Takes 2 cards", false, false, null));
			stack.add(new Card("Action", "Boring Meeting", null, null,
					"Play bug or lose \nmental health", false, false, null));

			stack.add(new Card("Ability", "Bug Delegation", null, null,
					"Delegates bug report. \n.25 chance to work", false, false, null));
			stack.add(new Card("Ability", "Wears Tie at \nWork", null, null,
					"Is seen with +1 prestige by \neverybody", false, false, null));
			stack.add(new Card("Ability", "Accenture", "-previous job-", null,
					"May report several bugs in \none round", false, false, null));
			stack.add(new Card("Ability", "Google", "-previous job-", null,
					"2 prestige", false, false, null));

		}

		stack.add(new Card("Action", "BUG", "-bug-", null,
				"-1 mental health", true, false, null));


		stack.add(new Card("Action", "Red Bull \nDispenser", null, null,
				"Take 3 Cards", false, false, null));

		stack.add(new Card("Action", "Heisenbug", null, null,
				"Bugs for everybody!", false, false, null));

		stack.add(new Card("Action", "LAN Party", null, null,
				"Mental health for \neverybody!", false, false, null));

		stack.add(new Card("Ability", "Wears \nSunglasses at \nWork", null, null,
				"Sees everybody with -1 \nprestige", false, false, null));

		stack.add(new Card("Ability", "NASA", "-previous job-", null,
				"3 prestige", false, false, null));

		stack.add(new Card("StumblingBlock", "Fortran \nMaintenance", "BOOM", "Stumbling Block",
				"Only playable on self. \nTakes 3 health points. \n.85 chance to deflect to \nnext developer",
				false, false, null));

	}

	public void setPlayableStack(List<Card> stack) {
		for (Card card : this.stack) {
			if (card.isPlayable())
				this.playableStack.add(card);

		}

	}

	public List<Card> getStack() {
		return stack;
	}

	public List<Card> getPlayableStack() {
		return playableStack;
	}

	public List<Card> getRoleCards() {
		return roleCards;
	}

	public List<Card> getCharacterCards() {
		return characterCards;
	}


//
//	public List<Card> getHeap() {
//		return heap;
//	}
//	public void setHeap(List<Card> heap) {
//		this.heap = heap;
//	}

}
