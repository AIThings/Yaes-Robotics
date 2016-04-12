package yaes.rcta.environment;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.rcta.RctaContext;
import yaes.rcta.RctaResourceHelper;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.Human;
import yaes.rcta.agents.civilian.Civilian;
import yaes.rcta.agents.robot.RoboScout;
import yaes.rcta.agents.robot.Robot;
import yaes.rcta.movement.DStarLitePP;
import yaes.rcta.movement.MapLocationAccessibility;
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
public class RctaEnvironmentHelper implements constRCTA, Serializable {

    private static final long serialVersionUID = 2927271217505318607L;

    /**
     * Initializes the Environment in the scenario
     * 
     * @param sip
     * @param sop
     */
    public static void initializeEnvironment(SimulationInput sip,
        SimulationOutput sop, RctaContext context) {
        String obstacleMapFile = sip.getParameterString(MAP_OBSTACLES);
        String backgroundMapFile = sip.getParameterString(MAP_BACKGROUND);
        EnvironmentModel envMdl =
                createEM(obstacleMapFile, backgroundMapFile, context);
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
    public static EnvironmentModel createEM(String obstacleMapFile,
        String backgroundMapFile, RctaContext context) {

        File fileObstacles =
        		RctaResourceHelper.getFile(obstacleMapFile);
        LinearColorToValue lctv = new LinearColorToValue(0, 100);

        EnvironmentModel retval = new EnvironmentModel("TheModel", 0, 0,
                context.getSimulationInput().getParameterInt(MAP_WIDTH),
                context.getSimulationInput().getParameterInt(MAP_HEIGHT), 1, 1);
        retval.createProperty(MAP_OBSTACLES);
        retval.loadDataFromImage(MAP_OBSTACLES, fileObstacles, lctv);

        if (backgroundMapFile != null) {
            File fileBackground =
            		RctaResourceHelper.getFile(backgroundMapFile);

            try {
                retval.loadBackgroundImage(fileBackground);
            } catch (IOException ioex) {
                TextUi.errorPrint("could not load background image file:"
                        + fileBackground);
            }
        }

        switch (context.getSimulationInput()
                .getParameterEnum(ScenarioType.class)) {
        case SINGLE_CONFLICT: {
            // no obstacles in single value
            lctv = new LinearColorToValue(0, 0);
            break;
        }
        case CLOSE_PROTECTION_CONTROL_MODEL:
        case CLOSE_PROTECTION_MULTIPLE_ROBOT:
        case CLOSE_PROTECTION_SINGLE_ROBOT:
        case SAME_CROWD_ADAPTIVE:
        case CROWD_SEEK_VIP:
        case CROWD_SIMULATION:
        case DEFAULT:
        case IMITATION:
        case MULTIPLE_ROBOT_FOLLOW:
        case OBSTACLE_AVOIDANCE:
        case OBSTACLE_AVOIDANCE_IN_CROWD:
        case REALISTIC:
        case REALISTIC_WITH_ROBOT_SCENARIO:
        case ROBOT_FOLLOW_SOLDIER:
        case ROBOT_FOLLOW_SOLDIER_MULTIPLE:
        case ROBOT_SCOUT:
        case SINGLE_ROBOT_FOLLOW:
        default: {
            lctv = new LinearColorToValue(0, 100);
            break;
        }

        }
        retval.loadDataFromImage(MAP_OBSTACLES, fileObstacles, lctv);

        if (backgroundMapFile != null) {
            File fileBackground =
            		RctaResourceHelper.getFile(backgroundMapFile);
            try {
                retval.loadBackgroundImage(fileBackground);
            } catch (IOException ioex) {
                TextUi.errorPrint("could not load background image file:"
                        + fileBackground);
            }
        }
        // create the property for the heatmap
        retval.createProperty(RctaContext.PROP_DENSITY);

        return retval;
    }

    public static void initEnvShoppingMall(SimulationInput sip,
        SimulationOutput sop, RctaContext context) {
        String obstacleMapFile = sip.getParameterString(MAP_OBSTACLES);
        String backgroundMapFile = sip.getParameterString(MAP_BACKGROUND);
        String mapEnteranceFile = sip.getParameterString(MAP_ENTRANCES);
        String mapExitFile = sip.getParameterString(MAP_EXITS);

        EnvironmentModel envMdl =
                createEM(obstacleMapFile, backgroundMapFile, context);
        context.setEnvironmentModel(envMdl);

        RctaResourceHelper helper = new RctaResourceHelper();
        File fileEntrances = helper.getFile(mapEnteranceFile);
        File fileExit = helper.getFile(mapExitFile);

        LinearColorToValue lctv = new LinearColorToValue(0, 100);

        context.getEnvironmentModel().createProperty(MAP_ENTRANCES);
        context.getEnvironmentModel().createProperty(MAP_EXITS);

        context.getEnvironmentModel().loadDataFromImage(MAP_ENTRANCES,
                fileEntrances, lctv);
        context.getEnvironmentModel().loadDataFromImage(MAP_EXITS, fileExit,
                lctv);

    }

    public static void initEnvOfficeSpace(SimulationInput sip,
        SimulationOutput sop, RctaContext context) {
        String obstacleMapFile = sip.getParameterString(MAP_OBSTACLES);
        String backgroundMapFile = sip.getParameterString(MAP_BACKGROUND);
        String mapEnteranceFile = sip.getParameterString(MAP_ENTRANCES);
        String mapExitFile = sip.getParameterString(MAP_EXITS);
        String mapDoorFile = sip.getParameterString(MAP_DOORS);

        EnvironmentModel envMdl =
                createEM(obstacleMapFile, backgroundMapFile, context);
        context.setEnvironmentModel(envMdl);

        RctaResourceHelper helper = new RctaResourceHelper();
        File fileEntrances = helper.getFile(mapEnteranceFile);
        File fileExit = helper.getFile(mapExitFile);
        File fileDoor = helper.getFile(mapDoorFile);

        LinearColorToValue lctv = new LinearColorToValue(0, 100);

        context.getEnvironmentModel().createProperty(MAP_ENTRANCES);
        context.getEnvironmentModel().createProperty(MAP_EXITS);
        context.getEnvironmentModel().createProperty(MAP_DOORS);

        context.getEnvironmentModel().loadDataFromImage(MAP_ENTRANCES,
                fileEntrances, lctv);
        context.getEnvironmentModel().loadDataFromImage(MAP_EXITS, fileExit,
                lctv);
        context.getEnvironmentModel().loadDataFromImage(MAP_DOORS, fileDoor,
                lctv);

        for (String loc : locList1.keySet()) {
            context.getPermanentJobSittingSpaces().put(loc, locList1.get(loc));
        }

    }

    public static void initEnvRedCarpet(SimulationInput sip,
        SimulationOutput sop, RctaContext context) {
        String obstacleMapFile = sip.getParameterString(MAP_OBSTACLES);
        String backgroundMapFile = sip.getParameterString(MAP_BACKGROUND);
        String mapEnteranceFile = sip.getParameterString(MAP_ENTRANCES);
        String mapExitFile = sip.getParameterString(MAP_EXITS);
        // String mapDoorFile = sip.getParameterString(MAP_DOORS);

        EnvironmentModel envMdl =
                createEM(obstacleMapFile, backgroundMapFile, context);
        context.setEnvironmentModel(envMdl);

        RctaResourceHelper helper = new RctaResourceHelper();
        File fileEntrances = helper.getFile(mapEnteranceFile);
        File fileExit = helper.getFile(mapExitFile);

        LinearColorToValue lctv = new LinearColorToValue(0, 100);

        context.getEnvironmentModel().createProperty(MAP_ENTRANCES);
        context.getEnvironmentModel().createProperty(MAP_EXITS);
        // this.emGlobalCost.createProperty(MAP_DOORS);
        context.getEnvironmentModel().loadDataFromImage(MAP_ENTRANCES,
                fileEntrances, lctv);
        context.getEnvironmentModel().loadDataFromImage(MAP_EXITS, fileExit,
                lctv);
        // this.emGlobalCost.loadDataFromImage(MAP_DOORS, fileDoor, lctv);

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
    public static boolean isLocationOccupied(RctaContext context, Location loc,
        int includeZones) {
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
        for (Civilian c : context.getCivilians()) {
            if (c.getLocation().equals(loc))
                return true;
        }

        double val = (double) context.getEnvironmentModel()
                .getPropertyAt(MAP_OBSTACLES, loc.getX(), loc.getY());
        // TextUi.println(val);
        if (val > 0)
            return true;

        if (loc.getX() > context.getEnvironmentModel().getXHigh()
                || loc.getX() < 0
                || loc.getY() > context.getEnvironmentModel().getYHigh()
                || loc.getY() < 0)
            return true;
        return false;
    }

    /**
     * The new version of generating the randomPOI's from POI Interface
     * Generates random points of interest
     * 
     * @author Saad Khan
     */
    public static Map<String, Location> generatePointsOfInterest(
        RctaContext context) {
        Location loc = new Location(
                context.getRandom()
                        .nextInt(context.getSimulationInput()
                                .getParameterInt(MAP_WIDTH)),
                context.getRandom().nextInt(context.getSimulationInput()
                        .getParameterInt(MAP_HEIGHT)));
        Map<String, Location> pointOfInterest = new HashMap<>();

        Set<Entry<String, Location>> entrySet = locList.entrySet();
        for (Entry<String, Location> entry : entrySet) {
            String locName = (String) entry.getKey();
            Location tempLoc = (Location) entry.getValue();
            while (isLocationOccupied(context, tempLoc, 0)
                    || pointOfInterest.containsValue(loc))
                loc = new Location(
                        context.getRandom()
                                .nextInt(context.getSimulationInput()
                                        .getParameterInt(MAP_WIDTH)),
                        context.getRandom().nextInt(context.getSimulationInput()
                                .getParameterInt(MAP_HEIGHT)));
            pointOfInterest.put(locName, loc);
        }

        return pointOfInterest;

    }

    public static boolean isLocationOccupied(EnvironmentModel environmentModel,
        Location loc, String mapProp) {

        if (loc.getX() > environmentModel.getXHigh() || loc.getX() < 0
                || loc.getY() > environmentModel.getYHigh() || loc.getY() < 0)
            return true;
        double val = (double) environmentModel.getPropertyAt(mapProp,
                loc.getX(), loc.getY());

        // TextUi.println(val);
        if (val > 0)
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
    public static boolean isVisible(RctaContext context, Location start,
        Location end, int includeZones) {
        PlannedPath path = new PlannedPath();
        double r = MapHelper.distance(start, end);
        double angle = Math.atan2(end.getY() - start.getY(),
                end.getX() - start.getX());
        double delta = context.getSimulationInput()
                .getParameterDouble(HUMAN_WIDTH_SHOULDER) / 2;
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
     * If soldier at 'start' location can see 'end' location, considering only
     * static obstacles.
     * 
     * @param start
     * @param end
     * @return
     */
    public static boolean isVisible(RctaContext context, Location start,
        Location end) {
        PlannedPath path = new PlannedPath();
        double r = MapHelper.distance(start, end);
        double angle = Math.atan2(end.getY() - start.getY(),
                end.getX() - start.getX());
        for (double i = 0; i < r; i = i + 0.5) {
            double x = start.getX() + (i * Math.cos(angle));
            double y = start.getY() + (i * Math.sin(angle));
            path.addLocation(new Location(x, y));
            if (isLocationOccupied(context.getEnvironmentModel(),
                    new Location(x, y), MAP_OBSTACLES)) {
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
    public static void calculateThreatLevel(RctaContext context) {
        double threatLevel = 0.0;
        for (Human soldier : context.getHumans()) {

            // if (soldier.isFocus()) {
            // displayText
            // .setDisplayText(soldier.getLocation().toString());
            // }
            if (soldier.getName().contains("CIV")) {
                threatLevel = displayThreatProbability(context, threatLevel,
                        soldier.getLocation());
            }

            // soldier.action();

        }
        context.getThreatValues().add(threatLevel);
        context.getDisplayText1().setDisplayText("TL: " + threatLevel);

    }

    /**
     * Calculate Reduced ThreatLevel RT
     */
    public static void calculateNutralizedThreatLevel(RctaContext context) {
        // Get VIP Location
        Location vipLocation = new Location(0, 0);
        for (Human soldier : context.getHumans()) {
            if (soldier.getName().contains("VIP")) {
                vipLocation = new Location(soldier.getLocation().getX(),
                        soldier.getLocation().getY());
            }
        }

        double threatLevel = 0.0;
        for (Human civilian : context.getHumans()) {
            if (civilian.getName().contains("CIV")) {
                // Check if VIP is in line of sight of Civilian
                if (isVisible(context, civilian.getLocation(), vipLocation,
                        2)) {
                    double chance = MathUtility.chanceOfAttack(
                            civilian.getLocation(), vipLocation);
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
    public static double displayThreatProbability(RctaContext context,
        double threatLevel, Location civLocation) {

        // Get VIP Location
        Location vipLocation = new Location(0, 0);
        for (Human soldier : context.getHumans()) {
            if (soldier.getName().contains("VIP")) {
                vipLocation = new Location(soldier.getLocation().getX(),
                        soldier.getLocation().getY());
            }
        }
        // Check if VIP is in line of sight of Civilian
        if (isVisible(context, civLocation, vipLocation, 0)) {
            double chance =
                    MathUtility.chanceOfAttack(civLocation, vipLocation);
            return MathUtility.combineProb(chance, threatLevel);
        } else {
            return threatLevel;
        }

    }

    /**
     * Return angle between currentLocation and targetLocation in radian or
     * degree
     * 
     * @param currentLocation
     * @param targetLocation
     * @param inDegree
     * @return double
     */
    public static double getAngle(Location currentLocation,
        Location targetLocation, boolean inDegree) {
        double angle =
                Math.atan2(targetLocation.getY() - currentLocation.getY(),
                        (targetLocation.getX() - currentLocation.getX()));

        if (inDegree) {
            angle = (double) Math.toDegrees(angle);
            if (angle < 0) {
                angle += 360;
            }
        }

        return angle;
    }

    public static void updateClusters(RctaContext context,
        HashMap<String, Location> robosearchLoclist,
        HashMap<String, Location> anchorloclist)
            throws MatlabConnectionException, MatlabInvocationException {
        double[][] virtualDimension =
                new double[robosearchLoclist.size()][anchorloclist.size()];
        double[][] mapPoints = new double[robosearchLoclist.size()][2];
        int counter_poi = 0, counter_anchor = 0;
        for (Location loc : robosearchLoclist.values()) {
            counter_anchor = 0;
            for (Location anchor : anchorloclist.values()) {
                DStarLitePP dStar = new DStarLitePP(
                        context.getEnvironmentModel(), loc, anchor);
                virtualDimension[counter_poi][counter_anchor] =
                        dStar.searchPath().getPathLenght();
                mapPoints[counter_poi][0] = loc.getX();
                mapPoints[counter_poi][1] = loc.getY();
                counter_anchor++;
            }
            counter_poi++;
        }

        MatlabProxyFactory factory = new MatlabProxyFactory();
        MatlabProxy proxy = factory.getProxy();
        proxy.eval("clc");
        proxy.eval("clear");
        proxy.eval("close all");

        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
        processor.setNumericArray("virtualDimension",
                new MatlabNumericArray(virtualDimension, null));
        processor.setNumericArray("mapPoints",
                new MatlabNumericArray(mapPoints, null));
        // script_generate(array, map_points, neighbors, num_clusters, sigma)
        proxy.eval("script_generate(virtualDimension, mapPoints, 1, 3, 0)");

    }

    public static void updateClusters(RctaContext context)
        throws MatlabConnectionException, MatlabInvocationException {
        int areaX = 500, areaY = 500, lineDepth_scan = 10;
        double[][] globalCost =
                new double[(areaX / lineDepth_scan) * (areaX / lineDepth_scan)
                        + context.getRoboScoutingLocs().size()][2];
        double[][] mapPoints =
                new double[(areaX / lineDepth_scan) * (areaX / lineDepth_scan)
                        + context.getRoboScoutingLocs().size()][2];
        int counter = 0;
        for (double x = 0; x < areaX; x += lineDepth_scan) {
            for (double y = 0; y < areaY; y += lineDepth_scan) {
                globalCost[counter][0] = (context.getRobots().get(0)
                        .getLocation().distanceTo(new Location(x, y))
                        + context.getRobots().get(1).getLocation()
                                .distanceTo(new Location(x, y))
                        + context.getRobots().get(2).getLocation()
                                .distanceTo(new Location(x, y)))
                        / 3;

                globalCost[counter][1] = (double) context.getEnvironmentModel()
                        .getPropertyAt(RctaContext.PROP_DENSITY, x, y);

                mapPoints[counter][0] = x;
                mapPoints[counter][1] = y;

                counter++;
            }
        }
        // Fill the array with location values from the scouting list
        int x = 0;
        for (Location loc : context.getRoboScoutingLocs()) {
            globalCost[counter + x][0] =
                    (context.getRobots().get(0).getLocation().distanceTo(loc)
                            + context.getRobots().get(1).getLocation()
                                    .distanceTo(loc)
                    + context.getRobots().get(2).getLocation().distanceTo(loc))
                    / 3;

            globalCost[counter + x][1] =
                    (double) context.getEnvironmentModel().getPropertyAt(
                            RctaContext.PROP_DENSITY, loc.getX(), loc.getY());

            mapPoints[counter + x][0] = loc.getX();
            mapPoints[counter + x][1] = loc.getY();

            x++;
        }

        // MatlabProxyFactoryOptions options = new
        // MatlabProxyFactoryOptions.Builder()
        // .setUsePreviouslyControlledSession(true)
        // .setHidden(true)
        // .setMatlabLocation(null).build();

        // MatlabProxyFactory factory = new MatlabProxyFactory(options);
        MatlabProxyFactory factory = new MatlabProxyFactory();
        MatlabProxy proxy = factory.getProxy();
        proxy.eval("clc");
        proxy.eval("clear");
        proxy.eval("close all");

        MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
        processor.setNumericArray("array",
                new MatlabNumericArray(globalCost, null));
        processor.setNumericArray("mapPoints",
                new MatlabNumericArray(mapPoints, null));

        proxy.eval("script_generate(array, mapPoints, 2, 4, 0)");

        MatlabNumericArray cluster_labels = processor.getNumericArray("ans");
        int[] clusterLabelList = new int[cluster_labels.getLength()];
        for (int i = 0; i < cluster_labels.getLength(); i++) {
            clusterLabelList[i] = (int) cluster_labels.getRealValue(i);
        }
        HashMap<Robot, ArrayList<Location>> scoutLocList =
                new HashMap<Robot, ArrayList<Location>>();

        // Create the seperate locations lists for the scout robots
        for (Robot robo : context.getRobots())
            scoutLocList.put(robo, new ArrayList<Location>());

        int[] clusterLabelsForScouts =
                new int[context.getRoboScoutingLocs().size()];

        for (int index = 0; index < context.getRoboScoutingLocs()
                .size(); index++) {
            clusterLabelsForScouts[index] = clusterLabelList[counter + index];
        }

        int index = 0;
        for (int cluster : clusterLabelsForScouts) {
            scoutLocList.get(context.getRobots().get(cluster - 1))
                    .add(context.getRoboScoutingLocs().get(index));
            index++;
        }
        index = 0;
        for (Robot robo : context.getRobots()) {
            ArrayList<Location> locs = scoutLocList.get(robo);
            ((RoboScout) robo).setRobotScout_locations(locs);
            ((RoboScout) robo).setTspPath(robo.tspPathPlanner(locs));
        }
        proxy.disconnect();

    }

    public static Location setRandomPOIForRobot(RctaContext context,
        Robot robot) {
        ArrayList<Location> locations =
                new ArrayList<Location>(locList.values());
        Location loc =
                locations.get(context.getRandom().nextInt(locList.size()));
        locations = new ArrayList<Location>(locList.values());
        loc = locations.get(context.getRandom().nextInt(locList.size()));
        while (loc.equals(robot.getLocalDestination()))
            loc = locations.get(context.getRandom().nextInt(locations.size()));
        robot.setLocalDestination(loc);
        return loc;
    }

    public static Location getInitialLocation(RctaContext context) {
        SimulationInput sip = context.getSimulationInput();
        Location initialLoc = new Location(
                context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
                context.getRandom().nextInt(sip.getParameterInt(MAP_HEIGHT)));
        while (RctaEnvironmentHelper.isLocationOccupied(context, initialLoc, 0))
            initialLoc = new Location(
                    context.getRandom().nextInt(sip.getParameterInt(MAP_WIDTH)),
                    context.getRandom()
                            .nextInt(sip.getParameterInt(MAP_HEIGHT)));
        return initialLoc;

    }

    /**
     * Check if newlocation is not accessible or obstacle
     * 
     * @param environmentIMap
     * @param mapLocationAccessibility
     * @param newLocation
     * @return
     */
    public static boolean isAccessible(EnvironmentModel environmentIMap,
        MapLocationAccessibility mapLocationAccessibility,
        Location newLocation) {
        if (mapLocationAccessibility.isAccessible(environmentIMap,
                newLocation)) {
            return true;
        } else {

            return false;
        }

    }
}
