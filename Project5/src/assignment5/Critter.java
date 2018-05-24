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
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;

public abstract class Critter {
	/* NEW FOR PROJECT 5 */
	public enum CritterShape {
		CIRCLE,
		SQUARE,
		TRIANGLE,
		DIAMOND,
		STAR,
		EMPTY
	}
	
	/* the default color is white, which I hope makes critters invisible by default
	 * If you change the background color of your View component, then update the default
	 * color to be the same as you background 
	 * 
	 * critters must override at least one of the following three methods, it is not 
	 * proper for critters to remain invisible in the view
	 * 
	 * If a critter only overrides the outline color, then it will look like a non-filled 
	 * shape, at least, that's the intent. You can edit these default methods however you 
	 * need to, but please preserve that intent as you implement them. 
	 */
	public javafx.scene.paint.Color viewColor() { 
		return javafx.scene.paint.Color.WHITE; 
	}
	
	private static boolean frame1 = false;
	
	public javafx.scene.paint.Color viewOutlineColor() { return viewColor(); }
	public javafx.scene.paint.Color viewFillColor() { return viewColor(); }
	
	public abstract CritterShape viewShape(); 
	
	private static String myPackage;
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();
	private boolean moved = false;

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	protected final String look(int direction, boolean steps) {
		//save coords
		int x = x_coord;
		int y = y_coord;
		
		//look walk or run
		if(!steps) {
			moveSteps(1, direction);
		}else moveSteps(2, direction);
		
		//restore saved coordds
		x_coord = x;
		y_coord = y;
		//subtract energy
		energy -= Params.look_energy_cost;
		
		//find overlaps
		for(Critter c : population){
			if(!c.equals(this) && c.x_coord == x && c.y_coord == y && c.energy > 0) {
				//if collision is true return name of 
				return c.toString();
			}
		}
		return null;
		
	}
	
	/* rest is unchanged from Project 4 */
	
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;
	private static boolean timeStep;
	
	/**
	 * walk method for critter, moves only once in direction specified.
	 * if not invoked during a timestep, check for collisions
	 * @param direction
	 */
	protected final void walk(int direction) {
		if(timeStep) {
			moveSteps(1, direction);
		}else {
			int x = x_coord;
			int y = y_coord;
			
			moveSteps(1, direction);
			//cannot move into a space containing another Critter when fleeing
			if(!checkCollision(x_coord, y_coord)){
				x_coord = x;
				y_coord = y;
			}
		}
		
		energy -= Params.walk_energy_cost;
	}

	/**
	 * 
	 * @param x X-coordinate of the Critter checking for collisions
	 * @param y Y-coordinate of the Critter checking for collisions
	 * @return false if the Critter occupies the same space as another Critter
	 * 			true if otherwise.
	 */
	private boolean checkCollision(int x, int y){
		for(Critter c : population){
			if(!c.equals(this) && c.x_coord == x && c.y_coord == y && c.energy > 0) {
				//if collision is true and critter can't move here
				return false;
			}
		}
		return true;
	}
	
	/**
	 * run method for critter, moves twice and deducts appropriate energy.
	 * if not invoked during timestep, must check for collisions with other critters
	 * @param direction
	 */
	protected final void run(int direction) {
		if(timeStep) {
			moveSteps(2, direction);
		}else {
			int x = x_coord;
			int y = y_coord;
			
			moveSteps(2, direction);
			//cannot move into a space containing another Critter when fleeing
			if(!checkCollision(x_coord, y_coord)){
				x_coord = x;
				y_coord = y;
			}
		}
		energy -= Params.run_energy_cost;
	}
	
	/**
	 * move the critter according to the direction provided and the number of moves
	 * @param n -> number of steps to take
	 * @param direction -> direction to go
	 */
	private final void moveSteps(int n, int direction) {
		//if critter has not moved yet
		if(!moved) {
			//switch case on direction
			switch(direction) {
				//South
				case 2: {
					y_coord -= n;
					//wrap around
					if(y_coord < 0){
						y_coord = Params.world_width-1;
					}
					//moved to true
					moved = true;
					break;
				}
				//Southeast
				case 1: {
					x_coord += n;
					y_coord -= n;
					
					if(y_coord < 0){
						y_coord = Params.world_height-1;
					}
					
					if(x_coord > Params.world_width-1) {
						x_coord = 0;
					}
					moved = true;
					break;
				}
				//East
				case 0: {
					x_coord += n;
					
					if(x_coord > Params.world_width-1) {
						x_coord = 0;
					}
					moved = true;
					break;
				}
				//Northeast
				case 7: {
					x_coord += n;
					y_coord += n;
					
					if(x_coord > Params.world_width-1) {
						x_coord = 0;
					}
					
					if(y_coord > Params.world_height-1) {
						y_coord = 0;
					}
					moved = true;
					break;
				}
				//North
				case 6: {
					y_coord += n;
					
					if(y_coord > Params.world_height-1) {
						y_coord = 0;
					}
					moved = true;
					break;
				}
				//Northwest
				case 5: {
					x_coord -= n;
					y_coord += n;
					
					if(x_coord < 0) {
						x_coord = Params.world_width-1;
					}
					
					if(y_coord > Params.world_height-1) {
						y_coord = 0;
					}
					moved = true;
					break;
				}
				//West
				case 4: {
					x_coord -= n;
					
					if(x_coord < 0) {
						x_coord = Params.world_width-1;
					}
					moved = true;
					break;
				}
				//Southwest
				case 3: {
					x_coord -= n;
					x_coord -= n;
					
					if(x_coord < 0) {
						x_coord = Params.world_width-1;
					}
					
					if(y_coord < 0) {
						y_coord = Params.world_height-1;
					}
					moved = true;
					break;
				}
				default: {
				}
			}
		}
	}
	
	/**
	 * add a new Critter offspring to the population with initialized
	 * values as appropriate. The offspring will not be added until the next timestep
	 * @param offspring
	 * @param direction
	 */
	protected final void reproduce(Critter offspring, int direction) {
		if(energy >= Params.min_reproduce_energy) {
			//energy assignment
			offspring.energy = energy/2;
			energy = (energy+1)/2;
			
			//location assignment
			offspring.x_coord = x_coord;
			offspring.y_coord = y_coord;
			
			//move offspring to new location
			offspring.moveSteps(1, direction);
			
			babies.add(offspring);
		}
	}

	public abstract void doTimeStep();
	public abstract boolean fight(String oponent);
	
	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name
	 * @throws InvalidCritterException
	 */
	@SuppressWarnings("rawtypes")
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		try {
			Class critterClass = Class.forName(myPackage + "." + critter_class_name);
			Critter c = (Critter) critterClass.newInstance();
			
			c.energy = Params.start_energy;
			c.x_coord = getRandomInt(Params.world_width);
			c.y_coord = getRandomInt(Params.world_height);
			
			population.add(c);
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new InvalidCritterException(critter_class_name);
		}
	}
	
	/**
	 * Gets a list of critters of a specific type.
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	@SuppressWarnings("rawtypes")
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new java.util.ArrayList<Critter>();
		try {
			Class critterClass = Class.forName(myPackage + "." + critter_class_name);
			for(Critter c : population){
				if(critterClass.isInstance(c)){
					result.add(c);
				}
			}
		} catch (ClassNotFoundException e) {
			throw new InvalidCritterException(critter_class_name);
		}
		
		return result;
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static String runStats(List<Critter> critters) {
		String stats = "" + critters.size() + " critters as follows -- ";
		//System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			stats += (prefix + s + ":" + critter_count.get(s));
			//System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		//System.out.println();		
		return stats;
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}
		
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}
		
		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		// Complete this method.
		population.clear();
		babies.clear();
	}
	
	public static void worldTimeStep() {
		timeStep = true;
		// Complete this method.
		for(Critter c : population){
			c.moved = false;
			c.doTimeStep();
		}

		timeStep = false;
		
		//check for encounters
		int e = 0;
		int f = 0;
		while(e < population.size() && e >= 0 && f >= 0){
			Critter A = population.get(e);
			while(f < population.size() && f >= 0 && e >= 0){
				Critter B = population.get(f);
				if(!A.equals(B)){
					boolean aFight = true;
					boolean bFight = true;
					
					if(A.x_coord == B.x_coord && A.y_coord == B.y_coord && A.energy > 0 && B.energy > 0){
						
						aFight = A.fight(B.toString());
						bFight = B.fight(A.toString());
						
						if(A.x_coord == B.x_coord && A.y_coord == B.y_coord && A.energy > 0 && B.energy > 0){

							boolean winner = battle(A, B, aFight, bFight);
							if(winner){
								population.remove(f);
								f--;
							}
							else{
								population.remove(e);
								e--;
							}
						}
					}
				}
				f++;
			}		
			e++;
		}
		
		//add children to population
		for(int b = 0; b < babies.size(); b++) {
			population.add(babies.get(b));
		}
		
		babies.clear();
		//subtract rest energy
		for(int d = 0; d < population.size(); d++) {
			Critter temp = population.get(d);
			temp.energy -= Params.rest_energy_cost;
		}
		
		//get rid of dead critters
		int c = 0;
		while(c < population.size()) {
			Critter temp = population.get(c);
			if(temp.energy <= 0) {
				population.remove(c);
			}else c++;
		}
		
		//add algae
		for(int d = 0; d < Params.refresh_algae_count; d++) {
			try {
				makeCritter("Algae");
			} catch (InvalidCritterException ignore) {
			}
		}
		
	}

	/**
	 * 
	 * @param a Critter A engaging in the fight
	 * @param b Critter B engaging in the fight
	 * @param aFight Boolean representing if Critter A wants to attack Critter B
	 * @param bFight Boolean representing if Critter B wants to attack Critter A
	 * @return false if Critter B defeats Critter A
	 * 			true if Critter A defeats Critter B
	 */
	private static boolean battle(Critter a, Critter b, boolean aFight, boolean bFight) {
		int aPower = (aFight) ? Critter.getRandomInt(a.energy) : 0; //if A elected not to fight, then it has a power level of 0
		int bPower = (bFight) ? Critter.getRandomInt(b.energy) : 0; //if B elected not to fight, then it has a power level of 0
		if(aPower > bPower){
			a.energy += (b.energy/2);
			return true;
		}
		else{
			b.energy += (a.energy/2);
			return false;
		}
		
		
	}

	public static void displayWorld() {
		drawText();
		CritterShape[][] worldGrid = Critter.displayGrid();
		//Main.grid = new Canvas(2*primScreenBounds.getWidth()/3, primScreenBounds.getHeight());
		//Canvas grid = new Canvas(Params.world_width*25,Params.world_height*25);
		//Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		frame1 = !frame1;
		double size_horizontal = (Main.grid.getWidth() * .9)/Params.world_width;
		double size_vertical = (Main.grid.getHeight() * .9)/Params.world_height;
		double rect_size = size_vertical;
		
		if(size_horizontal < size_vertical) {
			rect_size = size_horizontal;
		}
		GraphicsContext gc = Main.grid.getGraphicsContext2D();
		drawGrid(gc, rect_size);
		
		
		//add critters
		Critter[][] critter_loc = new Critter[Params.world_height][Params.world_width];

		for(int c = 0; c < population.size(); c++) {
			Critter temp = population.get(c);			
			critter_loc[temp.y_coord][temp.x_coord] = temp;
			
		}
		
		for(int i = 0; i < Params.world_height; i ++){
			for(int j = 0; j < Params.world_width; j++){
				double x_location = j * rect_size;
				double y_location = i * rect_size;
				Shape critter_shape;
				gc.setFill(Color.BLACK);
				
				switch(worldGrid[i][j]){
					case CIRCLE:{
						//gc.setFill()
						Color color_crit = critter_loc[i][j].viewColor();
						Color color_stroke = critter_loc[i][j].viewOutlineColor();
						Color color_fill = critter_loc[i][j].viewFillColor();
						
						gc.setFill(color_fill);
						gc.setStroke(color_stroke);
						
						if(color_stroke.equals(Color.GREEN)){
							gc.fillOval(x_location, y_location, rect_size, rect_size);
						}
						else{
							if(frame1){
								gc.fillOval(x_location, y_location, rect_size, rect_size);
							}
							else{
								gc.fillOval(x_location + (int)(rect_size * (1.0/16)), y_location, (rect_size*(7.0/8)), rect_size);
							}
						}
						break;
					}
					case SQUARE: {
						Color color_crit = critter_loc[i][j].viewColor();
						Color color_stroke = critter_loc[i][j].viewOutlineColor();
						Color color_fill = critter_loc[i][j].viewFillColor();
						
						gc.setFill(color_fill);
						gc.setStroke(color_stroke);
						drawSquareCritter(gc, frame1, x_location, y_location, rect_size);
						break;
					}
					case TRIANGLE: {
						Color color_crit = critter_loc[i][j].viewColor();
						Color color_stroke = critter_loc[i][j].viewOutlineColor();
						Color color_fill = critter_loc[i][j].viewFillColor();
						
						gc.setFill(color_fill);
						gc.setStroke(color_stroke);
						drawTriangleCritter(gc, ((Critter3) critter_loc[i][j]).didAct(), x_location, y_location, rect_size);
						break;
					}
					case DIAMOND: {
						Color color_crit = critter_loc[i][j].viewColor();
						Color color_stroke = critter_loc[i][j].viewOutlineColor();
						Color color_fill = critter_loc[i][j].viewFillColor();
						
						gc.setFill(color_fill);
						gc.setStroke(color_stroke);
						drawDiamondCritter(gc, ((Critter4) critter_loc[i][j]).isHappy(), x_location, y_location, rect_size);
						break;
					}
					case STAR: {
						break;
					}
					default:{
					}
				}
			}
		}
		
	}
	
	private static void drawSquareCritter(GraphicsContext gc, boolean frame, double x, double y, double rect_size) {
		if(frame){
			gc.fillRect(x + (1.0 * rect_size / 8), y + (1.0 * rect_size / 8), rect_size * (3.0 / 4), rect_size * (3.0 / 4));
			double eyePos1 = x + (rect_size / 2.0) - rect_size * (1.0 / 16);
			double eyePos2 = x + (rect_size / 2.0) + rect_size * (1.0 / 16);
			double eyeHeight = y + (rect_size / 2.0) - rect_size * (1.0 / 4);
			
			gc.setLineWidth(2);
			gc.setStroke(Color.BLACK);
			
			gc.strokeLine(eyePos1, eyeHeight, eyePos1, eyeHeight + (rect_size * (1.0 / 2)));
			gc.strokeLine(eyePos2, eyeHeight, eyePos2, eyeHeight + (rect_size * (1.0 / 2)));
		}
		else{
			gc.fillRect(x + (1.0 * rect_size / 8), y + (1.0 * rect_size / 4), rect_size * (3.0 / 4), rect_size * (3.0 / 4));
			double eyePos1 = x + (rect_size / 2.0) - rect_size * (1.0 / 16);
			double eyePos2 = x + (rect_size / 2.0) + rect_size * (1.0 / 16);
			double eyeHeight = y + (rect_size / 2.0) - rect_size * (1.0 / 8);
			
			gc.setLineWidth(2);
			gc.setStroke(Color.BLACK);
			
			gc.strokeLine(eyePos1, eyeHeight, eyePos1, eyeHeight + (rect_size * (1.0 / 2)));
			gc.strokeLine(eyePos2, eyeHeight, eyePos2, eyeHeight + (rect_size * (1.0 / 2)));
		}
	}
	
	private static void drawTriangleCritter(GraphicsContext gc, boolean acted, double x, double y, double rect_size) {
		if(acted){
			double eyePos1 = x + (rect_size / 2.0) - rect_size * (1.0 / 16) - (rect_size * (1.0 / 32));
			double eyePos2 = x + (rect_size / 2.0) + rect_size * (1.0 / 16) - (rect_size * (1.0 / 32));
			double eyeHeight = y + (rect_size / 2.0);
			
			gc.fillPolygon(new double[]{(x + (rect_size / 2.0)), x, (x + rect_size)},
	                new double[]{(y + (rect_size / 4.0)), (y + rect_size), (y + rect_size)}, 3);

			gc.setFill(Color.BLACK);
			gc.fillOval(eyePos1, eyeHeight, (rect_size / 16.0), (rect_size / 16.0));
			gc.fillOval(eyePos2, eyeHeight, (rect_size / 16.0), (rect_size / 16.0));
		}
		else{
			double eyePos1 = x + (rect_size / 2.0) - rect_size * (1.0 / 16) - (rect_size * (1.0 / 32));
			double eyePos2 = x + (rect_size / 2.0) + rect_size * (1.0 / 16) - (rect_size * (1.0 / 32));
			double eyeHeight = y + (rect_size / 2.0);			
			
			gc.fillPolygon(new double[]{(x + (rect_size / 2.0)), x, (x + rect_size)},
	                new double[]{(y + (rect_size * 3 / 4.0)), (y), (y)}, 3);
			
			gc.setFill(Color.BLACK);
			gc.fillOval(eyePos1, eyeHeight, (rect_size / 16.0), (rect_size / 16.0));
			gc.fillOval(eyePos2, eyeHeight, (rect_size / 16.0), (rect_size / 16.0));
			
			
		}
	}
	
	private static void drawDiamondCritter(GraphicsContext gc, boolean angry, double x, double y, double rect_size) {
		if(angry){
			double eyePos1 = x + (rect_size / 2.0) - rect_size * (1.0 / 16) - (rect_size * (1.0 / 32));
			double eyePos2 = x + (rect_size / 2.0) + rect_size * (1.0 / 16) - (rect_size * (1.0 / 32));
			double eyeHeight = y + (rect_size / 2.0);
			
			gc.fillPolygon(new double[]{(x + (rect_size / 2.0)), (x + rect_size), (x + (rect_size / 2.0)), x},
	                new double[]{y, (y + (rect_size / 2.0)), (y + rect_size), (y + (rect_size / 2.0))}, 4);

			gc.setFill(Color.BLACK);
			gc.fillOval(eyePos1, eyeHeight, (rect_size / 16.0), (rect_size / 16.0));
			gc.fillOval(eyePos2, eyeHeight, (rect_size / 16.0), (rect_size / 16.0));
		}
		else{
			double eyePos1 = x + (rect_size / 2.0) - rect_size * (1.0 / 16) - (rect_size * (1.0 / 32));
			double eyePos2 = x + (rect_size / 2.0) + rect_size * (1.0 / 16) - (rect_size * (1.0 / 32));
			double eyeHeight = y + (rect_size / 2.0);			
			
			gc.fillPolygon(new double[]{(x + (rect_size / 2.0)), (x + rect_size), (x + (rect_size / 2.0)), x},
	                new double[]{y, (y + (rect_size / 2.0)), (y + rect_size), (y + (rect_size / 2.0))}, 4);
			
			gc.setFill(Color.BLACK);
			gc.fillOval(eyePos1, eyeHeight, (rect_size / 16.0), (rect_size / 16.0));
			gc.fillOval(eyePos2, eyeHeight, (rect_size / 16.0), (rect_size / 16.0));
			
			
		}
	}
	
	
	public static void drawText() {
		//rows
		int height = Params.world_height+2;
		//columns
		int width = Params.world_width+2;
		String[][] world = new String[height][width];
		
		world[0][0] = "+";
		world[0][width-1] = "+";
		world[height-1][0] = "+";
		world[height-1][width-1] = "+";
		
		//column border
		for(int a = 1; a < width - 1; a++) {
			world[0][a] = "-";
			world[height-1][a] = "-";
		}
		
		//row border
		for(int b = 1; b < height - 1; b++) {
			world[b][0] = "|";
			world[b][width-1] = "|";
		}
		
		//add critters
		for(int c = 0; c < population.size(); c++) {
			Critter temp = population.get(c);			
			world[temp.y_coord+1][temp.x_coord+1] = temp.toString();
			
		}
		
		//print array
		for(int a = 0; a < height; a++) {
			
			for(int b = 0; b < width; b++) {
				if(world[a][b] == null) {
					world[a][b] = " ";
				}
				
				System.out.print(world[a][b]);
			}
			
			System.out.println();
		}
		
	}
	
	public static void drawGrid(GraphicsContext gc, double rect_size){
		
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, Main.grid.getWidth(), Main.grid.getHeight());
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		
		double horizontal_offset = Main.grid.getWidth() - (Params.world_width * rect_size);
		double vertical_offset = Main.grid.getHeight() - (Params.world_height * rect_size);

		
		for(int i = 0; i < Params.world_height + 1; i++){
			gc.strokeLine(0, (i*rect_size), (Main.grid.getWidth() - horizontal_offset), (i*rect_size));
		}
		for(int i = 0; i < Params.world_width + 1; i++){
			gc.strokeLine((i*rect_size), 0, (i*rect_size), (Main.grid.getHeight() - vertical_offset));
		}
	}
	
	public static CritterShape[][] displayGrid() {
		//rows
		int height = Params.world_height;
		//columns
		int width = Params.world_width;
		CritterShape[][] world = new CritterShape[height][width];
		
		for(int i = 0; i < Params.world_height; i++){
			for(int j = 0; j < Params.world_width; j++){
				world[i][j] = CritterShape.EMPTY;
			}
		}
		
		//add critters
		for(int c = 0; c < population.size(); c++) {
			Critter temp = population.get(c);			
			world[temp.y_coord][temp.x_coord] = temp.viewShape();
			
		}
		
		return world;
		
	}
	
}