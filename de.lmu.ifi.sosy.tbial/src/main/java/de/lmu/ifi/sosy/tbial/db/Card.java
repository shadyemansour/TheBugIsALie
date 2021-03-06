package de.lmu.ifi.sosy.tbial.db;

import java.io.Serializable;
import java.util.Objects;

public class Card implements Serializable {

  private final String type;
  private String title;
  private final String subTitle;
  private final String middleDesc;
  private final String bottomDesc;
  private boolean playable;
  private boolean visible;
  private final String gameLogic;

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


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Card card = (Card) o;
    return playable == card.playable && visible == card.visible && type.equals(card.type) && title.equals(card.title) && subTitle.equals(card.subTitle) && middleDesc.equals(card.middleDesc) && bottomDesc.equals(card.bottomDesc) && gameLogic.equals(card.gameLogic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, title, subTitle, middleDesc, bottomDesc, playable, visible, gameLogic);
  }
}
