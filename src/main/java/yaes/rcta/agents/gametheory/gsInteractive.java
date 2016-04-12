package yaes.rcta.agents.gametheory;

import java.util.ArrayList;
import java.util.List;

import yaes.ui.text.TextUi;

/**
 * An interactive strategy for moving
 * 
 * @author Lotzi Boloni
 * 
 */
public class gsInteractive implements iGameStrategy {

    private static final long serialVersionUID = 2269341024601772925L;

    private boolean printGame = true;

    @Override
    public String move(String player, Game game) {
        if (printGame) {
            TextUi.println(game.toString());
        }
        List<String> choices = new ArrayList<>();
        choices.addAll(game.getMoves());
        String choice =
                TextUi.menu(choices, choices.get(0), "Move by player: "
                        + player);
        return choice;
    }
}
