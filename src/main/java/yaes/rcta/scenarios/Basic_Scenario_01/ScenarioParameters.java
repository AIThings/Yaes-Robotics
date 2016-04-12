package yaes.rcta.scenarios.Basic_Scenario_01;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.constRCTA;

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
		/*
		 * Fixed Seed value for Random function
		 */
		sip.setParameter(RCTA_RANDOM_SEED, 5);
		/**
		 * Initial Soldier Location on smaller map.
		 */
		sip.setParameter(START_X, 150);
		sip.setParameter(START_Y, 35);
		sip.setParameter(DESTINATION_X, 42);
		sip.setParameter(DESTINATION_Y, 35);
		// Disable = 0, Enable = 1
		sip.setParameter(SHOW_TRACE, 1);
		sip.setParameter(SHOW_TRACE_TICKS, 0);

	}
}
