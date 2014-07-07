package admin.ersim.alexleewallace.com;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;

public class DiagnosisCreatorWindowController implements ActionListener, ListSelectionListener {
	JFrame myFrame;
	
	TriggerTableModel myTriggerModel;
	JTable myTriggerTable;
	
	ResponseTableModel myResponseModel;
	JTable myResponseTable;
	
	int myDiagnosisID;
	int mySelectedTriggerID;
	
	public DiagnosisCreatorWindowController() {
		setup();
	}
	
	private void setup() {
		// SETUP Frame
		myFrame = new JFrame("ERSim - Administration");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setLayout(new BorderLayout());

		
		myTriggerModel = new TriggerTableModel();
		myTriggerTable = new JTable(myTriggerModel);
		myTriggerTable.setFillsViewportHeight(true);
		myTriggerTable.getSelectionModel().addListSelectionListener(this);
		JScrollPane myTriggerPane = new JScrollPane(myTriggerTable);
		
		myDiagnosisID = 1;
		mySelectedTriggerID = 1;
		myResponseModel = new ResponseTableModel();
		myResponseTable = new JTable(myResponseModel);
		myResponseTable.setFillsViewportHeight(true);
		JScrollPane myResponsePane = new JScrollPane(myResponseTable);
		
		// Setup split pane
		JSplitPane mySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myTriggerPane, myResponsePane);
				
		// ADD to the frame
		myFrame.getContentPane().add("Center", mySplitPane);
		myFrame.pack();
		myFrame.setVisible(true);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {

	}
	
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getSource().equals(myTriggerTable.getSelectionModel())) {
			mySelectedTriggerID = Integer.valueOf((String)myTriggerTable.getValueAt(myTriggerTable.getSelectedRow(), 0));
			myResponseModel.fireTableDataChanged();
		}
	}

	private class TriggerTableModel extends AbstractTableModel {
		SQLiteQueue myDBTrigger;
		
		public TriggerTableModel() {
			super();
			
			// Make the connection with the SQL database
			myDBTrigger = new SQLiteQueue(new File("extra/test.sqlite"));
			myDBTrigger.start();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			// set the result of the job to output
			int myValue = myDBTrigger.execute(new SQLiteJob<Integer>() {
				protected Integer job(SQLiteConnection connection) throws SQLiteException {
					SQLiteStatement stTriggerList = connection.prepare("SELECT count(id) FROM triggers");
					try {
						stTriggerList.step();
						return stTriggerList.columnInt(0);		
					} finally {
						stTriggerList.dispose();
					}
				}
			}).complete();
			return myValue;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// You have to create this as a final variable so that the thread is okay
			final int tempRowIndex = rowIndex;
			final int tempColumnIndex = columnIndex;
			// set the result of the job to output
			String myValue = myDBTrigger.execute(new SQLiteJob<String>() {
				protected String job(SQLiteConnection connection) throws SQLiteException {
					String value = "";
					SQLiteStatement stTriggerList = connection.prepare("SELECT id, trigger FROM triggers LIMIT 1 OFFSET ?");
					stTriggerList.bind(1, tempRowIndex);
					try {
						if (stTriggerList.step()) {
							if (tempColumnIndex == 0) // ID = integer
								value = "" + stTriggerList.columnInt(0);
							else if (tempColumnIndex == 1) // TRIGGER = String
								value = stTriggerList.columnString(1);
						} else {
							value = "<ERR> no row";
						}
					} finally {
						stTriggerList.dispose();
					}
					return value;
				}
			}).complete();
			return myValue;
		}
		public void close() {
			myDBTrigger.stop(true);
		}		
	}
	
	private class ResponseTableModel extends AbstractTableModel {
		SQLiteQueue myDBResponse;
		
		public ResponseTableModel() {
			super();
			
			// Make the connection with the SQL database
			myDBResponse = new SQLiteQueue(new File("extra/test.sqlite"));
			myDBResponse.start();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			final int tempDiagnosisID = myDiagnosisID;
			final int tempSelectedTriggerID = mySelectedTriggerID;
			// set the result of the job to output
			int myValue = myDBResponse.execute(new SQLiteJob<Integer>() {
				protected Integer job(SQLiteConnection connection) throws SQLiteException {
					SQLiteStatement stTriggerList = connection.prepare("SELECT count(response_id) FROM dtr_links, tr_links "
								+ "WHERE diagnosis_id=? AND tr_links.id=tr_link_id AND trigger_id=?");
					stTriggerList.bind(1, tempDiagnosisID);
					stTriggerList.bind(2, tempSelectedTriggerID);
					try {
						stTriggerList.step();
						return stTriggerList.columnInt(0);		
					} finally {
						stTriggerList.dispose();
					}
				}
			}).complete();
			return myValue;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			// You have to create this as a final variable so that the thread is okay
			final int tempDiagnosisID = myDiagnosisID;
			final int tempSelectedTriggerID = mySelectedTriggerID;
			final int tempRowIndex = rowIndex;
			final int tempColumnIndex = columnIndex;
			// set the result of the job to output
			String myValue = myDBResponse.execute(new SQLiteJob<String>() {
				protected String job(SQLiteConnection connection) throws SQLiteException {
					String value = "";
					SQLiteStatement stTriggerList = connection.prepare("SELECT responses.id, response, media_id "
							    + "FROM responses, dtr_links, tr_links "
								+ "WHERE diagnosis_id=? AND tr_links.id=tr_link_id AND trigger_id=? AND responses.id=tr_links.response_id "
								+ "LIMIT 1 OFFSET ?");
					stTriggerList.bind(1, tempDiagnosisID);
					stTriggerList.bind(2, tempSelectedTriggerID);
					stTriggerList.bind(3, tempRowIndex);
					try {
						if (stTriggerList.step()) {
							if (tempColumnIndex == 0) // ID = integer
								value = "" + stTriggerList.columnInt(0);
							else if (tempColumnIndex == 1) // TRIGGER = String
								value = stTriggerList.columnString(1);
							else if (tempColumnIndex == 2) // MEDIA_ID = integer
								value = "" + stTriggerList.columnInt(2);
						} else {
							value = "<ERR> no row";
						}
					} finally {
						stTriggerList.dispose();
					}
					return value;
				}
			}).complete();
			return myValue;
		}
		public void close() {
			myDBResponse.stop(true);
		}		
	}
}
