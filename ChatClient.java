import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;



public class ChatClient {
	
	Socket clientsocket;
	PrintWriter output;
	
	Scanner scan;
	Scanner input;
	String message;

	
	
	public ChatClient(int port) {
		
		try {
			
			this.clientsocket = new Socket("localhost", port);	// make clientsocket

			this.scan = new Scanner(new InputStreamReader(System.in));		// read input from line

			this.output = new PrintWriter(clientsocket.getOutputStream(), true);	// get the client's outputstream

			this.input = new Scanner(new InputStreamReader(clientsocket.getInputStream()));	// get the clien'ts input stream
			
		} catch (Exception e) {}
	}

	public void close() {
		try {
			clientsocket.shutdownOutput();

			clientsocket.close();
			System.exit(0);
			
		} catch (Exception e) {}
	}
	
	public void start() {
		ExecutorService t = Executors.newFixedThreadPool(10);	// start the threads

		t.submit(() -> recv());	// let the thread recieve messages

		t.submit(() -> send());	// let the thread send messages

		t.shutdown();	// then we shutdown the thread

	}
	
	
	
	public void recv() {

		while(input.hasNextLine()) {		// continously read input

			message = input.nextLine();

			if(message.length() == 0) {		// when sent an empty string then stop reading input 

				break;
			}
			System.out.println(message);	// print the message received
		}

		close();		// close the socket
	}

	public void send() {
		
		while (scan.hasNextLine()) {	// scan continously
			
			message = scan.nextLine();
			
			if(message.length() == 0) { // break out of the loop when we get empty message like in Messenger
				
				break;
			}
			output.println(message);		// print the message to the output
		}
		close();			// close socket
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		int port = Integer.valueOf(args[0]);		// get the port number, start the client
		
		ChatClient client = new ChatClient(port);
		
		client.start();
	}
	
	
	

}
