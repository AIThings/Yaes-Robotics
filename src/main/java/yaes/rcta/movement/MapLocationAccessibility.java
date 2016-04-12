package yaes.rcta.movement;

import yaes.rcta.constRCTA;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;
import yaes.world.physical.map.IAccessibilityChecker;
import yaes.world.physical.map.IMap;

public class MapLocationAccessibility implements IAccessibilityChecker {

	@Override
	public boolean isAccessible(IMap imap, Location location) {
		EnvironmentModel environment = (EnvironmentModel) imap;
		if ((double) environment.getPropertyAt(constRCTA.MAP_OBSTACLES,
				location.getX(), location.getY()) > 0) {
			return false;
		}
		return true;
	}

}
