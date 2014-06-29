package ersim.alexleewallace.com;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;

public class PatientGenerator {
	Random random;
	
	public PatientGenerator() {
		random = new Random();
	}

	public Patient generatePatient(int diagnosisID) {
		// Make the connection with the SQL database
		SQLiteQueue dbQueue = new SQLiteQueue(new File("extra/test.sqlite"));
		dbQueue.start();
		
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
		// You have to create this as a final variable so that the thread is okay
		final int tempDiagnosisID = diagnosisID;
		// set the result of the job to output
		Patient patient = dbQueue.execute(new SQLiteJob<Patient>() {
			protected Patient job(SQLiteConnection connection) throws SQLiteException {
				Patient p = new Patient();
				
				SQLiteStatement stTriggerList = connection.prepare("SELECT id, trigger FROM triggers");
				try {
					while (stTriggerList.step()) {
						String tempTrigger = stTriggerList.columnString(1);
						
						// This section gets all possible responses for this trigger/diagnosis combo and randomly chooses one.
						// Create the statement (note ?'s are place holders that you can bind variables to)
						SQLiteStatement stResponseIDList = connection.prepare("SELECT responses.id FROM dtr_links, tr_links, responses " +
								"WHERE dtr_links.diagnosis_id=? AND dtr_links.tr_link_id AND tr_links.trigger_id=? AND responses.id=tr_links.response_id");
						
						int tempResponseID = 0;
						try {
							stResponseIDList.bind(1, tempDiagnosisID);
							stResponseIDList.bind(2, stTriggerList.columnInt(0));
							ArrayList<Integer> responseIDs = new ArrayList<Integer>();
							while (stResponseIDList.step())
								responseIDs.add(new Integer(stResponseIDList.columnInt(0)));
							
							if (responseIDs.size() > 0) {
								tempResponseID = responseIDs.get(random.nextInt(responseIDs.size())).intValue();
								
								SQLiteStatement stResponseList = connection.prepare("SELECT response, media_id FROM responses WHERE id=?");
								try {
									// This binds the first ? with temp_query == query in the main function
									stResponseList.bind(1, tempResponseID);
									
									// Retrieve the row with step
									stResponseList.step();
									String tempResponse = stResponseList.columnString(0);
									String tempMediaFile = "";
									
									// Check if there is a media file associated, if so grab it!
									if (!stResponseList.columnNull(1)) {
										int tempMediaFileID = stResponseList.columnInt(1);
										SQLiteStatement stMedia = connection.prepare("SELECT id, file FROM media WHERE id=?");
										try {
											stMedia.bind(1, tempMediaFileID);
											stMedia.step();
											if (!stMedia.columnNull(0))
												tempMediaFile = stMedia.columnString(1);
										} finally {
											stMedia.dispose();
										}
									}
										
									p.addResponsePair(tempTrigger, tempResponse, tempMediaFile);
								} finally {
									stResponseList.dispose();
								}
							} else {
								p.addResponsePair(tempTrigger, "I don't want to answer that.", "");
							}
						} finally {
							stResponseIDList.dispose();
						}
					}
				} finally {
					stTriggerList.dispose();
				}
				
				return p;
			}
		}).complete();
		
		dbQueue.stop(true);
		
		return patient;
	}
	
}
