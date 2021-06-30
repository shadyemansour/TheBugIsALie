package de.lmu.ifi.sosy.tbial.db;

import java.io.Serializable;
import java.util.Objects;

public class Card implements Serializable {

  private String type;
  private String title;
  private String subTitle;
  private String middleDesc;
  private String bottomDesc;
  private boolean playable;
  private boolean visible;
  private String gameLogic;

  public Card(String type, String title, String subTitle, String middleDesc,
              String bottomDesc, boolean playable, boolean visible, String gameLogic) {
    this.type = type;
    this.title = title;
    this.subTitle = subTitle;
    this.middleDesc = middleDesc;
    this.bottomDesc = bottomDesc;
    this.playable = playable;
    this.visible = visible;
    this.gameLogic = gameLogic;

  }

  public void setPlayable(boolean playable) {
    this.playable = playable;
  }


  public String getType() {
    return type;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public String getSubTitle() {
    return subTitle;
  }

  public String getMiddleDesc() {
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


  public String getGameLogic() {
    return gameLogic;
  }

  public void setGameLogic(String gameLogic) {
    this.gameLogic = gameLogic;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Card card = (Card) o;
    return playable == card.playable && visible == card.visible && Objects.equals(type, card.type) && Objects.equals(title, card.title) && Objects.equals(subTitle, card.subTitle) && Objects.equals(middleDesc, card.middleDesc) && Objects.equals(bottomDesc, card.bottomDesc) && Objects.equals(gameLogic, card.gameLogic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, title, subTitle, middleDesc, bottomDesc, playable, visible, gameLogic);
  }
}
