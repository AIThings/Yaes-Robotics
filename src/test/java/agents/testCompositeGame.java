package agents;

import org.junit.Test;

import yaes.rcta.agents.gametheory.CompositeGame;
import yaes.rcta.agents.gametheory.Game;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;

public class testCompositeGame {

    /**
     * Tests the creation, internal structure and printing of the game
     */
    @Test
    public void test() {
        Formatter fmt = new Formatter();
        fmt.add("Game1:");
        Game game = new Game();
        game.setPlayers("A", "B");
        game.setMoves("C", "D");
        game.setPayoff("A", 1.0, "C", "C");
        game.setPayoff("B", 2.0, "C", "C");
        game.setPayoff("A", 3.0, "C", "D");
        game.setPayoff("B", 4.0, "C", "D");
        game.setPayoff("A", 11.0, "D", "C");
        game.setPayoff("B", 12.0, "D", "C");
        game.setPayoff("A", 13.0, "D", "D");
        game.setPayoff("B", 14.0, "D", "D");
        fmt.addIndented(game.toString());
        
        // the second game: add extra cost for each C moves
        Game game2 = new Game();
        game2.setPlayers("A", "B");
        game2.setMoves("C", "D");
        game2.setPayoff("A", 1.0, "C", "C");
        game2.setPayoff("B", 1.0, "C", "C");
        game2.setPayoff("A", 1.0, "C", "D");
        game2.setPayoff("B", 0.0, "C", "D");
        game2.setPayoff("A", 0.0, "D", "C");
        game2.setPayoff("B", 1.0, "D", "C");
        game2.setPayoff("A", 0.0, "D", "D");
        game2.setPayoff("B", 0.0, "D", "D");
        fmt.add("Game2:");
        fmt.addIndented(game2.toString());
        
        CompositeGame cg = new CompositeGame(game);
        cg.addGame(game2, 1.0);
        fmt.add("Composite game:");
        fmt.addIndented(cg.toString());
        TextUi.println(fmt);
    }
    
}
