package assignment5;
/* CRITTERS Main.java
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


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jdk.management.cmm.SystemResourcePressureMXBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import assignment5.Critter.CritterShape;

import java.io.*;
import java.lang.reflect.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;



/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main extends Application{

    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    public static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    private static boolean DEBUG = false; // Use it or not, as you wish!
    private boolean running = false;
    static PrintStream old = System.out;	// if you want to restore output to console
    static Canvas grid;


    // Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     * @param args args can be empty.  If not empty, provide two parameters -- the first is a file name, 
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
    public static void main(String[] args) { 
    	launch(args);
    	
        if (args.length != 0) {
            try {
                inputFile = args[0];
                System.out.println(inputFile);
                kb = new Scanner(new File(inputFile));			
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java Main OR java Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
            }
            if (args.length >= 2) {
                if (args[1].equals("test")) { // if the word "test" is the second argument to java
                    // Create a stream to hold the output
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    // Save the old System.out.
                    old = System.out;
                    // Tell Java to use the special stream; all console output will be redirected here from now
                    System.setOut(ps);
                }
            }
        } else { // if no arguments to main
            kb = new Scanner(System.in); // use keyboard and console
        }

        /* Do not alter the code above for your submission. */
        /* Write your code below. */
        System.out.flush();

    }
    
    public String parse(String input) {
    	//System.out.println(input);
        String[] command;
    	//input = kb.nextLine();
    	command = input.split(" ");
    	int length = command.length;
    	
    	switch(command[0]){
    	
        	case "show": {
        		if(length != 1) {
        			System.out.println("error processing: " + input);
        		}else Critter.displayWorld();
        		return null;
        		//break;
        	}
        	case "step": {
        		try{
        			if(length < 3) {
		        		//default number of steps is 1
		        		int num_step = 1;
		        		
		        		//if specify steps, change num_step to value specified
		        		if(length == 2) {
		        			num_step = Integer.valueOf(command[1]);
		        		}
		        		
		        		//loop number of times specified
		        		for(int a = 0; a < num_step; a++) {
		        			Critter.worldTimeStep();
		        		}
        			}
        		}
        		catch(NumberFormatException e){
        			System.out.println("error processing: " + input);
        		}
        		return null;
        		//break;
        	}
        	case "make": {
        		if(length > 1 && length < 4) {
        			try {
        				if(length > 2){
        					int val = 1;	//number of Critters of specified type to make, default 1
        					if(length == 3 && Integer.valueOf(command[2]) >=0) {
        						val = Integer.valueOf(command[2]);	//if non-number specified here, throws exception and breaks
        					} else {
        						throw new NumberFormatException();
        					}
        					for(int i = 0; i < val; i++)
        						Critter.makeCritter(command[1]);
        				}
        				else {
        					Critter.makeCritter(command[1]);
        				}
					} 
        			catch (InvalidCritterException | NumberFormatException e) {
		        		System.out.println("error processing: " + input);
					}
        		}
        		else {
        			System.out.println("error processing: " + input);
        		}
        		return null;
        		//break;
        	}
        	case "seed": {
        		if(length == 2) {
        			try {
        				Critter.setSeed(Long.valueOf(command[1]));
        			} catch (NumberFormatException e) {
        				System.out.println("error processing: " + input);
        			}
        		}
        		else {
        			System.out.println("error processing: " + input);
        		}
        		return null;
        		//break;
        	}
        	case "stats": {
        		if(length == 2) {
        			try {
        				List<Critter> critterList = Critter.getInstances(command[1]);
        				Class c = Class.forName(myPackage + "." +command[1]);
        				Method methods = c.getMethod("runStats", List.class);
        				return (String) methods.invoke(c.newInstance(), critterList);
					} catch (ClassNotFoundException | NoSuchMethodException | SecurityException |
							IllegalArgumentException | InvalidCritterException | IllegalAccessException | 
							InvocationTargetException | InstantiationException e) {
		        		System.out.println("error processing: " + input);
					}
        		}
        		else System.out.println("error processing: " + input);
        		
        		return null;
        		//break;
        	}
        	case "quit":{
        		if(length != 1)		//if any extraneous input is included with "quit" command, return error processing
        			System.out.println("error processing: " + input);
                return null;
            }
        	
        	default : {
        		//if invalid command (not one of the above) produce this output.
        		System.out.println("invalid command: " + input);
        		return null;
        	}
        }
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		double border = 10.0;
		int big_font = 40;
		int small_font = 20;
		
		primaryStage.setTitle("Project 5 Critters");
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        
        BorderPane enclosure = new BorderPane();
        ScrollPane controls = new ScrollPane();
        

		VBox container = new VBox();
		Button stats_btn = new Button();

		/*
		 * Header
		 */
		Label heading = new Label();
		heading.setText("Project 5 Critter World");
		heading.setFont(Font.font(big_font));
		
		StackPane header = new StackPane();
		header.getChildren().add(heading);
		StackPane.setAlignment(heading, Pos.CENTER);
		header.setMinWidth(500);

		
		/*
		 * Display
		 */
		Label display_world_text = new Label();
		display_world_text.setText("Display World");
		display_world_text.setFont(Font.font(small_font));
		
		Button draw = new Button();
		draw.setText("Draw");
		draw.setMinWidth(60);
		draw.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!running) {
					Critter.displayWorld();
				}
			}
	            
	    });
		
		Button clear = new Button();
		clear.setText("Clear");
		clear.setMinWidth(60);
		clear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(!running) {
					Critter.clearWorld();
					stats_btn.fire();
					Critter.displayWorld();
				}
			}
	            
	    });
		
		HBox display_pane = new HBox(8);
		display_pane.getChildren().addAll(draw, clear);
		
		AnchorPane display_world = new AnchorPane();
		display_world.getChildren().addAll(display_world_text, display_pane);
		AnchorPane.setLeftAnchor(display_world_text, border);
		AnchorPane.setRightAnchor(display_pane, border);
		//displayWorldStage();
		
		/*
		 * Animation
		 */
		Label animation_text = new Label();
		animation_text.setText("Animation");
		animation_text.setFont(Font.font(small_font));
		
		ComboBox<String> speed = new ComboBox<>();
		String[] speed_steps = {"1 fps", "2 fps", "5 fps", "10 fps", "20 fps", "50 fps", "100 fps", "500 fps"};
		speed.setPromptText("Timestep Rate");
		for(int a = 0; a < speed_steps.length; a++) {
			speed.getItems().add(speed_steps[a]);
		}
		
		Button play = new Button();

		play.setText("Start");
		play.setMinWidth(60);
		play.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//set global to false
				//running = false;
				if(!running) {
					play.setText("Stop");
				}else 
					play.setText("Start");
				
				
				Thread start_anim = new Thread(new Runnable() {

					@Override
					public void run() {
						int speed_choice = 1;
						try{
							speed_choice = Integer.valueOf(speed.getValue().split(" ")[0]);
						}catch (NullPointerException e) {
						}
						// TODO Auto-generated method stub
						if(!running) {
							running = true;
						}else running = false;
						
						while(running) {
							parse("step ");
							Critter.displayWorld();
							
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									stats_btn.fire();
								}
								
							});
							
							try {
								System.out.println("Sleep");
								Thread.sleep(1000/speed_choice);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
						}
						
					}
					
				});
				
				start_anim.start();
			}
	            
	    });
		
		HBox anim_pane = new HBox(8);
		anim_pane.getChildren().addAll(speed, play);

		
		AnchorPane animation = new AnchorPane();
		animation.getChildren().addAll(animation_text, anim_pane);
		AnchorPane.setLeftAnchor(animation_text, border);
		AnchorPane.setRightAnchor(anim_pane, border);
		
		
		/*
		 * Make Critters
		 */
		ArrayList<String> critters = new ArrayList<>();
		try{
			URL mainPath = Critter.class.getResource("Critter.class");
			System.out.println(mainPath.toString());
			File temp = new File(mainPath.getFile());
			System.out.println(temp.getParentFile().toString());
			File[] files = temp.getParentFile().listFiles();
			System.out.println(files.length);
			for (File file : files) {
				if (file.isFile()) {
					String class_name = file.getName().split("\\.")[0];
					
					Class critterClass = Class.forName(myPackage + "." + class_name);
					try {
						Critter c = (Critter) critterClass.newInstance();
						Class basic = Class.forName(myPackage + "." + "Critter");
						
						if(basic.isInstance(c)) {
							
							critters.add(class_name);
							
						}
					}catch (ClassCastException | InstantiationException e) {
						//not a critter
					}
			    
			    }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		TextField number_critter = new TextField();
		number_critter.setText("1");
		number_critter.setMaxWidth(50);
		
		ComboBox<String> box = new ComboBox<String>();
		box.setPromptText("Critters");
		for(int a = 0; a < critters.size(); a++) {
			box.getItems().add(critters.get(a));
		}
		
		Button add = new Button();
		add.setText("Add Critter(s)");
		add.setMinWidth(60);
		add.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(!running) {
					try {
						int compare = Integer.valueOf(number_critter.getText());
						if(compare > 1) {
							parse("make " + box.getValue() + " " + compare);
							Critter.displayWorld();
						}else if(compare == 1){
							parse("make " + box.getValue());
							Critter.displayWorld();
						}else {
							System.out.println("error processing: " + "make " + box.getValue() + " " + number_critter.getText());
						}
					}catch (NumberFormatException e) {
						System.out.println("error processing: " + "make " + box.getValue() + " " + number_critter.getText());
					}
				}
			}
	            
	    });
		
		Label add_critter = new Label();
		add_critter.setText("Add Critters");
		add_critter.setFont(Font.font(small_font));
		
		HBox critter_pane = new HBox(8);
		critter_pane.getChildren().addAll(box, number_critter, add);
		
		AnchorPane make_critters = new AnchorPane();
		make_critters.getChildren().addAll(add_critter, critter_pane);
		AnchorPane.setLeftAnchor(add_critter, border);
		AnchorPane.setRightAnchor(critter_pane, border);
		
		/*
		 * Display Stats
		 */
		AnchorPane display_stats = new AnchorPane();
		GridPane stats_gridpane = new GridPane();
		display_stats.getChildren().add(stats_gridpane);
		AnchorPane.setLeftAnchor(stats_gridpane, border);
		
		
		/*
		 * Stats
		 */
		Label stats_text = new Label();
		stats_text.setText("Stats");
		stats_text.setFont(Font.font(small_font));
		
		MenuButton m = new MenuButton("Critters");
		for(int a = 0; a < critters.size(); a++) {
			m.getItems().add(new CheckMenuItem(critters.get(a)));
		}

		
		stats_btn.setText("Run Stats");
		stats_btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
			
				int location = 0;
		    	stats_gridpane.getChildren().clear();
				try {
					for(int a = 0; a < m.getItems().size(); a++) {
						if(((CheckMenuItem) m.getItems().get(a)).isSelected()) {
							String return_Val = parse("stats " + critters.get(a));
							//System.out.println(return_Val);
							Text stats_text = new Text();
							stats_text.setWrappingWidth(primScreenBounds.getWidth()/3);
					    	stats_text.setText(return_Val);
					    	stats_text.setTextAlignment(TextAlignment.CENTER);
					    	//stats_text.setAlignment(Pos.CENTER);
					    	
					    	stats_gridpane.add(stats_text, 0, a);
						}
						location++;
					}
					
				}catch (NumberFormatException e) {
					System.out.println("error processing: " + "make " + critters.get(location));
				}
				
			}
	            
	    });
		
		
		
		
		HBox stats_pane = new HBox(8);
		stats_pane.getChildren().addAll(m, stats_btn);

		AnchorPane run_stats = new AnchorPane();
		run_stats.getChildren().addAll(stats_text, stats_pane);
		AnchorPane.setLeftAnchor(stats_text, border);
		AnchorPane.setRightAnchor(stats_pane, border);
		AnchorPane.setBottomAnchor(stats_gridpane, border);
		
		/*
		 * Timestep
		 */
		Label time_step_label = new Label();
		time_step_label.setText("Time Step");
		time_step_label.setFont(Font.font(small_font));
		
		TextField number_timesteps = new TextField();
		number_timesteps.setText("1");
		number_timesteps.setMaxWidth(50);
		
		Button do_step = new Button();
		do_step.setText("Step");
		do_step.setMinWidth(60);
		do_step.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(!running) {
					try {
						int compare = Integer.valueOf(number_timesteps.getText());
						System.out.println(compare);
						if(compare > 1) {
							parse("step " + " " + compare);
						}else if(compare == 1){
							parse("step");
						}else {
							System.out.println("error processing: step " + number_timesteps.getText());
						}
					}catch (NumberFormatException e) {
						System.out.println("error processing: step " + number_timesteps.getText());
					}
				}
			}
	            
	    });
		
		HBox time_step_pane = new HBox(8);
		time_step_pane.getChildren().addAll(number_timesteps, do_step);
		
		AnchorPane time_step = new AnchorPane();
		time_step.getChildren().addAll(time_step_label, time_step_pane);
		AnchorPane.setLeftAnchor(time_step_label, border);
		AnchorPane.setRightAnchor(time_step_pane, border);
		
		/*
		 * Seed
		 */
		Label seed_label = new Label();
		seed_label.setText("Seed");
		seed_label.setFont(Font.font(small_font));
		
		TextField seed_value = new TextField();
		seed_value.setText("1");
		seed_value.setMaxWidth(50);
		
		Button set_seed = new Button();
		set_seed.setText("Set Seed");
		set_seed.setMinWidth(60);
		set_seed.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(!running) {
					try {
						int compare = Integer.valueOf(seed_value.getText());
						if(compare > 1) {
							parse("seed " + compare);
						}else {
							System.out.println("error processing: seed");
						}
					}catch (NumberFormatException e) {
						System.out.println("error processing: seed " + seed_value.getText());
					}
				}
			}
	            
	    });
		
		HBox seed_pane = new HBox(8);
		seed_pane.getChildren().addAll(seed_value, set_seed);
		
		AnchorPane seed = new AnchorPane();
		seed.getChildren().addAll(seed_label, seed_pane);
		AnchorPane.setLeftAnchor(seed_label, border);
		AnchorPane.setRightAnchor(seed_pane, border);
		
		/*
		 * Quit
		 */
		StackPane quit = new StackPane();
		Label quit_label = new Label();
		quit_label.setFont(Font.font(big_font));
		quit_label.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//System.out.println("Test");
				System.exit(0);
			}
	            
	    });
		quit_label.setText("QUIT");
		quit.getChildren().add(quit_label);
		StackPane.setAlignment(quit_label, Pos.CENTER);

		Rectangle space = new Rectangle(20, primScreenBounds.getHeight()/25);
		space.setFill(Color.TRANSPARENT);
		Rectangle space1 = new Rectangle(20, primScreenBounds.getHeight()/15);
		space1.setFill(Color.TRANSPARENT);
		Rectangle space2 = new Rectangle(20, primScreenBounds.getHeight()/15);
		space2.setFill(Color.TRANSPARENT);
		Rectangle space3 = new Rectangle(20, primScreenBounds.getHeight()/15);
		space3.setFill(Color.TRANSPARENT);
		Rectangle space4 = new Rectangle(20, primScreenBounds.getHeight()/15);
		space4.setFill(Color.TRANSPARENT);
		Rectangle space5 = new Rectangle(20, primScreenBounds.getHeight()/15);
		space5.setFill(Color.TRANSPARENT);
		Rectangle space6 = new Rectangle(20, primScreenBounds.getHeight()/25);
		space6.setFill(Color.TRANSPARENT);
		Rectangle space7 = new Rectangle(20, primScreenBounds.getHeight()/25);
		space7.setFill(Color.TRANSPARENT);
		Rectangle space8 = new Rectangle(20, primScreenBounds.getHeight()/25);
		space8.setFill(Color.TRANSPARENT);
		Rectangle space9 = new Rectangle(20, primScreenBounds.getHeight()/25);
		space9.setFill(Color.TRANSPARENT);

		container.getChildren().add(space6);
		container.getChildren().add(header);
		container.getChildren().add(space);
		container.getChildren().add(display_world);
		container.getChildren().add(space1);
		container.getChildren().add(animation);
		container.getChildren().add(space9);
		container.getChildren().add(make_critters);
		container.getChildren().add(space2);
		container.getChildren().add(time_step);
		container.getChildren().add(space3);
		container.getChildren().add(run_stats);
		container.getChildren().add(space7);
		container.getChildren().add(display_stats);
		container.getChildren().add(space8);
		container.getChildren().add(seed);
		container.getChildren().add(space4);
		container.getChildren().add(quit);
		container.getChildren().add(space5);
		
		
		controls.setContent(container);
		enclosure.setLeft(controls);
		
		/*
		 * Display Grid
		 */
		/*
		 * Display Grid
		 */
		//placeholder
		grid = new Canvas(primScreenBounds.getWidth() - controls.getWidth(), primScreenBounds.getHeight());
		//grid.autosize();
		enclosure.setRight(grid);
		
		
		//add to enclosure
		Scene scene1 = new Scene(enclosure);
		primaryStage.setScene(scene1);
		primaryStage.setHeight(primScreenBounds.getHeight());
		primaryStage.setWidth(primScreenBounds.getWidth());
        primaryStage.setX(0.0);
        primaryStage.setY(0.0);
		primaryStage.show();
		
		grid.setWidth(primScreenBounds.getWidth() - controls.getWidth());
		grid.setHeight(primScreenBounds.getHeight());
		Critter.displayWorld();
		System.out.println(controls.getWidth() +" "+ grid.getWidth());
	}
	
	
}
