package yaes.rcta.agents.zones;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import yaes.world.physical.location.Location;

public class azPersonalSpace extends AbstractAgentZone {
	private static final long serialVersionUID = 5170469759703211204L;
	private double circleRadius;

	/**
	 * Creates the personal space
	 * 
	 * @param circleRadius
	 */
	public azPersonalSpace(double circleRadius) {
		super();
		this.circleRadius = circleRadius;
	}

	@Override
	public void updateShape(Location agentLocation, double agentHeading) {
		this.agentLocation = agentLocation;
		this.agentHeading = agentHeading;
		Area area = new Area();
		Ellipse2D.Double circle = new Ellipse2D.Double(-circleRadius,
				-circleRadius, 2 * circleRadius, 2 * circleRadius);
		Area areaCircle = new Area(circle);
		area.add(areaCircle);
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getTranslateInstance(
				agentLocation.getX(), agentLocation.getY()));
		at.concatenate(AffineTransform.getRotateInstance(agentHeading));
		area.transform(at);
		setShape(area);
	}

}
