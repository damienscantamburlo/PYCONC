package net.cercis.jconc.ui;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.TextArea;
import java.awt.Panel;
import java.awt.Button;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Listing extends Frame implements WindowListener{

	private int state;
	/* 0 - Nodes
	 * 1 - Elements
	 * 2 - Forces
	 * 3 - Reactions
	 * 4 - Bar
	 * 5 - Conc
	 * 6 - Cables
	 */
	private String str;

	public Listing(String str, int state, DrawingCanvas dc){
		this.state = state;
		StringFile sf = Jconc.sf;
		if(state == 0)
			setTitle(sf.getValue("LISTNODE",1));
		else if(state == 1)
			setTitle(sf.getValue("LISTELEM",1));
		else if(state == 2)
			setTitle(sf.getValue("LISTFOR",1));
		else if(state == 3)
			setTitle(sf.getValue("LISTREAC",1));
		else if(state == 4)
			setTitle(sf.getValue("LISTBAR",1));
		else if(state == 5)
			setTitle(sf.getValue("LISTCONC",1));
		else if(state == 6)
			setTitle(sf.getValue("LISTCAB",1));
		else if(state == 7)
			setTitle(sf.getValue("LISTMSG",1));
		else if(state == 8)
			setTitle(sf.getValue("LISTALL",1));
		setSize(400,600);
		setLocationRelativeTo(dc);
		addWindowListener(this);
		setVisible(true);
		this.str = str;
		TextArea ta = new TextArea(str,5,100);
		add(ta);
	}
    public void windowActivated (WindowEvent e) {
    }
    public void windowClosed (WindowEvent e) {
    }
    public void windowClosing (WindowEvent e) {
	setVisible(false);
    }
    public void windowDeactivated (WindowEvent e) {
    }
    public void	windowDeiconified (WindowEvent e) {
    }
    public void windowIconified (WindowEvent e) {
    }
    public void windowOpened (WindowEvent e) {
	setVisible(true);
    }
}
