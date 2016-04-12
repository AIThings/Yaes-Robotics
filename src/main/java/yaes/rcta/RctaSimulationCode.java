/**
 * 
 */
package yaes.rcta;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;

import yaes.framework.simulation.IContext;
import yaes.framework.simulation.ISimulationCode;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.rcta.constRCTA.ScenarioType;
import yaes.rcta.agents.gametheory.Game;
import yaes.rcta.agents.gametheory.MicroConflict;
import yaes.rcta.agents.robot.Robot;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.util.FileWritingUtil;

/**
 * @author Taranjeet
 *
 */
public class RctaSimulationCode implements ISimulationCode {

	/*
	 * postprocess occur after the simulation time-ticks ends (non-Javadoc)
	 * 
	 * @see
	 * yaes.framework.simulation.ISimulationCode#postprocess(yaes.framework.
	 * simulation.SimulationInput, yaes.framework.simulation.SimulationOutput,
	 * yaes.framework.simulation.IContext)
	 */
	@Override
	public void postprocess(SimulationInput sip, SimulationOutput sop, IContext theContext) {
		TextUi.println("Postprocess method call");
		RctaContext context = (RctaContext) theContext;
		Formatter fmt = new Formatter();
		fmt.add("archived micro-conflicts");
		fmt.indent();
		for (MicroConflict mc : context.getArchivedMicroConflicts()) {
			fmt.add(mc);
			for (SimpleEntry<Double, Game> entry : mc.getGames()) {
				fmt.is("Game at time", entry.getKey());
				Game game = entry.getValue();
				// game.toLatexMatrix22(fmt);
			}
		}

		postprocessRealistic(sip, sop, context, fmt);
	}

	/*
	 * Simulation setup occur before the simulation states. (non-Javadoc)
	 * 
	 * @see
	 * yaes.framework.simulation.ISimulationCode#setup(yaes.framework.simulation
	 * .SimulationInput, yaes.framework.simulation.SimulationOutput,
	 * yaes.framework.simulation.IContext)
	 */
	@Override
	public void setup(SimulationInput sip, SimulationOutput sop, IContext theContext) {
		RctaContext context = (RctaContext) theContext;
		context.initialize(sip, sop);

	}

	/*
	 * Update occur at every simulation time-tick (non-Javadoc)
	 * 
	 * @see yaes.framework.simulation.ISimulationCode#update(double,
	 * yaes.framework.simulation.SimulationInput,
	 * yaes.framework.simulation.SimulationOutput,
	 * yaes.framework.simulation.IContext)
	 */
	@Override
	public int update(double time, SimulationInput sip, SimulationOutput sop, IContext theContext) {
		TextUi.println("Simulating we are at time: " + time);
		RctaContext context = (RctaContext) theContext;
		if (context.getVisualizer() != null) {
			context.getVisualizer().update();
			// Use a thread sleep to slow down the simulation
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		if ((sip.getParameterEnum(ScenarioType.class) == ScenarioType.ROBOT_SCOUT)
				|| (sip.getParameterEnum(ScenarioType.class) == ScenarioType.SINGLE_CONFLICT)) {
			SensorNetworkWorld sensorWorld = (SensorNetworkWorld) context.getWorld();
			sensorWorld.messageFlush();
		}
		context.update(sip, sop, time);
		return 1;
	}

	private static void postprocessRealistic(SimulationInput sip, SimulationOutput sop, RctaContext theContext,
			Formatter fmt) {
        Robot robot = theContext.getRobots().get(0);
        // calculate how many games the robot played, how many moves each, what percentage moves 
        // was C.
        
        int countMicroConflicts = 0;
        int countActiveMicroConflicts = 0;
        int countGames = 0;
        int countPlayedC = 0;
        int countPlayedD = 0;
        for(MicroConflict mc: theContext.getArchivedMicroConflicts()) {
            if (!mc.getParticipants().contains(robot)) {
                continue;
            }
            countMicroConflicts++;
            if (mc.getGames().size() == 0) {
                continue;
            }
            countActiveMicroConflicts++;
            for(SimpleEntry<Double, Game> entry: mc.getGames()) {
                countGames++;
                Game game = entry.getValue();
                String move = game.getDoneMove(robot.getName());
                if (move.equals("C")) {
                    countPlayedC++;
                }
                if (move.equals("D")) {
                    countPlayedD++;
                }
            }
        }
		sop.update(constRCTA.METRICS_SOCIAL_COST, robot.getCostVector().getSocial());
		sop.update(constRCTA.METRICS_MISSION_COST, robot.getCostVector().getMission());
		sop.update(constRCTA.METRICS_MAX_SOCIAL_COST, robot.getCostVector().getMaxSocial());
		//sop.update(METRICS_MICRO_CONFLICTS, countMicroConflicts);
		// sop.update(METRICS_ACTIVE_MICRO_CONFLICTS,
		// countActiveMicroConflicts);
		// sop.update(METRICS_GAMES_PLAYED, countGames);
		// sop.update(METRICS_PLAYED_C, countPlayedC);
		// sop.update(METRICS_PLAYED_D, countPlayedD);
		// add these to fmt
		fmt.is("Micro-conflicts for robot:", countMicroConflicts);
		fmt.is("Micro-conflicts with games played", countActiveMicroConflicts);
		fmt.is("Games played by the robot:", countGames);
		fmt.is("Robot played C:", countPlayedC);
		fmt.is("Robot played D:", countPlayedD);
		//TextUi.println(fmt);
		FileWritingUtil.writeToTextFile(new File("output/log.txt"), fmt.toString());
	}
}
