package yaes.rcta.scenarios.FixedFormationRobotFollow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ParameterSweepHelper;
import yaes.framework.simulation.parametersweep.ScenarioDistinguisher;
import yaes.rcta.RctaResourceHelper;
import yaes.rcta.constRCTA;
import yaes.ui.text.TextUi;

/**
 * 
 * This scenario contains 1, 2, 3, or 4 bodyguards protecting. Bodyguards are
 * deploying fixed formation strategy. i.e. BEHIND_LEFT, BEHIND_RIGHT,
 * FRONT_LEFT, and FRONT_RIGHT respectively.
 * 
 * @author Taranjeet
 *
 */
public class Main implements constRCTA {
	private static final String MENU_SIMPLE_RUN = "RUN 1: Vizualize simple run";
	private static final String MENU_EXPERIMENT_RUN_1 = "RUN 2: Run experiements and Generate Graphs";
	private static final String MENU_EXPERIMENT_RUN_2 = "RUN 3: Run experiements and Generate Graphs differently";
	private static final String MENU_EXPERIMENT_RUN_3 = "RUN 3: Run experiements and Generate raw result files";

	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {

		List<String> menu = new ArrayList<String>();
		menu.add(MENU_SIMPLE_RUN);
		menu.add(MENU_EXPERIMENT_RUN_1);
		menu.add(MENU_EXPERIMENT_RUN_2);
		menu.add(MENU_EXPERIMENT_RUN_3);

		String result = TextUi.menu(menu, MENU_SIMPLE_RUN, "Choose the sumulation: ");

		switch (result) {
		case MENU_SIMPLE_RUN:
			doSimpleRun(true, false);
			break;
		case MENU_EXPERIMENT_RUN_1:
			doExperimentRunGraphs();
			break;
		case MENU_EXPERIMENT_RUN_2:
			doExperimentRunGraphs_2();
			break;
		case MENU_EXPERIMENT_RUN_3:
			doSimpleRun(false, true);
			break;

		}

	}

	private static void doExperimentRunGraphs_2() {
		RctaResourceHelper.flushFolders();
		SimulationInput sip = new SimulationInput();
		sip.setParameter(ExperimentMode.PROGRAMMED_RUN);
		sip.setStopTime(50);
		sip.setParameter(VisualDisplay.NO);
		sip.setParameter(WriteOutput.NO);
		ScenarioParameters.createSipMarket(sip);
		ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
		pack.setModel(sip);

		sip.setParameter(ROBOTS_COUNT, 2);
		// Simulate for 4 crowd configurations
		ParameterSweep sweepDiscrete = new ParameterSweep("CrowdTypes");
		ScenarioDistinguisher sd = null;

		sd = new ScenarioDistinguisher("Static Crowd");
		sd.setDistinguisher(ExperimentConfiguration.STATIC_CIVILIANS);
		sweepDiscrete.addDistinguisher(sd);

		sd = new ScenarioDistinguisher("With the flow Crowd");
		sd.setDistinguisher(ExperimentConfiguration.INDIRECTION_CIVILIAN_MOVEMENT);
		sweepDiscrete.addDistinguisher(sd);

		sd = new ScenarioDistinguisher("Against the flow Crowd");
		sd.setDistinguisher(ExperimentConfiguration.OUTDIRECTION_CIVILIAN_MOVEMENT);
		sweepDiscrete.addDistinguisher(sd);

		sd = new ScenarioDistinguisher("Mixed Crowd");
		sd.setDistinguisher(ExperimentConfiguration.RANDOM_CIVILIAN_MOVEMENT);
		sweepDiscrete.addDistinguisher(sd);

		pack.addParameterSweep(sweepDiscrete);

		// Simulation for different start and end location of VIP
		ParameterSweep sweepLocations = ParameterSweepHelper
				.generateParameterSweepInteger("Different Start and End point for VIP", Y_OFFSET, 0, 49, 5);
		pack.addParameterSweep(sweepLocations);

		pack.setVariableDescription(THREAT_VALUE, "Avg Threat Value");
		pack.setVariableDescription(NEUTRALIZE_THREAT_VALUE, "Avg Neutralize Threat Value");
		pack.initialize();
		pack.run();
		pack.generateGraph(THREAT_VALUE, null, "ThrtValue");
		pack.generateGraph(NEUTRALIZE_THREAT_VALUE, null, "NeuThrtValue");

	}

	// Line Graphs comparing number of bodyguard threat values in static
	// civilians crowd
	private static void doExperimentRunGraphs() {
		RctaResourceHelper.flushFolders();
		SimulationInput sip = new SimulationInput();
		sip.setParameter(ExperimentMode.PROGRAMMED_RUN);

		sip.setStopTime(50);
		sip.setParameter(VisualDisplay.NO);
		sip.setParameter(WriteOutput.NO);
		ScenarioParameters.createSipMarket(sip);
		ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
		pack.setModel(sip);

		sip.setParameter(ExperimentConfiguration.STATIC_CIVILIANS);

		ParameterSweep sweepDiscrete = new ParameterSweep("Number of Robots");
		ScenarioDistinguisher sd = null;
		sd = new ScenarioDistinguisher("2 bodyguards");
		sd.setDistinguisher(ROBOTS_COUNT, 2);
		sweepDiscrete.addDistinguisher(sd);

		sd = new ScenarioDistinguisher("3 bodyguards");
		sd.setDistinguisher(ROBOTS_COUNT, 3);
		sweepDiscrete.addDistinguisher(sd);

		sd = new ScenarioDistinguisher("4 bodyguards");
		sd.setDistinguisher(ROBOTS_COUNT, 4);
		sweepDiscrete.addDistinguisher(sd);
		pack.addParameterSweep(sweepDiscrete);

		// Simulation for different start and end location of VIP
		ParameterSweep sweepLocations = ParameterSweepHelper
				.generateParameterSweepInteger("Different Start and End point for VIP", Y_OFFSET, 0, 49, 5);
		pack.addParameterSweep(sweepLocations);

		pack.setVariableDescription(THREAT_VALUE, "Avg Threat Value");
		pack.setVariableDescription(NEUTRALIZE_THREAT_VALUE, "Avg Neutralize Threat Value");
		pack.initialize();
		pack.run();
		pack.generateGraph(THREAT_VALUE, null, "ThrtValue");
		pack.generateGraph(NEUTRALIZE_THREAT_VALUE, null, "NeuThrtValue");

	}

	private static void doSimpleRun(boolean visualize, boolean writeRawdata) throws FileNotFoundException,
			InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		RctaResourceHelper.flushFolders();
		SimulationInput sip = new SimulationInput();
		// Interactive Run
		sip.setParameter(ExperimentMode.PROGRAMMED_RUN);
		sip.setStopTime(50);
		// Show Visualizer
		sip.setParameter(VisualDisplay.NO);
		if (visualize) {
			sip.setParameter(VisualDisplay.YES);
		}
		// Write raw output files
		sip.setParameter(WriteOutput.NO);
		if (writeRawdata) {
			sip.setParameter(WriteOutput.YES);
		}

		// Create simulation scenario
		ScenarioParameters.createSipMarket(sip);
		ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);

		pack.setModel(sip);

		// =======================================================
		// Simulation Parameters
		// Run for 1, 2, 3, and 4 robot bodyguards
		ParameterSweep robotCount = ParameterSweepHelper.generateParameterSweepInteger("Robot Count", ROBOTS_COUNT, 3,
				4);
		pack.addParameterSweep(robotCount);

		// Run for 4 types of crowd configurations
		ParameterSweep sweepDiscrete = new ParameterSweep("CrowdTypes");
		ScenarioDistinguisher sd = null;
		sd = new ScenarioDistinguisher("Static Crowd");
		sd.setDistinguisher(ExperimentConfiguration.STATIC_CIVILIANS);
		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("With the flow Crowd");
		sd.setDistinguisher(ExperimentConfiguration.INDIRECTION_CIVILIAN_MOVEMENT);
		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("Against the flow Crowd");
		sd.setDistinguisher(ExperimentConfiguration.OUTDIRECTION_CIVILIAN_MOVEMENT);
		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("Mixed Crowd");
		sd.setDistinguisher(ExperimentConfiguration.RANDOM_CIVILIAN_MOVEMENT);
		sweepDiscrete.addDistinguisher(sd);
		pack.addParameterSweep(sweepDiscrete);

		// Simulation for different start and end location of VIP
		ParameterSweep sweepLocations = ParameterSweepHelper
				.generateParameterSweepInteger("Different Start and End point for VIP", Y_OFFSET, 0, 49, 5);
		pack.addParameterSweep(sweepLocations);
		pack.cleanUp();
		pack.initialize();
		pack.run();

	}
}
