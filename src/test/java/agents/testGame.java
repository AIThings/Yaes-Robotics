package agents;

import org.junit.Ignore;
import org.junit.Test;

import yaes.rcta.constRCTA;
import yaes.rcta.agents.gametheory.Game;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;

public class testGame implements constRCTA{

    /**
     * Tests the creation, internal structure and printing of the game
     */
    @Ignore
    public void test() {
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

        game.setDoneMove("A", "C");
        TextUi.println(game.toString());
        
        
        Formatter fmt = new Formatter();
        fmt.is("A(C,C)", game.getPayoff("A", "C", "C"));
        fmt.is("B(C,C)", game.getPayoff("B", "C", "C"));
        fmt.is("A(C,D)", game.getPayoff("A", "C", "D"));
        fmt.is("B(C,D)", game.getPayoff("B", "C", "D"));
        fmt.is("A(D,C)", game.getPayoff("A", "D", "C"));
        fmt.is("B(D,C)", game.getPayoff("B", "D", "C"));
        fmt.is("A(D,D)", game.getPayoff("A", "D", "D"));
        fmt.is("B(D,D)", game.getPayoff("B", "D", "D"));
        TextUi.println(fmt);
    }
    
    /**
     * The test case for n x m games
     */
    @Test
    public void testNxM() {
    	TextUi.println(mixedGameMoves.keySet().toArray(new String[0]).toString());
    	String[] moves =   mixedGameMoves.keySet().toArray(new String[mixedGameMoves.keySet().size()]);
        Game game = new Game();
        game.setPlayers("A", "B");
        game.setMoves(moves);
        
        int cost = 0;
		for(String mv1: moves){
			for(String mv2: moves)
				game.setPayoff("A", cost++, mv1, mv2);			
			for(String mv2: moves)
				game.setPayoff("B", cost++, mv1, mv2);				
		}	
		

        game.setDoneMove("A", "N");
        TextUi.println(game.toString());
        
        
        Formatter fmt = new Formatter();
        fmt.is("A(N,N)", game.getPayoff("A", "N", "N"));
        fmt.is("B(N,W)", game.getPayoff("B", "N", "W"));
        fmt.is("A(W,E)", game.getPayoff("A", "W", "E"));
        fmt.is("B(W,N)", game.getPayoff("B", "W", "N"));
        TextUi.println(fmt);
    }
}
