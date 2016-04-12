package yaes.rcta.agentBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import yaes.framework.simulation.SimulationInput;
import yaes.rcta.RctaContext;
import yaes.rcta.constRCTA;
import yaes.rcta.agents.Human;
import yaes.rcta.environment.RctaEnvironmentHelper;
import yaes.world.physical.location.Location;

public class CrowdBuilder implements constRCTA, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 4785276697437407218L;

    public static void createTourOfficeSpace(SimulationInput sip,
        RctaContext context, OfficeDesignation designation) {
        // Choose the Entrance
        Object[] entryKeys = entranceLocList.keySet().toArray();
        String entryKey = (String) entryKeys[context.getRandom()
                .nextInt(entryKeys.length)];
        Location entryLocation = entranceLocList.get(entryKey);
        context.getPlannedRoute().add(entryLocation);
        context.getWaitingTime().put(entryLocation.toString(), 0);

        switch (designation) {
        case Perm:
            // select point of interest to explore
            // if condition is for making all the variable local
            if (true) {
                Object[] officeDesks = context.getPermanentJobSittingSpaces()
                        .keySet().toArray();
                String officeDesk = (String) officeDesks[context.getRandom()
                        .nextInt(officeDesks.length)];
                Location officeLocation =
                        context.getPermanentJobSittingSpaces().get(officeDesk);
                context.getPermanentJobSittingSpaces().remove(officeDesk);
                context.getPlannedRoute().add(officeLocation);
                context.getWaitingTime().put(officeLocation.toString(), 500);
            }

            break;
        case Temp:
            int stops = context.getRandom().nextInt(10);
            for (int i = 0; i < stops; i++) {
                if (true) {
                    Object[] officeDesks = locList1.keySet().toArray();
                    String officeDesk = (String) officeDesks[context.getRandom()
                            .nextInt(officeDesks.length - 1)];
                    Location officeLocation = locList1.get(officeDesk);
                    context.getPlannedRoute().add(officeLocation);
                    context.getWaitingTime().put(officeLocation.toString(),
                            context.getRandom().nextInt(25));
                }

            }

            break;
        default:
            throw new Error(
                    "No implementation for designation: " + designation);
        }

        // Take Exit
        Object[] exitKeys = exitLocList.keySet().toArray();
        String exitKey =
                (String) exitKeys[context.getRandom().nextInt(exitKeys.length)];
        Location exitLocation = exitLocList.get(exitKey);
        context.getPlannedRoute().add(exitLocation);
        context.getWaitingTime().put(exitLocation.toString(), 0);
    }

    public static void createTourRedCarpet(SimulationInput sip,
        RctaContext context) {
        // Choose the Entrance
        Object[] entryKeys = entranceLocList.keySet().toArray();
        String entryKey = (String) entryKeys[context.getRandom()
                .nextInt(entryKeys.length)];
        Location entryLocation = entranceLocList.get(entryKey);
        context.getPlannedRoute().add(entryLocation);
        context.getWaitingTime().put(entryLocation.toString(), 0);
        // Take Exit
        Object[] exitKeys = exitLocList.keySet().toArray();
        String exitKey =
                (String) exitKeys[context.getRandom().nextInt(exitKeys.length)];
        Location exitLocation = exitLocList.get(exitKey);
        context.getPlannedRoute().add(exitLocation);
        context.getWaitingTime().put(exitLocation.toString(), 0);

    }

    public static void createTourShoppingMall(SimulationInput sip,
        RctaContext context) {

        // Choose the Entrance
        Object[] entryKeys = entranceLocList.keySet().toArray();
        String entryKey = (String) entryKeys[context.getRandom()
                .nextInt(entryKeys.length)];
        Location entryLocation = entranceLocList.get(entryKey);
        context.getPlannedRoute().add(entryLocation);
        context.getWaitingTime().put(entryLocation.toString(), 0);

        // Define route by randomly selecting waiting time and shops
        switch (entryKey) {
        case "ENTRANCE1":
            for (Location loc : locList1.values()) {
                context.getPlannedRoute().add(loc);
                context.getWaitingTime().put(loc.toString(),
                        context.getRandom().nextInt(5));
            }
            break;
        case "ENTRANCE2":
            for (Location loc : locList2.values()) {
                context.getPlannedRoute().add(loc);
                context.getWaitingTime().put(loc.toString(),
                        context.getRandom().nextInt(5));
            }
            break;
        case "ENTRANCE3":
            if (context.getRandom().nextBoolean()) {
                ListIterator<String> iter =
                        new ArrayList<String>(locList1.keySet())
                                .listIterator(locList1.size());

                while (iter.hasPrevious()) {
                    String key = iter.previous();
                    context.getPlannedRoute().add(locList1.get(key));
                    context.getWaitingTime().put(locList1.get(key).toString(),
                            context.getRandom().nextInt(5));
                }
                context.getPlannedRoute().add(exitLocList.get("EXIT1"));
                context.getWaitingTime()
                        .put(exitLocList.get("EXIT1").toString(), 0);
            } else {
                ListIterator<String> iter =
                        new ArrayList<String>(locList2.keySet())
                                .listIterator(locList2.size());

                while (iter.hasPrevious()) {
                    String key = iter.previous();
                    context.getPlannedRoute().add(locList2.get(key));
                    context.getWaitingTime().put(locList2.get(key).toString(),
                            context.getRandom().nextInt(5));
                }
                context.getPlannedRoute().add(exitLocList.get("EXIT2"));
                context.getWaitingTime()
                        .put(exitLocList.get("EXIT2").toString(), 0);
            }
            return;
        default:
            break;

        }
        // Take Exit
        Object[] exitKeys = exitLocList.keySet().toArray();
        String exitKey =
                (String) exitKeys[context.getRandom().nextInt(exitKeys.length)];
        Location exitLocation = exitLocList.get(exitKey);
        context.getPlannedRoute().add(exitLocation);
        context.getWaitingTime().put(exitLocation.toString(), 0);

    }
    
    /**
     * add and remove crowd agents. Increase Crowd to match Civilian count.
     * Remove agents reaching exit destinations
     */
    public static void updateCrowd(SimulationInput sip, RctaContext context) {
        int crowdCount = sip.getParameterInt(CIVILIANS_COUNT);
        if (context.getHumans().size() < crowdCount) {
            Human civilian =
                    HumanBuilder.createHuman(sip, context, context.getHumans().size());
            context.getHumans().add(civilian);
            context.addToVisualizer(civilian);
        }
        ArrayList<Human> humansToRemove = new ArrayList<Human>();
        for (Human human : context.getHumans()) {
            if (human.getName().contains("CIV")) {
                if (RctaEnvironmentHelper.isLocationOccupied(
                        context.getEnvironmentModel(), human.getLocation(),
                        MAP_EXITS)) {
                    humansToRemove.add(human);
                }
            }
        }
        for (Human sol : humansToRemove) {
            context.getHumans().remove(sol);
            context.removeFromVisualizer(sol);
        }
    }
}
