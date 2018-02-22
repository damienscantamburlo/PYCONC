package net.cercis.jconc.ui;

import net.cercis.jconc.fem.*;

import java.applet.*;
import javax.swing.JApplet;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.JSeparator;
import javax.swing.JFrame;

import java.applet.Applet;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Button;
import java.awt.Panel;
import java.awt.GraphicsConfiguration;
import java.awt.Label;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.FlowLayout;
import java.awt.Polygon;
import java.awt.Dimension;
import java.applet.Applet;
import java.io.File;
import java.awt.BorderLayout;
import java.awt.Frame;	
import java.awt.TextArea;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.StringTokenizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.text.NumberFormat;
import java.text.DecimalFormat;

public class Jconc extends JApplet 
    implements ActionListener, MouseMotionListener,
	       MouseListener, MouseWheelListener, WindowStateListener {
    private static int digits = 5; // for the display of real numbers
    private static String version = "1.13";
    private DrawingCanvas dc;
    private Thickness tck;
    private static int strokeWidth = 0;
    private Mates mt;
    private Gradient gd;
    private String defaultAppletView;
    private Model model = null;
    private int x0 = 0;
    private int y0 = 0;
    public JMenuBar jmenubar;
    public JMenu state, options, listing, file, info;
    private int buttonNumber;
    private int displayState;
    private Viewer viewer;
    private Plot p;
    private GetFileList gf;
    private String urlBase;
    private String userID;
    private FilePanel fd;
    private String ip;
    private String fileName;
    private int thisInstance;
    private static int instance = 0;
    private String autoSave;
    public static StringFile sf;
    private static String curLang;
    public static int appApp = 1; //application = 0, Applet = 1. This value determines whether this is a application or Applet.
    private static double minStressLevel = 0;
    private static boolean noShade = false;
    private static int maxLenVec = -1;
    private static String messages = "";
	
    /*
     * displayState values :
     *
     * 1 - Display has neither the Thickness bar nor the Materials bar nor the Gradient Bar
     * 2 - Display has the Thickness bar
     * 3 - Display has the Materials bar
     * 4 - Display has the Gradient bar
     * 5 - Display has the Plot bar
     *
     */

    //Applet started with a model
    public Jconc (Model model, Viewer viewer) {

//	instance++;
//	thisInstance = instance;
 	this.model = model;
	this.viewer = viewer;
	displayState = 1;
	init();
     }
    public static void setLang(String lang){
    
    	curLang = lang;
    }

    public static void setStrokeWidth (int w) {
       strokeWidth = w;
    }

    public static int getStrokeWidth (){
       return strokeWidth;
    }

    public static void setMinStressLevel(double minStressLevel){
       Jconc.minStressLevel = minStressLevel;
    }

    public static double getMinStressLevel() {
       return minStressLevel;
    }

    public static void setNoShade (boolean noShade) {
       Jconc.noShade = noShade;
    }

    public static boolean getNoShade() {
       return noShade;
    }

    public static void setMaxLenVec(int maxLenVec) {
       Jconc.maxLenVec = maxLenVec;
    }

    public static int getMaxLenVec() {
       return maxLenVec;
    }

    public static String getVersion () {
       return version;
    }

    public static void setMessages(String s) {
       messages = s;
    }

    public static String getMessages() {
       return messages;
    }

    public static String memStatus() {
      String s = "";
      Runtime runtime = Runtime.getRuntime();

      double maxMemory = 0.01 * (int)(runtime.maxMemory() / 1024 / 1024 * 100);
      double allocatedMemory = 0.01 * (int)(runtime.totalMemory() / 1024 / 1024 * 100);
      double freeMemory = 0.01 * (int)(runtime.freeMemory() / 1024 / 1024 * 100);

      //s += "Free memory:       " + freeMemory + " MB\n";
      //s += "Allocated memory:  " + allocatedMemory + " MB\n";
      //s += "Max memory:        " + maxMemory + " MB\n";
      s += "Total free memory: " + (freeMemory + maxMemory - allocatedMemory) + " MB\n";
      return s;
    }
    public void printAllResults (String root) {
       root += "_";
       String s = getAllResults();
 	    try {
        		BufferedWriter out = new BufferedWriter(new FileWriter(root + "all.txt"));
            out.write(s);
		      out.close();
	    }
	    catch (IOException e) {
	    }
    }
    public String getAllResults () {
       String s = "";
       s += Jconc.getMessages();
       s += "\n" + listLoad();
       s += "\n" + listBoun();
       s += "\n" + listNode();
       s += "\n" + listElem();
       s += "\n" + listBar();
       s += "\n" + listConc();
       s += "\n" + listCab();
       return s.replaceAll("\n", "\r\n");
    }

    public static int getDigits() {
       return digits;
    }
    public Jconc () {
    }

    public int getDisplayState() {
    	return displayState;
    }

    public void init() {


	instance++;
	try{
	
		getAppletContext();
		appApp = 1;
	}
	catch(NullPointerException e){
	
		appApp = 0;
	}
	thisInstance = instance;
//	curLang = "English";

	if (model == null) {
	//	System.out.println("Value of appApp "+appApp);

	    if(appApp == 1){
		    
    		 urlBase = getCodeBase().toString();
		    userID = this.getParameter("USERID");
		    gf = new GetFileList(urlBase);
		    autoSave = this.getParameter("AUTOSAVE");
		    curLang = this.getParameter("LANGUAGE");
	    }
	    else if(appApp == 0){
		    urlBase = "";
		    userID = "";
		    curLang = "English";
	    }
	    Launcher launcher;
	    try{
		    if(appApp == 1){
			//    System.out.println(gf.readURLFile());
			    StringFile tmpSF = new StringFile(gf.readURLFile(),"[");
			    sf = new StringFile(tmpSF.getStringBlock(curLang));
		//	System.out.println("Blocks length is "+sf.countBlock());

		    }
		    else if (appApp == 0){
		    
			    BufferedReader is = new BufferedReader(new FileReader("text.ini"));
             
			    String data = "";
    			    int count = 0;
    			    int i = 0;
			    byte[] b = new byte[1000]; // max 1000 caracteres par ligne...
			    int c = 0;
			    while ( (c = is.read()) > -1) {
				    switch (c) {
					    case '\r':
						    break; // sauter, pour ne pas faire doublon avec les \n

					    case '\n': // fin de ligne : concatener le tampon b avec data
						    b[i++] = (byte) c;
						    String s = new String(b);
						    data += s.substring(0, i);	
    						    b = new byte[1000];
						    i = 0;
						    break;
					    default:	
    						    b[i++] = (byte) c;
						    break;
				    }
				    count++;
			    }							    
			    if(is!=null) is.close();   
			    StringFile tmpSf = new StringFile(data,"[");

			    sf = new StringFile(tmpSf.getStringBlock(curLang));
                            

		    }
	    }
	    catch(Exception e){
	        String temp = "** Toolbar buttons\n*THICKNESS=Thickness,Thick.\n*MATERIAL=Material\n*LISTING=Listing\n*FILE=File\n*MESH=Mesh\n*MODEL=Model\n*THICKBAR=Thickness Bar,Mesh - Displaying thickness of Bars : using Thickness\n*THICKSOL=Thickness Solid,Mesh - Displaying thickness of Solids\n*COLOR=Color\n*MATSOL=Material Solid,Mesh - Displaying materials of Solids\n*MATBAR=Material Bar,Mesh - Displaying materials of Bars\n*SELECT=Select Element/Elements\n*OPEN=Open...,Open a file\n*SAVE=Save...,Save a file\n*DEFO=Deformed\n*RELSTRESS=Relative Stresses\n*CONCSTRESS=Concrete relative Stresses\n*VARTHICK=Varying Thickness\n*VARLEN=Varying Length\n*VARLENGRAD=Varying length with gradient\n*SMOOTH=Smooth\n*DISCRETE=Discrete\n*STEELSTR=Steeel Relative Stresses\n*ETA2=Eta 2\n*ETA2GRAD=Eta 2 with gradient\n*CONCPLOT=Concrete Plots\n*BARPLOT=Bar Plots\n*CABPLOT=Cable Plots\n*INPUT=Input...,Input File,Generate from Input\n*STFOR=Steel Forces\n*CABFOR=Cable Forces\nSTRA1=Strain 1\nSTRA2=Strain 2\n*EPS1=Epsilon 1\n*EPS2=Epsilon 2\n*STR1=Stress 1\n*STR2=Stress 2\n*STRAIN=Strain\n*STRESS=Stress\n*FORCE=Forces\n*NODES=Nodes\n*ELEMS=Elements\n*REACTIONS=Reactions\n*BAR=Bar\n*CABLE=Cable\n*CONCRETE=Concrete\n*RESULTS=Results\n*STRA1CONC=Strain 1 Concrete\n*STRA2CONC=Strain 2 Concrete\n*STRABAR=Strain Bars\n*STREBAR=Stress Bars\n*FORBAR=Force Bars\n*STRACAB=Strain Cables\n*STRECAB=Stress Cables\n*FORCAB=Force Cables\n*STR1CONC=Stress 1 Concrete\n*STR2CONC=Stress 2 Concrete\n*ELEMNUM=Enter Element Number\n*TYPE=Type\n*SIGMA1=Sigma 1\n*SIGMA2=Sigma 2\n*ALPHA=Alpha\n*TOTAL=Total\n\n** MESSAGES\n*MSGLOGIN=You are not properly logged in! Please login from another browser or window!\n*MSGINPUT=Wrong Input\n*MSGINT=The element which you have entered is not integer\n*MSGOVERWRITE=Are you sure you want to overwrite the file?\n*MSGAUTH=You are not authorized to use this file name!\n\n** LISTINGS\n*LISTNODE=Listing of Nodes\n*LISTELEM=Listing of Elements\n*LISTFOR=Listing of Forces\n*LISTREAC=Listing of Reactions\n*LISTBAR=Listing of Bar Forces\n*LISTCONC=Listing of Concrete Forces\n*LISTCAB=Listing of  Cable Forces\n";
	    	sf = new StringFile(temp);
			System.out.println("Problem reading the text.ini file");
	    }
	   // System.out.println("FORBAR is "+sf.getValue("FORBAR",1));
		
	    if(appApp == 1){
		    if(this.getParameter("INPUTFILE").length() == 0){
			    launcher = new Launcher(this.getParameter("INPUTDATA"));
		    }
		    else{
			    launcher = new Launcher(gf.readFile("file","iConc",this.getParameter("INPUTFILE")));
			    fileName = this.getParameter("INPUTFILE");
		    }
	    }
	    else 
		    launcher = new Launcher(this.getParameter("INPUTDATA"));
	    
	    try {
		    
		    ip=InetAddress.getLocalHost().getHostAddress();
		   
	    }
	    catch(UnknownHostException ex) {
		    
	    } 
	    defaultAppletView = launcher.getDefaultAppletView();
	    model = launcher.getModel();
	}
	try{
		if (appApp == 0){
			
		/*	FileInputStream is = new FileInputStream("text.ini");
			System.out.println("Reading the ini file");
			String data = "";
			int count = 0;
			int i = 0;
			byte[] b = new byte[1000]; // max 1000 caracteres par ligne...
			int c = 0;
			while ( (c = is.read()) > -1) {
				switch (c) {
					case '\r':
						break; // sauter, pour ne pas faire doublon avec les \n
						
					case '\n': // fin de ligne : concatener le tampon b avec data					b[i++] = (byte) c;
						
						String s = new String(b);
						data += s.substring(0, i);	
						b = new byte[1000];
						i = 0;
						break;
				default:	
						b[i++] = (byte) c;
						break;
				}
				
				count++;
			}							    
			if (is != null) is.close();  
		       // System.out.println(data);*/
		   // BufferedReader br = new BufferedReader(new FileReader(iniFileName));
         InputStream is = getClass().getResourceAsStream("/net/cercis/text.ini");
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);

			String str = "";
			String string = br.readLine();
         string = string.substring(1,string.length()); // remove first character which seems wrong...
         //System.out.println(string);
			while(string!=null){
			
			//	System.out.print(string);
				str += string+'\n';
				string = br.readLine();
			}
			//System.out.println(str);

//		       	BufferedWriter out = new BufferedWriter(new FileWriter("text.txt"));
//			out.write(str);
//			out.close();
			StringFile tmpSf = new StringFile(str,"[");
			//System.out.println("language is "+curLang);
			
			sf = new StringFile(tmpSf.getStringBlock(curLang));
		//	System.out.println("Blocks length is "+sf.countBlock());
		//	System.out.println("Open is "+sf.getValue("OPEN",1));
		}
	}
	catch(FileNotFoundException e){
	
		System.out.println("Ini file not found");
	}
	catch(IOException e){
		System.out.println("IO Error");
	}
	loadUserInterface();
    }

    public void start () {
	dc.draw(defaultAppletView);
	//tck.display();
	repaint();
    }

    public Thickness getThickness() {
    
    	return tck;
    }

    public Mates getMate() {
    
    	return mt;
    }
    public Gradient getGD(){
    
    	return gd;
    }

    public Plot getPlot(){
    
    	return p;
    }

   public static String formatNumber (double num, int pos) {
       String s = "unknown";
       double val = num;
       double mant = num;
       int aux = 0;
       if (num == 0) return "0.0";
       String nfString = "0.";
       for (int i = 0; i < pos - 1; i++)
          nfString += "0";
       nfString += "E0";
       NumberFormat nf = new DecimalFormat(nfString);
       s = nf.format(num);
       /*
       int pow = (int) Math.log10(Math.abs(num));
       mant = num / Math.pow(10,pow);
       //if (Math.signum(num) < 0) sign +=1;
       //if ((Math.abs(num) > Math.pow(10,pos)) || (Math.abs(num) < Math.pow(10,-pos))) {
       if ((Math.abs(num) > Math.pow(10,pos-3)) || (Math.abs(num) < Math.pow(10,-pos-2))) {
          // scientific display necessary 
          s = nf.format(num);
       }
       else {
          val = Math.round(mant * Math.pow(10, pos - 2));
          val /= Math.pow(10, pos-2);
          val = val * Math.pow(10, pow);
          s = Double.toString(val);
          if (s.contains("E"))
             s = nf.format(num);
          else {
             if (s.contains(".")) aux+=1;
             if (s.contains("-")) aux+=1;
             if (s.length() > pos+aux) s = s.substring(0, pos+aux);
          }
       }
       */
       return s;
    }

    public void addMenuBar() {

       jmenubar.add(file);
       jmenubar.add(state);
       jmenubar.add(options);
       jmenubar.add(listing);
       jmenubar.add(info);
       jmenubar.setBorder(BorderFactory.createLineBorder(Color.black));

    }

    public void removeMenuBar() {
    
	jmenubar.remove(options);
	jmenubar.remove(state);
	jmenubar.remove(file);
	jmenubar.remove(listing);
	jmenubar.remove(info);
	jmenubar.setBorder(BorderFactory.createLineBorder(Color.black));

    }

    private void loadUserInterface () {

    JMenuItem button2,button3,button5,eta2,button7,button8,button9, eta2GradS, eta2GradD, stressLenBar, eps1, eps2;
    JMenuItem epsBar, s1Bar, forBar, epsCab, s1Cab, forCab, s1Conc, s2Conc;
    JMenu thick, mate,button4, jradioThickBar,button6, eta2Grad, concPlot, barPlot, cabPlot;
    JMenuItem jradioThickSol, jradioThickBarCol, jradioThickBarTck, jradioMateSol, jradioMateBar, jradioBoth, stressLength, stressThick, stressLenBarS, stressLenBarD;
    JMenuItem node, elem, force, reac, bar, conc, cab, msg, listall;
    JMenuItem help;
    JSeparator separate, separate2;
    JMenuItem select;

    JMenuItem open, save;

	//addWindowStateListener(this);
	addMouseListener(this);
	addMouseMotionListener(this);
	addMouseWheelListener(this);
	getContentPane().setLayout(new BorderLayout(0,0));
	setBackground(Color.gray);

	//GetFilelist
	if(appApp == 1)
		gf = new GetFileList(urlBase);
//Introduced code ------------ Rohit

	jmenubar = new JMenuBar();
	state = new JMenu();
	thick = new JMenu(sf.getValue("THICKNESS",1));
	mate = new JMenu(sf.getValue("MATERIAL",1));
	listing = new JMenu(sf.getValue("LISTING",1));
	file = new JMenu(sf.getValue("FILE",1));
   info = new JMenu("Jconc " + version);

	jradioThickSol = new JMenuItem();
	jradioThickBar = new JMenu();
	jradioMateSol = new JMenuItem();
	jradioMateBar = new JMenuItem();
	jradioBoth = new JMenuItem(sf.getValue("MESH",1));
	state.setText(sf.getValue("MODEL",1));

	jradioThickSol.setText(sf.getValue("THICKSOL",1));
	jradioThickSol.setActionCommand("thick_sol");
	jradioThickSol.addActionListener(this);

	jradioThickBar.setText(sf.getValue("THICKBAR",1));

	jradioThickBarTck = new JMenuItem(sf.getValue("THICKNESS",1));
	jradioThickBarTck.setActionCommand("thick_bar");
	jradioThickBarTck.addActionListener(this);

	jradioThickBarCol = new JMenuItem(sf.getValue("COLOR",1));
	jradioThickBarCol.setActionCommand("thick_barcol");
	jradioThickBarCol.addActionListener(this);

	jradioThickBar.add(jradioThickBarTck);
	jradioThickBar.add(jradioThickBarCol);

	thick.add(jradioThickSol);
	thick.add(jradioThickBar);

	mate.add(jradioMateSol);
	mate.add(jradioMateBar);

	jradioMateSol.setText(sf.getValue("MATSOL",1));
	jradioMateSol.setActionCommand("material_sol");
	jradioMateSol.addActionListener(this);

	jradioMateBar.setText(sf.getValue("MATBAR",1));
	jradioMateBar.setActionCommand("material_bar");
	jradioMateBar.addActionListener(this);

	jradioBoth.setActionCommand("both");
	jradioBoth.addActionListener(this);

	select = new JMenuItem(sf.getValue("SELECT",1));
	select.setActionCommand("select");
	select.addActionListener(this);

	state.add(thick);
	state.add(mate);
	state.add(jradioBoth);
	state.add(select);


	open = new JMenuItem(sf.getValue("OPEN",1));
	open.setActionCommand("open");
	open.addActionListener(this);

	save = new JMenuItem(sf.getValue("SAVE",1));
	save.setActionCommand("save");
	save.addActionListener(this);

	file.add(open);
	file.add(save);


//End of introduced code ------- Rohit
//	add("North",new Label("JConc 0.99"));
	//button1 = new JRadioButtonMenuItem("Mesh", true);        //NAME
	//button1.setActionCommand("drawMesh");//ACTION
	//button1.addActionListener(this);

	button2 = new JMenuItem(sf.getValue("DEFO",1));       //NAME
	button2.setActionCommand("drawDefMesh");//ACTION
	button2.addActionListener(this);

	button3 = new JMenuItem(sf.getValue("RELSTRESS",1));        //NAME
	button3.setActionCommand("drawStress");//ACTION
	button3.addActionListener(this);

	button4 = new JMenu(sf.getValue("CONCSTRESS",1));    //NAME
	//stressThick = new JMenuItem(sf.getValue("VARTHICK",1));
	//stressThick.setActionCommand("concrete_thick");
	//stressThick.addActionListener(this);
	stressLength = new JMenuItem(sf.getValue("VARLEN",1));
	stressLength.setActionCommand("concrete_len");
	stressLength.addActionListener(this);
	stressLenBar = new JMenu(sf.getValue("VARLENGRAD",1));

	stressLenBarS = new JMenuItem(sf.getValue("SMOOTH",1));
	stressLenBarS.addActionListener(this);
	stressLenBarS.setActionCommand("concrete_lenbarS");

	stressLenBarD = new JMenuItem(sf.getValue("DISCRETE",1));
	stressLenBarD.setActionCommand("concrete_lenbarD");
	stressLenBarD.addActionListener(this);

	stressLenBar.add(stressLenBarS);
	stressLenBar.add(stressLenBarD);

	button4.add(stressLength);
	//button4.add(stressThick);
	button4.add(stressLenBar);

	button5 = new JMenuItem(sf.getValue("STEELSTR",1));    //NAME
	button5.setActionCommand("drawCables");//ACTION
	button5.addActionListener(this);

	button6 = new JMenu(sf.getValue("ETA2",1));    //NAME

	eta2 = new JMenuItem(sf.getValue("ETA2",1));
	eta2.setActionCommand("drawEta2");//ACTION
	eta2.addActionListener(this);

	eta2Grad = new JMenu(sf.getValue("ETA2GRAD",1));

	concPlot = new JMenu (sf.getValue("CONCPLOT",1));

	barPlot = new JMenu (sf.getValue("BARPLOT",1));

	cabPlot = new JMenu (sf.getValue("CABPLOT",1));

	eta2GradS = new JMenuItem(sf.getValue("SMOOTH",1));
	eta2GradS.setActionCommand("drawEta2GradS");//ACTION
	eta2GradS.addActionListener(this);
	
	eta2GradD = new JMenuItem(sf.getValue("DISCRETE",1));
	eta2GradD.setActionCommand("drawEta2GradD");//ACTION
	eta2GradD.addActionListener(this);

	eta2Grad.add(eta2GradS);
	eta2Grad.add(eta2GradD);

	button6.add(eta2);
	button6.add(eta2Grad);

	button7 = new JMenuItem(sf.getValue("INPUT",1));    //NAME
	button7.setActionCommand("getInputFile");//ACTION
	button7.addActionListener(this);

	button8 = new JMenuItem(sf.getValue("STFOR",1));
	button8.setActionCommand("forceBar");
	button8.addActionListener(this);

	button9 = new JMenuItem(sf.getValue("CABFOR",1));
	button9.setActionCommand("forceCable");
	button9.addActionListener(this);

	eps1 = new JMenuItem(sf.getValue("EPS1",1));
	eps1.setActionCommand("eps1");
	eps1.addActionListener(this);

	eps2 = new JMenuItem (sf.getValue("EPS2",1));
	eps2.setActionCommand("eps2");
	eps2.addActionListener(this);

	s1Conc = new JMenuItem (sf.getValue("STR1",1));
	s1Conc.setActionCommand("s1Conc");
	s1Conc.addActionListener(this);

	s2Conc = new JMenuItem (sf.getValue("STR2",1));
	s2Conc.setActionCommand("s2Conc");
	s2Conc.addActionListener(this);

	epsBar = new JMenuItem (sf.getValue("STRAIN",1));
	epsBar.setActionCommand("epsBar");
	epsBar.addActionListener(this);

	s1Bar = new JMenuItem (sf.getValue("STRESS",1));
	s1Bar.setActionCommand("s1Bar");
	s1Bar.addActionListener(this);

	forBar = new JMenuItem (sf.getValue("FORCE",1));
	forBar.setActionCommand("forBar");
	forBar.addActionListener(this);

	barPlot.add(epsBar);
	barPlot.add(s1Bar);
	barPlot.add(forBar);

	epsCab = new JMenuItem (sf.getValue("STRAIN",1));
	epsCab.setActionCommand("epsCab");
	epsCab.addActionListener(this);

	s1Cab = new JMenuItem (sf.getValue("STRESS",1));
	s1Cab.setActionCommand("s1Cab");
	s1Cab.addActionListener(this);

	forCab = new JMenuItem (sf.getValue("FORCE",1));
	forCab.setActionCommand("forCab");
	forCab.addActionListener(this);

	cabPlot.add(epsCab);
	cabPlot.add(s1Cab);
	cabPlot.add(forCab);

	node = new JMenuItem(sf.getValue("NODES",1));
	node.setActionCommand("nodes");
	node.addActionListener(this);

	elem = new JMenuItem(sf.getValue("ELEMs",1));
	elem.setActionCommand("elems");
	elem.addActionListener(this);

	force = new JMenuItem(sf.getValue("FORCE",1));
	force.setActionCommand("forces");
	force.addActionListener(this);

	reac = new JMenuItem(sf.getValue("REACTIONS",1));
	reac.setActionCommand("reactions");
	reac.addActionListener(this);

	bar = new JMenuItem(sf.getValue("BAR",1));
	bar.setActionCommand("bars");
	bar.addActionListener(this);

	cab = new JMenuItem(sf.getValue("CABLE",1));
	cab.setActionCommand("cab");
	cab.addActionListener(this);

	conc = new JMenuItem(sf.getValue("CONCRETE",1));
	conc.setActionCommand("conc");
	conc.addActionListener(this);

	msg = new JMenuItem(sf.getValue("MESSAGES",1));
	msg.setActionCommand("messages");
	msg.addActionListener(this);

	listall = new JMenuItem(sf.getValue("LISTALL",1));
   listall.setActionCommand("listall");
	listall.addActionListener(this);

   separate = new JSeparator();
	separate2 = new JSeparator();

	listing.add(node);
	listing.add(elem);
	listing.add(force);
	listing.add(separate);
	listing.add(conc);
	listing.add(bar);
	listing.add(cab);
	listing.add(reac);
	listing.add(separate2);
	listing.add(msg);
	listing.add(listall);
   

	help = new JMenuItem(sf.getValue("HELP",1));
	help.setActionCommand("help");
	help.addActionListener(this);
   info.add(help);


	options = new JMenu(sf.getValue("RESULTS",1));
	options.add(button2);
	options.add(button3);
	options.add(button4);
	options.add(button5);
	options.add(button8);
	options.add(button9);
	concPlot.add(eps1);
	concPlot.add(eps2);
	concPlot.add(s1Conc);
	concPlot.add(s2Conc);
	options.add(concPlot);
	options.add(barPlot);
	options.add(cabPlot);
	options.add(button6);
	state.add(button7);
	jmenubar.add(file);
	jmenubar.add(state);
	jmenubar.add(options);
	jmenubar.add(listing);
	jmenubar.add(info);
	jmenubar.setBackground(Color.white);
	jmenubar.setBorder(BorderFactory.createLineBorder(Color.black));
	setJMenuBar(jmenubar);

	int width = this.getWidth();
	int height = this.getHeight();
	tck = new Thickness(model);
	mt = new Mates(model);
	gd = new Gradient("Sigma", false);
	gd.setMinimumSize(new Dimension(100,this.getHeight()-60));
	gd.setPreferredSize(new Dimension(100,this.getHeight()-60));
	gd.setMaximumSize(new Dimension(100, this.getHeight()-60));
	dc = new DrawingCanvas (model,width,height, tck, mt);
	dc.setPreferredSize(new Dimension(this.getWidth()-100, this.getHeight()-60));
	dc.setMinimumSize(new Dimension(this.getWidth()-100, this.getHeight()-60));
	dc.setMaximumSize(new Dimension(this.getWidth()-100, this.getHeight()-60));
	tck.setMinimumSize(new Dimension(100,this.getHeight()-60));
	tck.setPreferredSize(new Dimension(100,this.getHeight()-60));
	tck.setMaximumSize(new Dimension(100, this.getHeight()-60));
	mt.setMinimumSize(new Dimension(100,this.getHeight()-60));
	mt.setPreferredSize(new Dimension(100,this.getHeight()-60));
	mt.setMaximumSize(new Dimension(100, this.getHeight()-60));
	p = new Plot(dc.getValue(1),dc.getCount(1));
	p.setMinimumSize(new Dimension(100,this.getHeight()-60));
	p.setPreferredSize(new Dimension(100,this.getHeight()-60));
	p.setMaximumSize(new Dimension(100, this.getHeight()-60));
	getContentPane().add(dc, BorderLayout.CENTER);
	}

    public Model getModel() {
    
    	return this.model;
    }

    public void updateModel(Model model) {
	
	this.model = model;    
    }

    public void updateDrawingCanvas() {
    
    	dc.updateModel(model);
	tck.updateModel(model);
	mt.updateModel(model);
    }

    public String listNode() {
    
    	return dc.listNode(model.getMapOfNodes());
    }

    public String listElem() {
    
    	return dc.listElem(model.getMapOfElems());
    }
	    
    public String listLoad() {
    
    	return dc.listLoad(model.getMapOfLoads());
    }

    public String listBoun() {
    
    	return dc.listBoun(model.getMapOfBouns());
    }
    public String listBar() {
    
    	return dc.listBar(model.getMapOfElems());
    }
    public String listCab() {
    
    	return dc.listCab(model.getMapOfElems());
    }
    public String listConc() {
    
    	return dc.listConc(model.getMapOfElems());
    }
    public DrawingCanvas getDrawingCanvas () {
	return dc;
    }

    public void draw (String drawType) {
	dc.draw(drawType);
    }

    private String fileString () {
	String file = "";
	file+= "version,"+model.getVer()+"*"+'\n';
	file+="steps,"+model.getSteps()+"*"+'\n';
        file += "scaleforces," + model.getScaleForces() + "*\n\n";
        int numPos = 8; // number of characters user to output real numbers
        
	Map mMates = model.getMapOfMates();
	Iterator itMates = ((Collection) mMates.values()).iterator();
	while (itMates.hasNext()) {
	    Material mi = (Material) itMates.next();
            String matName = mi.type();
	    file = file + "m," + mi.id() + "," + matName + ","
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
                  file += "," + ms.modHard();
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
	    file += "*\n";
	}
        file += "\n";
	Map mBouns = model.getMapOfBouns();
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {
	    Boun bi = (Boun) itBouns.next();
	    file = file + "b," + bi.node() + "," + bi.type() + ","
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
	       file = file + "f," + li.node() + ","
                       + Jconc.formatNumber(li.fxInput() / scalLoc, numPos) + ","
                       + Jconc.formatNumber(li.fyInput() / scalLoc, numPos) + "*\n";
            }
	}
        file += "\n";
	Map mNodes = model.getMapOfNodes();
	Iterator itNodes = ((Collection) mNodes.values()).iterator();
	while (itNodes.hasNext()) {
	    Node ni = (Node) itNodes.next();
	    file = file + "n," + ni.id() + ","
                    + Jconc.formatNumber(ni.x(), numPos) + ","
                    + Jconc.formatNumber(ni.y(), numPos) + "*\n";
	}
        file += "\n";
	Map mElems = model.getMapOfElems();
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equalsIgnoreCase("Conc3N")) {
		file = file + "e," + ei.id() + ",conc3N," + 
		    ei.getNode(0) + "," + ei.getNode(1) + "," + ei.getNode(2)
		    + "," +  Jconc.formatNumber(ei.parameter(1), numPos) + "," +  
		    ei.material() + "*";
			file += '\n';//modified code
	    }
	    else if (ei.type().equalsIgnoreCase("Bar")) {
		file = file + "e," + ei.id() + ",bar," + 
		    ei.getNode(0) + "," + ei.getNode(1)
		    + "," +  Jconc.formatNumber(ei.parameter(1), numPos) + "," +
		    ei.material() + "*";
			file += '\n';//modified code
	    }
	    else if (ei.type().equalsIgnoreCase("Cable")) {
               Cable c = (Cable) ei;
               file = file + "e," + ei.id() + ",cable," +
               ei.getNode(0) + "," + ei.getNode(1) + "," +
               Jconc.formatNumber(ei.parameter(1), numPos) + "," +
               ei.material() + "*";
               file += '\n';
	    }
	}	
	return file;
    }
    
    public void actionPerformed (ActionEvent ae) {
	String cmd = ae.getActionCommand();
	
	if (cmd.equals("open")) {
	    dc.setState(0);
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }
       boolean canConnect = false;
       try {
          gf.isConnect();
          canConnect = true;
       }
       catch (Exception e) {
          canConnect = false;
       }
       if(!canConnect){
         String []str = new String[1];
          str[0]=sf.getValue("MSGLOGIN",1)+"\n"+sf.getValue("MSGLOGIN",2);
          fd = new FilePanel(Frame.getFrames()[0],str, 1, 0, "None", 3);

       }
       else{

          if(gf.getFileList(0,"iConc")[0].equals("10")){

             String []str = new String[0];
                //   str[0]=sf.getValue("MSGLOGIN",1)+"\n"+sf.getValue("MSGLOGIN",2);
             //   fd = new FilePanel(Frame.getFrames()[0],str, 1, 0, "None", 3);
             SelFile sf = new SelFile(str, 0,0);
          }
          else{
             SelFile sf = new SelFile(gf.getFileList(0,"iConc"), gf.getLength(),0);

          }
       /*fd = new FileDB(gf.getFileList(0,"iConc"),gf.getLength());
       while(fd.flag){
          System.out.println(fd.flag);
       }*/
       }
       
       dc.drawLine();

	}
	if (cmd.equals("save")) {
	    dc.setState(0);
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }
       boolean canConnect = false;
       try {
          gf.isConnect();
          canConnect = true;
       }
       catch (Exception e) {
          canConnect = false;
       }
	    if(!canConnect){
		    String []str = new String[1];
		    str[0]=sf.getValue("MSGLOGIN",1)+"\n"+sf.getValue("MSGLOGIN",2);
		    fd = new FilePanel(Frame.getFrames()[0],str, 1, 0, "None", 3);
	    	
	    }
	    else{
		    if(gf.getFileList(0,"iConc")[0].equals("10")){
			    String []str = new String[0];
		    	    //	    str[0]=sf.getValue("MSGLOGIN",1);
	    		    SelFile sf = new SelFile(str, 0,1);
		    }
		    else{
	 		    // System.out.println("Entered false part of saving");
			    SelFile sf = new SelFile(gf.getFileList(0,"iConc"), gf.getLength(),1);
		    }
		    /*gf.parameters(ip,"input3","iConc","1.10",false,false,true);
		     * 	    gf.write(fileString());*/
    	    }
	    dc.drawLine();
	}
	if (cmd.equals("drawMesh")) {
	    dc.setState(0);
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }
	    dc.drawLine();
	    dc.updateTitle(sf.getValue("MESH",1));

	    validate();
    			
	    dc.draw("mesh");
	}
	else if (cmd.equals("drawDefMesh")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    dc.updateTitle(sf.getValue("DEFO",1)+" "+sf.getValue("MESH",1));

	    validate();
	    
	    dc.drawLine();
	    dc.draw("defo");

	}	/*
	else if (cmd.equals("drawStress")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
		remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    validate();
	    dc.updateTitle(sf.getValue("RELSTRESS",1));

	    dc.drawLine();
	    dc.draw("stress");

	}	*/
	else if (cmd.equals("drawStress")) {
	    dc.setState(0);
	    if (displayState == 2) {
	    	remove(tck);
         displayState = 1;
	    }
	    else if (displayState == 3) {
         remove(mt);
		   displayState = 1;
	    }
	    else if (displayState == 4) {
         remove(gd);
		   displayState = 1;
       }
	    else if (displayState == 5) {
          remove(p);
		   displayState = 1;
       }
	    validate();
	    dc.updateTitle(sf.getValue("RELSTRESS",1));

       dc.drawLine();
	    dc.draw("stress");
	}

	else if (cmd.equals("concrete_thick")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
 	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    validate();
	    dc.updateTitle(sf.getValue("CONCSTRESS",1)+" - "+sf.getValue("VARTHICK",1));


	    dc.drawLine();

	    dc.draw("stressConcThick");
	}	
	else if (cmd.equals("concrete_len")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	   	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    validate();
	    dc.updateTitle(sf.getValue("CONCSTRESS",1)+" - "+sf.getValue("VARLEN",1));

	    dc.drawLine();
	    dc.draw("stressConcLen");
	}	
	else if (cmd.equals("concrete_lenbarS")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	   
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    gd.setType(false);
	    gd.setSmooth(true);
	    getContentPane().add(gd,BorderLayout.EAST);
	    dc.eraseLine();
	    displayState = 4;
	    validate();
	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    dc.updateTitle(sf.getValue("CONCSTRESS",1)+" - "+sf.getValue("VARLEN",1));
	    dc.draw("stressConcLen");
	}	
	else if (cmd.equals("concrete_lenbarD")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	   
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    gd.setType(false);
	    gd.setSmooth(false);
	    getContentPane().add(gd,BorderLayout.EAST);
	    dc.eraseLine();

	    displayState = 4;
	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    validate();
	    dc.updateTitle(sf.getValue("CONCSTRESS",1)+" - "+sf.getValue("VARLEN",1));
	    dc.draw("stressConcLen");
	}	
	else if (cmd.equals("drawCables")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    validate();
	    dc.updateTitle(sf.getValue("STEELSTR",1));
	    dc.drawLine();
	    dc.draw("stressCables");
	}	
	else if (cmd.equals("drawEta2")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    validate();
	    dc.drawLine();
	    dc.updateTitle(sf.getValue("ETA2",1));
	    dc.draw("eta2");
	}	
	else if (cmd.equals("drawEta2GradS")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	       	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    gd.setType(true);
	    gd.setSmooth(true);
	    getContentPane().add(gd,BorderLayout.EAST);
	    dc.eraseLine();

	    displayState = 4;
	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    validate();
	    dc.updateTitle(sf.getValue("ETA2GRAD",1));
	    dc.draw("eta2");
	}	
	else if (cmd.equals("drawEta2GradD")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    gd.setType(true);
	    gd.setSmooth(false);
	    getContentPane().add(gd,BorderLayout.EAST);
	    dc.eraseLine();

	    displayState = 4;
	    setSize(getWidth()-1,getHeight());	    

	    setSize(getWidth()+1,getHeight());
	    dc.updateTitle(sf.getValue("ETA2GRAD",1));
	    validate();
	    dc.draw("eta2");
	}	
	else if (cmd.equals("getInputFile")) {
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	
	   
	    NewThread nt = new NewThread("UpdateInput");
		nt.start();
		while(nt.isAlive());
	  /*  String str[] = new String[5];
	    str[0] = sf.getValue("INPUT",2);
	    str[1] = sf.getValue("INPUT",3);
	    str[2] = fileString();
	    str[3] = "30";
	    str[4] = "30";
	    fd = new FilePanel(Frame.getFrames()[0],str, 5, 0, "None", 4); 
    	    if(!fd.getStatus()){
		    Launcher launcher = new Launcher(fd.getValidatedText());
		    updateModel(launcher.getModel());
		    updateDrawingCanvas();
	    }
	    setSize(getWidth()+1, getHeight());
	    setSize(getWidth()-1, getHeight());*/
	    dc.draw("mesh");
	    dc.drawLine();
	    validate();
		
	}
	else if (cmd.equals("forceBar")) {
	
		dc.setState(0);
	   	if (displayState == 2) {
	    
	  	  	remove(tck);
			displayState = 1;
		  }
		else if (displayState == 3) {
	    
		    	remove(mt);
			displayState = 1;
		}
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
		validate();
	 dc.updateTitle(sf.getValue("STFOR",1));
      	 dc.drawLine();
	    	dc.draw("forceBars");
	}
	else if (cmd.equals("forceCable")) {
	
		dc.setState(0);
	   	if (displayState == 2) {
	    
	  	  	remove(tck);
			displayState = 1;
		  }
		else if (displayState == 3) {
	    
		    	remove(mt);
			displayState = 1;
		}
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
		validate();
	 dc.updateTitle(sf.getValue("CABFOR",1));
      	 dc.drawLine();
	    	dc.draw("forceCables");
	}
	else if (cmd.equals ("thick_sol")) {
		
		dc.setState(1);
		if (displayState == 3){
			remove(mt);
			validate();
		}
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    	tck.setState(true);
		tck.display();
		getContentPane().add(tck, BorderLayout.EAST);
	    dc.eraseLine();

		displayState = 2;
    		validate();
		dc.updateTitle(sf.getValue("THICKSOL",2));
		dc.draw("mesh");
	}	
	else if (cmd.equals ("thick_bar")) {
		
		dc.setState(2);
		if (displayState == 3){
			remove(mt);
			validate();
		}
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if(displayState == 2){
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    	tck.setState(false);
		tck.display();
		getContentPane().add(tck, BorderLayout.EAST);
	    dc.eraseLine();
		dc.updateTitle(sf.getValue("THICKBAR",2));
		displayState = 2;
		validate();
		dc.draw("mesh");
	}	
	else if (cmd.equals ("thick_barcol")) {
		
		dc.setState(5);
		if (displayState == 3){
			remove(mt);
			validate();
		}
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
		tck.setState(false);
		tck.display();
	    	getContentPane().add(tck, BorderLayout.EAST);
	    dc.eraseLine();
		dc.updateTitle(sf.getValue("THICKBARCOL",1));
		displayState = 2;
		    validate();
		dc.draw("mesh");
	}
	else if (cmd.equals ("material_sol")) {
		
		dc.setState(3);
		if (displayState == 2){
			remove(tck);
			validate();
		}
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    	getContentPane().add(mt, BorderLayout.EAST);
    		dc.eraseLine();
		dc.updateTitle(sf.getValue("MATSOL",2));		  
	      	validate();
		displayState = 3;
		dc.draw("mesh");
	}	
	else if (cmd.equals ("material_bar")) {
		
		dc.setState(4);
		if (displayState == 2){
			remove(tck);
			validate();
		}
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	    	getContentPane().add(mt, BorderLayout.EAST);
	    dc.eraseLine();
		dc.updateTitle(sf.getValue("MATBAR",2));
		displayState = 3;
		    validate();
		dc.draw("mesh");
	}	

	else if (cmd.equals ("both")) {
	
		dc.setState(0);
	 	if (displayState == 2) {
	    
	    		remove(tck);
			displayState = 1;
		}
		else if (displayState == 3) {
		    
	    		remove(mt);
			displayState = 1;
		    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    else if (displayState == 5) {
	    
	    	remove(p);
		displayState = 1;
	    }	 
	 	validate();
	    dc.drawLine();
		dc.updateTitle(sf.getValue("MESH",1));
		dc.draw("mesh");
	}

	else if (cmd.equals ("nodes")){

	 /*   String []str = new String[2];
	    str[0] = sf.getValue("LISTNODE",1);
	    str[1]=dc.listNode(model.getMapOfNodes());
	    fd = new FilePanel(Frame.getFrames()[0],str, 2, 0, "None", 3);

	    dc.drawLine();*/
	
		Listing list = new Listing(dc.listNode(model.getMapOfNodes()), 0,dc);
		list.setVisible(true);
	}

	else if (cmd.equals ("elems")){
	
	 /*   String []str = new String[2];
	    str[0] = sf.getValue("LISTELEM",1);
	    str[1]=dc.listElem(model.getMapOfElems());
	    fd = new FilePanel(Frame.getFrames()[0],str, 2, 0, "None", 3);*/
				Listing list = new Listing(dc.listElem(model.getMapOfElems()), 1,dc);
		list.setVisible(true);

	    dc.drawLine();
	}

	else if (cmd.equals ("forces")){
	
	/*    String []str = new String[2];
	    str[0] = sf.getValue("LISTFOR",1);
	    str[1]=dc.listLoad(model.getMapOfLoads());
	    fd = new FilePanel(Frame.getFrames()[0],str, 2, 0, "None", 3);*/
		Listing list = new Listing(dc.listLoad(model.getMapOfLoads()), 0,dc);
		list.setVisible(true);

	    dc.drawLine();
	}

	else if (cmd.equals ("reactions")){
	
	 /*   String []str = new String[2];
	    str[0] = sf.getValue("LISTREAC",1);
	    str[1]=dc.listBoun(model.getMapOfBouns());
	    fd = new FilePanel(Frame.getFrames()[0],str, 2, 0, "None", 3);*/
		Listing list = new Listing(dc.listBoun(model.getMapOfBouns()), 0,dc);
		list.setVisible(true);

	    dc.drawLine();
	}

	else if (cmd.equals ("bars")){
	
	/*    String []str = new String[2];
	    str[0] = sf.getValue("LISTBAR",1);
	    str[1]=dc.listBar(model.getMapOfElems());
	    fd = new FilePanel(Frame.getFrames()[0],str, 2, 0, "None", 3);*/
		Listing list = new Listing(dc.listBar(model.getMapOfElems()), 0,dc);
		list.setVisible(true);

	    dc.drawLine();
	}

	else if (cmd.equals ("cab")){
	
	/*    String []str = new String[2];
	    str[0] = sf.getValue("LISTCAB",1);
	    str[1]=dc.listCab(model.getMapOfElems());
	    fd = new FilePanel(Frame.getFrames()[0],str, 2, 0, "None", 3);*/
		Listing list = new Listing(dc.listCab(model.getMapOfElems()), 0,dc);
		list.setVisible(true);

	    dc.drawLine();
	}


	else if (cmd.equals ("conc")){
	
	/*    String []str = new String[2];
	    str[0] = sf.getValue("LISTCONC",1);
	    str[1]=dc.listConc(model.getMapOfElems());
	    fd = new FilePanel(Frame.getFrames()[0],str, 2, 0, "None", 3);*/
		Listing list = new Listing(dc.listConc(model.getMapOfElems()), 0,dc);
		list.setVisible(true);

	    dc.drawLine();
	}
	else if (cmd.equals("select")){
	

	    String str[] = new String[5];
	    str[0] = sf.getValue("ELEMNUM",1);
	    str[1] = sf.getValue("OK",1);
	    str[2] = "";
	    str[3] = "1";
	    str[4] = "10";
	    fd = new FilePanel(Frame.getFrames()[0],str, 5, 0, "None", 4);
	    if(!fd.getStatus()){ 
		    String eleNo = fd.getValidatedText();
		    if(eleNo.equals(""))
			    eleNo="0";
		    try {
			    StringTokenizer st = new StringTokenizer(eleNo," -");
			    int ele1=0,ele2=0;
			    if(st.countTokens()==1){
				    ele1 = Integer.parseInt(st.nextToken());
				    dc.draw("mesh1", ele1);
			    }
			    else if(st.countTokens()==2){					
				    
				    ele1 = Integer.parseInt(st.nextToken());
				    ele2 = Integer.parseInt(st.nextToken());
				    dc.draw("mesh2",ele1,ele2);	
			    }
		    else			    
			    System.out.println(sf.getValue("MSGINPUT",1));
		    }
		    catch (NumberFormatException e) {
			    System.out.println(sf.getValue("MSGINT",1));
		    }
	    }
		dc.updateTitle(sf.getValue("MESH",1));
		/*SelNode sel = new SelNode();
		sel.setVisible(true);*/
	    dc.drawLine();
	}
	else if (cmd.equals("eps1")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    
	    dc.draw("epsConc");
	    p.setValues(dc.getValue(1), dc.getCount(1),7, sf.getValue("STRA1CONC",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();

	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    dc.updateTitle(sf.getValue("STRA1CONC",1));
	    validate();
	
	}
	else if (cmd.equals("eps2")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(2), dc.getCount(2),7, sf.getValue("STRA2CONC",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();

	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
            dc.updateTitle(sf.getValue("STRA2CONC",1));   
	    dc.draw("eps2");
	
	}
	else if (cmd.equals("epsBar")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(3), dc.getCount(3),7, sf.getValue("STRABAR",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();

	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
            dc.updateTitle(sf.getValue("STRABAR",1));   	    
	    dc.draw("epsBar");
	
	}    
	else if (cmd.equals("s1Bar")) {
	    dc.setState(0);	
	    if (displayState == 2) {
		    String []str = new String[1];
		    str[0]=sf.getValue("MSGLOGIN",1)+"\n"+sf.getValue("MSGLOGIN",2);
		    fd = new FilePanel(Frame.getFrames()[0],str, 1, 0, "None", 3);
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(4), dc.getCount(4),2,sf.getValue("STREBAR",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();

	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
            dc.updateTitle(sf.getValue("STREBAR",1));   	    
	    dc.draw("s1Bar");	
	}

	else if (cmd.equals("forBar")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(5), dc.getCount(5),3,sf.getValue("FORBAR",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();

	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
            dc.updateTitle(sf.getValue("FORBAR",1));      
	    dc.draw("forBar");
	
	}
	else if (cmd.equals("epsCab")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(6), dc.getCount(6),7,sf.getValue("STRACAB",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();
            dc.updateTitle(sf.getValue("STRACAB",1));   
	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
	    
	    dc.draw("epsCab");
	
	}    
	else if (cmd.equals("s1Cab")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(7), dc.getCount(7),2,sf.getValue("STRECAB",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();
            dc.updateTitle(sf.getValue("STRECAB",1));   
	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
	    
	    dc.draw("s1Cab");
	
	}

	else if (cmd.equals("forCab")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(8), dc.getCount(8),3,sf.getValue("FORCAB",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();
            dc.updateTitle(sf.getValue("FORCAB",1));   
	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
	    
	    dc.draw("forCab");
	
	}
	else if (cmd.equals("s1Conc")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(9), dc.getCount(9),2,sf.getValue("STR1CONC",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();
            dc.updateTitle(sf.getValue("STR1CONC",1));   
	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
	    
	    dc.draw("s1Conc");
	
	}
	else if (cmd.equals("s2Conc")) {
	    dc.setState(0);	
	    if (displayState == 2) {
	    
	    	remove(tck);
		displayState = 1;
	    }
	    else if (displayState == 3) {
	    
	    	remove(mt);
		displayState = 1;
	    }
	    else if (displayState == 4) {
	    
	    	remove(gd);
		displayState = 1;
	    }
	    p.setValues(dc.getValue(10), dc.getCount(10),2,sf.getValue("STR2CONC",1));
	    getContentPane().add(p,BorderLayout.EAST);
	    dc.eraseLine();
            dc.updateTitle(sf.getValue("STR2CONC",1));   
	    setSize(getWidth()-1,getHeight());
	    setSize(getWidth()+1,getHeight());
	    displayState = 5;
	    validate();
	    
	    dc.draw("s2Conc");
	
	}
      else if (cmd.equals("help")) {
         String helpFileName = sf.getValue("HELPFILE",1); //"http://i-concrete.epfl.ch/aide/iConc/iconc01-1-01_e.asp";
         try {
            URL u = new URL(helpFileName);
            AppletContext a = getAppletContext();
            a.showDocument(u, "_blank");
          }
          catch (Exception e1) {
            System.out.println("Unable to start help page " + helpFileName);
          }

      }
      else if (cmd.equals("messages")) {
    		Listing list = new Listing(Jconc.getMessages(), 7,dc);
		   list.setVisible(true);
      }
      else if (cmd.equals("listall")) {
         String tmp = "";
         try {
            tmp = getAllResults();
         }
         catch (Exception e) {
            tmp = "Unable to get results from viewer\n";
         }
    		Listing list = new Listing(tmp, 8,dc);
		   list.setVisible(true);
      }
   }    
   
    public void mouseClicked (MouseEvent e) {
/*	    System.out.println("Mouse Clicked");
	    int x = e.getX();
	    int y = e.getY();
	    Iterator itNodes = ((Collection) model.getMapOfNodes().values()).iterator();
	    while (itNodes.hasNext()) {
	    	Node n = (Node) itNodes.next();
		if((x == n.x()) && (y == n.y())){
		}
	    }
	    Iterator itElems = ((Collection) model.getMapOfElems().values()).iterator();
	    while (itElems.hasNext()) {
	    
	    	Element ei = (Element) itElems.next();
		if(ei.type().equals("Conc3N")){
		
			int xc[] = new int[3];
			int yc[] = new int[3];
			for(int i = 0; i<3;i++){
				xc[i] =(int) ei.node(0).x();
				yc[i] =(int) ei.node(0).y();
			}
			for(int i = 0; i<3;i++)
				System.out.println(xc[i]+" "+yc[i]);
			System.out.println(x+" "+y);
			Polygon p = new Polygon(xc,yc,3);
		
				System.out.println(p.contains(x,y));
			if(p.contains(x,y)){
			
				String str = "";
				str+=ei.id();
	//			dc.setToolTipText(str);
			}
		}
	    }
	 	 
*/
    }

    public void mouseEntered (MouseEvent e) {
    }

    public void mouseExited (MouseEvent e) {
    }
    public void windowStateChanged(WindowEvent e) {
    
    	repaint();
    }

   public void mousePressed (MouseEvent e) {
      buttonNumber = e.getButton();
      if (buttonNumber == 1  & e.isAltDown()) {
      setNoShade (!getNoShade());
      dc.modifyForceScale(1.0); // to force redrawing...
      dc.changeCounter(true);
   }

    }

    public void mouseReleased (MouseEvent e) {
	x0 = 0;
	y0 = 0;
    }

        public void mouseDragged (MouseEvent e) {


        if (buttonNumber == 1) {
            if (x0 == 0) x0 = e.getX();
            if (y0 == 0) y0 = e.getY();
            dc.modifyFocus(e.getX()-x0,e.getY()-y0);
            x0 = e.getX();
            y0 = e.getY();
        }
        else if (buttonNumber == 2 ) {
            if (y0 == 0) y0 = e.getY();
            int notches = e.getY()-y0;
            if (notches < 0) {
                dc.modifyScale(1.1);
            }
            else if (notches > 0) {
                dc.modifyScale(0.9);
            }
            y0 = e.getY();
        }

        else if (buttonNumber == 3 & e.isShiftDown() & e.isControlDown()) {

            if (y0 == 0) y0 = e.getY();
            int notches = e.getY() - y0;
            if (notches > 0) {
               int w = getStrokeWidth();
               w -= 1;
               if (w < 1) w = 1;
               setStrokeWidth(w);
               dc.modifyForceScale(1.0); // to force redrawing...
               dc.changeCounter(true);
            }
            else if (notches < 0) {
               int w = getStrokeWidth();
               w += 1;
               setStrokeWidth(w);
               dc.modifyForceScale(1.0);
               dc.changeCounter(true);
            }
            y0 = e.getY();

        }
        else if (buttonNumber == 3 & e.isShiftDown() & e.isAltDown()) {

            if (y0 == 0) y0 = e.getY();
            int notches = e.getY() - y0;
            if (notches > 0) {
               int w = getMaxLenVec();
               w -= 1;
               if (w < 1) w = 0;
               setMaxLenVec(w);
               dc.modifyForceScale(1.0); // to force redrawing...
               dc.changeCounter(true);
            }
            else if (notches < 0) {
               int w = getMaxLenVec();
               w += 1;
               setMaxLenVec(w);
               dc.modifyForceScale(1.0);
               dc.changeCounter(true);
            }
            y0 = e.getY();

        }
        else if (buttonNumber == 3 & e.isAltDown() & e.isControlDown()) {
            if (y0 == 0) y0 = e.getY();
            int notches = e.getY() - y0;
            if (notches > 0) {
               double w = getMinStressLevel();
               w -= 0.001;
               if (w < 0) w = 0;
               setMinStressLevel(w);
               dc.modifyForceScale(1.0); // to force redrawing...
               dc.changeCounter(true);
            }
            else if (notches < 0) {
               double w = getMinStressLevel();
               if (w == 0)
                  w = 0.001;
               else
                  w += 0.001;
               setMinStressLevel(w);
               dc.modifyForceScale(1.0);
               dc.changeCounter(true);
            }
            y0 = e.getY();

        }
        else if (buttonNumber == 3 & e.isShiftDown()) {

            if (y0 == 0) y0 = e.getY();
            int notches = e.getY() - y0;
            if (notches < 0) {
              dc.modifyForceScale(1.1);
              dc.changeCounter(true);

            }
            else if (notches > 0) {
              dc.modifyForceScale(0.9);
              dc.changeCounter(false);
            }
            y0 = e.getY();

        }


        else if (buttonNumber == 3 & e.isControlDown()) {

            if (y0 == 0) y0 = e.getY();
            int notches = e.getY() - y0;
            if (notches < 0) {
              dc.modifyDefScale(1.1);
              dc.modifyForceScale(1/1.1);
              dc.changeCounter(true);

            }
            else if (notches > 0) {
              dc.modifyDefScale(0.9);
              dc.modifyForceScale(1/0.9);
              dc.changeCounter(false);
            }
            y0 = e.getY();

        }

                else if (buttonNumber == 3 & e.isAltDown()) {

            if (y0 == 0) y0 = e.getY();
            int notches = e.getY() - y0;
            if (notches < 0) {
              dc.modifyDefScale(1.1);
              dc.modifyForceScale(0);
              dc.changeCounter(true);

            }
            else if (notches > 0) {
              dc.modifyDefScale(0.9);
              dc.modifyForceScale(0);
              dc.changeCounter(false);
            }
            y0 = e.getY();

        }


        else if (buttonNumber == 3  ) {

            if (y0 == 0) y0 = e.getY();
            int notches = e.getY() - y0;
            if (notches < 0) {
              dc.modifyDefScale(1.1);
              dc.changeCounter(true);

            }
            else if (notches > 0) {
              dc.modifyDefScale(0.9);
              dc.changeCounter(false);
            }
            y0 = e.getY();

        }

        else if (buttonNumber == 3 & e.isShiftDown()) {

            if (y0 == 0) y0 = e.getY();
            int notches = e.getY() - y0;
            if (notches < 0) {
              dc.modifyForceScale(1.1);
              dc.changeCounter(true);

            }
            else if (notches > 0) {
              dc.modifyForceScale(0.9);
              dc.changeCounter(false);
            }
            y0 = e.getY();

        }
    }
    public void mouseMoved (MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
	int notches = e.getWheelRotation();
	if (notches < 0) {
	    dc.modifyScale(1.1);
	}
	else if (notches > 0) {
	    dc.modifyScale(0.9);
	}
    }

//Introduced code ------- rohit
	/*
	public class ThreadDraw extends Thread {

		String draw;
	
		public ThreadDraw(String str, String draw) {
		
			super(str);
			this.draw = draw;
		}

		public void run() {		
			dc.draw(draw);
		}
	}*/

	public class NewThread extends Thread {

		public Timer timer;

		public NewThread(String str) {

			super(str);
		}

		public void run() {
		
			ModelWriter mw = new ModelWriter(model,Frame.getFrames()[0]);
			timer = new Timer();
			timer.schedule(new CheckStatus(mw), 0, 10);
			mw.setVisible(true);
			
		}
		public class CheckStatus extends TimerTask {
		
			private ModelWriter mw;

			public CheckStatus(ModelWriter mw) {
		
				this.mw = mw;
			}
			public void run() {
		
				boolean status = mw.getStatus();
				if(!status) {
	
					updateModel(mw.getModel());
					updateDrawingCanvas();
					setSize(getWidth()+1, getHeight());
					setSize(getWidth()-1, getHeight());
					timer.cancel();
				}

			}

		}
	}
//end of the introduced code -------- rohit
//modified code ------- rohit
/*	public class SelNode extends Frame implements WindowListener, ActionListener	{

		private String eleNo;
		private Button button1;
		private TextArea ta;


		public SelNode() {
	
			setTitle(sf.getValue("ELEMNUM",1));
			setSize(200,100);
			setVisible(true);
			addWindowListener(this);
			ta = new TextArea(eleNo,5,10);	
			eleNo = null;
			setLocationRelativeTo(dc);
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
				eleNo = ta.getText();
				if(eleNo.equals(""))
					eleNo="0";
				setVisible(false);
				//System.out.println("File name is : "+fileName);
				try {
					StringTokenizer st = new StringTokenizer(eleNo," -");
					int ele1=0,ele2=0;
					if(st.countTokens()==1){
						ele1 = Integer.parseInt(st.nextToken());
						dc.draw("mesh1", ele1);
					}
					else if(st.countTokens()==2){
					
						ele1 = Integer.parseInt(st.nextToken());
						ele2 = Integer.parseInt(st.nextToken());
						dc.draw("mesh2",ele1,ele2);
					}
					else
						System.out.println(sf.getValue("MSGINPUT",1));
				}
				catch (NumberFormatException e) {
					System.out.println(sf.getValue("MSGINT",1));
				}
	
			}
		}
		public String getElement() {
		
			return eleNo;
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
*/
	public class SelFile extends Thread{

		String []str;
		int length;
		int open;
		Frame frame;

	
		public SelFile(String str[], int length,int open) {

			this.str = new String[length];
			this.str = str;
			this.length = length;

			this.open = open;
			run();


		}
		public void createAndShowGUI(){

		/*	JFrame.setDefaultLookAndFeelDecorated(true);
			if(open==0)
				frame = new JFrame("Open a file");
			else if(open ==1)
				frame = new JFrame("Save file as");
			fd = new FilePanel(frame,str,length,open, fileName);
			JComponent newContentPane = fd  ;
			newContentPane.setOpaque(true); //content panes must be opaque
		        frame.setContentPane(newContentPane);
		        frame.pack();

		        frame.setVisible(true);		*/
		//	frame = Frame.getFrames()[thisInstance-1];
			//frame = new Frame("Hi");
			frame = Frame.getFrames()[0];
			if(autoSave==null || open == 0){
				fd = new FilePanel(frame,str,length,open, fileName,0);
			}
		}

		
			
		


				
			public void run() {
			createAndShowGUI();
		
				if(open==0){
				
						if(!fd.getStatus()){
							fileName = fd.getValidatedText();							
							Launcher la = new Launcher(gf.readFile("file", "iConc", fd.getValidatedText()));
							updateModel(la.getModel());
							updateDrawingCanvas();
							validate();
							setSize(getWidth()-1,getHeight());
							setSize(getWidth()+1,getHeight());
						}
				}
				else if(open == 1) {

					if(autoSave==null){
						
						if(!fd.getStatus()){

							String name = fd.getValidatedText();
							gf.parameters(ip,fd.getValidatedText(),"iConc","1.10",false,false,false);
							boolean success = gf.write(fileString().replaceAll("\n","£££"));
							int reponse = gf.reponse;
						//	System.out.println("The value of response is "+reponse+" "+success);
							if(!success){

								if(reponse == 1){
									str[0] = sf.getValue("MSGOVERWRITE",1);
									fd = new FilePanel(frame,str,length,open,fileName,1);
								}
								else if(reponse == 3){
									str[0] = sf.getValue("WARNING",1);								
									str[1] =sf.getValue("MSGAUTH",1);		
									fd = new FilePanel(frame,str,2,open,fileName,2);
								}
								else if(reponse == 10){
								
									str[0] = sf.getValue("WARNING",1);
									str[1] = sf.getValue("MSGLOGIN",1)+"\n"+sf.getValue("MSGLOGIN",2);
									fd = new FilePanel(frame, str, 2, open, fileName, 2);	
								}

								if(!fd.getStatus()){

									gf.parameters(ip,name,"iConc","1.10",false,false,true);
									gf.write(fileString().replaceAll("\n","???"));		
								}
								else{
									run();
								}
							}

						}
					}
					else{
					
						gf.parameters(ip,autoSave,"iConc","1.10",false,false,true);
						gf.write(fileString().replaceAll("\n","???"));
					}
				}
			//	frame.setVisible(false);
			}
	
	}
//end of modified code ----------  rohit	

}
