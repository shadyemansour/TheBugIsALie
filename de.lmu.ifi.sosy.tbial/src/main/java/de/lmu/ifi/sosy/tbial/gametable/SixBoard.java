package de.lmu.ifi.sosy.tbial.gametable;

//import de.lmu.ifi.sosy.tbial.db.User;
import de.lmu.ifi.sosy.tbial.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

@AuthenticationRequired
public class SixBoard extends GameView {
    /** UID for serialization. */
  private static final long serialVersionUID = 1L;
  private int numPlayer = 5;
  
  public SixBoard() {
  	
  	Label player1 = new Label("p1", "player1-name");
  	add(player1);
  	Label player2 = new Label("p2", "player2-name");
  	add(player2);
  	Label player3 = new Label("p3", "player3-name");
  	add(player3);
  	Label player4 = new Label("p4", "player4-name");
  	add(player4);
    Label player5 = new Label("p5", "player5-name");
  	add(player5);
    Label player6 = new Label("p6", "player6-name");
  	add(player6);

  }
}