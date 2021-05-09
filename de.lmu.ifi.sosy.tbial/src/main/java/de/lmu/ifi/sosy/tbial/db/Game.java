package de.lmu.ifi.sosy.tbial.db;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Game {

        private List<User> players = new  ArrayList<User>();

     //   private  ArrayList<Card> charakterCards = new  ArrayList<Card>(); TODO later (US37)

        private List<Card> stack = new  ArrayList<Card>(); // all action, ability, and stumbling blocks cards
        private List<Card> playableStack = new  ArrayList<Card>(); // only bugs, exuses, solutions playable

    //    private  List<Card> heap = new  ArrayList<Card>(); TODO later

        public Game(List<User> players, List<Card> stack, List<Card> playableStack ){

            this.players=players;
            this.stack=stack;
            this.playableStack=playableStack;
}

    public void startGame() {

        setStack();
        Collections.shuffle(this.stack);
        setPlayableStack(this.stack);
        Collections.shuffle(this.playableStack);

        for (User player: this.players) {

            player.setPrestige(0);
            player.setHealth(4);
            /* TODO health +1 for MANAGER */

            for (int i = 0; i < player.getHealth(); i++) {
                player.getHand().add(this.playableStack.get(i));

            }

            for (int i = 0; i < player.getHealth(); i++) {
                this.playableStack.remove(i);

            }
        }



        }
        
    public void setStack() {

        for (int i=1;i<=4;i++) {

            this.stack.add(new Card("black","Action Card", "BUG: Nullpointer!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card ", "BUG: Off By One!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card", "BUG: Class Not Found!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card", "BUG: System Hangs!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card", "BUG: Core Damp!",
                    "-1 mental health", true));
            this.stack.add(new Card("black","Action Card", "BUG: Customer Hates UI!",
                    "-1 mental health", true));

            this.stack.add(new Card("black","Action Card", "EXUSE: Works For Me!",
                    "Fends off bug report", true));
            this.stack.add(new Card("black","Action Card", "EXUSE: It's a Feature!",
                    "Fends off bug report", true));
            this.stack.add(new Card("black","Action Card", "EXUSE: I'm not Responsible!",
                    "Fends off bug report", true));

            this.stack.add(new Card("black","Action Card", "I refactored your code. Away",
                    "Ignors prestige. Drop one card", false));
            this.stack.add(new Card("black","Action Card", "Pwnd.",
                    "Cede one card. Same or lower prestige required", false));




        }

        for (int i=1;i<=3;i++){

            this.stack.add(new Card("black","Action Card", "System Integration",
                    "My code is better than yours!", false));
            this.stack.add(new Card("blue","Ability Card", "Microsoft (Previous Job)",
                    "1 prestige", false));
            this.stack.add(new Card("magenta","Stumbling Block", "Off-The-Job Training",
                    "Not for manager. Cannot play this turn. .25 chance to deflect", false));


        }
        for (int i=1;i<=2;i++){

            this.stack.add(new Card("black","Action Card", "SOLUTION: Coffee",
                    "+1 mental health", true));
            this.stack.add(new Card("black","Action Card", "SOLUTION: Code+Fix Session",
                    "+1 mental health", true));
            this.stack.add(new Card("black","Action Card", "SOLUTION: I know regular expressions",
                    "+1 mental health", true));

            this.stack.add(new Card("black","Action Card", "Standup Meeting",
                    "The cards are on the table", false));
            this.stack.add(new Card("black","Action Card", "Personal Coffee Machine",
                    "Takes 2 cards", false));
            this.stack.add(new Card("black","Action Card", "Boring Meeting",
                    "Play bug or lose mental health", false));

            this.stack.add(new Card("blue","Ability Card", "Bug Delegation",
                    "Delegates bug report. .25 chance to work", false));
            this.stack.add(new Card("blue","Ability Card", "Wears Tie at Work",
                    "Is seen with +1 prestige by everybody", false));
            this.stack.add(new Card("blue","Ability Card", "Accenture -previous job-",
                    "May report several bugs in one round", false));
            this.stack.add(new Card("blue","Ability Card", "Google -previous job-",
                    "2 prestige", false));

        }

        this.stack.add(new Card("black","Action Card", "BUG ",
                "-1 mental health", true));


        this.stack.add(new Card("black","Action Card", "Red Bull Dispenser",
                "Take 3 Cards", false));

        this.stack.add(new Card("black","Action Card", "Heisenbug",
                "Bugs for everybody!", false));

        this.stack.add(new Card("black","Action Card", "LAN Party",
                "Mental health for everybody!", false));

        this.stack.add(new Card("blue","Ability Card", "Wears Sunglasses at Work",
                "Sees everybody with -1 prestige", false));

        this.stack.add(new Card("blue","Ability Card", "NASA -previous job-",
                "3 prestige", false));

        this.stack.add(new Card("magenta","Stumbling Block", "Fortran Maintenance BOOM",
                "Only playable on self. Takes 3 health points. .85 chance to deflect to next developer", false));


    }

    public void setPlayableStack(List<Card> stack){


        for (Card card : this.stack){
            if (card.isPlayable())
                this.playableStack.add(card);

        }


    }

}
