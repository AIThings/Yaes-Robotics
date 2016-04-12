package yaes.rcta.agents.zones;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import yaes.world.physical.location.Location;

/**
 * Implement the physical shape of a human
 * 
 * @author Taranjeet
 * 
 */
public class azPhysicalHuman extends AbstractAgentZone {

	private static final long serialVersionUID = 2912873840661742042L;
	private double width;
	private double height;
	private double circleRadius;

	public azPhysicalHuman(double width) {
		this.width = width;
		this.height = width / 4;
		this.circleRadius = this.height;

	}

	/**
	 * Creates the shape for the representation of a human over visual
	 * simulation
	 */
	@Override
	public void updateShape(Location agentLocation, double agentHeading) {
		this.agentLocation = agentLocation;
		this.agentHeading = agentHeading;
		Area area = new Area();

		Rectangle2D.Double body = new Rectangle2D.Double(-width / 2,
				-height / 2, width, height);
		Area areaBody = new Area(body);
		area.add(areaBody);
		Ellipse2D.Double head = new Ellipse2D.Double(-circleRadius,
				-circleRadius, 2 * circleRadius, 2 * circleRadius);
		Area areaCircle = new Area(head);
		area.add(areaCircle);

		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getTranslateInstance(
				agentLocation.getX(), agentLocation.getY()));
		at.concatenate(AffineTransform.getRotateInstance(agentHeading + Math.PI
				/ 2));
		area.transform(at);
		setShape(area);
	}

}
