package yaes.rcta.agents.gametheory;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.rcta.agents.CostVector;
import yaes.ui.format.Formatter;
import yaes.world.physical.location.INamed;

/**
 * Represents the conflict between two or more agents with regards to the
 * movement.
 * 
 * @author Lotzi Boloni
 * 
 */
public class MicroConflict implements Serializable, INamed {

    private static final long serialVersionUID = 8209111826554071974L;
    private List<AbstractPhysicalAgent> participants = new ArrayList<>();
    /**
     * An externally set value which determines the importance of the micro-conflict.
     */
    private double score;
    /**
     * An externally set value if the microconflict is active or not. For an active microconflict,
     * the agents know about it and their behavior is affected by it. 
     */
    private boolean active;
    private String name;
    /**
     * Start time when the first game was played for this MC
     */
    private String startTime;
    /**
     * The list of games which had been played in the context of this microconflict
     */
    private List<SimpleEntry<Double, Game>> games = new ArrayList<>();
    
    /**
     * Stores the maximum social cost as of yet encountered
     */
    private Map<AbstractPhysicalAgent, Double> maxSocialCost = new HashMap<>();
    /*
     * these cost vectors used only if the MC involves a robot
     */
    private CostVector costVector = new CostVector();
    private double totalCost = 0;
        
	/**
     * Constructor for creating a micro-conflict from agents passed as parameter
     */
    public MicroConflict(String startTime, AbstractPhysicalAgent... agents) {
        setStartTime(startTime);
    	this.name = "MC_";
        for (AbstractPhysicalAgent agent : agents) {
        	this.participants.add(agent);
        	this.maxSocialCost.put(agent, 0.0);
            this.name = this.name + "_" + agent.getName();
        }
        this.name = this.name + "_" + getStartTime();
    }

    /**
     * Returns the maximum social cost
     * @param agent
     * @return
     */
    public double getMaxSocialCost(AbstractPhysicalAgent agent) {
        return this.maxSocialCost.get(agent);
    }

    /**
     * Sets the maximum social cost
     * @param agent
     * @return
     */
    public void updateMaxSocialCost(AbstractPhysicalAgent agent, double value) {
        double current = getMaxSocialCost(agent);
        this.maxSocialCost.put(agent, Math.max(current, value));
    }
    
    
    
    
    /**
     * Checks whether the other microconflict has the same agents. Used to
     * prevent having multiple copies of essentially the same agent.
     * 
     * @param other
     * @return
     */
    public boolean sameAgents(MicroConflict other) {
        if (other.participants.containsAll(this.participants)
                && (other.participants.size() == this.participants.size())) {
            return true;
        }
        return false;
    }

    /**
     * @return the participants
     */
    public List<AbstractPhysicalAgent> getParticipants() {
        return this.participants;
    }

    /**
     * @return the score
     */
    public double getScore() {
        return this.score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    
    /**
     * Adds a game which is being played for resolving a micro-conflict
     */
    public void addGame(double time, Game game) {
        SimpleEntry<Double, Game> entry = new SimpleEntry<>(time, game);
        this.games.add(entry);        
    }

    /**
     * @return the games
     */
    public List<SimpleEntry<Double, Game>> getGames() {
        return this.games;
    }
    
    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("MicroConflict: " + getName());
        fmt.indent();
        fmt.is("active", this.active);
        fmt.is("score", this.score);
        fmt.add("Games played:");
        fmt.indent();
        for(SimpleEntry<Double, Game> entry: this.games) {
            fmt.add("Game at: " + Formatter.fmt(entry.getKey()));
            fmt.addIndented(entry.getValue());
        }
        return fmt.toString();
    }

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public CostVector getCostVector() {
		return this.costVector;
	}

	public double getTotalCost() {
		return this.totalCost;
	}
	
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public void updateTotalCost() {
		double tc = getCostVector().getSocial() + 50*getCostVector().getMission();
		setTotalCost(tc);		
	}
}
