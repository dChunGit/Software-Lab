package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Observable;


public class ServerMain extends Observable {
	
	private static ArrayList<ClientHandler> users_active;

	public static void main(String[] args) {
		users_active = new ArrayList<>();
		setUpDatabase();
		System.out.println("Server started");
		try {
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void setUpDatabase() {
		Connection c = null;
		Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      
	      stmt = c.createStatement();
	      String sql = "CREATE TABLE IF NOT EXISTS USER_CLIENT " +
	                   "(ID TEXT PRIMARY KEY     NOT NULL," +
	                   " PASSWORD           TEXT    NOT NULL, " + 
	                   " CHATS        TEXT     NOT NULL);"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	      
	      
	      stmt = c.createStatement();
	      sql = "CREATE TABLE IF NOT EXISTS USERS " +
	    		"(ID TEXT PRIMARY KEY		NOT NULL," +
	    		" ONLINE			TEXT	NOT NULL);";
	      stmt.executeUpdate(sql);
	      stmt.close();
	      
	      stmt = c.createStatement();
	      sql = "CREATE TABLE IF NOT EXISTS GROUPS " +
	    		"(ID TEXT PRIMARY KEY		NOT NULL," +
	    		" USERS			TEXT	NOT NULL," +
	    		" CHATLOG		TEXT	NOT NULL);";
	      stmt.executeUpdate(sql);
	      stmt.close();
	      
	      c.close();
	      //System.out.println("Connection to SQLite has been established.");
	      
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	      //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      ////System.exit(0);
	    }
	}
	
	class update_runnable implements Runnable {
		@Override
		public void run() {
			while(true) {
				setChanged();
				notifyObservers("update");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		//update_runnable run_update = new update_runnable();
		
		/*Thread update_thread = new Thread(run_update);
		update_thread.start();*/
		
		while (true) {
			Socket clientSocket = serverSock.accept();
			String[] info = log_Next(clientSocket);

			String user = info[0];
			String password = info[1];
			////System.out.println("Observer User: "+user);
			ChatRoom_Observer writer = new ChatRoom_Observer(clientSocket.getOutputStream(), user);
			ClientHandler user_detected = new ClientHandler(clientSocket, user);
			users_active.add(user_detected);
			Thread t = new Thread(user_detected);
			t.start();
			this.addObserver(writer);
			
			if(checkUsername(user, password) == 1) {
				//////System.outprintln("Observer User: "+user);
				//System.out.println("User");
				users_active.add(user_detected);
				setStatus(true, user);
				setChanged();
				notifyObservers("update" + "~" + parseDatabase());
				
				System.out.println("got a connection");

				////System.outprintln("got a connection");
			}else if(checkUsername(user, password) == -2) {
				//System.out.println("New User");
				addNewUser(user, password);
				setStatus(true, user);

				setChanged();
				notifyObservers("update" + "~" + parseDatabase());
				System.out.println("got a connection");
				
			}else {
				System.out.println("Failed " + checkUsername(user, password));
				setChanged();
				notifyObservers("failure");
				
			}

		}
		
		
	}
	
	private int checkUsername(String user, String pass) {
		//check username exists
		//check password
		ArrayList<String> online_users = getOnline(true);
		//////System.outprintln(personal_data.toString());
		//////System.outprintln(passwords.toString());
		System.out.println("Online: " + online_users.toString());
		
		if(getNames(getDatabase()).contains(user) && getPasswords(getDatabase()).contains(pass) && !online_users.contains(user) ) {
			System.out.println("firstCheck");
			return 1;
		}else if(online_users.contains(user)) {
			System.out.println("secondCheck");
			return -1;
		}else if(!getNames(getDatabase()).contains(user)) {
			System.out.println("thirdCheck");
				return -2;
		}
		//getDatabase();
		return 0;
	}
	
	private ArrayList<String> getNames(ArrayList<ArrayList<String>> parse_array) {
		ArrayList<String> names = new ArrayList<String>();
		for(int a = 0; a < parse_array.size(); a++) {
			//////System.outprintln(parse_array.get(a).toString());
			names.add(parse_array.get(a).get(0));
		}
		
		return names;
	}
	
	private ArrayList<String> getPasswords(ArrayList<ArrayList<String>> parse_array) {
		ArrayList<String> names = new ArrayList<String>();
		for(int a = 0; a < parse_array.size(); a++) {
			//////System.outprintln(parse_array.get(a).toString());
			names.add(parse_array.get(a).get(1));
		}
		
		return names;
	}
	
	private String parseDatabase() {
		String users_data = concat_ArrayList2(getDatabase());
		
		String online_offline = concat_ArrayList2(getStatus());
		
		String groups = concat_ArrayList2(getGroups());
		
		return users_data + "&" + online_offline + "&" + groups;
		
	}
	
	/*private String concat_ArrayList3(ArrayList<ArrayList<String>> users_data) {
		String[] temp = new String[users_data.size()];
		for(int a = 0; a < users_data.size(); a++) {
			ArrayList<String> info = users_data.get(a);
			
		}
	}*/
	
	private String concat_ArrayList2(ArrayList<ArrayList<String>> users_data) {
		String[] temp = new String[users_data.size()];
		for(int a = 0; a < users_data.size(); a++) {
			ArrayList<String> user = users_data.get(a);
			String temp_data = "";
			for(int b = 0; b < user.size(); b++) {
				String input = user.get(b);
				if(input.contains("BEGCHAT")) {
					input = input.split(":")[0] + ":" + input.split(":")[2] + "-";
				}
				//System.out.println("Input " +input);
				temp_data += input + "#";
			}
			temp[a] = temp_data;
		}
		String concat = "";
		for(int a = 0; a < temp.length; a++) {
			concat += temp[a] + "%";
		}
		return concat;
	}
	
	private boolean addNewUser(String username, String password) {
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      //////System.outprintln("Opened database successfully");
	      ArrayList<String> current_users = getNames(getDatabase());
	      if(current_users.contains(username)) {
	    	  return false;
	      }

	      //stmt = c.createStatement();
	      String sql = "INSERT INTO USER_CLIENT (ID,PASSWORD,CHATS) " +
	                   "VALUES (?, ?, ? );";
	  
	      PreparedStatement ps = c.prepareStatement(sql);
	      ps.setString(1, username);
	      ps.setString(2, password);
	      ps.setString(3, "");
	      
	      ps.executeUpdate();
	      
	      ps.close();
	      
	      sql = "INSERT INTO USERS (ID,ONLINE) " +
                  "VALUES (?, ? );";
 
	      ps = c.prepareStatement(sql);
	      ps.setString(1, username);
	      ps.setString(2, "online");
	     
	      ps.executeUpdate();
	     
	      ps.close();

	      
	      //stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	      //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      ////System.exit(0);
	    }
	    //////System.outprintln("Records created successfully");
	    return true;
	}
	
	private void setStatus(boolean status, String username) {
		////System.out.println(i++ + " " + status + username);
		
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      //////System.outprintln("Opened database successfully");

	      String sql = "UPDATE USERS set ONLINE = ? where ID=?;";

	      PreparedStatement ps = c.prepareStatement(sql);
	      ps = c.prepareStatement(sql);
	      if(!status) {
	    	  ps.setString(1, "offline");
	      }else {
	    	  ps.setString(1, "online");
	      }
	      ps.setString(2, username);
	     
	      ps.executeUpdate();
	     
	      ps.close();

	      
	      //stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	      ////System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      //////System.exit(0);
	    }
	    //////System.outprintln("Records created successfully");
	}
	
	private ArrayList<ArrayList<String>> getStatus() {
		Connection c = null;
	    Statement stmt = null;
        ArrayList<ArrayList<String>> users_status = new ArrayList<>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      //////System.outprintln("Opened database successfully - existing");
	      

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS;" );
	      while ( rs.next() ) {
	    	  ArrayList<String> temp = new ArrayList<>();
	         String name = rs.getString("ID");
	         //////System.outprintln(name);
	         String online_user = rs.getString("ONLINE");
	         ////System.out.println(name + " " +online_user);
	         temp.add(name);
	         temp.add(online_user);
	         users_status.add(temp);
	      }
	      rs.close();
	      stmt.close();
	      
	      
	      c.close();
	      
	    } catch ( Exception e ) {
	    	//e.printStackTrace();
	    	////System.exit(0);
	    }
	    //////System.outprintln("Operation done successfully - online users");
	    //////System.outprintln(temp.toString());
	    return users_status;
	}
	
	private ArrayList<String> getOnline(boolean online_check) {
		Connection c = null;
	    Statement stmt = null;
        ArrayList<String> temp = new ArrayList<>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      //////System.outprintln("Opened database successfully - existing");
	      

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS;" );
	      while ( rs.next() ) {
	         String name = rs.getString("ID");
	         //////System.outprintln(name);
	         String online_user = rs.getString("ONLINE");
	         ////System.out.println(name + " " +online_user);
	         String compare = "online";
	         if(!online_check) {
	        	 compare = "offline";
	         }
	         if(online_user.equals(compare)) {
	        	 temp.add(name);
	         }
	      }
	      rs.close();
	      stmt.close();
	      
	      
	      c.close();
	      
	    } catch ( Exception e ) {
	    	//e.printStackTrace();
	    	////System.exit(0);
	    }
	    //////System.outprintln("Operation done successfully - online users");
	    //////System.outprintln(temp.toString());
	    return temp;
	}
	
	private ArrayList<ArrayList<String>> getDatabase() {
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<ArrayList<String>> users_data = new ArrayList<>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      //////System.outprintln("Opened database successfully - existing");
	      

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM USER_CLIENT;" );
	      while ( rs.next() ) {
	         ArrayList<String> temp = new ArrayList<>();
	         String name = rs.getString("ID");
	         //////System.outprintln(name);
	         String password = rs.getString("PASSWORD");
	         //////System.outprintln(password);
	         String chats = rs.getString("CHATS");
	         ////System.out.println("Chats: " + chats);
	         
	         
	         temp.add(name);
	         temp.add(password);
	         temp.add(chats);
	         users_data.add(temp);
	      }
	      rs.close();
	      stmt.close();
	      
	      
	      c.close();
	      
	    } catch ( Exception e ) {
	    	//e.printStackTrace();
	    	////System.exit(0);
	    }
	    //////System.outprintln("Operation done successfully");
	    ////System.out.println(users_data.toString());
	    return users_data;
	}
	
	private String concatMembers(ArrayList<String> con) {
		String message = "";
		for(int a = 0; a < con.size(); a++) {
			message += con.get(a) + ":";
		}
		
		return message;
	}
	
	private boolean addGroup(String name, ArrayList<String> members) {
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      ////System.out.println("Opened database successfully");
	      ArrayList<String> current_users = getNames(getGroups());
	      if(current_users.contains(name)) {
	    	  return false;
	      }

	      //stmt = c.createStatement();
	      String sql = "INSERT INTO GROUPS (ID,USERS,CHATLOG) " +
	                   "VALUES (?, ?, ?);";
	  
	      PreparedStatement ps = c.prepareStatement(sql);
	      ps.setString(1, name);
	      String concat_members = concatMembers(members);
	      ps.setString(2, concat_members);
	      ps.setString(3, "");
	      
	      ps.executeUpdate();
	      
	      ps.close();

	      
	      //stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      //System.exit(0);
	    }
	    ////System.out.println("Records created successfully");
	    return true;
	}
	
	private void updateGrouplog(String text, String username, ArrayList<String> chat_with_others, String group_name) {
		//System.out.println("Grouplog: " + text);
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      ////System.out.println("Opened database successfully");
	      ArrayList<String> database = getNames(getGroups());

	      String sql = "UPDATE GROUPS set CHATLOG = ? where ID=?;";

	      PreparedStatement ps = c.prepareStatement(sql);
	      ps = c.prepareStatement(sql);
	      
	      int a = 0;
	      boolean found = false;
	      while(a < database.size() && !found) {
	    	  if(database.get(a).equals(group_name)) {
	    		  found = true;
	    	  }else {
	    		  a++;
	    	  }
	      }
	      if(found) {
		      ArrayList<String> chatlog = getGroups().get(a);
		      String chats = chatlog.get(2);
		      for(int b = 0; b < chat_with_others.size(); b++) {
				  chats += "USERID."+chat_with_others.get(b)+"."+text+"-";
			  }
		      //System.out.println("Group Chats saved: "+chats);
		      
	    	  ps.setString(1, chats);
		      ps.setString(2, group_name);
		     
		      ps.executeUpdate();
	      }
	     
	      ps.close();

	      
	      //stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	      //System.exit(0);
	    }
	}

	private ArrayList<ArrayList<String>> getGroups() {
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<ArrayList<String>> groups_data = new ArrayList<>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      //////System.outprintln("Opened database successfully - existing");
	      

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM GROUPS;" );
	      while(rs.next()) {
	          ArrayList<String> temp = new ArrayList<>();
	    	  String name = rs.getString("ID");
		      //////System.outprintln(name);
	    	  String users = rs.getString("USERS");
		      //////System.outprintln(users);
	    	  String chatlog = rs.getString("CHATLOG");
		      //////System.outprintln(users);
	    	  temp.add(name);
	    	  temp.add(users);
	    	  temp.add(chatlog);
	    	  System.out.println("Groups " + temp.toString());
	    	  groups_data.add(temp);
	      } 
	      
	      rs.close();
	      stmt.close();
	      
	      
	      c.close();
	      
	    }catch (Exception e){
	    	
	    }
	    return groups_data;
	}
	
	private String[] log_Next(Socket client) {
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String[] data = new String[2];
			data[0] = input.readLine();
			data[1] = input.readLine();
	        return data;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	private ArrayList<String> parse(String arg) {
		String message = arg;
		ArrayList<String> temp = new ArrayList<>();
		int a = 0;
		while(a + 6 < message.length()) {
			String checkbeing = message.substring(a, a + 7);
			//System.out.println("Checkbeing: " + checkbeing);
			if(checkbeing.equals("BEGCHAT")) {
				int ending = a;
				String endcheck = checkbeing;
				while(!endcheck.equals("ENDCHAT") && (ending+7) < message.length()) {
					//System.out.println("Endcheck: " + endcheck);
					ending++;
					endcheck = message.substring(ending, ending + 7);
				}
				temp.add(message.substring(a + 7, ending));
			}
			a++;
		}
		
		return temp;
	}
	
	private void updateChatlog(String text, String username, ArrayList<String> chat_with_others) {
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:server.db");
	      c.setAutoCommit(false);
	      ////System.out.println("Opened database successfully");
	      ArrayList<String> database = getNames(getDatabase());

	      String sql = "UPDATE USER_CLIENT set CHATS = ? where ID=?;";

	      
	      int a = 0;
	      boolean found = false;
	      while(a < database.size() && !found) {
	    	  if(database.get(a).equals(username)) {
	    		  found = true;
	    	  }else {
	    		  a++;
	    	  }
	      }
	      if(found) {
		      ArrayList<String> chatlog = getDatabase().get(a);
		      String chats = chatlog.get(2);
		      
		      for(int b = 0; b < chat_with_others.size(); b++) {
		    	  //String temp = username + ":" + text.split(":")[1];
		    	  if(!chat_with_others.get(b).equals(username)) {
					  chats += "USERID."+chat_with_others.get(b)+"."+text+"-";
		    	  }
			  }

		      //System.out.println("Chat logs: " +chats);

		      PreparedStatement ps = c.prepareStatement(sql);
		      ps = c.prepareStatement(sql);
		      
	    	  ps.setString(1, chats);
		      ps.setString(2, username);
		     
		      ps.executeUpdate();
		      ps.close();
		      
		      for(int b = 0; b < chat_with_others.size(); b++) {

			      //System.out.println("Chat logs: " +chats);

			      ps = c.prepareStatement(sql);
			      ps = c.prepareStatement(sql);
			      
		    	  ps.setString(1, chats);
			      ps.setString(2, chat_with_others.get(b));
			     
			      ps.executeUpdate();
			      ps.close();
		      }
	      }
	     

	      
	      //stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	      //System.exit(0);
	    }
	    ////System.out.println("Records created successfully");
	}
	
	private ArrayList<String> parseArrayList(String data) {
		String[] temp = data.split(":");
		ArrayList<String> alist = new ArrayList<>();
		for(int a = 0; a < temp.length; a++) {
			alist.add(temp[a]);
		}
		
		return alist;
	}
	
	class ClientHandler implements Runnable {
		private BufferedReader reader;
		private String username;

		public ClientHandler(Socket clientSocket, String name) {
			username = name;
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println("server read "+message);
					if(message.contains("newGroup")) {
						String group_data = message.split("~")[1];
						addGroup(group_data.split("&")[0], parseArrayList(group_data.split("&")[1]));
						
						setChanged();
						notifyObservers("update" + "~" + parseDatabase());
						
					}else if(message.equals("update")) {
						System.out.println("update" + "~" + parseDatabase());
						setChanged();
						notifyObservers("update" + "~" + parseDatabase());

					}else if(message.equals("signout")) {
						setStatus(false, username);
						setChanged();
						notifyObservers("update" + "~" + parseDatabase());

					}else if(message.split("&")[0].equals("message")) {
						String save_message = message.split("&")[1];
						System.out.println("Username: " + save_message);
						String users = save_message.split(":")[1];
						if(save_message.split(":").length < 4) {
							System.out.println("Updating chatlog");
							updateChatlog(save_message, username, parse(users));
						}else {
							System.out.println("Updating grouplog " + save_message.split(":")[3] + " " + save_message.split(":")[2]);
							updateGrouplog(save_message.split(":")[2], username, parse(users), save_message.split(":")[3]);
						}

						setChanged();
						notifyObservers(save_message);

					}else {
						setChanged();
						notifyObservers(message);
					}
					
					
				}
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
}
