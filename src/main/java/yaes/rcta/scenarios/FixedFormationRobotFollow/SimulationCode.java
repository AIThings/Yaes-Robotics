/**
 * 
 */
package yaes.rcta.scenarios.FixedFormationRobotFollow;

import java.io.File;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import yaes.framework.simulation.IContext;
import yaes.framework.simulation.ISimulationCode;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.rcta.constRCTA;
import yaes.rcta.constRCTA.ExperimentConfiguration;
import yaes.rcta.constRCTA.WriteOutput;
import yaes.ui.text.TextUi;
import yaes.util.FileWritingUtil;

/**
 * @author Taranjeet
 *
 */
public class SimulationCode implements ISimulationCode {

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
		Context context = (Context) theContext;
		// TextUi.println("This is postprocess method call");

		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (double th : context.getThreatValues()) {
			stats.addValue(th);
		}
		sop.update(constRCTA.THREAT_VALUE, stats.getMean());

		stats = new DescriptiveStatistics();
		for (double th : context.getNeutralizeThreatValue()) {
			stats.addValue(th);
		}
		sop.update(constRCTA.NEUTRALIZE_THREAT_VALUE, stats.getMean());

		// ==================================================
		// Write raw output to log files
		// ==================================================
		if (sip.getParameterEnum(WriteOutput.class) == WriteOutput.YES) {
			String type = sip.getParameterEnum(ExperimentConfiguration.class).toString();
			int k = sip.getParameterInt(constRCTA.ROBOTS_COUNT);
			int i = sip.getParameterInt(constRCTA.Y_OFFSET);
			File plotFile = new File(constRCTA.logDir,
					"ThreatLevel" + "-" + type + "-" + (int) i / 5 + "-" + k + ".txt");
			StringBuilder plotData = new StringBuilder();
			for (double d : context.getThreatValues()) {
				plotData.append(d + "\n");
			}
			FileWritingUtil.writeToTextFile(plotFile, plotData.toString());
			plotFile = new File(constRCTA.logDir,
					"NeutralizeThreatLevel" + "-" + type + "-" + (int) i / 5 + "-" + k + ".txt");

			plotData = new StringBuilder();
			for (double d : context.getNeutralizeThreatValue()) {
				plotData.append(d + "\n");
			}
			FileWritingUtil.writeToTextFile(plotFile, plotData.toString());
			plotFile = new File(constRCTA.logDir + "");
			TextUi.println("Raw output files saved in Directory " + plotFile.getAbsolutePath());
		}

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
		Context context = (Context) theContext;
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
		// TextUi.println("Simulating we are at time: " + time);
		Context context = (Context) theContext;
		if (context.getVisualizer() != null) {
			context.getVisualizer().update();
			// Use a thread sleep to slow down the simulation
			try {
				Thread.sleep(100);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		context.update(sip, sop, time);
		return 1;
	}
}
