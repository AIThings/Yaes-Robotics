package yaes.rcta.scenarios.Basic_Scenario_02;

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
 * 
 * This scenario contains 1, 2, 3, or 4 bodyguards protecting VIP. Bodyguards are
 * deploying TVR and QLB strategies.
 * 
 * @author Taranjeet
 *
 */
public class Main implements constRCTA {
	private static final String MENU_SIMPLE_RUN = "RUN 1: Vizualize simple run";
	private static final String MENU_EXPERIMENT_RUN = "RUN 3: Run experiements and Generate raw result files";

	public static void main(String[] args) throws FileNotFoundException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {

		List<String> menu = new ArrayList<String>();
		menu.add(MENU_SIMPLE_RUN);
		menu.add(MENU_EXPERIMENT_RUN);

		String result = TextUi.menu(menu, MENU_SIMPLE_RUN, "Choose the sumulation: ");

		switch (result) {
		case MENU_SIMPLE_RUN:
			doSimpleRun(true, false);
			break;
		case MENU_EXPERIMENT_RUN:
			doSimpleRun(false, true);
			break;

		}

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
		sip.setParameter(ExperimentConfiguration.RANDOM_CIVILIAN_MOVEMENT);
		ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);

		pack.setModel(sip);

		// =======================================================
		// Simulation for different start and end location of VIP
		ParameterSweep sweepLocations = ParameterSweepHelper
				.generateParameterSweepInteger("Different Start and End point for VIP", Y_OFFSET, 0, 49, 5);
		pack.addParameterSweep(sweepLocations);
		pack.cleanUp();
		pack.initialize();
		pack.run();

	}
}
