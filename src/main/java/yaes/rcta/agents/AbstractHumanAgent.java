package yaes.rcta.agents;

import yaes.rcta.agents.zones.azFocusFrame;
import yaes.rcta.agents.zones.azMovementCone;
import yaes.rcta.agents.zones.azPersonalSpace;
import yaes.rcta.agents.zones.azPhysicalHuman;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;

/**
 * Creates a human agent: assume that the Location metric are centimeters
 * 
 * @author Taranjeet
 *
 */
public abstract class AbstractHumanAgent extends AbstractPhysicalAgent {

	private static final long serialVersionUID = -8688450172332481845L;

	// Parameters for creating Zones boundaries
	private double widthShoulder = 60;
	private double radiusPersonalSpace = 100;
	private double diameterMovementCone = 400;
	private double angleMovementCone = 30;

	public AbstractHumanAgent(String name, Location location, double heading) {
		super(name, location, heading);
	}

	@Override
	public void createZones() {
		this.azPhysical = new azPhysicalHuman(this.widthShoulder);
		this.azPhysical.setValues(10.0, 2 * this.widthShoulder);

		this.azPersonalSpace = new azPersonalSpace(this.radiusPersonalSpace);
		this.azPersonalSpace.setValues(5.0, 2 * this.radiusPersonalSpace);

		this.azMovementCone = new azMovementCone(this.diameterMovementCone, this.angleMovementCone);
		this.azMovementCone.setValues(5.0, this.diameterMovementCone);

		this.azFocusFrame = new azFocusFrame(this.radiusPersonalSpace);
		this.azFocusFrame.setValues(5.0, 2 * this.radiusPersonalSpace);

		this.updateZones();
	}

	

	public double getWidthShoulder() {
		return widthShoulder;
	}

	public void setWidthShoulder(double widthShoulder) {
		this.widthShoulder = widthShoulder;
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
