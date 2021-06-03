package de.lmu.ifi.sosy.tbial;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.networking.BugWebSocketResource;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.BaseWebSocketBehavior;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.TextMessage;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;

/**
 * Basic page with style template as well as access to {@link TBIALSession} and {@link Database}.
 *
 * @author Andreas Schroeder, SWEP 2013 Team.
 */
public abstract class BasePage extends WebPage {

  /**
   * UID for serialization.
   */
  private static final long serialVersionUID = 1L;

  private Link<Void> link;

  private Label users;

  protected Database getDatabase() {
    return TBIALApplication.getDatabase();
  }

  protected TBIALApplication getTbialApplication() {
    return (TBIALApplication) super.getApplication();
  }

  @Override
  public TBIALSession getSession() {
    return (TBIALSession) super.getSession();
  }

  public BasePage() {
    add(new BaseWebSocketBehavior(BugWebSocketResource.NAME));


    link =

            new Link<Void>("signout") {

              /** UID for serialization. */
              private static final long serialVersionUID = 1L;

              @Override
              public void onClick() {
                Session session = super.getSession();
                if (session instanceof AuthenticatedWebSession) {
                  ((AuthenticatedWebSession) session).signOut();
                }
                session.invalidate();
              }
            };
    users = new Label("users", new PropertyModel<>(this, "usersString"));
    users.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)));
    add(link);
    add(users);
    Model<String> usernameMDL = Model.of("");
    Label username = new Label("username", usernameMDL);
    username.setOutputMarkupId(true);
    add(username);

    if (!getSession().isSignedIn()) {
      link.setVisible(false);
      link.setEnabled(false);
      username.setVisible(false);
    } else {

      usernameMDL.setObject("Logged in as " + ((TBIALSession) getSession()).getUser().getName());
      username.setVisible(true);

    }


  }

  private void getPayload(String msg) {
  }

  public String getUsersString() {
    int users = getTbialApplication().getUsersLoggedInCount();
    return users == 1 ? "1 player online." : users + " players online.";
  }
}


