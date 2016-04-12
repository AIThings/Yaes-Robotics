package yaes.rcta.agentBuilder;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import yaes.framework.agent.ICommunicatingAgent;
import yaes.framework.algorithm.search.IHeuristic;
import yaes.framework.simulation.SimulationInput;
import yaes.rcta.PathPolicy;
import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.Human;
import yaes.rcta.agents.PropertiesFile;
import yaes.rcta.agents.gametheory.gsAdaptiveStochastic;

import yaes.rcta.agents.gametheory.gsInteractive;

import yaes.rcta.agents.gametheory.gsStochastic;
import yaes.rcta.agents.gametheory.iGameStrategy;
import yaes.rcta.agents.gametheory.naiveBayesClassifier.gsClassifier;
import yaes.rcta.agents.robot.RoboScout;
import yaes.rcta.agents.robot.Robot;
import yaes.rcta.constRCTA.RobotMicroConfictStrategy;
import yaes.rcta.movement.MapLocationAccessibility;
import yaes.rcta.movement.SteeringHelper;
import yaes.sensornetwork.model.SensorNode;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.AbstractPathCost;
import yaes.world.physical.path.Path;
import yaes.world.physical.path.PathLength;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.pathplanning.AStarPP;
import yaes.world.physical.pathplanning.DistanceHeuristic;

public class RobotBuilder implements constRCTA, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7462204914622035826L;

    public static Robot createRobot(RctaContext context, String name) {
        SimulationInput sip = context.getSimulationInput();
        PropertiesFile prop = new PropertiesFile();
        prop.readProperties(name);
        double speed = Double.parseDouble(prop.getProperty(SPEED));
        int x = sip.getParameterInt(ROBOT_X);
        int y = sip.getParameterInt(ROBOT_Y);
        DefaultOrientation orientation =
                DefaultOrientation.valueOf(prop.getProperty(ORIENTATION));
        String target = prop.getProperty(TARGET);
        double shoulderWidth =
                Double.parseDouble(prop.getProperty(SHOULDER_WIDTH));
        double personalSpaceRadius =
                Double.parseDouble(prop.getProperty(PERSONAL_SPACE_RADIUS));
        double movementConeAngle =
                Double.parseDouble(prop.getProperty(MOVEMENT_CONE));
        double movementConeDiameter =
                Double.parseDouble(prop.getProperty(MOVEMENT_DIAMETER));

        int pathTrace = Integer.parseInt(prop.getProperty(MOVEMENT_TRACE));
        int pathTraceTicks =
                Integer.parseInt(prop.getProperty(MOVEMENT_TRACE_TICKS));
        int minDistance = Integer.parseInt(prop.getProperty(MIN_DISTANCE));
        int maxDistance = Integer.parseInt(prop.getProperty(MAX_DISTANCE));
        int mass = Integer.parseInt(prop.getProperty(MASS));
        double maxVelocity = Double.parseDouble(prop.getProperty(MAX_VELOCITY));
        Robot robot = new Robot(name, new Location(x, y), 0.0);
        robot.setMass(mass);
        robot.setMaxSpeed(maxVelocity);
        robot.setVelocity(new Vector2D(-1, 0));

        switch (context.getSceneType()) {
        case SAME_CROWD_ADAPTIVE:
            robot = new Robot(name, new Location(138, 66), 0.0);
            Location robotStart = new Location(138, 66);
            Location robotEnd = new Location(12, 41);
            robot.setPath(new PlannedPath(robotStart, robotEnd));

            robot.setMass(mass);
            robot.setMaxSpeed(maxVelocity);
            robot.setVelocity(new Vector2D(-1, 0));
            robot.setSteering(
                    new SteeringHelper(robot, context.getEnvironmentModel()));
            robot.setLocalDestination(new Location(12, 41));
            robot.setTargetSoldier(null);
            // robot.setOrientation(orientation);

            PathPolicy.setAgentAstarPath(robot, context.getEnvironmentModel());
            TextUi.println("The path for the robot is" + robot.getPath());

            robot.setContext(context);
            robot.setSpeed(speed);
            robot.setWidthRobot(shoulderWidth);
            robot.setRadiusPersonalSpace(personalSpaceRadius);
            robot.setAngleMovementCone(movementConeAngle);
            robot.setDiameterMovementCone(movementConeDiameter);
            setMicroconflictStrategy(context);
            robot.setGameStrategy(setMicroconflictStrategy(context));
            break;
        case CROWD_SEEK_VIP:
        case CROWD_SIMULATION:
        case DEFAULT:
        case IMITATION:
        case MULTIPLE_ROBOT_FOLLOW:
        case OBSTACLE_AVOIDANCE:
        case OBSTACLE_AVOIDANCE_IN_CROWD:
        case REALISTIC:
        case REALISTIC_WITH_ROBOT_SCENARIO:
        case ROBOT_FOLLOW_SOLDIER:
        case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
        case ROBOT_SCOUT:
        case SINGLE_CONFLICT:
        case SINGLE_ROBOT_FOLLOW:
        case CLOSE_PROTECTION_MULTIPLE_ROBOT:
        case CLOSE_PROTECTION_SINGLE_ROBOT:
        case CLOSE_PROTECTION_CONTROL_MODEL:
            robot.setSteering(
                    new SteeringHelper(robot, context.getEnvironmentModel()));
            robot.setLocalDestination(new Location(0, y));
            robot.setTargetSoldier(getVip(context, target));
            robot.setOrientation(orientation);

            robot.setStartLocation(new Location(x, y));
            robot.setStartTime(context.getWorld().getTime());
            robot.setContext(context);
            robot.setSpeed(speed);
            robot.setWidthRobot(shoulderWidth);
            robot.setRadiusPersonalSpace(personalSpaceRadius);
            robot.setAngleMovementCone(movementConeAngle);
            robot.setDiameterMovementCone(movementConeDiameter);
            // robot.setOrientation(orientation);
            robot.setMinDistance(minDistance);
            robot.setMaxDistance(maxDistance);
            // robot.setOrientation(orientation);
            robot.setPath(new PlannedPath(robot.getLocation(),
                    robot.getLocalDestination()));
            robot.setDesiredPath(new Path());
            robot.setActualPath(new Path());

            if (pathTrace == 1) {
                robot.setDisplayTrace(true);
            }
            if (pathTraceTicks == 1) {
                robot.setDisplayTraceTicks(true);
            }
            break;
        default:
            break;
        }

        robot.createZones();

        return robot;
    }

    /**
     * Create Robot agents
     */
    public static void createScoutRobots(SimulationInput sip,
        RctaContext context) {
        ArrayList<Location> locations =
                new ArrayList<Location>(roboSearch_LocList.values());
        double speed = 6.0;
        double assumptionC = 0.5;
        Location initialLoc = locations
                .get(context.getRandom().nextInt(roboSearch_LocList.size()));
        for (int i = 0; i != sip.getParameterInt(ROBOTS_COUNT); i++) {

            final SensorNode staticNode = new SensorNode();
            staticNode.setName("S-" + String.format("%02d", i));

            String robotName = "R" + i;
            // look for the initial location
            initialLoc = locations.get(
                    context.getRandom().nextInt(roboSearch_LocList.size()));
            RoboScout scoutAgent = createScoutRobot(sip, context, robotName,
                    initialLoc, speed, assumptionC);
            // staticNode.setAgent((ICommunicatingAgent) scoutAgent);
            scoutAgent.setNode(staticNode);
            context.getSensorWorld().addSensorNode(staticNode);
            context.getSensorWorld().getDirectory()
                    .addAgent((ICommunicatingAgent) scoutAgent);
        }
    }

    /**
     * Creating a single scout robot agent
     * 
     * 1. Set the physical parameters of the robot 2. Set the micro-conflict
     * strategy for collision avoidance 3. Find the best path using D*-lite 4.
     * Schedule the movement of path using TSP
     */
    public static RoboScout createScoutRobot(SimulationInput sip,
        RctaContext context, String name, Location initialLoc, double speed,
        double assumptionC) {
        Robot robot = new RoboScout(name, initialLoc, (new Location(10, 10)),
                context, speed, context.getSensorWorld());
        robot.setWidthRobot(sip.getParameterDouble(ROBOT_WIDTH_ROBOT));
        robot.setRadiusPersonalSpace(
                sip.getParameterDouble(ROBOT_RADIUS_PERSONAL_SPACE));
        robot.setAngleMovementCone(
                sip.getParameterDouble(ROBOT_ANGLE_MOVEMENT_CONE));
        robot.setDiameterMovementCone(
                sip.getParameterDouble(ROBOT_DIAMETER_MOVEMENT_CONE));
        robot.createZones();
        iGameStrategy robotStrategy =
                new gsStochastic(context.getRandom(), assumptionC); // assumptionC
        // =
        // 0.5
        robot.setGameStrategy(robotStrategy);

        AbstractPathCost pathCost = new PathLength();
        IHeuristic heuristic =
                new DistanceHeuristic(robot.getLocalDestination());
        PlannedPath path = robot.getPath();
        AStarPP aStar = new AStarPP(path, context.getEnvironmentModel(),
                pathCost, heuristic, new MapLocationAccessibility());
        aStar.setReturnFirst(true);
        aStar.planPath(path, context.getEnvironmentModel());
        TextUi.println(robot.getPath());
        robot.setPath(path);
        context.getRobots().add(robot);
        ((RoboScout) robot).setTspPath(
                robot.tspPathPlanner(context.getRoboScoutingLocs()));
        return (RoboScout) robot;
    }

    public static Human getVip(RctaContext context, String target) {
        Human vip = null;
        for (Human temp : context.getSoldiers()) {
            if (temp.getName().contains(target)) {
                vip = temp;
            }
        }
        if (vip == null) {
            TextUi.errorPrint("Missing Target Human of Robot agents.");
            System.exit(0);
        }
        return vip;
    }

    private static iGameStrategy setMicroconflictStrategy(RctaContext context) {
        SimulationInput sip = context.getSimulationInput();
        switch (sip.getParameterEnum(RobotMicroConfictStrategy.class)) {
        case STOCHASTIC:
        case PMC_ADAPTIVE: {
            double assumptionC = sip.getParameterDouble(
                    ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER);
            return new gsStochastic(context.getRandom(), assumptionC);
        }
        case ADAPTIVE_STOCHASTIC: {
            double assumptionC = sip.getParameterDouble(
                    ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER);
            return new gsStochastic(context.getRandom(), assumptionC);
        }
        case INTERACTIVE: {
            return new gsInteractive();

        }
		case CLASSIFIER:
			return new gsClassifier(context, 0.0);
		case IMITATE:
		case IMITATE_NEAT:
		case MIXED_STRATEGY:

        default:
            double assumptionC = sip.getParameterDouble(
                    ROBOT_MICRO_CONFLICT_STRATEGY_PARAMETER);
            return new gsStochastic(context.getRandom(), assumptionC);

        }

    }

}
