package yaes.rcta.scenarios.TVR_QLB_Robotfollow;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.PropertiesFile;
import yaes.rcta.movement.SteeringHelper;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents.Human;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents.Robot;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;
import yaes.world.physical.path.PlannedPath;

public class AgentBuilder implements constRCTA {

	public static Human createSoldier(Context context, String name, Location start, Location dest, double speed) {
		SimulationInput sip = context.getSimulationInput();
		Human soldier = new Human(name, start, 0.0);
		// Human soldier = new Human(context, name, start, dest, speed);
		soldier.setContext(context);
		soldier.setGlobalDestination(dest);
		soldier.setPath(new PlannedPath(start, dest));
		soldier.setSpeed(speed);
		soldier.setWidthShoulder(sip.getParameterDouble(HUMAN_WIDTH_SHOULDER));
		soldier.setRadiusPersonalSpace(sip.getParameterDouble(HUMAN_RADIUS_PERSONAL_SPACE));
		soldier.setAngleMovementCone(sip.getParameterDouble(HUMAN_ANGLE_MOVEMENT_CONE));
		soldier.setDiameterMovementCone(sip.getParameterDouble(HUMAN_DIAMETER_MOVEMENT_CONE));
		soldier.createZones();

		// AbstractPathCost pathCost = new PathLength();
		// IHeuristic heuristic = new
		// DistanceHeuristic(soldier.getGlobalDestination());
		PlannedPath path = soldier.getPath();
		// AStarPP aStar = new AStarPP(path, context.getEnvironmentModel(),
		// pathCost, heuristic,
		// new MapLocationAccessibility());
		// aStar.setReturnFirst(true);
		// aStar.planPath(path, context.getEnvironmentModel());
		// TextUi.println(soldier.getPath());
		soldier.setPath(path);
		if (sip.getParameterInt(SOLDIER_TRACE) == 1) {
			soldier.setDisplayTrace(true);
		}
		if (sip.getParameterInt(SOLDIER_TRACE_TICKS) == 1) {
			soldier.setDisplayTraceTicks(true);
		}
		return soldier;
	}

	public static void createSoldier(Context context, int count) {
		SimulationInput sip = context.getSimulationInput();
		for (int i = 0; i != count; i++) {
			String civilianName = "CIV-" + i;

			// look for the initial location
			Location initialLoc = new Location(context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
					context.getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));

			while (Helper.isLocationOccupied(context, initialLoc, 0)) {
				initialLoc = new Location(context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
						context.getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));
			}
			double speed = (double) context.getRandom().nextInt(3) + 1;
			// double mass = (double) context.getRandom().nextInt(10) + 10;
			Human civilian = null;

			civilian = createSoldier(context, civilianName, initialLoc, initialLoc, speed);

			civilian.setGlobalDestination(civilian.getLocation());
			context.getHumans().add(civilian);

		}
	}

	public static Robot createRobot(Context context, String name) {
		SimulationInput sip = context.getSimulationInput();
		PropertiesFile prop = new PropertiesFile();
		prop.readProperties(name);
		double speed = Double.parseDouble(prop.getProperty(SPEED));
		int x = sip.getParameterInt(ROBOT_X);
		int y = sip.getParameterInt(ROBOT_Y);
		TextUi.println("Orientation: " + prop.getProperty(ORIENTATION));
		DefaultOrientation orientation = DefaultOrientation.valueOf(prop.getProperty(ORIENTATION));
		String target = prop.getProperty(TARGET);
		double shoulderWidth = Double.parseDouble(prop.getProperty(SHOULDER_WIDTH));
		double personalSpaceRadius = Double.parseDouble(prop.getProperty(PERSONAL_SPACE_RADIUS));
		double movementConeAngle = Double.parseDouble(prop.getProperty(MOVEMENT_CONE));
		double movementConeDiameter = Double.parseDouble(prop.getProperty(MOVEMENT_DIAMETER));

		int pathTrace = Integer.parseInt(prop.getProperty(MOVEMENT_TRACE));
		int pathTraceTicks = Integer.parseInt(prop.getProperty(MOVEMENT_TRACE_TICKS));
		int minDistance = Integer.parseInt(prop.getProperty(MIN_DISTANCE));
		int maxDistance = Integer.parseInt(prop.getProperty(MAX_DISTANCE));
		int mass = Integer.parseInt(prop.getProperty(MASS));
		double maxVelocity = Double.parseDouble(prop.getProperty(MAX_VELOCITY));
		Robot robot = new Robot(name, new Location(x, y), 0.0);
		robot.setMass(mass);
		robot.setMaxSpeed(maxVelocity);
		robot.setVelocity(new Vector2D(-1, 0));

		robot.setSteering(new SteeringHelper(robot, context.getEnvironmentModel()));
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
		robot.setPath(new PlannedPath(robot.getLocation(), robot.getLocalDestination()));
		robot.setDesiredPath(new Path());
		robot.setActualPath(new Path());

		if (pathTrace == 1) {
			robot.setDisplayTrace(true);
		}
		if (pathTraceTicks == 1) {
			robot.setDisplayTraceTicks(true);
		}

		robot.createZones();

		return robot;
	}

	public static Human getVip(Context context, String target) {
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
}
