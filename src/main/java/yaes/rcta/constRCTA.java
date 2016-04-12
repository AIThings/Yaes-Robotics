package yaes.rcta;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import yaes.world.physical.location.Location;

/**
 * The various constants, especially input and output paramete0rs of the RCTA
 * project simulation
 * 
 */
public interface constRCTA {
    // Creation of required directories for storing log, output and graphs
    public final static File logDir = new File("data/log");
    public final static File outputDir = new File("data/output");
    public final static File graphDir = new File("data/graphs");

    public static final String MAP_BACKGROUND = "MapBackground";
    public static final String MAP_OBSTACLES = "MapObstacles";
    public static final String MAP_ENTRANCES = "MapEntrances";
    public static final String MAP_EXITS = "MapExits";
    public static final String MAP_DOORS = "MapDoors";

    public static final String CIVILIAN_PATH_POLICY = "CivilianPathPolicy";
    public static final String CIVILIANS_COUNT = "CivilianCount";
    public static final String ANGLE_DIFF = "AngleDiff";
    public static final String TIME_TO_DESTINATION = "TimeToDestination";

    // Simulation environments attributes
    public static final String SIZE_OF_AREA = "SizeOfImageArea";
    public static final String PIXEL_PER_FEET = "PixelPerFeet";
    public static final String MAP_HEIGHT = "MapHeight";
    public static final String MAP_WIDTH = "MapWidth";
    
    // Metric to be measured and plotted
    public static final String THREAT_VALUE = "threatValues";
    public static final String NEUTRALIZE_THREAT_VALUE = "neutralizeThreatValue";

    /**
     * Defines the class of the micro-conflict strategy of the civilian
     */
    public enum CivilianMicroConfictStrategy {
        STOCHASTIC, INTERACTIVE
    }

    // define the microconflict strategy used by the robot
    public enum RobotMicroConfictStrategy {
        STOCHASTIC,
        ADAPTIVE_STOCHASTIC,
        CLASSIFIER,
        INTERACTIVE,
        IMITATE,
        PMC_ADAPTIVE,
        IMITATE_NEAT,
        MIXED_STRATEGY
    }

    public enum ExperimentMode {
        NORMAL_OPERATION, PROGRAMMED_RUN, MANUAL_RUN

    }

    public enum ExperimentConfiguration {
        STATIC_CIVILIANS,
        INDIRECTION_CIVILIAN_MOVEMENT,
        OUTDIRECTION_CIVILIAN_MOVEMENT,
        RANDOM_CIVILIAN_MOVEMENT,
        CROWD_SEEK_VIP_MOVEMENT,
        CROWD_MOVEMENT
    }

    public static final String RCTA_RANDOM_SEED = "RandomSeed";

    public enum ScenarioType {
        DEFAULT,
        OBSTACLE_AVOIDANCE,
        DSTAR_LITE_SIMPLE,
        OBSTACLE_AVOIDANCE_IN_CROWD,
        ROBOT_FOLLOW_SOLDIER,
        ROBOT_FOLLOW_SOLDIER_MULTIPLE,
        // REALISTIC_WITH_INTERACTIVE_SOLDIER,
        CROWD_SIMULATION,
        SINGLE_ROBOT_FOLLOW,
        MULTIPLE_ROBOT_FOLLOW,
        CLOSE_PROTECTION_SINGLE_ROBOT,
        CLOSE_PROTECTION_MULTIPLE_ROBOT,
        CLOSE_PROTECTION_CONTROL_MODEL,
        CROWD_SEEK_VIP,
        REALISTIC_WITH_ROBOT_SCENARIO,
        // SCOUTING ROBOTS
        ROBOT_SCOUT,
        // GAMETHEORY
        REALISTIC,
        SINGLE_CONFLICT,
        // AAMAS-14
        IMITATION,
        // The situation in which the robot has to adapt itself by learning from
        // skills of each individual
        SAME_CROWD_ADAPTIVE
    }

    public enum CrowdScenarioType {
        SHOPPING_MALL, OFFICE_SPACE, RED_CARPET
    }

    /**
     * Default Orientation for robots around soldier/VIP
     * 
     * @author Taranjeet
     *
     */
    public enum DefaultOrientation {

        BEHIND_LEFT,
        BEHIND_RIGHT,
        FRONT_RIGHT,
        FRONT_LEFT,
        BEHIND,
        FRONT,
        LEFT,
        RIGHT

    }

    public enum KeyPressed{
    	KEY_UP, KEY_DOWN, KEY_LEFT, KEY_RIGHT, KEY_ESC
    }
    public enum PathPlanning {
        ASTAR, ASTARNEW, DSTARLITE, NONE
    }

    public enum FixedPointsOfInterest {
        YES, NO
    }

    public enum VisualDisplay {
        YES, NO
    }
    public enum WriteOutput {
        YES, NO
    }
    public enum OfficeDesignation {
        Perm, Temp
    }

    // Remember that the below distribution of angles is considered
    // counterclockwise
    public HashMap<String, Double> mixedGameMoves =
            new HashMap<String, Double>() {
                private static final long serialVersionUID =
                        -7436167369087873163L;

                {
                    put("N", 0.0);
                    put("E", 270.0);
                    put("W", 90.0);
                    put("NE", 315.0);
                    put("NW", 45.0);
                }
            };

    public static final String MASS = "mass";
    public static final String MAX_VELOCITY = "maxVelocity";
    public static final String SPEED = "speed";
    public static final String ORIENTATION = "orientation";
    public static final String TARGET = "target";
    public static final String MOVEMENT_CONE = "movementConeAngle";
    public static final String MOVEMENT_DIAMETER = "diameterMovementCone";
    public static final String PERSONAL_SPACE_RADIUS = "personalSpaceRadius";
    public static final String SHOULDER_WIDTH = "shoulderWidth";
    public static final String MOVEMENT_TRACE = "movementTrace";
    public static final String MOVEMENT_TRACE_TICKS = "movementTraceTicks";
    public static final String MIN_DISTANCE = "minDist";
    public static final String MAX_DISTANCE = "maxDist";

    public static final String ROBOT_X = "coordinateX";
    public static final String ROBOT_Y = "coordinateY";
    public static final String ROBOTS_COUNT = "robotsCount";
    public static final String ROBOT_TRACE = "robotMovementTrace";
    public static final String ROBOT_TRACE_TICKS = "robotMovementTraceTicks";

    public static final String ROBOT_ANGLE_MOVEMENT_CONE =
            "RobotAngleMovementCone";
    public static final String ROBOT_WIDTH_ROBOT = "RobotWidthRobot";
    public static final String ROBOT_RADIUS_PERSONAL_SPACE =
            "RobotRadiusPersonalSpace";
    public static final String ROBOT_DIAMETER_MOVEMENT_CONE =
            "RobotDiameterMovementCone";

    public static final String Y_OFFSET = "SoldierLocationY offset";
    public static final String SOLDIER_X = "SoldierLocationX";
    public static final String SOLDIER_Y = "SoldierLocationY";
    public static final String SOLDIER_X_DESTINATION = "SoldierDestinationX";
    public static final String SOLDIER_Y_DESTINATION = "SoldierDestinationY";
    public static final String SOLDIER_TRACE = "SoldierMovementTrace";
    public static final String SOLDIER_TRACE_TICKS =
            "SoldierMovementTraceTicks";
    public static final String SOLDIER_FINAL_DESTINATION =
            "SoldierFinalDestination";

    public static final String START_X = "StartX";
    public static final String START_Y = "StartY";
    public static final String DESTINATION_X = "DestinationX";
    public static final String DESTINATION_Y = "DestinationY";
    public static final String SHOW_TRACE = "ShowAgentMovementTrace";
    public static final String SHOW_TRACE_TICKS= "ShowAgentMovementTraceTimeSteps";
    
    
    public static final String HUMAN_WIDTH_SHOULDER = "Human";
    public static final String HUMAN_RADIUS_PERSONAL_SPACE =
            "HumanRadiusPersonalSpace";
    public static final String HUMAN_ANGLE_MOVEMENT_CONE =
            "HumanAngleMovementCone";
    public static final String HUMAN_DIAMETER_MOVEMENT_CONE =
            "HumanDiameterMovementCone";

    // GamePlay const
    public static final String CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER =
            "CivilianMicroConflictStrategyParameter";
    public static final String ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER =
            "RobotMicroConflictStrategyParameter";

    // Map Constants
    public static final String ACCESSIBLE = "Accessible";
    public static final String OBSTACLES = "obstacle";
    public static final String SAFE = "Safe";

    public HashMap<String, Location> locList = new HashMap<String, Location>();
    public LinkedHashMap<String, Location> locList1 =
            new LinkedHashMap<String, Location>();
    public LinkedHashMap<String, Location> locList2 =
            new LinkedHashMap<String, Location>();
    public HashMap<String, Location> entranceLocList =
            new HashMap<String, Location>();
    public HashMap<String, Location> exitLocList =
            new HashMap<String, Location>();
    public HashMap<String, Location> doorLocList =
            new HashMap<String, Location>();
    public HashMap<String, Location> courtLocList =
            new HashMap<String, Location>();
    public HashMap<String, Location> outerRegion_LocList =
            new HashMap<String, Location>();
    public HashMap<String, Location> room_LocList =
            new HashMap<String, Location>();
    public HashMap<String, Location> roboSearch_LocList =
            new HashMap<String, Location>();
    public HashMap<String, Location> anchorLocList =
            new HashMap<String, Location>();

    /**
     * How close the robot can get to soldier/VIP to avoid bumping into soldier
     */
    // public static final String MIN_DIST_SOLDIER_ROBOT =
    // "MinFollowingDistanceBetweenSoldierAndRobot";
    /**
     * How far the robot can fall behind soldier/VIP after which it incurs
     * mission cost
     */
    // public static final String MAX_DIST_SOLDIER_ROBOT =
    // "MaxFollowingDistanceBetweenSoldierAndRobot";

    //
    // metrics to be measured and plotted
    //
    public static final String METRICS_SOCIAL_COST = "MetricsSocialCost";
    public static final String METRICS_MAX_SOCIAL_COST = "MetricsMaxSocialCost";
    public static final String METRICS_MISSION_COST = "MetricsMissionCost";
    public static final String METRICS_MICRO_CONFLICTS =
            "MetricsMissionConflicts";
    public static final String METRICS_ACTIVE_MICRO_CONFLICTS =
            "MetricsActiveMicroConflicts";
    public static final String METRICS_GAMES_PLAYED = "MetricsGamesPlayed";
    public static final String METRICS_PLAYED_C = "MetricsPlayedC";
    public static final String METRICS_PLAYED_D = "MetricsPlayedD";

    public static final String MALE_POPULATION = "MalePopulation";
    public static final String YOUNGSTERS_DISTRIBUTION =
            "YoungstersDistribution";
    public static final String CHILDREN_DISTRIBUTION = "ChildrenDistribution";
    public static final String SENIORS_DISTRIBUTION = "SeniorDistribution";

    public enum DayDivision {
        MORNING,
        EVENING,
        NIGHT,
    }
}
