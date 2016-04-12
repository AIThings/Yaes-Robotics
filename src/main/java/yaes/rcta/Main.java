
package yaes.rcta;

import java.util.ArrayList;
import java.util.List;

import yaes.rcta.constRCTA.ExperimentMode;
import yaes.rcta.constRCTA.ScenarioType;
import yaes.ui.simulationcontrol.SimulationReplayTxt;
import yaes.ui.text.TextUi;

/**
 * This is the main class for running RCTA scenario
 * 
 * @author Taranjeet
 * 
 */
public class Main {
    // TODO: It would be better to reformat the string values to identify which
    // research papers are they contributing towards
    // Scenarios by Taranjeet
    private static final String MENU_SINGLE_CONFLICT = "Single conflict";
    private static final String MENU_SIMPLE_RUN_1 =
            "SIMPLE RUN 1: Agent to goal avoiding obstacles";
    private static final String MENU_SIMPLE_RUN_2 =
            "SIMPLE RUN 2: Agent to goal followed by the robot";
    private static final String MENU_SIMPLE_RUN_3 =
            "INTERACTIVE RUN: Crowd Simulation";
    private static final String MENU_SIMPLE_RUN_4 =
            "SIMPLE RUN 4: Agent to goal followed by the robots";
    private static final String MENU_SIMPLE_RUN_5 =
            "SIMPLE RUN 5: Agent to goal with crowd";
    private static final String MENU_INTERACTIVE_RUN_0 =
            "INTERACTIVE RUN 0: Single Robot Follow";
    private static final String MENU_INTERACTIVE_RUN_1 =
            "INTERACTIVE RUN 1: Multiple Robot Follow";
    private static final String MENU_INTERACTIVE_RUN_2 =
            "INTERACTIVE RUN 2: Single Robot Close protection";
    private static final String MENU_INTERACTIVE_RUN_3 =
            "INTERACTIVE RUN 3: Multiple Robots Close protection";
    private static final String MENU_INTERACTIVE_RUN_4 =
            "INTERACTIVE RUN 4: Crowd seek VIP";
    private static final String MENU_INTERACTIVE_RUN_5 =
            "INTERACTIVE RUN 5: Close protection with control behavior";
    // Scenarios by Saad
    private static final String MENU_ROBOT_SCOUT = "The RoboScout scenario";
    private static final String MENU_ROBOT_SCOUT_GRAPH =
            "The graphs for the roboscout scenario";
    // Scenarios by Taranjeet
    private static final String MENU_REALISTIC_WITH_SOLDIER_SCENARIO =
            "realistic with soldier scenario";
    private static final String MENU_VIEW = "Visualize previous run";
    // Re-enacted scenarios by Saad-Khan
    private static final String MENU_IMITATE =
            "Imitation Learning using ensemble learning (AAMAS-14)";
    private static final String MENU_IMITATE_NEAT =
            "Imitation Learning using neuroevolution (AAAI-14)";
    private static final String MENU_ADAPTIVE_CROWD =
            "Adaptive behavior of the robot for different crowd (based on crowd features)";
    private static final String MENU_SP_MG_VARIABLE_ROBOT_POLICIES = "SimulationPack: MixedGender with Varible Robot Policies";
    private static final String MENU_SP_MG_VARIABLE_POPULATION_STOCHASTIC_ROBOT = "SimulationPack: MixedGender with Varible Population and Stochastic Robot Strategy";
    private static final String MENU_SP_MG_VARIABLE_POPULATION_ADAPTIVE_ROBOT = "SimulationPack: MixedGender with Varible Population and Adaptive Robot Strategy (PAIR-13)";

    //
    public static void main(String[] args)
        throws InstantiationException, IllegalAccessException {

        List<String> menu = new ArrayList<String>();
        menu.add(MENU_SINGLE_CONFLICT);
        menu.add(MENU_SIMPLE_RUN_1);
        menu.add(MENU_SIMPLE_RUN_2);
        menu.add(MENU_SIMPLE_RUN_3);
        menu.add(MENU_SIMPLE_RUN_4);
        menu.add(MENU_SIMPLE_RUN_5);
        menu.add(MENU_INTERACTIVE_RUN_0);
        menu.add(MENU_INTERACTIVE_RUN_1);
        menu.add(MENU_INTERACTIVE_RUN_2);
        menu.add(MENU_INTERACTIVE_RUN_3);
        menu.add(MENU_INTERACTIVE_RUN_4);
        menu.add(MENU_INTERACTIVE_RUN_5);
        menu.add(MENU_ROBOT_SCOUT);
        menu.add(MENU_ROBOT_SCOUT_GRAPH);
        menu.add(MENU_REALISTIC_WITH_SOLDIER_SCENARIO);
        menu.add(MENU_VIEW);
        menu.add(MENU_IMITATE);
        menu.add(MENU_IMITATE_NEAT);
        menu.add(MENU_ADAPTIVE_CROWD);
        menu.add(MENU_SP_MG_VARIABLE_ROBOT_POLICIES);
        menu.add(MENU_SP_MG_VARIABLE_POPULATION_STOCHASTIC_ROBOT);        
        menu.add(MENU_SP_MG_VARIABLE_POPULATION_ADAPTIVE_ROBOT);
        String result =
                TextUi.menu(menu, MENU_SIMPLE_RUN_1, "Choose the sumulation: ");

        switch (result) {
        case MENU_SINGLE_CONFLICT:
        	simRCTA.scenarioType = ScenarioType.SINGLE_CONFLICT;
            simRCTA.doSimpleRun();
            break;
        case MENU_SIMPLE_RUN_1:
        	simRCTA.scenarioType = ScenarioType.OBSTACLE_AVOIDANCE;
            simRCTA.doObstacleAvoidanceSoldier();
            break;
        case MENU_SIMPLE_RUN_2:
        	simRCTA.scenarioType = ScenarioType.ROBOT_FOLLOW_SOLDIER;
            simRCTA.doRobotFollowSoldier();
            break;

        case MENU_SIMPLE_RUN_4:
        	simRCTA.scenarioType = ScenarioType.ROBOT_FOLLOW_SOLDIER_MULTIPLE;
            simRCTA.doMultipleRobotsFollowSoldier();
            break;

        case MENU_SIMPLE_RUN_5:
        	simRCTA.scenarioType = ScenarioType.OBSTACLE_AVOIDANCE_IN_CROWD;
            simRCTA.doObstacleAvoidanceWithCrowd();
            break;
        case MENU_SIMPLE_RUN_3:
            simRCTA.mode = ExperimentMode.PROGRAMMED_RUN;
            simRCTA.scenarioType = ScenarioType.CROWD_SIMULATION;
            simRCTA.doCrowdSimulation();
            break;
        case MENU_INTERACTIVE_RUN_0:
            simRCTA.mode = ExperimentMode.PROGRAMMED_RUN;
            simRCTA.scenarioType = ScenarioType.SINGLE_ROBOT_FOLLOW;
            simRCTA.doSingleRobotFollow();
            break;
        case MENU_INTERACTIVE_RUN_1:
            simRCTA.mode = ExperimentMode.PROGRAMMED_RUN;
            simRCTA.scenarioType = ScenarioType.MULTIPLE_ROBOT_FOLLOW;
            simRCTA.doMultipleRobotFollow();
            break;
        case MENU_INTERACTIVE_RUN_2:
            simRCTA.mode = ExperimentMode.PROGRAMMED_RUN;
            simRCTA.scenarioType = ScenarioType.CLOSE_PROTECTION_SINGLE_ROBOT;
            simRCTA.doCloseProtectionSingleRobot();
            break;
        case MENU_INTERACTIVE_RUN_3:
            simRCTA.mode = ExperimentMode.PROGRAMMED_RUN;
            simRCTA.scenarioType = ScenarioType.CLOSE_PROTECTION_MULTIPLE_ROBOT;
            simRCTA.doCloseProtectionMultipleRobot();
            break;
        case MENU_INTERACTIVE_RUN_4:
            simRCTA.mode = ExperimentMode.PROGRAMMED_RUN;
            simRCTA.scenarioType = ScenarioType.CROWD_SEEK_VIP;
            simRCTA.doCrowdSeekVIP();
            break;
        case MENU_INTERACTIVE_RUN_5:
            simRCTA.mode = ExperimentMode.PROGRAMMED_RUN;
            simRCTA.scenarioType = ScenarioType.CLOSE_PROTECTION_CONTROL_MODEL;
            simRCTA.doCloseProtectionWithControlBehavior();
            break;
        case MENU_REALISTIC_WITH_SOLDIER_SCENARIO:
            simRCTA.doMarketSimulationWithSoldier();
            break;
        case MENU_ROBOT_SCOUT:
            simRCTA.mode = ExperimentMode.NORMAL_OPERATION;
            simRCTA.doScoutRobots();
            break;
        case MENU_ROBOT_SCOUT_GRAPH:
            simRCTA.mode = ExperimentMode.NORMAL_OPERATION;
            simRCTA.runScoutSimulation();
            break;
        case MENU_VIEW:
            SimulationReplayTxt sr = new SimulationReplayTxt(simRCTA.logDir);
            sr.mainLoop();
            break;

        case MENU_IMITATE: // AAMAS-13 IMITATE
            simRCTA.doImitatePlay();
            break;
            
        case MENU_ADAPTIVE_CROWD:
            simRCTA.mode = ExperimentMode.NORMAL_OPERATION;
            simRCTA.scenarioType = ScenarioType.SAME_CROWD_ADAPTIVE;
            simRCTA.doAdaptiveRobotBehavior();
            break;
            
        case MENU_SP_MG_VARIABLE_ROBOT_POLICIES: 
            simRCTA.runVarStrategyMGSim();
            break;

        case MENU_SP_MG_VARIABLE_POPULATION_STOCHASTIC_ROBOT: 
            simRCTA.runVarDayDivisions();
            break;
            
        case MENU_SP_MG_VARIABLE_POPULATION_ADAPTIVE_ROBOT: 
            simRCTA.runVarDayDivisions();
            break;
        /*
         * case MENU_IMITATE_NEAT: //AAAI-14 NEAT-IMITATE
         * simRCTA.doImitateNEATPlay(); break;
         */ default:
            break;
        }

    }

}
