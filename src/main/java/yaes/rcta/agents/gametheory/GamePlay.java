package yaes.rcta.agents.gametheory;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.rcta.agents.CostVector;
import yaes.rcta.agents.Human;
import yaes.rcta.agents.civilian.Civilian;
import yaes.rcta.agents.robot.Robot;
import yaes.world.physical.environment.EnvironmentModel;

public class GamePlay implements constRCTA {

    /**
     * Returns the mission cost for collaborating
     */
    public static double getMissionCostForCollaborating(
        AbstractPhysicalAgent agent, MicroConflict mc) {
        if (agent instanceof Civilian) {
            return 1.0;
        }
        if (agent instanceof Robot) {
            int countPlayedC = 0;
            for (SimpleEntry<Double, Game> entry : mc.getGames()) {
                Game game = entry.getValue();
                String move = game.getDoneMove(agent.getName());
                if (move.equals("C")) {
                    countPlayedC++;
                }
            }
            return countPlayedC + 1.0;
        }
        throw new Error("Cannot recognize the type of agent");
    }

    /**
     * Sets the social costs for the Game
     * 
     * @param context
     * @param agent1
     * @param agent2
     */
    private static Game setGameSocialCost(RctaContext context,
        AbstractPhysicalAgent agent1, AbstractPhysicalAgent agent2,
        boolean noCostCollaborate) {
        //
        // the social cost game
        //
        Game gameSocial = new Game();
        gameSocial.setPlayers(agent1.getName(), agent2.getName());
        gameSocial.setMoves("C", "D");
        setSocialCosts(gameSocial, context, agent1, agent2, noCostCollaborate);
        return gameSocial;
    }

    private static Game setGameMaxSocialCost(RctaContext context,
        MicroConflict mc, Game gameSocial, AbstractPhysicalAgent agent1,
        AbstractPhysicalAgent agent2) {
        Game gameMaxSocial = new Game();
        gameMaxSocial.setPlayers(agent1.getName(), agent2.getName());
        gameMaxSocial.setMoves("C", "D");
        setMaxSocialCost(gameMaxSocial, gameSocial, context, agent1, agent2,
                mc);
        return gameMaxSocial;
    }

    /**
     * Manages the playing of the game between the agents participating in the
     * microconflict.
     * 
     * FIXME: implement me, this is a playholder
     * 
     * @param context
     * @param mc
     */
    public static void play(RctaContext context, MicroConflict mc) {
        // Placeholder: let one of them advance
        int advance = context.getRandom().nextInt(2);
        mc.getParticipants().get(advance).action();
        // Create a game
        AbstractPhysicalAgent agent1 = mc.getParticipants().get(0);
        AbstractPhysicalAgent agent2 = mc.getParticipants().get(1);
        // set gameSocial costs with collaborateCost=true
        Game gameSocial = setGameSocialCost(context, agent1, agent2, true);
        Game gameMaxSocial =
                setGameMaxSocialCost(context, mc, gameSocial, agent1, agent2);
        //
        // the mission cost game
        //
        Game gameMission = new Game();
        gameMission.setPlayers(agent1.getName(), agent2.getName());
        gameMission.setMoves("C", "D");
        // The mission cost: linear for the Civilian, increasing for the robot
        double collabCost1 = getMissionCostForCollaborating(agent1, mc);
        double collabCost2 = getMissionCostForCollaborating(agent2, mc);
        setCollaborationCosts(gameMission, agent1.getName(), collabCost1,
                agent2.getName(), collabCost2);
        //
        // create the composite game - used to be the social game, now the
        // maxcost
        //
        // CompositeGame cgame = new CompositeGame(gameSocial);
        CompositeGame cgame = new CompositeGame(gameMaxSocial);
        cgame.addGame(gameMission, 1.0);

        // add the game to the microconflict
        mc.addGame(context.getWorld().getTime(), cgame);
        // Ok, this is a bit of hack to set the opponents C into the adaptive
        // stochastic,
        // will be removed later
        // FIXME:
        if (agent1.getGameStrategy() instanceof gsAdaptiveStochastic) {
            gsAdaptiveStochastic gsas =
                    (gsAdaptiveStochastic) agent1.getGameStrategy();
            gsas.setOpponent(agent2);
        }
        if (agent2.getGameStrategy() instanceof gsAdaptiveStochastic) {
            gsAdaptiveStochastic gsas =
                    (gsAdaptiveStochastic) agent2.getGameStrategy();
            gsas.setOpponent(agent1);
        }

        // now let them play
        String move1 = agent1.play(cgame);
        if (move1.equals("D")) {
            agent1.action();
        }
        String move2 = agent2.play(cgame);
        if (move2.equals("D")) {
            agent2.action();
        }

        // FIXME:
        // if both of them do "C", increment stuck count
        // add the evasive action here.
        // TODO: We can identify the game here

        // manage the incurred costs
        // manage the incurred costs
        CostVector cv1 = agent1.getCostVector();
        double social1 = gameSocial.getPayoff(agent1.getName(), move1, move2);
        double maxSocial1 =
                gameMaxSocial.getPayoff(agent1.getName(), move1, move2);
        mc.updateMaxSocialCost(agent1, social1);
        cv1.addSocial(social1);
        cv1.addMaxSocial(maxSocial1);
        cv1.addMission(gameMission.getPayoff(agent1.getName(), move1, move2));
        if (move1.equals("C")) {
            cv1.addTimeDelay(
                    gameMission.getPayoff(agent1.getName(), move1, move2));
        }
        CostVector cv2 = agent2.getCostVector();
        double social2 = gameSocial.getPayoff(agent2.getName(), move1, move2);
        double maxSocial2 =
                gameMaxSocial.getPayoff(agent2.getName(), move1, move2);
        mc.updateMaxSocialCost(agent2, social2);
        cv2.addSocial(social2);
        cv2.addMaxSocial(maxSocial2);
        cv2.addMission(gameMission.getPayoff(agent2.getName(), move1, move2));
        if (move2.equals("C")) {
            cv2.addTimeDelay(
                    gameMission.getPayoff(agent2.getName(), move1, move2));
        }
    }

    /**
     * Initializes a hawk-dove game
     * 
     * @param game
     */
    public static void setHawkDove(Game game, String player1, String player2) {
        game.setPayoff(player1, 0, "C", "C");
        game.setPayoff(player1, -1.0, "C", "D");
        game.setPayoff(player1, +1.0, "D", "C");
        game.setPayoff(player1, -10.0, "D", "D");
        game.setPayoff(player2, +1.0, "C", "C");
        game.setPayoff(player2, -1.0, "C", "D");
        game.setPayoff(player2, +1.0, "D", "C");
        game.setPayoff(player2, -10.0, "D", "D");
    }

    /**
     * Initializes a game in such a way that the player has collaborationCost
     * for playing C and 0 for playing D
     * 
     * @param game
     * @param player
     * @param timeCost
     */
    public static void setCollaborationCosts(Game game, String player1,
        double cCost1, String player2, double cCost2) {
        game.setPayoff(player1, cCost1, "C", "C");
        game.setPayoff(player1, cCost1, "C", "D"); // FIXME: It should be cCost1
        game.setPayoff(player1, 0.0, "D", "C");
        game.setPayoff(player1, 0.0, "D", "D");
        game.setPayoff(player2, cCost2, "C", "C");
        game.setPayoff(player2, 0, "C", "D");
        game.setPayoff(player2, cCost2, "D", "C");
        game.setPayoff(player2, 0, "D", "D");
    }

    public static void setMixedGameCollaborationCosts(Game game, String player1,
        double cCost1, String player2, double cCost2) {
        ArrayList<String> moves =
                new ArrayList<String>(mixedGameMoves.keySet());
        for (String mv1 : moves) {
            for (String mv2 : moves) {
                if (!mv1.equals("N"))
                    game.setPayoff(player1, cCost1, mv1, mv2);
                else if (mv1.equals("N"))
                    game.setPayoff(player1, 0.0, mv1, mv2);
            }
            // TODO: Check if payoff's are assigned properly
            for (String mv2 : moves) {
                if (!mv2.equals("N"))
                    game.setPayoff(player2, cCost2, mv1, mv2);
                else if (mv2.equals("N"))
                    game.setPayoff(player2, 0.0, mv1, mv2);
            }

        }
    }

    /**
     * Initializes a game where the payoffs are the values are the social costs
     * which exceed the already reached maximum
     * 
     * @param maxGame
     * @param socialGame
     * @param context
     * @param agent1
     * @param agent2
     */
    public static void setMaxSocialCost(Game maxGame, Game socialGame,
        RctaContext context, AbstractPhysicalAgent agent1,
        AbstractPhysicalAgent agent2, MicroConflict mc) {
        String player1 = agent1.getName();
        String player2 = agent2.getName();
        double socialMax1 = mc.getMaxSocialCost(agent1);
        double socialMax2 = mc.getMaxSocialCost(agent2);
        setPayoffMaxSocialCost(maxGame, socialGame, player1, socialMax1, "C",
                "C");
        setPayoffMaxSocialCost(maxGame, socialGame, player1, socialMax1, "C",
                "D");
        setPayoffMaxSocialCost(maxGame, socialGame, player1, socialMax1, "D",
                "C");
        setPayoffMaxSocialCost(maxGame, socialGame, player1, socialMax1, "D",
                "D");
        setPayoffMaxSocialCost(maxGame, socialGame, player2, socialMax2, "C",
                "C");
        setPayoffMaxSocialCost(maxGame, socialGame, player2, socialMax2, "C",
                "D");
        setPayoffMaxSocialCost(maxGame, socialGame, player2, socialMax2, "D",
                "C");
        setPayoffMaxSocialCost(maxGame, socialGame, player2, socialMax2, "D",
                "D");
    }

    /**
     * Sets the max value for a certain combination
     * 
     * @param maxGame
     * @param socialGame
     * @param player
     * @param socialMax
     * @param moves
     */
    private static void setPayoffMaxSocialCost(Game maxGame, Game socialGame,
        String player, double socialMax, String... moves) {
        double socialValue = socialGame.getPayoff(player, moves);
        double maxValue = Math.max(0, socialValue - socialMax);
        maxGame.setPayoff(player, maxValue, moves);
    }

    /**
     * Initializes a game such that the payoff are the social costs
     * 
     * @param noCostCollaborate
     *            = if set, collaborate does not inccur any social cost
     */
    public static void setSocialCosts(Game game, RctaContext context,
        AbstractPhysicalAgent agent1, AbstractPhysicalAgent agent2,
        boolean noCostCollaborate) {
        String player1 = agent1.getName();
        String player2 = agent2.getName();
        SimpleEntry<EnvironmentModel, EnvironmentModel> cost1 =
                context.getSingleAgentCosts(agent1);
        EnvironmentModel em1C = cost1.getKey();
        EnvironmentModel em1D = cost1.getValue();
        SimpleEntry<EnvironmentModel, EnvironmentModel> cost2 =
                context.getSingleAgentCosts(agent2);
        EnvironmentModel em2C = cost2.getKey(); // FIXME: Should it be cost1 or
                                                // cost2? it was cost1
        EnvironmentModel em2D = cost2.getValue(); // FIXME: Should it be cost1
                                                  // or cost2? it was cost1
        double value = 0;
        //
        // the values for agent1: taken from the costs generated by agent2
        //
        // agent1 and agent2 does not move (C, C)
        value = (Double) em2C.getPropertyAt(RctaContext.PROP_DENSITY,
                agent1.getLocation().getX(), agent1.getLocation().getY());
        if (noCostCollaborate) {
            game.setPayoff(player1, 0.0, "C", "C");
        } else {
            game.setPayoff(player1, value, "C", "C");
        }
        // agent1 does not move, agent2 moves (C, D)
        value = (Double) em2D.getPropertyAt(RctaContext.PROP_DENSITY,
                agent1.getLocation().getX(), agent1.getLocation().getY());
        if (noCostCollaborate) {
            game.setPayoff(player1, 0.0, "C", "D");
        } else {
            game.setPayoff(player1, value, "C", "D");
        }
        // agent1 moves, agent2 does not moves (D, C)
        value = (Double) em2C.getPropertyAt(RctaContext.PROP_DENSITY,
                agent1.getIntendedMove().getX(),
                agent1.getIntendedMove().getY());
        game.setPayoff(player1, value, "D", "C");
        // agent1 moves, agent2 also moves
        value = (Double) em2D.getPropertyAt(RctaContext.PROP_DENSITY,
                agent1.getIntendedMove().getX(),
                agent1.getIntendedMove().getY());
        game.setPayoff(player1, value, "D", "D");
        //
        // the values for agent2: taken from the cost generated by agent1
        //
        // agent1 and agent2 does not move
        value = (Double) em1C.getPropertyAt(RctaContext.PROP_DENSITY,
                agent2.getLocation().getX(), agent2.getLocation().getY());
        if (noCostCollaborate) {
            game.setPayoff(player2, 0.0, "C", "C");
        } else {
            game.setPayoff(player2, value, "C", "C");
        }
        // agent1 does not move, agent2 is
        value = (Double) em1C.getPropertyAt(RctaContext.PROP_DENSITY,
                agent2.getIntendedMove().getX(),
                agent2.getIntendedMove().getY());
        game.setPayoff(player2, value, "C", "D");
        // agent1 moves, agent2 does not
        value = (Double) em1D.getPropertyAt(RctaContext.PROP_DENSITY,
                agent2.getLocation().getX(), agent2.getLocation().getY());
        if (noCostCollaborate) {
            game.setPayoff(player2, 0.0, "D", "C");
        } else {
            game.setPayoff(player2, value, "D", "C");
        }
        // agent1 moves, agent2 also moves
        value = (Double) em1D.getPropertyAt(RctaContext.PROP_DENSITY,
                agent2.getIntendedMove().getX(),
                agent2.getIntendedMove().getY());
        game.setPayoff(player2, value, "D", "D");

    }

}
