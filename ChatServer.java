

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;


public class ChatServer {
	
	ServerSocket serversocket;
	
	HashMap<Socket, PrintWriter> listofclients;	// pair our clients with their outputstream

	ExecutorService pthread;	
	
	
	
	public ChatServer(int portnumber) {	// constructor
		
		try {

			this.serversocket = new ServerSocket(portnumber);

			this.pthread = Executors.newFixedThreadPool(10);
			this.listofclients = new HashMap<>();
		}
		catch (Exception e) {}
	}


	public void close() {
		try {
			pthread.shutdown();		// call to shutdown the server
		}
		catch (Exception e) {}
		}
	
	public void connectclients() {	// find and connect the clients to the server
		
		while(true) {		// continously search
			try {
				
				Socket client = serversocket.accept();
				PrintWriter output = new PrintWriter(client.getOutputStream(),true);
				
				
				listofclients.put(client, output);
				
				pthread.submit(() -> sendmessages(client)); // now we send messages to the clients
			} catch (Exception e) {}
		}
	}
		public void sendmessages(Socket csocket) {
			
			try {
				
				BufferedReader input = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
				String name = null;
				String message;
				
				while ((message = input.readLine()) != null) { // read from input continously
					
					if (name == null) {		// assume start of read input is the client name
						name = message;
					} else {
						
						for (Socket s : listofclients.keySet()) {
							if(!s.equals(csocket)) {
								listofclients.get(s).println(name + ": " + message);
							}
						}
					}
					
					
				}
			} catch (Exception e) {}
			
		}
		
		

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int portnumber = Integer.parseInt(args[0]);
		ChatServer server = new ChatServer(portnumber);

		server.connectclients();
		server.close();
	}

	
	
	
	
}
	



