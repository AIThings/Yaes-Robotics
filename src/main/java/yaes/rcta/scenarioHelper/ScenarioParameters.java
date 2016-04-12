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
public class ScenarioParameters implements constRCTA {

	public static void createIndoorCrowd(SimulationInput sip) {
		sip.setContextClass(RctaContext.class);
		sip.setSimulationClass(RctaSimulationCode.class);
		sip.setParameter(ExperimentMode.NORMAL_OPERATION);
		sip.setParameter(MAP_WIDTH, 500);
		sip.setParameter(MAP_HEIGHT, 500);
		sip.setParameter(MAP_OBSTACLES, "Indoor2.png");
		sip.setParameter(MAP_BACKGROUND, "Indoor-Bkgnd.png");

		// sip.setParameter(HUMAN_WIDTH_SHOULDER, 3.0);
		// sip.setParameter(HUMAN_RADIUS_PERSONAL_SPACE, 4.0);
		// sip.setParameter(HUMAN_DIAMETER_MOVEMENT_CONE, 30.0);
		// sip.setParameter(HUMAN_ANGLE_MOVEMENT_CONE, 30.0);

		sip.setParameter(HUMAN_WIDTH_SHOULDER, 6.0);
		sip.setParameter(HUMAN_RADIUS_PERSONAL_SPACE, 8.0);
		sip.setParameter(HUMAN_DIAMETER_MOVEMENT_CONE, 30.0);
		sip.setParameter(HUMAN_ANGLE_MOVEMENT_CONE, 30.0);

		// sip.setParameter(ROBOT_WIDTH_ROBOT, 3.0);
		// sip.setParameter(ROBOT_RADIUS_PERSONAL_SPACE, 5.0);
		// sip.setParameter(ROBOT_DIAMETER_MOVEMENT_CONE, 20.0);
		// sip.setParameter(ROBOT_ANGLE_MOVEMENT_CONE, 30.0);

		sip.setParameter(ROBOT_WIDTH_ROBOT, 6.0);
		sip.setParameter(ROBOT_RADIUS_PERSONAL_SPACE, 10.0);
		sip.setParameter(ROBOT_DIAMETER_MOVEMENT_CONE, 40.0);
		sip.setParameter(ROBOT_ANGLE_MOVEMENT_CONE, 180.0);

		sip.setParameter(ROBOTS_COUNT, 1);
		/*
		 * Fixed Seed value for Random function
		 */
		sip.setParameter(RCTA_RANDOM_SEED, 5);

		/*
		 * the parameter for civilians population
		 */
		sip.setParameter(CIVILIANS_COUNT, 3);
		sip.setParameter(CIVILIAN_PATH_POLICY, "DSTARLITE");

		/*
		 * the parameter for scenario
		 */
		sip.setParameter(FixedPointsOfInterest.YES);
		sip.setParameter(PathPlanning.DSTARLITE);

		/**
		 * Initial Robot Location on smaller map.
		 */
		// sip.setParameter(ROBOT_X, 100);
		// sip.setParameter(ROBOT_Y, 130);

		// sip.setParameter(ROBOT_X, 50);
		// sip.setParameter(ROBOT_Y, 30);

		if (sip.getParameter(MAP_BACKGROUND).equals("Indoor.png")) {
			// POI's for the outer region
			outerRegion_LocList.put("OuterRegion-POI1", new Location(102, 132));
			outerRegion_LocList.put("OuterRegion-POI2", new Location(186, 150));
			outerRegion_LocList.put("OuterRegion-POI3", new Location(99, 251));
			outerRegion_LocList.put("OuterRegion-POI4", new Location(59, 400));

			// POI's for courtyard (The most busy spot)
			courtLocList.put("CourtYard-POI1", new Location(151, 292));
			courtLocList.put("CourtYard-POI2", new Location(151, 300));
			courtLocList.put("CourtYard-POI3", new Location(184, 363));
			courtLocList.put("CourtYard-POI4", new Location(269, 360));
			courtLocList.put("CourtYard-POI5", new Location(352, 275));
			courtLocList.put("CourtYard-POI6", new Location(266, 170));

			// POI's for the ROOM-B
			room_LocList.put("RoomB-POI1", new Location(343, 111));
			room_LocList.put("RoomB-POI2", new Location(428, 90));
			room_LocList.put("RoomB-POI3", new Location(350, 111));
			room_LocList.put("RoomB-POI4", new Location(266, 85));

			roboSearch_LocList.put("RoboSearch-POI1", new Location(59, 85));
			roboSearch_LocList.put("RoboSearch-POI2", new Location(102, 85));
			roboSearch_LocList.put("RoboSearch-POI3", new Location(160, 85));
			// RoboSearch_LocList.put("RoboSearch-POI4", new Location(59, 132));
			roboSearch_LocList.put("RoboSearch-POI5", new Location(59, 160));
			roboSearch_LocList.put("RoboSearch-POI6", new Location(61, 251));
			roboSearch_LocList.put("RoboSearch-POI7", new Location(65, 300));
			roboSearch_LocList.put("RoboSearch-POI8", new Location(45, 350));
			roboSearch_LocList.put("RoboSearch-POI9", new Location(50, 400));
			roboSearch_LocList.put("RoboSearch-POI10", new Location(102, 140));
			roboSearch_LocList.put("RoboSearch-POI11", new Location(99, 300));
			roboSearch_LocList.put("RoboSearch-POI12", new Location(99, 400));
			// roboSearch_LocList.put("RoboSearch-POI13", new Location(184,
			// 170));
			roboSearch_LocList.put("RoboSearch-POI14", new Location(266, 160));
			roboSearch_LocList.put("RoboSearch-POI15", new Location(184, 343));
			roboSearch_LocList.put("RoboSearch-POI16", new Location(280, 350));
			roboSearch_LocList.put("RoboSearch-POI17", new Location(343, 150));
			roboSearch_LocList.put("RoboSearch-POI18", new Location(343, 251));
			roboSearch_LocList.put("RoboSearch-POI19", new Location(350, 400));
			roboSearch_LocList.put("RoboSearch-POI20", new Location(300, 85));
			roboSearch_LocList.put("RoboSearch-POI21", new Location(400, 90));
			roboSearch_LocList.put("RoboSearch-POI22", new Location(460, 85));
			roboSearch_LocList.put("RoboSearch-POI23", new Location(400, 160));
			roboSearch_LocList.put("RoboSearch-POI24", new Location(450, 251));
			roboSearch_LocList.put("RoboSearch-POI25", new Location(425, 300));
			roboSearch_LocList.put("RoboSearch-POI26", new Location(460, 350));
			roboSearch_LocList.put("RoboSearch-POI27", new Location(460, 400));
			locList.putAll(outerRegion_LocList);
			locList.putAll(courtLocList);
			locList.putAll(room_LocList);
		} else {
			// roboSearch_LocList.put("RoboSearch-POI1", new Location(20,10));
			// roboSearch_LocList.put("RoboSearch-POI1", new Location(20,300));
			// roboSearch_LocList.put("RoboSearch-POI1", new Location(20,450));
			// roboSearch_LocList.put("RoboSearch-POI1", new Location(30,300));
			roboSearch_LocList.put("RoboSearch-POI1", new Location(40, 300));
			// roboSearch_LocList.put("RoboSearch-POI1", new Location(50,300));
			// roboSearch_LocList.put("RoboSearch-POI1", new Location(60,300));
			// roboSearch_LocList.put("RoboSearch-POI1", new Location(450,450));

			// roboSearch_LocList.put("RoboSearch-POI2", new Location(102, 85));
			// roboSearch_LocList.put("RoboSearch-POI3", new Location(160, 85));

			room_LocList.put("RegionA-POI1", new Location(165, 93));
			room_LocList.put("RegionA-POI2", new Location(280, 93));
			room_LocList.put("RegionA-POI3", new Location(380, 93));
			room_LocList.put("RegionA-POI4", new Location(473, 93));

			room_LocList.put("RegionB-POI1", new Location(100, 170));
			room_LocList.put("RegionB-POI2", new Location(170, 170));
			room_LocList.put("RegionB-POI3", new Location(180, 170));
			room_LocList.put("RegionB-POI4", new Location(380, 170));

			room_LocList.put("RegionB-POI5", new Location(380, 260));
			room_LocList.put("RegionB-POI6", new Location(380, 355));

			room_LocList.put("RegionC-POI1", new Location(70, 270));
			room_LocList.put("RegionC-POI2", new Location(240, 270));
			room_LocList.put("RegionC-POI3", new Location(70, 365));
			room_LocList.put("RegionC-POI4", new Location(240, 365));

			// inner region
			room_LocList.put("RegionD-POI1", new Location(460, 155));
			room_LocList.put("RegionD-POI2", new Location(460, 225));
			room_LocList.put("RegionD-POI3", new Location(460, 290));
			room_LocList.put("RegionD-POI4", new Location(460, 365));

			// right section
			// room_LocList.put("RoomB-POI2", new Location(474, 156));
			// room_LocList.put("RoomB-POI3", new Location(474, 248));
			// room_LocList.put("RoomB-POI4", new Location(474, 59));

			//
			anchorLocList.put("AnchorPoint-1", new Location(20, 83));
			anchorLocList.put("AnchorPoint-2", new Location(471, 83));
			anchorLocList.put("AnchorPoint-3", new Location(22, 411));
			anchorLocList.put("AnchorPoint-4", new Location(476, 411));
			anchorLocList.put("AnchorPoint-5", new Location(343, 290));
			// locList.putAll(roboSearch_LocList);
			locList.putAll(room_LocList);
		}
	}

	
	public static void createSimpleMarket(SimulationInput sip){
        sip.setParameter(MAP_WIDTH, 150);
        sip.setParameter(MAP_HEIGHT, 120);
        sip.setParameter(MAP_OBSTACLES, "Scenario_1_Small_2.png");
        sip.setParameter(MAP_BACKGROUND, "Scenario_1_Small_Bkgnd.png");        
        sip.setParameter(RCTA_RANDOM_SEED, 5);
        sip.setParameter(CIVILIANS_COUNT, 10);
        sip.setParameter(CIVILIAN_PATH_POLICY, "DSTARLITE");
        sip.setParameter(FixedPointsOfInterest.YES);
        sip.setParameter(PathPlanning.DSTARLITE);

        
        /**
         * Initial Robot Location on smaller map.
         */
        sip.setParameter(ROBOT_X, 150);
        sip.setParameter(ROBOT_Y, 30);

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
	public static void createSipMarket(SimulationInput sip) {
		sip.setContextClass(RctaContext.class);
		sip.setSimulationClass(RctaSimulationCode.class);

		/*
		 * assume that this is measured in Feet? THIS IS FOR SMALLER MAP For
		 * Larger MAP refer RobotMovementDStar class.
		 */
		sip.setParameter(MAP_WIDTH, 150);
		sip.setParameter(MAP_HEIGHT, 120);
		// sip.setParameter(MAP_OBSTACLES, "Scenario-1-Small-2.png");
		sip.setParameter(MAP_OBSTACLES, "Scenario_1_Small_2.png");
		sip.setParameter(MAP_BACKGROUND, "Scenario_1_Small_Bkgnd.png");

		ScenarioType scenario = sip.getParameterEnum(ScenarioType.class);
		switch (scenario) {
		case CLOSE_PROTECTION_SINGLE_ROBOT:
		case CLOSE_PROTECTION_MULTIPLE_ROBOT:
		case CLOSE_PROTECTION_CONTROL_MODEL:
		case SINGLE_ROBOT_FOLLOW:
		case MULTIPLE_ROBOT_FOLLOW:
		case CROWD_SEEK_VIP:
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
			break;
		case SAME_CROWD_ADAPTIVE:
		case CROWD_SIMULATION:
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
			// Default personal space parameters for human
			sip.setParameter(HUMAN_WIDTH_SHOULDER, 3.0);
			sip.setParameter(HUMAN_RADIUS_PERSONAL_SPACE, 4.0);
			sip.setParameter(HUMAN_DIAMETER_MOVEMENT_CONE, 20.0);
			sip.setParameter(HUMAN_ANGLE_MOVEMENT_CONE, 30.0);

			// Default personal space parameters for Robot
			sip.setParameter(ROBOT_WIDTH_ROBOT, 3.0);
			sip.setParameter(ROBOT_RADIUS_PERSONAL_SPACE, 6.0);
			sip.setParameter(ROBOT_DIAMETER_MOVEMENT_CONE, 24.0);
			sip.setParameter(ROBOT_ANGLE_MOVEMENT_CONE, 40.0);
			break;
		}

		/*
		 * Fixed Seed value for Random function
		 */
		sip.setParameter(RCTA_RANDOM_SEED, 5);

		/*
		 * the parameter for civilians population
		 */
		sip.setParameter(CIVILIANS_COUNT, 10);
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
		sip.setParameter(SOLDIER_Y, 60);
		sip.setParameter(SOLDIER_X_DESTINATION, 60);
		sip.setParameter(SOLDIER_Y_DESTINATION, 60);
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

	/**
	 * Helper method to populate shopping mall scenario images and dimensions
	 * for the environment map
	 * 
	 * @param sip
	 */
	public static void createShoppingMall(SimulationInput sip) {

		sip.setContextClass(RctaContext.class);
		sip.setSimulationClass(RctaSimulationCode.class);

		/*
		 * assume that this is measured in Feet? THIS IS FOR SMALLER MAP For
		 * Larger MAP refer RobotMovementDStar class.
		 */
		sip.setParameter(MAP_WIDTH, 150);
		sip.setParameter(MAP_HEIGHT, 120);
		// sip.setParameter(MAP_OBSTACLES, "Scenario-1-Small-2.png");
		sip.setParameter(MAP_OBSTACLES, "ScenarioMarket2Obstacles2.png");
		sip.setParameter(MAP_BACKGROUND, "ScenarioMarket2Background2.png");
		sip.setParameter(MAP_ENTRANCES, "ScenarioMarket2Entrance.png");
		sip.setParameter(MAP_EXITS, "ScenarioMarket2Exits.png");

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
		sip.setParameter(CIVILIANS_COUNT, 25);
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
		sip.setParameter(SOLDIER_Y, 60);
		sip.setParameter(SOLDIER_X_DESTINATION, 60);
		sip.setParameter(SOLDIER_Y_DESTINATION, 60);
		// Disable = 0, Enable = 1
		sip.setParameter(SOLDIER_TRACE, 1);
		sip.setParameter(SOLDIER_TRACE_TICKS, 0);
		sip.setParameter(SOLDIER_FINAL_DESTINATION, 1);

		// Note: All the location values mention here are accessible in the map
		// and have V=100 as in HSV color values of the pixels in the image. I
		// used paint.net color picker tool to identify these locations.
		entranceLocList.put("ENTRANCE1", new Location(148, 20));
		entranceLocList.put("ENTRANCE2", new Location(148, 74));
		entranceLocList.put("ENTRANCE3", new Location(2, 80));

		exitLocList.put("EXIT1", new Location(148, 39));
		exitLocList.put("EXIT2", new Location(148, 96));
		exitLocList.put("EXIT3", new Location(2, 33));

		locList1.put("ADIDAS", new Location(122, 11));
		locList1.put("HALLMARK", new Location(101, 11));
		locList1.put("BESTBUY", new Location(81, 11));
		locList1.put("APPLESTORE", new Location(60, 11));
		locList1.put("GAP", new Location(38, 11));

		locList2.put("TMOBILE", new Location(119, 107));
		locList2.put("STARBUCKS", new Location(99, 107));
		locList2.put("NIKE", new Location(79, 107));
		locList2.put("PUMA", new Location(58, 107));
		locList2.put("GAMESTOP", new Location(36, 107));
	}

	/**
	 * Helper method to populate office indoor scenario images and dimensions
	 * for the environment map
	 * 
	 * @param sip
	 */
	public static void createOfficeSpace(SimulationInput sip) {

		sip.setContextClass(RctaContext.class);
		sip.setSimulationClass(RctaSimulationCode.class);

		/*
		 * assume that this is measured in Feet? THIS IS FOR SMALLER MAP For
		 * Larger MAP refer RobotMovementDStar class.
		 */
		sip.setParameter(MAP_WIDTH, 150);
		sip.setParameter(MAP_HEIGHT, 120);
		sip.setParameter(MAP_OBSTACLES, "ScenarioOffice1Obstacle2.png");
		sip.setParameter(MAP_BACKGROUND, "ScenarioOffice1Background.png");
		sip.setParameter(MAP_DOORS, "ScenarioOffice1Door.png");
		sip.setParameter(MAP_EXITS, "ScenarioOffice1Exits.png");
		sip.setParameter(MAP_ENTRANCES, "ScenarioOffice1Enterance.png");

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
		sip.setParameter(CIVILIANS_COUNT, 25);
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
		sip.setParameter(SOLDIER_Y, 60);
		sip.setParameter(SOLDIER_X_DESTINATION, 60);
		sip.setParameter(SOLDIER_Y_DESTINATION, 60);
		// Disable = 0, Enable = 1
		sip.setParameter(SOLDIER_TRACE, 1);
		sip.setParameter(SOLDIER_TRACE_TICKS, 0);
		sip.setParameter(SOLDIER_FINAL_DESTINATION, 1);

		// Note: All the location values mention here are accessible in the map
		// and have V=100 as in HSV color values of the pixels in the image. I
		// used paint.net color picker tool to identify these locations.
		entranceLocList.put("ENTRANCE1", new Location(135, 116));

		exitLocList.put("EXIT1", new Location(113, 116));
		exitLocList.put("EXIT2", new Location(5, 60));
		exitLocList.put("EXIT3", new Location(5, 62));
		exitLocList.put("EXIT4", new Location(5, 64));
		exitLocList.put("EXIT5", new Location(5, 66));

		doorLocList.put("Off1Door", new Location(48, 48));
		doorLocList.put("Off2Door", new Location(70, 48));
		doorLocList.put("Off3Door", new Location(47, 75));
		doorLocList.put("Off1Door", new Location(69, 75));

		locList1.put("OFF1DESK1", new Location(11, 13));
		locList1.put("OFF1DESK2", new Location(46, 13));
		locList1.put("OFF1DESK3", new Location(11, 37));
		locList1.put("OFF2DESK1", new Location(69, 12));
		locList1.put("OFF2DESK2", new Location(98, 12));
		locList1.put("OFF2DESK3", new Location(99, 38));
		locList1.put("OFF3DESK1", new Location(10, 87));
		locList1.put("OFF3DESK2", new Location(46, 107));
		locList1.put("OFF3DESK3", new Location(10, 107));
		locList1.put("OFF4DESK1", new Location(82, 97));
		locList1.put("RECEPTION", new Location(130, 15));

	}

	/**
	 * Helper method to populate red carpet scenario images and dimensions for
	 * the environment map
	 * 
	 * @param sip
	 */
	public static void createRedCarpet(SimulationInput sip) {

		sip.setContextClass(RctaContext.class);
		sip.setSimulationClass(RctaSimulationCode.class);
		sip.setParameter(MAP_WIDTH, 150);
		sip.setParameter(MAP_HEIGHT, 120);
		sip.setParameter(MAP_OBSTACLES, "ScenariorRedCarpetObstacles2.png");
		sip.setParameter(MAP_BACKGROUND, "ScenariorRedCarpetBackground.png");
		sip.setParameter(MAP_ENTRANCES, "ScenariorRedCarpetEnterance.png");
		sip.setParameter(MAP_EXITS, "ScenariorRedCarpetExit.png");

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
		sip.setParameter(CIVILIANS_COUNT, 25);
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
		sip.setParameter(SOLDIER_X, 137);
		sip.setParameter(SOLDIER_Y, 59);
		sip.setParameter(SOLDIER_X_DESTINATION, 15);
		sip.setParameter(SOLDIER_Y_DESTINATION, 59);
		// Disable = 0, Enable = 1
		sip.setParameter(SOLDIER_TRACE, 1);
		sip.setParameter(SOLDIER_TRACE_TICKS, 0);
		sip.setParameter(SOLDIER_FINAL_DESTINATION, 1);

		// Note: All the location values mention here are accessible in the map
		// and have V=100 as in HSV color value of a pixel in the image. I
		// used paint.net color picker tool to identify these locations.
		entranceLocList.put("ENTRANCE1", new Location(98, 16));
		entranceLocList.put("ENTRANCE2", new Location(63, 16));
		entranceLocList.put("ENTRANCE3", new Location(28, 16));
		entranceLocList.put("ENTRANCE4", new Location(44, 103));
		entranceLocList.put("ENTRANCE5", new Location(79, 103));
		entranceLocList.put("ENTRANCE6", new Location(110, 103));

		exitLocList.put("EXIT1", new Location(102, 11));
		exitLocList.put("EXIT2", new Location(68, 11));
		exitLocList.put("EXIT3", new Location(32, 11));
		exitLocList.put("EXIT4", new Location(39, 108));
		exitLocList.put("EXIT5", new Location(82, 108));
		exitLocList.put("EXIT6", new Location(116, 108));

	}
}
