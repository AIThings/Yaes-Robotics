package yaes.rcta.agentBuilder;

import java.io.Serializable;
import java.util.Random;

import yaes.framework.algorithm.search.IHeuristic;
import yaes.framework.simulation.SimulationInput;
import yaes.rcta.PathPolicy;
import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.civilian.Child;
import yaes.rcta.agents.civilian.Civilian;
import yaes.rcta.agents.civilian.HumanCharacteristics.AgeGroup;
import yaes.rcta.agents.civilian.HumanCharacteristics.ChinType;
import yaes.rcta.agents.civilian.HumanCharacteristics.Gender;
import yaes.rcta.agents.civilian.HumanCharacteristics.HairType;
import yaes.rcta.agents.civilian.Senior;
import yaes.rcta.agents.civilian.Youngster;
import yaes.rcta.agents.gametheory.gsStochastic;
import yaes.rcta.agents.gametheory.iGameStrategy;
import yaes.rcta.environment.RctaEnvironmentHelper;
import yaes.rcta.movement.MapLocationAccessibility;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.AbstractPathCost;
import yaes.world.physical.path.PathLength;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.pathplanning.AStarPP;
import yaes.world.physical.pathplanning.DistanceHeuristic;

public class CivilianBuilder implements constRCTA, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5459001349980397332L;

    /**
     * Creates civilian nodes
     */
    public static void createCivilians(SimulationInput sip,
        RctaContext context) {
        for (int i = 0; i != sip.getParameterInt(CIVILIANS_COUNT); i++) {
            String civilianName = "C" + i;

            // The civilian population will have variance in the strategies for
            // the micro-conflict
            double r = context.getRandom().nextDouble();
            double assumptionC = 0;
            if (r < 0.1) { // 10% crowd is respectful
                assumptionC = 0;
            } else if (r < 0.2) { // 10% crowd is bully
                assumptionC = 1.0;
            } else if (r < 0.7) { // 50% crowd is tight-front
                assumptionC = 0.75;
            } else {
                assumptionC = 0.25; // 20% crowd is tight-after
            }

            // look for the initial location
            Location initialLoc = new Location(
                    context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
                    context.getRandom()
                            .nextInt(sip.getParameterInt(MAP_HEIGHT)));
            while (RctaEnvironmentHelper.isLocationOccupied(context, initialLoc,
                    0))
                initialLoc = new Location(
                        context.getRandom()
                                .nextInt(sip.getParameterInt(MAP_WIDTH)),
                        context.getRandom()
                                .nextInt(sip.getParameterInt(MAP_HEIGHT)));

            double speed = (double) context.getRandom().nextInt(3) + 1;
            Civilian civilian = null;
            switch (sip.getParameterEnum(ScenarioType.class)) {
            case OBSTACLE_AVOIDANCE:
            case OBSTACLE_AVOIDANCE_IN_CROWD:
                civilian = createCivilian(sip, context, civilianName,
                        initialLoc, initialLoc, speed);
                civilian.setLocalDestinationAsRandomPoI();
                break;
            case ROBOT_SCOUT:
            case SINGLE_CONFLICT:
                civilian = createCivilian(sip, context, civilianName,
                        initialLoc, initialLoc, speed);
                civilian.setLocalDestinationAsRandomPoI();
                iGameStrategy civilianStrategy = new gsStochastic(
                        context.getRandom(), sip.getParameterDouble(
                                CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER)); // assumptionC
                                                                              // =
                                                                              // 0.5
                civilian.setGameStrategy(civilianStrategy);
                break;
            case CLOSE_PROTECTION_CONTROL_MODEL:
            case CLOSE_PROTECTION_MULTIPLE_ROBOT:
            case CLOSE_PROTECTION_SINGLE_ROBOT:
            case SAME_CROWD_ADAPTIVE:
                civilian = createCivilian(sip, context, civilianName,
                        initialLoc, initialLoc, speed);
                civilian.setLocalDestinationAsRandomPoI();

                // double assumptionC = sip.getParameterDouble(
                // CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER);
                civilianStrategy =
                        new gsStochastic(context.getRandom(), assumptionC); // assumptionC
                                                                            // =
                                                                            // 0.5
                civilian.setGameStrategy(civilianStrategy);
                civilian.setLocalDestinationAsRandomPoI();
                break;
            case CROWD_SEEK_VIP:
            case CROWD_SIMULATION:
            case DEFAULT:
            case IMITATION:
            case MULTIPLE_ROBOT_FOLLOW:
            case REALISTIC:
            case REALISTIC_WITH_ROBOT_SCENARIO:
            case ROBOT_FOLLOW_SOLDIER:
            case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
            case SINGLE_ROBOT_FOLLOW:
            default:
                civilian = createCivilian(sip, context, civilianName,
                        initialLoc, initialLoc, speed);
                civilian.setLocalDestinationAsRandomPoI();
                break;
            }
            context.getCivilians().add(civilian);
        }
    }

    /**
     * Creates a civilian at a random unoccupied location
     * 
     * @return
     */
    public static Civilian createCivilian(SimulationInput sip,
        RctaContext context, String civilianName, double assumptionC,
        Location civilianStart, Location civilianEnd, double speed) {
        Civilian civilian =
                new Civilian(context, civilianName, civilianStart, speed);
        civilian.setWidthShoulder(sip.getParameterDouble(HUMAN_WIDTH_SHOULDER));
        civilian.setRadiusPersonalSpace(
                sip.getParameterDouble(HUMAN_RADIUS_PERSONAL_SPACE));
        civilian.setAngleMovementCone(
                sip.getParameterDouble(HUMAN_ANGLE_MOVEMENT_CONE));
        civilian.setDiameterMovementCone(
                sip.getParameterDouble(HUMAN_DIAMETER_MOVEMENT_CONE));
        civilian.createZones();
        iGameStrategy civilianStrategy = null;
        switch (context.getSceneType()) {
        case REALISTIC: {
            // path will be created on demand
            // create the micro-conflict strategy of the civilian
            //
            civilianStrategy =
                    new gsStochastic(context.getRandom(), assumptionC);
            break;
        }
        case SINGLE_CONFLICT: {
            // nothing here, leave the path a straight line
            PlannedPath path = new PlannedPath(civilianStart, civilianEnd);
            // path.addLocation(civilianStart);
            // path.addLocation(civilianEnd);
            AbstractPathCost pathCost = new PathLength();
            IHeuristic heuristic = new DistanceHeuristic(civilianEnd);
            AStarPP aStar = new AStarPP(path, context.getEnvironmentModel(),
                    pathCost, heuristic, new MapLocationAccessibility());
            aStar.setReturnFirst(true);
            aStar.planPath(path, context.getEnvironmentModel());

            civilian.setPath(path);
            // create the micro-conflict strategy of the civilian
            //
            civilianStrategy =
                    new gsStochastic(context.getRandom(), assumptionC);
            civilian.setGameStrategy(civilianStrategy);
            break;
        }
        case CLOSE_PROTECTION_CONTROL_MODEL:
        case CLOSE_PROTECTION_MULTIPLE_ROBOT:
        case CLOSE_PROTECTION_SINGLE_ROBOT:
        case SAME_CROWD_ADAPTIVE:
        case CROWD_SEEK_VIP:
        case CROWD_SIMULATION:
        case DEFAULT:
        case IMITATION:
        case MULTIPLE_ROBOT_FOLLOW:
        case OBSTACLE_AVOIDANCE:
        case OBSTACLE_AVOIDANCE_IN_CROWD:
        case REALISTIC_WITH_ROBOT_SCENARIO:
        case ROBOT_FOLLOW_SOLDIER:
        case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
        case ROBOT_SCOUT:
        case SINGLE_ROBOT_FOLLOW:
        default:
            civilianStrategy =
                    new gsStochastic(context.getRandom(), assumptionC);
            civilian.setGameStrategy(civilianStrategy);

            break;
        }
        return civilian;
    }

    /**
     * Creates a civilian at a random unoccupied location
     * 
     * @return a Civilian class
     */
    private static Civilian createCivilian(SimulationInput sip,
        RctaContext context, String civilianName, Location civilianStart,
        Location civilianEnd, double speed) {
        Civilian civilian =
                new Civilian(context, civilianName, civilianStart, speed);
        civilianEnd = civilian.getLocalDestination();
        setCivilianZone(civilian, context);
        // nothing here, leave the path a straight line
        // initialize a path with no movement pattern
        civilian.setPath(new PlannedPath(civilianStart, civilianEnd));
        PathPolicy.setAgentAstarPath(civilian, context.getEnvironmentModel());
        return civilian;
    }

    static int civilianTagCounter = 0;

    public static void createMalePopulation(RctaContext context) {
        SimulationInput sip = context.getSimulationInput();
        Random random = context.getRandom();
        int malePopulation = (int) (sip.getParameterInt(CIVILIANS_COUNT)
                * sip.getParameterDouble(MALE_POPULATION));

        double childDistribution =
                sip.getParameterDouble(CHILDREN_DISTRIBUTION);
        double youngstersDistribution =
                sip.getParameterDouble(YOUNGSTERS_DISTRIBUTION);
        double seniorDistribution =
                sip.getParameterDouble(SENIORS_DISTRIBUTION);

        Gender gender = Gender.MALE;
        String civilianName;

        for (int count = 0; count <= malePopulation
                * childDistribution; count++) {
            civilianName = "C" + civilianTagCounter++;
            Location initialLoc =
                    RctaEnvironmentHelper.getInitialLocation(context);
            // children run and move fast
            double speed = (double) random.nextInt(4) + 1;

            Child maleChild =
                    new Child(context, civilianName, initialLoc, speed);
            setCivilianZone(maleChild, context);
            maleChild.setPath(new PlannedPath(initialLoc,
                    maleChild.getLocalDestination()));
            PathPolicy.setAgentAstarPath(maleChild,
                    context.getEnvironmentModel());

            maleChild.setGender(gender);
            maleChild.setHeight(random.nextInt(3) + 1);
            maleChild.setHairType(HairType.SHORT);
            maleChild.setChinType(ChinType.ROUND);
            maleChild.setLocalDestinationAsRandomPoI();
            // let kids move right-front
            iGameStrategy civilianStrategy = new gsStochastic(random, 0.75);
            maleChild.setGameStrategy(civilianStrategy);
            maleChild.setLocalDestinationAsRandomPoI();
            context.getCivilians().add(maleChild);
        }

        for (int count = 0; count <= malePopulation
                * youngstersDistribution; count++) {
            civilianName = "C" + civilianTagCounter++;
            Location initialLoc =
                    RctaEnvironmentHelper.getInitialLocation(context);
            double speed = (double) random.nextInt(3) + 1;
            Youngster maleYoungster =
                    new Youngster(context, civilianName, initialLoc, speed);
            setCivilianZone(maleYoungster, context);
            maleYoungster.setPath(new PlannedPath(initialLoc,
                    maleYoungster.getLocalDestination()));
            PathPolicy.setAgentAstarPath(maleYoungster,
                    context.getEnvironmentModel());

            maleYoungster.setGender(gender);
            maleYoungster.setHairType(HairType.SHORT);
            maleYoungster.setChinType(ChinType.SQUARE);
            maleYoungster.setHeight(random.nextInt(2) + 5);
            maleYoungster.setLocalDestinationAsRandomPoI();
            // let young men be right-after
            iGameStrategy civilianStrategy = new gsStochastic(random, 0.00);
            maleYoungster.setGameStrategy(civilianStrategy);
            maleYoungster.setLocalDestinationAsRandomPoI();
            context.getCivilians().add(maleYoungster);
        }

        for (int count = 0; count <= malePopulation
                * seniorDistribution; count++) {
            civilianName = "C" + civilianTagCounter++;
            Location initialLoc =
                    RctaEnvironmentHelper.getInitialLocation(context);
            // senior move slow
            double speed = (double) random.nextInt(1) + 1;
            Senior maleSenior =
                    new Senior(context, civilianName, initialLoc, speed);
            setCivilianZone(maleSenior, context);
            maleSenior.setPath(new PlannedPath(initialLoc,
                    maleSenior.getLocalDestination()));
            PathPolicy.setAgentAstarPath(maleSenior,
                    context.getEnvironmentModel());
            maleSenior.setGender(gender);
            maleSenior.setHairType(HairType.BALD);
            maleSenior.setChinType(ChinType.SQUARE);
            maleSenior.setHeight(random.nextInt(3) + 4);
            maleSenior.setLocalDestinationAsRandomPoI();
            // let senior bully
            iGameStrategy civilianStrategy = new gsStochastic(random, 1.0);
            maleSenior.setGameStrategy(civilianStrategy);
            maleSenior.setLocalDestinationAsRandomPoI();
            context.getCivilians().add(maleSenior);
        }

    }

    public static void createFemalePopulation(RctaContext context) {
        SimulationInput sip = context.getSimulationInput();

        int femalePopulation = (int) (sip.getParameterInt(CIVILIANS_COUNT)
                * (1 - sip.getParameterDouble(MALE_POPULATION)));

        double childDistribution =
                sip.getParameterDouble(CHILDREN_DISTRIBUTION);
        double youngstersDistribution =
                sip.getParameterDouble(YOUNGSTERS_DISTRIBUTION);
        double seniorDistribution =
                sip.getParameterDouble(SENIORS_DISTRIBUTION);

        Random random = context.getRandom();
        Gender gender = Gender.FEMALE;
        String civilianName;

        for (int count = 0; count <= femalePopulation
                * childDistribution; count++) {
            civilianName = "C" + civilianTagCounter++;
            Location initialLoc =
                    RctaEnvironmentHelper.getInitialLocation(context);
            // children run and move fast
            double speed = (double) random.nextInt(4) + 1;
            Child femaleChild =
                    new Child(context, civilianName, initialLoc, speed);
            setCivilianZone(femaleChild, context);
            femaleChild.setPath(new PlannedPath(initialLoc,
                    femaleChild.getLocalDestination()));
            PathPolicy.setAgentAstarPath(femaleChild,
                    context.getEnvironmentModel());
            femaleChild.setGender(gender);
            femaleChild.setHairType(HairType.SHORT);
            femaleChild.setChinType(ChinType.ROUND);
            femaleChild.setHeight(random.nextInt(3) + 1);
            femaleChild.setLocalDestinationAsRandomPoI();
            // let kids move right-front
            iGameStrategy civilianStrategy = new gsStochastic(random, 0.75);
            femaleChild.setGameStrategy(civilianStrategy);
            femaleChild.setLocalDestinationAsRandomPoI();
            context.getCivilians().add(femaleChild);
        }

        for (int count = 0; count <= femalePopulation
                * youngstersDistribution; count++) {
            civilianName = "C" + civilianTagCounter++;
            Location initialLoc =
                    RctaEnvironmentHelper.getInitialLocation(context);
            double speed = (double) random.nextInt(3) + 1;
            Youngster femaleYoungster =
                    new Youngster(context, civilianName, initialLoc, speed);
            setCivilianZone(femaleYoungster, context);
            femaleYoungster.setPath(new PlannedPath(initialLoc,
                    femaleYoungster.getLocalDestination()));
            PathPolicy.setAgentAstarPath(femaleYoungster,
                    context.getEnvironmentModel());

            femaleYoungster.setGender(gender);
            femaleYoungster.setHairType(HairType.LONG);
            femaleYoungster.setChinType(ChinType.HEART);

            femaleYoungster.setHeight(random.nextInt(1) + 5);
            femaleYoungster.setLocalDestinationAsRandomPoI();
            // let young men be right-after
            iGameStrategy civilianStrategy = new gsStochastic(random, 0.0);
            femaleYoungster.setGameStrategy(civilianStrategy);
            femaleYoungster.setLocalDestinationAsRandomPoI();
            context.getCivilians().add(femaleYoungster);
        }

        for (int count = 0; count <= femalePopulation
                * seniorDistribution; count++) {
            civilianName = "C" + civilianTagCounter++;
            Location initialLoc =
                    RctaEnvironmentHelper.getInitialLocation(context);
            // senior move slow
            double speed = (double) random.nextInt(1) + 1;
            Senior femaleSenior =
                    new Senior(context, civilianName, initialLoc, speed);
            setCivilianZone(femaleSenior, context);
            femaleSenior.setPath(new PlannedPath(initialLoc,
                    femaleSenior.getLocalDestination()));
            PathPolicy.setAgentAstarPath(femaleSenior,
                    context.getEnvironmentModel());
            femaleSenior.setGender(gender);
            femaleSenior.setHairType(HairType.LONG);
            femaleSenior.setChinType(ChinType.HEART);
            femaleSenior.setHeight(random.nextInt(3) + 3);
            femaleSenior.setLocalDestinationAsRandomPoI();
            // let senior bully
            iGameStrategy civilianStrategy = new gsStochastic(random, 1.0);
            femaleSenior.setGameStrategy(civilianStrategy);
            femaleSenior.setLocalDestinationAsRandomPoI();
            context.getCivilians().add(femaleSenior);
        }

    }

    private static void setCivilianZone(Civilian civilian,
        RctaContext context) {
        SimulationInput sip = context.getSimulationInput();
        civilian.setWidthShoulder(sip.getParameterDouble(HUMAN_WIDTH_SHOULDER));
        civilian.setRadiusPersonalSpace(
                sip.getParameterDouble(HUMAN_RADIUS_PERSONAL_SPACE));
        civilian.setAngleMovementCone(
                sip.getParameterDouble(HUMAN_ANGLE_MOVEMENT_CONE));
        civilian.setDiameterMovementCone(
                sip.getParameterDouble(HUMAN_DIAMETER_MOVEMENT_CONE));
        civilian.createZones();
    }
}