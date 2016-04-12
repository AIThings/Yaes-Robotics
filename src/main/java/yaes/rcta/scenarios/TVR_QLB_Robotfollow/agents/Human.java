package yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractHumanAgent;
import yaes.rcta.movement.DStarLitePP;
import yaes.rcta.movement.MapLocationAccessibility;
import yaes.rcta.scenarioHelper.MathHelper;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.Context;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.Helper;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;

public class Human extends AbstractHumanAgent implements constRCTA {

	private static final long serialVersionUID = 3706056854541867721L;
	private Context context;

	// private Location nextLocation;
	private boolean turnLeft = true;
	private double speed;

	public Human(String name, Location location, double heading) {
		super(name, location, heading);

	}

	@Override
	public void action() {
		Location loc = null;
		ExperimentMode mode = context.getSimulationInput().getParameterEnum(ExperimentMode.class);
		switch (mode) {
		case MANUAL_RUN:
			keyResponse(context.getInteractiveSim().getKeypress());
			mouseResponse(context.getInteractiveSim().getLocMouse());
			break;
		case PROGRAMMED_RUN:
			if (this.getName().contains("CIV")) {
				loc = takeAction(context.getSimulationInput().getParameterEnum(ExperimentConfiguration.class));
			} else {
				loc = takeActionObstacleAvoidance();
			}

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

	private void mouseResponse(Location location) {
		double dist = getLocation().distanceTo(location);
		if (dist <= getRadiusPersonalSpace()) {
			setFocus(true);
		} else {
			setFocus(false);
		}

		Helper.calculateThreatLevel(context);
		Helper.calculateNutralizedThreatLevel(context);

	}

	/**
	 * Default method which is same as strategy 0, which is to walk on D*
	 * planned path ignore other agents in the crowd
	 * 
	 * @param experimentConfiguration
	 * @return
	 */
	public Location takeAction(ExperimentConfiguration experimentConfiguration) {

		DStarLitePP dStar = null;
		Location newLocation = null;
		double xMove;

		if (this.getLocation().equals(this.getGlobalDestination())) {
			switch (experimentConfiguration) {
			case STATIC_CIVILIANS:
				return this.getLocation();

			case INDIRECTION_CIVILIAN_MOVEMENT:
				xMove = this.getLocation().getX() + this.speed;
				newLocation = new Location(xMove, this.getLocation().getY());
				while (!(isAccessible(this.context.getEnvironmentModel(), new MapLocationAccessibility(),
						newLocation))) {
					xMove = xMove + this.speed;
					newLocation = new Location(xMove, this.getLocation().getY());
				}

				if (xMove > this.context.getSimulationInput().getParameterInt(MAP_WIDTH)) {
					xMove = 0.0;
					newLocation = new Location(xMove, this.getLocation().getY());
					while (!(isAccessible(this.context.getEnvironmentModel(), new MapLocationAccessibility(),
							newLocation))) {
						xMove = xMove + this.speed;
						if (xMove > this.context.getSimulationInput().getParameterInt(MAP_WIDTH)) {
							xMove = 0.0;
						}
						newLocation = new Location(xMove, this.getLocation().getY());
					}
					this.setGlobalDestination(newLocation);
					return newLocation;
				}

				this.setGlobalDestination(newLocation);
				dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(), this.getGlobalDestination());
				this.setPath(dStar.searchPath());
				break;

			case OUTDIRECTION_CIVILIAN_MOVEMENT:
				xMove = this.getLocation().getX() - this.speed;
				newLocation = new Location(xMove, this.getLocation().getY());

				if (xMove < 0) {
					xMove = this.context.getSimulationInput().getParameterInt(MAP_WIDTH);
					newLocation = new Location(xMove, this.getLocation().getY());
					while (!(isAccessible(this.context.getEnvironmentModel(), new MapLocationAccessibility(),
							newLocation))) {
						xMove = xMove - this.speed;
						newLocation = new Location(xMove, this.getLocation().getY());
					}
					this.setGlobalDestination(newLocation);
					return newLocation;
				} else {
					while (!(isAccessible(this.context.getEnvironmentModel(), new MapLocationAccessibility(),
							newLocation))) {
						xMove = xMove - this.speed;
						if (xMove < 0) {
							xMove = this.context.getSimulationInput().getParameterInt(MAP_WIDTH);
							this.setLocation(new Location(xMove, this.getLocation().getY()));
						}
						newLocation = new Location(xMove, this.getLocation().getY());
					}

				}
				this.setGlobalDestination(newLocation);
				dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(), this.getGlobalDestination());
				this.setPath(dStar.searchPath());
				break;

			case RANDOM_CIVILIAN_MOVEMENT:
				this.setGlobalDestinationAsRandomPoI();
				dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(), this.getGlobalDestination());
				this.setPath(dStar.searchPath());
				break;

			default:
				break;

			}

		}

		Location loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
		if (loc != null)
			return loc;
		else
			return getGlobalDestination();

	}

	/**
	 * Method to decide the next location coordinate
	 * 
	 * @return
	 */
	public Location takeActionObstacleAvoidance() {
		Location intendedLocation = this.getGlobalDestination();
		Location currentLocation = this.getLocation();

		if (((int) currentLocation.getX() == (int) intendedLocation.getX())
				&& ((int) currentLocation.getY() == (int) intendedLocation.getY())) {
			TextUi.println("Target Reached");
			return currentLocation;
		}
		Location newLocation = findNewLocation(currentLocation, intendedLocation);

		// this.setNextLocation(newLocation);

		return newLocation;
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

		Helper.calculateThreatLevel(context);
		Helper.calculateNutralizedThreatLevel(context);
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
