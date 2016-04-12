package yaes.rcta.scenarios.Basic_Scenario_03;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yaes.framework.simulation.Simulation;
import yaes.framework.simulation.SimulationInput;
import yaes.rcta.RctaResourceHelper;
import yaes.rcta.constRCTA;
import yaes.ui.text.TextUi;

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
		
		TextUi.println("Postprocessing Complete, System exit.");
        System.exit(0);

	}

	private static void doSimpleRun(boolean visualize, boolean writeRawdata) throws FileNotFoundException,
			InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		RctaResourceHelper.flushFolders();

		// Running this configuration for 10 runs of VIP
		for (int i = 0; i < 50; i += 5) {
			SimulationInput sip = new SimulationInput();
			sip.setParameter(Y_OFFSET, i);
			sip.setParameter(ExperimentMode.PROGRAMMED_RUN);
			sip.setStopTime(50);
			sip.setParameter(VisualDisplay.NO);
			if (visualize) {
				sip.setParameter(VisualDisplay.YES);
			}
			sip.setParameter(WriteOutput.NO);
			if (writeRawdata) {
				sip.setParameter(WriteOutput.YES);
			}
			ScenarioParameters.createSipMarket(sip);
			Context context = new Context();
			Simulation.simulate(sip, SimulationCode.class,context, logDir);

		}

	}
}
