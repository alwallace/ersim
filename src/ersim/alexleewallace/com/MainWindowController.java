package ersim.alexleewallace.com;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainWindowController implements ActionListener {
	JFrame myFrame;
	
	JScrollPane myInfoPane;
	JScrollPane myMediaPane;
	JTextArea myInfoArea;
	String info;
	
	JTextField myCommandField;
	
	/*
	 * Connected pieces
	 * 
	 */
	MainController mc;
	
	public MainWindowController(MainController mc) {
		setup();
		this.mc = mc;
	}
	
	private void setup() {
		// SETUP Frame
		myFrame = new JFrame("ERSim");
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setLayout(new BorderLayout());
		
		// SETUP Info Pane
		myInfoArea = new JTextArea(30,50);
		myInfoArea.setLineWrap(true);
		myInfoArea.setEditable(false);
		myInfoPane = new JScrollPane(myInfoArea);
		
		// SETUP Command Pane
		myCommandField = new JTextField();
		myCommandField.addActionListener(this);
		
		// Setup left pane
		JSplitPane myLeftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, myInfoArea, myCommandField);
		
		// Setup the media pane
		myMediaPane = new JScrollPane();
		
		// Setup split pane
		JSplitPane mySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myLeftPane, myMediaPane);
		
		// ADD to the frame
		myFrame.getContentPane().add("Center", mySplitPane);
		
		myFrame.pack();
		myFrame.setVisible(true);
		
		info = "";
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == myCommandField) {
			String userInput = myCommandField.getText();
			if (!userInput.isEmpty()) {
				info += "> " + userInput + "\n";
				String result = mc.processInput(userInput);
				info += result + "\n";
				myCommandField.setText("");
				myInfoArea.setText(info);
			}
		}
	}

}
