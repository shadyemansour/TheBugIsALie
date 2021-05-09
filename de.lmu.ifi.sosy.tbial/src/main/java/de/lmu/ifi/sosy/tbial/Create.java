package de.lmu.ifi.sosy.tbial;

import java.util.Arrays;
import java.util.List;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.CheckBox;

@AuthenticationRequired
public class Create extends BasePage {
	
	/** UID for serialization. */
	private static final long serialVersionUID = 1L;
	
	private final Button submitGame;
	private final TextField<String> gameName;
	private final PasswordTextField passwordField;
	private List players = Arrays.asList(new Integer[] {4, 5, 6, 7});
	public Integer selected = 4;
	private final CheckBox privateGame;
	
	public Create() {
		gameName = new TextField<>("name", new Model<>(""));
		gameName.setRequired(true);
		passwordField = new PasswordTextField("password", new Model<>(""));
		DropDownChoice choice = new DropDownChoice("ddc", new PropertyModel(this, "selected"), players);
		privateGame = new CheckBox("checkboxPrivate", Model.of(Boolean.TRUE)); 
		submitGame = new Button("submitgame") {
			/** UID for serialization. */
	        private static final long serialVersionUID = 1;

	        public void onSubmit() {
	        }
		};
		
		Form<?> form = new Form<>("create");
		form.add(gameName).add(passwordField).add(submitGame).add(choice).add(privateGame);
		add(form);
	}
}