package yaes.rcta.agents;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * The vector of various cost types occuring to an agent
 * @author Lotzi Boloni
 *
 */
public class CostVector implements Serializable {

    private static final long serialVersionUID = -6289156284109019485L;
    private ArrayList<Double> cgameSelf = new ArrayList<Double>();
    private ArrayList<Double> cgameOpponent = new ArrayList<Double>();
    private String move = "";
    private double social = 0;
    private double maxSocial = 0;
    private double timeDelay = 0;
    private double mission = 0;
    /**
     * @return the social
     */
    public double getSocial() {
        return social;
    }
    /**
     * @return the timeDelay
     */
    public double getTimeDelay() {
        return timeDelay;
    }
    /**
     * @return the mission
     */
    public double getMission() {
        return mission;
    }
    
    public void addSocial(double val) {
        social += val;
    }
    
    public void addTimeDelay(double val) {
        timeDelay += val;
    }

    public void addMission(double val) {
        mission += val;
    }
    
    public void addMaxSocial(double val) {
        maxSocial += val;
    }
    
    /**
     * @return the maxSocial
     */
    public double getMaxSocial() {
        return maxSocial;
    }
    
    //add agent's own values of composite game
    public void addCGameSelf(double val){
    	cgameSelf.add(val);   	
    }
    
    //add opponents values of composite game
    public void addCGameOpponent(double val){
    	cgameOpponent.add(val);   	
    }
    
    public ArrayList<Double> getCGameSelf(){
    	return cgameSelf;
    }
    
    public ArrayList<Double> getCGameOpponent(){
    	return cgameOpponent;
    }
	public String getMove() {
		return move;
	}
	public void setMove(String move) {
		this.move = move;
	}
}
