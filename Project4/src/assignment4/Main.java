package assignment4;
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

import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main {

    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    public static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    private static boolean DEBUG = false; // Use it or not, as you wish!
    static PrintStream old = System.out;	// if you want to restore output to console


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
        String input;
        String[] command;
        do{
        	input = kb.nextLine();
        	command = input.split(" ");
        	int length = command.length;
        	
        	switch(command[0]){
        	
	        	case "show": {
	        		if(length != 1) {
	        			System.out.println("error processing: " + input);
	        		}else Critter.displayWorld();
	        		break;
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
	        		
	        		break;
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
	        		
	        		break;
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
	        		
	        		break;
	        	}
	        	case "stats": {
	        		if(length == 2) {
	        			try {
	        				List<Critter> critterList = Critter.getInstances(command[1]);
	        				Class c = Class.forName(myPackage + "." +command[1]);
	        				Method methods = c.getMethod("runStats", List.class);
	        				methods.invoke(c.newInstance(), critterList);
						} catch (ClassNotFoundException | NoSuchMethodException | SecurityException |
								IllegalArgumentException | InvalidCritterException | IllegalAccessException | 
								InvocationTargetException | InstantiationException e) {
			        		System.out.println("error processing: " + input);
						}
	        		}
	        		else System.out.println("error processing: " + input);
	        		break;
	        	}
	        	case "quit":{
	        		if(length != 1)		//if any extraneous input is included with "quit" command, return error processing
	        			System.out.println("error processing: " + input);
                    break;
                }
	        	
	        	default : {
	        		//if invalid command (not one of the above) produce this output.
	        		System.out.println("invalid command: " + input);
	        	}
        	}
        }while(!input.equals("quit"));
        
        /* Write your code above */
        System.out.flush();

    }
}
