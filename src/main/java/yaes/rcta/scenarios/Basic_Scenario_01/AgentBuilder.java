package yaes.rcta.scenarios.Basic_Scenario_01;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.constRCTA;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;
import yaes.world.physical.path.PlannedPath;

/**
 * Helper class to code agent features
 * 
 * @author Taranjeet
 *
 */
public class AgentBuilder implements constRCTA {

	public static Human createSoldier(Context context, String name, Location start, Location dest, double speed) {
		SimulationInput sip = context.getSimulationInput();
		Human vip = new Human(name, start, 0.0);
		// Human soldier = new Human(context, name, start, dest, speed);
		vip.setContext(context);
		vip.setGlobalDestination(dest);
		vip.setPath(new PlannedPath(start, dest));
		vip.setSpeed(speed);
		vip.setWidthShoulder(sip.getParameterDouble(HUMAN_WIDTH_SHOULDER));
		vip.setRadiusPersonalSpace(sip.getParameterDouble(HUMAN_RADIUS_PERSONAL_SPACE));
		vip.setAngleMovementCone(sip.getParameterDouble(HUMAN_ANGLE_MOVEMENT_CONE));
		vip.setDiameterMovementCone(sip.getParameterDouble(HUMAN_DIAMETER_MOVEMENT_CONE));
		vip.createZones();
		vip.setDesiredPath(new Path());
		PlannedPath path = vip.getPath();
		vip.setPath(path);
		if (sip.getParameterInt(SHOW_TRACE) == 1) {
			vip.setDisplayTrace(true);
		}
		if (sip.getParameterInt(SHOW_TRACE_TICKS) == 1) {
			vip.setDisplayTraceTicks(true);
		}
		return vip;
	}
}
