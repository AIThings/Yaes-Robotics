package yaes.rcta.agents.steering;

import java.io.Serializable;

import javax.vecmath.Vector2d;

/**
 * Class for implementing steering behavior and calling them as boids.
 * 
 * @author Taranjeet Singh Bhatia
 *
 */
public class Boid implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6917377505547745490L;
	public double mass = 1.0;
	public int maxSpeed = 2;
	public static final int AVOID_FORCE = 350;
	public static final int MAX_AVOID_AHEAD = 100;
	public static final double MAX_FORCE = 5.4;

	private Vector2d position;
	private Vector2d velocity;
	private Vector2d steerForce;
	private Vector2d ahead;
	private Vector2d avoidance;
	private double distanceToTarget;
	/**
	 * Vector in the direction from the character to the target (Craig W.
	 * Reynolds).
	 */
	private Vector2d desiredVelocity;

	public Boid(Vector2d initialPosition) { // , ArrayList<Vector2d>
											// checkpoints) {
		desiredVelocity = new Vector2d();
		position = initialPosition;
		velocity = new Vector2d(0, 0);
		steerForce = new Vector2d();
		ahead = new Vector2d();
		avoidance = new Vector2d();
		distanceToTarget = 0d;
	}

	/**
	 * Steers the boid to move toward the target
	 * (http://www.red3d.com/cwr/steer/SeekFlee.html).
	 */
	public void seek(Vector2d targetPosition) {
		desiredVelocity.sub(targetPosition, position);
		distanceToTarget = desiredVelocity.length();
		desiredVelocity.normalize();
		desiredVelocity.scale(maxSpeed);
		steerForce.sub(desiredVelocity, velocity);
	}

	/**
	 * FIXME: This is incomplete because we do not have implementation for
	 * detecting obstacles or walls.
	 */
	public void collisionAvoidance() {
		Vector2d tv = new Vector2d(velocity.x, velocity.y);
		tv.normalize();
		tv.scale(MAX_AVOID_AHEAD * velocity.length() / maxSpeed);
		ahead = new Vector2d(position.x, position.y);
		ahead.add(tv);

	}

	/**
	 * Steers the boid to move away from the target
	 * (http://www.red3d.com/cwr/steer/SeekFlee.html).
	 */
	public void flee(Vector2d targetPosition) {
		desiredVelocity.sub(position, targetPosition);
		distanceToTarget = desiredVelocity.length();
		desiredVelocity.normalize();
		desiredVelocity.scale(maxSpeed);
		steerForce.sub(desiredVelocity, velocity);
	}

	/**
	 * Steers the boid to arrive slowly on the target
	 * (http://www.red3d.com/cwr/steer/Arrival.html).
	 */
	public void arrival(Vector2d targetPosition) {
		int arriveRadius = 10;
		desiredVelocity.sub(targetPosition, position);
		distanceToTarget = desiredVelocity.length();
		if (distanceToTarget > 0) {
			Double speed = maxSpeed * (distanceToTarget / arriveRadius);
			speed = Math.min(speed, maxSpeed);
			desiredVelocity.scale(speed / distanceToTarget);
			steerForce.sub(desiredVelocity, velocity);
		}
	}

	public void update(Vector2d targetPosition, BehaviorEnum behavior) {
		chooseBehavior(targetPosition, behavior);
		// acceleration = force / mass
		Vector2d acceleration = new Vector2d();
		acceleration.x = steerForce.x / mass;
		acceleration.y = steerForce.y / mass;
		velocity.add(acceleration);
		position.add(velocity, position);
	}

	private void chooseBehavior(Vector2d targetPosition, BehaviorEnum behavior) {
		switch (behavior) {
		case SEEK:
			seek(targetPosition);
			break;
		case FLEE:
			flee(targetPosition);
			break;
		case ARRIVAL:
			arrival(targetPosition);
			break;
		case PATHFOLLOWING:
		default:
			break;
		}
	}

	public Vector2d getSteerForce() {
		return steerForce;
	}

	public int getX() {
		return (int) position.x;
	}

	public int getY() {
		return (int) position.y;
	}

	public int getDistanceToTarget() {
		return (int) distanceToTarget;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
}
