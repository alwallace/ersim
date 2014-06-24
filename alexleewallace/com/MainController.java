package ersim.alexleewallace.com;

import java.io.File;

import com.almworks.sqlite4java.*;

public class MainController {

	SQLiteQueue dbQueue;
	
	public MainController() {
		// Make the connection with the SQL database
		dbQueue = new SQLiteQueue(new File("extra/test.sqlite"));
		dbQueue.start();
		
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
		String output = "";
		
		/*
		 *  THIS IS SOME CRAZY SHIT
		 *  So in working with GUI's there are multithreaded systems going on so..
		 *  sqlite4java creates a job queue that has a controller and manages the multithreading for you
		 *  thus you create jobs and pas them along to the job queue. Jobs are basically mini functions
		 *  that return a result
		 *  This function makes a query "select id, trigger, etc..." then returns the value of the response that matches
		 *  the trigger and sets it to output.
		 *  
		 *  Follow this: https://code.google.com/p/sqlite4java/wiki/JobQueue
		 *  
		 */
		// You have to create this as a final variable so that the threat is okay
		final String temp_query = query;
		// set the result of the job to output
		output = dbQueue.execute(new SQLiteJob<String>() {
			protected String job(SQLiteConnection connection) throws SQLiteException {
				// Create the statement (note ?'s are place holders that you can bind variables to)
				SQLiteStatement st = connection.prepare("SELECT id, trigger, response, media_id FROM queries WHERE trigger=?");
				try {
					// This binds the first ? with temp_query == query in the main function
					st.bind(1, temp_query);
					// Retrieve the row with step
					st.step();
					// Return the value in column 2 "response" (note column numbering starts at 0)
					if (st.columnNull(0)) // if there wasn't a row that matches then return null
						return null; 
					else 
						return st.columnString(2);
				} finally {
					st.dispose();
				}
			}
		}).complete();
		
		// This tests to see if a row was actually returned, if there wasn't
		// then the result was null, change output to a generic answer
		if (output == null) {
			output = "I don't want to answer that.";
		}
		return output;
	}
	
	private String processAction(String action) {
		String output = "";

		return output;
	}
	
}
