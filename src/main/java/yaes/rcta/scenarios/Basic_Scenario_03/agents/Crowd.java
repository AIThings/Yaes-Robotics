package yaes.rcta.scenarios.Basic_Scenario_03.agents;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractHumanAgent;
import yaes.rcta.movement.DStarLitePP;
import yaes.rcta.scenarios.Basic_Scenario_03.Context;
import yaes.rcta.scenarios.Basic_Scenario_03.Helper;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

public class Crowd extends AbstractHumanAgent implements constRCTA {

	private static final long serialVersionUID = 3706056854541867721L;
	private Context context;
	private double speed;
	private PlannedPath localPlanPath = new PlannedPath();

	public Crowd(String name, Location location, double heading) {
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
			loc = getMoveFollowPath();
//			 loc = getMove();

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

	private Location getMove() {
		Location loc = null;

		if (this.getLocation().equals(this.getGlobalDestination())) {
			this.setPath(new PlannedPath(this.getLocation(), this.getLocation()));
			this.localPlanPath = new PlannedPath();
			
			setGlobalDestinationAsRandomPoI();
			DStarLitePP dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(),
					this.getGlobalDestination());
			PlannedPath pathExp = dStar.searchPath();
			if (pathExp == null) {
				return this.getLocation();
			}
			this.setPath(dStar.searchPath());
			this.setDesiredPath(this.getPath());
			loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
			if (loc == null) {
				loc = this.getGlobalDestination();
			}
			this.setLocalDestination(loc);
		}
		else if (this.getLocation().equals(this.getLocalDestination())) {
			this.localPlanPath = new PlannedPath();
			loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
			if (loc == null) {
				loc = this.getGlobalDestination();
			}
			this.setLocalDestination(loc);
		}else {
			loc = this.localPlanPath.getNextLocation(this.getLocation(), (int) speed);
			if (loc == null) {
				loc = this.getLocalDestination();
			}
			if (Helper.isLocationOccupied(context, this, loc, 2)) {
				loc = findLocalPath();
			}
			
			
		}		
		
		if (Helper.isLocationOccupied(context, this, loc, 2)) {

			// Find local Destination
			findLocalDestination(this.getLocation());

			Helper.agent = this;
			DStarLitePP dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(),
					this.getLocalDestination()) {
				private static final long serialVersionUID = -6624184457874279664L;

				@Override
				protected boolean isAccessible(Location loc) {
					if (Helper.isLocationOccupied(context, Helper.agent, loc, 2)) {
						return false;
					}
					return true;
				}
			};
			PlannedPath pathExp = dStar.searchPath();

			if (pathExp == null) {
				return this.getLocation();
			}

			this.localPlanPath  = pathExp;
			this.setDesiredPath(this.localPlanPath);
			loc = this.localPlanPath.getNextLocation(this.getLocation(), (int) speed);
			if (loc == null) {
				loc = this.getLocalDestination();
			}
		}
		return loc;
	}

	private Location  findLocalPath(){
			Location loc;
			Helper.agent = this;
			DStarLitePP dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(),
					this.getLocalDestination()) {
				private static final long serialVersionUID = -6624184457874279664L;

				@Override
				protected boolean isAccessible(Location loc) {
					if (Helper.isLocationOccupied(context, Helper.agent, loc, 2)) {
						return false;
					}
					return true;
				}
			};
			PlannedPath pathExp = dStar.searchPath();

			if (pathExp == null) {
				return this.getLocation();
			}
			this.localPlanPath  = pathExp;
			this.setDesiredPath(this.localPlanPath);
			loc = this.localPlanPath.getNextLocation(this.getLocation(), (int) speed);
			if (loc == null) {
				loc = this.getLocalDestination();
			}
			return loc;
		
	}
	private void findLocalDestination(Location loc) {
		Location nxt = this.getPath().getNextLocation(loc, (int) speed);
		if (nxt == null) {
			this.setLocalDestination(this.getGlobalDestination());
			return;
		}
		if (Helper.isLocationOccupied(context, this, nxt, 2)) {
			findLocalDestination(nxt);
		} else {
			this.setLocalDestination(nxt);
		}

	}

	/**
	 * If source and destination are same, assign new random destination from
	 * the pre-canned destinatios.
	 * 
	 * If next location is occupied by any other agent. Search new path avoiding
	 * other agents
	 * 
	 * If no path found, do not move.
	 * 
	 * @return
	 */
	private Location getMoveFollowPath() {
		Location loc = null;
		if (this.getLocation().equals(this.getGlobalDestination())) {
			this.setPath(new PlannedPath(this.getLocation(), this.getLocation()));
			setGlobalDestinationAsRandomPoI();
		}
		if (this.getPath().getPathSize() == 0) {
			Helper.agent = this;
			DStarLitePP dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(),
					this.getGlobalDestination()) {
				private static final long serialVersionUID = -6624184457874279664L;

				@Override
				protected boolean isAccessible(Location loc) {
					if (Helper.isLocationOccupied(context, Helper.agent, loc, 1)) {
						return false;
					}
					return true;
				}
			};
			PlannedPath pathExp = dStar.searchPath();
			if (pathExp == null) {
				return this.getLocation();
			}
			this.setPath(dStar.searchPath());
			this.setDesiredPath(this.getPath());
			loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
		} else {
			loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
		}
		if (loc == null) {
			return this.location;
		}
		if (Helper.isLocationOccupied(context, this, loc, 1)) {
			Helper.agent = this;
			DStarLitePP dStar = new DStarLitePP(context.getEnvironmentModel(), this.getLocation(),
					this.getGlobalDestination()) {
				private static final long serialVersionUID = -6624184457874279664L;

				@Override
				protected boolean isAccessible(Location loc) {
					if (Helper.isLocationOccupied(context, Helper.agent, loc, 1)) {
						return false;
					}
					return true;
				}
			};
			PlannedPath pathExp = dStar.searchPath();
			if (pathExp == null) {
				return this.getLocation();
			}
			this.setPath(dStar.searchPath());
			this.setDesiredPath(this.getPath());
			loc = this.getPath().getNextLocation(this.getLocation(), (int) speed);
		}
		if (loc == null) {
			return this.getPath().getDestination();
		}
		return loc;
	}

	private void mouseResponse(Location location) {

		double dist = getLocation().distanceTo(location);
		if (dist <= getRadiusPersonalSpace()) {
			setFocus(true);
		} else {
			setFocus(false);
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
