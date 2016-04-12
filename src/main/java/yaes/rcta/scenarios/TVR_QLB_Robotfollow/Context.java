
package yaes.rcta.scenarios.TVR_QLB_Robotfollow;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yaes.framework.simulation.AbstractContext;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.rcta.constRCTA;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents.Human;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents.Robot;
import yaes.rcta.simulationHelper.InteractiveSimulation;
import yaes.rcta.ui.painterLabel;
import yaes.rcta.ui.painterPhysicalAgent;
import yaes.rcta.ui.painterRobotAgent;
import yaes.rcta.util.DisplayText;
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

	private List<Robot> robots = new ArrayList<Robot>();
	private List<Human> humans = new ArrayList<Human>();

	private List<Double> threatValues = new ArrayList<Double>();
	private List<Double> neutralizeThreatValue = new ArrayList<Double>();

	/**
	 * Display ThreatLevel TL on Visualizer
	 */
	private DisplayText displayText1;
	/**
	 * Display Reduced ThreatLevel RT on Visualizer
	 */
	private DisplayText displayText2;

	/**
	 * An environmental model which contains the obstacles
	 */
	private EnvironmentModel emGlobalCost;

	// To decide which mode of experiment to run;
	// 0: Run normally
	// 1: Programmed Mode
	// --> Enter button: true: Run preprogrammed simulation visually
	// --> Enter button: false: Run preprogrammed simulation step by step using
	// [SPACE] button
	// 2: Manual Mode
	// --> Enter button true: Robot take action when any agent in the simulation
	// moved manually
	// --> Enter button false: Robot take action when [SPACE] button pressed
	// private int experimentMode = 0;
	// private boolean simulationContinue = true;
	// private boolean enterButton = true;
	// private boolean spaceButton = false;

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
		this.displayText1 = new DisplayText();
		this.displayText2 = new DisplayText(new Location(0, 5));

		interactiveSim = new InteractiveSimulation(this);
		// metrics for the costs
		sop.createVariable(THREAT_VALUE, false);
		sop.createVariable(NEUTRALIZE_THREAT_VALUE, false);

		// this.pathPolicy = new PathPolicy(this.emGlobalCost, this);
		Helper.initializeEnvironment(sip, sop, this);

		// generatePointsOfInterest();
		// isVisible(new Location(100, 60), new Location(150, 70));
		Human VIP = null;

		VIP = AgentBuilder.createSoldier(this, "VIP",
				new Location(sip.getParameterInt(SOLDIER_X),
						sip.getParameterInt(SOLDIER_Y) + sip.getParameterInt(Y_OFFSET)),
				new Location(sip.getParameterInt(SOLDIER_X_DESTINATION),
						sip.getParameterInt(SOLDIER_Y_DESTINATION) + sip.getParameterInt(Y_OFFSET)),
				2.0);
		humans.add(VIP);

		int count = sip.getParameterInt(ROBOTS_COUNT);
		if (count <= 4) {
			for (int i = 1; i <= count; i++) {
				robots.add(AgentBuilder.createRobot(this, "HR-" + i));
			}
		}

		AgentBuilder.createSoldier(this, 6);

		// set agents for interactive simulation

		for (Robot rt : robots) {
			interactiveSim.addAutonomousAgent(rt);
		}
		for (Human hm : humans) {
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
		 * Paint robot nodes
		 */
		painterRobotAgent pntRobot = new painterRobotAgent(Color.black, Color.red, 0.01f, Color.blue, 0.01f);
		for (Robot r : robots) {
			visualizer.addObject(r, pntRobot);
		}

		/*
		 * Paint soldier nodes
		 */

		for (Human r : humans) {
			if (r.getName().contains("BG")) {
				painterPhysicalAgent pntSoldier = new painterPhysicalAgent(Color.BLUE, Color.GRAY, 0.01f, Color.GRAY,
						0.01f);
				this.visualizer.addObject(r, pntSoldier);
			} else if (r.getName().contains("CIV")) {
				painterPhysicalAgent pntSoldier = new painterPhysicalAgent(Color.black, Color.yellow, 0.01f,
						Color.yellow, 0.01f);
				this.visualizer.addObject(r, pntSoldier);
			} else {
				painterPhysicalAgent pntSoldier = new painterPhysicalAgent(Color.GREEN, Color.GRAY, 0.01f, Color.GRAY,
						0.01f);
				this.visualizer.addObject(r, pntSoldier);
			}
		}

		/*
		 * paint display Text
		 */

		this.visualizer.getVisualCanvas().getInternalPanel().addMouseListener(interactiveSim);
		this.visualizer.getVisualCanvas().getInternalPanel().addKeyListener(interactiveSim);
		painterLabel pntDisplayText = new painterLabel(null);
		this.visualizer.addObject(this.displayText1, pntDisplayText);
		this.visualizer.addObject(this.displayText2, pntDisplayText);

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
		Helper.calculateThreatLevel(this);
		Helper.calculateNutralizedThreatLevel(this);
		interactiveSim.runAgent();
	}

	int terminate = 0;

	public List<Double> getThreatValues() {
		return threatValues;
	}

	public List<Double> getNeutralizeThreatValue() {
		return neutralizeThreatValue;
	}

	public List<Human> getHumans() {
		return humans;
	}

	public void setHumans(List<Human> humans) {
		this.humans = humans;
	}

	public List<Robot> getRobots() {
		return this.robots;
	}

	public void setRobots(List<Robot> robots) {
		this.robots = robots;
	}

	public List<Human> getSoldiers() {
		return this.humans;
	}

	public void setSoldiers(List<Human> soldiers) {
		this.humans = soldiers;
	}

	public EnvironmentModel getEnvironmentModel() {
		return this.emGlobalCost;
	}

	public void setEnvironmentModel(EnvironmentModel environmentModel) {
		this.emGlobalCost = environmentModel;
	}

	public DisplayText getDisplayText1() {
		return this.displayText1;
	}

	public void setDisplayText1(DisplayText displayText1) {
		this.displayText1 = displayText1;
	}

	public DisplayText getDisplayText2() {
		return displayText2;
	}

	public void setDisplayText2(DisplayText displayText2) {
		this.displayText2 = displayText2;
	}

	public void setThreatValues(List<Double> threatValues) {
		this.threatValues = threatValues;
	}

	public void setNeutralizeThreatValue(List<Double> neutralizeThreatValue) {
		this.neutralizeThreatValue = neutralizeThreatValue;
	}

	public InteractiveSimulation getInteractiveSim() {
		return interactiveSim;
	}

}
