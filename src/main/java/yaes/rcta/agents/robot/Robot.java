package yaes.rcta.agents.robot;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractRobotAgent;
import yaes.rcta.agents.Human;
import yaes.rcta.environment.RctaEnvironmentHelper;
import yaes.rcta.movement.DStarLitePP;
import yaes.rcta.movement.TSPNearestNeighbor;
import yaes.rcta.util.PIDController;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

/**
 * This class defines the functionality of the robot agent
 * 
 * @author Taranjeet
 * 
 */

public class Robot extends AbstractRobotAgent implements constRCTA {
    private static final long serialVersionUID = -2627947164445902056L;
    private Location finalDestination;
    private Location startLocation, nextLocation;
    // PIDController(0.5, 50, 0.5);
    private PIDController pidController = new PIDController(0.5, 50, 0.5);

    private double speed, maximumSpeed = 5.0, minimumSpeed = 0, distance;
    private double startTime;
    private boolean turnLeft = true;
    private DefaultOrientation orientation;

    private RctaContext context;
    private Human targetSoldier;
    private int workLoad = 2;
    private int minDistance, maxDistance;

    public Robot(String name, Location location, double heading) {
        super(name, location, heading);
    }

    @Override
    public void action() {
        Location loc, newLocation;
        switch (this.context.getSceneType()) {
        case REALISTIC_WITH_ROBOT_SCENARIO:
            loc = this.plannedPath.getNextLocation(this.getLocation(),
                    (int) this.speed);
            newLocation = RobotHelper.findNearestLocation(this.context,
                    getVIPLocation(), loc, this.minDistance);
            this.setLocation(newLocation);
            break;

        case ROBOT_FOLLOW_SOLDIER:
        case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
        case SINGLE_ROBOT_FOLLOW:
        case CROWD_SEEK_VIP:
        case MULTIPLE_ROBOT_FOLLOW:
            this.setLocalDestination(getVIPLocation());
            loc = getIntendedMove();
            if (Math.ceil(loc.distanceTo(getVIPLocation())) < this.minDistance)
                return;
            newLocation = RobotHelper.findNearestLocation(this.context,
                    getVIPLocation(), loc, this.minDistance);
            this.setLocation(newLocation);
            break;
        case CLOSE_PROTECTION_SINGLE_ROBOT:
            // Single Robot Approach
            // based on Resultant vectors
            loc = RobotHelper.calculateCiviliansVectors(this.context, this,
                    getVIPLocation(), this.context.getSoldiers());
            newLocation = RobotHelper.findNearestLocation(this.context,
                    getVIPLocation(), loc, this.minDistance);
            this.setLocation(newLocation);
            // Use A* algorithm to reach the intendedLocation and return next
            // possible path coordinate.
            // TextUi.println("Robot: " + getName() + " moving by 1 step");
            // TextUi.println("Next Location to move" + loc.toString());
            break;
        case CLOSE_PROTECTION_MULTIPLE_ROBOT:
            loc = RobotHelper.quadrantLoadBalancing(this.context, this,
                    this.context.getSoldiers());
            newLocation = RobotHelper.findNearestLocation(this.context,
                    getVIPLocation(), loc, this.minDistance);
            this.setLocation(newLocation);
            break;
        case CLOSE_PROTECTION_CONTROL_MODEL:
            // Single Robot Approach
            // based on Resultant vectors
            loc = RobotHelper.quadrantLoadBalancing(this.context, this,
                    this.context.getSoldiers());
            newLocation = RobotHelper.findNearestLocation(this.context,
                    getVIPLocation(), loc, this.minDistance);
            this.getDesiredPath().addLocation(newLocation);

            this.steering().seek(
                    new Vector2D(newLocation.getX(), newLocation.getY()), 8);
            // this.steering().collisionAvoidance();
            this.steering().update();
            this.getActualPath().addLocation(this.getLocation());
            TextUi.println("Desired Location: " + newLocation
                    + "\n Actual Location: " + this.getLocation());
                    // Use A* algorithm to reach the intendedLocation and return
                    // next
                    // possible path coordinate.

            // TextUi.println("Robot: " + getName() + " moving by 1 step");
            // TextUi.println("Next Location to move" + loc.toString());
            break;
        case SAME_CROWD_ADAPTIVE:
            // The robot is going to move towards the finalDestionation till it
            // reaches the finalDestination. After that a new destination would
            // be generated from the list of POTs for the agents
            if (this.getLocation().equals(this.getLocalDestination())) {
                RctaEnvironmentHelper.setRandomPOIForRobot(this.context, this);
                DStarLitePP dStar =
                        new DStarLitePP(this.context.getEnvironmentModel(),
                                this.location, this.finalDestination);
                this.plannedPath = dStar.searchPath();
            }
            loc = this.plannedPath.getNextLocation(this.location,
                    (int) this.speed);
            // if the nextLocation in path is null, it means that the robot has
            // reached to its destination
            if (loc != null)
                this.setLocation(loc);
            else
                this.setLocation(this.finalDestination);
            break;
        case CROWD_SIMULATION:
        case DEFAULT:
        case IMITATION:
        case OBSTACLE_AVOIDANCE:
        case OBSTACLE_AVOIDANCE_IN_CROWD:
        case REALISTIC:
        case ROBOT_SCOUT:
        case SINGLE_CONFLICT:
        default:

        }

        // PID controller for speed
        // double distance = this.getLocation().distanceTo(loc);
        // pidController.getInput(distance);
        // this.speed = pidController.performPID();

        // if (isAccessible(this.context.getEnvironmentIMap(),
        // new MapLocationAccessibility(), loc)) {
        // this.setLocalDestination(loc);
        // }else {
        // this.setLocalDestination(this.targetSoldier.getLocation());
        // }
        //
        // this.setLocalDestination(loc);
        // DStarLitePP dStar = new DStarLitePP(context.environmentIMap,
        // this.getLocation(), this.getLocalDestination());
        // this.path = dStar.searchPath();
        // if(this.path != null){
        // Location newLocation = path.getNextLocation(this.getLocation(), (int)
        // speed);
        // this.setLocation(newLocation);
        // }

        // Location newLocation = findNewLocation(this.getLocation(), loc);
        // this.setLocation(newLocation);

        // Add current location to list of past location
        // this.plannedPath.addLocation(this.getLocation());

        // TextUi.println("Desired Location: " + newLocation +
        // "\n Current Location: "+ this.getLocation());
        // Update current location

    }

    /**
     * Taran - Method to decide the next location coordinate
     * 
     * Saad - This method returns the nextIntendedMove in the game
     * 
     * If the location in the pathPlanned is null then then next destination is
     * same as final destination, i.e., current destination
     * 
     * @return
     */
    @Override
    public Location getIntendedMove() {
        // this.setNextLocation(this.getLocation());
        Location loc = this.plannedPath.getNextLocation(this.location,
                (int) this.speed);
        if (loc != null) {
            return loc;
        }
        // if (this.getOrientation() != null) {
        // return this.setOrientation(this.getOrientation());
        // }

        // return getLocalDestination();

        return this.location;

    }

    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("Robot");
        fmt.indent();
        fmt.is("Name", this.name);
        fmt.is("Location", this.location);
        fmt.is("Current Destination", this.finalDestination);
        fmt.is("Speed", this.speed);
        fmt.is("Maximum Speed", this.maximumSpeed);
        fmt.is("Speed", this.minimumSpeed);
        return fmt.toString();
    }

    /**
     * Return appropriate location as per the default orientation
     * 
     * @param orientation
     * @return Location
     */
    public Location setOrientation(DefaultOrientation orientation) {
        double deltaX, deltaY;
        Location intendedLocation = this.targetSoldier.getLocation();
        this.orientation = orientation;
        switch (orientation) {
        case FRONT_LEFT:
            if (this.targetSoldier.getHeading() != 0.0) {
                TextUi.println(Math.toDegrees(this.targetSoldier.getHeading()));
            }
            deltaX = this.minDistance * Math
                    .cos(this.targetSoldier.getHeading() + Math.toRadians(45));
            deltaY = this.minDistance * Math
                    .sin(this.targetSoldier.getHeading() + Math.toRadians(45));
            intendedLocation = new Location(intendedLocation.getX() + deltaX,
                    intendedLocation.getY() + deltaY);

            break;
        case FRONT_RIGHT:

            deltaX = this.minDistance * Math
                    .cos(this.targetSoldier.getHeading() - Math.toRadians(45));
            deltaY = this.minDistance * Math
                    .sin(this.targetSoldier.getHeading() - Math.toRadians(45));
            intendedLocation = new Location(intendedLocation.getX() + deltaX,
                    intendedLocation.getY() + deltaY);
            break;
        case BEHIND_LEFT:

            deltaX = this.minDistance * Math
                    .cos(this.targetSoldier.getHeading() - Math.toRadians(135));
            deltaY = this.minDistance * Math
                    .sin(this.targetSoldier.getHeading() - Math.toRadians(135));
            intendedLocation = new Location(intendedLocation.getX() + deltaX,
                    intendedLocation.getY() + deltaY);
            break;
        case BEHIND_RIGHT:
            deltaX = this.minDistance * Math
                    .cos(this.targetSoldier.getHeading() + Math.toRadians(135));
            deltaY = this.minDistance * Math
                    .sin(this.targetSoldier.getHeading() + Math.toRadians(135));
            intendedLocation = new Location(intendedLocation.getX() + deltaX,
                    intendedLocation.getY() + deltaY);
            break;
        case FRONT:
            deltaX = this.minDistance
                    * Math.cos(this.targetSoldier.getHeading());
            deltaY = this.minDistance
                    * Math.sin(this.targetSoldier.getHeading());
            intendedLocation = new Location(intendedLocation.getX() + deltaX,
                    intendedLocation.getY() + deltaY);
            break;
        case BEHIND:
            deltaX = this.minDistance * Math
                    .cos(this.targetSoldier.getHeading() + Math.toRadians(180));
            deltaY = this.minDistance * Math
                    .sin(this.targetSoldier.getHeading() + Math.toRadians(180));
            intendedLocation = new Location(intendedLocation.getX() + deltaX,
                    intendedLocation.getY() + deltaY);
            break;
        case LEFT:
            deltaX = this.minDistance * Math
                    .cos(this.targetSoldier.getHeading() + Math.toRadians(90));
            deltaY = this.minDistance * Math
                    .sin(this.targetSoldier.getHeading() + Math.toRadians(90));
            intendedLocation = new Location(intendedLocation.getX() + deltaX,
                    intendedLocation.getY() + deltaY);
            break;
        case RIGHT:
            deltaX = this.minDistance * Math
                    .cos(this.targetSoldier.getHeading() - Math.toRadians(90));
            deltaY = this.minDistance * Math
                    .sin(this.targetSoldier.getHeading() - Math.toRadians(90));
            intendedLocation = new Location(intendedLocation.getX() + deltaX,
                    intendedLocation.getY() + deltaY);
            break;
        default:

            break;
        }
        return intendedLocation;
    }

    public ArrayList<Location> tspPathPlanner(ArrayList<Location> locations) {
        locations.add(0, this.getLocation());
        DStarLitePP dStar;
        PlannedPath path;
        int adjacencyMatrix[][] =
                new int[locations.size() + 1][locations.size() + 1];
        int tracker = 1;
        int neighborTracker = 1;
        for (Location loc : locations) {
            neighborTracker = 1;
            for (Location neighborLoc : locations) {
                if (neighborLoc.equals(loc)) {
                    adjacencyMatrix[tracker][tracker] = 0;
                    continue;
                }
                // FIXME: Avoiding the D-Lite Paths for swift calculations
                // dStar = new DStarLitePP(context.emGlobalCost, loc,
                // neighborLoc);
                // path = dStar.searchPath();
                // adjacencyMatrix[tracker][neighborTracker++] = (int)
                // path.getPathLenght();
                adjacencyMatrix[tracker][neighborTracker++] =
                        (int) this.getLocation().distanceTo(neighborLoc);
            }
            tracker++;
        }

        for (int i = 1; i <= locations.size(); i++)
            for (int j = 1; j <= locations.size(); j++)
                if (adjacencyMatrix[i][j] == 1 && adjacencyMatrix[j][i] == 0)
                    adjacencyMatrix[j][i] = 1;

        TSPNearestNeighbor tspNearestNeighbor = new TSPNearestNeighbor();
        ArrayList<Integer> tspOrder = tspNearestNeighbor.tsp(adjacencyMatrix);
        ArrayList<Location> finalList = new ArrayList<Location>();
        TextUi.println(tspOrder.toString());
        for (int order : tspOrder) {
            finalList.add(locations.get(order - 1));
        }
        TextUi.println(finalList.toString());
        return finalList;
    }

    private Location getVIPLocation() {
        return this.getTargetSoldier().getLocation();
    }

    public int getMinDistance() {
        return this.minDistance;
    }

    public void setMinDistance(int minDistance) {
        this.minDistance = minDistance;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public PIDController getPidController() {
        return this.pidController;
    }

    public void setPidController(PIDController pidController) {
        this.pidController = pidController;
    }

    public boolean isTurnLeft() {
        return this.turnLeft;
    }

    public void setTurnLeft(boolean turnLeft) {
        this.turnLeft = turnLeft;
    }

    public int getWorkLoad() {
        return this.workLoad;
    }

    public void setWorkLoad(int workLoad) {
        this.workLoad = workLoad;
    }

    public RctaContext getContext() {
        return this.context;
    }

    public void setContext(RctaContext context) {
        this.context = context;
    }

    public Human getTargetSoldier() {
        return this.targetSoldier;
    }

    public void setTargetSoldier(Human targetSoldier) {
        this.targetSoldier = targetSoldier;
    }

    @Override
    public Location getLocalDestination() {
        return this.finalDestination;
    }

    @Override
    public void setLocalDestination(Location localDestination) {
        this.finalDestination = localDestination;
    }

    public Location getStartLocation() {
        return this.startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getNextLocation() {
        return this.nextLocation;
    }

    public void setNextLocation(Location nextLocation) {
        this.nextLocation = nextLocation;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getMaximumSpeed() {
        return this.maximumSpeed;
    }

    public void setMaximumSpeed(double maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

    public double getMinimumSpeed() {
        return this.minimumSpeed;
    }

    public void setMinimumSpeed(double minimumSpeed) {
        this.minimumSpeed = minimumSpeed;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getStartTime() {
        return this.startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public DefaultOrientation getOrientation() {
        return this.orientation;
    }

}
