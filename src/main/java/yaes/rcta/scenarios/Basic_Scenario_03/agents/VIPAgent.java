package yaes.rcta.scenarios.Basic_Scenario_03.agents;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractHumanAgent;
import yaes.rcta.movement.MapLocationAccessibility;
import yaes.rcta.scenarioHelper.MathHelper;
import yaes.rcta.scenarios.Basic_Scenario_03.Context;
import yaes.rcta.scenarios.Basic_Scenario_03.Helper;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;

public class VIPAgent extends AbstractHumanAgent implements constRCTA {

	private static final long serialVersionUID = 3706056854541867721L;
	private Context context;

	// private Location nextLocation;
	private boolean turnLeft = true;
	private double speed;

	public VIPAgent(String name, Location location, double heading) {
		super(name, location, heading);

	}

	@Override
	public void action() {
		Location loc = null;
		ExperimentMode mode = context.getSimulationInput().getParameterEnum(ExperimentMode.class);
		switch (mode) {
		case MANUAL_RUN:
			switch (context.getInteractiveSim().getEventType()) {
			case KeyEvent:
				keyResponse(context.getInteractiveSim().getKeypress());
				break;
			case MouseEvent:
				mouseResponse(context.getInteractiveSim().getLocMouse());
				break;
			}

			break;
		case PROGRAMMED_RUN:
			loc = getMoveObstacleAvoidance();

			if (loc != null) {
				this.setLocation(loc);
			}
			break;
		case NORMAL_OPERATION:
			break;
		default:
			break;

		}
	}

	private Location getMoveObstacleAvoidance() {
		Location intendedLocation = this.getGlobalDestination();
		Location currentLocation = this.getLocation();
		if (((int) currentLocation.getX() == (int) intendedLocation.getX())
				&& ((int) currentLocation.getY() == (int) intendedLocation.getY())) {
			TextUi.println("Target Reached");
			return currentLocation;
		}
		Location newLocation = findNewLocation(currentLocation, intendedLocation);
		this.getActualPath().addLocation(newLocation);
		return newLocation;
	}

	private void mouseResponse(Location location) {

		double dist = getLocation().distanceTo(location);
		if (dist <= getRadiusPersonalSpace()) {
			setFocus(true);
		} else {
			setFocus(false);
		}

	}

	// Bug0 Algorithm assuming left movement is available else right
	private Location findNewLocation(Location currentLocation, Location intendedLocation) {

		double movementAngle = MathHelper.getAngle(currentLocation, intendedLocation, true);
		// TextUi.println("");
		// TextUi.println("Actual angle: " + movementAngle);
		double temp = movementAngle;
		int counter = 0;
		while (true) {

			double deltaX = speed * Math.cos(Math.toRadians(temp));
			double deltaY = speed * Math.sin(Math.toRadians(temp));
			Location newLocation = new Location(this.getLocation().getX() + deltaX, this.getLocation().getY() + deltaY);
			// Check If location is accessible
			if (isAccessible(this.context.getEnvironmentModel(), new MapLocationAccessibility(), newLocation)) {
				// TextUi.println("Movement angle: " + temp);
				return newLocation;
			}
			counter++;
			if (this.turnLeft) {
				if (counter <= 100) {
					temp = movementAngle + counter;
				} else {
					this.turnLeft = false;
					counter = 0;
				}

			} else {
				if (counter <= 100) {
					temp = movementAngle - counter;
				} else {
					this.turnLeft = true;
					counter = 0;
				}
			}

		}

	}

	/**
	 * Check if newlocation is not accessible or obstacle
	 * 
	 * @param environmentIMap
	 * @param mapLocationAccessibility
	 * @param newLocation
	 * @return
	 */
	private boolean isAccessible(EnvironmentModel environmentIMap, MapLocationAccessibility mapLocationAccessibility,
			Location newLocation) {
		if (mapLocationAccessibility.isAccessible(environmentIMap, newLocation)) {
			return true;
		} else {

			return false;
		}

	}

	/**
	 * Moves the soldier following the arrow keys pressed on keyboard
	 * 
	 * @param direction
	 */
	public void keyResponse(int direction) {
		Location currentLoc = this.getLocation();
		Location newLocation = new Location(0, 0);
		switch (direction) {
		case KeyEvent.VK_UP:
			newLocation = new Location(currentLoc.getX(), currentLoc.getY() - this.getSpeed());
			if (Helper.isLocationOccupied(context, newLocation, 0))
				return;
			this.setLocation(newLocation);
			break;
		case KeyEvent.VK_DOWN:
			newLocation = new Location(currentLoc.getX(), currentLoc.getY() + this.getSpeed());
			if (Helper.isLocationOccupied(context, newLocation, 0))
				return;
			this.setLocation(newLocation);
			break;
		case KeyEvent.VK_LEFT:
			newLocation = new Location(currentLoc.getX() - this.getSpeed(), currentLoc.getY());
			if (Helper.isLocationOccupied(context, newLocation, 0))
				return;
			this.setLocation(newLocation);
			break;
		case KeyEvent.VK_RIGHT:
			newLocation = new Location(currentLoc.getX() + this.getSpeed(), currentLoc.getY());
			if (Helper.isLocationOccupied(context, newLocation, 0))
				return;
			this.setLocation(newLocation);
			break;
		default:
			return;
		}

	}

	/**
	 * Set global destination as randomly select destination from list of POI
	 */
	public void setGlobalDestinationAsRandomPoI() {
		ArrayList<Location> locations = new ArrayList<Location>(locList.values());
		Location loc = locations.get(context.getRandom().nextInt(locList.size()));

		while (loc.equals(getGlobalDestination()))
			loc = locations.get(context.getRandom().nextInt(locations.size()));
		this.setGlobalDestination(loc);
	}

	/**
	 * To check if given location is inside personal radius of this agent.
	 * 
	 * @param loc
	 * @return
	 */
	public boolean containLocation(Location loc) {
		double distance = this.getLocation().distanceTo(loc);
		if (distance <= this.getRadiusPersonalSpace()) {
			TextUi.println("Point inside the perseon space");
			return true;
		}
		return false;

	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public String toString() {
		Formatter fmt = new Formatter();
		fmt.add("Soldier");
		fmt.indent();
		fmt.is("Name", getName());
		fmt.is("Location", getLocation());
		fmt.is("Current Destination", getGlobalDestination());
		fmt.is("Speed", speed);

		return fmt.toString();
	}

	@Override
	public Location getIntendedMove() {
		// TODO Auto-generated method stub
		return null;
	}

}
