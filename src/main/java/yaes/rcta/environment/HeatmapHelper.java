package yaes.rcta.environment;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import yaes.rcta.agents.zones.AbstractAgentZone;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;

/**
 * This class is a collection of static functions which help maintaining a
 * heatmap
 * 
 * A heatmap is just a variable in one particular EnvironmentModel
 * 
 * @author Saad Ahmad Khan
 * 
 */
public class HeatmapHelper {

    
    /**
     * Adds an agent zone with a certain scale
     * 
     * FIXME: problem here with multiple level adding...
     * 
     * @param em
     * @param az
     * @param propertyName
     * @param scale
     */
    public static void addAgentZone(EnvironmentModel em, AbstractAgentZone az,
            String propertyName, double scale) {
        Rectangle2D.Double bounds =
                (Rectangle2D.Double) az.getShape().getBounds2D();
        for (double x = bounds.getMinX(); x < bounds.getMaxX(); x++) {
            for (double y = bounds.getMinY(); y < bounds.getMaxY(); y++) {
                Location current = new Location(x, y);
                double value = (double) em.getPropertyAt(propertyName, x, y);
                double valueDelta = scale * az.getValue(current);
                // TextUi.println("valueDelta " + valueDelta);
                em.setPropertyAt(propertyName, x, y, value + valueDelta);
            }
        }

    }

    /**
     * Adds a certain value in the circle of radius radius in the heatmap
     * defined by the specific property in the specific environment model
     * 
     * @param em
     *            EnvironmentModel containing the heatmap property
     * @param propertyName
     *            PropertyName of the associated heatmap
     * @param center
     *            The center of the circle, e.g., location of the agent
     * @param radius
     *            The radius of the circle
     * @param valueDelta
     *            The variance in the circle shades
     * @param decreasing
     *            - if set, decrease the value from the center to the edge
     */
    public static void addCircle(EnvironmentModel em, String propertyName,
            Location center, double radius, double valueDelta,
            boolean decreasing) {
        // we iterate on a square, but add only when in the circle
        for (double x = center.getX() - radius; x < center.getX() + radius; x++) {
            for (double y = center.getY() - radius; y < center.getY() + radius; y++) {
                Location current = new Location(x, y);
                double distance = current.distanceTo(center);
                if (distance > radius) {
                    continue;
                }
                double value = (double) em.getPropertyAt(propertyName, x, y);
                if (decreasing)
                    value = value + ((radius - distance) / radius) * valueDelta;
                else
                    value = valueDelta;
                em.setPropertyAt(propertyName, x, y, value);
            }
        }
    }

    /**
     * This method would paint a sector in the direction of the movement of the
     * agent
     * 
     * @param em
     *            EnvironmentModel containing the heatmap property
     * @param propertyName
     *            PropertyName of the associated heatmap
     * @param center
     *            The center of the circle, e.g., location of the agent
     * @param destination
     *            The next location (destination) of the agent
     * @param radius
     *            The radius of the sector
     * @param valueDelta
     *            The range of variance in the sector shades
     */
    public static void addSector(EnvironmentModel em, String propertyName,
            Location center, Location destination, double radius,
            double valueDelta) {
        // we iterate on a square, but add only when in the circle
        for (double x = center.getX() - radius; x < center.getX() + radius; x++) {
            for (double y = center.getY() - radius; y < center.getY() + radius; y++) {
                Location current = new Location(x, y);
                double distance = current.distanceTo(center);
                Line2D referenceLine =
                        new Line2D.Double(center.getX(), center.getY(),
                                destination.getX(), destination.getY());
                Line2D pointLine =
                        new Line2D.Double(center.getX(), center.getY(), x, y);
                if (!(Math.abs(angleBetween2Lines(referenceLine, pointLine)) < 0.55))
                    continue;

                if (distance > radius) {
                    continue;
                }
                double value = (double) em.getPropertyAt(propertyName, x, y);
                value = value + ((radius - distance) / radius) * valueDelta;
                em.setPropertyAt(propertyName, x, y, value);
            }
        }
    }

    /**
     * This method would take into account the target's predicted location, next
     * destination, radial length of the predicted movement's sector and then
     * calculate whether the point in the sector's area overlaps with the
     * social-circle zone of the agent
     * 
     * @param predictedCenter
     *            the predicted center of the target
     * @param predictedDestination
     *            the predicted next step (destination) of the target
     * @param radius
     *            the target's sector radius length
     * @param center
     *            the agent's center location
     * @param destination
     *            the agent's next step (destination location)
     * @param selfSocialZoneRadius
     *            the radial length of agent's social zone
     * @return true if overlap exists
     */
    public static boolean predictedIntrusionInSector(Location predictedCenter,
            Location predictedDestination, double radius, Location center,
            Location destination, double selfSocialZoneRadius) {
        boolean overlap = false;
        // we iterate on a square, but add only when it is in the sector
        // (portion of the circle)
        for (double x = predictedCenter.getX() - radius; x < predictedCenter
                .getX() + radius; x++) {
            for (double y = predictedCenter.getY() - radius; y < predictedCenter
                    .getY() + radius; y++) {
                Location current = new Location(x, y);
                double distance = current.distanceTo(predictedCenter);
                Line2D referenceLine =
                        new Line2D.Double(predictedCenter.getX(),
                                predictedCenter.getY(),
                                predictedDestination.getX(),
                                predictedDestination.getY());
                Line2D pointLine =
                        new Line2D.Double(predictedCenter.getX(),
                                predictedCenter.getY(), x, y);
                if (!(Math.abs(angleBetween2Lines(referenceLine, pointLine)) < 0.55)) {
                    continue;
                }
                if (distance > radius) {
                    continue;
                }
                if (Point2D.distance(x, y, center.getX(), center.getY()) < selfSocialZoneRadius) {
                    overlap = true;
                    break;
                }
            }
        }
        return overlap;
    }

    /**
     * This method is used by the agent-1 to calculate whether its intruding
     * agent-2 social zone or not
     * 
     * @param agent2PredictedLocation
     *            The location of the agent-2
     * @param agent1PredictedLocation
     *            The location of the agent-1
     * @param agent2SocialRadius
     *            The social radius of zone of the agent-2
     * @return return true if intrusion holds
     */
    public static boolean predictedIntrusionInCircle(
            Location agent2PredictedLocation, Location agent1PredictedLocation,
            double agent2SocialRadius) {
        if (agent1PredictedLocation.distanceTo(agent2PredictedLocation) < agent2SocialRadius)
            return true;
        return false;

    }

    /*
    public static void addSocialZoneCircle(Civilian civ, EnvironmentModel em,
            String propertyName, Location center, double radius,
            double valueDelta) {
        civ.setSocioConsultiveZone(radius);
        civ.setIntimateZone(civ.getSocioConsultiveZone() * 0.75);
        civ.setCasualPersonalZone(civ.getIntimateZone() * 0.5);
        // we iterate on a square, but add only when in the circle
        for (double x = center.getX() - radius; x < center.getX() + radius; x++) {
            for (double y = center.getY() - radius; y < center.getY() + radius; y++) {
                Location current = new Location(x, y);
                double distance = current.distanceTo(center);
                if (distance > radius) {
                    continue;
                }
                double value = (double) em.getPropertyAt(propertyName, x, y);
                value = valueDelta;
                if (distance < civ.getSocioConsultiveZone())
                    em.setPropertyAt(propertyName, x, y, 100 + value);
                if (distance < civ.getCasualPersonalZone())
                    em.setPropertyAt(propertyName, x, y, 50 + value);
                if (distance < civ.getIntimateZone())
                    em.setPropertyAt(propertyName, x, y, value);
            }
        }
    }
*/

    /**
     * Returns the angle between two lines in radians
     * 
     * @param line1
     * @param line2
     * @return
     */
    public static double angleBetween2Lines(Line2D line1, Line2D line2) {
        double angle1 =
                Math.atan2(line1.getY1() - line1.getY2(),
                        line1.getX1() - line1.getX2());
        double angle2 =
                Math.atan2(line2.getY1() - line2.getY2(),
                        line2.getX1() - line2.getX2());
        return angle1 - angle2;
    }

}
