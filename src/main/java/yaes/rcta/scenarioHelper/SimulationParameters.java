package yaes.rcta.scenarioHelper;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.constRCTA;

public class SimulationParameters implements constRCTA {

	public static void simulationParameter(SimulationInput sip){
		
		if(sip.getParameterEnum(ExperimentMode.class) == ExperimentMode.MANUAL_RUN){
			sip.setStopTime(20000);
			sip.setParameter(VisualDisplay.YES);
		}else if(sip.getParameterEnum(ExperimentMode.class) == ExperimentMode.PROGRAMMED_RUN){
			// Running exactly for 50 sample points for plotting
			sip.setStopTime(2000);
			sip.setParameter(VisualDisplay.YES);
		}else {
			sip.setStopTime(100);
			sip.setParameter(VisualDisplay.NO);
		}
		
		
	}
	
}
