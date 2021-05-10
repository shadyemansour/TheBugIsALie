package de.lmu.ifi.sosy.tbial;

//import de.lmu.ifi.sosy.tbial.db.Game;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.AjaxRequestTarget;

@AuthenticationRequired
public class Create extends BasePage {
	
	/** UID for serialization. */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LogManager.getLogger(Create.class);
	
	private final Button submitGame;
	private final Button cancelGame;
	private final TextField<String> gameName;
	private List players = Arrays.asList(new Integer[] {4, 5, 6, 7});
	public Integer selected = 4;
	private final CheckBox publicGame;
	private final Model<Boolean> checkboxState = Model.of(true);
	private final TextField<String> password;
	private final DropDownChoice choice;
	
	public Create() {
		add(new FeedbackPanel("feedback"));
		
		gameName = new TextField<>("name", new Model<>(""));
		gameName.setRequired(true);
		choice = new DropDownChoice("ddc", new PropertyModel(this, "selected"), players);
		
		password = new TextField<String>("password", Model.of("")){
		    @Override
		    public boolean isEnabled() {
		        return !checkboxState.getObject();
		    }
		};
		password.setOutputMarkupId(true);
		password.setRequired(true);
		publicGame = new AjaxCheckBox("publicGame", checkboxState) {
		    @Override
		    protected void onUpdate(AjaxRequestTarget target) {
		        target.add(password);
		    }
		};
		
		submitGame = new Button("submitgame") {
			/** UID for serialization. */
	        private static final long serialVersionUID = 1;

	        public void onSubmit() {
	        	String name = gameName.getModelObject();
	        	String host = ((TBIALSession)getSession()).getUser().getName();
	        	String pw = "";
	        	String gamestate = "new";
	            int numplayers = selected;
	    		String pub = publicGame.getModelObject().toString();
	    		if(!publicGame.getModelObject()) {
	    			pw = password.getModelObject();
	    		}
	    		//info("name: " + name + " pub: " + pub + " pw: " + pw + " host: " + host + " player: " + numplayers);
	        	//performCreation(name, host, pw, gamestate, numplayers);
	        }
		};
		cancelGame = new Button("cancelgame") {
			/** UID for serialization. */
	        private static final long serialVersionUID = 1;

	        public void onSubmit() {
	        	setResponsePage(getApplication().getHomePage());
	        }
		};
		cancelGame.setDefaultFormProcessing(false);

		
		Form<?> form = new Form<>("create");
		form.add(gameName).add(submitGame).add(choice).add(cancelGame).add(password, publicGame);
		add(form);
	}
	
	public void performCreation(String name, String host, String pw, String gamestate, int numplayers) {
//		Game game = getDatabase().createGame(name, host, pw, gamestate, numplayers);
//		if (game != null) {
//		    setResponsePage(getApplication().getHomePage());
//		    info("Registration successful! You are now logged in.");
//		    LOGGER.info("New game '" + name + "' created");
//		} else {
//			error("could not create game");
//			LOGGER.debug("New game '" + name + "' creation failed");
//		}
	}
}