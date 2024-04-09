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


public class Server{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<String> friends = new ArrayList<>();
	TheServer server;
	private Consumer<Serializable> callback;
	
	
	Server(Consumer<Serializable> call){
	
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	
	public class TheServer extends Thread{
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);){
		    System.out.println("Server is waiting for a client!");
			
		    while(true) {
		
				ClientThread c = new ClientThread(mysocket.accept(), count);
				clients.add(c);
				c.start();
				
				count++;
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	

		class ClientThread extends Thread{

		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			String clientThreadName;
			boolean online = false;
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
			}
			
			public void updateClients(String data) {
                for (int i = 0; i < clients.size(); i++) {
                    ClientThread t = clients.get(i);
                    try {
                        t.out.writeObject(new Message(t.clientThreadName, null, data, "isPublicText"));
                    } catch (Exception e) {}
                }
			}

			public void privateSend(String receiver, String data) {
				for (int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					if (Objects.equals(t.clientThreadName, receiver)) {
						try {
							t.out.writeObject(new Message(t.clientThreadName, null, data, "isPrivateText"));
							break;
						} catch (Exception e) {}
					}
				}
			}

			public void updateClientsArrayLists(String username, String data) {
                for (int i = 0; i < clients.size(); i++) {
                    ClientThread t = clients.get(i);
                    try {
						if (t.online) {
							t.out.writeObject(new Message(username, null, data, "isUpdateFriends"));
						}
                    } catch (Exception e) {}
                }
			}

			public void updateNewClientFriendListUptoDate() {
                for (int i = 0; i < friends.size(); i++) {
                    String friend = friends.get(i);
                    try {
						if (!Objects.equals(friend, clientThreadName)) {
							out.writeObject(new Message(friend, null, "addFriend", "isUpdateFriends"));
						}
                    } catch (Exception e) {}
                }
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

				// Receive new client's name
                Message msg = null;
                try {
                    msg = (Message) in.readObject();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

				// Send new client's name
                if (msg.isInfoName()) {

					String textInfo = ">> " + msg.getUsername() + " just dropped by!";
					callback.accept(textInfo);
					updateClients(textInfo);

					updateNewClientFriendListUptoDate();
					online = true;
					updateClientsArrayLists(msg.getUsername(), "addFriend");

					clientThreadName = msg.getUsername();
					friends.add(clientThreadName);
				}
					
				 while(true) {
					    try {
							msg = (Message) in.readObject();
							String textChat = "";
							if (msg.isPublicText()) {
								textChat = "[Public] > " + msg.getUsername() + ": " + msg.getData();
								updateClients(textChat);
								textChat = "[" + msg.getUsername() + "] > [Public] ~ " + msg.getData();
							}
							else if (msg.isPrivateText()) {
								textChat = "[Private] > " + msg.getUsername() + ": " + msg.getData();
								privateSend(msg.getReceiver(), textChat);
								textChat = "[" + msg.getUsername() + "] > [" + msg.getReceiver() + "] ~ " + msg.getData();
							}
							callback.accept(textChat);
						}
					    catch(Exception e) {
					    	callback.accept(">> "+ clientThreadName + " just left the server!");
					    	updateClients(">> "+ clientThreadName +" has left the server!");

							updateClientsArrayLists(clientThreadName, "removeFriend");
							friends.remove(clientThreadName);
							clients.remove(this);
					    	break;
					    }
					}
				}//end of run
			
			
		}//end of client thread
}


	
	

	
