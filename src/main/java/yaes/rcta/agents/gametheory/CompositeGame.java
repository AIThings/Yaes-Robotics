package yaes.rcta.agents.gametheory;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;


/**
 * A version of game which is created by having a primary game (which provides the
 * players and moves, then a number of secondary games which add to the payoffs)
 * 
 * Used to compose different types of costs.
 * 
 * @author Lotzi Boloni
 *
 */
public class CompositeGame extends Game {

    private static final long serialVersionUID = 5202210269663439891L;
    private Game primary;
    private List<SimpleEntry<Game, Double>> addons = new ArrayList<>();
    
    public CompositeGame(Game primary) {
        this.primary = primary;
    }
    
    /**
     * Adds a game. It must have at least the same players and moves 
     * as the primary game. 
     * 
     * @param game
     * @param multiplier
     */
    public void addGame(Game game, double multiplier) {
       SimpleEntry<Game, Double> entry = new SimpleEntry<>(game, multiplier);
       addons.add(entry);
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#setPlayers(java.lang.String[])
     */
    @Override
    public void setPlayers(String... players) {
        throw new Error("Can not set the players in CompositeGame");
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#setMoves(java.lang.String[])
     */
    @Override
    public void setMoves(String... moves) {
        throw new Error("Can not set the players in CompositeGame");
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#setPayoff(java.lang.String, double, java.lang.String[])
     */
    @Override
    public void setPayoff(String beneficiary, double value, String... moves) {
        throw new Error("Can not set the players in CompositeGame");
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#getPayoff(java.lang.String, java.lang.String[])
     */
    @Override
    public double getPayoff(String beneficiary, String... moves) {
        double payoff = primary.getPayoff(beneficiary, moves);
        for(SimpleEntry<Game, Double> entry: addons) {
            Game addonGame = entry.getKey();
            double addonMultiplier = entry.getValue();
            double addonPayoff = addonMultiplier * addonGame.getPayoff(beneficiary, moves);
            payoff = payoff + addonPayoff;
        }
        return payoff;
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#setDoneMove(java.lang.String, java.lang.String)
     */
    @Override
    public void setDoneMove(String player, String move) {
        primary.setDoneMove(player, move);
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#getDoneMove(java.lang.String)
     */
    @Override
    public String getDoneMove(String player) {
        return primary.getDoneMove(player);
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#getMoves()
     */
    @Override
    public List<String> getMoves() {
        return primary.getMoves();
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#getPlayers()
     */
    @Override
    public List<String> getPlayers() {
        return primary.getPlayers();
    }

    /* (non-Javadoc)
     * @see yaes.rcta.agents.gametheory.Game#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }

    
    
}
