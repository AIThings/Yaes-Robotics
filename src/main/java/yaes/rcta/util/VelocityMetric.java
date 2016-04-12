package yaes.rcta.util;

import java.io.Serializable;

/**
 * 
 * @author Taranjeet
 *
 */
public class VelocityMetric implements Serializable {

	private static final long serialVersionUID = 8711401340342803774L;

	private double m_prevDistance = 0.0; // the prior sensor input (used to
											// compute velocity)
	private double m_totalVelocity = 0.0; // the sum of the errors for use in
											// the integral calc
	private double m_Velocity = 0.0;
	private double m_inputDistance = 0.0;

	/**
	 * Read the input, calculate the output accordingly, and write to the
	 * output. This should only be called by the PIDTask and is created during
	 * initialization.
	 */
	private void calculate() {

		// Calculate the acceleration with time step 1
		m_Velocity = m_inputDistance - m_prevDistance;

		/* Integral of velocities */

		m_totalVelocity += m_Velocity;

		// Set the current error to the previous error for the next cycle
		m_prevDistance = m_inputDistance;

	}

	/**
	 * @return the integrated acceleration
	 */
	public double getTotalVelocity() {
		return m_totalVelocity;
	}

	/**
	 * set the new distance
	 */
	public void addInput(double distance) {
		m_inputDistance = distance;
		calculate();
	}

	public double getVelocity() {
		return m_Velocity;
	}

}