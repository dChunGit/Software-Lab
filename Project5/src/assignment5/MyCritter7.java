package assignment5;

import assignment5.Critter.TestCritter;

public class MyCritter7 extends TestCritter {
	
	@Override
	public void doTimeStep() {
	}

	@Override
	public boolean fight(String opponent) {

		return true;
	}

	@Override
	public String toString () {
		return "7";
	}

	@Override
	public CritterShape viewShape() {
		// TODO Auto-generated method stub
		return null;
	}
}
