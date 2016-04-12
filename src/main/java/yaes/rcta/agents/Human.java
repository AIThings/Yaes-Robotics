package yaes.rcta.agents;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.gametheory.MicroConflict;
import yaes.rcta.agents.steering.BehaviorEnum;
import yaes.rcta.agents.steering.Boid;
import yaes.rcta.environment.RctaEnvironmentHelper;
import yaes.rcta.movement.DStarLitePP;
import yaes.rcta.movement.MapLocationAccessibility;
import yaes.rcta.util.PlannedTour;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

public class Human extends AbstractHumanAgent implements constRCTA {

	private static final long serialVersionUID = 3706056854541867721L;
	private RctaContext context;

	// private Location nextLocation;
	private boolean turnLeft = true;
	private double speed;
	// private PlannedPath path;
	private PlannedTour plannedTour = new PlannedTour();
	// private PlannedTour plannedPathForLocalDestination = new PlannedTour();
	private Map<String, Integer> waitingTime = new HashMap<String, Integer>();
	private int timeToSpendAtPOI = 0;
	private int timeLeftAtPOI = 0;
	private int timeToResolveConflict = 0;
	private int timeLeftToResolveConflict = 0;
	private static final int CONFLICT_WAIT_TIME = 5;
	private Boid boid;

	public Human(String name, Location location, double heading) {
		super(name, location, heading);

	}

	@Override
	public void action() {
		Location loc = null;

		switch (context.getSimulationInput().getParameterEnum(ScenarioType.class)) {

		case ROBOT_FOLLOW_SOLDIER:
			loc = getIntendedMoveObstacleAvoidance();
			break;

		case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
			loc = getIntendedMoveObstacleAvoidance();
			break;

		case OBSTACLE_AVOIDANCE:
			// Location loc = this.path.getNextLocation(this.getLocation(),
			// (int)this.speed);
			loc = getIntendedMoveObstacleAvoidance();
			break;

		case OBSTACLE_AVOIDANCE_IN_CROWD:
			loc = getIntendedMoveObstacleAvoidance();
			break;
		case SINGLE_ROBOT_FOLLOW:
		case MULTIPLE_ROBOT_FOLLOW:
		case CLOSE_PROTECTION_SINGLE_ROBOT:
		case CLOSE_PROTECTION_MULTIPLE_ROBOT:
		case CLOSE_PROTECTION_CONTROL_MODEL:
		case CROWD_SEEK_VIP:
		case CROWD_SIMULATION:
			// If experiment mode is "Enter button"
			// Run All the civilians on the pre-planned path
			switch (context.getSimulationInput().getParameterEnum(ExperimentConfiguration.class)) {
			case CROWD_MOVEMENT:
				// Select strategy for crowd agent
				int strategy = 0;
				if (this.getName().contains("CIV")) {
					switch (strategy) {
					case 0:
						loc = getIntendedMoveCrowd_0();
						break;
					case 1:
						loc = getIntendedMoveCrowd_1();
						break;
					case 2:
						loc = getIntendedMoveCrowd_2();
						break;
					default:
						break;
					}

				} else {
					loc = getIntendedMoveObstacleAvoidance();
				}
				break;
			case CROWD_SEEK_VIP_MOVEMENT:
			case INDIRECTION_CIVILIAN_MOVEMENT:
			case OUTDIRECTION_CIVILIAN_MOVEMENT:
			case RANDOM_CIVILIAN_MOVEMENT:
			case STATIC_CIVILIANS:
			default:
				if (this.getName().contains("CIV")) {
					loc = getIntendedMove(context.getSimulationInput().getParameterEnum(ExperimentConfiguration.class));
				} else {
					loc = getIntendedMoveObstacleAvoidance();
				}
				break;

			}

			break;
		case SAME_CROWD_ADAPTIVE:
		case DEFAULT:
		case IMITATION:
		case REALISTIC:
		case REALISTIC_WITH_ROBOT_SCENARIO:
		case ROBOT_SCOUT:
		case SINGLE_CONFLICT:

		default:
			loc = getIntendedMoveObstacleAvoidance();
			break;

		}

		if (loc != null) {
			this.setLocation(loc);
		}

	}

	/**
	 * This behavior use steering control to reach local destination and D* lite
	 * to reach global destination. Every crowd agent has list of point of
	 * interests to visit. These locations called global destinations. From one
	 * globalDestination to next globalDestination, agent use D* algorithm to
	 * get the path. Local destination is a location on this path which is
	 * reachable to agent without any static obstacle in between. crowd agent
	 * approach the local destination using steering control. This steering
	 * does not have approach to avoid static obstacles in the map.
	 * 
	 * @return
	 */
	private Location getIntendedMoveCrowd_2() {
		Location loc = null;
		if (this.getLocation().equals(getGlobalDestination())) {
			if (timeLeftAtPOI == 0 && timeToSpendAtPOI == 0) {
				timeToSpendAtPOI = this.getWaitingTime(this.getGlobalDestination().toString());
				timeLeftAtPOI = timeToSpendAtPOI;
				if (timeToSpendAtPOI == 0) {
					setNewGlobalDestination();
				} else {
					loc = this.getLocation();
				}

			} else if (--timeLeftAtPOI > 0) {
				loc = this.getLocation();
			} else {
				setNewGlobalDestination();
			}
		}

		if (getLocalDestination() == null) {
			setLocalDestination(this.getLocation());
		}
		double distanceToSource = this.getLocation().distanceTo(this.getPath().getSource());
		double localDestinationDistToSource = this.getLocalDestination().distanceTo(this.getPath().getSource());
		if (distanceToSource >= localDestinationDistToSource) {
			findLocalDestinationOnPath();
		}

		if (this.getLocation().distanceTo(this.getGlobalDestination()) < 10) {
			this.getBoid().update(new Vector2d(getLocalDestination().getX(), getLocalDestination().getY()),
					BehaviorEnum.ARRIVAL);
		} else {
			this.getBoid().update(new Vector2d(getLocalDestination().getX(), getLocalDestination().getY()),
					BehaviorEnum.SEEK);
		}

		loc = new Location(this.getBoid().getX(), this.getBoid().getY());
		return loc;

	}

	/**
	 * Method to set new Global Destination.
	 */
	private void setNewGlobalDestination() {
		Location newPOI = this.getPlannedTour().getNextLocation(getGlobalDestination(), 1);
		if (newPOI == null) {
			int index = this.getPlannedTour().getIndexPOI();
			newPOI = this.getPlannedTour().getLocationAt(++index);
		}
		if (newPOI != null) {
			this.setGlobalDestination(newPOI);
		}
		DStarLitePP dStar = null;
		dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(), this.getGlobalDestination());
		this.setPath(dStar.searchPath());
		// this.path = dStar.searchPath();
	}

	/**
	 * Set Local destination which is one of the farthest location on the path
	 * to global destination
	 * 
	 * @return
	 */
	private void findLocalDestinationOnPath() {
		Location loc = null;
		int _index = -1;
		int pathLength = 0;
		if (this.getPath() == null) {
			loc = getGlobalDestination();
		} else {
			pathLength = this.getPath().getPathSize();
			if (getLocalDestination() == null) {
				_index = 0;
			} else {
				_index = this.getPath().indexOfLocation(getLocalDestination());
			}
		}

		do {
			if (_index == pathLength || pathLength == 0) {
				break;
			}
			loc = this.getPath().getLocationAt(_index++);
		} while (RctaEnvironmentHelper.isVisible(context, this.getLocation(), loc));

		setLocalDestination(loc);

	}

	/**
	 * Method to return intended move of the agent in the crowd.
	 * 
	 * This behavior is the fastest. The agent in the crowd follow D* planned
	 * path without considering any conflicts with other agents in the crowd.
	 * 
	 * @return
	 */
	private Location getIntendedMoveCrowd_0() {
		return continuePlan();

	}

	/**
	 * Method to return intended move of the agent in the crowd.
	 * 
	 * This behavior look for micro conflicts and then decide either to stop for
	 * sometime or re-plan the path using D* lite. This behavior become
	 * computationally expensive as increase in the crowd members causing lots
	 * of D* path planning. It is not using the steering behaviors.
	 * 
	 * @return
	 */
	private Location getIntendedMoveCrowd_1() {

		Location newLocation = null;
		if (this.getMicroConflict() != null) {
			newLocation = resolveConflict1();
		} else {
			newLocation = continuePlan();
		}
		return newLocation;

	}

	/**
	 * Behavior 1:
	 * 
	 * Crowd agent continue its originally planned path, if there is no
	 * conflict.
	 * 
	 * Disadvantage: Crowd agent avoid obstacles but not other agents.
	 * 
	 * @return
	 */
	private Location continuePlan() {

		Location loc = null;
		if (this.getLocation().equals(this.getGlobalDestination())) {
			if (timeLeftAtPOI == 0 && timeToSpendAtPOI == 0) {
				timeToSpendAtPOI = this.getWaitingTime(this.getLocation().toString());
				timeLeftAtPOI = timeToSpendAtPOI;
				if (timeToSpendAtPOI == 0) {
					loc = stepToNextPOI();
				} else {
					loc = this.getLocation();
				}

			} else if (--timeLeftAtPOI > 0) {
				loc = this.getLocation();
			} else {
				loc = stepToNextPOI();
			}
		} else {
			loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
			if (loc == null) {
				loc = getGlobalDestination();
			}
		}
		return loc;
	}

	private Location getNextLocation() {
		Location loc;
		loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
		if (loc == null) {
			loc = getGlobalDestination();
		}
		return loc;
	}

	/**
	 * This method return first Step on a path toward next (point of interest)
	 * POI
	 * 
	 * @return
	 */
	private Location stepToNextPOI() {
		DStarLitePP dStar = null;
		Location loc = null;
		Location newPOI = this.getPlannedTour().getNextLocation(getGlobalDestination(), 1);
		if (newPOI == null) {
			int index = this.getPlannedTour().getIndexPOI();
			this.getPlannedTour().getLocationAt(++index);
		}
		if (newPOI != null) {
			this.setGlobalDestination(newPOI);
			dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(), this.getGlobalDestination());
			this.setPath(dStar.searchPath());
			loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
		}
		return loc;
	}

	/**
	 * Behavior 2a:
	 * 
	 * This method tells what to do when conflict of the way occur with any
	 * other agent. Simple approach is to wait for some random seconds and see
	 * if other agent has figured out the solution before you make move.
	 * otherwise continue walking with intention that other agent will give you
	 * right to continue. We have different speed for the agents. As of now they
	 * are not giving up their original speed, but ready to stop as long as
	 * required.
	 * 
	 * Following condition can lead to conflicts. 1) Both agent moving in same
	 * direction, 2) Both agent moving in opposite direction, 3) Both agent
	 * moving toward same point of intersection, 4)Both agent moving away from
	 * same point of intersection. 5)Both agents are violating their personal
	 * space when one agent moving away from point of intersection, whereas
	 * other one moving toward point of intersection.
	 * 
	 * we can reduce the number of condition shown above by considering agent
	 * direction as well.
	 * 
	 * 
	 * Disadvantage: This behavior does not avoid agent in 1 and 2 condition
	 * mentioned above
	 * 
	 * @return
	 */
	private Location resolveConflict1() {
		Location loc = null;
		if (this.getLocation().equals(this.getGlobalDestination())) {
			loc = continuePlan();
		} else {
			if (timeToResolveConflict == 0 && timeLeftToResolveConflict == 0) {
				timeToResolveConflict = context.getRandom().nextInt(CONFLICT_WAIT_TIME);
				timeLeftToResolveConflict = timeToResolveConflict;
				if (timeToResolveConflict > 0) {
					loc = this.getLocation();
				} else {
					loc = continuePlan();
				}
			} else if (--timeLeftToResolveConflict > 0) {
				loc = this.getLocation();
			} else {
				loc = continuePlan();
			}
		}

		return loc;
	}

	/**
	 * Behavior 2b: If encounter with another agent try to move aside and
	 * continue walking.
	 * 
	 * Disadvantage: To much computation for D*lite and hence program slow down
	 * or hang. Agents deadlock in the crowded situations
	 * 
	 * @return
	 */
	private Location resolveConflict2() {
		Location loc = null;
		if (this.getLocation().equals(this.getGlobalDestination())) {
			loc = continuePlan();
		} else {
			if (timeToResolveConflict == 0 && timeLeftToResolveConflict == 0) {
				timeToResolveConflict = context.getRandom().nextInt(CONFLICT_WAIT_TIME);
				timeLeftToResolveConflict = timeToResolveConflict;
				if (timeToResolveConflict > 0) {
					loc = this.getLocation();
				} else {
					loc = takeAction();
				}
			} else if (--timeLeftToResolveConflict > 0) {
				loc = this.getLocation();
			} else {
				loc = takeAction();
			}
		}

		return loc;
	}

	/**
	 * This method is called when agent need to decide his next action after
	 * valid conflict occur with other agent in the scenario. It can either find
	 * alternate route or continue walking
	 * 
	 * 
	 * 
	 * @return
	 */
	private Location takeAction() {
		Location loc = null;
		// Take action if next location is occupied and you can't wait anymore
		if (getNextLocation().equals(getGlobalDestination())) {
			loc = stepToNextPOI();
		} else {
			// Treat other agents as obstacle and find the path to destination.
			//
			MicroConflict mc = this.getMicroConflict();
			if (mc != null) {
				AbstractPhysicalAgent myself = mc.getParticipants().get(0);
				AbstractPhysicalAgent others = mc.getParticipants().get(1);
				if (others.getName().equals(this.getName())) {
					myself = mc.getParticipants().get(1);
					others = mc.getParticipants().get(0);
				}
				loc = avoidOtherAgent(myself, others);
			}
		}
		return loc;
	}

	/**
	 * This method re-plan the new path by considering personal area of other
	 * agent as obstacle in the map. First, we are making personal area of other
	 * agent as obstacle in obstacle map. Second, re-run the D* algo. If no path
	 * exist, continue with last plan, otherwise, trace new path. Third, recover
	 * original obstacle Map
	 * 
	 * @param myself
	 * @param others
	 * @return
	 */
	private Location avoidOtherAgent(AbstractPhysicalAgent myself, AbstractPhysicalAgent others) {
		Location newLocation = null;
		Area myArea = new Area(myself.getAzPersonalSpace().getShape());
		Area otherAgentArea = new Area(others.getAzPersonalSpace().getShape());
		otherAgentArea.subtract(myArea);

		Rectangle2D.Double bounds = (Rectangle2D.Double) otherAgentArea.getBounds2D();
		HashMap<Location, Double> saveArea = new HashMap<>();
		for (double x = bounds.getMinX(); x < bounds.getMaxX(); x++) {
			for (double y = bounds.getMinY(); y < bounds.getMaxY(); y++) {
				Location current = new Location(x, y);
				double value = (double) context.getEnvironmentModel().getPropertyAt(MAP_OBSTACLES, x, y);
				saveArea.put(current, value);
				if (value == 0) {
					context.getEnvironmentModel().setPropertyAt(MAP_OBSTACLES, x, y, (value + 1) > 0 ? value + 1 : 1);
				}
			}
		}
		// newLocation = searchPath();
		newLocation = searchPath2(others);
		// Recover Obstacle background
		for (Location loc : saveArea.keySet()) {
			context.getEnvironmentModel().setPropertyAt(MAP_OBSTACLES, loc.getX(), loc.getY(), saveArea.get(loc));
		}
		return newLocation;
	}

	/**
	 * Run D* Lite algo from current location to GlobalDestination.
	 * 
	 * @return
	 */
	private Location searchPath() {
		DStarLitePP dStar = null;
		Location loc = null;
		dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(), this.getGlobalDestination());

		PlannedPath newPath = dStar.searchPath();
		if (newPath == null)
			return continuePlan();
		else
			this.setPath(newPath);
		loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
		return loc;
	}

	private Location searchPath2(AbstractPhysicalAgent others) {

		int step = (int) speed;
		Location nextOnPath = this.getPath().getNextLocation(this.getLocation(), step);
		while (others.getAzPersonalSpace().getShape().contains(nextOnPath.getX(), nextOnPath.getY())) {
			nextOnPath = this.getPath().getNextLocation(this.getLocation(), ++step);
		}
		DStarLitePP dStar = null;
		Location loc = null;
		dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(), nextOnPath);
		PlannedPath newPath = dStar.searchPath();
		if (newPath == null)
			return continuePlan();
		else {
			while (this.getPath().getNextLocation(nextOnPath, step) != null) {
				newPath.addLocation(this.getPath().getNextLocation(nextOnPath, step++));
			}
			this.setPath(newPath);
		}
		loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
		return loc;
	}

	/**
	 * Default method which is same as strategy 0, which is to walk on D*
	 * planned path ignore other agents in the crowd
	 * 
	 * @param experimentConfiguration
	 * @return
	 */
	public Location getIntendedMove(ExperimentConfiguration experimentConfiguration) {

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
			case CROWD_SEEK_VIP_MOVEMENT:

				Human vip = null;
				for (Human temp : context.getSoldiers()) {
					if (temp.getName().contains("VIP")) {
						vip = temp;
					}
				}
				this.steering().seek(new Vector2D(vip.getLocation().getX(), vip.getLocation().getY()), 10);
				// this.steering().collisionAvoidance();
				this.steering().update();
				this.setGlobalDestination(this.getLocation());
				dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(), this.getGlobalDestination());
				this.setPath(dStar.searchPath());
				break;
			case CROWD_MOVEMENT:
				timeLeftAtPOI = this.getWaitingTime(this.getLocation().toString());
				Location loc = this.getPlannedTour().getNextLocation(this.getLocation(), 1);
				if (loc != null) {
					this.setGlobalDestination(loc);
					dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(),
							this.getGlobalDestination());
					this.setPath(dStar.searchPath());
				}

				break;

			default:
				break;

			}

		}
		// this.setNextLocation(this.getLocation());
		if (timeLeftAtPOI == 0) {
			Location loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
			if (loc != null)
				return loc;
			else
				return getGlobalDestination();
		} else {
			this.setWaitingTime(this.getLocation().toString(), --timeLeftAtPOI);
			return this.getLocation();
		}

	}

	/**
	 * Method to decide the next location coordinate
	 * 
	 * @return
	 */
	public Location getIntendedMoveObstacleAvoidance() {
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

		double movementAngle = RctaEnvironmentHelper.getAngle(currentLocation, intendedLocation, true);
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
	public void moveWithArrowKeys(String direction) {
		Location currentLoc = this.getLocation();
		Location newLocation = new Location(0, 0);
		switch (direction) {
		case "UP":
			newLocation = new Location(currentLoc.getX(), currentLoc.getY() - this.getSpeed());
			if (RctaEnvironmentHelper.isLocationOccupied(context, newLocation, 0))
				return;
			this.setLocation(newLocation);
			break;
		case "DOWN":
			newLocation = new Location(currentLoc.getX(), currentLoc.getY() + this.getSpeed());
			if (RctaEnvironmentHelper.isLocationOccupied(context, newLocation, 0))
				return;
			this.setLocation(newLocation);
			break;
		case "LEFT":
			newLocation = new Location(currentLoc.getX() - this.getSpeed(), currentLoc.getY());
			if (RctaEnvironmentHelper.isLocationOccupied(context, newLocation, 0))
				return;
			this.setLocation(newLocation);
			break;
		case "RIGHT":
			newLocation = new Location(currentLoc.getX() + this.getSpeed(), currentLoc.getY());
			if (RctaEnvironmentHelper.isLocationOccupied(context, newLocation, 0))
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

	// public PlannedPath getPath() {
	// return path;
	// }
	//
	// public void setPath(PlannedPath path) {
	// this.path = path;
	// }

	public PlannedTour getPlannedTour() {
		return plannedTour;
	}

	public int getWaitingTime(String plannedStop) {
		return this.waitingTime.get(plannedStop);
	}

	public void setWaitingTime(String plannedStop, int waitTime) {
		this.waitingTime.put(plannedStop, waitTime);
	}

	public void setContext(RctaContext context) {
		this.context = context;
	}

	public Boid getBoid() {
		return boid;
	}

	public void setBoid(Location initialPosition) {
		this.boid = new Boid(new Vector2d(initialPosition.getX(), initialPosition.getY()));
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
