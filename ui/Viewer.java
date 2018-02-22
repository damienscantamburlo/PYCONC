package net.cercis.jconc.ui;

import net.cercis.jconc.fem.Model;

import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.awt.Dimension;
import java.util.Properties;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Button;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Graphics;
import java.awt.Frame;
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.ps.PSGraphics2D;
import org.freehep.util.export.ExportDialog;
import org.freehep.util.export.ExportFileType;

class Viewer implements ActionListener {

    public Model model; //*****warning***** removed the final tag from the variable model
    private Button png, eps, saveList; 
    private Jconc jc;
    private JFrame mainFrame;

    public Viewer (Model model) {
	this.model = model;
    }
//modified code ------- rohit
	   
    public void updateModel(Model model) {

	this.model = model;
    }

    public Jconc getJc() {
       return jc;
    }
//end of modified code ------ rohit


	public void displayResults (String view, int width, int height, boolean noDisplay) {

	mainFrame = new JFrame("MODEL VIEWER");
	mainFrame.setLocation(200, 100);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	jc = new Jconc(model, this);
	mainFrame.getContentPane().setLayout(new BorderLayout());
	mainFrame.getContentPane().add("Center", jc); 

	png = new Button("Save as");
	png.addActionListener(this);
	eps = new Button("EPS");
	eps.addActionListener(this);
	saveList = new Button("Save Listing");
	saveList.setActionCommand("save");
	saveList.addActionListener(this);
	Panel p = new Panel();
	p.add(eps);
	p.add(png);
	p.add(saveList);
	mainFrame.getContentPane().add("South", p);

	mainFrame.pack();
	//mainFrame.setSize(600,600);
   mainFrame.setSize(width, height);
	mainFrame.setVisible(!noDisplay);
	
	//Default view
	jc.draw(view);

    }

    public void close () {
	mainFrame.dispose();
	mainFrame.setVisible(false);
    }

    public void actionPerformed(ActionEvent ae) {
	String cmd = ae.getActionCommand();
	//I take the picture from the interactive canvas
	DrawingCanvas dc = jc.getDrawingCanvas();
   dc.setDrawLegend(true);
	dc.repaint();
	if (cmd.equals("Save as")) {
//modified code --------- rohit
	//	Result re = new Result(dc);
	//	re.setVisible(true);
		saveResults(jc,"png", new File("view.png"));
//end of modified code --------- rohit		
		
	}
	else if (cmd.equals("EPS")) {
	    	Result re = new Result(jc);
		re.setVisible(true);
		//saveResults (jc, "eps",new File("view.eps"));
	}
	//Introducded Code ------- Rohit
   /* Desactivated by OB / 10.11.2008 - probably not working
	else if (cmd.equals("save")) {
	
	    try {
        		BufferedWriter out = new BufferedWriter(new FileWriter("node.txt"));
		        out.write(jc.listNode());
			out.close();
        		out = new BufferedWriter(new FileWriter("elem.txt"));
			out.write(jc.listElem());
			out.close();
        		out = new BufferedWriter(new FileWriter("forces.txt"));
			out.write(jc.listLoad());
			out.close();
        		out = new BufferedWriter(new FileWriter("reactions.txt"));
			out.write(jc.listBoun());
		        out.close();
        		out = new BufferedWriter(new FileWriter("bars.txt"));
			out.write(jc.listBar());
		        out.close();
        		out = new BufferedWriter(new FileWriter("conc.txt"));
			out.write(jc.listConc());
			out = new BufferedWriter(new FileWriter("cables.txt"));
			out.write(jc.listCab());
		        out.close();
	    }
	    catch (IOException e) {
	    }
	} End of desactivated code - OB */
	//End of introduced code ----- Rohit
    }

    public void printResults (String root) {
       root += "_";
 	    try {
        		BufferedWriter out = new BufferedWriter(new FileWriter(root + "node.txt"));
	         out.write(jc.listNode().replaceAll("\n", "\r\n"));
            out.close();
        		out = new BufferedWriter(new FileWriter(root + "elem.txt"));
   			out.write(jc.listElem().replaceAll("\n", "\r\n"));
      		out.close();
            out = new BufferedWriter(new FileWriter(root + "forces.txt"));
            out.write(jc.listLoad().replaceAll("\n", "\r\n"));
            out.close();
        		out = new BufferedWriter(new FileWriter(root + "reactions.txt"));
   			out.write(jc.listBoun().replaceAll("\n", "\r\n"));
            out.close();
        		out = new BufferedWriter(new FileWriter(root + "bars.txt"));
         	out.write(jc.listBar().replaceAll("\n", "\r\n"));
		      out.close();
        		out = new BufferedWriter(new FileWriter(root + "conc.txt"));
            out.write(jc.listConc().replaceAll("\n", "\r\n"));
            out.close();
            out = new BufferedWriter(new FileWriter(root + "cables.txt"));
            out.write(jc.listCab().replaceAll("\n", "\r\n"));
		      out.close();
            out = new BufferedWriter(new FileWriter(root + "messages.txt"));
            out.write(Jconc.getMessages().replaceAll("\n", "\r\n"));
		      out.close();
	    }
	    catch (IOException e) {
	    }	

    }

    public void saveResults (String result, String fileType, String file) {
	//If no DrawingCanvas is provided I create one
	DrawingCanvas dc = new DrawingCanvas (model,600,600);
	dc.setSize(1000,1000);
	dc.draw(result);

	saveResults (dc, fileType, new File(file));
	  //  jc.draw("mesh");
	    //jc.actionPerformed(new ActionEvent(jc,2,result));
//	    saveResults(jc,fileType,new File(file));
    }


   public void saveResults (String result, double fact1, double fact2, String fileType, String file) {
	DrawingCanvas dc = new DrawingCanvas (model,600,600);
	dc.setSize(1000,1000);
   dc.setDrawLegend(false);
	dc.draw(result,fact1, fact2);
	saveResults (dc, fileType, new File(file));
    }


    public void saveResults (DrawingCanvas dc, String fileType, File file) {
		
	if (fileType.equals("png")) {
	    try {
		ImageIO.write(dc.getCanvasImage(), "png", file);	    
	    }
	    catch (IOException e) {
		System.err.println("Error saving png");
	    }
	}
	else if (fileType.equals("eps")) {
	    try {
		Properties p = new Properties();
		p.setProperty("PageSize","A5");
		VectorGraphics g = new PSGraphics2D
		    (file, new Dimension(dc.getWidth(),
					 dc.getHeight()));
		g.setProperties(p); 
		g.startExport(); 
		dc.print(g); 
		g.endExport();
	    }
	    catch (IOException e) {
		System.out.println(e);
		System.out.println("Error writing eps");
	    }
	}

    }
 
//Introduced Code ------- Rohit

    public void saveResults (Jconc jc, String fileType, File file) {
		
	DrawingCanvas dc = jc.getDrawingCanvas();
	Thickness tck = jc.getThickness();
	jc.validate();	
	Mates mt = jc.getMate();
	Gradient gd = jc.getGD();
	Plot plot = jc.getPlot();
	if (fileType.equals("png")) {
		   /* ImageIO.write(dc.getCanvasImage(), "png", file);*/
			jc.removeMenuBar();
			jc.validate();
		    ExportDialog export = new ExportDialog();

		    export.showExportDialog(jc,"Export view as.....", jc, "view.png");
		  //	export.selectFile();
	     	//	export.writeFile(dc, new ExportFileType());		   
			jc.addMenuBar();
			jc.validate();
	    		
	}
	else if (fileType.equals("eps")) {
	    try {
		Properties p = new Properties();
		p.setProperty("PageSize","A5");
		VectorGraphics g = new PSGraphics2D
		    (file, new Dimension(dc.getWidth(),
					 dc.getHeight()));
		g.setProperties(p); 
		g.startExport();

		dc.eraseLines();
	       	dc.print(g);
		if(jc.getDisplayState()==2){
			tck.eraseLine();
			tck.print(g);
			g.endExport();
			tck.drawLine();
		}
		else if(jc.getDisplayState()==3){
			mt.eraseLine();
			mt.print(g);
			g.endExport();
			mt.drawLine();
		}
		else if(jc.getDisplayState()==4){
			gd.eraseLine();
			gd.print(g);
			g.endExport();
			gd.drawLine();
		}
		else if(jc.getDisplayState()==5){
			plot.eraseLine();
			plot.print(g);
			g.endExport();
			plot.drawLine();
		}
		dc.drawLines();
	    }
	    catch (IOException e) {
		System.out.println(e);
		System.out.println("Error writing eps");
	    }
	}

    }

    //End of introduced code
//modified code ------- rohit
	public class Result extends Frame implements WindowListener, ActionListener	{

		private String fileName;
		private Button button1;
		private TextArea ta;
		private Jconc jc;


		public Result(Jconc jc) {
	
			setTitle("Enter file name");
			setSize(200,100);
			setLocation(600, 100);
			setVisible(true);
			addWindowListener(this);
			this.jc = jc;
			ta = new TextArea(fileName,5,10);	
			fileName = null;
			add(ta);
			button1 = new Button("OK");       //button for saving the eps and png files under a new name
			button1.setActionCommand("ok");
			button1.addActionListener(this);
			Panel p = new Panel();
			p.add(button1);
			add("South",p);

		}
		public void actionPerformed (ActionEvent ae) {
		
			String command = ae.getActionCommand ();
			if(command.equals("ok")) {
				fileName = ta.getText();
				if(fileName.equals(""))
					fileName="view.eps";
				setVisible(false);
				//System.out.println("File name is : "+fileName);
				saveResults (jc, "eps",new File(getFilename()));
			}
		}
		public String getFilename() {
		
			return fileName;
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
//end of modified code ----------  rohit	

}
