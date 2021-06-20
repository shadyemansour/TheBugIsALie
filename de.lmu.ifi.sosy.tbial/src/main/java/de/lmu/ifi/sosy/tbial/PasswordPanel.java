package de.lmu.ifi.sosy.tbial;


import de.lmu.ifi.sosy.tbial.db.Game;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;


public abstract class PasswordPanel extends Panel {

	/**
	 * UID for serialization.
	 */
	private static final long serialVersionUID = 1L;

	private final TextField<String> password;
	private final Button confirm;
	private final Button cancel;

	public PasswordPanel(String id, String message, Game game) {
		super(id);


		MultiLineLabel messageLabel = new MultiLineLabel("message", message);

		password = new PasswordTextField("password2", Model.of(""));
		password.setOutputMarkupId(true);


		confirm = new Button("confirm") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				String z = password.getModelObject();
				if (z.equals(game.getPassword())) {
					onConfirm();
				} else {
					onCancel();
				}
			}
		};

		cancel = new Button("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				onCancel();
			}
		};
		cancel.setDefaultFormProcessing(false);
		Form<?> passwordForm = new Form<>("passwordForm");
		passwordForm.add(messageLabel).add(password).add(cancel).add(confirm);
		add(passwordForm);


	}

	protected abstract void onCancel();

	protected abstract void onConfirm();


}
