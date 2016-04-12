package yaes.rcta.scenarios.TVR_QLB_Robotfollow;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.constRCTA;
import yaes.world.physical.location.Location;

/**
 * Sets the simulation parameters for the realistic market scenario with non
 * interactive soldier and BigDog movement
 * 
 * @author Taranjeet
 * 
 */
public class ScenarioParameters implements constRCTA {

	public static void createSipMarket(SimulationInput sip) {
		sip.setContextClass(Context.class);
		sip.setSimulationClass(SimulationCode.class);

		/*
		 * assume that this is measured in Feet? THIS IS FOR SMALLER MAP For
		 * Larger MAP refer RobotMovementDStar class.
		 */
		sip.setParameter(MAP_WIDTH, 150);
		sip.setParameter(MAP_HEIGHT, 120);
		// sip.setParameter(MAP_OBSTACLES, "Scenario-1-Small-2.png");
		sip.setParameter(MAP_OBSTACLES, "Scenario_1_Small_2.png");
		sip.setParameter(MAP_BACKGROUND, "Scenario_1_Small_Bkgnd.png");
		// Personal space parameters for human
		sip.setParameter(HUMAN_WIDTH_SHOULDER, 1.6);
		sip.setParameter(HUMAN_RADIUS_PERSONAL_SPACE, 4.0);
		sip.setParameter(HUMAN_DIAMETER_MOVEMENT_CONE, 20.0);
		sip.setParameter(HUMAN_ANGLE_MOVEMENT_CONE, 30.0);

		// Personal space parameters for Robot
		sip.setParameter(ROBOT_WIDTH_ROBOT, 2.0);
		sip.setParameter(ROBOT_RADIUS_PERSONAL_SPACE, 4.0);
		sip.setParameter(ROBOT_DIAMETER_MOVEMENT_CONE, 20.0);
		sip.setParameter(ROBOT_ANGLE_MOVEMENT_CONE, 30.0);

		/*
		 * Fixed Seed value for Random function
		 */
		sip.setParameter(RCTA_RANDOM_SEED, 5);

		/*
		 * the parameter for civilians population
		 */
		// sip.setParameter(CIVILIANS_COUNT, 10);
		sip.setParameter(CIVILIAN_PATH_POLICY, "DSTARLITE");

		/*
		 * the parameter for scenario
		 */
		sip.setParameter(FixedPointsOfInterest.YES);
		sip.setParameter(PathPlanning.DSTARLITE);

		/**
		 * Initial Robot Location on smaller map.
		 */
		sip.setParameter(ROBOT_X, 150);
		sip.setParameter(ROBOT_Y, 30);
		// Disable = 0, Enable = 1
		sip.setParameter(ROBOT_TRACE, 1);
		sip.setParameter(ROBOT_TRACE_TICKS, 1);

		// sip.setParameter(ROBOT_X, 50);
		// sip.setParameter(ROBOT_Y, 30);

		// sip.setParameter(MAX_DIST_SOLDIER_ROBOT, 12);
		// sip.setParameter(MIN_DIST_SOLDIER_ROBOT, 6);
		/**
		 * Initial Soldier Location on smaller map.
		 */
		sip.setParameter(SOLDIER_X, 150);
		sip.setParameter(SOLDIER_Y, 35);
		sip.setParameter(SOLDIER_X_DESTINATION, 42);
		sip.setParameter(SOLDIER_Y_DESTINATION, 35);
		// Disable = 0, Enable = 1
		sip.setParameter(SOLDIER_TRACE, 1);
		sip.setParameter(SOLDIER_TRACE_TICKS, 0);

		locList.put("ELECTRIC", new Location(131, 28));
		locList.put("FOOD", new Location(106, 28));
		locList.put("GROCERY", new Location(81, 28));
		locList.put("HOME GOODS", new Location(57, 28));
		locList.put("PHARMACY", new Location(31, 41));
		locList.put("COSMATICS", new Location(12, 41));
		locList.put("POST OFFICE", new Location(12, 78));
		locList.put("HARDWARE", new Location(31, 78));
		locList.put("OFFICE SUPPLIES", new Location(57, 91));
		locList.put("PRODUCE", new Location(82, 91));
		locList.put("SPORTS", new Location(106, 91));
		locList.put("PIZZA", new Location(131, 91));

	}
}
