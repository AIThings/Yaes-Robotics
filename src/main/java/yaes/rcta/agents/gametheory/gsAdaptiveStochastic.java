package yaes.rcta.agents.gametheory;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.Random;

import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;

public class gsAdaptiveStochastic extends gsStochastic {

    public gsAdaptiveStochastic(Random random) {
        super(random, 0);
    }

    private static final long serialVersionUID = 2884448458153234578L;
    private double opponentAssumptionC; 
    
    
    /**
     * Calculates the values based on the assumptions
     * 
     *  FIXME: implement me, currently falls back to the old one
     */
    @Override
    public SimpleEntry<Double,Map<String, Double>> calculateValues(Game game, int myId, int opponentId) {
        assumptionC = estimateOpponentMoves(game, myId, opponentId);
        // trying to pull it to a fixed location
        //if (assumptionC > 0.7) {
        //    assumptionC = 1.0;
        //} else {
        //    assumptionC = 0.0;
        //}
        // Trying out weird stuff
        assumptionC = 1.0 - assumptionC;
        TextUi.println("estimated opponent to be: " + Formatter.fmt(assumptionC));
        return super.calculateValues(game, myId, opponentId);
    }

    /**
     * Calculates the correct value of the assumptionC by calculating the opponent's move
     * @param game
     * @param myId
     * @param opponentId
     * @return
     */
    private double estimateOpponentMoves(Game game, int myId, int opponentId) {
        Random localRandom = new Random(0);
        gsStochastic gm = new gsStochastic(localRandom, opponentAssumptionC);
        double cCount = 0;
        double iteration = 100;
        String opponentName = game.getPlayers().get(opponentId);
        for(int i = 0; i < iteration; i++) {
            String m = gm.move(opponentName, game);
            if (m.equals("C")) {
                cCount++;
            }
        }        
        return cCount / iteration;
    }


    /**
     * @param opponentAssumptionC the opponentAssumptionC to set
     */
    public void setOpponentAssumptionC(double opponentAssumptionC) {
        this.opponentAssumptionC = opponentAssumptionC;
    }


    /**
     * Learns from the opponent
     * @param agent2
     */
    public void setOpponent(AbstractPhysicalAgent agent) {
        if (agent.getGameStrategy() instanceof gsStochastic) {
            this.opponentAssumptionC = ((gsStochastic) agent.getGameStrategy()).getAssumptionC();
        }
    }
    
    
    
}
