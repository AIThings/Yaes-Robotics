package yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yaes.rcta.movement.MapLocationAccessibility;
import yaes.rcta.scenarioHelper.MathHelper;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.Context;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.Helper;
import yaes.rcta.util.MathUtility;
import yaes.ui.text.TextUi;
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
     * Quadrant Load balancing algorithm
     */
    public static Location quadrantLoadBalancing(Context context,
        Robot robot, List<Human> soldiers) {
        Location vipLoc = robot.getTargetSoldier().getLocation();
        Location newLocation = new Location(0.0, 0.0);
        double[] quadrants = new double[4];
        Map<Integer, List<Human>> civQuadrants =
                new HashMap<Integer, List<Human>>();
        // initializing quadrant Map
        for (int i = 1; i <= quadrants.length; i++) {
            ArrayList<Human> soldierArray = new ArrayList<Human>();
            civQuadrants.put(i, soldierArray);
        }

        // Sorting all civilians according to their quadrants
        for (Human civilian : soldiers) {
            if (civilian.getName().contains("CIV")) {
                if (Helper.isVisible(context, vipLoc,
                        civilian.getLocation(), 0)) {
                    double deltaX =
                            civilian.getLocation().getX() - vipLoc.getX();
                    double deltaY =
                            vipLoc.getY() - civilian.getLocation().getY();
                    double angleBetween = MathHelper.getAngle(
                            new Location(0.0, 0.0),
                            new Location(deltaX, deltaY), true);
                    // TextUi.println(civilian.getName().toString() +
                    // "at angle: "
                    // + angleBetween);
                    if (angleBetween >= 0 && angleBetween < 90) {
                        civQuadrants.get(1).add(civilian);
                    } else if (angleBetween >= 90 && angleBetween < 180) {
                        civQuadrants.get(2).add(civilian);
                    } else if (angleBetween >= 180 && angleBetween < 270) {
                        civQuadrants.get(3).add(civilian);
                    } else if (angleBetween >= 270 && angleBetween < 360) {
                        civQuadrants.get(4).add(civilian);
                    }
                }
            }
        }

        // calculating ThreatLevel value of each quadrant
        for (int i : civQuadrants.keySet()) {
            quadrants[i - 1] = calculateThreatLevel(robot, civQuadrants.get(i));
        }

        // Go through all the quadrant one by one and check
        // 1) find highest threat quadrant
        // 2) Check if occupied by other robot
        // 3) See if need more robots in that quadrant
        // 4) Move to second highest quadrant if no more robots required in this
        // quadrant
        while (true) {
            double q_max_value = 0.0;
            int q_max = 0;
            for (int i = 0; i < quadrants.length; i++) {
                if (quadrants[i] > q_max_value) {
                    q_max_value = quadrants[i];
                    q_max = i + 1;
                }
            }

            // If no more threat in any quadrant
            // Move to default Orientation
            if (q_max <= 0) {
                if (robot.getOrientation() != null)
                    return robot.setOrientation(robot.getOrientation());
            }
            // Check for the number of civilian in the max threat quadrant
            double workLoad = civQuadrants.get(q_max).size();

            // see if any other robot is handling this quadrant
            for (Robot r : context.getRobots()) {

                if (!(r.getName() == robot.getName())) {
                    int r_q = 0;
                    double deltaX = r.getLocation().getX() - vipLoc.getX();
                    double deltaY = vipLoc.getY() - r.getLocation().getY();
                    double angleBetween = MathHelper.getAngle(
                            new Location(0.0, 0.0),
                            new Location(deltaX, deltaY), true);
                    if (angleBetween >= 0 && angleBetween < 90) {
                        r_q = 1;
                    } else if (angleBetween >= 90 && angleBetween < 180) {
                        r_q = 2;
                    } else if (angleBetween >= 180 && angleBetween < 270) {
                        r_q = 3;
                    } else if (angleBetween >= 270 && angleBetween < 360) {
                        r_q = 4;
                    }

                    if (q_max == r_q) {
                        if (r.getWorkLoad() < workLoad) {
                            workLoad = workLoad - r.getWorkLoad();
                        } else {
                            workLoad = 0;
                            break;
                        }
                    }
                }

            }

            if (workLoad > 0) {
                // Move to new location in this quadrant in case need more robot
                // or no robot present
                newLocation = calculateCiviliansVectors(context, robot, vipLoc,
                        civQuadrants.get(q_max));
                break;
            } else {
                quadrants[q_max - 1] = 0;

            }

        }

        return newLocation;

    }

    /**
     * THREAT VECTOR RESOLUTION
     * 
     * @param vipLoc
     * @param soldiers
     * @return intended location for robot to protect VIP
     */
    public static Location calculateCiviliansVectors(Context context,
        Robot robot, Location vipLoc, List<Human> soldiers) {

        // Parallelogram law of forces
        // calculate all the xVector from VIP to civilians and add them
        // calculate all the yVector from VIP to civilians and add them
        // calculate the final angleVector from VIP to resultant xVector and
        // yVector
        // Return intendedLocation of robot = VIPlocation +
        // minRobotDistance*VectorDirection

        double x = 0.0, y = 0.0;
        for (Human civilian : soldiers) {
            if (civilian.getName().contains("CIV")) {
                if (Helper.isVisible(context, vipLoc,
                        civilian.getLocation(), 0)) {
                    double angleBetween = MathHelper.getAngle(vipLoc,
                            civilian.getLocation(), false);
                    double alpha = MathUtility.chanceOfAttack(vipLoc,
                            civilian.getLocation());
                    x = x + Math.cos(angleBetween) * alpha * (100);
                    y = y + Math.sin(angleBetween) * alpha * (100);

                }
            }

        }

        // Desired location on the visualizer map
        Location desiredLocation =
                new Location(vipLoc.getX() + x, vipLoc.getY() + y);

        Location newLocation = findNearestLocation(context, vipLoc,
                desiredLocation, robot.getMinDistance());
        return newLocation;
    }

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

    
    // Bug0 Algorithm assuming left movement is available else right
    public static Location findNewLocation(Robot robot, Context context, Location currentLocation,
        Location intendedLocation) {
        double movementAngle = MathHelper.getAngle(currentLocation,
                intendedLocation, true);
        // TextUi.println("");
        // TextUi.println("Actual angle: " + movementAngle);
        double temp = movementAngle;
        int counter = 0;
        while (true) {

            if (robot.getSpeed() <= 0.0) {
                return currentLocation;
            }
            double deltaX = robot.getSpeed() * Math.cos(Math.toRadians(temp));
            double deltaY = robot.getSpeed() * Math.sin(Math.toRadians(temp));
            Location newLocation =
                    new Location(robot.getLocation().getX() + deltaX,
                            robot.getLocation().getY() + deltaY);
            // Check If location is accessible
            if (MathHelper.isAccessible(
                    context.getEnvironmentModel(),
                    new MapLocationAccessibility(), newLocation)) {
                TextUi.println("Movement angle:  " + temp);
                return newLocation;
            }
            counter++;
            if (robot.isTurnLeft()) {
                if (counter <= 100) {
                    temp = movementAngle + counter;
                } else {
                    robot.setTurnLeft(false);
                    counter = 0;
                }

            } else {
                if (counter <= 100) {
                    temp = movementAngle - counter;
                } else {
                    robot.setTurnLeft(true);
                    counter = 0;
                }
            }

        }

    }
    
    public static double calculateThreatLevel(Robot robot,
        List<Human> civilians) {
        double threatLevel = 0.0;
        for (Human civilian : civilians) {

            threatLevel = displayThreatProbability(robot, threatLevel,
                    civilian.getLocation());

        }
        return threatLevel;
    }

    public static double displayThreatProbability(Robot robot,
        double threatLevel, Location civLocation) {
        // Get VIP Location
        Location vipLocation = robot.getTargetSoldier().getLocation();
        // calculate chance of attack by civilian at location civLocations
        double chance = MathUtility.chanceOfAttack(civLocation, vipLocation);
        return MathUtility.combineProb(chance, threatLevel);

    }
}
