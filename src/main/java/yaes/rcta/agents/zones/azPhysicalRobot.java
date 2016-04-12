package yaes.rcta.agents.zones;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import yaes.world.physical.location.Location;

public class azPhysicalRobot extends AbstractAgentZone {

	private static final long serialVersionUID = 8067059646618889672L;
	private double width;
	private double height;
	private double r;

	public azPhysicalRobot(double width) {
		this.width = width;
		this.height = 1.5 * width;
		this.r = this.height / 10;
	}

	@Override
	public void updateShape(Location agentLocation, double agentHeading) {
		this.agentLocation = agentLocation;
		this.agentHeading = agentHeading;
		Area area = new Area();
		Rectangle2D.Double body = new Rectangle2D.Double(-width / 2,
				-height / 2, width, height);
		Area areaBody = new Area(body);
		area.add(areaBody);
		// add the wheels
		for (int i = 0; i != 2; i++) {
			for (int j = 0; j != 3; j++) {
				double x = -width / 2 + i * width;
				double y = -height / 2 + 2 * r + 3 * j * r;
				Ellipse2D.Double wheel = new Ellipse2D.Double(x - r, y - r,
						2 * r, 2 * r);
				Area areaWheel = new Area(wheel);
				area.add(areaWheel);
			}
		}
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getTranslateInstance(
				agentLocation.getX(), agentLocation.getY()));
		at.concatenate(AffineTransform.getRotateInstance(agentHeading + Math.PI
				/ 2));
		area.transform(at);
		setShape(area);

	}

}
