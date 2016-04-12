/**
 * 
 */
package yaes.rcta.scenarios.Basic_Scenario_02;

import yaes.framework.simulation.IContext;
import yaes.framework.simulation.ISimulationCode;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;

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
