package assignment4;
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
 * @author Faisal M - First Critter
 * Critter 3 is extremely lazy. It will only move if an opponent is threatening its sleeping zone or if it makes a baby (a 25% chance given 
 * that the minimum reproduction energy requirement is met) in which case, it needs to make room for the child.
 */

public class Critter3 extends Critter {
	private boolean acted = false;
	@Override
	public String toString() {
		return "3"; 
	}
	
	@Override
	public void doTimeStep() {
		if(Critter.getRandomInt(2) == 1 && getEnergy() > Params.min_reproduce_energy){
			acted = true;
			Critter3 child = new Critter3();
			child.acted = false;
			reproduce(child, 0);
			walk(Critter.getRandomInt(8));
		}
		else acted = false;
	}

	@Override
	public boolean fight(String opponent) {
		acted = true;
		return true;
	}
	
	public static void runStats(java.util.List<Critter> critters){
		int numMoved = 0;
		for(Critter c : critters){
			Critter3 c3 = (Critter3) c;
			if(c3.acted)
				numMoved++;
		}
		System.out.println("There are: " + critters.size() + "Critter 3 critters.");
		System.out.println("" + (((100.00) * numMoved) / critters.size()) + "% of which had to go out of their way and act last turn.");
		System.out.println();
	}

}
