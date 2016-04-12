package yaes.rcta.agents.civilian;

import yaes.rcta.RctaContext;
import yaes.world.physical.location.Location;

public class Youngster extends Civilian implements HumanCharacteristics {

    private static final long serialVersionUID = -1913498531958429601L;

    public Youngster(String name, Location location, double heading) {
        super(name, location, heading);
        // TODO Auto-generated constructor stub
    }

    public Youngster(RctaContext rctaContext, String civilianName,
        Location civilianStart, double speed) {
        super(rctaContext, civilianName, civilianStart, speed);
        this.ageGroup = AgeGroup.YOUNGSTER;
    }


}
