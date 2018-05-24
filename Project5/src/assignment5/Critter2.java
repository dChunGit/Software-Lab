package assignment5;
/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * David Chun
 * dc37875
 * 16236
 * Faisal Mahmood
 * fm7859
 * 16238
 * Slip days used: <0>
 * Spring 2017
 */
/**
 * 
 * @author David C - Second Critter
 * Critter2 will create a new child and run away
 * It will only fight if it has spawned at least 2 offspring and
 * if it has less energy than it takes to reproduce once
 * Stats display total number of offspring and offspring produced
 */

public class Critter2 extends Critter {
	private int offspring = 0;
	
	@Override
	public String toString() {
		return "2"; 
	}
	
	@Override
	public void doTimeStep() {
		Critter2 child = new Critter2();
		child.offspring = 0;
		reproduce(child, Critter.getRandomInt(8));
		run(Critter.getRandomInt(8));
		offspring++;
		
	}

	@Override
	public boolean fight(String oponent) {
		return offspring > 2 && getEnergy() < Params.min_reproduce_energy;
	}
	
	public static String runStats(java.util.List<Critter> critter2) {
		int numoff = 0;
		for (Object obj : critter2) {
			Critter2 c = (Critter2) obj;
			numoff += c.offspring;
			
		}
		String stats = ("" + critter2.size() + " total Critter2's    ");
		stats += ("" + numoff + " total offspring   ");
		
		return stats;
		//System.out.println();
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.SQUARE;
	}
	
	@Override
	public javafx.scene.paint.Color viewFillColor() { return javafx.scene.paint.Color.BLUE; }

}