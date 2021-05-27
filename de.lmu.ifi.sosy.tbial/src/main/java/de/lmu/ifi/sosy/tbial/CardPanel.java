package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.AttributeModifier;
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
	
	IModel<Card> cardModel;

	public CardPanel(String id, IModel<Card> cardModel) {
		super(id, new CompoundPropertyModel<Card>(cardModel));
		setOutputMarkupId(true);
//		setDefaultModelObject(cardModel);
		this.cardModel = cardModel;
		
		WebMarkupContainer frame = new WebMarkupContainer("frame");
		add(frame);
		WebMarkupContainer border = new WebMarkupContainer("border");
		frame.add(border);
		
		if (!cardModel.getObject().isVisible()) {
			frame.add(new AttributeModifier("id", "card-back"));
		}
		
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
	
	protected void onModelChanged() {
		System.out.println("onModelChanged");
		WebMarkupContainer frame = new WebMarkupContainer("frame");
		add(frame);
		
		if (!cardModel.getObject().isVisible()) {
			frame.add(new AttributeModifier("id", "card-back"));
		}
	}

}
