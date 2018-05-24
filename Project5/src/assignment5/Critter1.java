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
 * @author David C - First Critter
 * Critter1 moves to a random location during its time step
 * It will attempt to fight only if has at least half of its original energy left
 */

public class Critter1 extends Critter {
	
	@Override
	public String toString() {
		return "1"; 
	}

	@Override
	public void doTimeStep() {
		int dir = Critter.getRandomInt(8);

	 	walk(dir);
  		look(dir, false);
  		
  	}
  
  	@Override
  	public boolean fight(String oponent) {
 		return getEnergy() > Params.start_energy/2;
  	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.CIRCLE;
	}
  	
	@Override
	public javafx.scene.paint.Color viewColor() { return javafx.scene.paint.Color.RED; }
}