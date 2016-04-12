package yaes.rcta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import yaes.framework.simulation.Simulation;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ParameterSweep.ParameterSweepType;
import yaes.framework.simulation.parametersweep.ParameterSweepHelper;
import yaes.framework.simulation.parametersweep.ScenarioDistinguisher;
import yaes.rcta.constRCTA.CivilianMicroConfictStrategy;
import yaes.rcta.constRCTA.RobotMicroConfictStrategy;
import yaes.rcta.graph.GraphUtil;
import yaes.rcta.graph.ParameterSweeper;
import yaes.rcta.scenarioHelper.RobotMovementDStar;
import yaes.rcta.scenarioHelper.ScenarioParameters;
import yaes.rcta.scenarioHelper.SimulationParameters;
import yaes.ui.text.TextUi;
import yaes.util.FileWritingUtil;

/**
 * This class define the different types of simulations that can be done using
 * the RCTA context
 * 
 * @author Taranjeet
 * 
 */
public class simRCTA implements constRCTA {

    static ExperimentMode mode = ExperimentMode.NORMAL_OPERATION;
    static ScenarioType scenarioType = ScenarioType.DEFAULT;

    /**
     * Runs a simulation of the RCTA stuff. The parameters of the simulation are
     * defined by the default simulation input. We turn off the visualization
     * here, normally this will be visualized after running through the view
     * menu item.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void doSimpleRun()
        throws InstantiationException, IllegalAccessException {
        SimulationInput sip = createSipSingleConflict();
        sip.setParameter(ANGLE_DIFF, 0.5);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);
    }

    private static SimulationInput createSipSingleConflict() {
        SimulationInput sip = createSip();
        sip.setParameter(scenarioType);
        sip.setParameter(FixedPointsOfInterest.NO);
        sip.setParameter(PathPlanning.NONE);
        setPersonalSpaceParameters(sip);
        sip.setParameter(CIVILIANS_COUNT, 1);
        sip.setParameter(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.5);
        return sip;

    }

    /**
     * Sets the parameters for the personal areas of the robots and civilians
     * 
     * @param sip
     */
    private static void setPersonalSpaceParameters(SimulationInput sip) {
        sip.setParameter(HUMAN_WIDTH_SHOULDER, 3.0);
        sip.setParameter(HUMAN_RADIUS_PERSONAL_SPACE, 4.0);
        sip.setParameter(HUMAN_DIAMETER_MOVEMENT_CONE, 20.0);
        sip.setParameter(HUMAN_ANGLE_MOVEMENT_CONE, 30.0);
        sip.setParameter(ROBOT_WIDTH_ROBOT, 3.0);
        sip.setParameter(ROBOT_RADIUS_PERSONAL_SPACE, 6.0);
        sip.setParameter(ROBOT_DIAMETER_MOVEMENT_CONE, 24.0);
        sip.setParameter(ROBOT_ANGLE_MOVEMENT_CONE, 40.0);
    }

    /**
     * The shared part of the simulation input
     * 
     * @return
     */
    private static SimulationInput createSip() {
        SimulationInput sip = new SimulationInput();
        sip.setContextClass(RctaContext.class);
        sip.setSimulationClass(RctaSimulationCode.class);
        sip.setStopTime(200);
        sip.setParameter(VisualDisplay.YES);

        // assume that this is measured in foot?
        sip.setParameter(MAP_HEIGHT, 360);
        sip.setParameter(MAP_WIDTH, 450);
        sip.setParameter(MAP_OBSTACLES, "Market Map.png");
        sip.setParameter(MAP_BACKGROUND, "Market Map Bkgnd.png");

        // the personal space parameters
        setPersonalSpaceParameters(sip);
        sip.setParameter(RobotMicroConfictStrategy.ADAPTIVE_STOCHASTIC);
        sip.setParameter(CivilianMicroConfictStrategy.STOCHASTIC);
        sip.setParameter(RCTA_RANDOM_SEED, 5);
        return sip;
    }

    /**
     * This run the simulation for the marketplace of RCTA with non-interactive
     * partrolling soldier followed by robot
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void doRobotFollowSoldier()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setParameter(scenarioType);
        // set ExperimentMode
        sip.setParameter(mode);
        ScenarioParameters.createSipMarket(sip);
        SimulationParameters.simulationParameter(sip);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context,
                constRCTA.logDir);

    }

    /**
     * This run the simulation for the marketplace of RCTA with non-interactive
     * partrolling soldier followed by multiple robots at particular orientation
     * such as left, right, front, back
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void doMultipleRobotsFollowSoldier()
        throws InstantiationException, IllegalAccessException {
        // Deleting and recreating all the directories before new run
        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setParameter(scenarioType);

        // set ExperimentMode
        sip.setParameter(mode);
        ScenarioParameters.createSipMarket(sip);
        SimulationParameters.simulationParameter(sip);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);

    }

    /**
     * This run the simulation for the marketplace of RCTA with a
     * non-interactive patrolling soldier
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void doMarketSimulationWithSoldier()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setParameter(ScenarioType.REALISTIC_WITH_ROBOT_SCENARIO);
        RobotMovementDStar.createSipMarket(sip);
        SimulationParameters.simulationParameter(sip);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);

    }

    /**
     * This run the simulation for the marketplace of RCTA with a Simple
     * Obstacle Avoidance for non-interactive patrolling soldier
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void doObstacleAvoidanceSoldier()
        throws InstantiationException, IllegalAccessException {

        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setParameter(scenarioType);

        // Set ExperimentMode
        sip.setParameter(mode);

        ScenarioParameters.createSipMarket(sip);
        SimulationParameters.simulationParameter(sip);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);

    }

    public static void doScoutRobots()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setStopTime(1000);
        sip.setParameter(VisualDisplay.YES);
        sip.setParameter(ScenarioType.ROBOT_SCOUT);
        sip.setParameter(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.5);
        ScenarioParameters.createIndoorCrowd(sip);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);
    }

    public static void doObstacleAvoidanceWithCrowd()
        throws InstantiationException, IllegalAccessException {

        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setParameter(scenarioType);

        // set ExperimentMode
        sip.setParameter(mode);
        ScenarioParameters.createSipMarket(sip);
        SimulationParameters.simulationParameter(sip);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);

    }

    /**
     * This methods is used to initialize the parameters required for the
     * adaptive robot scenario
     * 
     * Adaptive Robot Scenario: The robot will adaptive its behavior with the
     * game theoretic strategies used by crowd
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void doAdaptiveRobotBehavior()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setStopTime(100);
        sip.setParameter(VisualDisplay.YES);
        sip.setParameter(ScenarioType.SAME_CROWD_ADAPTIVE);
        sip.setParameter(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.5);
        sip.setParameter(RobotMicroConfictStrategy.STOCHASTIC);
        sip.setParameter(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.9);
        // set crowd parameters
        sip.setParameter(MALE_POPULATION, 0.7);
        sip.setParameter(CHILDREN_DISTRIBUTION, 0.1);
        sip.setParameter(YOUNGSTERS_DISTRIBUTION, 0.2);
        sip.setParameter(SENIORS_DISTRIBUTION, 0.7);

        // set ExperimentMode
        sip.setParameter(mode);
        ScenarioParameters.createSipMarket(sip);
        // set Civilian Count
        sip.setParameter(CIVILIANS_COUNT, 20);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);
    }

    // Single Robot Follow
    public static void doSingleRobotFollow()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        if (mode == ExperimentMode.PROGRAMMED_RUN) {
            // Run for 1 Robot in scenario
            for (int k = 1; k <= 1; k++) {

                // Running experiment for 4 different configuration in
                // See ExperimentConfiguration enum
                for (int j = 1; j < 4; j++) {
                    SimulationInput sip = new SimulationInput();
                    sip.setParameter(mode);
                    sip.setParameter(scenarioType);

                    ScenarioParameters.createSipMarket(sip);
                    SimulationParameters.simulationParameter(sip);

                    // Running this configuration for 10 runs of VIP
                    for (int i = 0; i < 50; i = i + 5) {

                        sip.setParameter(SOLDIER_X, 150);
                        sip.setParameter(SOLDIER_Y, 35 + i);
                        sip.setParameter(SOLDIER_X_DESTINATION, 42);
                        sip.setParameter(SOLDIER_Y_DESTINATION, 35 + i);
                        // Set ExperimentConfiguration parameter in sip.
                        ExperimentConfiguration type =
                                ExperimentConfiguration.values()[j];
                        sip.setParameter(type);
                        RctaContext context = null;
                        context = new RctaContext();
                        context.setEnterButton(true);
                        Simulation.simulate(sip, RctaSimulationCode.class,
                                context, logDir);

                        // Writing to output file
                        File plotFile = new File(outputDir,
                                "Fixed-ThreatLevel" + "-" + type.toString()
                                        + "-" + (int) i / 5 + "-" + k + ".txt");
                        StringBuilder plotData = new StringBuilder();
                        for (double d : context.getThreatValues()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                        plotFile = new File(outputDir,
                                "Fixed-NeutralizeThreatLevel" + "-"
                                        + type.toString() + "-" + (int) i / 5
                                        + "-" + k + ".txt");
                        plotData = new StringBuilder();
                        for (double d : context.getNeutralizeThreatValue()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                    }
                }
            }
        } else if (mode == ExperimentMode.MANUAL_RUN) {
            SimulationInput sip = new SimulationInput();
            sip.setParameter(mode);
            sip.setParameter(scenarioType);
            ScenarioParameters.createSipMarket(sip);
            SimulationParameters.simulationParameter(sip);
            sip.setParameter(SOLDIER_X, 150);
            sip.setParameter(SOLDIER_Y, 35);
            sip.setParameter(SOLDIER_X_DESTINATION, 42);
            sip.setParameter(SOLDIER_Y_DESTINATION, 35);
            sip.setParameter(ExperimentConfiguration.STATIC_CIVILIANS);
            RctaContext context = new RctaContext();
            Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);
        }
        TextUi.println("Postprocessing Complete, System exit.");
        System.exit(0);
    }

    // Close protection simulation with single robot
    public static void doCloseProtectionSingleRobot()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();

        if (mode == ExperimentMode.MANUAL_RUN) {
            SimulationInput sip = new SimulationInput();
            sip.setParameter(mode);
            sip.setParameter(scenarioType);
            ScenarioParameters.createSipMarket(sip);
            SimulationParameters.simulationParameter(sip);
            sip.setParameter(SOLDIER_X, 150);
            sip.setParameter(SOLDIER_Y, 35);
            sip.setParameter(SOLDIER_X_DESTINATION, 42);
            sip.setParameter(SOLDIER_Y_DESTINATION, 35);
            sip.setParameter(ExperimentConfiguration.STATIC_CIVILIANS);
            RctaContext context = new RctaContext();
            Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);

        } else if (mode == ExperimentMode.PROGRAMMED_RUN) {
            // Run for 1 Robot in scenario
            for (int k = 1; k <= 1; k++) {

                // Running experiment for 4 different configuration in
                // See ExperimentConfiguration enum
                for (int j = 0; j < 4; j++) {
                    SimulationInput sip = new SimulationInput();
                    sip.setParameter(mode);
                    sip.setParameter(scenarioType);

                    ScenarioParameters.createSipMarket(sip);
                    SimulationParameters.simulationParameter(sip);

                    // Running this configuration for 10 runs of VIP
                    for (int i = 0; i < 50; i = i + 5) {

                        sip.setParameter(SOLDIER_X, 150);
                        sip.setParameter(SOLDIER_Y, 35 + i);
                        sip.setParameter(SOLDIER_X_DESTINATION, 42);
                        sip.setParameter(SOLDIER_Y_DESTINATION, 35 + i);
                        // Set ExperimentConfiguration parameter in sip.
                        ExperimentConfiguration type =
                                ExperimentConfiguration.values()[j];
                        sip.setParameter(type);
                        RctaContext context = new RctaContext();
                        context.setEnterButton(true);
                        Simulation.simulate(sip, RctaSimulationCode.class,
                                context, logDir);

                        // Writing to output file
                        File plotFile =
                                new File(outputDir,
                                        "ThreatLevel" + "-" + type.toString()
                                                + "-" + (int) i / 5 + "-" + k
                                                + ".txt");
                        StringBuilder plotData = new StringBuilder();
                        for (double d : context.getThreatValues()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                        plotFile = new File(outputDir,
                                "NeutralizeThreatLevel" + "-" + type.toString()
                                        + "-" + (int) i / 5 + "-" + k + ".txt");
                        plotData = new StringBuilder();
                        for (double d : context.getNeutralizeThreatValue()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                    }
                }
            }
        }

        TextUi.println("Postprocessing Complete, System exit.");
        System.exit(0);

    }

    public static void doMultipleRobotFollow()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        if (mode == ExperimentMode.PROGRAMMED_RUN) {
            // Run for 2 and 3 Robot in scenario
            for (int k = 3; k <= 3; k++) {

                // Running experiment for 4 different configuration in
                // ExperimentConfiguration enum
                for (int j = 3; j < 4; j++) {
                    SimulationInput sip = new SimulationInput();
                    sip.setParameter(mode);
                    sip.setParameter(scenarioType);

                    ScenarioParameters.createSipMarket(sip);
                    SimulationParameters.simulationParameter(sip);
                    sip.setParameter(ROBOTS_COUNT, k);
                    // Running this configuration for 10 runs of VIP
                    for (int i = 0; i < 50; i = i + 5) {
                        sip.setParameter(SOLDIER_X, 150);
                        sip.setParameter(SOLDIER_Y, 35 + i);
                        sip.setParameter(SOLDIER_X_DESTINATION, 42);
                        sip.setParameter(SOLDIER_Y_DESTINATION, 35 + i);
                        // Running exactly for 50 sample points for plotting
                        sip.setStopTime(50);

                        // Set ExperimentConfiguration parameter in sip.
                        ExperimentConfiguration type =
                                ExperimentConfiguration.values()[j];
                        sip.setParameter(type);
                        RctaContext context = new RctaContext();
                        context.setEnterButton(true);
                        Simulation.simulate(sip, RctaSimulationCode.class,
                                context, logDir);

                        // Writing to output file
                        File plotFile =
                                new File(outputDir,
                                        "ThreatLevel" + "-" + type.toString()
                                                + "-" + (int) i / 5 + "-" + k
                                                + ".txt");
                        StringBuilder plotData = new StringBuilder();
                        for (double d : context.getThreatValues()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                        plotFile = new File(outputDir,
                                "NeutralizeThreatLevel" + "-" + type.toString()
                                        + "-" + (int) i / 5 + "-" + k + ".txt");
                        plotData = new StringBuilder();
                        for (double d : context.getNeutralizeThreatValue()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                    }
                }
            }
        } else if (mode == ExperimentMode.MANUAL_RUN) {
            SimulationInput sip = new SimulationInput();
            sip.setParameter(mode);
            sip.setParameter(scenarioType);
            ScenarioParameters.createSipMarket(sip);
            SimulationParameters.simulationParameter(sip);
            sip.setParameter(SOLDIER_X, 150);
            sip.setParameter(SOLDIER_Y, 35);
            sip.setParameter(SOLDIER_X_DESTINATION, 42);
            sip.setParameter(SOLDIER_Y_DESTINATION, 35);
            sip.setParameter(ExperimentConfiguration.STATIC_CIVILIANS);
            sip.setParameter(ROBOTS_COUNT, 2);
            RctaContext context = new RctaContext();
            Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);
        }
        TextUi.println("Postprocessing Complete, System exit.");
        System.exit(0);

    }

    // Close protection simulation with multiple robots
    public static void doCloseProtectionMultipleRobot()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        if (mode == ExperimentMode.MANUAL_RUN) {
            SimulationInput sip = new SimulationInput();
            sip.setParameter(mode);
            sip.setParameter(scenarioType);
            ScenarioParameters.createSipMarket(sip);
            SimulationParameters.simulationParameter(sip);
            sip.setParameter(SOLDIER_X, 150);
            sip.setParameter(SOLDIER_Y, 35);
            sip.setParameter(SOLDIER_X_DESTINATION, 42);
            sip.setParameter(SOLDIER_Y_DESTINATION, 35);
            sip.setParameter(ExperimentConfiguration.STATIC_CIVILIANS);
            sip.setParameter(ROBOTS_COUNT, 2);
            RctaContext context = new RctaContext();
            Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);

        } else if (mode == ExperimentMode.PROGRAMMED_RUN) {
            // Run for 2 and 3 Robot in scenario
            for (int k = 3; k <= 3; k++) {

                // Running experiment for 4 different configuration in
                // ExperimentConfiguration enum
                for (int j = 2; j < 4; j++) {
                    SimulationInput sip = new SimulationInput();
                    sip.setParameter(mode);
                    sip.setParameter(scenarioType);

                    ScenarioParameters.createSipMarket(sip);
                    SimulationParameters.simulationParameter(sip);
                    sip.setParameter(ROBOTS_COUNT, k);
                    // Running this configuration for 10 runs of VIP
                    for (int i = 0; i < 50; i = i + 5) {
                        sip.setParameter(SOLDIER_X, 150);
                        sip.setParameter(SOLDIER_Y, 35 + i);
                        sip.setParameter(SOLDIER_X_DESTINATION, 42);
                        sip.setParameter(SOLDIER_Y_DESTINATION, 35 + i);
                        // Running exactly for 50 sample points for plotting
                        sip.setStopTime(50);

                        // Set ExperimentConfiguration parameter in sip.
                        ExperimentConfiguration type =
                                ExperimentConfiguration.values()[j];
                        sip.setParameter(type);
                        RctaContext context = new RctaContext();
                        context.setEnterButton(true);
                        Simulation.simulate(sip, RctaSimulationCode.class,
                                context, logDir);

                        // Writing to output file
                        File plotFile =
                                new File(outputDir,
                                        "ThreatLevel" + "-" + type.toString()
                                                + "-" + (int) i / 5 + "-" + k
                                                + ".txt");
                        StringBuilder plotData = new StringBuilder();
                        for (double d : context.getThreatValues()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                        plotFile = new File(outputDir,
                                "NeutralizeThreatLevel" + "-" + type.toString()
                                        + "-" + (int) i / 5 + "-" + k + ".txt");
                        plotData = new StringBuilder();
                        for (double d : context.getNeutralizeThreatValue()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                    }
                }
            }
        }

        TextUi.println("Postprocessing Complete, System exit.");
        System.exit(0);
    }

    private static final String SHOPPING_MALL = "Scenario: Shopping Mall";
    private static final String OFFICE_SPACE = "Scenario: Office Space";
    private static final String RED_CARPET = "Scenario: Red Carpet";

    public static void doCrowdSimulation()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();

        List<String> menu = new ArrayList<String>();
        menu.add(SHOPPING_MALL);
        menu.add(OFFICE_SPACE);
        menu.add(RED_CARPET);

        String result =
                TextUi.menu(menu, SHOPPING_MALL, "Choose the Crowd Scenario: ");
        SimulationInput sip = new SimulationInput();
        sip.setParameter(mode);
        sip.setParameter(scenarioType);

        switch (result) {
        case SHOPPING_MALL:
            sip.setParameter(CrowdScenarioType.SHOPPING_MALL);
            ScenarioParameters.createShoppingMall(sip);
            break;
        case OFFICE_SPACE:
            sip.setParameter(CrowdScenarioType.OFFICE_SPACE);
            ScenarioParameters.createOfficeSpace(sip);
            break;
        case RED_CARPET:
            sip.setParameter(CrowdScenarioType.RED_CARPET);
            ScenarioParameters.createRedCarpet(sip);
            break;
        default:
            sip.setParameter(CrowdScenarioType.SHOPPING_MALL);
            ScenarioParameters.createShoppingMall(sip);
        }
        SimulationParameters.simulationParameter(sip);
        sip.setParameter(ExperimentConfiguration.CROWD_MOVEMENT);
        RctaContext context = new RctaContext();
        if (mode == ExperimentMode.MANUAL_RUN) {
        } else if (mode == ExperimentMode.PROGRAMMED_RUN) {
            context.setEnterButton(true);
        }
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);
        TextUi.println("Postprocessing Complete, System exit.");
        System.exit(0);

    }

    /**
     * Close protection with motion control behavior of robot, considering
     * steering robot with wheels.
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void doCloseProtectionWithControlBehavior()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        if (mode == ExperimentMode.MANUAL_RUN) {
            SimulationInput sip = new SimulationInput();
            sip.setParameter(mode);
            sip.setParameter(scenarioType);
            ScenarioParameters.createSipMarket(sip);
            SimulationParameters.simulationParameter(sip);
            sip.setParameter(SOLDIER_X, 150);
            sip.setParameter(SOLDIER_Y, 35);
            sip.setParameter(SOLDIER_X_DESTINATION, 42);
            sip.setParameter(SOLDIER_Y_DESTINATION, 35);
            sip.setParameter(ExperimentConfiguration.STATIC_CIVILIANS);
            sip.setParameter(ROBOTS_COUNT, 2);
            RctaContext context = new RctaContext();
            Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);

        } else if (mode == ExperimentMode.PROGRAMMED_RUN) {
            // Run for 2 and 3 Robot in scenario
            for (int k = 1; k <= 3; k++) {

                // Running experiment for 4 different configuration in
                // ExperimentConfiguration enum
                for (int j = 3; j < 4; j++) {
                    SimulationInput sip = new SimulationInput();
                    sip.setParameter(mode);
                    sip.setParameter(scenarioType);

                    ScenarioParameters.createSipMarket(sip);
                    SimulationParameters.simulationParameter(sip);
                    sip.setParameter(ROBOTS_COUNT, k);
                    // Running this configuration for 10 runs of VIP
                    for (int i = 0; i < 50; i = i + 5) {
                        sip.setParameter(SOLDIER_X, 150);
                        sip.setParameter(SOLDIER_Y, 35 + i);
                        sip.setParameter(SOLDIER_X_DESTINATION, 42);
                        sip.setParameter(SOLDIER_Y_DESTINATION, 35 + i);

                        sip.setParameter(ROBOT_X, 150);
                        sip.setParameter(ROBOT_Y, 35 + i);

                        // Running exactly for 50 sample points for plotting
                        sip.setStopTime(50);

                        // Set ExperimentConfiguration parameter in sip.
                        ExperimentConfiguration type =
                                ExperimentConfiguration.values()[j];
                        sip.setParameter(type);
                        RctaContext context = new RctaContext();
                        context.setEnterButton(true);
                        Simulation.simulate(sip, RctaSimulationCode.class,
                                context, logDir);

                        // Writing to output file
                        File plotFile =
                                new File(outputDir,
                                        "ThreatLevel" + "-" + type.toString()
                                                + "-" + (int) i / 5 + "-" + k
                                                + ".txt");
                        StringBuilder plotData = new StringBuilder();
                        for (double d : context.getThreatValues()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                        plotFile = new File(outputDir,
                                "NeutralizeThreatLevel" + "-" + type.toString()
                                        + "-" + (int) i / 5 + "-" + k + ".txt");
                        plotData = new StringBuilder();
                        for (double d : context.getNeutralizeThreatValue()) {
                            plotData.append(d + "\n");
                        }
                        FileWritingUtil.writeToTextFile(plotFile,
                                plotData.toString());

                    }
                }
            }
        }

        TextUi.println("Postprocessing Complete, System exit.");
        System.exit(0);

    }

    /**
     * Steering behavior, crowd agents seek VIP
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void doCrowdSeekVIP()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        if (mode == ExperimentMode.PROGRAMMED_RUN) {
            // Run for 1 Robot in scenario
            for (int k = 1; k <= 1; k++) {

                // Running experiment for 4 different configuration in
                // See ExperimentConfiguration enum
                for (int j = 0; j < 4; j++) {
                    SimulationInput sip = new SimulationInput();
                    sip.setParameter(mode);
                    sip.setParameter(scenarioType);

                    ScenarioParameters.createSipMarket(sip);
                    SimulationParameters.simulationParameter(sip);

                    // Running this configuration for 10 runs of VIP
                    for (int i = 0; i < 50; i = i + 5) {

                        sip.setParameter(SOLDIER_X, 150);
                        sip.setParameter(SOLDIER_Y, 35 + i);
                        sip.setParameter(SOLDIER_X_DESTINATION, 42);
                        sip.setParameter(SOLDIER_Y_DESTINATION, 35 + i);
                        // Set ExperimentConfiguration parameter in sip.
                        // ExperimentConfiguration type =
                        // ExperimentConfiguration
                        // .values()[j];
                        sip.setParameter(
                                ExperimentConfiguration.CROWD_SEEK_VIP_MOVEMENT);
                        RctaContext context = null;
                        context = new RctaContext();
                        context.setEnterButton(true);
                        Simulation.simulate(sip, RctaSimulationCode.class,
                                context, logDir);

                        // Writing to output file
                        // File plotFile = new File(outputDir,
                        // "Fixed-ThreatLevel" + "-"
                        // + type.toString() + "-" + (int) i / 5 + "-" + k
                        // + ".txt");
                        // StringBuilder plotData = new StringBuilder();
                        // for (double d : context.getThreatValues()) {
                        // plotData.append(d + "\n");
                        // }
                        // FileWritingUtil.writeToTextFile(plotFile,
                        // plotData.toString());
                        //
                        // plotFile = new File(outputDir,
                        // "Fixed-NeutralizeThreatLevel"
                        // + "-" + type.toString() + "-" + (int) i / 5 + "-"
                        // + k + ".txt");
                        // plotData = new StringBuilder();
                        // for (double d : context.getNeutralizeThreatValue()) {
                        // plotData.append(d + "\n");
                        // }
                        // FileWritingUtil.writeToTextFile(plotFile,
                        // plotData.toString());

                    }
                }
            }
        } else if (mode == ExperimentMode.MANUAL_RUN) {
            SimulationInput sip = new SimulationInput();
            sip.setParameter(mode);
            sip.setParameter(scenarioType);
            ScenarioParameters.createSipMarket(sip);
            SimulationParameters.simulationParameter(sip);
            sip.setParameter(SOLDIER_X, 150);
            sip.setParameter(SOLDIER_Y, 35);
            sip.setParameter(SOLDIER_X_DESTINATION, 42);
            sip.setParameter(SOLDIER_Y_DESTINATION, 35);
            sip.setParameter(ExperimentConfiguration.STATIC_CIVILIANS);
            RctaContext context = new RctaContext();
            Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);
        }
        TextUi.println("Postprocessing Complete, System exit.");
        System.exit(0);
    }

    /**
     * This method is used to run the imitation play. This method is setup to
     * run the random forest or any other selected classifier
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void doImitatePlay()
        throws InstantiationException, IllegalAccessException {
        RctaResourceHelper.flushFolders();
        // SimulationInput sip = createSipMarket();
        SimulationInput sip = new SimulationInput();
        sip.setParameter(RobotMicroConfictStrategy.IMITATE);
        sip.setParameter(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.1);
        sip.setParameter(VisualDisplay.YES);
        sip.setParameter(CIVILIANS_COUNT, 100);
        sip.setParameter(ScenarioType.IMITATION);
        RctaContext context = new RctaContext();
        Simulation.simulate(sip, RctaSimulationCode.class, context, logDir);
    }

    /**
     * AAMAS-15 Scout Robots This is the simulation pack to run the graphs
     */
    public static void runScoutSimulation() {
        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setStopTime(300);
        sip.setParameter(VisualDisplay.NO);
        sip.setParameter(ScenarioType.ROBOT_SCOUT);
        ScenarioParameters.createIndoorCrowd(sip);
        // sip.setParameter(CivilianMicroConfictStrategy.STOCHASTIC);
        // sip.setParameter(RobotMicroConfictStrategy.IMITATE);
        compareCivilianAgents(sip);
    }

    /**
     * PAIR-13 Runs graphs for variable robot strategy The
     */
    public static void runVarStrategyMGSim() {
        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setStopTime(200);
        sip.setParameter(VisualDisplay.NO);
        sip.setParameter(ScenarioType.SAME_CROWD_ADAPTIVE);
        sip.setParameter(RobotMicroConfictStrategy.STOCHASTIC);
//        sip.setParameter(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.9);
        sip.setParameter(MALE_POPULATION, 0.6);
        sip.setParameter(CHILDREN_DISTRIBUTION, 0.1);
        sip.setParameter(YOUNGSTERS_DISTRIBUTION, 0.7);
        sip.setParameter(SENIORS_DISTRIBUTION, 0.2);
        // set ExperimentMode
        sip.setParameter(mode);
        ScenarioParameters.createSipMarket(sip);

        compareVariableStrategies(sip);

    }

    /**
     * Runs the graph with varying the population statistics based on different
     * divisions of the day
     */
    public static void runVarDayDivisions() {
        RctaResourceHelper.flushFolders();
        SimulationInput sip = new SimulationInput();
        sip.setStopTime(250);
        sip.setParameter(VisualDisplay.NO);

        sip.setParameter(ScenarioType.SAME_CROWD_ADAPTIVE);
        sip.setParameter(RobotMicroConfictStrategy.CLASSIFIER);
        //sip.setParameter(ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER, 0.9);
        // set crowd parameters
        sip.setParameter(MALE_POPULATION, 0.7);
        // set ExperimentMode
        sip.setParameter(mode);
        ScenarioParameters.createSipMarket(sip);
        compareVariableDayDivisions(sip);

    }

    /**
     * AAMAS-15 VariableCrowd Graph Generation
     * 
     * This generates graphs for different behavior of the crowd. The
     * representation of each subgroup within the population is controlled by
     * the probability to defect or corporate
     * 
     * @param model
     */

    private static void compareCivilianAgents(SimulationInput model) {
        ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
        pack.setModel(model);
        // Define the probability of the subpopulation
        ParameterSweep sweepDiscrete =
                ParameterSweeper.getStochasticCivilianTypes();
        pack.addParameterSweep(sweepDiscrete);
        // parameterized sweep
        ParameterSweep sweepCivilians =
                ParameterSweepHelper.generateParameterSweepInteger(
                        "Number of civilians", CIVILIANS_COUNT, 5, 10, 5);
        pack.addParameterSweep(sweepCivilians);

        // ParameterSweep sweepRandom =
        // ParameterSweepHelper.generateParameterSweepInteger("label",
        // RCTA_RANDOM_SEED, 0, 10); // was
        // // 50
        // sweepRandom.setType(ParameterSweepType.Repetition);
        // pack.addParameterSweep(sweepRandom);
        GraphUtil.generateGraphs(pack);

    }

    /**
     * PAIR13-MixedCrowd Graph Generation
     * 
     * This is used to generate the graphs for the scenario where the
     * distribution modality of the population remains same. The robot uses
     * different set of strategies against the same distribution of population
     * over increasing number of civilians
     * 
     * @param model
     */
    private static void compareVariableStrategies(SimulationInput model) {
        ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
        pack.setModel(model);
        ParameterSweep sweepDiscrete = ParameterSweeper.getRobotPolicyTypes();
        pack.addParameterSweep(sweepDiscrete);
        // parameterized sweep
        ParameterSweep sweepCivilians =
                ParameterSweepHelper.generateParameterSweepInteger(
                        "Number of civilians", CIVILIANS_COUNT, 2, 20, 2);
        pack.addParameterSweep(sweepCivilians);
        ParameterSweep sweepRandom =
                ParameterSweepHelper.generateParameterSweepInteger("label",
                        RCTA_RANDOM_SEED, 0, 20); // was 50
        sweepRandom.setType(ParameterSweepType.Repetition);
        pack.addParameterSweep(sweepRandom);
        GraphUtil.generateGraphs(pack);
    }

    /**
     * Generates graph by varying the population statistics for different times
     * of a day
     * 
     * @param model
     */
    private static void compareVariableDayDivisions(SimulationInput model) {
        ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
        pack.setModel(model);
        ParameterSweep sweepDiscrete =
                ParameterSweeper.getVariableDayDivision();
        pack.addParameterSweep(sweepDiscrete);
        // parameterized sweep
        ParameterSweep sweepCivilians =
                ParameterSweepHelper.generateParameterSweepInteger(
                        "Number of civilians", CIVILIANS_COUNT, 2, 30, 2);
        pack.addParameterSweep(sweepCivilians);
        ParameterSweep sweepRandom =
                ParameterSweepHelper.generateParameterSweepInteger("label",
                        RCTA_RANDOM_SEED, 0, 5); // was 50
        sweepRandom.setType(ParameterSweepType.Repetition);
        //pack.addParameterSweep(sweepRandom);
        GraphUtil.generateGraphs(pack);
    }
}
