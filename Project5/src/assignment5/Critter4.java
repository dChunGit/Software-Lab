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

import java.util.List;

import assignment5.Critter.CritterShape;

/**
 * 
 * @author Faisal M - Second Critter
 * Critter4 hates odd numbers. Will always move in an even(cardinal) direction and only reproduces if it has at least
 * minimum reproduction energy and its energy is an even number.
 * Keeps a count of how many turns it has been since it was born and it is only happy on the even numbered turns.
 * If it meets a Critter1 or a Critter3, which are represented by odd numbers, and it is not happy, it will viciously attack.
 */

public class Critter4 extends Critter {
	private boolean happy = true;
	private int lifespan = 0;	//counter specifying how many turns since this Critter's creation
	@Override
	public String toString() {
		return "4"; 
	}
	
	@Override
	public void doTimeStep() {
		int dir = Critter.getRandomInt(4)*2;
		if(lifespan % 2 == 0)
			happy = true;
		else
			happy = false;
		if(getEnergy() > Params.min_reproduce_energy && (getEnergy() % 2) == 1){
			reproduce(new Critter4(), Critter.getRandomInt(4)*2);
		}
		lifespan++;
		walk(dir);
	}

	@Override
	public boolean fight(String opponent) {
		return (opponent.equals("1") || opponent.equals("3") && !happy);
	}
	
	public boolean isHappy(){return happy;}
	
	/**
	 * 
	 * @param critters List of all Critter4 critters.
	 * Prints out how many Critter4 critters exist in the world and what percentage of them are happy.
	 */
	public static String runStats(List<Critter> critters){
		int numHappy = 0;
		for(Critter c : critters){
			Critter4 c4 = (Critter4) c;
			if(c4.happy)
				numHappy++;
		}
		String stats = ("There are: " + critters.size() + " Critter 4 critters.\n");
		stats += ("" + (((100.00) * numHappy) / critters.size()) + "% of which are happy.");
		
		return stats;
		//System.out.println();
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.DIAMOND;
	}
	
	@Override
	public javafx.scene.paint.Color viewFillColor() { 
		if(happy) 
			return javafx.scene.paint.Color.LIMEGREEN;
		else
			return javafx.scene.paint.Color.DARKRED;
	}
}
