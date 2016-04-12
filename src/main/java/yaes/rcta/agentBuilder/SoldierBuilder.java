package yaes.rcta.agentBuilder;

import java.io.Serializable;
import java.util.List;

import yaes.framework.algorithm.search.IHeuristic;
import yaes.framework.simulation.SimulationInput;
import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.Human;
import yaes.rcta.environment.RctaEnvironmentHelper;
import yaes.rcta.movement.MapLocationAccessibility;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.AbstractPathCost;
import yaes.world.physical.path.PathLength;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.pathplanning.AStarPP;
import yaes.world.physical.pathplanning.DistanceHeuristic;

public class SoldierBuilder implements constRCTA, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2310444913419919985L;

	/**
	 * Creating a single soldier agent
	 */
	public static void createSoldier(RctaContext context, double speed, List<Human> humans) {
		SimulationInput sip = context.getSimulationInput();
		Human soldier = createSoldier(context, "VIP",
				new Location(sip.getParameterInt(SOLDIER_X), sip.getParameterInt(SOLDIER_Y)), new Location(0, 60),
				speed);
		humans.add(soldier);
	}

	public static Human createSoldier(RctaContext context, String name, Location start, Location dest, double speed) {
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

		AbstractPathCost pathCost = new PathLength();
		IHeuristic heuristic = new DistanceHeuristic(soldier.getGlobalDestination());
		PlannedPath path = soldier.getPath();
		AStarPP aStar = new AStarPP(path, context.getEnvironmentModel(), pathCost, heuristic,
				new MapLocationAccessibility());
		aStar.setReturnFirst(true);
		aStar.planPath(path, context.getEnvironmentModel());
		TextUi.println(soldier.getPath());
		soldier.setPath(path);
		if (sip.getParameterInt(SOLDIER_TRACE) == 1) {
			soldier.setDisplayTrace(true);
		}
		if (sip.getParameterInt(SOLDIER_TRACE_TICKS) == 1) {
			soldier.setDisplayTraceTicks(true);
		}
		return soldier;
	}

	/**
	 * Create soldier population
	 * 
	 * @param count
	 */
	public static void createSoldier(RctaContext context, int count) {
		SimulationInput sip = context.getSimulationInput();
		for (int i = 0; i != count; i++) {
			String civilianName = "CIV-" + i;

			// look for the initial location
			Location initialLoc = new Location(context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
					context.getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));

			while (RctaEnvironmentHelper.isLocationOccupied(context, initialLoc, 0)) {
				initialLoc = new Location(context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
						context.getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));
			}
			double speed = (double) context.getRandom().nextInt(3) + 1;
			double mass = (double) context.getRandom().nextInt(10) + 10;
			Human civilian = null;
			switch (sip.getParameterEnum(ScenarioType.class)) {
			case SINGLE_ROBOT_FOLLOW:
			case MULTIPLE_ROBOT_FOLLOW:
			case CLOSE_PROTECTION_SINGLE_ROBOT:
			case CLOSE_PROTECTION_MULTIPLE_ROBOT:
			case CLOSE_PROTECTION_CONTROL_MODEL:
				civilian = createSoldier(context, civilianName, initialLoc, initialLoc, speed);
				break;
			case CROWD_SEEK_VIP:
				civilian = HumanBuilder.createHuman(sip, context, civilianName, initialLoc, initialLoc, speed, mass, 3,
						context.getEnvironmentModel());
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
				break;
			}

			civilian.setGlobalDestination(civilian.getLocation());
			context.getHumans().add(civilian);

		}
	}

	/**
	 * Creating a single soldier agent
	 */
	public static void createSoldier(RctaContext context, double speed) {
		SimulationInput sip = context.getSimulationInput();
		Human soldier = createSoldier(context, "VIP",
				new Location(sip.getParameterInt(SOLDIER_X), sip.getParameterInt(SOLDIER_Y)), new Location(0, 60),
				speed);
		context.getHumans().add(soldier);
	}

}
