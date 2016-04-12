package yaes.rcta.agents.civilian;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.AbstractHumanAgent;
import yaes.rcta.agents.civilian.HumanCharacteristics.AgeGroup;
import yaes.rcta.agents.civilian.HumanCharacteristics.ChinType;
import yaes.rcta.agents.civilian.HumanCharacteristics.Gender;
import yaes.rcta.agents.civilian.HumanCharacteristics.HairType;
import yaes.rcta.agents.gametheory.Game;
import yaes.rcta.agents.gametheory.MicroConflict;
import yaes.rcta.movement.DStarLitePP;
import yaes.ui.format.Formatter;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

public class Civilian extends AbstractHumanAgent implements constRCTA {

    private static final long serialVersionUID = 6036929789119275380L;
    private String familyName;
    private double speed;
    private PlannedPath path;
    private RctaContext context;
    private Location localDestination;
    private Location nextLocation;
    protected AgeGroup ageGroup;
    protected double height;
    protected Gender gender;
    protected ChinType chinType;
    protected HairType hairType;

    public Civilian(String name, Location location, double heading) {
        super(name, location, heading);
    }

    public Civilian(RctaContext rctaContext, String civilianName,
        Location civilianStart, double speed) {
        super(civilianName, civilianStart, 0.0);
        this.context = rctaContext;
        this.path = new PlannedPath(this.getLocation(), this.getLocalDestination());
        // FIXME:
        this.setLocation(civilianStart);
        this.setLocalDestinationAsRandomPoI();
        this.setSpeed(speed);
    }

    @Override
    public void action() {
        setLocation(getIntendedMove());
    }

    /**
     * Returns the intended move but it does not execute it.
     * 
     * This is called from action (where it will execute it), or it is called
     * from the game, where it will correspond to a "D" play
     * 
     */

    @Override
    public Location getIntendedMove() {
        if (this.getLocation().equals(this.getLocalDestination())) {
            this.setLocalDestinationAsRandomPoI();
            DStarLitePP dStar = new DStarLitePP(this.context.getEnvironmentModel(),
                    this.getLocation(), this.getLocalDestination());
            this.path = dStar.searchPath();
        }
        this.setNextLocation(this.getLocation());
        Location loc =
                this.path.getNextLocation(this.getLocation(), (int) this.speed);
        if (loc != null)
            return loc;
        else
            return this.localDestination;
    }

    /**
     * Set local destination for this civilian randomly from amongst predefined
     * POIs Checks whether the new local destination is already this agent local
     * destination (in case the agent has reached its destination and requires a
     * new one)
     */
    public Location setLocalDestinationAsRandomPoI() {
        // ArrayList<Location> locations = new
        // ArrayList<Location>(courtLocList.values());
        // Location loc =
        // locations.get(context.getRandom().nextInt(courtLocList.size()));
        ArrayList<Location> locations =
                new ArrayList<Location>(locList.values());
        Location loc =
                locations.get(context.getRandom().nextInt(locList.size()));

        // if(context.getRandom().nextInt(1) == 0){
        locations = new ArrayList<Location>(locList.values());
        loc = locations.get(context.getRandom().nextInt(locList.size()));
        while (loc.equals(localDestination))
            loc = locations.get(context.getRandom().nextInt(locations.size()));
        // }

        while (loc.equals(localDestination))
            loc = locations.get(context.getRandom().nextInt(locations.size()));
        this.setLocalDestination(loc);
        return loc;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public PlannedPath getPath() {
        return path;
    }

    @Override
    public void setPath(PlannedPath path) {
        this.path = path;
    }

    @Override
    public Location getLocalDestination() {
        return localDestination;
    }

    @Override
    public void setLocalDestination(Location localDestination) {
        this.localDestination = localDestination;
    }

    public Location getNextLocation() {
        return nextLocation;
    }

    public void setNextLocation(Location nextLocation) {
        this.nextLocation = nextLocation;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }

    public ChinType getChinType() {
        return chinType;
    }

    public void setChinType(ChinType chinType) {
        this.chinType = chinType;
    }

    public HairType getHairType() {
        return hairType;
    }

    public void setHairType(HairType hairType) {
        this.hairType = hairType;
    }

    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("Civilian");
        fmt.indent();
        fmt.is("Name", getName());
        fmt.is("Location", getLocation());
        fmt.is("Current Destination", this.localDestination);
        fmt.is("Speed", speed);
        fmt.is("Strategy", this.getGameStrategy());

        /********/
        // calculate how many games the agent played, how many moves each, what
        // percentage moves
        // was C.
        int countMicroConflicts = 0;
        int countActiveMicroConflicts = 0;
        int countGames = 0;
        int countPlayedC = 0;
        int countPlayedD = 0;
        for (MicroConflict mc : context.getArchivedMicroConflicts()) {
            if (!mc.getParticipants().contains(this)) {
                continue;
            }
            countMicroConflicts++;
            if (mc.getGames().size() == 0) {
                continue;
            }
            countActiveMicroConflicts++;
            for (SimpleEntry<Double, Game> entry : mc.getGames()) {
                countGames++;
                Game game = entry.getValue();
                String move = game.getDoneMove(this.getName());
                if (move.equals("C") || move.equals("N")) {
                    countPlayedC++;
                }
                if (move.equals("D") || move.equals("E") || move.equals("W")
                        || move.equals("NE") || move.equals("NW")) {
                    countPlayedD++;
                }
            }
            fmt.add(mc);
        }
        // add these to fmt
        fmt.is("Micro-conflicts for " + this.getName() + ":",
                countMicroConflicts);
        fmt.is("Micro-conflicts with games played", countActiveMicroConflicts);
        fmt.is("Games played by the Civilian:", countGames);
        fmt.is(this.getName() + " played C:", countPlayedC);
        fmt.is(this.getName() + " played D:", countPlayedD);
        /**********/
        return fmt.toString();
    }

}
