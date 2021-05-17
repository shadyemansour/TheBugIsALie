package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.User;
import org.apache.wicket.markup.html.basic.Label;

@AuthenticationRequired
public class Gametable extends BasePage {
    /** UID for serialization. */
  private static final long serialVersionUID = 1L;
  public int numplay = 4;
  
  public Gametable() {
  	
  	Label player1 = new Label("p1", "player1-name");
  	add(player1);
  	Label player2 = new Label("p2", "player2-name");
  	add(player2);
  	Label player3 = new Label("p3", "player3-name");
  	add(player3);
  	Label player4 = new Label("p4", "player4-name");
  	add(player4);

  }
}