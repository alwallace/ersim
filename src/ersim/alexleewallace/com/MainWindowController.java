package ersim.alexleewallace.com;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MainWindowController implements ActionListener {
	JFrame myFrame;
	
	JScrollPane myInfoPane;
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
		myInfoArea = new JTextArea(5,20);
		myInfoPane = new JScrollPane(myInfoArea);
		myInfoArea.setEditable(false);
		
		// SETUP Command Pane
		myCommandField = new JTextField();
		myCommandField.addActionListener(this);
		
		// ADD to the frame
		myFrame.getContentPane().add("Center", myInfoPane);
		myFrame.getContentPane().add("South", myCommandField);
		
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
