package yaes.rcta.agents.civilian;

import yaes.rcta.RctaContext;
import yaes.world.physical.location.Location;

public class Senior extends Civilian implements HumanCharacteristics{
    private static final long serialVersionUID = 8460162741472210241L;

    
    public Senior(RctaContext rctaContext, String civilianName,
        Location civilianStart, double speed) {
        super(rctaContext, civilianName, civilianStart, speed);
        this.ageGroup = AgeGroup.SENIOR;
    }

}
