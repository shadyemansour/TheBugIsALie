package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class GameView extends BasePage {
	
	private static final long serialVerisonUID = 1L;
	
	public GameView(PageParameters parameters) {
		//super(parameters);
		String name = parameters.get("name").toString();
		System.out.println(name);
	}
	
}