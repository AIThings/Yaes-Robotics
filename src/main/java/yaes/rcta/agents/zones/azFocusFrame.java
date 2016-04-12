package yaes.rcta.agents.zones;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import yaes.world.physical.location.Location;

public class azFocusFrame extends AbstractAgentZone {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6748137101200620824L;
	private double rectRadius;

	public azFocusFrame(double rectRadius){
		super();
		this.rectRadius = rectRadius;
	}
	@Override
	public void updateShape(Location agentLocation, double agentHeading) {
		this.agentLocation = agentLocation;
		this.agentHeading = agentHeading;
				 
		Area area = new Area();
		Rectangle2D.Double rect = new Rectangle2D.Double(-rectRadius,
				-rectRadius, 2*rectRadius, 2*rectRadius);
		
		Area areaRect = new Area(rect);
		area.add(areaRect);
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getTranslateInstance(
				agentLocation.getX(), agentLocation.getY()));
		at.concatenate(AffineTransform.getRotateInstance(agentHeading));
		area.transform(at);
		setShape(area);

	}

}
