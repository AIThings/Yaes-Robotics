package yaes.rcta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.rcta.agents.Human;
import yaes.rcta.agents.gametheory.MicroConflict;
import yaes.rcta.agents.robot.Robot;
import yaes.ui.text.TextUi;

/**
 * A class for managing the microconflicts in the context
 * 
 * @author  
 */
public class MicroConflictManager {

    public static final double MINIMUM_SCORE = 0.1;

    /**
     * Manages the microconflicts
     * 
     * @param context
     */
    public static void manageMicroConflicts(RctaContext context) {
        // create a list of all the physical agents
        List<AbstractPhysicalAgent> agents = new ArrayList<>();
        agents.addAll(context.getCivilians());
        agents.addAll(context.getSoldiers());
        agents.addAll(context.getRobots());
        // a new set of microconflicts
        List<MicroConflict> newMicroConflicts = new ArrayList<>();
        // create all the possible microconflicts of two agents
        for (int i = 0; i < agents.size() - 1; i++) {
            for (int j = i + 1; j < agents.size(); j++) {
                AbstractPhysicalAgent agent1 = agents.get(i);
                AbstractPhysicalAgent agent2 = agents.get(j);
                
                if(agent1.getName() == agent2.getName()){
                    TextUi.errorPrint("You shouldn't be here");
                }
                
                //No microconflict between Robot and Humans?
                if ((agent1 instanceof Robot && agent2 instanceof Human) ||
                	(agent2 instanceof Robot && agent1 instanceof Human)) {
                	continue;
                }                		
                MicroConflict mc = new MicroConflict(Long.toString((long)context.getWorld().getTime()), agent1, agent2);
                // look up the old MC if any
                mc = findMicroConflict(context, mc);
                double score = score(mc);
                if (score > MINIMUM_SCORE) {
                    newMicroConflicts.add(mc);
                    mc.setScore(score);
                }
            }
        }
        Collections.sort(newMicroConflicts, new Comparator<MicroConflict>() {

            @Override
            public int compare(MicroConflict o1, MicroConflict o2) {
                return Double.compare(o1.getScore(), o2.getScore());
            }
        });
        Collections.reverse(newMicroConflicts);
        // sets the new set of microconflicts
        context.setMicroConflicts(newMicroConflicts);
        //
        // manage the activity level of microconflicts
        //
        List<MicroConflict> activeMicroConflicts = new ArrayList<>();
        // now assign the microconflicts to the agents
        for (AbstractPhysicalAgent apa : agents) {
            apa.setMicroConflict(null);
        }
        for (MicroConflict mc : newMicroConflicts) {
            boolean active = true;
            // only active if all participants are available
            for (AbstractPhysicalAgent apa : mc.getParticipants()) {
                if (apa.getMicroConflict() != null) {
                    active = false;
                    break;
                }
            }
            mc.setActive(active);
            context.addMicroConflictToArchive(mc);
            // if active, assign it to the participants
            if (active) {
                activeMicroConflicts.add(mc);
                for (AbstractPhysicalAgent apa : mc.getParticipants()) {
                    apa.setMicroConflict(mc);
                }
            }
        }
    }

    /**
     * If a microconflict with the current agents exist, returns it. Otherwise
     * it returns the passed micro-conflict.
     * 
     * @return
     */
    public static MicroConflict findMicroConflict(RctaContext context,
            MicroConflict newMC) {
        for (MicroConflict mc : context.getMicroConflicts()) {
            if (mc.sameAgents(newMC)) {
                return mc;
            }
        }
        return newMC;
    }

    /**
     * Returns a score of microconflicts.
     * 
     * FIXME: this should be more sophisticated, current returns the inverse of
     * the distance
     * 
     * @param mc
     * @return
     */
    public static double score(MicroConflict mc) {
        double stake = 0.0;
        for (int i = 0; i < mc.getParticipants().size() - 1; i++) {
            for (int j = i + 1; j < mc.getParticipants().size(); j++) {
                AbstractPhysicalAgent agent1 = mc.getParticipants().get(i);
                AbstractPhysicalAgent agent2 = mc.getParticipants().get(j);
                double distance =
                        agent1.getLocation().distanceTo(agent2.getLocation());
                if (distance == 0.0) {
                    stake = Double.MAX_VALUE;
                    return stake;
                } else {
                    double tmp = 1.0 / distance;
                    if (tmp > stake) {
                        stake = tmp;
                    }
                }
            }
        }
        double retval = stake;
        return retval;
    }

}
