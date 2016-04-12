/**
 * 
 */
package yaes.rcta;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import yaes.framework.simulation.AbstractContext;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.rcta.agentBuilder.CivilianBuilder;
import yaes.rcta.agentBuilder.CrowdBuilder;
import yaes.rcta.agentBuilder.RobotBuilder;
import yaes.rcta.agentBuilder.SoldierBuilder;
import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.rcta.agents.Human;
import yaes.rcta.agents.civilian.Civilian;
import yaes.rcta.agents.gametheory.GamePlay;
import yaes.rcta.agents.gametheory.MicroConflict;
import yaes.rcta.agents.robot.Robot;
import yaes.rcta.environment.RctaEnvironmentHelper;
import yaes.rcta.environment.V2CHeatmap;
import yaes.rcta.environment.V2CHeatmap.ColorScheme;
import yaes.rcta.ui.painterLabel;
import yaes.rcta.ui.painterPhysicalAgent;
import yaes.rcta.ui.painterRobotAgent;
import yaes.rcta.util.DisplayText;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SinkNode;
import yaes.ui.plot.SurfacePlot;
import yaes.ui.plot.SurfacePlot.SurfaceType;
import yaes.ui.text.TextUi;
import yaes.ui.visualization.Visualizer;
import yaes.ui.visualization.painters.IValueToColor;
import yaes.ui.visualization.painters.paintEnvironmentModel;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

/**
 * The class defines the context of the RCTA scenario
 * 
 * @author Taranjeet
 * 
 */
public class RctaContext extends AbstractContext
    implements constRCTA, MouseListener, KeyListener, Serializable {

    private static final long serialVersionUID = 8867181182726587567L;

    private SensorNetworkWorld sensorWorld;
    private int sensorNodeCount;
    protected SinkNode theSinkNode;
    protected double transmissionRange;

    // public static final String2 PROP_OBSTACLE = "obstacle";
    public static String PROP_DENSITY = "crowd-density";
    private static String PROP_ALERT = "intrusion-alert";

    private final double SCALE_MOVEMENT_CONE = 15.0;
    private final double SCALE_PERSONAL_SPACE = 10.0;
    private final double SCALE_PHYSICAL = 40.0;

    private List<Robot> robots = new ArrayList<Robot>();
    private List<Human> humans = new ArrayList<Human>();
    private List<Civilian> civilians = new ArrayList<Civilian>();

    List<Double> threatValues = new ArrayList<Double>();

    List<Double> neutralizeThreatValue = new ArrayList<Double>();

    /**
     * Display ThreatLevel TL on Visualizer
     */
    private DisplayText displayText1;
    /**
     * Display Reduced ThreatLevel RT on Visualizer
     */
    private DisplayText displayText2;

    private ArrayList<Location> roboScoutingLocs;

    private PathPolicy pathPolicy;
    List<PlannedPath> lineOfSights = new ArrayList<PlannedPath>();

    // private HashMap<String, Location> locList = new HashMap<String,

    // Location>();
    /**
     * An environmental model which contains the obstacles
     */
    private EnvironmentModel emGlobalCost;

    /*
     * public Map<String, Location> pointOfInterest = new HashMap<String,
     * Location>();
     */

    private ArrayList<Location> plannedRoute = new ArrayList<Location>();

    public Map<String, Integer> waitingTime = new HashMap<String, Integer>();

    private Map<String, PlannedPath> pathValues =
            new HashMap<String, PlannedPath>();

    // For office simulation
    private HashMap<String, Location> permanentJobSittingSpaces =
            new HashMap<String, Location>();

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
    private int experimentMode = 0;
    private boolean simulationContinue = true;
    private boolean enterButton = false;
    private boolean spaceButton = false;

    /**
     * The currently active microconflicts in the context
     */
    private List<MicroConflict> microConflicts = new ArrayList<>();
    /**
     * The archives list of microconflicts. All the microconflicts which had
     * been at least once active will be added to this list (these are the ones
     * which have games played)
     */
    private List<MicroConflict> archivedMicroConflicts = new ArrayList<>();
    private static final String outputDir = "output";
    private SpecSingleConflict specSingleConflict;
    private double assumptionCforRobot; // used in Soldier scenario

    // private static final String outputDir = "output";

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

        this.random = new Random(sip.getParameterInt(RCTA_RANDOM_SEED));

        this.setSensorWorld(new SensorNetworkWorld(sop));
        this.theWorld = getSensorWorld();

        this.displayText1 = new DisplayText();
        this.displayText2 = new DisplayText(new Location(0, 5));

        // metrics for the costs
        sop.createVariable(METRICS_SOCIAL_COST, false);
        sop.createVariable(METRICS_MAX_SOCIAL_COST, false);
        sop.createVariable(METRICS_MISSION_COST, false);

        this.pathPolicy = new PathPolicy(this.emGlobalCost, this);
        this.roboScoutingLocs =
                new ArrayList<Location>(roboSearch_LocList.values());
        RctaEnvironmentHelper.initializeEnvironment(sip, sop, this);

        // generatePointsOfInterest();
        // isVisible(new Location(100, 60), new Location(150, 70));
        Human VIP = null;

        // Setting up different scenario specs for each scenario
        switch (this.getSceneType()) {
        case SINGLE_CONFLICT: {
            // the difference between the angles
            double angleDiff = (double) sip.getParameterDouble(ANGLE_DIFF);

            specSingleConflict = new SpecSingleConflict();
            specSingleConflict.setCenter(new Location(70, 70));
            // robot
            specSingleConflict.setRadiusR(30);
            specSingleConflict.setStartAngleR(0.0 * Math.PI); // was 0
            specSingleConflict.setDestAngleR(1.0 * Math.PI); // was Math.PI
            specSingleConflict.setSpeedR(3.0);
            // civilian
            specSingleConflict.setRadiusC(20);
            specSingleConflict.setStartAngleC(angleDiff * Math.PI);
            specSingleConflict.setDestAngleC((angleDiff + 1.0) * Math.PI);
            specSingleConflict.setSpeedC(2.0);
            // set up the conflict
            specSingleConflict.setupSingleConflict(sip, this);
            break;
        }

        case ROBOT_FOLLOW_SOLDIER:
            SoldierBuilder.createSoldier(this, 2.0);
            robots.add(RobotBuilder.createRobot(this, "HR-1"));

            break;
        case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
            SoldierBuilder.createSoldier(this, 2.0);
            robots.add(RobotBuilder.createRobot(this, "HR-1"));
            robots.add(RobotBuilder.createRobot(this, "HR-2"));
            robots.add(RobotBuilder.createRobot(this, "HR-3"));
            break;
        case OBSTACLE_AVOIDANCE:
            SoldierBuilder.createSoldier(this, 2.0);
            break;
        case OBSTACLE_AVOIDANCE_IN_CROWD:
            SoldierBuilder.createSoldier(this, 2.0);
            CivilianBuilder.createCivilians(sip, this);
            pathPolicy.generateAStarPaths();
            break;
        case CROWD_SIMULATION: {
            switch (sip.getParameterEnum(CrowdScenarioType.class)) {
            case SHOPPING_MALL:
                RctaEnvironmentHelper.initEnvShoppingMall(sip, sop, this);
                break;
            case OFFICE_SPACE:
                RctaEnvironmentHelper.initEnvOfficeSpace(sip, sop, this);
                break;
            case RED_CARPET:
                RctaEnvironmentHelper.initEnvRedCarpet(sip, sop, this);
                break;
            default:
                throw new Error("No Environment implemented as default");
            }
            break;
        }
        case SINGLE_ROBOT_FOLLOW:
        case CLOSE_PROTECTION_SINGLE_ROBOT:
        case CROWD_SEEK_VIP:
            VIP = SoldierBuilder.createSoldier(this, "VIP",
                    new Location(sip.getParameterInt(SOLDIER_X),
                            sip.getParameterInt(SOLDIER_Y)),
                    new Location(sip.getParameterInt(SOLDIER_X_DESTINATION),
                            sip.getParameterInt(SOLDIER_Y_DESTINATION)),
                    2.0);
            this.humans.add(VIP);
            this.robots.add(RobotBuilder.createRobot(this, "HR-1"));
            SoldierBuilder.createSoldier(this, 6);
            // pathPolicy.generateAStarPathsSoldiers();
            break;
        case MULTIPLE_ROBOT_FOLLOW:
        case CLOSE_PROTECTION_MULTIPLE_ROBOT:
        case CLOSE_PROTECTION_CONTROL_MODEL:
            VIP = SoldierBuilder.createSoldier(this, "VIP",
                    new Location(sip.getParameterInt(SOLDIER_X),
                            sip.getParameterInt(SOLDIER_Y)),
                    new Location(sip.getParameterInt(SOLDIER_X_DESTINATION),
                            sip.getParameterInt(SOLDIER_Y_DESTINATION)),
                    2.0);
            humans.add(VIP);
            int count = sip.getParameterInt(ROBOTS_COUNT);
            if (count <= 4) {
                for (int i = 1; i <= count; i++) {
                    robots.add(RobotBuilder.createRobot(this, "HR-" + i));
                }
            }

            SoldierBuilder.createSoldier(this, 6);
            break;
        case REALISTIC_WITH_ROBOT_SCENARIO:
            robots.add(RobotBuilder.createRobot(this, "HR-1"));
            break;
        case ROBOT_SCOUT:
            // createScoutRobot(2.0);
            RobotBuilder.createScoutRobots(sip, this);
            CivilianBuilder.createCivilians(sip, this);
            // generatePointsOfInterest();
            // pathPolicy.generateAStarPaths();
            break;
        case SAME_CROWD_ADAPTIVE:
            //CivilianBuilder.createCivilians(sip, this);
            CivilianBuilder.createMalePopulation(this);
            CivilianBuilder.createFemalePopulation(this);
            Robot robot = RobotBuilder.createRobot(this, "HR-1");
            this.robots.add(robot);
            break;
        case DEFAULT:
        case IMITATION:
        case REALISTIC:

        default:
            SoldierBuilder.createSoldier(this, 2);
            this.robots.add(RobotBuilder.createRobot(this, "HR-1"));
            CivilianBuilder.createCivilians(sip, this);
            this.pathPolicy.generateAStarPaths();
        }

        // setting up the mode of simulation, its defaults would be a
        // non-controlled mode
        switch (sip.getParameterEnum(ExperimentMode.class)) {
        case NORMAL_OPERATION:
            this.experimentMode = 0;
            break;
        case PROGRAMMED_RUN:
            this.experimentMode = 1;
            break;
        case MANUAL_RUN:
            this.experimentMode = 2;
            break;
        default:
            this.experimentMode = 0;
            break;

        }

        // if the VisualDisplay is required then create the visual
        // representation
        if (sip.getParameterEnum(VisualDisplay.class) == VisualDisplay.YES) {
            createVisualRepresentation(null);
        }

    }

    private long idCounter = 0;

    public String createID() {
        return String.valueOf(idCounter++);
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
                this.visualizer =
                        new Visualizer(this.sip.getParameterInt(MAP_WIDTH),
                                this.sip.getParameterInt(MAP_HEIGHT), null,
                                "RCTA scenario", true);
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
            /*
             * Add a specific color scheme for the heatmap
             */
            IValueToColor v2c =
                    new V2CHeatmap(ColorScheme.UcfTwoColorProgression, 0, 100);
            paintEM.addV2C(PROP_DENSITY, v2c);
            this.visualizer.addObject(emGlobalCost, paintEM);
        }
        /*
         * Paint robot nodes
         */
        painterRobotAgent pntRobot = new painterRobotAgent(Color.black,
                Color.red, 0.01f, Color.blue, 0.01f);
        for (Robot r : robots) {
            visualizer.addObject(r, pntRobot);
        }

        /*
         * paint civilian nodes
         */
        painterPhysicalAgent pntCivilian = new painterPhysicalAgent(Color.black,
                Color.yellow, 0.01f, Color.yellow, 0.01f);
        for (Civilian c : this.getCivilians()) {
            this.visualizer.addObject(c, pntCivilian);

        }

        /*
         * Paint soldier nodes
         */

        for (Human r : humans) {
            if (r.getName().contains("BG")) {
                painterPhysicalAgent pntSoldier = new painterPhysicalAgent(
                        Color.BLUE, Color.GRAY, 0.01f, Color.GRAY, 0.01f);
                this.visualizer.addObject(r, pntSoldier);
            } else if (r.getName().contains("CIV")) {
                painterPhysicalAgent pntSoldier = new painterPhysicalAgent(
                        Color.black, Color.yellow, 0.01f, Color.yellow, 0.01f);
                this.visualizer.addObject(r, pntSoldier);
            } else {
                painterPhysicalAgent pntSoldier = new painterPhysicalAgent(
                        Color.GREEN, Color.GRAY, 0.01f, Color.GRAY, 0.01f);
                this.visualizer.addObject(r, pntSoldier);
            }
        }

        /*
         * paint display Text
         */

        switch (this.getSceneType()) {
        case CLOSE_PROTECTION_CONTROL_MODEL:
        case CLOSE_PROTECTION_MULTIPLE_ROBOT:
        case CLOSE_PROTECTION_SINGLE_ROBOT:
        case SINGLE_ROBOT_FOLLOW:
        case MULTIPLE_ROBOT_FOLLOW:
        case CROWD_SEEK_VIP:
        case CROWD_SIMULATION:
            this.visualizer.getVisualCanvas().getInternalPanel()
                    .addMouseListener(this);
            this.visualizer.getVisualCanvas().getInternalPanel()
                    .addKeyListener(this);
            break;
        case SAME_CROWD_ADAPTIVE:
        case DEFAULT:
        case IMITATION:
        case OBSTACLE_AVOIDANCE:
        case OBSTACLE_AVOIDANCE_IN_CROWD:
        case REALISTIC:
        case REALISTIC_WITH_ROBOT_SCENARIO:
        case ROBOT_FOLLOW_SOLDIER:
        case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
        case ROBOT_SCOUT:
        case SINGLE_CONFLICT:
        default:
            break;

        }

        painterLabel pntDisplayText = new painterLabel(null);
        this.visualizer.addObject(this.displayText1, pntDisplayText);
        this.visualizer.addObject(this.displayText2, pntDisplayText);

    }

    /**
     * For the update-phase, do the required action in a micro-conflict
     */
    private void actionMicroConflict() {
        // For the agents which are not in microconflicts, let them take the
        // actions
        for (Civilian civ : this.civilians) {
            if (civ.getMicroConflict() == null)
                civ.action();
        }
        for (Robot robot : this.robots) {
            if (robot.getMicroConflict() == null)
                robot.action();
        }

        // Now, for all the active microconflicts, play the games:
        for (MicroConflict mc : this.microConflicts) {
            // TODO: Same agents can play microconflict with eachother
            if (mc.isActive()) {
                GamePlay.play(this, mc);
            }
        }
    }

    /**
     * Update the costs vector at the end of the micro-conflict
     */
    private void updateGlobalCost() {
        // update the new global cost estimate
        this.emGlobalCost.setPropertyToValue(PROP_DENSITY, 0.0);
        for (Robot robot : robots) {
            robot.addCosts(this.emGlobalCost, PROP_DENSITY, SCALE_PHYSICAL,
                    SCALE_PERSONAL_SPACE, SCALE_MOVEMENT_CONE);
        }

        for (Civilian civ : this.getCivilians()) {
            civ.addCosts(this.emGlobalCost, PROP_DENSITY, SCALE_PHYSICAL,
                    SCALE_PERSONAL_SPACE, SCALE_MOVEMENT_CONE);
        }

        MicroConflictManager.manageMicroConflicts(this);
    }

    /**
     * Print out the property in the form of a m-file to visualize the graph of
     * globalCost
     */
    private void createCostPropertyGraph() {
        // save the property value
        try {
            File plotFile = new File(outputDir,
                    "density_" + (int) getWorld().getTime() + ".m");
            SurfacePlot.createEMSurfacePlot(SurfaceType.LIGHTED, emGlobalCost,
                    PROP_DENSITY, plotFile);
        } catch (IOException ioex) {
            ioex.printStackTrace();
            TextUi.errorPrint("not continuing, fix this!!!");
            System.exit(1);
        }
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
        switch (sip.getParameterEnum(ScenarioType.class)) {
        case DEFAULT:
        case IMITATION:
        case REALISTIC:
        case REALISTIC_WITH_ROBOT_SCENARIO:
        case CLOSE_PROTECTION_SINGLE_ROBOT:
        case CLOSE_PROTECTION_MULTIPLE_ROBOT:
        case CLOSE_PROTECTION_CONTROL_MODEL:
        case SINGLE_ROBOT_FOLLOW:
        case MULTIPLE_ROBOT_FOLLOW:
        case CROWD_SEEK_VIP:
        case CROWD_SIMULATION: {
            RctaEnvironmentHelper.calculateThreatLevel(this);
            RctaEnvironmentHelper.calculateNutralizedThreatLevel(this);

            while (!this.simulationContinue) {
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (this.experimentMode == 2) {
                if (this.enterButton) {
                    for (Robot robot : robots)
                        robot.action();
                } else {
                    if (this.spaceButton) {
                        for (Robot robot : robots)
                            robot.action();
                        this.spaceButton = false;
                    }
                }
                this.simulationContinue = false;
            } else if (this.experimentMode == 1) {
                if (this.enterButton) {
                    this.simulationContinue = true;
                    for (Human soldier : humans)
                        soldier.action();
                    for (Robot robot : robots)
                        robot.action();
                } else {

                    if (this.spaceButton) {
                        for (Human soldier : humans)
                            soldier.action();
                        for (Robot robot : robots)
                            robot.action();
                        this.spaceButton = false;
                    }
                    this.simulationContinue = false;
                }
            }

            // actionMicroConflict();
            // updateGlobalCost();
            // save and create the property value
            // createCostPropertyGraph();
            break;
        }
        case OBSTACLE_AVOIDANCE:
        case OBSTACLE_AVOIDANCE_IN_CROWD:
        case ROBOT_FOLLOW_SOLDIER:
        case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
            for (Human soldier : humans)
                soldier.action();
            for (Robot robot : robots)
                robot.action();
            for (Civilian civ : this.getCivilians()) {
                civ.action();
            }
            break;

        case SINGLE_CONFLICT:
        case SAME_CROWD_ADAPTIVE:
            try {
                actionMicroConflict();
                updateGlobalCost();
                createCostPropertyGraph();
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            break;
        case ROBOT_SCOUT:
            actionMicroConflict();
            updateGlobalCost();
            if (time == 0.00) {
                try {
                    RctaEnvironmentHelper.updateClusters(this, locList,
                            anchorLocList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // save the property value
            createCostPropertyGraph();
            break;
        default:
            break;

        }

        // update the crowd for crowd_simulation scenario
        if (this.getSceneType() == ScenarioType.CROWD_SIMULATION) {
            CrowdBuilder.updateCrowd(sip, this);

        }
        // Method below search for conflict of the way among the agents.
        // This calculation is based on personal space which can be violated
        // from any direction.
        //
        // Following condition can lead to conflicts. 1) Both agent moving
        // in same direction, 2) Both agent moving in opposite direction, 3)
        // Both agent moving toward same point of intersection, 4)Both agent
        // moving away from same point of intersection. 5)Both agents are
        // violating their personal space when one agent moving away from
        // point of intersection, whereas other one moving toward point of
        // intersection.

        // Method below search for conflict of the way among the agents
        // based on personal but only in the direction of walk

        // FIXME: Implement new method below
        /*
         * if (terminate == 1) { return 0; } else { return 1; }
         */

    }

    /**
     * Creates a model for the cost in a single agent. returns the cost of
     * playing either C or D
     * 
     * @param agent
     * @return
     */
    public SimpleEntry<EnvironmentModel, EnvironmentModel> getSingleAgentCosts(
        AbstractPhysicalAgent agent) {
        // the cost for current location
        EnvironmentModel emCurrentLocationCost = new EnvironmentModel(
                "TheModel", 0, 0,
                this.getSimulationInput().getParameterInt(MAP_WIDTH),
                this.getSimulationInput().getParameterInt(MAP_HEIGHT), 1, 1);
        emCurrentLocationCost.createProperty(PROP_DENSITY);
        emCurrentLocationCost.setPropertyToValue(PROP_DENSITY, 0.0);
        Location agentLocation = agent.getLocation();
        agent.addCosts(emCurrentLocationCost, PROP_DENSITY,
                agent.getAzPhysical().getValue(agentLocation),
                agent.getAzPersonalSpace().getValue(agentLocation),
                agent.getAzMovementCone().getValue(agentLocation));
        // the cost for the next location, this does a little trick by moving
        // the agent back and forth
        Location currentLocation = agent.getLocation();
        agent.setLocation(agent.getIntendedMove());
        agent.updateZones();
        EnvironmentModel emIntendedLocationCost = new EnvironmentModel(
                "TheModel", 0, 0,
                this.getSimulationInput().getParameterInt(MAP_WIDTH),
                this.getSimulationInput().getParameterInt(MAP_HEIGHT), 1, 1);
        emIntendedLocationCost.createProperty(PROP_DENSITY);
        emIntendedLocationCost.setPropertyToValue(PROP_DENSITY, 0.0);
        agent.addCosts(emIntendedLocationCost, PROP_DENSITY,
                agent.getAzPhysical().getValue(agentLocation),
                agent.getAzPersonalSpace().getValue(agentLocation),
                agent.getAzMovementCone().getValue(agentLocation));
        // now move back
        agent.setLocation(currentLocation);
        agent.updateZones();
        return new SimpleEntry<>(emCurrentLocationCost, emIntendedLocationCost);
    }

    /**
     * Gives the predicted cost for moving to a certain location
     * 
     * @param location
     * @param agent
     */
    public double predictedCost(Location location,
        AbstractPhysicalAgent agent) {
        EnvironmentModel emMyCost = getSingleAgentCosts(agent).getKey();
        double globalCost = (double) this.emGlobalCost
                .getPropertyAt(PROP_DENSITY, location.getX(), location.getY());
        double myCost = (double) emMyCost.getPropertyAt(PROP_DENSITY,
                location.getX(), location.getY());
        return globalCost - myCost;
    }

    int terminate = 0;

    /**
     * Remove
     * 
     * @param object
     */
    public void removeFromVisualizer(Object object) {
        this.visualizer.removeObject(object);

    }

    public void addToVisualizer(AbstractPhysicalAgent r) {

        if (r.getName().contains("BG")) {
            painterPhysicalAgent pntSoldier = new painterPhysicalAgent(
                    Color.BLUE, Color.GRAY, 0.01f, Color.GRAY, 0.01f);
            this.visualizer.addObject(r, pntSoldier);
        } else if (r.getName().contains("CIV")) {
            painterPhysicalAgent pntSoldier = new painterPhysicalAgent(
                    Color.black, Color.yellow, 0.01f, Color.yellow, 0.01f);
            this.visualizer.addObject(r, pntSoldier);
        } else {
            painterPhysicalAgent pntSoldier = new painterPhysicalAgent(
                    Color.GREEN, Color.GRAY, 0.01f, Color.GRAY, 0.01f);
            this.visualizer.addObject(r, pntSoldier);
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TextUi.println("Focus:" + visualizer.getFrame().getFocusOwner());
        // TextUi.println("Mouse pressed");
        visualizer.getVisualCanvas().getInternalPanel().setFocusable(true);
        visualizer.getVisualCanvas().getInternalPanel().requestFocus();

        // Location loc = soldiers.get(0).getLocation();
        // TextUi.println("Location :" + loc);
        // TextUi.println("Location to transformed:"
        // + PainterHelper.locationToTransformedPoint(loc,
        // visualizer.getVisualCanvas()));

        Point position = e.getPoint();
        AffineTransform transform =
                visualizer.getVisualCanvas().getTheTransform();
        Point2D.Double realpoint = new Point2D.Double();
        try {
            transform.inverseTransform(position, realpoint);
        } catch (NoninvertibleTransformException nive) {

            nive.printStackTrace();
        }
        Location locMouse = new Location(realpoint.getX(), realpoint.getY());
        // TextUi.println("Realpoint:" + realpoint);

        for (Human soldier : humans) {
            double dist = soldier.getLocation().distanceTo(locMouse);
            if (dist <= soldier.getRadiusPersonalSpace()) {
                soldier.setFocus(true);

            } else {
                soldier.setFocus(false);
            }

        }
        RctaEnvironmentHelper.calculateThreatLevel(this);
        RctaEnvironmentHelper.calculateNutralizedThreatLevel(this);
        visualizer.getVisualCanvas().update();
    }

    @Override
    public void keyPressed(KeyEvent key) {
        if ((this.sip.getParameterEnum(
                ScenarioType.class) == ScenarioType.CLOSE_PROTECTION_SINGLE_ROBOT)
                || (this.sip.getParameterEnum(
                        ScenarioType.class) == ScenarioType.CLOSE_PROTECTION_MULTIPLE_ROBOT)
                || (this.sip.getParameterEnum(
                        ScenarioType.class) == ScenarioType.CLOSE_PROTECTION_CONTROL_MODEL)
                || (this.sip.getParameterEnum(
                        ScenarioType.class) == ScenarioType.SINGLE_ROBOT_FOLLOW)
                || (this.sip.getParameterEnum(
                        ScenarioType.class) == ScenarioType.MULTIPLE_ROBOT_FOLLOW)
                || (this.sip.getParameterEnum(
                        ScenarioType.class) == ScenarioType.CROWD_SEEK_VIP)
                || (this.sip.getParameterEnum(
                        ScenarioType.class) == ScenarioType.CROWD_SIMULATION)) {
            if (key.getKeyCode() == KeyEvent.VK_ESCAPE) {
                terminate = 1;
                this.simulationContinue = true;
            }

            if (key.getKeyCode() == KeyEvent.VK_SPACE) {
                this.simulationContinue = true;
                this.spaceButton = true;

            }
            if (key.getKeyCode() == KeyEvent.VK_ENTER) {
                this.enterButton = !this.enterButton;
                this.simulationContinue = !this.simulationContinue;
            }
            if (this.experimentMode == 2) {
                if (key.getKeyCode() == KeyEvent.VK_UP) {

                    for (Human soldier : humans) {
                        if (soldier.isFocus()) {
                            soldier.moveWithArrowKeys("UP");
                            this.simulationContinue = true;
                        }
                    }

                }

                if (key.getKeyCode() == KeyEvent.VK_DOWN) {

                    for (Human soldier : humans) {
                        if (soldier.isFocus()) {
                            soldier.moveWithArrowKeys("DOWN");
                            this.simulationContinue = true;
                        }
                    }

                }

                if (key.getKeyCode() == KeyEvent.VK_LEFT) {

                    for (Human soldier : humans) {
                        if (soldier.isFocus()) {
                            soldier.moveWithArrowKeys("LEFT");
                            this.simulationContinue = true;
                        }
                    }

                }
                if (key.getKeyCode() == KeyEvent.VK_RIGHT) {

                    for (Human soldier : humans) {
                        if (soldier.isFocus()) {
                            soldier.moveWithArrowKeys("RIGHT");
                            this.simulationContinue = true;
                        }
                    }

                }
            }
            RctaEnvironmentHelper.calculateThreatLevel(this);
            RctaEnvironmentHelper.calculateNutralizedThreatLevel(this);
            visualizer.getVisualCanvas().update();
        }

    }

    public List<Double> getThreatValues() {
        return threatValues;
    }

    public List<Double> getNeutralizeThreatValue() {
        return neutralizeThreatValue;
    }

    // Unimplemented mouseListener methods.
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    // Unimplemented KeyListener methods.
    @Override
    public void keyReleased(KeyEvent arg0) {

    }

    @Override
    public void keyTyped(KeyEvent arg0) {

    }

    public int getExperimentMode() {
        return experimentMode;
    }

    public void setExperimentMode(int experimentMode) {
        this.experimentMode = experimentMode;
    }

    public boolean isSimulationContinue() {
        return simulationContinue;
    }

    public boolean isEnterButton() {
        return enterButton;
    }

    public void setEnterButton(boolean enterButton) {
        this.enterButton = enterButton;
    }

    public boolean isSpaceButton() {
        return spaceButton;
    }

    /**
     * @return the microConflicts
     */
    public List<MicroConflict> getMicroConflicts() {
        return microConflicts;
    }

    /**
     * Replaces the list of microconflicts with the other microconflicts.
     * 
     * @param microConflicts
     *            the microConflicts to set
     */
    public void setMicroConflicts(List<MicroConflict> microConflicts) {
        this.microConflicts = microConflicts;
    }

    /**
     * Adds a micro conflict to the archive if it is not already there
     * 
     * @param microConflict
     */
    public void addMicroConflictToArchive(MicroConflict microConflict) {
        if (!archivedMicroConflicts.contains(microConflict)) {
            archivedMicroConflicts.add(microConflict);
        }
    }

    /**
     * @return the archivedMicroConflicts
     */
    public List<MicroConflict> getArchivedMicroConflicts() {
        return archivedMicroConflicts;
    }

    public SensorNetworkWorld getSensorWorld() {
        return sensorWorld;
    }

    public void setSensorWorld(SensorNetworkWorld sensorWorld) {
        this.sensorWorld = sensorWorld;
    }

    public List<Human> getHumans() {
        return humans;
    }

    public void setHumans(List<Human> humans) {
        this.humans = humans;
    }

    public ArrayList<Location> getPlannedRoute() {
        return plannedRoute;
    }

    public void setPlannedRoute(ArrayList<Location> plannedRoute) {
        this.plannedRoute = plannedRoute;
    }

    public Map<String, Integer> getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(Map<String, Integer> waitingTime) {
        this.waitingTime = waitingTime;
    }

    public HashMap<String, Location> getPermanentJobSittingSpaces() {
        return permanentJobSittingSpaces;
    }

    public void setPermanentJobSittingSpaces(
        HashMap<String, Location> permanentJobSittingSpaces) {
        this.permanentJobSittingSpaces = permanentJobSittingSpaces;
    }

    public ArrayList<Location> getRoboScoutingLocs() {
        return roboScoutingLocs;
    }

    public void setRoboScoutingLocs(ArrayList<Location> roboScoutingLocs) {
        this.roboScoutingLocs = roboScoutingLocs;
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

    public List<Civilian> getCivilians() {
        return this.civilians;
    }

    public void setCivilians(List<Civilian> civilians) {
        this.civilians = civilians;
    }

    public PathPolicy getPathPolicy() {
        return this.pathPolicy;
    }

    public void setPathPolicy(PathPolicy pathPolicy) {
        this.pathPolicy = pathPolicy;
    }

    public EnvironmentModel getEnvironmentModel() {
        return this.emGlobalCost;
    }

    public void setEnvironmentModel(EnvironmentModel environmentModel) {
        this.emGlobalCost = environmentModel;
    }

    public Map<String, PlannedPath> getPathValues() {
        return this.pathValues;
    }

    public void setPathValues(Map<String, PlannedPath> pathValues) {
        this.pathValues = pathValues;
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

    public ScenarioType getSceneType() {
        return this.getSimulationInput().getParameterEnum(ScenarioType.class);
    }

    // public static String getPROP_DENSITY() {
    // return PROP_DENSITY;
    // }
    //
    // public void setPROP_DENSITY(String pROP_DENSITY) {
    // PROP_DENSITY = pROP_DENSITY;
    // }

    public String getPROP_ALERT() {
        return PROP_ALERT;
    }

    public void setPROP_ALERT(String pROP_ALERT) {
        PROP_ALERT = pROP_ALERT;
    }

}
