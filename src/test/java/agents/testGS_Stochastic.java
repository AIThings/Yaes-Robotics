package agents;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import yaes.rcta.agents.gametheory.Game;
import yaes.rcta.agents.gametheory.gsStochastic;
import yaes.rcta.agents.gametheory.iGameStrategy;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;

public class testGS_Stochastic {
    String playerA = "A";
    String playerB = "B";
    Formatter fmt;

    public Game makeGame() {
        Game game = new Game();
        game.setPlayers(this.playerA, this.playerB);
        game.setMoves("C", "D");
        game.setPayoff(this.playerA, 1.0, "C", "C");
        game.setPayoff(this.playerB, 2.0, "C", "C");
        game.setPayoff(this.playerA, 3.0, "C", "D");
        game.setPayoff(this.playerB, 4.0, "C", "D");
        game.setPayoff(this.playerA, 11.0, "D", "C");
        game.setPayoff(this.playerB, 12.0, "D", "C");
        game.setPayoff(this.playerA, 13.0, "D", "D");
        game.setPayoff(this.playerB, 14.0, "D", "D");
        return game;
    }

    @Test
    public void testMove() {
        Game game = makeGame();
        Random random = new Random();
        iGameStrategy strategy;

        for (double assumptionC = 0.0; assumptionC <= 1.0; assumptionC =
                assumptionC + 0.6) {
            this.fmt = new Formatter();
            this.fmt.add("The assumptionC for this game for playerA is:"
                    + assumptionC);
            strategy = new gsStochastic(random, assumptionC);
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < 20; i++)
                buf.append(strategy.move(this.playerA, game));
            this.fmt.addIndented("The moves used in the game are:" + buf);
            this.fmt.add("");
            TextUi.print(this.fmt.toString());
        }

    }

    @Test
    public void randomPlayInitiate() {
        Random random = new Random();
        double r ;
        double assumptionC = 0;
        int[] arrayCount = new int[4];
        for (int i = 0; i <= 100; i++) {
            r = new Random().nextDouble();
            if (r < 0.1) {
                assumptionC = 0;
                arrayCount[0]++;
            } else if (r < 0.2) {
                assumptionC = 1.0;
                arrayCount[1]++;

            } else if (r < 0.7) {
                assumptionC = 0.75;
                arrayCount[2]++;

            } else {
                assumptionC = 0.25;
                arrayCount[3]++;
            }
        }
        
        TextUi.print(Arrays.toString(arrayCount));
    }
}
