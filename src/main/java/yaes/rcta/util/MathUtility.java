package yaes.rcta.util;

import yaes.world.physical.location.Location;
import yaes.world.physical.map.MapHelper;

public class MathUtility {

	/**
	 * Inverting the importance of distance with threat level , Higher the
	 * distance, lower the threat. 
	 * 
	 * Sigmoid function
	 * 
	 * @param person1Location
	 * @param person2Location
	 * @return
	 */
	public static double chanceOfAttack(Location person1Location,
			Location person2Location) {

		double distance = MapHelper.distance(person1Location, person2Location);
		if (distance > 100) {
			return 0.0;
		}
		// scale = 1;
		// inflation = 0;
		// tighten = 10;
		// retval = Math.floor(scale * (-1 + 2/(1 + Math.exp(-(x - inflation) *
		// tighten))) * 10000) / 10000;
		// retval value is between 0 and 1;
		double retval = Math.floor(Math.exp((-distance * 10) / 100) * 10000) / 10000;
		return retval;
	}

	/**
	 * Probabilities of [not-(not-A and not-B)]
	 * 
	 * @param p
	 * @return
	 */
	public static double combineProb(double... p) {
		double totalProbability = 1.0;
		for (int i = 0; i < p.length; i++) {
			double x = 1 - p[i];
			totalProbability *= x;
		}
		// Rounding to four decimal place
		return Math.floor((1 - totalProbability) * 10000) / 10000;
	}

	public static void main(String[] args0) {
		double x1 = chanceOfAttack(new Location(138, 60), new Location(140, 60));
		System.out.println("x1: " + x1);
		double x2 = chanceOfAttack(new Location(140, 70), new Location(140, 60));
		System.out.println("x2: " + x2);
		double combine = combineProb(new double[] { x1, x2 });
		System.out.println("combine: " + combine);
	}

}
