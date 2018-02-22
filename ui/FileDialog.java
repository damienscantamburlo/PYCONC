package net.cercis.jconc.ui;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;



import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ComponentEvent;
import java.awt.Frame;
import java.util.StringTokenizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class FileDialog extends JDialog implements ActionListener, PropertyChangeListener, ListSelectionListener{

	private JTextField textField;
	private JTextArea ta;
   private JOptionPane optionPane;
	private JList list;
   private DefaultListModel listModel;
   private String typedText = null;
	private String text1, text2;
	boolean flag;
	private int mode;
	private Frame frame;
	int open;
    	public String getValidatedText() {
		   return typedText;
      }

	public FileDialog(Frame frame, String []str, int length, int open, String fileName, int mode){
	
		super(frame,  true);
/*	    urlBase = getCodeBase().toString();
	    curLang = this.getParameter("LANGUAGE");
	    try{
		    StringFile tmpSF = new StringFile(gf.readURLFile(),"[");
		    sf = new StringFile(tmpSF.getStringBlock(curLang));
	    }
	    catch(Exception e){
	    
	    	System.out.println("Problem reading the text.ini file");
	    }*/
      int panelWidth = 500;
      int panelHeight = 500;
		StringFile sf = Jconc.sf;

		this.frame = frame;
		this.mode = mode;
		setSize(panelWidth, panelHeight);
		if(mode == 0){
			if(open == 0)
				setTitle(sf.getValue(("OPEN"),2));
			if(open == 1)
				setTitle(sf.getValue(("SAVE"),2));
			flag = true;
			this.open = open;
			text1 = sf.getValue("OK",1);
			text2 = sf.getValue("CANCEL",1);
			listModel = new DefaultListModel();
			for(int i=0;i<length;i++)
				listModel.addElement(str[i]);
			list = new JList(listModel);
         list.setLayoutOrientation(list.VERTICAL);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			//list.setSelectedIndex(0);
			list.addListSelectionListener(this);
			list.setVisibleRowCount(8);
			//JScrollPane listScrollPane = new JScrollPane(list);
			textField = new JTextField(10);
			Object []array = {list, textField};
			Object []options = {text1,text2};
			optionPane = new JOptionPane(array,JOptionPane.PLAIN_MESSAGE,JOptionPane.YES_NO_OPTION,
                        null,options,options[0]);
		}
		else if(mode == 1){
		
			setTitle(sf.getValue("CONFIRM",1));
			flag = true;
			text1 = sf.getValue("YES",1);
			text2 = sf.getValue("NO",1);
			Object [] array = {str[0]};
			Object []options = {text1, text2};
			optionPane = new JOptionPane(array,
    					JOptionPane.QUESTION_MESSAGE,
    					JOptionPane.YES_NO_OPTION,
    					null,
    					options,
    					options[0]);

		}
		else if(mode == 2){
		

			setTitle(str[0]);
			flag = true;
			text1 =sf.getValue("OK",1);
			Object [] array = {str[1]};
			Object []options = {text1};
			optionPane = new JOptionPane(array,
    					JOptionPane.QUESTION_MESSAGE,
    					JOptionPane.YES_NO_OPTION,
    					null,
    					options,
    					options[0]);

		}
		else if(mode == 3){
		

			setTitle(str[0]);
			flag = true;
			text1 = sf.getValue("OK",1);
         String cont = "<empty list>";
         if (str.length > 1)
            cont = str[1];
			ta = new JTextArea(cont,30,40);
			ta.setEditable(false);
			Object [] array = {new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)};
			Object []options = {text1};
			optionPane = new JOptionPane(array,	JOptionPane.PLAIN_MESSAGE,
                           JOptionPane.YES_NO_OPTION,null,options,options[0]);
		}
		else if(mode == 4){
		

			setTitle(str[0]);
			flag = true;
			text1 = str[1];
			ta = new JTextArea(str[2],Integer.parseInt(str[3]),Integer.parseInt(str[4]));
			Object [] array = {new JScrollPane(ta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)};
			Object []options = {text1};
			optionPane = new JOptionPane(array,
    					JOptionPane.QUESTION_MESSAGE,
    					JOptionPane.YES_NO_OPTION,
    					null,
    					options,
    					options[0]);

		}


		getContentPane().add(optionPane);
		//Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/*
				 * Instead of directly closing the window,
				 * 		                                                                 		                                                              * we're going to change the JOptionPane's
				 * value property.
				 */
				optionPane.setValue(new Integer(
						JOptionPane.CLOSED_OPTION));
			}
		});   
	   	if(mode == 0){		
			addComponentListener(new ComponentAdapter() {
				public void componentShown(ComponentEvent ce) {
					textField.requestFocusInWindow();
				}
			});
			
			//Register an event handler that puts the text into the option pane.
			
			textField.addActionListener(this);
		}
		//Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);
		

		

	}
  	public void valueChanged(ListSelectionEvent e) {
    		if(open == 1){
    			StringTokenizer st = new StringTokenizer((String)list.getSelectedValue()," :");
    			textField.setText(st.nextToken());
    		}
    		else if(open == 0){
    			textField.setText((String)list.getSelectedValue());
			
    		}	
    	}
    
	/** This method handles events for the text field. */
	public void actionPerformed(ActionEvent e) {
		optionPane.setValue(text1);
    	}
     	/** This method reacts to state changes in the option pane. */
    	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();
		
		if (isVisible() && (e.getSource() == optionPane) 
				&& (JOptionPane.VALUE_PROPERTY.equals(prop)
				|| JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
			Object value = optionPane.getValue();			
	    		if (value == JOptionPane.UNINITIALIZED_VALUE) {             		   
				//ignore reset
				return;
	    		}
	    		optionPane.setValue(
		    			JOptionPane.UNINITIALIZED_VALUE);
	    		if (text1.equals(value)) {
				if(mode == 0)
					typedText = textField.getText();
				if(mode == 4)
					typedText = ta.getText();
				flag = false;
				clearAndHide();
	    		}
		       	else { 
				//user closed dialog or clicked cancel
				typedText = null;
				flag = true;
				clearAndHide();
	    		}
		}
    	}
	
	
	
	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		if(mode == 0)
			textField.setText(null);
		setVisible(false);
    	}







}
