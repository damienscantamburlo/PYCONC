package net.cercis.jconc.ui;
import net.cercis.jconc.fem.*;

import javax.swing.JApplet;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.TextArea;
import java.awt.Panel;
import java.awt.Button;
import java.awt.Component;
import java.awt.Window;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class ModelWriter extends Frame implements WindowListener, ActionListener {

    private Model model = null;
    private String file;
    private TextArea ta;
    private boolean status;
    StringFile sf;

    //Applet started with a model
    public ModelWriter (Model model, Frame frame) {

	setTitle("Input File");
	setLocationRelativeTo(frame);
	setSize(400,600);
	sf = Jconc.sf;
	setVisible(true);
	addWindowListener(this);
 	this.model = model;
	status = true;
	fileString();
	ta = new TextArea(file, 5, 100);
	add(ta);
	Button button1 = new Button(sf.getValue("INPUT",3)); //button for reloading the input file      
	button1.setActionCommand("ok");
	button1.addActionListener(this);
	Panel p = new Panel();
	p.add(button1);
	add("South",p);
	setVisible(true);
	ta.requestFocus();
	getParent();
    }
    
  private String fileString () {
	file = "";
	file+= "version,"+model.getVer()+"*"+'\n';
	file+="steps,"+model.getSteps()+"*"+'\n';
        file += "scaleforces," + model.getScaleForces() + "*\n\n";
        int numPos = 8; // number of characters user to output real numbers
        
	Map mMates = model.getMapOfMates();
	Iterator itMates = ((Collection) mMates.values()).iterator();
	while (itMates.hasNext()) {
	    Material mi = (Material) itMates.next();
            String matName = mi.type();
	    file += "m," + mi.id() + "," + matName + ","
                    + Jconc.formatNumber(mi.fc(), numPos) + ","
                    + Jconc.formatNumber(mi.modEl(), numPos);
            if (matName.equalsIgnoreCase("concrete")) {
               MaterialConc mc = (MaterialConc) mi;
               if (mc.getInputCoefEtas())
                  file += "," + mc.getCoefEta1() + "," + mc.getCoefEta2();
            }
            else if (matName.equalsIgnoreCase("steel")) {
               MaterialSteel ms = (MaterialSteel) mi;
               if (ms.modHard() != ms.modEl() / 10000) 
                  file += "," + Jconc.formatNumber(ms.modHard(), numPos);
            }
            else if (matName.equalsIgnoreCase("steelTC")) {
               MaterialSteelTC ms = (MaterialSteelTC) mi;
               if (ms.modHard() != ms.modEl() / 10000) 
                  file += "," + Jconc.formatNumber(ms.modHard(), numPos);
            }
            else if (matName.equalsIgnoreCase("strucSteel")) {
               MaterialStrucSteel ms = (MaterialStrucSteel) mi;
               if (ms.modHard() != ms.modEl() / 10000) 
                  file += "," + Jconc.formatNumber(ms.modHard(), numPos);
            }
            else if (matName.equalsIgnoreCase("cable")) {
               MaterialCable mcab = (MaterialCable) mi;
               file += "," + Jconc.formatNumber(mcab.sig0(), numPos);
               if (mcab.modHard() != mcab.modEl() / 10000)
                  file += "," + Jconc.formatNumber(mcab.modHard(), numPos);
            }
            else if (matName.equalsIgnoreCase("concWithfct")) {
               file += "," + Jconc.formatNumber(((MaterialConcWithfct) mi).fct(), numPos);
            }
            else if (matName.equalsIgnoreCase("tensOnly")) {
               MaterialTensOnly ms = (MaterialTensOnly) mi;
               if (ms.modHard() != ms.modEl() / 10000) 
                  file += "," + Jconc.formatNumber(ms.modHard(), numPos);
            }
            else if (matName.equalsIgnoreCase("compOnly")) {
               MaterialCompOnly ms = (MaterialCompOnly) mi;
               if (ms.modHard() != ms.modEl() / 10000) 
                  file += "," + Jconc.formatNumber(ms.modHard(), numPos);
            }
             else
                System.out.println("Cannot output material '" + matName + "'");

	    file += "*\n";
	}
        file += "\n";
	Map mBouns = model.getMapOfBouns();
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {
	    Boun bi = (Boun) itBouns.next();
	    file += "b," + bi.node() + "," + bi.type() + ","
                    + Jconc.formatNumber(bi.disp(), numPos) + "*\n";
	}
        file += "\n";
	Map mLoads = model.getMapOfLoads();
	Iterator itLoads = ((Collection) mLoads.values()).iterator();
        double scalLoc = model.getScaleForces();
        if (scalLoc == 0) scalLoc = 1.0;
	while (itLoads.hasNext()) {
	    Load li = (Load) itLoads.next();
            if (!li.noInput()) {
	       file += "f," + li.node() + ","
                       + Jconc.formatNumber(li.fxInput() / scalLoc, numPos) + ","
                       + Jconc.formatNumber(li.fyInput() / scalLoc, numPos) +"*\n";
            }
	}
        file += "\n";
	Map mNodes = model.getMapOfNodes();
	Iterator itNodes = ((Collection) mNodes.values()).iterator();
	while (itNodes.hasNext()) {
	    Node ni = (Node) itNodes.next();
	    file += "n," + ni.id() + ","
                    + Jconc.formatNumber(ni.x(), numPos) + ","
                    + Jconc.formatNumber(ni.y(), numPos) + "*\n";
	}
        file += "\n";
	Map mElems = model.getMapOfElems();
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equalsIgnoreCase("Conc3N")) {
		file += "e," + ei.id() + ",conc3N," + 
		    ei.getNode(0) + "," + ei.getNode(1) + "," + ei.getNode(2)
		    + "," +  Jconc.formatNumber(ei.parameter(1), numPos) + "," +  
		    ei.material() + "*";
			file += '\n';//modified code
	    }
	    else if (ei.type().equalsIgnoreCase("Bar")) {
		file += "e," + ei.id() + ",bar," + 
		    ei.getNode(0) + "," + ei.getNode(1)
		    + "," +  Jconc.formatNumber(ei.parameter(1), numPos) + "," +
		    ei.material() + "*";
			file += '\n';//modified code
	    }
	    else if (ei.type().equalsIgnoreCase("Cable")) {
               Cable c = (Cable) ei;
               file += "e," + ei.id() + ",cable," +
               ei.getNode(0) + "," + ei.getNode(1) + "," +
               Jconc.formatNumber(ei.parameter(1), numPos) + "," +
               ei.material() + "*";
               file += '\n';
	    }
	}	
	return file;
    }
	
    public boolean getStatus() {
    
    	return status;
    }
	//INTRODUCED BY ROHIT---------->
	public void actionPerformed(ActionEvent ae) {
//focus text
	for (Component c=this; c != null; c = c.getParent()) {
	    if (c instanceof Window) {
		//System.out.println("focus at " + ((Window) c).getFocusOwner());
	    }
	}
//end of focus text

		String cmd = ae.getActionCommand();
		if(cmd.equals("ok")) {
		
			//write the code here for reloading
			setVisible(false);
			Launcher launcher = new Launcher(ta.getText());
			model = launcher.getModel();
			status = false;
			//launcher.loadViewer();
		}
	}

	public Model getModel() {
	
		return model;
	}
	//<---------------END OF INTRODUCED BY ROHIT
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
