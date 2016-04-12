
package yaes.rcta.scenarios.Basic_Scenario_03;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yaes.framework.simulation.AbstractContext;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractHumanAgent;
import yaes.rcta.scenarios.Basic_Scenario_03.agents.VIPAgent;
import yaes.rcta.simulationHelper.InteractiveSimulation;
import yaes.rcta.ui.painterHumanAgent;
import yaes.rcta.ui.painterPhysicalAgent;
import yaes.ui.visualization.Visualizer;
import yaes.ui.visualization.painters.paintEnvironmentModel;
import yaes.world.World;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;

/**
 * The class defines the context of the scenario
 * 
 * @author Taranjeet
 * 
 */
public class Context extends AbstractContext implements constRCTA, Serializable {

	private static final long serialVersionUID = 359443118685990510L;
	private List<AbstractHumanAgent> humans = new ArrayList<AbstractHumanAgent>();

	/**
	 * An environmental model which contains the obstacles
	 */
	private EnvironmentModel emGlobalCost;
	private InteractiveSimulation interactiveSim;

	/**
	 * This is the initialization function. It will be called at the beginning
	 * of every simulation. This is the point where we will create the world of
	 * the simulation based on the simulation input parameters
	 * 
	 * @param sip
	 * @param sop
	 */
	@Override
	public void initialize(SimulationInput sip, SimulationOutput sop) {

		super.initialize(sip, sop);
		theWorld = new World(sop);

		this.random = new Random(sip.getParameterInt(RCTA_RANDOM_SEED));

		interactiveSim = new InteractiveSimulation(this);
		// metrics for the costs
		sop.createVariable(THREAT_VALUE, false);
		sop.createVariable(NEUTRALIZE_THREAT_VALUE, false);

		// this.pathPolicy = new PathPolicy(this.emGlobalCost, this);
		Helper.initializeEnvironment(sip, sop, this);

		// generatePointsOfInterest();
		// isVisible(new Location(100, 60), new Location(150, 70));
		VIPAgent VIP = null;

		VIP = AgentBuilder.createVIP(this, "VIP",
				new Location(sip.getParameterInt(SOLDIER_X),
						sip.getParameterInt(SOLDIER_Y) + sip.getParameterInt(Y_OFFSET)),
				new Location(sip.getParameterInt(SOLDIER_X_DESTINATION),
						sip.getParameterInt(SOLDIER_Y_DESTINATION) + sip.getParameterInt(Y_OFFSET)),
				2.0);
		humans.add(VIP);

		AgentBuilder.createCrowd(this, 6);
		for (AbstractHumanAgent hm : humans) {
			interactiveSim.addManualAgent(hm);
		}

		// setting up the mode of simulation, its defaults would be a
		// non-controlled mode
		switch (sip.getParameterEnum(ExperimentMode.class)) {
		case NORMAL_OPERATION:
			interactiveSim.setExperimentMode(0);
			break;
		case PROGRAMMED_RUN:
			interactiveSim.setExperimentMode(1);
			break;
		case MANUAL_RUN:
			interactiveSim.setExperimentMode(2);
			break;
		default:
			interactiveSim.setExperimentMode(0);
			break;

		}

		// if the VisualDisplay is required then create the visual
		// representation
		if (sip.getParameterEnum(VisualDisplay.class) == VisualDisplay.YES) {
			createVisualRepresentation(null);
		}

	}

	/**
	 * Creates a visual representation
	 */
	@Override
	public void createVisualRepresentation(Visualizer existingVisualizer) {
		/*
		 * Create the visualizer covering the full considered area
		 */
		if (existingVisualizer != null) {
			this.visualizer = existingVisualizer;
			this.visualizer.setUpdatedInspector(true);
			this.visualizer.removeAllObjects();
			// Set the magnification of visual scenario
			this.visualizer.getVisualCanvas().setMagnify(5.0);
			visualizer.setVisible(true);
		} else {
			if (sip.getSimulationControlPanel() == null) {
				this.visualizer = new Visualizer(this.sip.getParameterInt(MAP_WIDTH),
						this.sip.getParameterInt(MAP_HEIGHT), null, "RCTA scenario", true);
				// Set the magnification of visual scenario
				this.visualizer.getVisualCanvas().changeMagnify(5.0);
				this.visualizer.setUpdatedInspector(true);
				this.visualizer.setVisible(true);

			} else {
				throw new Error("Simulation control panel not to supported");
			}
		}

		/*
		 * Create an environment painter, which also paints the heatmap
		 */
		boolean doPaintEM = true;
		if (doPaintEM) {
			paintEnvironmentModel paintEM = new paintEnvironmentModel();
			this.visualizer.addObject(emGlobalCost, paintEM);
		}

		/*
		 * Paint soldier nodes
		 */

		for (AbstractHumanAgent r : humans) {
			if (r.getName().contains("BG")) {
				painterPhysicalAgent pntSoldier = new painterHumanAgent(Color.BLUE, Color.GRAY, 0.01f, Color.GRAY,
						0.01f);
				this.visualizer.addObject(r, pntSoldier);
			} else if (r.getName().contains("CIV")) {
				painterPhysicalAgent pntSoldier = new painterHumanAgent(Color.black, Color.yellow, 0.01f,
						Color.yellow, 0.01f);
				this.visualizer.addObject(r, pntSoldier);
			} else {
				painterPhysicalAgent pntSoldier = new painterHumanAgent(Color.GREEN, Color.GRAY, 0.01f, Color.GRAY,
						0.01f);
				this.visualizer.addObject(r, pntSoldier);
			}
		}

		/*
		 * paint display Text
		 */

		this.visualizer.getVisualCanvas().getInternalPanel().addMouseListener(interactiveSim);
		this.visualizer.getVisualCanvas().getInternalPanel().addKeyListener(interactiveSim);

	}

	/**
	 * Context update function
	 * 
	 * @param sip
	 * @param sop
	 * @param time
	 * @return returns 0 to stop the simulation, timestep to continue (normally
	 *         1)
	 */
	public void update(SimulationInput sip, SimulationOutput sop, double time) {
		getWorld().setTime(time);
		interactiveSim.runAgent();
	}

	int terminate = 0;

	public List<AbstractHumanAgent> getHumans() {
		return humans;
	}

	public void setHumans(List<AbstractHumanAgent> humans) {
		this.humans = humans;
	}

	

	

	public EnvironmentModel getEnvironmentModel() {
		return this.emGlobalCost;
	}

	public void setEnvironmentModel(EnvironmentModel environmentModel) {
		this.emGlobalCost = environmentModel;
	}

	public InteractiveSimulation getInteractiveSim() {
		return interactiveSim;
	}

}
