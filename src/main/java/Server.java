import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


/*
Server class
 */
public class Server{

	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<String> friends = new ArrayList<>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	/*
	Default constructor
	 */
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}


	/*
	Server thread class
	 */
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept());
				clients.add(c);
				c.start();
			    }
			}//end of try
				catch(Exception e) {
					Message callMsg = new Message("","",
							"--- Server socket did not launch ---", "");
					callback.accept(callMsg);
				}
			}//end of while
		}
	

		/*
		Client thread class
		 */
		class ClientThread extends Thread{

		
			Socket connection;
			ObjectInputStream in;
			ObjectOutputStream out;
			String clientThreadName;
			boolean online = false;

			/*
			Default constructor
			 */
			ClientThread(Socket s){
				this.connection = s;
			}


			/*
			Helper Functions
			 */

			// Update all clients, public chat
			public void updateClients(String data, String flag) {
                for (int i = 0; i < clients.size(); i++) {
                    ClientThread t = clients.get(i);
                    try {
                        t.out.writeObject(new Message(t.clientThreadName, null,
								data, flag));
                    } catch (Exception e) {}
                }
			}

			// Update specific client, private chat
			public void privateSend(String receiver, String data) {

				try {
					out.writeObject(new Message(clientThreadName, null,
							data, "isPrivateText"));
				} catch (Exception e) {}

                for (int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					if (Objects.equals(t.clientThreadName, receiver)) {
						try {
							t.out.writeObject(new Message(t.clientThreadName, null,
									data, "isPrivateText"));
							break;
						} catch (Exception e) {}
					}
				}
			}

			// Update all clients profile_friends with new incoming client
			public void updateClientsArrayLists(String username, String data) {
                for (int i = 0; i < clients.size(); i++) {
                    ClientThread t = clients.get(i);
                    try {
						if (t.online) {
							t.out.writeObject(new Message(username, null,
									data, "isUpdateFriends"));
						}
                    } catch (Exception e) {}
                }
			}

			// Update the new client to be up-to-date with all known clients/friends
			public void updateNewClientFriendListUptoDate() {
                for (int i = 0; i < friends.size(); i++) {
                    String friend = friends.get(i);
                    try {
						if (!Objects.equals(friend, clientThreadName)) {
							out.writeObject(new Message(friend, null,
									"addFriend", "isUpdateFriends"));
						}
                    } catch (Exception e) {}
                }
			}

			// Return true if is unique name, false otherwise
			public boolean checkUniqueName() {
				Message msg = null;
				try {
					msg = (Message) in.readObject();
				} catch (Exception e) {}

				if (!friends.isEmpty()) {
					for (String friendName : friends) {
						if (Objects.equals(msg.getUsername(), friendName)) {
							try {
								out.writeObject(new Message(msg.getUsername(), "",
										"false", "isCheckUniqueName"));
							} catch (Exception e) {
							}
							return false;
						}
					}
				}

				try {
					out.writeObject(new Message(msg.getUsername(), "",
							"true", "isCheckUniqueName"));
				} catch (Exception e) {}

				return true;
			}


			public void run(){

				// Define new input and output streams
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}

				// Loop until unique name found
				while (!checkUniqueName()) {}


				// Receive new client's name
				Message msg = null;
				try {
					msg = (Message) in.readObject();
				} catch (Exception e) {}

				// Send new client's name
				if (msg.isInfoName()) {

					String textInfo = "-- " + msg.getUsername() + " just dropped by! --";
					Message callMsg = new Message("", "",
							textInfo, "isInfoName");

					callback.accept(callMsg);
					updateClients(textInfo, "isInfoName");

					updateNewClientFriendListUptoDate();
					online = true;
					updateClientsArrayLists(msg.getUsername(), "addFriend");

					clientThreadName = msg.getUsername();
					friends.add(clientThreadName);
				}

				// Continuously receive input
				while(true) {
					    try {
							msg = (Message) in.readObject();


							String textChat = "";
							// Input message is public text
							if (msg.isPublicText()) {
								textChat = msg.getUsername() + " > [Public]: " + msg.getData();
								updateClients(textChat, "isPublicText");
							}
							// Input message is private text
							else if (msg.isPrivateText()) {
								textChat = msg.getUsername() + " > [" + msg.getReceiver() +
										"]: " + msg.getData();
								privateSend(msg.getReceiver(), textChat);
							}
							Message callMsg = new Message("","",
									textChat,"isPublicText");
							callback.accept(callMsg);
						}
						// Client got disconnected
					    catch(Exception e) {

							String leftServerText = "-- " + clientThreadName + " has left the server! --";
							Message callMsg = new Message("","",
									leftServerText,"");

					    	callback.accept(callMsg);
					    	updateClients(leftServerText, "");

							updateClientsArrayLists(clientThreadName, "removeFriend");
							friends.remove(clientThreadName);
							clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
