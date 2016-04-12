package yaes.rcta.scenarioHelper;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.RctaContext;
import yaes.rcta.RctaSimulationCode;
import yaes.rcta.constRCTA;
import yaes.world.physical.location.Location;

/**
 * Sets the simulation parameters for the realistic market scenario with non
 * interactive soldier and BigDog movement
 * 
 * @author Taranjeet
 * 
 */
public class RobotMovementDStar implements constRCTA {

	
	public static void createSipMarket(SimulationInput sip) {
		
		sip.setContextClass(RctaContext.class);
		sip.setSimulationClass(RctaSimulationCode.class);
		
		//Total number of time steps to run simulation
		sip.setStopTime(100);
		sip.setParameter(VisualDisplay.NO);

		/*
		 * assume that this is measured in foot? THIS IS FOR LARGER MAP
		 */
		// sip.setParameter(MAP_HEIGHT, 360);
		// sip.setParameter(MAP_WIDTH, 450);
		// sip.setParameter(MAP_OBSTACLES, "Market Map.png");
		// sip.setParameter(MAP_BACKGROUND, "Market Map Bkgnd.png");

		/*
		 * assume that this is measured in foot? THIS IS FOR SMALLER MAP
		 */
		sip.setParameter(MAP_HEIGHT, 120);
		sip.setParameter(MAP_WIDTH, 150);
		sip.setParameter(MAP_OBSTACLES, "Scenario 1 Small.png");
		sip.setParameter(MAP_BACKGROUND, "Scenario 1 Small Bkgnd.png");

		/*
		 * the personal space parameters for human
		 */
		sip.setParameter(HUMAN_WIDTH_SHOULDER, 3.0);
		sip.setParameter(HUMAN_RADIUS_PERSONAL_SPACE, 4.0);
		sip.setParameter(HUMAN_DIAMETER_MOVEMENT_CONE, 20.0);
		sip.setParameter(HUMAN_ANGLE_MOVEMENT_CONE, 30.0);

		/*
		 * the personal space parameters for Robot
		 */
		sip.setParameter(ROBOT_WIDTH_ROBOT, 3.0);
		sip.setParameter(ROBOT_RADIUS_PERSONAL_SPACE, 6.0);
		sip.setParameter(ROBOT_DIAMETER_MOVEMENT_CONE, 24.0);
		sip.setParameter(ROBOT_ANGLE_MOVEMENT_CONE, 40.0);

		/*
		 * Fixed Seed value for Random function
		 */
		sip.setParameter(RCTA_RANDOM_SEED, 5);

		/*
		 * the parameter for civilians population
		 */
		sip.setParameter(CIVILIANS_COUNT, 100);
		sip.setParameter(CIVILIAN_PATH_POLICY, "DSTARLITE");
		
		
		/*
		 * Parameter for scenario
		 */
		sip.setParameter(FixedPointsOfInterest.YES);
		sip.setParameter(PathPlanning.DSTARLITE);
		

		/**
		 * These are for smaller map. Do not delete, we might need to reuse the
		 * smaller map
		 */
		sip.setParameter(ROBOT_X, 150);
		sip.setParameter(ROBOT_Y, 60);

		sip.setParameter(SOLDIER_X, 150);
		sip.setParameter(SOLDIER_Y, 30);

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

		/*
		 * Sets the locations for points of interest For example, electricX and
		 * electricY correspond to the X and Y locations for electric shop for
		 * Larger Map.
		 */
		/*
		 * initializing the static list of locations as defined in the constRCTA
		 * file
		 */

		// sip.setParameter(SOLDIER_X, 426);
		// sip.setParameter(SOLDIER_Y, 30);
		//
		// sip.setParameter(ROBOT_X, 430);
		// sip.setParameter(ROBOT_Y, 150);
		// locList.put("SHOP_1", new Location(117, 28));
		// locList.put("SHOP_2", new Location(142, 28));
		// locList.put("SHOP_3", new Location(167, 28));
		// locList.put("SHOP_4", new Location(193, 28));
		// locList.put("SHOP_5", new Location(218, 28));
		// locList.put("SHOP_6", new Location(242, 28));
		// locList.put("SHOP_7", new Location(267, 28));
		// locList.put("SHOP_8", new Location(292, 28));
		// locList.put("SHOP_9", new Location(317, 28));
		// locList.put("SHOP_10", new Location(342, 28));
		// locList.put("SHOP_11", new Location(367, 28));
		// locList.put("SHOP_12", new Location(392, 28));
		// locList.put("SHOP_13", new Location(416, 28));
		//
		// locList.put("SHOP_14", new Location(12, 42));
		// locList.put("SHOP_15", new Location(32, 42));
		// locList.put("SHOP_16", new Location(52, 42));
		// locList.put("SHOP_17", new Location(71, 42));
		// locList.put("SHOP_18", new Location(92, 42));
		//
		// locList.put("SHOP_19", new Location(12, 319));
		// locList.put("SHOP_20", new Location(32, 319));
		// locList.put("SHOP_21", new Location(52, 319));
		// locList.put("SHOP_22", new Location(71, 319));
		// locList.put("SHOP_23", new Location(92, 319));
		//
		// locList.put("SHOP_24", new Location(117, 332));
		// locList.put("SHOP_25", new Location(142, 332));
		// locList.put("SHOP_26", new Location(167, 332));
		// locList.put("SHOP_27", new Location(193, 332));
		// locList.put("SHOP_28", new Location(218, 332));
		// locList.put("SHOP_29", new Location(242, 332));
		// locList.put("SHOP_30", new Location(267, 332));
		// locList.put("SHOP_31", new Location(292, 332));
		// locList.put("SHOP_32", new Location(317, 332));
		// locList.put("SHOP_33", new Location(342, 332));
		// locList.put("SHOP_34", new Location(367, 332));
		// locList.put("SHOP_35", new Location(392, 332));
		// locList.put("SHOP_36", new Location(416, 332));

		switch (sip.getParameterEnum(ScenarioType.class)) {
		case CLOSE_PROTECTION_SINGLE_ROBOT:
			sip.setParameter(VisualDisplay.YES);
			break;
		case CLOSE_PROTECTION_MULTIPLE_ROBOT:
		case CLOSE_PROTECTION_CONTROL_MODEL:
			sip.setParameter(VisualDisplay.YES);
			break;
		case OBSTACLE_AVOIDANCE:
		case REALISTIC_WITH_ROBOT_SCENARIO:
			sip.setParameter(VisualDisplay.NO);

			break;
		case SAME_CROWD_ADAPTIVE:
		case CROWD_SEEK_VIP:
		case CROWD_SIMULATION:
		case DEFAULT:
		case IMITATION:
		case MULTIPLE_ROBOT_FOLLOW:
		case OBSTACLE_AVOIDANCE_IN_CROWD:
		case REALISTIC:
		case ROBOT_FOLLOW_SOLDIER:
		case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
		case ROBOT_SCOUT:
		case SINGLE_CONFLICT:
		case SINGLE_ROBOT_FOLLOW:

		default:
			throw new Error(
					"Wrong Scenario Type. Only should have Realistic_With_Soldier_Scenario_X");
		}

		

	}
}
