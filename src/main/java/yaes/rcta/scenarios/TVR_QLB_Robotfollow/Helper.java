package yaes.rcta.scenarios.TVR_QLB_Robotfollow;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.rcta.RctaResourceHelper;
import yaes.rcta.constRCTA;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents.Human;
import yaes.rcta.scenarios.TVR_QLB_Robotfollow.agents.Robot;
import yaes.rcta.util.MathUtility;
import yaes.ui.text.TextUi;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.environment.LinearColorToValue;
import yaes.world.physical.location.Location;
import yaes.world.physical.map.MapHelper;
import yaes.world.physical.path.PlannedPath;

/**
 * This class creates different environments for different simulations
 * 
 * @author SaadKhan
 *
 */
public class Helper implements constRCTA, Serializable {

	private static final long serialVersionUID = 2927271217505318607L;

	/**
	 * Initializes the Environment in the scenario
	 * 
	 * @param sip
	 * @param sop
	 */
	public static void initializeEnvironment(SimulationInput sip, SimulationOutput sop, Context context) {
		String obstacleMapFile = sip.getParameterString(MAP_OBSTACLES);
		String backgroundMapFile = sip.getParameterString(MAP_BACKGROUND);
		EnvironmentModel envMdl = createEM(obstacleMapFile, backgroundMapFile, context);
		context.setEnvironmentModel(envMdl);
	}

	/**
	 * Utility function to create a specific environment model corresponding to
	 * the RCTA stuff. It is a static function to allow being called from
	 * outside (for instance from unit tests)
	 * 
	 * @param obstacleMapFile
	 * @return
	 */
	public static EnvironmentModel createEM(String obstacleMapFile, String backgroundMapFile, Context context) {

		File fileObstacles = RctaResourceHelper.getFile(obstacleMapFile);
		LinearColorToValue lctv = new LinearColorToValue(0, 100);

		EnvironmentModel retval = new EnvironmentModel("TheModel", 0, 0,
				context.getSimulationInput().getParameterInt(MAP_WIDTH),
				context.getSimulationInput().getParameterInt(MAP_HEIGHT), 1, 1);
		retval.createProperty(MAP_OBSTACLES);
		retval.loadDataFromImage(MAP_OBSTACLES, fileObstacles, lctv);

		if (backgroundMapFile != null) {
			File fileBackground = RctaResourceHelper.getFile(backgroundMapFile);

			try {
				retval.loadBackgroundImage(fileBackground);
			} catch (IOException ioex) {
				TextUi.errorPrint("could not load background image file:" + fileBackground);
			}
		}

	
			lctv = new LinearColorToValue(0, 100);
	
	
		retval.loadDataFromImage(MAP_OBSTACLES, fileObstacles, lctv);

		if (backgroundMapFile != null) {
			File fileBackground = RctaResourceHelper.getFile(backgroundMapFile);
			try {
				retval.loadBackgroundImage(fileBackground);
			} catch (IOException ioex) {
				TextUi.errorPrint("could not load background image file:" + fileBackground);
			}
		}

		return retval;
	}

	/**
	 * Checks if a particular location is occupied or not. Checks for all agents
	 * (robots, civilians, obstacles) returns true if occupied, and false
	 * otherwise.
	 * 
	 * To include zones ( 1-Physical Zone, 2-Personal zone, 3-MovementCone)
	 * 
	 * Note: Not implemented for 3-MovementCone yet.
	 * 
	 * @param loc
	 * @param includeZones
	 * @return
	 */
	public static boolean isLocationOccupied(Context context, Location loc, int includeZones) {
		if (loc == null)
			return false;

		for (Robot r : context.getRobots()) {
			if (includeZones == 2) {
				if (r.getAzPersonalSpace().getValue(loc) != 0) {
					return true;
				}
			} else if (includeZones == 1) {
				if (r.getAzPhysical().getValue(loc) != 0)
					return true;
			} else {
				if (r.getLocation().equals(loc))
					return true;
			}

		}
		for (Human s : context.getHumans()) {
			if (s.getLocation().equals(loc))
				return true;
		}

		double val = (double) context.getEnvironmentModel().getPropertyAt(MAP_OBSTACLES, loc.getX(), loc.getY());
		// TextUi.println(val);
		if (val > 0)
			return true;

		if (loc.getX() > context.getEnvironmentModel().getXHigh() || loc.getX() < 0
				|| loc.getY() > context.getEnvironmentModel().getYHigh() || loc.getY() < 0)
			return true;
		return false;
	}

	/**
	 * If soldier at 'start' location can see soldier at 'end' location
	 * considering given zone
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isVisible(Context context, Location start, Location end, int includeZones) {
		PlannedPath path = new PlannedPath();
		double r = MapHelper.distance(start, end);
		double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
		double delta = context.getSimulationInput().getParameterDouble(HUMAN_WIDTH_SHOULDER) / 2;
		for (double i = 0 + delta; i < r - delta; i = i + 0.5) {
			double x = start.getX() + (i * Math.cos(angle));
			double y = start.getY() + (i * Math.sin(angle));
			path.addLocation(new Location(x, y));
			if (isLocationOccupied(context, new Location(x, y), includeZones)) {
				path = new PlannedPath();
				return false;
			}
		}

		return true;
		// TextUi.println(path.toString());

	}

	/**
	 * Calculate ThreatLevel TL
	 */
	public static void calculateThreatLevel(Context context) {
		double threatLevel = 0.0;
		for (Human soldier : context.getHumans()) {

			// if (soldier.isFocus()) {
			// displayText
			// .setDisplayText(soldier.getLocation().toString());
			// }
			if (soldier.getName().contains("CIV")) {
				threatLevel = displayThreatProbability(context, threatLevel, soldier.getLocation());
			}

			// soldier.action();

		}
		context.getThreatValues().add(threatLevel);
		context.getDisplayText1().setDisplayText("TL: " + threatLevel);
		
	}

	/**
	 * Calculate Reduced ThreatLevel RT
	 */
	public static void calculateNutralizedThreatLevel(Context context) {
		// Get VIP Location
		Location vipLocation = new Location(0, 0);
		for (Human soldier : context.getHumans()) {
			if (soldier.getName().contains("VIP")) {
				vipLocation = new Location(soldier.getLocation().getX(), soldier.getLocation().getY());
			}
		}

		double threatLevel = 0.0;
		for (Human civilian : context.getHumans()) {
			if (civilian.getName().contains("CIV")) {
				// Check if VIP is in line of sight of Civilian
				if (isVisible(context, civilian.getLocation(), vipLocation, 2)) {
					double chance = MathUtility.chanceOfAttack(civilian.getLocation(), vipLocation);
					threatLevel = MathUtility.combineProb(chance, threatLevel);
				}
			}
		}

		context.getNeutralizeThreatValue().add(threatLevel);
		context.getDisplayText2().setDisplayText("RT: " + threatLevel);
	}

	/**
	 * 
	 * @param threatLevel
	 * @param civLocation
	 * @return probability of threat to VIP from civilian
	 */
	public static double displayThreatProbability(Context context, double threatLevel, Location civLocation) {

		// Get VIP Location
		Location vipLocation = new Location(0, 0);
		for (Human soldier : context.getHumans()) {
			if (soldier.getName().contains("VIP")) {
				vipLocation = new Location(soldier.getLocation().getX(), soldier.getLocation().getY());
			}
		}
		// Check if VIP is in line of sight of Civilian
		if (isVisible(context, civLocation, vipLocation, 0)) {
			double chance = MathUtility.chanceOfAttack(civLocation, vipLocation);
			return MathUtility.combineProb(chance, threatLevel);
		} else {
			return threatLevel;
		}

	}

	public static Location setRandomPOIForRobot(Context context, Robot robot) {
		ArrayList<Location> locations = new ArrayList<Location>(locList.values());
		Location loc = locations.get(context.getRandom().nextInt(locList.size()));
		locations = new ArrayList<Location>(locList.values());
		loc = locations.get(context.getRandom().nextInt(locList.size()));
		while (loc.equals(robot.getLocalDestination()))
			loc = locations.get(context.getRandom().nextInt(locations.size()));
		robot.setLocalDestination(loc);
		return loc;
	}

	public static Location getInitialLocation(Context context) {
		SimulationInput sip = context.getSimulationInput();
		Location initialLoc = new Location(context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
				context.getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));
		while (Helper.isLocationOccupied(context, initialLoc, 0))
			initialLoc = new Location(context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
					context.getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));
		return initialLoc;

	}
}
