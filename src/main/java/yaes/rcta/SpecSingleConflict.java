package yaes.rcta;

import java.io.Serializable;

import yaes.framework.simulation.SimulationInput;
import yaes.world.physical.location.Location;

/**
 * This class contains the specification for a single conflict scenario between
 * a robot and a civilian
 * 
 * @author Lotzi Boloni
 * 
 */
public class SpecSingleConflict implements constRCTA, Serializable {

    private static final long serialVersionUID = 5638574770903706963L;
    private Location center;
    private double radiusR;
    private double startAngleR;
    private double destAngleR;
    private double radiusC;
    private double startAngleC;
    private double destAngleC;
    private double speedR;
    private double speedC;

    public Location getLocation(double angle, double r) {
        int x = (int) (center.getX() + r * Math.cos(angle));
        int y = (int) (center.getY() + r * Math.sin(angle));
        return new Location(x, y);
    }

    /**
     * Sets up a single conflict
     * 
     * @param sip
     * @param context
     */
    public void setupSingleConflict(SimulationInput sip, RctaContext context) {
        /**
         * The robot
         */
/*        Location robotStart = getLocation(startAngleR, radiusR);
        Location robotDest = getLocation(destAngleR, radiusR);
        Robot robot = context.createRobot(robotStart, robotDest, speedR);
        context.robots.add(robot);
        *//**
         * The civilian
         *//*
        Location civilianStart = getLocation(startAngleC, radiusC);
        Location civilianEnd = getLocation(destAngleC, radiusC);
        double mcParameter =
                sip.getParameterDouble(CIVILIAN_MICRO_CONFLICT_STRATEGY_PARAMETER);
        Civilian civilian =
                context.createCivilian("Civilian", mcParameter, civilianStart,
                        civilianEnd, speedC);
        context.civilians.add(civilian);*/
    }

    /**
     * @return the center
     */
    public Location getCenter() {
        return center;
    }

    /**
     * @param center
     *            the center to set
     */
    public void setCenter(Location center) {
        this.center = center;
    }

    /**
     * @return the radiusR
     */
    public double getRadiusR() {
        return radiusR;
    }

    /**
     * @param radiusR
     *            the radiusR to set
     */
    public void setRadiusR(double radiusR) {
        this.radiusR = radiusR;
    }

    /**
     * @return the startAngleR
     */
    public double getStartAngleR() {
        return startAngleR;
    }

    /**
     * @param startAngleR
     *            the startAngleR to set
     */
    public void setStartAngleR(double startAngleR) {
        this.startAngleR = startAngleR;
    }

    /**
     * @return the destAngleR
     */
    public double getDestAngleR() {
        return destAngleR;
    }

    /**
     * @param destAngleR
     *            the destAngleR to set
     */
    public void setDestAngleR(double destAngleR) {
        this.destAngleR = destAngleR;
    }

    /**
     * @return the radiusC
     */
    public double getRadiusC() {
        return radiusC;
    }

    /**
     * @param radiusC
     *            the radiusC to set
     */
    public void setRadiusC(double radiusC) {
        this.radiusC = radiusC;
    }

    /**
     * @return the startAngleC
     */
    public double getStartAngleC() {
        return startAngleC;
    }

    /**
     * @param startAngleC
     *            the startAngleC to set
     */
    public void setStartAngleC(double startAngleC) {
        this.startAngleC = startAngleC;
    }

    /**
     * @return the destAngleC
     */
    public double getDestAngleC() {
        return destAngleC;
    }

    /**
     * @param destAngleC
     *            the destAngleC to set
     */
    public void setDestAngleC(double destAngleC) {
        this.destAngleC = destAngleC;
    }

    /**
     * @return the speedR
     */
    public double getSpeedR() {
        return speedR;
    }

    /**
     * @param speedR
     *            the speedR to set
     */
    public void setSpeedR(double speedR) {
        this.speedR = speedR;
    }

    /**
     * @return the speedC
     */
    public double getSpeedC() {
        return speedC;
    }

    /**
     * @param speedC
     *            the speedC to set
     */
    public void setSpeedC(double speedC) {
        this.speedC = speedC;
    }

}
