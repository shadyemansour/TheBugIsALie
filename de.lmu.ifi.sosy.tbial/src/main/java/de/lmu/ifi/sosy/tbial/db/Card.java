package de.lmu.ifi.sosy.tbial.db;

import java.io.Serializable;

public class Card implements Serializable {

    private String type;
    private String title;
    private String subTitle;
    private String middleDesc;
    private String bottomDesc;
    private boolean playable;
    private boolean visible;

    public Card(String type, String title, String subTitle, String middleDesc,
                      String bottomDesc, boolean playable, boolean visible){
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.middleDesc = middleDesc;
        this.bottomDesc = bottomDesc;
        this.playable = playable;
        this.visible = visible;

    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }


    public String getType() {
        return type;
    }
    
    public void setTitle(String title ) {
    	this.title = title;
    }

    public String getTitle(){
        return title;
    }
    
    public String getSubTitle() {
    	return subTitle;
    }
    
    public String getMiddleDesc( ) {
    	return middleDesc;
    }

    public String getBottomDesc() {
        return bottomDesc;
    }

    public boolean isPlayable() {
        return playable;
    }
    
    public void setVisible(boolean visible) {
    	this.visible = visible;
    }
    
    public boolean isVisible() {
    	return visible;
    }
}
