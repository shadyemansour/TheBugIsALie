package de.lmu.ifi.sosy.tbial.db;

public class Card {

    private String color;
    private String cardType;
    private String title;
    private String bottomDescription;
    private boolean playable;

    public Card(String color, String cardType, String title,
                      String bottomDescription, boolean playable){
        this.color=color;
        this.cardType= cardType;
        this.title=title;
        this.bottomDescription=bottomDescription;
        this.playable=playable;


    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }


    public String getColor(){
        return (color);
    }

    public String getCardType() {
        return cardType;
    }

    public String getTitle(){
        return (title);
    }

    public String getBottomDescription() {
        return bottomDescription;
    }

    public boolean isPlayable() {
        return playable;
    }
}
