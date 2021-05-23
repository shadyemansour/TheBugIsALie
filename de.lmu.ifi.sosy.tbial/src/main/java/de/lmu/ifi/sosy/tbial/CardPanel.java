package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import de.lmu.ifi.sosy.tbial.db.Card;

public class CardPanel extends Panel {
 	/** UID for serialization. */
	private static final long serialVersionUID = 1L;
	
	Card card;

	public CardPanel(String id, IModel<Card> cardModel) {
//		super(id, cardModel);
//		IModel<Card> compound = new CompoundPropertyModel<Card>(cardModel);
//		card = cardModel.getObject();
//		add(new Label("bottomDesc", card.getBottomDesc()));
		
		super(id, new CompoundPropertyModel<Card>(cardModel));
		
		WebMarkupContainer border = new WebMarkupContainer("border");
		add(border);
		String color;
		Boolean makeMiddleBold = false;
		switch (cardModel.getObject().getType()) {
			case "Role":
				color = "green";
				makeMiddleBold = true;
				break;
			case "Character":
				color = "yellow";
				break;
			case "Action":
				color = "black";
				break;
			case "Ability":
				color = "blue";
				break;
			case "StumblingBlock":
				color = "violet";
				break;
			default:
				color = "black";
				break;
		}
		String borderColor = String.format("border-color: %s", color);
		border.add(new AttributeAppender("style", borderColor));
		
		border.add(new Label("title"));
		border.add(new Label("subTitle"));
		Label middleDesc = new Label("middleDesc");
		if (makeMiddleBold) {
			middleDesc.add(new AttributeAppender("style", "font-weight: bold"));
		}
		border.add(middleDesc);
		border.add(new Label("bottomDesc"));
	}

}

//, 
//String type, // defines color
//String title, // bold printed title of the card
//String middleDesc,
//String bottomDesc
