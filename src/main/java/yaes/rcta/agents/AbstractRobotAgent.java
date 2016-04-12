package yaes.rcta.agents;

import yaes.rcta.agents.zones.azMovementCone;
import yaes.rcta.agents.zones.azPersonalSpace;
import yaes.rcta.agents.zones.azPhysicalRobot;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;

/**
 * Create a robot agent : assume that the Location metric are centimeters
 * 
 * @author Taranjeet
 * 
 */
public abstract class AbstractRobotAgent extends AbstractPhysicalAgent {

	private static final long serialVersionUID = -4508977709887516480L;
	private double widthRobot = 60;
	private double radiusPersonalSpace = 100;
	private double diameterMovementCone = 500;
	private double angleMovementCone = 40;

	// Physical agent movement path
	protected Path actualPath = null;
	protected Path desiredPath = null;
	private boolean displayDesiredTrace = false;
	private boolean displayActualPathTrace = false;

	public AbstractRobotAgent(String name, Location location, double heading) {
		super(name, location, heading);

	}

	@Override
	public void createZones() {
		azPhysical = new azPhysicalRobot(widthRobot);
		azPhysical.setValues(10.0, widthRobot * 2);
		azPersonalSpace = new azPersonalSpace(radiusPersonalSpace);
		azPhysical.setValues(5.0, 2 * radiusPersonalSpace);
		azMovementCone = new azMovementCone(angleMovementCone,
				diameterMovementCone);
		azPhysical.setValues(5.0, diameterMovementCone);
		updateZones();

	}
	
	public Path getActualPath() {
		return actualPath;
	}

	public void setActualPath(Path actualPath) {
		this.actualPath = actualPath;
	}
	public boolean isDisplayActualPathTrace() {
		return displayActualPathTrace;
	}

	public void setDisplayActualPathTrace(boolean displayActualPathTrace) {
		this.displayActualPathTrace = displayActualPathTrace;
	}
	
	public boolean isDisplayDesiredTrace() {
		return displayDesiredTrace;
	}

	public void setDisplayDesiredTrace(boolean displayDesiredTrace) {
		this.displayDesiredTrace = displayDesiredTrace;
	}
	
	public Path getDesiredPath() {
		return desiredPath;
	}

	public void setDesiredPath(Path desiredPath) {
		this.desiredPath = desiredPath;
	}

	public double getWidthRobot() {
		return widthRobot;
	}

	public void setWidthRobot(double widthRobot) {
		this.widthRobot = widthRobot;
	}

	public double getRadiusPersonalSpace() {
		return radiusPersonalSpace;
	}

	public void setRadiusPersonalSpace(double radiusPersonalSpace) {
		this.radiusPersonalSpace = radiusPersonalSpace;
	}

	public double getDiameterMovementCone() {
		return diameterMovementCone;
	}

	public void setDiameterMovementCone(double diameterMovementCone) {
		this.diameterMovementCone = diameterMovementCone;
	}

	public double getAngleMovementCone() {
		return angleMovementCone;
	}

	public void setAngleMovementCone(double angleMovementCone) {
		this.angleMovementCone = angleMovementCone;
	}

}
