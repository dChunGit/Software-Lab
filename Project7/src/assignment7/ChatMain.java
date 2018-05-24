package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChatMain extends Application{

	private static BufferedReader reader;
	private static PrintWriter writer;
	private static Socket socket;
	private static TextArea input, text;
	private static String username, password, group_name = "", chat_user = "";
	private static String ip = "127.0.0.1";
	private static ArrayList<String> personal_data;
	private static ArrayList<String> passwords;
	private static ArrayList<String> chat_with_others;
	private static HBox online_box, offline_box, group_box;
	private static VBox threads_container, container;
	private static Label heading;
	private static Stage stage;
	private static StackPane overview;
	private static ScrollPane controls;
	private static Text wrong_info;
	private static TextField user_name, ipAddress;
	private static PasswordField password_field;

	
	public static void main(String[] args) {
    	try {
			new ChatMain().run(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
	}
	
	public void run(String[] args) throws Exception {
		//setUpDatabase();
		chat_with_others = new ArrayList<>();
		personal_data = new ArrayList<>();
		passwords = new ArrayList<>();
    	launch(args);
	}
	
	private void setUpChat(boolean toggle) throws Exception {
		if(toggle) {
			ip = "127.0.0.1";
			ChatMain.socket = new Socket(ip, 4242);
			InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
			ChatMain.reader = new BufferedReader(streamReader);
			ChatMain.writer = new PrintWriter(socket.getOutputStream());
	    	ChatMain.writer.println(username);
	    	ChatMain.writer.flush();
	    	ChatMain.writer.println(password);
	    	ChatMain.writer.flush();
	    	
	    	//ChatMain.writer.println("update");
	    	//ChatMain.writer.flush();
	
			////System.out.println("networking established");
			Thread readerThread = new Thread(new IncomingReader());
			readerThread.start();
		}else {
			if(ChatMain.socket!=null) {
				setStatus(false, username);
				//System.out.println("socket closed on: " + username);
				ChatMain.writer.println("signout");
				ChatMain.writer.flush();
				ChatMain.socket.close();
				ChatMain.reader.close();
				ChatMain.writer.close();
			}
		}
		//////System.out.println("Setup finished");
	}
	
	private class groupUsers implements EventHandler<Event> {
		private String name;
		private ArrayList<String> members;
		
		public groupUsers(ArrayList<String> members) { 
			this.name = members.get(0);
			this.members = parseMembers(members);
			//System.out.println("Group: " + this.members.toString());
		}
		
		private ArrayList<String> parseMembers(ArrayList<String> parse_array) {
			//System.out.println(parse_array.toString());
			String[] temp = parse_array.get(1).split(":");
			//System.out.println(Arrays.toString(temp));
			ArrayList<String> members_parsed = new ArrayList<>();
			
			for(int a = 0; a < temp.length; a++) {
				members_parsed.add(temp[a]);
			}
			
			return members_parsed;
		}
		
		@Override
		public void handle(Event event) {
			
			group_name = name;
			
			heading.setText("Chatting with: " + name);
			restoreGrouplog();
			chat_with_others = members;
			/*try {
				setUpChat(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}*/
			threads_container.setVisible(false);
			container.setVisible(true);
		}
		
	}
	
	private void updateUsers() {
		ArrayList<String> users = getNames(getDatabase());
		ArrayList<String> online_user_check = getOnline(true);
		ArrayList<String> offline_user_check = getOnline(false);
		ArrayList<ArrayList<String>> group_check = getGroups();
		//System.out.println("Group_Check in update: " + group_check.toString());
		Platform.runLater(new Runnable() {
			   @Override
			   public void run() {
				   online_box.getChildren().clear();
				   offline_box.getChildren().clear();
				   group_box.getChildren().clear();
			   }
		});
		
		for(int a = 0; a < group_check.size(); a++) {
			Button group_button = new Button();
			group_button.setText(group_check.get(a).get(0));
		
			
			/*if(group_check.get(a).contains(username)) {
				
			}*/
			groupUsers listener = new groupUsers(group_check.get(a));
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					group_box.getChildren().add(group_button);
					group_button.setOnMouseClicked(listener);
				}
			});
		}
		
		for(int a = 0; a < users.size(); a++) {
			/*Label user_icon = new Label(users.get(a).substring(0, 1));

			Text user_icon_name = new Text(users.get(a));
			GridPane user_icon_pane = new GridPane();
			user_icon_pane.add(user_icon, 0, 0);
			user_icon_pane.add(user_icon_name, 0, 1);*/
			Button user_button = new Button();
			user_button.setText(users.get(a));
			//////System.out.println(username);
			if(online_user_check.contains(users.get(a)) && !users.get(a).equals(username)) {
				Platform.runLater(new Runnable() {
					   @Override
					   public void run() {
						    online_box.getChildren().add(user_button);
							user_button.setOnMouseClicked(new EventHandler<Event>() {

								@Override
								public void handle(Event event) {
									//////System.out.println("Clicked");
									ChatMain.writer.println("update");
									ChatMain.writer.flush();
									heading.setText("Chatting with: " + user_button.getText());
									chat_user = user_button.getText();
									chat_with_others.add(user_button.getText());
									/*try {
										setUpChat(true);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										//e.printStackTrace();
									}*/
									if(group_name.equals("")) {
										restoreChat();
									}
									threads_container.setVisible(false);
									container.setVisible(true);
								}
								
							});
						}
					});
			}else if(offline_user_check.contains(users.get(a)) && !users.get(a).equals(username)) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						offline_box.getChildren().add(user_button);
						
					}
				});
			}
		}
	}
	
	class IncomingReader implements Runnable {
		public void run() {
			String message;
			//System.out.println("Read");
			try {
				while ((message = reader.readLine()) != null) {
					System.out.println(this.toString() + " " + message);
					if(message.equals("failure")) {
						try {
							setUpChat(false);
							wrong_info.setVisible(true);
							wrong_info.setText("Incorrect Username or Password");

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(message.equals("signout")) {
						updateUsers();
					}else if(message.contains("update")) {
						syncDatabase(message);
						Platform.runLater(new Runnable() {
							   @Override
							   public void run() {
									stage.setTitle(username);
									controls.setContent(
											overview);
									stage.sizeToScene();
							   }
						});
						updateUsers();
					}else {
						//updateChatlog(message);
						String sentfrom[] = message.split(":");
						//System.out.println(Arrays.toString(sentfrom));
						if(chat_with_others.contains(sentfrom[0]) || sentfrom[0].equals(username)) {
							//System.out.println("Updating: " + group_name);
							//group_name = "";
							if(!group_name.equals("")) {
								if(sentfrom.length == 3) {
									if(sentfrom[2].equals(group_name)) {
										//System.out.println("Correct");
										message = sentfrom[0] + ":" + sentfrom[1];
										updateGrouplog(message);
										restoreGrouplog();
									}
								}
							}else {
								if(sentfrom.length < 3) {
									//System.out.println("Incorrect");
									updateChatlog(message);
									restoreChat();
								}
							}
							//ChatMain.text.appendText(message + "\n");
						}
					}
				}
			} catch (IOException ex) {
				//ex.printStackTrace();
			}
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		int big_font = 32;
		stage = primaryStage;
		
		primaryStage.setTitle("Chat Client");
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        wrong_info = new Text();

		/*
		 * 
		 * Thread View Interface
		 * 
		 */
		Label online_users = new Label();
		online_users.setText("Who is online");
		Label offline_users = new Label();
		offline_users.setText("Who is offline");
		online_box = new HBox();
		offline_box = new HBox();
		Label groups = new Label();
		groups.setText("Groups");
		group_box = new HBox();
		
		Button new_group = new Button();
		new_group.setText("Start new group chat");
		new_group.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				ScrollPane container_group = new ScrollPane();
				Scene scene2 = new Scene(container_group);
	            Stage create_group = new Stage();
				TextField group_naming = new TextField();
				Button create = new Button();
				MenuButton m = new MenuButton("Select Members");
				ArrayList<String> names = getNames(getDatabase());
				for(int a = 0; a <names.size(); a++) {
					m.getItems().add(new CheckMenuItem(names.get(a)));
				}

				
				create.setText("Create Group");
				create.setOnMouseClicked(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						if(!group_naming.equals("")) {
						ArrayList<String> members = new ArrayList<>();
							for(int a = 0; a < m.getItems().size(); a++) {
								if(((CheckMenuItem) m.getItems().get(a)).isSelected()) {
									members.add(m.getItems().get(a).getText());
								}
							}
							addGroup(group_naming.getText(), members);
							create_group.close();
							ChatMain.writer.println("newGroup~" + group_naming.getText() + "&" + concatMembers(members));
							ChatMain.writer.flush();
						}
					}
				});
				
				GridPane group_overview = new GridPane();
				group_overview.add(group_naming, 0, 0);
				group_overview.add(create, 1, 0);
				group_overview.add(m, 2, 0);
				
				container_group.setContent(group_overview);
				
	            create_group.setScene(scene2);
	            create_group.show();

			}
			
		});
		
		threads_container = new VBox();
		threads_container.setPadding(new Insets(10, 20, 10, 10));
		
		Label user_signed = new Label();

		threads_container.getChildren().add(online_users);
		threads_container.getChildren().add(online_box);
		threads_container.getChildren().add(offline_users);
		threads_container.getChildren().add(offline_box);
		threads_container.getChildren().add(groups);
		threads_container.getChildren().add(group_box);
		threads_container.getChildren().add(new_group);
		threads_container.getChildren().add(user_signed);
		
        
        /*
         * 
         * Actual Chat Window Interface
         * 
         */
        controls = new ScrollPane();
		container = new VBox();
		BorderPane sign_in = new BorderPane();

		container.setPadding(new Insets(10, 20, 10, 10));

		heading = new Label();
		heading.setText("Project 7 Chat Client");
		heading.setFont(Font.font(big_font));
		
		StackPane header = new StackPane();
		header.getChildren().add(heading);
		StackPane.setAlignment(heading, Pos.CENTER);
		header.setMinWidth(primScreenBounds.getWidth()/3);
		
		text = new TextArea();
		text.setText("");
		text.setEditable(false);
		text.setMinHeight(primScreenBounds.getHeight()/3);
		text.setWrapText(true);
		text.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> ob, String o,
	                String n) {
	            // expand the textfield
	        	text.selectPositionCaret(text.getLength()); 
	    		text.deselect(); 
	        }
	    });
		
		header.getChildren().add(text);
		
		input = new TextArea();
		input.setWrapText(true);
		input.setPrefHeight(12);
		input.setMaxHeight(70);
		input.setPrefWidth(primScreenBounds.getWidth()/6);
		input.setText("");
		input.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> ob, String o,
	                String n) {
	            // expand the textfield
	        	input.setPrefHeight(TextUtils.computeTextHeight(input.getFont(),
	        			input.getText(), input.getPrefWidth()));
	        }
	    });

        sendMessage send_message = new sendMessage();
		input.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent ke) {
		        if (ke.getCode().equals(KeyCode.ENTER)) {
					//System.out.println("Send");
		        	String text = input.getText();
		            //////System.out.println(text);
		        	if(!(text.equals("") || text.equals(" ") || text.equals("	"))) {
			            send_message.set_Text(text);
			            //System.out.println("help " + group_name);
			            /*if(!group_name.equals("")) {
			            	updateGrouplog(text);
			            }*/
			            //updateChatlog(text);
			            Thread message = new Thread(send_message);
			            message.start();
			            
		        	}
		            input.clear();
		        	
		            ke.consume();
		        }
		    }
		});
		
		Button send = new Button();
		send.setText("Send");
		send.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				//System.out.println("Send");
				String text = input.getText();
	            //////System.out.println(text);
	        	if(!(text.equals("") || text.equals(" ") || text.equals("	"))) {
		            send_message.set_Text(text);
		            //System.out.println("help " + group_name);
		            /*if(!group_name.equals("")) {
		            	updateGrouplog(text);
		            }*/
		            //updateChatlog(text);
		            Thread message = new Thread(send_message);
		            message.start();
		            
	        	}
	            input.clear();
			}
			
		});

		BorderPane inputs = new BorderPane();
		inputs.setCenter(input);
		inputs.setRight(send);
		BorderPane.setMargin(send, new Insets(5, 0, 0, 5));
		inputs.setPadding(new Insets(20, 40, 40, 40));
		
		container.getChildren().add(header);
		container.getChildren().add(text);
		container.getChildren().add(inputs);
		/*header.setVisible(false);
		text.setVisible(false);
		inputs.setVisible(false);*/
		container.setVisible(false);
		
		Button visibility = new Button();
		visibility.setText("Sign Out");
		visibility.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				//////System.out.println(visibility.getText());
				//System.out.println("Send");
				try {
					setUpChat(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				//username = "";
				password = "";
				wrong_info.setVisible(false);
				controls.setContent(sign_in);
				primaryStage.sizeToScene();
			}
			
		});
		
		Button back_button = new Button();
		back_button.setText("Back");
		back_button.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				//////System.out.println(back_button.getText());
				if(back_button.getText().equals("Back")) {
					chat_with_others.clear();
					input.setText("");
					text.setText("");
					group_name = "";
					chat_user = "";
					container.setVisible(false);
					threads_container.setVisible(true);

			    	ChatMain.writer.println("update");
			    	ChatMain.writer.flush();
					//updateUsers();

					
				}
			}
			
		});
		
		container.getChildren().add(back_button);
		threads_container.getChildren().add(visibility);
		
		
		
		
		
		/*
		 * 
		 * Set outer stackpane for children
		 * 
		 */
		overview = new StackPane();
		overview.getChildren().add(container);
		overview.getChildren().add(threads_container);
		
	
		
		/*
		 * 
		 * User Sign In Interface
		 * 
		 */
		ipAddress = new TextField();
		ipAddress.setPromptText("Enter IP Address");
		user_name = new TextField();
		user_name.setPromptText("Username");
		password_field = new PasswordField();
		password_field.setPromptText("Password");
		wrong_info.setText("Incorrect Username or Password");
		wrong_info.setVisible(false);
		wrong_info.setFill(Color.RED);

		user_signed.setText(user_name.getText());
		
		Button new_user = new Button();
		new_user.setText("New User");
		new_user.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				try {
					ip = ipAddress.getText();
					username = user_name.getText();
					password = password_field.getText();
					password_field.setText("");
					////System.out.println("test");
					
					setUpChat(true);
					/*ip = ipAddress.getText();
					username = user_name.getText();
					password = password_field.getText();
					if(addNewUser()) {

						password_field.setText("");
						ArrayList<String> data = getNames(getDatabase());
						personal_data = data;
						passwords = getPasswords(getDatabase());
						
						heading.setText("Welcome: " + data.get(0));
						setUpChat(true);
						primaryStage.setTitle(username);

						//updateUsers();
						
						//setStatus(true, username);
						controls.setContent(overview);
						primaryStage.sizeToScene();
					}else {
						wrong_info.setText("Username already exists");
						////System.out.println("Duplicate Users");
					}*/
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
			}
			
		});
		
		Button done_info = new Button();
		done_info.setText("Sign In");
		done_info.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				////System.out.println(user_name.getText());
				////System.out.println(password_field.getText());
				
				try {
					ip = ipAddress.getText();
					username = user_name.getText();
					password = password_field.getText();
					password_field.setText("");
					////System.out.println("test");
					
					setUpChat(true);
					/*ArrayList<String> data = getNames(getDatabase());
					////System.out.println(data.toString());
					personal_data = data;
					passwords = getPasswords(getDatabase());
					ip = ipAddress.getText();
					int status_login = checkUsername(user_name.getText(), password_field.getText());
					if(status_login == 1) {
						password_field.setText("");
						//setStatus(true, username);
						setUpChat(true);
						primaryStage.setTitle(username);

						updateUsers();
						
						controls.setContent(overview);
						primaryStage.sizeToScene();
					}else if(status_login == 0){
						wrong_info.setVisible(true);
						wrong_info.setText("Incorrect Username or Password");
						////System.out.println("Wrong sign-in");
					}else {
						wrong_info.setVisible(true);
						wrong_info.setText("User already signed in");
					}*/
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			
		});
		
		Label welcome = new Label();
		welcome.setText("Project 7 Chat Client");
		welcome.setFont(Font.font(big_font));
		
		StackPane welcome_stack = new StackPane();
		welcome_stack.getChildren().add(welcome);
		StackPane.setAlignment(welcome, Pos.CENTER);
		
		GridPane options = new GridPane();
		options.add(done_info, 0, 0);
		options.add(new_user, 1, 0);
		GridPane.setMargin(done_info, new Insets(10, 10, 10, 10));
		GridPane.setMargin(new_user, new Insets(10, 10, 10, 10));
		options.setAlignment(Pos.CENTER);
		
		GridPane authenticate = new GridPane();
		authenticate.add(welcome_stack, 0, 0);
		authenticate.add(ipAddress, 0, 1);
		authenticate.add(user_name, 0, 2);
		authenticate.add(password_field, 0, 3);
		authenticate.add(wrong_info, 0, 4);
		authenticate.add(options, 0, 5);
		GridPane.setMargin(welcome_stack, new Insets(10, 20, 50, 20));
		GridPane.setMargin(ipAddress, new Insets(10, 20, 50, 20));
		GridPane.setMargin(user_name, new Insets(0, 20, 0, 20));
		GridPane.setMargin(password_field, new Insets(0, 20, 0, 20));
		GridPane.setMargin(wrong_info, new Insets(0, 20, 50, 20));
		GridPane.setMargin(options, new Insets(0, 20, 50, 20));
		GridPane.setHalignment(options, HPos.CENTER);
		GridPane.setHalignment(wrong_info, HPos.CENTER);
		BorderPane.setAlignment(authenticate, Pos.CENTER);
		sign_in.setCenter(authenticate);
		
		controls.setContent(sign_in);
		
		
		
		
		
		/*
		 * 
		 * Set Scene
		 * 
		 */
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent e) {
					
					try {
						setUpChat(false);
					} catch (Exception f) {
						// TODO Auto-generated catch block
						f.printStackTrace();
					}
				}
		});
		Scene scene1 = new Scene(controls);
		primaryStage.setScene(scene1);
		primaryStage.show();



	}
	
	private void syncDatabase(String message) {
		System.out.println("Syncing Database");
		restoreDatabase(message.split("~")[1]);
		//System.outprintln(database);
		ArrayList<String> data = getNames(getDatabase());
		////System.outprintln(data.toString());
		personal_data = data;
		passwords = getPasswords(getDatabase());
		ip = ipAddress.getText();		
		username = user_name.getText();
		password = password_field.getText();
		password_field.setText("");
	}
	
	private void restoreDatabase(String database) {
		//System.out.println("Whole Database:  " + database);
		dropTables();
		setUpDatabase();
		String[] parsed_database = database.split("&");
		System.out.println(Arrays.toString(parsed_database));
		if(parsed_database.length > 0) {
			String r_database = parsed_database[0];
			System.out.println(r_database);
			//parseConcatString(r_database);
			updateDatabase(parseConcatString(r_database));

		}
		if(parsed_database.length > 1) {
			String r_onlineoffline = parsed_database[1];
			System.out.println(r_onlineoffline);
			//parseConcatString(r_onlineoffline);
			updateOnline(parseConcatString(r_onlineoffline));

		}
		if(parsed_database.length > 2) {
			String r_groups = parsed_database[2];
			System.out.println("R_Group " + r_groups);
			
			updateGroups(parseConcatString(r_groups));

		}
		
	} 
	
	private void dropTables() {
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      c.setAutoCommit(false);
	      
	      String drop_table = "DROP TABLE IF EXISTS USER_CLIENT";
	      Statement statement = c.createStatement();
	      statement.execute(drop_table);
	      statement.close();
	      
	      drop_table = "DROP TABLE IF EXISTS GROUPS";
	      statement = c.createStatement();
	      statement.execute(drop_table);
	      statement.close();
	      
	      drop_table = "DROP TABLE IF EXISTS USERS";
	      statement = c.createStatement();
	      statement.execute(drop_table);
	      statement.close();
	      
	      c.commit();
	      c.close();
	      
	      }catch(Exception e) {
	    	  e.printStackTrace();
	      }
	}
	
	private void updateGroups(ArrayList<ArrayList<String>> information) {
		//INSERT INTO table (id, name, age) VALUES(1, "A", 19) ON DUPLICATE KEY UPDATE
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      c.setAutoCommit(false);
	      /*String drop_table = "DROP TABLE IF EXISTS USER_CLIENT";
	      Statement statement = c.createStatement();
	      statement.execute(drop_table);*/
	      

	      //stmt = c.createStatement();
	      for(int a = 0; a < information.size(); a++) {
	    	  ArrayList<String> user_info = information.get(a);
	    	  System.out.println("Restore group " + user_info.toString());
		      String sql = "INSERT INTO GROUPS (ID,USERS,CHATLOG) " +
		                   "VALUES (?, ?, ?);";
		  
		      PreparedStatement ps = c.prepareStatement(sql);
		      ps.setString(1, user_info.get(0));
		      //String concat_members = concatMembers(members);
		      ps.setString(2, user_info.get(1));
		      ps.setString(3, user_info.get(2));
		      
		      ps.executeUpdate();
		      
		      ps.close();
	      }

	      
	      //stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	    }
	    ////System.out.println("Records created successfully");
	}
	
	private void updateOnline(ArrayList<ArrayList<String>> information) {
		for(int a = 0; a < information.size(); a++) {
			ArrayList<String> temp = information.get(a);
			//System.out.println(temp.toString());
			if(temp.size() > 1) {
				boolean status = temp.get(1).equals("online");
				System.out.println(status);
				setStatus(status, temp.get(0));
			}
		}
	}
	
	private void updateDatabase(ArrayList<ArrayList<String>> information) {
		//INSERT INTO table (id, name, age) VALUES(1, "A", 19) ON DUPLICATE KEY UPDATE
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      c.setAutoCommit(false);
	      /*String drop_table = "DROP TABLE IF EXISTS USER_CLIENT";
	      Statement statement = c.createStatement();
	      statement.execute(drop_table);*/
	      

	      //stmt = c.createStatement();
	      for(int a = 0; a < information.size(); a++) {
	    	  ArrayList<String> user_info = information.get(a);
	    	  System.out.println("Database " + a + " " + user_info.size());
	    	  //System.out.println(user_info.toString());
		      String sql = "INSERT INTO USER_CLIENT (ID,PASSWORD,CHATS) " +
		                   "VALUES (?, ?, ?);";
		  
		      PreparedStatement ps = c.prepareStatement(sql);
		      ps.setString(1, user_info.get(0));
		      //String concat_members = concatMembers(members);
		      ps.setString(2, user_info.get(1));
		      String temp = user_info.get(2);
		      if(!user_info.get(2).equals("")) {
			      if(temp.split(":")[1].contains("BEGCHAT")) {
			    	  temp = temp.split(":")[0] + ":" + temp.split(":")[2];
			      }
		      }
		      ps.setString(3, temp);
		      
		      ps.executeUpdate();
		      
		      ps.close();
		      
		      sql = "INSERT INTO USERS (ID,ONLINE) " +
	                  "VALUES (?, ? );";
	 
		      ps = c.prepareStatement(sql);
		      ps.setString(1, user_info.get(0));
		      ps.setString(2, "offline");
		     
		      ps.executeUpdate();
		     
		      ps.close();
	      }

	      
	      //stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	    }
	    ////System.out.println("Records created successfully");
	}
	
	private ArrayList<ArrayList<String>> parseConcatString(String work) {
		ArrayList<ArrayList<String>> returned_array = new ArrayList<>();
		String[] outside = work.split("%");
		System.out.println("Parse Concat");
		System.out.println(work);
		System.out.println(Arrays.toString(outside));
		for(int a = 0; a < outside.length; a++) {
			ArrayList<String> temp = new ArrayList<>();
			String[] inside = outside[a].split("#");
			for(int b = 0; b < inside.length; b++) {
				if(!inside[b].equals("%")) {
					temp.add(inside[b]);
				}
			}
			if(inside.length < 3) {
				temp.add("");
			}
			
			System.out.println(temp.toString());
			returned_array.add(temp);
		}
		
		return returned_array;
	}
	
	private int checkUsername(String user, String pass) {
		//check username exists
		//check password
		ArrayList<String> online_users = getOnline(true);
		////System.out.println(personal_data.toString());
		////System.out.println(passwords.toString());
		if(personal_data.contains(user) && passwords.contains(pass) && !online_users.contains(user) ) {
			username = user;
			password = pass;
			return 1;
		}else if(online_users.contains(user)) {
			return -1;
		}
		//getDatabase();
		return 0;
	}
	
	private ArrayList<String> getOnline(boolean online_check) {
		Connection c = null;
	    Statement stmt = null;
        ArrayList<String> temp = new ArrayList<>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      c.setAutoCommit(false);
	      ////System.out.println("Opened database successfully - existing");
	      

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS;" );
	      while ( rs.next() ) {
	         String name = rs.getString("ID");
	         ////System.out.println(name);
	         String online_user = rs.getString("ONLINE");
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
	    	e.printStackTrace();
	    	////System.exit(0);
	    }
	    ////System.out.println("Operation done successfully - online users");
	    ////System.out.println(temp.toString());
	    return temp;
	}
	
	private boolean addNewUser() {
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      c.setAutoCommit(false);
	      ////System.out.println("Opened database successfully");
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
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      ////System.exit(0);
	    }
	    ////System.out.println("Records created successfully");
	    return true;
	}
	
	private void setStatus(boolean status, String user) {
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      c.setAutoCommit(false);
	      ////System.out.println("Opened database successfully");

	      String sql = "UPDATE USERS set ONLINE = ? where ID=?;";

	      PreparedStatement ps = c.prepareStatement(sql);
	      ps = c.prepareStatement(sql);
	      if(!status) {
	    	  ps.setString(1, "offline");
	      }else {
	    	  ps.setString(1, "online");
	      }
	      ps.setString(2, user);
	     
	      ps.executeUpdate();
	     
	      ps.close();

	      
	      //stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	      //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      ////System.exit(0);
	    }
	    ////System.out.println("Records created successfully");
	}
	
	private void restoreGrouplog() {
		//System.out.println("Restoring Group");
		ArrayList<String> chatlogs = retrieveGroupHistory();
		text.setText("");
		//System.out.println("Group history: " + chatlogs.toString());
		for(int a = 0; a < chatlogs.size(); a++) {
			text.appendText(chatlogs.get(a) + "\n");
			////System.out.println(chatlogs.get(a));
		}
	}
	
	private void updateGrouplog(String text) {
		//System.out.println("Grouplog: " + text);
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
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
	
	private ArrayList<String> retrieveGroupHistory() {
		//System.out.println("Retrieving group chat");
		ArrayList<String> database = getNames(getGroups());
	    ArrayList<String> chat_logs = new ArrayList<>();
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
	    	//System.out.println("Group Chats: " + chats);
		    String[] threads = chats.split("-");
	    	////System.out.println("Group Thread array: " + Arrays.toString(threads));
		    for(int b = 0; b < threads.length; b++) {
			    String messages = "";
		    	String temp = threads[b];
		    	////System.out.println("Parsed string: " + temp);
		    	String[] parser = temp.split("\\.");
		    	//System.out.println(Arrays.toString(parser));
		    	if(parser.length > 1) {
		    		
		    		if(parser.length > 2 && parser[1].equals(username)) {
		    			String parse_message = parser[2].split(":")[0];
		    			if(parse_message.equals(parser[1])) {
		    				messages = parser[2];
		    			}else {
		    				messages = parser[2];
		    			}
		    		}
		    	}
		    	////System.out.println("Parsed array: " + Arrays.toString(parser));
			    chat_logs.add(messages);
		    }
		}
	    
	    return chat_logs;
	}
	
	private void updateChatlog(String text) {
		Connection c = null;
	    //Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
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
	
	private void restoreChat() {
		ArrayList<String> chatlogs = retrieveHistory();
		text.setText("");
		for(int a = 0; a < chatlogs.size(); a++) {
			text.appendText(chatlogs.get(a) + "\n");
			////System.out.println(chatlogs.get(a));
		}
	}
	
	private ArrayList<String> retrieveHistory() {
		ArrayList<String> database = getNames(getDatabase());
	    ArrayList<String> chat_logs = new ArrayList<>();
		int a = 0;
		boolean found = false;
		////System.out.println(username);
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
	    	//System.out.println("Chats: " + chats);
		    String[] threads = chats.split("-");
	    	////System.out.println("Thread array: " + Arrays.toString(threads));
		    for(int b = 0; b < threads.length; b++) {
			    String messages = "";
		    	String temp = threads[b];
		    	////System.out.println("Parsed string: " + temp);
		    	String[] parser = temp.split("\\.");
		    	if(parser.length > 1) {
		    		
		    		if(parser.length > 2 && (parser[1].equals(chat_user) || parser[1].equals(username))) {
		    			System.out.println("Parser "+parser[2]);
		    			messages = parser[2];
		    		}
		    	}
		    	////System.out.println("Parsed array: " + Arrays.toString(parser));
			    chat_logs.add(messages);
		    }
		}
	    
	    return chat_logs;
	}
	
	private ArrayList<String> getNames(ArrayList<ArrayList<String>> parse_array) {
		ArrayList<String> names = new ArrayList<String>();
		for(int a = 0; a < parse_array.size(); a++) {
			////System.out.println(parse_array.get(a).toString());
			names.add(parse_array.get(a).get(0));
		}
		
		return names;
	}
	
	private ArrayList<String> getPasswords(ArrayList<ArrayList<String>> parse_array) {
		ArrayList<String> names = new ArrayList<String>();
		for(int a = 0; a < parse_array.size(); a++) {
			////System.out.println(parse_array.get(a).toString());
			names.add(parse_array.get(a).get(1));
		}
		
		return names;
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
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
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
	
	private ArrayList<ArrayList<String>> getGroups() {
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<ArrayList<String>> groups_data = new ArrayList<>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      c.setAutoCommit(false);
	      ////System.out.println("Opened database successfully - existing");
	      

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM GROUPS;" );
	      while(rs.next()) {
	          ArrayList<String> temp = new ArrayList<>();
	    	  String name = rs.getString("ID");
		      ////System.out.println(name);
	    	  String users = rs.getString("USERS");
		      ////System.out.println(users);
	    	  String chatlog = rs.getString("CHATLOG");
		      ////System.out.println(users);
	    	  temp.add(name);
	    	  temp.add(users);
	    	  temp.add(chatlog);
	    	  groups_data.add(temp);
	      } 
	      
	      rs.close();
	      stmt.close();
	      
	      
	      c.close();
	      
	    }catch (Exception e){
	    	
	    }
	    return groups_data;
	}
	
	private ArrayList<ArrayList<String>> getDatabase() {
		Connection c = null;
	    Statement stmt = null;
	    ArrayList<ArrayList<String>> users_data = new ArrayList<>();
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      c.setAutoCommit(false);
	      ////System.out.println("Opened database successfully - existing");
	      

	      stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM USER_CLIENT;" );
	      while ( rs.next() ) {
	         ArrayList<String> temp = new ArrayList<>();
	         String name = rs.getString("ID");
	         ////System.out.println(name);
	         String password = rs.getString("PASSWORD");
	         ////System.out.println(password);
	         String chats = rs.getString("CHATS");
	         ////System.out.println(chats);
	         
	         
	         temp.add(name);
	         temp.add(password);
	         temp.add(chats);
	         users_data.add(temp);
	      }
	      rs.close();
	      stmt.close();
	      
	      
	      c.close();
	      
	    } catch ( Exception e ) {
	    	e.printStackTrace();
	    }
	    ////System.out.println("Operation done successfully");
	    return users_data;
	}
	
	private void setUpDatabase() {
		Connection c = null;
		Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:"+username+".db");
	      
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
	
	class sendMessage implements Runnable {

	    private String text_field;
	    
	    public void set_Text(String text) {
	    	this.text_field = text;
	    }

	    public void run() {
	    	//System.out.println("Sending message: " + text_field);
	    	String message = "message&" + username+":";
			for(int a = 0; a < chat_with_others.size(); a++) {
				message += "BEGCHAT" + chat_with_others.get(a) + "ENDCHAT";
			}
			message += ":" + this.text_field;
			message += ":" + group_name;
	    	ChatMain.writer.println(message);
	    	ChatMain.writer.flush();
	    	
	    }
	}

	

}
