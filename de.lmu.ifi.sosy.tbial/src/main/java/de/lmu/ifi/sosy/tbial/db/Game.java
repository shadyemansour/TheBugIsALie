package de.lmu.ifi.sosy.tbial.db;

import org.apache.wicket.model.IModel;
import static java.util.Objects.requireNonNull;

import de.lmu.ifi.sosy.tbial.db.SQLDatabase;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;


public class Game implements Serializable {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

	private String name;
	private int id;
	private Boolean pwProtected;
	private String password;
	private Integer numPlayers; // 4 - 7
	private List<User> players;
	private String hostName;
	private User host;
	private int playersTurn; // 1 - 7

	     //   private  ArrayList<Card> charakterCards = new  ArrayList<Card>(); TODO later (US37)

  private List<Card> stack; // all action, ability, and stumbling blocks cards
  private List<Card> playableStack; // only bugs, exuses, solutions playable

    //    private  List<Card> heap = new  ArrayList<Card>(); TODO later

	
//	private GameState state; 
	private volatile String gameState; // waiting for players, ready, playing, stopped, game over
	protected PropertyChangeSupport propertyChangeSupport;
//	private ArrayList<Card> stack;
//	private ArrayList<Card> heap;
	
	public Game(int id, String name, String password, Integer numPlayers, String gameState, String hostName) {
		this.id = id;
		this.name = requireNonNull(name);
		this.password = password;
		this.gameState = gameState;
		if (password.length() == 0) {
			this.pwProtected = false;
		} else {
			this.pwProtected = true;
		}
		this.numPlayers = requireNonNull(numPlayers);
		this.players = new ArrayList<User>();
		this.hostName = requireNonNull(hostName);
		for (int i=0; i<numPlayers; i++) {
			this.players.add(null);
		}
		this.generatePlayerAttributes();
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * method created for setup of us7
	 * player attributes should be generated random for real game
	 */
	public void generatePlayerAttributes() {
		String managerRole = "Manager"; // only 1 card exists
		String consultantRole = "Consultant"; // only 1 card exists
		String honestDeveloperRole = "Honest Developer"; // 2 cards exist
		String evilCodeMonkeyRole = "Evil Code Monkey"; // 3 cards exist
		
		String markZuckerbergCharacter = "Mark Zuckerberg";
		String tomAndersonCharacter = "Tom Anderson";
		String jeffTaylorCharacter = "Jeff Taylor";
		String larryPageCharacter = "Larry Page";
		String larryEllisonCharacter = "Larry Ellison";
		String kentBeckCharacter = "Kent Beck";
		String steveJobsCharacter = "Steve Jobs";
		
		for (int i=0; i<this.players.size(); i++) {
			if (this.players.get(i) != null) {
				if (i==0) {
					this.players.get(i).setRole(managerRole);
					this.players.get(i).setCharacter(markZuckerbergCharacter);
				} else if (i==1) {
					this.players.get(i).setRole(consultantRole);
					this.players.get(i).setCharacter(tomAndersonCharacter);
				} else if (i==2) {
					this.players.get(i).setRole(honestDeveloperRole);
					this.players.get(i).setCharacter(jeffTaylorCharacter);
				} else if (i==3) {
					this.players.get(i).setRole(evilCodeMonkeyRole);
					this.players.get(i).setCharacter(larryPageCharacter);
				} else if (i==4) {
					this.players.get(i).setRole(honestDeveloperRole);
					this.players.get(i).setCharacter(larryEllisonCharacter);
				} else if (i==5) {
					this.players.get(i).setRole(evilCodeMonkeyRole);
					this.players.get(i).setCharacter(kentBeckCharacter);
				} else if (i==6) {
					this.players.get(i).setRole(evilCodeMonkeyRole);
					this.players.get(i).setCharacter(steveJobsCharacter);
				}
				this.players.get(i).setHealth(3);
				this.players.get(i).setPrestige(1);
			}
		}
		

	}

	public void addPlayer(User player) {
		for (int i=0; i<this.players.size(); i++) {
			if (this.players.get(i) == null) {
				this.players.set(i, player);
				break;
			}
		}
//		if (!players.contains(player)){
//			players.add(player);
//		}
		this.gameLobbyGameState();
		this.generatePlayerAttributes(); // only necessary for debug
	}

	public void removePlayer(User player) {
		for (int i=0; i<this.players.size(); i++) {
			if (this.players.get(i) == player) {
				this.players.set(i, null);
			}
		}
//		this.players.remove(player);
		this.gameLobbyGameState();
		
		// handle if host leaves the game
		if (player.equals(host)) {
			if (this.getActivePlayers() > 0) {
				for (int i=0; i<this.players.size(); i++) {
					if (this.players.get(i) != null) {
						this.setHost(this.players.get(i));
						break;
					}
				}
			} else {
				// TODO: delete game
				propertyChangeSupport.firePropertyChange("LastPlayerRemovedProperty", player, this);
			}
		}
	}
	
	private Integer calcOpenSpots() {
		int openSpots = 0;
		for (int i=0; i<this.players.size(); i++) {
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
	}

	public int getPlayersTurn() {
		return playersTurn;
	}
	public void setPlayersTurn(int playersTurn) {
		this.playersTurn = playersTurn;
	}
	
	public List<User> getPlayers() {
		if (host == null){
			propertyChangeSupport.firePropertyChange("GameHostProperty",this, hostName);
			players.add(host);
		}
		return players;

	}
	public void setPlayers(List<User> players) {
		this.players = players;
	}
	
	public String getGameState() {
		return gameState;
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void setGameState(String gameState) {
		this.gameState = gameState;
		propertyChangeSupport.firePropertyChange("GameStateProperty",id, gameState);
	}

	public int getActivePlayers() {
		int ap=0;
		if (host == null){
			propertyChangeSupport.firePropertyChange("GameHostProperty",this, hostName);
			players.add(host);
		}
		for (User player:players){
			if(player!=null){
				ap++;
			}
		}
		return ap;
	}

	   public void startGame() {
     	stack = new  ArrayList<Card>(); // all action, ability, and stumbling blocks cards
        playableStack = new  ArrayList<Card>();
        setStack();
        Collections.shuffle(this.stack);
        setPlayableStack(this.stack);
        Collections.shuffle(this.playableStack);

        for (User player: this.players) {

            player.setPrestige(0);
            player.setHealth(4);
            /* TODO health +1 for MANAGER */

            for (int i = 0; i < player.getHealth(); i++) {
                player.getHand().add(this.playableStack.get(i));

            }

            for (int i = 0; i < player.getHealth(); i++) {
                this.playableStack.remove(i);

            }
        }



        }
        
    public void setStack() {

        for (int i=1;i<=4;i++) {

            this.stack.add(new Card("black","Action Card", "BUG: Nullpointer!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card ", "BUG: Off By One!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card", "BUG: Class Not Found!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card", "BUG: System Hangs!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card", "BUG: Core Damp!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card", "BUG: Customer Hates UI!",
                    "-1 mental health", true));

            this.stack.add(new Card("black","Action Card", "EXUSE: Works For Me!",
                    "Fends off bug report", true));
            this.stack.add(new Card("black","Action Card", "EXUSE: It's a Feature!",
                    "Fends off bug report", true));
            this.stack.add(new Card("black","Action Card", "EXUSE: I'm not Responsible!",
                    "Fends off bug report", true));

            this.stack.add(new Card("black","Action Card", "I refactored your code. Away",
                    "Ignors prestige. Drop one card", false));
            this.stack.add(new Card("black","Action Card", "Pwnd.",
                    "Cede one card. Same or lower prestige required", false));




        }

        for (int i=1;i<=3;i++){

            this.stack.add(new Card("black","Action Card", "System Integration",
                    "My code is better than yours!", false));
            this.stack.add(new Card("blue","Ability Card", "Microsoft (Previous Job)",
                    "1 prestige", false));
            this.stack.add(new Card("magenta","Stumbling Block", "Off-The-Job Training",
                    "Not for manager. Cannot play this turn. .25 chance to deflect", false));


        }
        for (int i=1;i<=2;i++){

            this.stack.add(new Card("black","Action Card", "SOLUTION: Coffee",
                    "+1 mental health", true));
            this.stack.add(new Card("black","Action Card", "SOLUTION: Code+Fix Session",
                    "+1 mental health", true));
            this.stack.add(new Card("black","Action Card", "SOLUTION: I know regular expressions",
                    "+1 mental health", true));

            this.stack.add(new Card("black","Action Card", "Standup Meeting",
                    "The cards are on the table", false));
            this.stack.add(new Card("black","Action Card", "Personal Coffee Machine",
                    "Takes 2 cards", false));
            this.stack.add(new Card("black","Action Card", "Boring Meeting",
                    "Play bug or lose mental health", false));

            this.stack.add(new Card("blue","Ability Card", "Bug Delegation",
                    "Delegates bug report. .25 chance to work", false));
            this.stack.add(new Card("blue","Ability Card", "Wears Tie at Work",
                    "Is seen with +1 prestige by everybody", false));
            this.stack.add(new Card("blue","Ability Card", "Accenture -previous job-",
                    "May report several bugs in one round", false));
            this.stack.add(new Card("blue","Ability Card", "Google -previous job-",
                    "2 prestige", false));

        }

        this.stack.add(new Card("black","Action Card", "BUG ",
                "-1 mental health", true));


        this.stack.add(new Card("black","Action Card", "Red Bull Dispenser",
                "Take 3 Cards", false));

        this.stack.add(new Card("black","Action Card", "Heisenbug",
                "Bugs for everybody!", false));

        this.stack.add(new Card("black","Action Card", "LAN Party",
                "Mental health for everybody!", false));

        this.stack.add(new Card("blue","Ability Card", "Wears Sunglasses at Work",
                "Sees everybody with -1 prestige", false));

        this.stack.add(new Card("blue","Ability Card", "NASA -previous job-",
                "3 prestige", false));

        this.stack.add(new Card("magenta","Stumbling Block", "Fortran Maintenance BOOM",
                "Only playable on self. Takes 3 health points. .85 chance to deflect to next developer", false));


    }

    public void setPlayableStack(List<Card> stack){


        for (Card card : this.stack){
            if (card.isPlayable())
                this.playableStack.add(card);

        }


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
