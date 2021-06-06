package de.lmu.ifi.sosy.tbial;


import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;


public abstract class ConfirmCancelPanel extends Panel {

	/**
	 * UID for serialization.
	 */
	private static final long serialVersionUID = 1L;


	public ConfirmCancelPanel(String id, String message) {
		super(id);

		Form<?> yesNoForm = new Form<>("yesNoForm");

		MultiLineLabel messageLabel = new MultiLineLabel("message", message);
		yesNoForm.add(messageLabel);


		yesNoForm.add(new Link<>("confirm") {


			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				onConfirm();
			}
		});


		yesNoForm.add(new Link<>("cancel") {


			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				onCancel();
			}
		});


		add(yesNoForm);


	}

	protected abstract void onCancel();

	protected abstract void onConfirm();


}
