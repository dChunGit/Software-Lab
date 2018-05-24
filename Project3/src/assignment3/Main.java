/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * <David Chun>
 * <dc37875>
 * <16239>
 * <Robin Dhakal>
 * <rd27955>
 * <16239>
 * Slip days used: <0>
 * Git URL: https://github.com/dChunGit/EE422C_Project3.git
 * Spring 2017
 */


package assignment3;
import java.util.*;
import java.io.*;

public class Main {
	
	static final char[] alphabet = {'a','b','c','d','e','f','g','h',
			'i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	static Set<String> dictionary;
	static ArrayList<String> inputs;
	static ArrayList<String> dfs_ladder;
	static HashMap<String, String> dfs_visited;
	
	/*
	 * main sets scanners and initializes variables, testing main
	 * 
	 * @param args[] input arguments
	 * @return void
	 */
	public static void main(String[] args) throws Exception {
		
		Scanner kb;	// input Scanner for commands
		PrintStream ps;	// output file
		// If arguments are specified, read/write from/to files instead of Std IO.
		if (args.length != 0) {
			kb = new Scanner(new File(args[0]));
			ps = new PrintStream(new File(args[1]));
			System.setOut(ps);			// redirect output to ps
		} else {
			kb = new Scanner(System.in);// default from Stdin
			ps = System.out;			// default to Stdout
		}
		initialize();
		boolean quit = false;
	
		while(!quit) {
			inputs = parse(kb);
			if(inputs.size() == 0) {
				quit = true;
			}else {
				if(inputs != null) {
					//printLadder(getWordLadderBFS(inputs.get(0), inputs.get(1)));
					printLadder(getWordLadderDFS(inputs.get(0), inputs.get(1)));
				}
			}
		}
		
	}
	
	/*
	 * initialize set up dictionary
	 * 
	 * @param void
	 * @return void
	 */
	public static void initialize() {
		//initialize the dictionary
		dictionary = makeDictionary();
		
	}
	
	/**
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of 2 Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		// TO DO
		ArrayList<String> word = new ArrayList<>();
		String first = keyboard.next();
		if(first.equals("/quit")) {
			return word;
		}
		String second = keyboard.next();
		if(second.equals("/quit")) {
			return word;
		}
		word.add(first);
		word.add(second);
		
		return word;
	}
	
	/*
	 * getWordLadderDFS searches for word ladder from start to end strings using depth first search
	 * 
	 * @param start string to start at
	 * @param end string to end at
	 * @return ArrayList ladder from start to end
	 */
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		//variables to track
		dfs_ladder = new ArrayList<>();
		dfs_visited = new HashMap<>();
		
		//if no inputs, create inputs list
		if(inputs == null) {
    		inputs = new ArrayList<>();
    	}
    	//save input values in inputs
    	inputs.add(0, start);
    	inputs.add(1, end);
    	
    	//check for quit
		if(start.equalsIgnoreCase("/quit") || end.equalsIgnoreCase("/quit")) {
			return new ArrayList<>();
		}
		
		//check for identical inputs
		if(start.equals(end)) {
			dfs_ladder.add(start);
			dfs_ladder.add(end);
			return dfs_ladder;
		}
		
    	//set to uppercase for comparison
		start = start.toUpperCase();
		end = end.toUpperCase();
		
		if(find(start, end)) {
			//if word ladder was found, add start value and reverse ArrayList
			dfs_visited.put(start,null);
			ArrayList<String> rungs = new ArrayList<>();

			String temp = end;
			//add to beginning of ladder
			rungs.add(0, temp.toLowerCase());
			//while not reached start value
			while(!temp.equals(start)) {
				//get parent of current value
				String prev = dfs_visited.get(temp);
				//append to ladder
				rungs.add(0, prev.toLowerCase());
				//set temp to parent value
				temp = prev;
			}
			return rungs;
			
		}else {
			//if no word ladder, add start and end values
			dfs_ladder.add(start);
			dfs_ladder.add(end);
			return dfs_ladder;
		}
		
	}

	/*
	 * find recursive function to find word ladder between 2 words
	 * 
	 * @param word starting string
	 * @param end string to end at
	 * @return boolean true if found false otherwise
	 */
	private static boolean find(String word, String end) {
		//base case if word not available
		if(word == null) {
			return false;
		}
		//add to visited set
		dfs_visited.put(word, null);
		//if value return true
		if(word.equals(end)) {
			return true;
		}else {
			//get neighbors and optimize order
			ArrayList<String> neighbors = rankNeighbors(getNeighbors(word));
			
			//loop through neighbors
			for(int a = 0; a < neighbors.size(); a++) {
				//if not visited
				if(!dfs_visited.containsKey(neighbors.get(a))) {
					//check recursively
					boolean found = find(neighbors.get(a), end);
					//if found, add to ladders
					if(found) {
						dfs_visited.put(neighbors.get(a), word);

						return true;
					}
				}
			}
			//not found
			return false;
		}
		
		
	}
	
	/*
	 * rankNeighbors ranks neighbors by number of characters similar to end and returns a sorted ArrayList based on ranking
	 * 
	 * @param neighbors ArrayList of neighbors
	 * @return ArrayList neighbors ranked from most likely to least likely
	 */
	private static ArrayList<String> rankNeighbors(ArrayList<String> n) {
		//array to hold ranking values
		int[] ranking = new int[n.size()];
		//loop through neighbors
		for(int a = 0; a < n.size(); a++) {
			//loop through end characters
			for(int b = 0; b < inputs.get(1).length(); b++) {
				//if equivalent characters
				if(inputs.get(1).substring(b, b+1).equalsIgnoreCase(n.get(a).substring(b, b+1))) {
					//add to ranking at location
					ranking[a]++;
				}
			}
		}
		//call sort and return sorted ArrayList
		return sortNeighbors(ranking, n);
	}
	
	/*
	 * sortNeighbors sorts neighbors ArrayList 
	 * 
	 * @param ranking Integer array of rankings for each word
	 * @param n ArrayList of neighbors to rank
	 * @return ArrayList ranked neighbors in sorted order
	 */
	private static ArrayList<String> sortNeighbors(int[] ranking, ArrayList<String> n) {
		//sorted neighbors and looked flags
		ArrayList<String> r_neighbors = new ArrayList<>();
		
		//while not added all neighbors
		while(r_neighbors.size() != n.size()) {
			//track max and index
			int current_max = 0, current_index = 0;
			//find max not added index
			for(int b = 0; b < ranking.length; b++) {
				//if not looked and greater than or equal to max
				if(ranking[b] >= current_max) {
					//update current index and max
					current_max = ranking[b];
					current_index = b;
				}
			}
			//add to ArrayList
			r_neighbors.add(n.get(current_index));
			//set to looked
			ranking[current_index] = -1;
			
		}
		//return sorted ArrayList
		return r_neighbors;
	}
	
	/*
	 * getWordLadderBFS searches for word ladder from start to end strings using breadth first search
	 * 
	 * @param start string to start at
	 * @param end string to end at
	 * @return ArrayList ladder from start to end
	 */
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
    	//if not yet initialized, initialize inputs
    	if(inputs == null) {
    		inputs = new ArrayList<>();
    	}
    	//save input values in inputs
    	inputs.add(0, start);
    	inputs.add(1, end);
    	
    	//set to uppercase for comparison
		start = start.toUpperCase();
		end = end.toUpperCase();
		
    	//temp variables to store visited, parents, and neighbors
		HashMap<String, String> visited = new HashMap<>();
		ArrayList<String> neighbors;
		boolean found = false;
		//ArrayLists for correct ladder
		ArrayList<String> rungs = new ArrayList<>();
		//queue to store list of nodes to visit
		Queue<String> nodes = new LinkedList<>();
		
		//check for quit
		if(start.equalsIgnoreCase("/quit") || end.equalsIgnoreCase("/quit")) {
			return new ArrayList<>();
		}
		
		//check for identical inputs
		if(start.equals(end)) {
			rungs.add(start);
			rungs.add(end);
			return rungs;
		}
		
		//first node
		visited.put(start, null);
		nodes.add(start);
		//loop while nodes to look at and not found
		while(!nodes.isEmpty()) {
			//pop from queue
			String head = nodes.poll();
			//if head a value and equal to end, set found true
			if(head != null) {
				if(head.equals(end)) {
					found = true;
				}
			}
			//if not found yet
			if(!found) {
				//get neighbors of current node
				neighbors = getNeighbors(head);
				//search for not visited neighbors
				for(int a = 0; a < neighbors.size(); a++) {					
					if(!visited.containsKey(neighbors.get(a))) {
						//set as visited and add parent
						if(head != null) {
							visited.put(neighbors.get(a), head);
						}
						nodes.add(neighbors.get(a));
					}
					
				}
			}
		}
		
    	//if word was found
		if(found) {
			//set temp string to last value
			String temp = end;
			//add to beginning of ladder
			rungs.add(0, temp.toLowerCase());
			//while not reached start value
			while(!temp.equals(start)) {
				//get parent of current value
				String prev = visited.get(temp);
				//append to ladder
				rungs.add(0, prev.toLowerCase());
				//set temp to parent value
				temp = prev;
			}
		}else {
			//add start and end values
			rungs.add(start);
			rungs.add(end);
		}
		
		return rungs;
	}

	/*
	 * getNeighbors searches for valid neighbors in dictionary 1 letter different from current word
	 * 
	 * @param current_Word word to find neighbors of
	 * @return ArrayList list of valid neighbors of current_Word
	 */
    private static ArrayList<String> getNeighbors(String current_Word) {
    	//get alphabet, try each letter for each character, check against dictionary
    	ArrayList<String> neighbors = new ArrayList<>();
    	
    	//loop through characters of word
    	for(int a = 0; a < current_Word.length(); a++) {
    		//loop through alphabet
    		for(int b = 0; b < alphabet.length; b++) {
    			//concatenate word with current alphabet character
    			String temp = (current_Word.substring(0, a) + alphabet[b] + 
    					current_Word.substring(a+1, current_Word.length())).toUpperCase();
    			
    			//if valid word, not already marked, and not the same as current word
    			if(dictionary.contains(temp) && !temp.equals(current_Word) && !neighbors.contains(temp)) {
    				//add to neighbors list
    				neighbors.add(temp);
    			}
    		}
    	}
    	 	    	
    	return neighbors;
    }

	/*
	 * makeDictionary reads dictionary from file and stores in set
	 * 
	 * @return Set words in dictionary
	 */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			//infile = new Scanner (new File("short_dict.txt"));
			infile = new Scanner (new File("five_letter_words.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary File not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		while (infile.hasNext()) {
			words.add(infile.next().toUpperCase());
		}
		return words;
	}

	/*
	 * printLadder prints results from ladder
	 * 
	 * @param ladder ArrayList of search ladder results
	 * @return void
	 */
	public static void printLadder(ArrayList<String> ladder) {
		//if ladder is valid
		if(ladder != null) {

			if(ladder.size() > 2) {
				//if ladder contains more than just start and end values
				
				System.out.println("a " + (ladder.size() - 2) + "-rung word ladder exists between " 
						+ inputs.get(0) + " and " + inputs.get(1) + ".");
				
				//print contents of ladder
				for(int a = 0; a < ladder.size(); a++) {
					System.out.println(ladder.get(a));
				}
				
			}else if(ladder.size() == 2) {
				//if ladder only has start and end values
				
				System.out.println("no word ladder can be found between " 
						+ inputs.get(0) + " and " + inputs.get(1) + ".");
			}
			
		}
		
		
	}
}
