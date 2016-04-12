package yaes.rcta.agents.zones;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import yaes.world.physical.location.Location;

/**
 * Abstract class for implementing the IAgentZone. Other model will implement
 * from here.
 * 
 * @author Taranjeet
 * 
 */
public abstract class AbstractAgentZone implements IAgentZone, Serializable {

	private static final long serialVersionUID = 1482746800454937782L;

	private Shape shape;
	protected Location agentLocation;
	protected double agentHeading;

	/**
	 * The distance at which the value decreases to zero. If this is set to zero
	 * the value does not decrease at all.
	 */
	protected double diameter = 0.0;
	/**
	 * The maximum value of the zones
	 */
	protected double maxValue = 1.0;

	@Override
	public Shape getShape() {
		return shape;
	}

	/**
	 * sets the decay of the values: the maximum value and the diameter at which
	 * it decays
	 * 
	 * @param maxValue
	 * @param diameter
	 */
	public void setValues(double maxValue, double diameter) {
		this.maxValue = maxValue;
		this.diameter = diameter;
	}

	@Override
	public double getValue(Location location) {
		if (!shape.contains(location.asPoint())) {
			return 0;
		}
		if (diameter == 0.0) {
			return maxValue;
		}
		return ((maxValue * location.distanceTo(agentLocation)) / diameter);

	}

	/**
	 * This function will create the shape. To be implemented in the
	 * implementing code
	 * 
	 * @param agentLocation
	 * @param agentHeading
	 */
	public abstract void updateShape(Location agentLocation, double agentHeading);

	public void setShape(Shape shape) {
		// To make the shape serializable
		this.shape = AffineTransform.getTranslateInstance(0, 0)
				.createTransformedShape(shape);
	}
}
