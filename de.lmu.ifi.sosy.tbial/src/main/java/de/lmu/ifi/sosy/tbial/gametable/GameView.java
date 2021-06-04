package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.db.Database;
import de.lmu.ifi.sosy.tbial.*;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;

public abstract class GameView extends WebPage {
    /** UID for serialization. */
    private static final long serialVersionUID = 1L;

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

}