package yaes.rcta.agents;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import yaes.framework.agent.IAgent;
import yaes.rcta.agents.gametheory.Game;
import yaes.rcta.agents.gametheory.MicroConflict;
import yaes.rcta.agents.gametheory.iGameStrategy;
import yaes.rcta.agents.zones.AbstractAgentZone;
import yaes.rcta.environment.HeatmapHelper;
import yaes.rcta.movement.SteeringHelper;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.INamed;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;
import yaes.world.physical.path.PlannedPath;

/**
 * Abstract Physical Agent and its shape boundaries
 * 
 * @author Taranjeet
 * 
 */
public abstract class AbstractPhysicalAgent implements IAgent, IMoving, INamed {

	private static final long serialVersionUID = 346791201623652350L;

	protected String name;
	protected String currentMove;

	// Game theory move (C for cooperate or D for defeat)
	// private String currentMove;

	// Location coordinates of physical agent
	protected Location location;
	private Location globalDestination;
	private Location localDestination;

	// Heading direction in radians
	private double heading;

	// Total distance covered
	private double distanceTravelled = 0;

	// This agent will be using keyboard strokes when focused
	private boolean isFocus = false;

	// Actual traversed path
	private Path actualPath = null;
	// Pre-calculated path
	private Path desiredPath = null;
	private boolean displayDesiredTrace = false;
	private boolean displayActualPathTrace = false;

	// To enable visual display of path followed and next location/desire
	// location
	private boolean displayTrace = false;
	private boolean displayTraceTicks = false;

	// To enable visual display of destinations
	private boolean displayDestination = false;

	// Physical zones for defining personal space boundaries.
	protected AbstractAgentZone azPhysical = null;
	protected AbstractAgentZone azPersonalSpace = null;
	protected AbstractAgentZone azMovementCone = null;
	protected AbstractAgentZone azFocusFrame = null;
	protected AbstractAgentZone azMovementTrace = null;

	// Physical agent movement path
	protected PlannedPath plannedPath = null;

	/**
	 * A current micro-conflict in which the abstract agent might be involved.
	 */
	protected MicroConflict microConflict;
	protected iGameStrategy gameStrategy;
	/**
	 * The social cost collected over the lifetime of the agent set from the
	 * games it participated
	 */
	private CostVector costVector = new CostVector();

	/**
	 * Steering control system parameters
	 */
	private Vector2D velocity;
	private double mass = 1;
	private double maxSpeed = 1;
	private SteeringHelper steering;

	/**
	 * 
	 * Set name, location and heading direction of an agent. Create Zones for
	 * personal space boundaries.
	 * 
	 * @param name
	 * @param location
	 * @param heading
	 */
	public AbstractPhysicalAgent(String name, Location location, double heading) {
		super();
		this.name = name;
		this.location = location;
		this.heading = heading;
		this.actualPath = new Path();
		createZones();
	}

	public Vector2D getVelocity() {
		return this.velocity;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public SteeringHelper getSteering() {
		return steering;
	}

	public void setSteering(SteeringHelper steering) {
		this.steering = steering;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getMaxVelocity() {
		return this.maxSpeed;
	}

	public Vector2D getPosition() {
		return new Vector2D(this.location.getX(), this.location.getY());
	}

	public double getMass() {
		return this.mass;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	public SteeringHelper steering() {
		return this.steering;
	}

	public void setPosition(Vector2D position) {
		this.setLocation(new Location(position.getX(), position.getY()));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	/**
	 * Set the location and updates the heading to point to the direction of the
	 * movement
	 */
	@Override
	public void setLocation(Location location) {
		Location oldLocation = this.location;
		this.location = location;
		this.distanceTravelled = this.distanceTravelled + oldLocation.distanceTo(location);
		// Heading direction update
		if (oldLocation.distanceTo(location) == 0.0) {
			return;
		}
		double angle = Math.atan2(location.getY() - oldLocation.getY(), location.getX() - oldLocation.getX());
		this.heading = angle;
		updateZones();
		this.getActualPath().addLocation(location);

	}

	/**
	 * Update the zones parameters
	 */
	public void updateZones() {
		if (azMovementCone != null) {
			azMovementCone.updateShape(location, heading);
		}
		if (azPersonalSpace != null) {
			azPersonalSpace.updateShape(location, heading);
		}
		if (azPhysical != null) {
			azPhysical.updateShape(location, heading);
		}
		if (azFocusFrame != null) {
			azFocusFrame.updateShape(location, heading);
		}
	}

	/**
	 * Function to be implemented by child class which create the appropriate
	 * zones
	 */
	public abstract void createZones();

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public double getDistanceTravelled() {
		return distanceTravelled;
	}

	public void setDistanceTravelled(double distanceTravelled) {
		this.distanceTravelled = distanceTravelled;
	}

	public AbstractAgentZone getAzPhysical() {
		return azPhysical;
	}

	public void setAzPhysical(AbstractAgentZone azPhysical) {
		this.azPhysical = azPhysical;
	}

	public AbstractAgentZone getAzPersonalSpace() {
		return azPersonalSpace;
	}

	public void setAzPersonalSpace(AbstractAgentZone azPersonalSpace) {
		this.azPersonalSpace = azPersonalSpace;
	}

	public AbstractAgentZone getAzFocusFrame() {
		return azFocusFrame;
	}

	public void setAzFocusFrame(AbstractAgentZone azFocusFrame) {
		this.azFocusFrame = azFocusFrame;
	}

	public AbstractAgentZone getAzMovementCone() {
		return azMovementCone;
	}

	public void setAzMovementCone(AbstractAgentZone azMovementCone) {
		this.azMovementCone = azMovementCone;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Check if agent is mouse focused or not
	 * 
	 * @return
	 */
	public boolean isFocus() {
		return isFocus;
	}

	/**
	 * This agent will be using keyboard strokes when focused with mouse
	 * 
	 * @param isFocus
	 */
	public void setFocus(boolean isFocus) {
		this.isFocus = isFocus;
	}

	/**
	 * If display of the path followed is enabled.
	 * 
	 * @return true/false
	 */
	public boolean isDisplayTrace() {
		return displayTrace;
	}

	/**
	 * To enable visual display of path followed and next location/desire
	 * location
	 * 
	 * @param displayTrace
	 */
	public void setDisplayTrace(boolean displayTrace) {
		this.displayTrace = displayTrace;
	}

	public boolean isDisplayTraceTicks() {
		return displayTraceTicks;
	}

	public void setDisplayTraceTicks(boolean displayTraceTicks) {
		this.displayTraceTicks = displayTraceTicks;
	}

	public PlannedPath getPath() {
		return plannedPath;
	}

	public void setPath(PlannedPath path) {
		this.plannedPath = path;
	}

	/**
	 * The agent plays a game
	 * 
	 * @param game
	 * @return
	 */
	public String play(Game game) {
		String move = gameStrategy.move(name, game);
		this.currentMove = move; // TODO: Maybe a better solution to find
									// currentMove
		game.setDoneMove(name, move);
		return move;
	}

	// TODO: Maybe a better solution to find currentMove
	public String getCurrentMove() {
		return this.currentMove;
	}

	/**
	 * The next location which will be done if the action is not inhibited
	 * 
	 * @return
	 */
	public abstract Location getIntendedMove();

	/**
	 * Adds the costs of the various zones to an environment model
	 * 
	 * @param emGlobalCost
	 */
	public void addCosts(EnvironmentModel emGlobalCost, String propertyName, double scalePhysical,
			double scalePersonalSpace, double scaleMovementCone) {
		HeatmapHelper.addAgentZone(emGlobalCost, getAzPhysical(), propertyName, scalePhysical);
		HeatmapHelper.addAgentZone(emGlobalCost, getAzPersonalSpace(), propertyName, scalePersonalSpace);
		HeatmapHelper.addAgentZone(emGlobalCost, getAzMovementCone(), propertyName, scaleMovementCone);
	}

	/**
	 * @return the microConflict
	 */
	public MicroConflict getMicroConflict() {
		return microConflict;
	}

	/**
	 * @param microConflict
	 *            the microConflict to set
	 */
	public void setMicroConflict(MicroConflict microConflict) {
		this.microConflict = microConflict;
	}

	/**
	 * @return the costVector
	 */
	public CostVector getCostVector() {
		return costVector;
	}

	/**
	 * @return the gameStrategy
	 */
	public iGameStrategy getGameStrategy() {
		return gameStrategy;
	}

	/**
	 * @param gameStrategy
	 *            the gameStrategy to set
	 */
	public void setGameStrategy(iGameStrategy gameStrategy) {
		this.gameStrategy = gameStrategy;
	}

	/**
	 * This method return the globalDestination location. We did D* lite path
	 * planning from one globalDestination to next globalDestination. This path
	 * provide localDestination for steering movement to run.
	 * 
	 * @return
	 */
	public Location getGlobalDestination() {
		return globalDestination;
	}

	public void setGlobalDestination(Location gDestination) {
		this.globalDestination = gDestination;
	}

	/**
	 * This method return the intermediate destination for steering to pursue
	 * which lies between two globalDestinations.
	 * 
	 * @return
	 */
	public Location getLocalDestination() {
		return localDestination;
	}

	public void setLocalDestination(Location localDestination) {
		this.localDestination = localDestination;
	}

	public boolean isDisplayDestination() {
		return displayDestination;
	}

	/**
	 * //To enable visual display of destinations
	 * 
	 * @param displayDestination
	 */
	public void setDisplayDestination(boolean displayDestination) {
		this.displayDestination = displayDestination;
	}

	/**
	 * Method to return traversed path
	 * @return
	 */
	public Path getActualPath() {
		return actualPath;
	}

	/**
	 * Show traversed path if enable
	 * @return
	 */
	public boolean isDisplayActualPathTrace() {
		return displayActualPathTrace;
	}

	/**
	 * Method to enable or disable trace of traversed path.
	 * @param displayActualPathTrace
	 */
	public void setDisplayActualPathTrace(boolean displayActualPathTrace) {
		this.displayActualPathTrace = displayActualPathTrace;
	}

	/**
	 * Show pre-canned path if enable
	 * @return
	 */
	public boolean isDisplayDesiredTrace() {
		return displayDesiredTrace;
	}

	/**
	 * Enable or disable trace of pre-canned path.
	 * @param displayDesiredTrace
	 */
	public void setDisplayDesiredTrace(boolean displayDesiredTrace) {
		this.displayDesiredTrace = displayDesiredTrace;
	}

	/**
	 * Method to return pre-canned path
	 * @return
	 */
	public Path getDesiredPath() {
		return desiredPath;
	}

	/**
	 * Method to set pre-canned path
	 * @param desiredPath
	 */
	public void setDesiredPath(Path desiredPath) {
		this.desiredPath = desiredPath;
	}
}
