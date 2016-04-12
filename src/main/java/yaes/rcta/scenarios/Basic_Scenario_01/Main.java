package yaes.rcta.scenarios.Basic_Scenario_01;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ParameterSweepHelper;
import yaes.rcta.RctaResourceHelper;
import yaes.rcta.constRCTA;
import yaes.ui.text.TextUi;

/**
 * Simple scenario for initiating single agent performing
 * <ol>
 * <li>D* lite path planing</li>
 * <li>Obstacle avoidance</li>
 * </ol>
 * 
 * @author Taranjeet Singh Bhatia
 *
 */
public class Main implements constRCTA {
	private static final String MENU_SIMPLE_RUN_1 = "RUN 1: Visualize Obstacle Avoidance";
	private static final String MENU_SIMPLE_RUN_2 = "RUN 2: Visualize D*lite path planning";

	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {
		List<String> menu = new ArrayList<String>();
		menu.add(MENU_SIMPLE_RUN_1);
		menu.add(MENU_SIMPLE_RUN_2);
		String result = TextUi.menu(menu, MENU_SIMPLE_RUN_1, "Choose the sumulation: ");

		switch (result) {
		case MENU_SIMPLE_RUN_1:
			doSimpleRun(constRCTA.ScenarioType.OBSTACLE_AVOIDANCE);
			break;
		case MENU_SIMPLE_RUN_2:
			doSimpleRun(constRCTA.ScenarioType.DSTAR_LITE_SIMPLE);
			break;
		default:
			break;
		}

	}

	private static void doSimpleRun(ScenarioType scenarioType) throws FileNotFoundException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException {
		RctaResourceHelper.flushFolders();
		SimulationInput sip = new SimulationInput();
		// Interactive Run
		sip.setParameter(ExperimentMode.PROGRAMMED_RUN);
		sip.setStopTime(50);
		// Show Visualizer
		sip.setParameter(VisualDisplay.NO);
		sip.setParameter(VisualDisplay.YES);

		// Write raw output files
		sip.setParameter(WriteOutput.NO);
		// Set scenarioType
		sip.setParameter(scenarioType);
		// Create simulation scenario
		ScenarioParameters.createSipMarket(sip);
		ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);

		pack.setModel(sip);
		pack.cleanUp();
		// Simulation for different start and end location of VIP
		ParameterSweep sweepLocations = ParameterSweepHelper
				.generateParameterSweepInteger("Different Start and End point for VIP", Y_OFFSET, 0, 49, 5);
		pack.addParameterSweep(sweepLocations);

		pack.initialize();
		pack.run();
	}
}
