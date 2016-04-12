package yaes.rcta.agents.civilian;

import yaes.rcta.RctaContext;
import yaes.world.physical.location.Location;

public class Child extends Civilian implements HumanCharacteristics {
    private static final long serialVersionUID = 8922045631360985955L;

    public Child(String name, Location location, double heading) {
        super(name, location, heading);
        // TODO Auto-generated constructor stub
    }

    public Child(RctaContext rctaContext, String civilianName,
        Location civilianStart, double speed) {
        super(rctaContext, civilianName, civilianStart, speed);
        this.ageGroup = AgeGroup.CHILD;        
    }

}
