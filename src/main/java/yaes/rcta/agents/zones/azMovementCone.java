package yaes.rcta.agents.zones;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;

import yaes.world.physical.location.Location;

public class azMovementCone extends AbstractAgentZone {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3244157750824958258L;
	private double angleRange;
	private double distance;

	public azMovementCone(double angleRange, double distance) {
		super();
		this.angleRange = angleRange;
		this.distance = distance;
	}

	@Override
	public void updateShape(Location agentLocation, double agentHeading) {
		this.agentLocation = agentLocation;
		this.agentHeading = agentHeading;
		Area area = new Area();
		Arc2D.Double arc = new Arc2D.Double(-distance / 2, -distance / 2,
				distance, distance, -angleRange / 2, angleRange, Arc2D.PIE);
		Area areaBody = new Area(arc);
		area.add(areaBody);
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getTranslateInstance(
				agentLocation.getX(), agentLocation.getY()));
		// - angleRange * Math.PI / 360.0
		at.concatenate(AffineTransform.getRotateInstance(agentHeading));
		area.transform(at);
		setShape(area);

	}

}
