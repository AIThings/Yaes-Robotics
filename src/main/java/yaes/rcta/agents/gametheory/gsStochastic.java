package yaes.rcta.agents.gametheory;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import yaes.ui.format.Formatter;

/**
 * A very simple stochastic strategy implementing the game playing strategy. It
 * does not perform opponent modeling and it does not perform learning.
 * 
 * Assumes that the opponent picks randomly its moves, and then takes the
 * average
 * 
 * @author Lotzi Boloni
 * 
 */
public class gsStochastic implements iGameStrategy {

    private static final long serialVersionUID = -2223944979642006220L;
    private Random random;
    /**
     * The probability that the opponent will play C
     */
    protected double assumptionC;

    public gsStochastic(Random random, double assumptionC) {
        this.random = random;
        this.assumptionC = assumptionC;
    }

    @Override
	public String move(String player, Game game) {
        String move = null;
        // identify my and the opponents ids
        int myId = -1;
        int opponentId = -1;
        for (int i = 0; i != game.getPlayers().size(); i++) {
            String pl = game.getPlayers().get(i);
            if (pl.equals(player)) {
                myId = i;
            } else {
                opponentId = i;
            }
        }

        
        // now create a move based on the random choice
        SimpleEntry<Double, Map<String, Double>> entry =
                calculateValues(game, myId, opponentId);
        Map<String, Double> values = entry.getValue();
        double totalValue = entry.getKey();

        double randomValue = totalValue * this.random.nextDouble();
        double accumulate = 0;
        for (String mv : game.getMoves()) {
            accumulate += values.get(mv);
            if (accumulate >= randomValue) {
                move = mv;
                break;
            }
        }
        // because we are calculating costs, we need to flip
        if (move == "C") { //$NON-NLS-1$
            return "D"; //$NON-NLS-1$
        } else {
			return "C"; //$NON-NLS-1$
        }
    }

    /**
     * Calculates the values based on the assumptions
     */
    public SimpleEntry<Double, Map<String, Double>> calculateValues(Game game,
        int myId, int opponentId) {

        // calculate the cost of moves (sum of possible opponent values)
        Map<String, Double> values = new HashMap<>();
        double totalValue = 0;
        for (String myMove : game.getMoves()) {
            double value = 0;
            for (String opponentMove : game.getMoves()) {
                String currentMoves[] = new String[2];
                currentMoves[myId] = myMove;
                currentMoves[opponentId] = opponentMove;
                double localvalue = game.getPayoff(game.getPlayers().get(myId),
                        currentMoves);
                switch (myMove) {
                case "C": //$NON-NLS-1$
                    value += this.assumptionC * localvalue;
                    break;
                case "D": //$NON-NLS-1$
                    value += (1 - this.assumptionC) * localvalue;
                    break;
                default:
                    throw new Error(
"This strategy can only be used with moves C and D, was: " //$NON-NLS-1$
                                    + myMove);
                }
            }
            values.put(myMove, value);
            totalValue += value;
        }
        return new SimpleEntry<>(totalValue, values);
    }

    /**
     * @return the assumptionC
     */
    public double getAssumptionC() {
        return this.assumptionC;
    }

    /**
     * 
     */
    public void setAssumptionC(double assumptionC) {
        this.assumptionC = assumptionC;
    }

    @Override
    public String toString() {
        return "gsStochastic [assumptionC=" + Formatter.fmt(this.assumptionC) + "]";  //$NON-NLS-1$//$NON-NLS-2$
    }

}
