package yaes.rcta.util;

import yaes.world.physical.location.Location;
import yaes.world.physical.path.Path;

public class PlannedTour extends Path {
	private static final long serialVersionUID = 6428887168418974315L;
	private int indexPOI = -1;

	public int getIndexPOI() {
		return indexPOI;
	}
	@Override
	public Location getNextLocation(Location loc, int index) {
		int locIndex = -1;
		locIndex = indexOfLocation(loc);
		if ((locIndex >= 0) && (locIndex + index < getPathSize()) && (locIndex + index > 0)) {
			indexPOI = locIndex + index;
			return getLocationAt(locIndex + index);
		}
		return null;
	}
	@Override
	public Location getLocationAt(int index) {
		Location newLoc = locations.get(index);
		if (newLoc != null){
			indexPOI = index;
		}
        return locations.get(index);
    }

}
