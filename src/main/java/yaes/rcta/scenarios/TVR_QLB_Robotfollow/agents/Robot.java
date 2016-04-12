package yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents;

import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractRobotAgent;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.Context;
import yaes.ui.format.Formatter;
import yaes.world.physical.location.Location;

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
	private double speed, maximumSpeed = 5.0, minimumSpeed = 0, distance;
	private double startTime;
	private boolean turnLeft = true;
	private DefaultOrientation orientation;

	private Context context;
	private Human targetSoldier;
	private int workLoad = 2;
	private int minDistance, maxDistance;

	public Robot(String name, Location location, double heading) {
		super(name, location, heading);
	}

	@Override
	public void action() {
		Location loc, newLocation;
		loc = RobotHelper.quadrantLoadBalancing(this.context, this,
                this.context.getSoldiers());
        newLocation = RobotHelper.findNearestLocation(this.context,
                getVIPLocation(), loc, this.minDistance);
        this.setLocation(newLocation);
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
		Location loc = this.plannedPath.getNextLocation(this.location, (int) this.speed);
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
			// if (this.targetSoldier.getHeading() != 0.0) {
			// TextUi.println(Math.toDegrees(this.targetSoldier.getHeading()));
			// }
			deltaX = this.minDistance * Math.cos(this.targetSoldier.getHeading() + Math.toRadians(45));
			deltaY = this.minDistance * Math.sin(this.targetSoldier.getHeading() + Math.toRadians(45));
			intendedLocation = new Location(intendedLocation.getX() + deltaX, intendedLocation.getY() + deltaY);

			break;
		case FRONT_RIGHT:

			deltaX = this.minDistance * Math.cos(this.targetSoldier.getHeading() - Math.toRadians(45));
			deltaY = this.minDistance * Math.sin(this.targetSoldier.getHeading() - Math.toRadians(45));
			intendedLocation = new Location(intendedLocation.getX() + deltaX, intendedLocation.getY() + deltaY);
			break;
		case BEHIND_LEFT:

			deltaX = this.minDistance * Math.cos(this.targetSoldier.getHeading() - Math.toRadians(135));
			deltaY = this.minDistance * Math.sin(this.targetSoldier.getHeading() - Math.toRadians(135));
			intendedLocation = new Location(intendedLocation.getX() + deltaX, intendedLocation.getY() + deltaY);
			break;
		case BEHIND_RIGHT:
			deltaX = this.minDistance * Math.cos(this.targetSoldier.getHeading() + Math.toRadians(135));
			deltaY = this.minDistance * Math.sin(this.targetSoldier.getHeading() + Math.toRadians(135));
			intendedLocation = new Location(intendedLocation.getX() + deltaX, intendedLocation.getY() + deltaY);
			break;
		case FRONT:
			deltaX = this.minDistance * Math.cos(this.targetSoldier.getHeading());
			deltaY = this.minDistance * Math.sin(this.targetSoldier.getHeading());
			intendedLocation = new Location(intendedLocation.getX() + deltaX, intendedLocation.getY() + deltaY);
			break;
		case BEHIND:
			deltaX = this.minDistance * Math.cos(this.targetSoldier.getHeading() + Math.toRadians(180));
			deltaY = this.minDistance * Math.sin(this.targetSoldier.getHeading() + Math.toRadians(180));
			intendedLocation = new Location(intendedLocation.getX() + deltaX, intendedLocation.getY() + deltaY);
			break;
		case LEFT:
			deltaX = this.minDistance * Math.cos(this.targetSoldier.getHeading() + Math.toRadians(90));
			deltaY = this.minDistance * Math.sin(this.targetSoldier.getHeading() + Math.toRadians(90));
			intendedLocation = new Location(intendedLocation.getX() + deltaX, intendedLocation.getY() + deltaY);
			break;
		case RIGHT:
			deltaX = this.minDistance * Math.cos(this.targetSoldier.getHeading() - Math.toRadians(90));
			deltaY = this.minDistance * Math.sin(this.targetSoldier.getHeading() - Math.toRadians(90));
			intendedLocation = new Location(intendedLocation.getX() + deltaX, intendedLocation.getY() + deltaY);
			break;
		default:

			break;
		}
		return intendedLocation;
	}

	public Location getVIPLocation() {
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

	public Context getContext() {
		return this.context;
	}

	public void setContext(Context context) {
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
