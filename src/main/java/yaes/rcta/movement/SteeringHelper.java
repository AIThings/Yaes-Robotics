package yaes.rcta.movement;

import java.io.Serializable;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import yaes.rcta.agents.AbstractPhysicalAgent;
import yaes.ui.text.TextUi;
import yaes.world.physical.environment.EnvironmentModel;
import yaes.world.physical.location.Location;

/**
 * 
 * @author Taranjeet
 *
 */
public class SteeringHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3899089892310365158L;

	public EnvironmentModel em;
	// Parameter for wander force
	public static final double CIRCLE_DISTANCE = 6;
	public static final double CIRCLE_RADIUS = 8;
	public static final double ANGLE_CHANGE = 1;
	private double wanderAngle;

	// Parameter for seek/flee
	public Vector2D desired; // desired vector direction

	// Pursuit and Evade
	public Vector2D distance = new Vector2D(0, 0);
	public Vector2D targetFuturePosition = new Vector2D(0, 0);

	// Collision Avoidance
	public static final double MAX_FORCE = 5.4; // 300; // Maximum Steering force
	public static final double MAX_VELOCITY = 2.4; // Maximum movement velocity
	public static final double MAX_AVOID_AHEAD = 10; // Maximum avoid distance
														// ahead
	public static final double AVOID_FORCE = 200; // Maximum avoidance force
													// magnitude

	// Helper parameter
	public Vector2D steering;
	public AbstractPhysicalAgent host;
	
	public SteeringHelper() {
		
	}

	public SteeringHelper(AbstractPhysicalAgent host,
			EnvironmentModel environmentModel) {
		this.host = host;
		this.desired = new Vector2D(0, 0);
		this.steering = new Vector2D(0, 0);
		this.setWanderAngle(0);
		host.setVelocity(truncate(host.getVelocity(), host.getMaxVelocity()));
		em = environmentModel;
	}

	public void seek(Vector2D target, double slowingRadius) {
		this.steering = this.steering.add(doSeek(target, slowingRadius));

	}

	private Vector2D doSeek(Vector2D target, double slowingRadius) {
		Vector2D force;
		double distance;

		desired = target.subtract(this.host.getPosition());

		distance = length(desired);
		desired = desired.normalize();
		if (distance <= slowingRadius) {
			desired = desired.scalarMultiply(this.host.getMaxVelocity()
					* distance / slowingRadius);
		} else {
			desired = desired.scalarMultiply(this.host.getMaxVelocity());
		}
		force = desired.subtract(this.host.getVelocity());

		return force;
	}

	/**
	 * Flee behavior: which makes the character move away from a pursuer.
	 * 
	 * @param target
	 */
	public void flee(Vector2D target) {
		this.steering = this.steering.add(doFlee(target));
	}

	private Vector2D doFlee(Vector2D target) {
		Vector2D force;
		desired = host.getPosition().subtract(target);
		desired = desired.normalize();
		desired = desired.scalarMultiply(this.host.getMaxVelocity());
		force = desired.subtract(this.host.getVelocity());
		return force;
	}

	/**
	 * wander behavior: which makes the agent move randomly, visually pleasant
	 * and realistic enough.
	 */
	public void wander() {
		this.steering = this.steering.add(doWander());
	}

	private Vector2D doWander() {
		Vector2D wanderForce, circleCenter, displacement;

		circleCenter = new Vector2D(host.getVelocity().getX(), host
				.getVelocity().getY());
		circleCenter = circleCenter.scalarMultiply(CIRCLE_DISTANCE);

		displacement = new Vector2D(0, -1);
		displacement = displacement.scalarMultiply(CIRCLE_RADIUS);

		displacement = setAngle(displacement, wanderAngle);
		wanderAngle += Math.random() * ANGLE_CHANGE - ANGLE_CHANGE * 0.5;

		wanderForce = circleCenter.add(displacement);

		return wanderForce;
	}

	/**
	 * Evade: which make your agent avoid the target.
	 * 
	 * @param target
	 */
	public void evade(AbstractPhysicalAgent target) {
		this.steering = this.steering.add(doEvade(target));
	}

	private Vector2D doEvade(AbstractPhysicalAgent target) {
		distance = target.getPosition().subtract(host.getPosition());

		double updatesNeeded = length(distance) / host.getMaxVelocity();
		Vector2D tv = new Vector2D(target.getVelocity().getX(), target
				.getVelocity().getY());
		tv = tv.scalarMultiply(updatesNeeded);
		targetFuturePosition = new Vector2D(target.getPosition().getX(), target
				.getPosition().getY()).add(tv);
		return doFlee(targetFuturePosition);
	}

	/**
	 * Pursuit: which make your agent follow the target.
	 * 
	 * @param target
	 */
	public void pursuit(AbstractPhysicalAgent target) {
		this.steering = this.steering.add(doPursuit(target));
	}

	private Vector2D doPursuit(AbstractPhysicalAgent target) {
		distance = target.getPosition().subtract(host.getPosition());
		double updatesNeeded = length(distance) / host.getMaxVelocity();
		Vector2D tv = new Vector2D(target.getVelocity().getX(), target
				.getVelocity().getY());
		tv = tv.scalarMultiply(updatesNeeded);
		targetFuturePosition = new Vector2D(target.getPosition().getX(), target
				.getPosition().getY()).add(tv);

		return doSeek(targetFuturePosition, 0);
	}

	public void collisionAvoidance() {
		this.steering = this.steering.add(doCollisionAvoidance());

	}

	private Vector2D doCollisionAvoidance() {
		Vector2D tv = new Vector2D(host.getVelocity().getX(), host
				.getVelocity().getY());
		tv = tv.normalize();
		tv = tv.scalarMultiply(MAX_AVOID_AHEAD * length(host.getVelocity())
				/ MAX_VELOCITY);
		Vector2D ahead = new Vector2D(host.getPosition().getX(), host
				.getPosition().getY());
		ahead = ahead.add(tv);
		Vector2D avoidance = new Vector2D(0, 0);
		if (obstacleAhead(ahead)) {
			// perpendicular vector force
			// A vector perpendicular to x = (a,b) is (-b,a)
			avoidance = new Vector2D(-ahead.getY(), ahead.getX());
			avoidance = avoidance.normalize();
			avoidance = avoidance.scalarMultiply(AVOID_FORCE);
		} else {
			avoidance = avoidance.scalarMultiply(0);
		}

		return avoidance;
	}

	private boolean obstacleAhead(Vector2D ahead) {
		MapLocationAccessibility mapAccess = new MapLocationAccessibility();
		if (!mapAccess.isAccessible(em,
				new Location(ahead.getX(), ahead.getY()))) {
			return true;
		} else if (ahead.getX() < em.getXLow() || ahead.getX() > em.getXHigh()
				|| ahead.getY() < em.getYLow() || ahead.getY() > em.getYHigh()) {
			return true;
		}

		return false;
	}

	private Vector2D truncate(Vector2D vector, double max) {
//		double i;
//		i = max / length(vector);
//		i = i < 1.0 ? i : 1.0;
//		vector = vector.scalarMultiply(i);
//		return vector;
		return length(vector) > max? vector.normalize().scalarMultiply(max):vector;
		
	}

	public double getAngle(Vector2D vector) {
		return Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));
	}

	public double length(Vector2D vector) {
		return vector.distance(vector.getZero());
	}

	private Vector2D setAngle(Vector2D displacement, double wanderAngle2) {
		double len = length(displacement);
		displacement = new Vector2D(Math.cos(wanderAngle2) * len,
				Math.sin(wanderAngle2) * len);
		return displacement;
	}

	public void update() {
		Vector2D velocity = host.getVelocity();
		Vector2D position = host.getPosition();

		steering = truncate(steering, MAX_FORCE);
		steering = steering.scalarMultiply(1 / host.getMass());

		velocity = velocity.add(steering);
		velocity = truncate(velocity, host.getMaxVelocity());
		position = position.add(velocity);

		host.setVelocity(velocity);
		host.setPosition(position);

	}

	public void reset() {
		desired = new Vector2D(0, 0);
		steering = new Vector2D(0, 0);
	}

	public double getWanderAngle() {
		return wanderAngle;
	}

	public void setWanderAngle(double wanderAngle) {
		this.wanderAngle = wanderAngle;
	}
	
	public static void main(String[] args){
		SteeringHelper steer = new SteeringHelper();
		TextUi.println(steer.truncate(new Vector2D(4,4), 2));
		TextUi.println(new Vector2D(4, 4).normalize());
		//TextUi.println(steer.getAngle(new Vector2D(-1, 0)));
		//TextUi.println(RctaContext.getAngle(new Location(0,0),new Location(-1,0),true));
	}

}
