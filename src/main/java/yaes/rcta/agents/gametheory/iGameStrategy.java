package yaes.rcta.agents.gametheory;

import java.io.Serializable;

/**
 * An interface to be implemented by all the game playing strategies
 * 
 * @author Lotzi Boloni
 *
 */
public interface iGameStrategy extends Serializable {

    /**
     * For a given game, taking the role of the given player
     * returns the move
     *  
     * @param player
     * @param game
     * @return
     */
    String move(String player, Game game);
    
}
