package yaes.rcta.scenarioHelper;

import java.io.Serializable;

import yaes.rcta.constRCTA;
import yaes.rcta.movement.MapLocationAccessibility;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;

/**
 * This class creates different environments for different simulations
 * 
 * @author SaadKhan
 *
 */
public class MathHelper implements constRCTA, Serializable {

	private static final long serialVersionUID = 2927271217505318607L;

	

	public static boolean isLocationOccupied(EnvironmentModel environmentModel, Location loc, String mapProp) {

		if (loc.getX() > environmentModel.getXHigh() || loc.getX() < 0 || loc.getY() > environmentModel.getYHigh()
				|| loc.getY() < 0)
			return true;
		double val = (double) environmentModel.getPropertyAt(mapProp, loc.getX(), loc.getY());

		// TextUi.println(val);
		if (val > 0)
			return true;
		return false;

	}

	/**
	 * Return angle between currentLocation and targetLocation in radian or
	 * degree
	 * 
	 * @param currentLocation
	 * @param targetLocation
	 * @param inDegree
	 * @return double
	 */
	public static double getAngle(Location currentLocation, Location targetLocation, boolean inDegree) {
		double angle = Math.atan2(targetLocation.getY() - currentLocation.getY(),
				(targetLocation.getX() - currentLocation.getX()));

		if (inDegree) {
			angle = (double) Math.toDegrees(angle);
			if (angle < 0) {
				angle += 360;
			}
		}

		return angle;
	}

	/**
	 * Check if newlocation is not accessible or obstacle
	 * 
	 * @param environmentIMap
	 * @param mapLocationAccessibility
	 * @param newLocation
	 * @return
	 */
	public static boolean isAccessible(EnvironmentModel environmentIMap,
			MapLocationAccessibility mapLocationAccessibility, Location newLocation) {
		if (mapLocationAccessibility.isAccessible(environmentIMap, newLocation)) {
			return true;
		} else {

			return false;
		}

	}
}
