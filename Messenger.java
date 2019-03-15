	// Jeff

	import java.net.*;
	import java.io.*;


	public class Messenger{
		
		public static void main(String[] args) {
			
			if(args.length < 1 || args.length > 3) { 
				System.out.println("Usage: Messenger -l <port number> for server or Messenger <port number> <serveraddress> for client");
				return;
			}
			
			// For server
			if(args[0].equals("-l")) {
				
				try {			// Try/catch for exceptions
					
					int portnumber = Integer.valueOf(args[1]);
					ServerSocket serversocket = new ServerSocket(portnumber);
					
					Socket clientsocket = serversocket.accept();
					serversocket.close();
					
					Writer serverWriter = new Writer(clientsocket);
					Reader serverReader = new Reader(clientsocket);
					
					Thread t1 = new Thread(serverReader);
					Thread t2 = new Thread(serverWriter);
					
					t1.start();
					t2.start();
					
					try {				// sync the threads
						t1.join();
						t2.join();
					} catch (Exception e) {}
				}
				catch (Exception e) {}
				}
				else {
				try {
					
					int portnumber = Integer.valueOf(args[0]);
					Socket clientsocket = new Socket("localhost", portnumber);
					
					Writer clientWriter = new Writer(clientsocket);
					Reader clientReader = new Reader(clientsocket);
					
					Thread t1 = new Thread(clientReader);
					Thread t2 = new Thread(clientWriter);
					
					t1.start();
					t2.start();
					
					try {
						t1.join();
						t2.join();
					}
					catch (Exception e) {}
					
				}
				catch (Exception e) {}
			}
		
		
		
		}
		
		static class Writer implements Runnable{
			private Socket cs;
			
			Writer(Socket clientsocket) {
				cs = clientsocket;
			}

			@Override
			public void run() {
				try {
				PrintWriter output = new PrintWriter(this.cs.getOutputStream(), true);
				
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(System.in));
				
				
				String message;
				message = stdInput.readLine();
				
				
				while(message != null) {

					output.println(message);
					message = stdInput.readLine();
				}
				
				
				cs.close();
				output.close();
				stdInput.close();
				System.exit(0);
			}
				catch (Exception e) {}	
		}
			
	}
		
		static class Reader implements Runnable {
			
			
			private Socket cs;
			
			Reader(Socket clientsocket) {
				cs = clientsocket;
				
			}

			@Override
			public void run() {
				try {
					BufferedReader input = new BufferedReader(new InputStreamReader(this.cs.getInputStream()));
					String line;
					
					while((line = input.readLine()) != null) {
						System.out.println(line);
					}
					
					input.close();
					cs.close();
					System.exit(0);
					
				}
				catch (Exception e) {}
				
			}
			
			
		}
	}