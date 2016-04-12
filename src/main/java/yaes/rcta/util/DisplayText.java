package yaes.rcta.util;

import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.INamed;
import yaes.world.physical.location.Location;

/**
 * An object which represents in a YAES diagram a changeable text representation
 * shown in the visual field (usually, a label). 
 * 
 * @author lboloni
 *
 */
public class DisplayText implements INamed, IMoving {

	private static final long serialVersionUID = 6636378942248601796L;
	private Location displayLocation;
	private String displayText;

	public DisplayText() {
		this.displayLocation = new Location(0, 0);
		this.displayText = "Nothing";
	}

	public DisplayText(Location displayLocation) {
		this.displayLocation = displayLocation;
		this.displayText = "Nothing";
	}

	@Override
	public Location getLocation() {
		return displayLocation;
	}

	@Override
	public void setLocation(Location location) {
		this.displayLocation = location;

	}

	@Override
	public String getName() {
		return displayLocation.toString();
	}

	public void setDisplayText(String text) {
		this.displayText = text;
	}
	
	@Override
	public String toString() {
		return "Display text at: " + displayLocation + "\n" + displayText;
	}

}
