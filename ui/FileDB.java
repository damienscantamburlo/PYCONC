//import java.awt.*;
//import java.awt.event.*;
package net.cercis.jconc.ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.ListSelectionModel;
import javax.swing.JComponent;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.TextArea;

import java.util.StringTokenizer;

//import javax.swing.event.*;
public class FileDB extends JPanel implements ListSelectionListener,ActionListener{
    


    private JList list;
    private DefaultListModel listModel;
    private static JFrame frame;
    public String selecStr;
    public boolean flag;
    public int open;
    private TextArea ta;

    /*
     * open = 0 - Open a file
     * open = 1 - Save a file
     * */

    public FileDB(String []str, int length, int open,String fileName){
        
        super(new BorderLayout());
	this.open = open;
	selecStr = "";
	flag = true;
        listModel = new DefaultListModel();
	for(int i=0;i<length;i++)
		listModel.addElement(str[i]);
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        list.setVisibleRowCount(8);
        JScrollPane listScrollPane = new JScrollPane(list);
        JButton ok = new JButton("OK");

        ok.setActionCommand("ok");
        ok.addActionListener(this);
/*	ok.setMinimumSize(new Dimension(this.getWidth()/2,this.getHeight()/4));
	ok.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()/4));
	ok.setMaximumSize(new Dimension(this.getWidth()/2,this.getHeight()/4));*/
        JButton cancel = new JButton("Cancel");
        cancel.setActionCommand("cancel");
        cancel.addActionListener(this);
/*	cancel.setMinimumSize(new Dimension(this.getWidth()/2,this.getHeight()/4));
	cancel.setPreferredSize(new Dimension(this.getWidth()/2,this.getHeight()/4));
	cancel.setMaximumSize(new Dimension(this.getWidth()/2,this.getHeight()/4));*/
        JPanel buttonPane = new JPanel();   
	if(open==1){

		System.out.println("Filename is "+fileName);
		if(fileName!=null){
			StringTokenizer st = new StringTokenizer(fileName," :");
			ta = new TextArea(st.nextToken());
		}
		else
			ta = new TextArea("iConc");
	}
	else if(open == 0){
	
		ta = new TextArea("iConc");
	}
	buttonPane.add(ta);
        buttonPane.add(ok);
        buttonPane.add(cancel);
        add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
	createAndShowGUI();
    }
    
    public void actionPerformed(ActionEvent e){
    
        String cmd = e.getActionCommand();
        if(cmd.equals("ok")){
        
		selecStr = ta.getText();
		frame.setVisible(false);
		flag = false;
        }
        else if(cmd.equals("cancel")){
            
            frame.setVisible(false);
            
        }
    }
    public void valueChanged(ListSelectionEvent e) {
	    if(open == 1){
		    StringTokenizer st = new StringTokenizer((String)list.getSelectedValue()," :");
		    ta.setText(st.nextToken());
	    }
	    else if(open == 0){
		    ta.setText((String)list.getSelectedValue());
    					    
	    }
			    

    }
    public  void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
	if(open==0)
		frame = new JFrame("Open a file");
	else if(open ==1)
		frame = new JFrame("Save file as");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane =this  ;
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
       // frame.setSize(500,500);
//	validate();
    }
 
}

