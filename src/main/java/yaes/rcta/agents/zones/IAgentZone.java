/**
 * 
 */
package yaes.rcta.agents.zones;

import java.awt.Shape;

import yaes.world.physical.location.Location;

/**
 * This interface represents a zone attached to the physical agent
 * @author Taranjeet
 *
 */
public interface IAgentZone {

	/**
	 * Represents the shape in the coordinate system represented in the location system 
	 * @return
	 */
	Shape getShape();
	
	/**
     * Represents the value of the zone at a certain system. By convention, this MUST be 
     * zero for locations outside the system. 
     * @param location
     * @return
     */
	double getValue(Location location);
}
