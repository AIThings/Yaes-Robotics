package yaes.rcta.scenarios.FixedFormationRobotFollow.agents;

import yaes.rcta.movement.MapLocationAccessibility;
import yaes.rcta.scenarioHelper.MathHelper;
import yaes.rcta.scenarios.FixedFormationRobotFollow.Context;
import yaes.world.physical.location.Location;

/**
 * This is the helper class for the robot. This class implements different
 * functions that would be used by the robot in various scenarios
 * 
 * @author SaadKhan
 *
 */
public class RobotHelper {

    /**
     * Find the closest location around the desired location near VIP if desired
     * location not available directly; (This should be preferred toward higher
     * threat civilian: Not Implement this fact yet) .
     * 
     * @param targetLocation
     *            : Location of Soldier/VIP
     * @param desiredLocation
     *            : Location of robot to take position
     * @param minimumDistanceFromVIP
     * @return best available location
     */
    public static Location findNearestLocation(Context context,
        Location targetLocation, Location desiredLocation,
        double minimumDistanceFromVIP) {
        double desiredAngleFromVIP = MathHelper
                .getAngle(targetLocation, desiredLocation, false);

        double radius = minimumDistanceFromVIP;
        double angle = desiredAngleFromVIP;
        int counter = 0;
        while (counter < 180) {
            // new location in the clockwise direction by 1 degree
            double newXC = targetLocation.getX()
                    + (double) radius * Math.cos(angle + counter * 0.017);
            // This is subtracting because screen y positive is downward.
            double newYC = targetLocation.getY()
                    - (double) radius * Math.sin(angle + counter * 0.017);
            // new location in the anti-clockwise direction by 1 degree
            double newXA = targetLocation.getX()
                    + (double) radius * Math.cos(angle - counter * 0.017);
            // This is subtracting because screen y positive is downward.
            double newYA = targetLocation.getY()
                    - (double) radius * Math.sin(angle - counter * 0.017);

            if (MathHelper.isAccessible(
                    context.getEnvironmentModel(),
                    new MapLocationAccessibility(),
                    new Location(newXC, newYC))) {
                // See if new location is available then compare with other
                // location
                return new Location(newXC, newYC);

            } else if (MathHelper.isAccessible(
                    context.getEnvironmentModel(),
                    new MapLocationAccessibility(),
                    new Location(newXA, newYA))) {
                return new Location(newXA, newYA);
            }
            counter += 5;
        }
        return new Location(0.0, 0.0);

    }
}
