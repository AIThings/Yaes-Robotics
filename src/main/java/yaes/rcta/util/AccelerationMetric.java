package yaes.rcta.util;

import java.io.Serializable;
 
/**
 * 
 * @author Taranjeet
 *
 */
public class AccelerationMetric implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 8711401340342803774L;
	
    private double m_prevVelocity = 0.0;   // the prior sensor input (used to compute velocity)
    private double m_totalAcceleration = 0.0; //the sum of the errors for use in the integral calc
    private double m_acceleration = 0.0;
    private double m_inputVelocity = 0.0;

  

    /**
     * Read the input, calculate the output accordingly, and write to the output.
     * This should only be called by the PIDTask
     * and is created during initialization.
     */
    private void calculate() {

            // Calculate the acceleration with time step 1
    		m_acceleration = m_inputVelocity - m_prevVelocity;

            /* Integrate the acceleration*/
           
    		m_totalAcceleration += m_acceleration;

            // Set the current error to the previous error for the next cycle
    		m_prevVelocity = m_inputVelocity;

    }

    

    /**
     * @return the integrated acceleration
     */
    public double getTotalAcceleration() {
        return Math.abs(m_totalAcceleration);
    }  

    /**
     * input the new speed
     */
    public void addInput(double velocity){
    	m_inputVelocity = velocity;
    	calculate();
    }

    public double getAcceleration(){
    	return m_acceleration;
    }
}