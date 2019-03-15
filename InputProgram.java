
import java.util.Scanner;

public class InputProgram {
	
	// can read data from standard input in main method
	// should have something to find the correct values of hte options in the command line
	
	public static String[] search(String [] arg) {
		
		String [] optionstrings = new String[3];
		
		for(int i = 0; i < arg.length; i++) {
			String options = arg[i];
			if(options.equals("-o")) {
				optionstrings[0] = ("option 1: " + arg[i+1]);
			}
			if(options.equals("-t")) {
				optionstrings[1] = ("option 2: " +arg[i+1]);
			}
			if(options.equals("-h")) {
				optionstrings[2] = "option 3";
			}
		}
		return optionstrings;
	}
	
	
			
		
		
		
	public static void Printoptions(String[] optionsarr) {
		for(int i = 0; i < optionsarr.length; i++) {
			if(optionsarr[i] != null) {
				System.out.println(optionsarr[i]);
			}
		}
	}

	public static void main(String[] args){
		//InputProgram p = new InputProgram();
		System.out.println("Standard Input:");
		Scanner s = new Scanner(System.in);
		
		while(s.hasNextLine()) {
			String scan = s.nextLine();
			System.out.println(scan);
			
			
		}
		String [] options = search(args);
		System.out.println("Command line arguments:");
		Printoptions(options);
	}}