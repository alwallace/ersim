package ersim.alexleewallace.com;

import java.io.File;

import com.almworks.sqlite4java.*;

public class MainController {

	PatientGenerator myPatientGen;
	Patient myPatient;
	
	public MainController() {
		myPatientGen = new PatientGenerator();
		myPatient = myPatientGen.generatePatient(1);
		
	}
	
	/* 
	 * Takes a string input from the user
	 * Parses the query into the language (either ___? or order ____)
	 * Takes the appropriate query or action
	 * Returns information about query or action taken to responds to the user
	 * 
	 */
	public String processInput(String input) {
		String output = "";
		// Test if is a query by ending with "?"
		if (input.endsWith("?")) {
			// trim the ? off the query
			output = processQuery(input.substring(0, input.length()-1));
		} 
		// Test if is an action by starting with "order "
		else if (input.startsWith("order ")) {
			output = processAction(input.substring(6));
		} 
		else {
			output = "HELP: either end with '?' or start with 'order ' to get a response or perform an action!";
		}
		return output;
	}
	
	private String processQuery(String query) {
		String response = "No patient is being examined. Please open an encounter!";
		if (myPatient != null)
			response = myPatient.getResponse(query);
		return response;
	}
	
	private String processAction(String action) {
		String output = "";

		return output;
	}
	
}
