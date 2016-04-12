package yaes.rcta.scenarios.Basic_Scenario_03;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.constRCTA;
import yaes.rcta.scenarios.Basic_Scenario_03.agents.Crowd;
import yaes.rcta.scenarios.Basic_Scenario_03.agents.VIPAgent;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;
import yaes.world.physical.path.PlannedPath;

public class AgentBuilder implements constRCTA {

	public static Crowd createAgent(Context context, String name, Location start, Location dest, double speed) {
		SimulationInput sip = context.getSimulationInput();
		Crowd agent = new Crowd(name, start, 0.0);
		// Human soldier = new Human(context, name, start, dest, speed);
		agent.setContext(context);
		agent.setGlobalDestination(dest);
		agent.setPath(new PlannedPath(start, dest));
		agent.setSpeed(speed);
		agent.setWidthShoulder(sip.getParameterDouble(HUMAN_WIDTH_SHOULDER));
		agent.setRadiusPersonalSpace(sip.getParameterDouble(HUMAN_RADIUS_PERSONAL_SPACE));
		agent.setAngleMovementCone(sip.getParameterDouble(HUMAN_ANGLE_MOVEMENT_CONE));
		agent.setDiameterMovementCone(sip.getParameterDouble(HUMAN_DIAMETER_MOVEMENT_CONE));
		agent.createZones();
		agent.setDesiredPath(new Path());
		PlannedPath path = agent.getPath();
		agent.setPath(path);
		if (sip.getParameterInt(SOLDIER_TRACE) == 1) {
			agent.setDisplayTrace(true);
		}
		if (sip.getParameterInt(SOLDIER_TRACE_TICKS) == 1) {
			agent.setDisplayTraceTicks(true);
		}
		agent.setDisplayDestination(true);
		return agent;
	}
	
	public static VIPAgent createVIP(Context context, String name, Location start, Location dest, double speed) {
		SimulationInput sip = context.getSimulationInput();
		VIPAgent agent = new VIPAgent(name, start, 0.0);
		// Human soldier = new Human(context, name, start, dest, speed);
		agent.setContext(context);
		agent.setGlobalDestination(dest);
		agent.setPath(new PlannedPath(start, dest));
		agent.setSpeed(speed);
		agent.setWidthShoulder(sip.getParameterDouble(HUMAN_WIDTH_SHOULDER));
		agent.setRadiusPersonalSpace(sip.getParameterDouble(HUMAN_RADIUS_PERSONAL_SPACE));
		agent.setAngleMovementCone(sip.getParameterDouble(HUMAN_ANGLE_MOVEMENT_CONE));
		agent.setDiameterMovementCone(sip.getParameterDouble(HUMAN_DIAMETER_MOVEMENT_CONE));
		agent.createZones();
		agent.setDesiredPath(new Path());
		PlannedPath path = agent.getPath();
		agent.setPath(path);
		if (sip.getParameterInt(SOLDIER_TRACE) == 1) {
			agent.setDisplayTrace(true);
		}
		if (sip.getParameterInt(SOLDIER_TRACE_TICKS) == 1) {
			agent.setDisplayTraceTicks(true);
		}
		return agent;
	}

	public static void createCrowd(Context context, int count) {
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
			//double speed = (double) context.getRandom().nextInt(3) + 1;
			double speed = 1.0;
			// double mass = (double) context.getRandom().nextInt(10) + 10;
			Crowd civilian = null;

			civilian = createAgent(context, civilianName, initialLoc, initialLoc, speed);

			civilian.setGlobalDestination(civilian.getLocation());
			context.getHumans().add(civilian);

		}
	}
}
