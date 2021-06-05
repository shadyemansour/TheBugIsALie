package de.lmu.ifi.sosy.tbial.gametable;

import de.lmu.ifi.sosy.tbial.*;

import static de.lmu.ifi.sosy.tbial.TestUtil.hasNameAndPassword;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;

public class GameBoardTest extends PageTestBase {
    @Before
    public void setUp() {
        setupApplication();
        database.register("testuser", "testpassword");
    }

    @Test
    public void renderFourBoard() {
        loginUser();

        tester.clickLink("four");
        
        tester.assertRenderedPage(FourBoard.class);
        tester.assertLabel("p1", "player1-name");
        tester.assertLabel("p2", "player2-name");
        tester.assertLabel("p3", "player3-name");
        tester.assertLabel("p4", "player4-name");
    }

    @Test
    public void renderFiveBoard() {
        loginUser();

        tester.clickLink("five");

        tester.assertRenderedPage(FiveBoard.class);
        tester.assertLabel("p1", "player1-name");
        tester.assertLabel("p2", "player2-name");
        tester.assertLabel("p3", "player3-name");
        tester.assertLabel("p4", "player4-name");
        tester.assertLabel("p5", "player5-name");
    }

    @Test
    public void renderSixBoard() {
        loginUser();

        tester.clickLink("six");
        
        tester.assertRenderedPage(SixBoard.class);
        tester.assertLabel("p1", "player1-name");
        tester.assertLabel("p2", "player2-name");
        tester.assertLabel("p3", "player3-name");
        tester.assertLabel("p4", "player4-name");
        tester.assertLabel("p5", "player5-name");
        tester.assertLabel("p6", "player6-name");
    }

    @Test
    public void renderSevenBoard() {
        loginUser();

        tester.clickLink("seven");
        
        tester.assertRenderedPage(SevenBoard.class);
        tester.assertLabel("p1", "player1-name");
        tester.assertLabel("p2", "player2-name");
        tester.assertLabel("p3", "player3-name");
        tester.assertLabel("p4", "player4-name");
        tester.assertLabel("p5", "player5-name");
        tester.assertLabel("p6", "player6-name");
        tester.assertLabel("p7", "player7-name");
    }

    public void loginUser() {
        tester.startPage(Login.class);
        tester.assertRenderedPage(Login.class);
        FormTester form = tester.newFormTester("login");
        form.setValue("name", "testuser");
        form.setValue("password", "testpassword");
        form.submit("loginbutton");

        tester.assertRenderedPage(Lobby.class);
    }
}