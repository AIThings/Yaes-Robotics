package yaes.rcta.util;

import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.INamed;
import yaes.world.physical.location.Location;

public class DisplayText implements INamed, IMoving {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6636378942248601796L;
	private Location displayLocation;
	private String displayText;
	public DisplayText(){
		this.displayLocation = new Location(0,0);
		this.displayText = "Nothing";
	}
	public DisplayText(Location displayLocation){
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
		return this.displayText;
	}
	
	public void setDisplayText(String text){
		this.displayText = text;
	}

}
