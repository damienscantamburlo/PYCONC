package net.cercis.jconc.ui;

import net.cercis.jconc.fem.Node;
import net.cercis.jconc.fem.Element;
import net.cercis.jconc.fem.Load;
import net.cercis.jconc.fem.Boun;
import net.cercis.jconc.fem.Model;

import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.FontMetrics;

import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import java.lang.Math;
import java.math.BigDecimal;

public class DrawingCanvas extends JComponent {

    private Graphics2D g2d;
    private Model model;
    private String draw;
    private Thickness tck;
    private Mates mt;
    private int digits = Jconc.getDigits();
    private float x_max;
    private float x_min;
    private float y_max;
    private float y_min;
    private float dist_max;
    private float x_c;
    private float y_c;
    private float xd_c;
    private float yd_c;
    private double scale;
    private double zoomFact;
    private double forceZoomFact = 1.0;
    private double defZoomFact;
    private double forcedScale1 = 0;
    private double forcedScale2 = 0;
    private boolean drawLegend = true;
    private double ix;
    private double iy;
    private int state;
    private float counter;
    private List listStore;
    private boolean flag;
    private int eleNo1;
    private int eleNo2;
    private int countConc;
    private int countBar;
    private int countCab;
    private int countConc1;
    private int countConc2;
    private int countConc3;
    private int countConc4;
    private int countBar1;
    private int countBar2;
    private int countBar3;
    private int countCab1;
    private int countCab2;
    private int countCab3;
    private double stS1Bar[];
    private double stEps2[];
    private double stForceBar[];
    private double stS1Cab[];
    private double stForceCab[];
    private double stEps1Conc[];
    private double stS1Conc[];
    private double stS2Conc[];
    private double stEps1Bar[];
    private double stEps1Cab[];
    private StringFile sf;
    private boolean erase;
    private boolean erase1;
    private String title;


/*
 *  The different values of state are explained below :
 *  state = 0	Both the solid elements and bars are displayed in the natural color of red and blue
 *  state = 1	Only the solid elements are displayed with the color being derived from their respective thickness
 *  state = 2	Bar elements are displayed in the color wrt thickness and the solid elements are displayed white with their outlines
 *  state = 3	Only the solid elements are displayed with the color being derived from their respective material
 *  state = 4	Bar elements are displayed in the color wrt material and the solid elements are displayed white with their outlines
 */
    public DrawingCanvas (Model model, int width, int height, Thickness tck, Mates mt) {
	setBackground(Color.white);
	setBorder(BorderFactory.createLineBorder(Color.black));
	title = "Mesh";
	this.model = model;
	state = 0;
	this.tck = tck;
	this.mt = mt;
	this.draw = "";
	zoomFact = 1.0;
	defZoomFact = 1.0;
	ix = 0;
	counter = 0;
	iy = 0;
	flag = true;
	listStore = new LinkedList();
	calcValues(model.getMapOfElems());
	eleNo1 = 0;
	sf = Jconc.sf;
	eleNo2 = 0;
	erase = false;
	erase1 = false;
	repaint();


    }
    public double[] getValue(int num) {

	    if(num == 1)
		    return stEps1Conc;
	    else if(num == 2)
		    return stEps2;
	    else if(num == 3)
		    return stEps1Bar;
	    else if(num == 4)
		    return stS1Bar;
	    else if(num == 5)
		    return stForceBar;
	    else if(num == 6)
		    return stEps1Cab;
	    else if(num == 7)
		    return stS1Cab;
	    else if(num == 8)
		    return stForceCab;
	    else if(num == 9)
		    return stS1Conc;
	    else if (num == 10)
		    return stS2Conc;
	    else return stEps1Conc;
    }

    public int getCount(int chance) {
	    switch(chance){
		    case 1 : return countConc1;
		    case 2 : return countConc2;
		    case 3 : return countBar1;
		    case 4 : return countBar2;
		    case 5 : return countBar3;
		    case 6 : return countCab1;
		    case 7 : return countCab2;
		    case 8 : return countCab3;
		    case 9 : return countConc3;
		    case 10 : return countConc4;

	    }

    	return countConc1;
    }
    public void modifyForceScale (double fact) {
       if (fact == 0)
          forceZoomFact = 1;
       else
          forceZoomFact *= fact;
       draw(draw);
    }

    public void setDrawLegend (boolean b) {
       drawLegend = b;
    }

    public DrawingCanvas (Model model, int width, int height) {
	setBackground(Color.white);
	this.model = model;
	title = "mesh";
	state = 0;
	this.draw = "";
	zoomFact = 1.0;
	defZoomFact = 1.0;
   forceZoomFact =1.0;
	ix = 0;
	counter = 0;
	iy = 0;
	calcValues(model.getMapOfElems());
	flag = true;
	repaint();
    }

    public void draw (String draw) {
      this.draw = draw;
      scale();
      repaint();
    }


  public void draw (String draw, double fact) {
   forcedScale1 = fact;
     this.draw = draw;
	scale();
	repaint();

    }

  public void draw (String draw, double fact1, double fact2) {
   forcedScale1 = fact1;
   forcedScale2 = fact2;
   this.draw = draw;
	scale();
	repaint();

    }


    public void updateTitle(String title){

    	this.title = title;
    }

    public void draw (String draw, int no) {
    	this.draw = draw;
	scale();
	eleNo1 = no;
	repaint();

    }


    public void draw (String draw, int no1, int no2) {
    	this.draw = draw;
	scale();
	eleNo1 = no1;
	eleNo2 = no2;
	repaint();

    }
    public void setState(int state) {

    	this.state = state;
    }

    public void updateModel(Model model) {

    	this.model = model;
	calcValues(model.getMapOfElems());
    }

    private void scale() {
	float xdw_c = (float) (getWidth()/2.0);
	float ydw_c = (float) (getHeight()/2.0);
	xd_c = (float) (ix + getWidth()/2.0);
	yd_c = (float) (iy + getHeight()/2.0);
	Map mElems = model.getMapOfElems();
	Element e1 = (Element) mElems.get((Integer)((TreeMap) mElems).firstKey());
	x_max = (e1.node(0)).x();
	x_min = (e1.node(0)).x();
	y_max = (e1.node(0)).y();
	y_min = (e1.node(0)).y();
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    int numTotNodes = ei.getNumNodes ();
	    for (int i=0; i<numTotNodes; i++) {
		if (ei.node(i).x() > x_max) x_max = ei.node(i).x();
		if (ei.node(i).x() < x_min) x_min = ei.node(i).x();
		if (ei.node(i).y() > y_max) y_max = ei.node(i).y();
		if (ei.node(i).y() < y_min) y_min = ei.node(i).y();
	    }
	}
	dist_max = x_max - x_min;
	if ((y_max - y_min) > dist_max) dist_max = (y_max - y_min);

	//Compute the scale factor
	scale = 2*xdw_c/(x_max-x_min);
	if ((ydw_c/(y_max-y_min))<scale) scale = ydw_c/(y_max-y_min);
	scale = scale*zoomFact*0.75; //Effective value of the scale <=> ZOOM
	x_c = (float) ((x_max + x_min)/2.0);
	y_c = (float) ((y_max + y_min)/2.0);
    }


    public void modifyScale (double fact) {
	zoomFact = zoomFact*fact;
	draw(draw);
    }

    public void modifyDefScale (double fact) {
	defZoomFact = defZoomFact*fact;
	draw(draw);
    }

    public void modifyFocus (double incx, double incy) {
	this.ix = ix + incx;
	this.iy = iy + incy;
	draw(draw);
    }

    public void changeCounter(boolean movement) {

    	if(!flag) {
		if(movement){
			if(counter<2)
				counter+=.1;
		}
		else {
			if(counter>0)
				counter-=.1;
		}
	}
    }
    public void eraseLine(){

	repaint();
	g2d.setColor(Color.white);
	erase = true;
	g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
	g2d.setColor(Color.black);
	repaint();
    }
    public void drawLine(){
    	g2d.setColor(Color.black);
	erase = false;
	g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
	g2d.setColor(Color.white);
	repaint();


    }
    public void eraseLines(){

    	g2d.setColor(Color.white);
	erase1 = true;
	g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
	g2d.drawLine(0,0,0,getHeight()-1);
	g2d.drawLine(0,getHeight()-1,getWidth()-1,getHeight()-1);
	g2d.setColor(Color.black);
	repaint();

    }
    public void drawLines(){

    	g2d.setColor(Color.black);
	erase1 = false;
	g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
	g2d.drawLine(0,0,0,getHeight()-1);
	g2d.setColor(Color.white);
	repaint();

    }

    public void paint (Graphics g) {
	g2d = (Graphics2D) g;
	FontMetrics fm = g2d.getFontMetrics(g2d.getFont());
	int strWidth = fm.stringWidth(title);
	//I draw a blank rectangle for non-interactive saving
	g2d.setColor(getBackground());
	g2d.fillRect(0,0,getWidth(),getHeight());
	g2d.setColor(Color.black);
	if(!erase1)
		g2d.drawLine(0,0,0,getHeight()-1);
	int xpos = (getWidth()-strWidth)/2;
   //Plots the title
	//g2d.drawString(title,xpos,30);
	if(!erase)
		g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);

	g2d.drawLine(0,getHeight()-1,getWidth()-1,getHeight()-1);
//	eraseLine();

//	g2d.drawRect(0,0,0,getHeight()-1);
// 	g2d.setColor(Color.BLACK);
// 	g2d.drawString(""+yd_c,20,10);
	Map mElems = model.getMapOfElems();
	if(draw == null )
		draw = "mesh";
	if (draw.equals("mesh")) {
	    drawElements(mElems);
            //updateList(model.getMapOfElems());
	    drawBoundaries(model.getMapOfBouns());
	    drawLoads(model.getMapOfLoads());
	}
	else if (draw.equals("mesh1")) {

		drawElements(mElems, eleNo1); //To draw elements with the specified element highlighted
		drawBoundaries(model.getMapOfBouns());
		drawLoads(model.getMapOfLoads());
	}
	else if (draw.equals("mesh2")) {

		drawElements(mElems, eleNo1, eleNo2); //To draw elements with the specified element highlighted
		drawBoundaries(model.getMapOfBouns());
		drawLoads(model.getMapOfLoads());
	}
	else if (draw.equals("defo")) {
	    drawDefElements(mElems);
	    //drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("stress")) {
	    drawStresses(mElems,"all");
	    //drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("stressConcThick")) {
	    drawStresses(mElems,"concThick");
	    //drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("stressConcLen")) {
	    drawStresses(mElems,"concLen");
	    //drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("stressCables")) {
	    drawStresses(mElems,"cables");
	    //drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("eta1")) {
	    drawEta(mElems,"dir1");
	    //drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("eta2")) {
	    drawEta(mElems,"dir2");
	    //drawBoundaries(model.getMapOfBouns());
       //drawLoads(model.getMapOfLoads());

	}
	else if (draw.equals("forceBars")) {
		drawStresses(mElems,"forceBars");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("forceCables")) {
		drawStresses(mElems,"forceCables");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("epsConc")) {

		drawEps(mElems,"eps1");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("eps2")) {

		drawEps(mElems,"eps2");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("s1Conc")) {
		drawEps(mElems,"s1");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("s2Conc")) {

		drawEps(mElems,"s2");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("epsBar")) {

		drawBars(mElems,"eps1");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("s1Bar")) {

		drawBars(mElems,"s1");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("forBar")) {

		drawBars(mElems,"force");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("epsCab")) {

		drawCables(mElems,"eps1");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("s1Cab")) {

		drawCables(mElems,"s1");
		//drawBoundaries(model.getMapOfBouns());
	}
	else if (draw.equals("forCab")) {

		drawCables(mElems,"force");
		//drawBoundaries(model.getMapOfBouns());
	}

    }

    public String listNode(Map mNodes) {

	String str = "";
	str = "**** " + sf.getValue("NODES",1)+" ***************************************";
   str = str.substring(0, 40) + "\n\n";

    	str+=sf.getValue("NODES",2)+"\tx\ty\tu_x\tu_y\n\n";
	Iterator itNodes = ((Collection) mNodes.values()).iterator();
	while (itNodes.hasNext()) {

		Node n = (Node) itNodes.next();
		str+=n.id();
		str+='\t';
		str+=n.x();
		str+='\t';
		str+=n.y();
                str+='\t';
                str+=Jconc.formatNumber(n.getU(), digits);
                str+='\t';
                str+=Jconc.formatNumber(n.getV(), digits);
		str+='\n';
	}
	return str;
    }

    public String listElem(Map mElems) {

    	String str = "";
  	str = "**** " + sf.getValue("ELEMS",1)+" ***************************************";
   str = str.substring(0, 40) + "\n\n";

	str+=sf.getValue("ELEMS",2)+"\t"+sf.getValue("TYPE",1)+"\t"+sf.getValue("MATERIAL",1)+"\t"+sf.getValue("THICKNESS",1)+"\t"+sf.getValue("NODES",1)+"\t"+"\n\n";

	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {

		Element ei = (Element) itElems.next();
		str+=ei.id();
		str+='\t';
		str+=ei.type();
		str+='\t';
		str+=ei.material();
		str+='\t';
		str+=(float)ei.parameter(1);
		str+='\t';
		for(int i=0; i<ei.getNumNodes();i++) {
			str+=ei.node(i).id();
			if(i!=ei.getNumNodes()-1)
				str+=",";
		}
		str+='\n';
	}
	return str;
    }

    public String listLoad(Map mLoads) {

    final int digits = Jconc.getDigits();
   String str = "";
   str = "**** " + sf.getValue("FORCE",1)+" ***************************************";
   str = str.substring(0, 40) + "\n\n";
	str+=sf.getValue("NODES",2)+"\tFx\tFy\n\n";
	double fx = 0, fy = 0;
	Iterator itLoads = ((Collection) mLoads.values()).iterator();
	while (itLoads.hasNext()) {

		Load l = (Load) itLoads.next();
		str+=l.node();
		str+='\t';
		str+= Jconc.formatNumber(l.fx(), digits);
		str+='\t';
		str+=Jconc.formatNumber(l.fy(), digits);
		str+='\n';
		fx+=l.fx();
		fy+=l.fy();
	}
	str+="\n"+sf.getValue("TOTAL")+"\t";
	str+=fx;
	str+='\t';
	str+=fy;
	str+='\n';
	return str;
    }
    public String listBoun (Map mBouns) {
    	String str = "";
	str = "**** " + sf.getValue("REACTIONS",1)+" ***************************************";
   str = str.substring(0, 40) + "\n\n";

	str+=sf.getValue("NODES",2)+"\treacX\treacY\n\n";
	double fx = 0, fy = 0;
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {

		Boun b = (Boun) itBouns.next();
		str+=b.node();
		str+='\t';
		double reacX = b.getReacX(), reacY = b.getReacY();
		Iterator itLoads = ((Collection) model.getMapOfLoads().values()).iterator();
		while(itLoads.hasNext()){

			Load l = (Load) itLoads.next();
			if(b.node()==l.node()){
				reacX-=l.fx();
				reacY-=l.fy();
			}
		}
		str+=Jconc.formatNumber(reacX, digits);
		str+='\t';
		str+=Jconc.formatNumber(reacY, digits);
		str+='\n';
		fx+=reacX;
		fy+=reacY;
	}
	str+="\n"+sf.getValue("TOTAL")+"\t";
	str+=Jconc.formatNumber(fx, digits);
	str+='\t';
	str+=Jconc.formatNumber(fy, digits);
	str+='\n';
	return str;
    }
    public String listBar (Map mElems) {
  	String str = "";
	str = "**** " + sf.getValue("BAR",1)+" ***************************************";
   str = str.substring(0, 40) + "\n\n";
	str+=sf.getValue("ELEMS",2)+"\t"+sf.getValue("STRAIN",1)+"\t"+sf.getValue("STRESS",1)+"\t"+sf.getValue("FORCE",1)+"\n\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {

		Element ei = (Element) itElems.next();
		if(ei.type().equals("Bar")) {

			str+=ei.id();
			str+='\t';
			str+=Jconc.formatNumber(ei.getEps1(), digits);
			str+='\t';
			str+=Jconc.formatNumber(ei.getS1(), digits);
			str+='\t';
			double force = (ei.getS1()*ei.parameter(1));
			str+=Jconc.formatNumber(force, digits);
			str+='\n';
		}
	}
	return str;
    }
    public String listCab (Map mElems) {
  	String str = "";
	str = "**** " + sf.getValue("CABLE",1)+" ***************************************";
   str = str.substring(0, 40) + "\n\n";
	str+=sf.getValue("ELEMS",2)+"\t"+sf.getValue("STRAIN",1)+"\t"+sf.getValue("STRESS",1)+"\t"+sf.getValue("FORCE",1)+"\n\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {

		Element ei = (Element) itElems.next();
		if(ei.type().equals("Cable")) {

			str+=ei.id();
			str+='\t';
			str+=Jconc.formatNumber(ei.getEps1(), digits);
			str+='\t';
			str+=Jconc.formatNumber(ei.getS1(), digits);
			str+='\t';
			double force = (ei.getS1()*ei.parameter(1));
			str+=Jconc.formatNumber(force, digits);
			str+='\n';
		}
	}
	return str;
    }
    public String listConc (Map mElems) {
  	String str = "";
	str = "**** " + sf.getValue("CONCRETE",1)+" ***************************************";
   str = str.substring(0, 40) + "\n\n";
	str+=sf.getValue("ELEMS",2)+"\t"+sf.getValue("STRA1",1)+"\t"+sf.getValue("STRA2",1)+"\t"+sf.getValue("ALPHA")+"\t"+sf.getValue("SIGMA1",1)+"\t"+sf.getValue("SIGMA2",1)+"\t"+sf.getValue("ETA2",1)+"\n\n";

	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {

		Element ei = (Element) itElems.next();
		if(ei.type().equals("Conc3N")) {

			str+=ei.id();
			str+='\t';
			str+=Jconc.formatNumber(ei.getEps1(), digits);
			str+='\t';
			str+=Jconc.formatNumber(ei.getEps2(), digits);
			str+='\t';
			str+=Jconc.formatNumber(ei.getAlpha(), digits);
			str+='\t';
			str+=Jconc.formatNumber(ei.getS1(), digits);
			str+='\t';
			str+=Jconc.formatNumber(ei.getS2(), digits);
			str+='\t';
			str+=Jconc.formatNumber(ei.getEta2(), digits);
			str+='\n';

		}
	}
	return str;
    }

/*
 * //Unused funtion. Introduced by Rohit - commented by Miguel (09.2009)
 * //Used in mesh plot by Rohit
    private void updateList(Map mElems) {

    	double sRelMax = 0.0;
	Iterator itElems = ((Collection) mElems.values()).iterator();
	double defScale = 0;
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    double sReli = 0.0;
	    if (ei.type().equals("Bar")) {
		sReli = Math.abs(ei.getPercentS1());
	    }
	    else if (ei.type().equals("Conc3N")) {
		sReli = Math.abs(ei.getPercentS1()) +
		    Math.abs(ei.getPercentS2());
	    }
	    if (sReli > sRelMax) sRelMax = sReli;
	}
	//IF DEFORMED ZOOM IS TO BE CHANGED, TOUCH IN NEXT LINE
	defScale = 0.05*defZoomFact*dist_max/sRelMax; //SC = 0.05 D_max
	itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {

		Element ei = (Element) itElems.next();
		if(ei.type().equals("Conc3N")) {

			double intS1 = Math.abs(ei.getPercentS1());
			double intS2 = Math.abs(ei.getPercentS2());
			StrucStore eiStore = new StrucStore();
			eiStore.intS1 = intS1;
			eiStore.intS2 = intS2;
			eiStore.scale = scale;
			eiStore.dist_max = dist_max;
			eiStore.defScale = (float) defScale;
			eiStore.sinAlpha = (float)Math.sin(ei.getAlpha());
			eiStore.cosAlpha = (float)Math.cos(ei.getAlpha());
			listStore.add(eiStore);
		}
	}
    }
*/
    private void drawElements (Map mElems) {
	Color grey = new Color(0.8f, 0.8f, 0.8f);	//modified colour
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    //Color elemColor = tck.getColor(ei);
	    Color elemColor = Color.blue;
	    if (ei.type().equals("Conc3N")) {
	    	if (state==3)
	    		elemColor = mt.getMaterial(ei.material());
	    	else if (state==1)
	    		elemColor = tck.getColor(ei);
		else if ( state == 4 | state == 2 | state == 5)
			elemColor = Color.white;
		else if ( state == 0)
			elemColor = Color.blue;
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};

		polygon(elemColor,nodes[0].x(),nodes[0].y(),
			nodes[1].x(),nodes[1].y(),
			nodes[2].x(),nodes[2].y());
		if(state == 4 | state == 2)
			closedPolyline(new Color(0.6f, 0.6f, 0.6f),nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());
		else
			closedPolyline(Color.black,nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());

	    }
	    else if (ei.type().equals("Bar")) {
		Node[] nodes = {ei.node(0), ei.node(1)};
		double e = 0.0025 * dist_max;
		if(state == 0)
			elemColor = Color.red;
		else if (state == 4)
			elemColor = mt.getMaterial(ei.material());
		else if (state == 2) {

			elemColor = Color.red;
			e =  ei.parameter(1);
		}
		else if (state == 5) {
			elemColor = tck.getColor(ei);
		}
		double ct = Math.cos(ei.getAlpha());
		double st = Math.sin(ei.getAlpha());

		if (state!=1 && state!=3)
			polygon(elemColor,(float) (nodes[0].x()+e*st),
				(float) (nodes[0].y()-e*ct),
				(float) (nodes[1].x()+e*st),
				(float) (nodes[1].y()-e*ct),
				(float) (nodes[1].x()-e*st),
				(float) (nodes[1].y()+e*ct),
				(float) (nodes[0].x()-e*st),
				(float) (nodes[0].y()+e*ct));
	    }
	    else if (ei.type().equals("Cable")) {
		Node[] nodes = {ei.node(0), ei.node(1)};
		double e = 0.0025 * dist_max;
		if(state == 0)
			elemColor = Color.green;
		else if (state == 4)
			elemColor = mt.getMaterial(ei.material());
		else if (state == 2) {

			elemColor = Color.red;
			e =  ei.parameter(1);
		}
		else if (state == 5) {
			elemColor = tck.getColor(ei);
		}
		double ct = Math.cos(ei.getAlpha());
		double st = Math.sin(ei.getAlpha());

		if (state!=1 && state!=3)
			polygon(elemColor,(float) (nodes[0].x()+e*st),
				(float) (nodes[0].y()-e*ct),
				(float) (nodes[1].x()+e*st),
				(float) (nodes[1].y()-e*ct),
				(float) (nodes[1].x()-e*st),
				(float) (nodes[1].y()+e*ct),
				(float) (nodes[0].x()-e*st),
				(float) (nodes[0].y()+e*ct));
	    }
	}
    }

    private void drawElements (Map mElems, int eleNo) {
	Color grey = new Color(0.8f, 0.8f, 0.8f);	//modified colour
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    //Color elemColor = tck.getColor(ei);
	    Color elemColor = Color.blue;
	    if (ei.type().equals("Conc3N")) {
	    	if (state==3)
	    		elemColor = mt.getMaterial(ei.material());
	    	else if (state==1)
	    		elemColor = tck.getColor(ei);
		else if ( state == 4 | state == 2 | state == 5)
			elemColor = Color.white;
		else if ( state == 0)
			elemColor = Color.blue;

		if(ei.id()==eleNo){
		    elemColor = Color.black;
		}
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};

		polygon(elemColor,nodes[0].x(),nodes[0].y(),
			nodes[1].x(),nodes[1].y(),
			nodes[2].x(),nodes[2].y());
		if(state == 4 | state == 2)
			closedPolyline(new Color(0.6f, 0.6f, 0.6f),nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());
		else
			closedPolyline(Color.black,nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());

	    }
	    else if (ei.type().equals("Bar")) {
		Node[] nodes = {ei.node(0), ei.node(1)};
		double e = 0.0025 * dist_max;
		if(state == 0)
			elemColor = Color.red;
		else if (state == 4)
			elemColor = mt.getMaterial(ei.material());
		else if (state == 2) {

			elemColor = Color.red;
			e =  ei.parameter(1);
		}
		else if (state == 5) {
			elemColor = tck.getColor(ei);
		}
		double ct = Math.cos(ei.getAlpha());
		double st = Math.sin(ei.getAlpha());

		if (state!=1 && state!=3)
			polygon(elemColor,(float) (nodes[0].x()+e*st),
				(float) (nodes[0].y()-e*ct),
				(float) (nodes[1].x()+e*st),
				(float) (nodes[1].y()-e*ct),
				(float) (nodes[1].x()-e*st),
				(float) (nodes[1].y()+e*ct),
				(float) (nodes[0].x()-e*st),
				(float) (nodes[0].y()+e*ct));
	    }
	}
    }

    private void drawElements (Map mElems, int eleNo1, int eleNo2) {
	Color grey = new Color(0.8f, 0.8f, 0.8f);	//modified colour
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    //Color elemColor = tck.getColor(ei);
	    Color elemColor = Color.blue;
	    if (ei.type().equals("Conc3N")) {
	    	if (state==3)
	    		elemColor = mt.getMaterial(ei.material());
	    	else if (state==1)
	    		elemColor = tck.getColor(ei);
		else if ( state == 4 | state == 2 | state == 5)
			elemColor = Color.white;
		else if ( state == 0)
			elemColor = Color.blue;

		if((ei.id()>=eleNo1)&&(ei.id()<=eleNo2)){
		    elemColor = Color.black;
		}
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};

		polygon(elemColor,nodes[0].x(),nodes[0].y(),
			nodes[1].x(),nodes[1].y(),
			nodes[2].x(),nodes[2].y());
		if(state == 4 | state == 2)
			closedPolyline(new Color(0.6f, 0.6f, 0.6f),nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());
		else
			closedPolyline(Color.black,nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());

	    }
	    else if (ei.type().equals("Bar")) {
		Node[] nodes = {ei.node(0), ei.node(1)};
		double e = 0.0025 * dist_max;
		if(state == 0)
			elemColor = Color.red;
		else if (state == 4)
			elemColor = mt.getMaterial(ei.material());
		else if (state == 2) {

			elemColor = Color.red;
			e =  ei.parameter(1);
		}
		else if (state == 5) {
			elemColor = tck.getColor(ei);
		}
		double ct = Math.cos(ei.getAlpha());
		double st = Math.sin(ei.getAlpha());

		if (state!=1 && state!=3)
			polygon(elemColor,(float) (nodes[0].x()+e*st),
				(float) (nodes[0].y()-e*ct),
				(float) (nodes[1].x()+e*st),
				(float) (nodes[1].y()-e*ct),
				(float) (nodes[1].x()-e*st),
				(float) (nodes[1].y()+e*ct),
				(float) (nodes[0].x()-e*st),
				(float) (nodes[0].y()+e*ct));
	    }
	}
    }


    private void drawDefElements (Map mElems) {
	//Automatic scaling
	double despMax = 0.0;
	Iterator itNodes = ((Collection) (model.getMapOfNodes()).
		       values()).iterator();
	while (itNodes.hasNext()) {
	    Node ni = (Node) itNodes.next();
	    double despi = Math.sqrt(ni.getU()*ni.getU()+ni.getV()*ni.getV());
	    if (despi > despMax) despMax = despi;
	}
	//IF DEFORMED ZOOM IS TO BE CHANGED, TOUCH IN NEXT LINE
	//double defScale = 0.10*defZoomFact*dist_max/despMax;//SCALE = 1/10 D_max

   double defScale = 0.10*dist_max/despMax;//SCALE = 1/10 D_max
   defScale *= defZoomFact;
   if (forcedScale1 != 0) defScale = forcedScale1;
   if (drawLegend) {
      String s = "Def. x " + ((double)Math.round(defScale * 100)/100);
      g2d.drawString(s, 5, 15);
   }
   
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    Color elemColor = Color.blue;

	    if (ei.type().equals("Conc3N")) {

		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};
		//Deformed element

//original_code
		polygon(elemColor,
			(float) (nodes[0].x()+defScale*nodes[0].getU()),
			(float) (nodes[0].y()+defScale*nodes[0].getV()),
			(float) (nodes[1].x()+defScale*nodes[1].getU()),
			(float) (nodes[1].y()+defScale*nodes[1].getV()),
			(float) (nodes[2].x()+defScale*nodes[2].getU()),
			(float) (nodes[2].y()+defScale*nodes[2].getV()));
		closedPolyline(Color.black,
			(float) (nodes[0].x()+defScale*nodes[0].getU()),
			(float) (nodes[0].y()+defScale*nodes[0].getV()),
			(float) (nodes[1].x()+defScale*nodes[1].getU()),
			(float) (nodes[1].y()+defScale*nodes[1].getV()),
			(float) (nodes[2].x()+defScale*nodes[2].getU()),
			(float) (nodes[2].y()+defScale*nodes[2].getV()));
		//Undeformed Grid
		Color grey = new Color(0.6f,0.6f,0.6f);
		closedPolyline(grey,nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());
	    }
	    else if (ei.type().equals("Bar")) {
		Node[] nodes = {ei.node(0), ei.node(1)};
		elemColor = Color.red;

		double e = 0.0025 * dist_max;
		double alpha =
		    Math.atan2(nodes[1].y()+defScale*nodes[1].getV()-
			       nodes[0].y()-defScale*nodes[0].getV(),
			       nodes[1].x()+defScale*nodes[1].getU()-
			       nodes[0].x()-defScale*nodes[0].getU());
		double ct = Math.cos(alpha);
		double st = Math.sin(alpha);


		polygon(elemColor,
			(float) (nodes[0].x()+defScale*nodes[0].getU()+e*st),
			(float) (nodes[0].y()+defScale*nodes[0].getV()-e*ct),
			(float) (nodes[1].x()+defScale*nodes[1].getU()+e*st),
			(float) (nodes[1].y()+defScale*nodes[1].getV()-e*ct),
			(float) (nodes[1].x()+defScale*nodes[1].getU()-e*st),
			(float) (nodes[1].y()+defScale*nodes[1].getV()+e*ct),
			(float) (nodes[0].x()+defScale*nodes[0].getU()-e*st),
			(float) (nodes[0].y()+defScale*nodes[0].getV()+e*ct));
	    }
	}
    }

    private void drawStresses (Map mElems, String par) {
	//Automatic scaling
	double sRelMax = 0.0;
   double minStressLevel = Jconc.getMinStressLevel();
   int maxLenVec = Jconc.getMaxLenVec();

   boolean noShade = Jconc.getNoShade();
	Iterator itElems = ((Collection) mElems.values()).iterator();
	double defScale = 0.05*dist_max/sRelMax;
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    double sReli = 0.0;
	    if (ei.type().equals("Bar")) {
		sReli = Math.abs(ei.getPercentS1());
	    }
	    else if (ei.type().equals("Conc3N")) {
		sReli = Math.abs(ei.getPercentS1()) +
		    Math.abs(ei.getPercentS2());
	    }
	    if (sReli > sRelMax) sRelMax = sReli;
	}
	//IF DEFORMED ZOOM IS TO BE CHANGED, TOUCH IN NEXT LINE
	defScale = 0.05*defZoomFact*dist_max/sRelMax; //SC = 0.05 D_max
   if (forcedScale1 != 0) defScale = forcedScale1;
//   System.out.println ("Concrete " + ((double)Math.round(defScale * 1000)/1000) + " Bars " + ((double)Math.round(defScale * forceZoomFact * 1000)/1000));
   int nLines = 0;
   if (drawLegend) {
      g2d.setColor(Color.BLACK);
      if (par.equals("all") | par.equals("Conc3N")) {
         nLines++;
         String s = "Concrete " + ((double)Math.round(defScale * 1000)/1000);
         g2d.drawString(s, 5, nLines * 15);
         /*if (minStressLevel != 0) */{
            nLines++;
            s = "Min. stress level " + ((double)Math.round(minStressLevel * 1000)/1000);
            g2d.drawString(s, 5, nLines * 15);
         }
      }
      if (par.equals("all") | par.equals("cables") | par.equals("forceBars")) {
         nLines++;
         String s = "Steel " + ((double)Math.round(defScale * forceZoomFact * 1000)/1000);
         g2d.drawString(s, 5, nLines * 15);
      }
   }
   Color grey = new Color(0.8f,0.8f,0.8f);
	Color blue = new Color(0.1f, 0.1f, 1.0f);
	Color red = new Color(1.0f, 0.1f, 0.1f);
	Color redPlas = new Color(0.5f, 0.1f, 0.1f);
   int strokeWidth = Jconc.getStrokeWidth();
   Stroke s = new BasicStroke(strokeWidth);

	itElems = ((Collection) mElems.values()).iterator();
	//FIRST THE BARS AND CONCRETE FILL ARE DRAWN
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Bar") && par.compareTo("forceCables")!=0) {
		if (par.equals("all") | par.equals("cables") | par.equals("forceBars")) {
		    Node[] nodes = {ei.node(0), ei.node(1)};
		    double intS1 = Math.abs(ei.getPercentS1());
		    Color ci;
		    if (ei.getPercentS1()>=0) { ci = red; }
		    else { ci = blue; }
		    if (Math.abs(ei.getPercentS1())>=1.0) { ci = redPlas; }
		    double e = 0;
		    if (par.equals("forceBars"))
			    e = defScale*ei.getS1()*ei.parameter(1);
		    else
			    e = defScale * intS1 * dist_max * forceZoomFact ;
          if ((forcedScale1 != 0) && (forcedScale2 != 0))
             e *= forcedScale2 / forcedScale1 ;
		    double ct = Math.cos(ei.getAlpha());
		    double st = Math.sin(ei.getAlpha());
		    polygon(ci,(float) (nodes[0].x()+e*st),
			    (float) (nodes[0].y()-e*ct),
			    (float) (nodes[1].x()+e*st),
			    (float) (nodes[1].y()-e*ct),
			    (float) (nodes[1].x()-e*st),
			    (float) (nodes[1].y()+e*ct),
			    (float) (nodes[0].x()-e*st),
			    (float) (nodes[0].y()+e*ct));
		}
	    }
	    else if (ei.type().equals("Conc3N")) {
		//COLOURED ELEMENT
		double intS1 = Math.abs(ei.getPercentS1());
		double intS2 = Math.abs(ei.getPercentS2());
		double intS = intS1;
		if (intS2 > intS1) intS = intS2;
      if (Math.abs(intS) > 1) intS = 1;
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};
		//Couloured elements
		Color ci = new Color((float)(1.0-intS),
				     (float)(1.0-intS),
				     (float)(1.0-intS));
		//Activating the following line disables colouring concrete
		//ci = new Color(1.0f,1.0f,1.0f);
		//Stress intensity
      if (noShade) ci = new Color(1.0f,1.0f,1.0f);
		polygon(ci,nodes[0].x(),nodes[0].y(),
			nodes[1].x(),nodes[1].y(),
			nodes[2].x(),nodes[2].y());
		//Undeformed grid
		closedPolyline(grey,nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());
	    }
	}
	//FINALLY CONCRETE MAIN DIRECTIONS AND CABLE STRESSES ARE DRAWN


	//Initializing the values of the strucStore class so that you dont get the exception

	itElems = ((Collection) mElems.values()).iterator();
	int index = 0;//keeps the count of the number of elements in the list
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    StrucStore eiStore = new StrucStore();
	    if (ei.type().equals("Conc3N")) {

		double intS1 = Math.abs(ei.getPercentS1());
		//System.out.println(ei.getPercentS1());
		double intS2 = Math.abs(ei.getPercentS2());
		double intS = (intS1 + intS2)*0.5;
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};
		//Directions
		double midNodeX =
		    (nodes[0].x()+nodes[1].x()+nodes[2].x())/3;
		double midNodeY =
		    (nodes[0].y()+nodes[1].y()+nodes[2].y())/3;
		double lenMax = Math.sqrt(Math.pow((nodes[0].x()-nodes[1].x()),2) + Math.pow((nodes[0].y()-nodes[1].y()),2));
		//double lenMax = getLength(nodes[0].x(), nodes[0].y(), nodes[1].x(), nodes[1].y());
		Color c1;
		Color c2;
		if (ei.getEps1() > 0) { c1 = red; }
		else { c1 = blue; }
		if (ei.getEps2() > 0) { c2 = red; }
		else { c2 = blue; }
      double lenVec = defScale * dist_max * intS1;
      if (maxLenVec >= 0)
         if ((int)(2 * lenVec * scale) > maxLenVec)
            lenVec = maxLenVec / scale;
		double v1x = lenVec * Math.cos(ei.getAlpha());
		double v1y = lenVec * Math.sin(ei.getAlpha());
      lenVec = defScale*dist_max*intS2;
      if (maxLenVec >= 0)
         if ((int)(2 * lenVec * scale) > maxLenVec)
            lenVec = maxLenVec / scale;
		double v2x = -lenVec * Math.sin(ei.getAlpha());
		double v2y = lenVec * Math.cos(ei.getAlpha());
		double dist = 2*Math.sqrt(v1x*v1x+v1y*v1y);
	/*	double dist = getLength((float)(midNodeX-v1x), (float)(midNodeY-v1y), (float)(midNodeX+v1x), (float)(midNodeY+v1y));

		if(dist<getLength((float)(midNodeX-v2x), (float)(midNodeY-v2y), (float)(midNodeX+v2x), (float)(midNodeY+v2y))) {

		}*/
		//System.out.println("The value of dist and lenMax are : "+dist+" "+lenMax);
		double width = getLength((float)(midNodeX-v2x), (float)(midNodeY-v2y), (float)(midNodeX+v2x), (float)(midNodeY+v2y));
/*		if(par.equals("concThick")) {
		if(dist<0.0015*lenMax) {
			if (par.compareTo("cables") != 0 && par.compareTo("forceBars") != 0 && par.compareTo("forceCables")!=0) {
				openLine(c1,(float)(midNodeX-v1x),(float)(midNodeY-v1y),
					(float)(midNodeX+v1x),(float)(midNodeY+v1y));
				openLine(c2,(float)(midNodeX-v2x),(float)(midNodeY-v2y),
					(float)(midNodeX+v2x),(float)(midNodeY+v2y));
			}
			eiStore.defScale = (float)defScale;
			eiStore.scale = scale;
			eiStore.dist_max = dist_max;
			eiStore.sinAlpha = (float)Math.sin(ei.getAlpha());
			eiStore.cosAlpha = (float)Math.cos(ei.getAlpha());
			eiStore.intS1 = intS1;
			eiStore.intS2 = intS2;
			flag = true;
			listStore.set(index,eiStore);
		}
		else {
			flag = false;
			eiStore = (StrucStore) listStore.get(index);
			if (par.compareTo("cables") != 0 && par.compareTo("forceBars") != 0 && par.compareTo("forceCables")!=0) {

				v1x = eiStore.defScale*eiStore.dist_max*eiStore.intS1*eiStore.cosAlpha;
				v1y = eiStore.defScale*eiStore.dist_max*eiStore.intS1*eiStore.sinAlpha;
				v2x = -eiStore.defScale*eiStore.dist_max*eiStore.intS2*eiStore.sinAlpha;
				v2y = eiStore.defScale*eiStore.dist_max*eiStore.intS2*eiStore.cosAlpha;
				openLine(c1,(float)(midNodeX-v1x),(float)(midNodeY-v1y),
					(float)(midNodeX+v1x),(float)(midNodeY+v1y),(float) (counter*width/100),scale);
				openLine(c2,(float)(midNodeX-v2x),(float)(midNodeY-v2y),
					(float)(midNodeX+v2x),(float)(midNodeY+v2y), (float) (counter*width/100),scale);
		//		counter+=0.00001;
			}
		}
		index++;
		}
		else */
      if(par.equals("concLen") | par.equals("all")) {
         g2d.setStroke(s);
         if (Math.abs(ei.getPercentS1()) > minStressLevel)
				openLine(c1,(float)(midNodeX-v1x),(float)(midNodeY-v1y),
					(float)(midNodeX+v1x),(float)(midNodeY+v1y));
         if (Math.abs(ei.getPercentS2()) > minStressLevel)
				openLine(c2,(float)(midNodeX-v2x),(float)(midNodeY-v2y),
					(float)(midNodeX+v2x),(float)(midNodeY+v2y));

		}
	    }
	    if (ei.type().equals("Cable") & (par.equals("cables") | par.equals("forceCables"))) {
		Node[] nodes = {ei.node(0), ei.node(1)};
		double intS1 = Math.abs(ei.getPercentS1());
		Color ci;
		if (ei.getPercentS1() >= 0) {
		    ci = new Color(0.1f, (float) (0.9 - 0.5*intS1), 0.1f);
		}
		else {
		    ci = new Color(0.1f, 0.1f, (float) (0.9 - 0.5*intS1));
		}
		double e = defScale * intS1 * dist_max;
		double ct = Math.cos(ei.getAlpha());
		double st = Math.sin(ei.getAlpha());
		polygon(ci,(float) (nodes[0].x()+e*st),
			(float) (nodes[0].y()-e*ct),
			(float) (nodes[1].x()+e*st),
			(float) (nodes[1].y()-e*ct),
			(float) (nodes[1].x()-e*st),
			(float) (nodes[1].y()+e*ct),
			(float) (nodes[0].x()-e*st),
			(float) (nodes[0].y()+e*ct));
	    }
	}
    }



    private void drawEta (Map mElems, String par) {
	Color grey = new Color(0.8f,0.8f,0.8f);
	Iterator itElems = ((Collection) mElems.values()).iterator();
	double valMin = 1.1;
	int eleMin = 0;
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Conc3N")) {
		double intS = 1.0;
		if (par.equals("dir1")) intS = ei.getEta1();
		else if (par.equals("dir2")) intS = ei.getEta2();
		if (intS < valMin) {
		    valMin = intS;
		    eleMin = ei.id();
		}
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};
		Color ci = new Color((float)(intS),
				     (float)(intS),
				     (float)(intS));
		polygon(ci,nodes[0].x(),nodes[0].y(),
			nodes[1].x(),nodes[1].y(),
			nodes[2].x(),nodes[2].y());
		closedPolyline(grey,nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());
	    }
	}
	//WRITES THE MINIMUM VALUE
	Element eMin = (Element) mElems.get(new Integer(eleMin));
	Node[] nMin = {eMin.node(0), eMin.node(1), eMin.node(2)};
	drawString (Color.black, Jconc.formatNumber(valMin, digits),
		    (nMin[0].x() + nMin[1].x() + nMin[2].x())/3,
		    (nMin[0].y() + nMin[1].y() + nMin[2].y())/3);
    }

    private void calcValues(Map mElems){

    	Iterator itElems = ((Collection) mElems.values()).iterator();
	countConc = 0;
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Conc3N"))
		    countConc++;
	}
	itElems = ((Collection) mElems.values()).iterator();
	stEps1Conc = new double[countConc];
	stEps2 = new double[countConc];
	stS1Conc = new double[countConc];
	stS2Conc = new double[countConc];
       	countConc1 = 0;
	countConc2 = 0;
	countConc3 = 0;
	countConc4 = 0;
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Conc3N")){
		    double temp = roundDBL(ei.getEps1()*10000000);
		    boolean flag = true;
		    for(int i = 0; i<countConc1;i++)
			    if(stEps1Conc[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stEps1Conc[countConc1++] =  temp;
		    temp = roundDBL(ei.getEps2()*10000000);
		    flag = true;
		    for(int i = 0; i<countConc2;i++)
			    if(stEps2[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stEps2[countConc2++] = temp;
		    temp = roundDBL(ei.getS1()*100);
		    flag = true;
		    for(int i = 0; i<countConc3;i++)
			    if(stS1Conc[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stS1Conc[countConc3++] = temp;
		    temp = roundDBL(ei.getS2()*100);
		    flag = true;
		    for(int i = 0; i<countConc4;i++)
			    if(stS2Conc[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stS2Conc[countConc4++] = temp;
	    }
	}
	double tmp = 0;

	for (int i=0; i<countConc1 -1; i++) {
	  for (int j=0; j<countConc1-1-i; j++){
	    if (stEps1Conc[j+1] < stEps1Conc[j]) {
	      tmp = stEps1Conc[j];
	      stEps1Conc[j] = stEps1Conc[j+1];
	      stEps1Conc[j+1] = tmp;
	    }
	  }
	}
	for (int i=0; i<countConc2 -1; i++) {
	  for (int j=0; j<countConc2-1-i; j++){
	    if (stEps2[j+1] < stEps2[j]) {

	    	tmp = stEps2[j];
		stEps2[j] = stEps2[j+1];
		stEps2[j+1] = tmp;
	    }
	  }
	}
	for (int i=0; i<countConc3 -1; i++) {
	  for (int j=0; j<countConc3-1-i; j++){
	    if (stS1Conc[j+1] < stS1Conc[j]) {
	      tmp = stS1Conc[j];
	      stS1Conc[j] = stS1Conc[j+1];
	      stS1Conc[j+1] = tmp;
	    }
	  }
	}
	for (int i=0; i<countConc4 -1; i++) {
	  for (int j=0; j<countConc4-1-i; j++){
	    if (stS2Conc[j+1] < stS2Conc[j]) {
	      tmp = stS2Conc[j];
	      stS2Conc[j] = stS2Conc[j+1];
	      stS2Conc[j+1] = tmp;
	    }
	  }
	}

	itElems = ((Collection) mElems.values()).iterator();
	countBar = 0;
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Bar"))
		    countBar++;
	}
	itElems = ((Collection) mElems.values()).iterator();
	stEps1Bar = new double[countBar];
	stS1Bar = new double[countBar];
	stForceBar = new double[countBar];
       	countBar1 = 0;
	countBar2 = 0;
	countBar3 = 0;
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Bar")){
		    double temp = roundDBL (ei.getEps1()*10000000);
		    boolean flag = true;
		    for(int i = 0; i<countBar1;i++)
			    if(stEps1Bar[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stEps1Bar[countBar1++] = temp;
		    temp = roundDBL (ei.getS1()*100);
		    flag = true;
		    for(int i = 0; i<countBar2;i++)
			    if(stS1Bar[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stS1Bar[countBar2++] = temp;
		    temp = roundDBL (ei.getS1()*ei.parameter(1)*1000);
		    flag = true;
		    for(int i = 0; i<countBar3;i++)
			    if(stForceBar[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag){
			    stForceBar[countBar3++] = temp;
		    }
	    }
	}
	tmp = 0;
	for (int i=0; i<countBar1 -1; i++) {
	  for (int j=0; j<countBar1-1-i; j++){
	    if (stEps1Bar[j+1] < stEps1Bar[j]) {
	      tmp = stEps1Bar[j];
	      stEps1Bar[j] = stEps1Bar[j+1];
	      stEps1Bar[j+1] = tmp;
	    }
	  }
	}
	for (int i=0; i<countBar2 -1; i++) {
	  for (int j=0; j<countBar2-1-i; j++){

	    if (stS1Bar[j+1] < stS1Bar[j]) {

	    	tmp = stS1Bar[j];
		stS1Bar[j] = stS1Bar[j+1];
		stS1Bar[j+1] = tmp;
	    }
	  }
	}
	for (int i=0; i<countBar3 -1; i++) {
	  for (int j=0; j<countBar3-1-i; j++){
	    if (stForceBar[j+1] < stForceBar[j]) {

	    	tmp = stForceBar[j];
		stForceBar[j] = stForceBar[j+1];
		stForceBar[j+1] = tmp;
	    }
	  }
	}
	itElems = ((Collection) mElems.values()).iterator();
	countCab = 0;
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Cable"))
		    countCab++;
	}
	itElems = ((Collection) mElems.values()).iterator();
	stEps1Cab = new double[countCab];
	stS1Cab = new double[countCab];
	stForceCab = new double[countCab];
       	countCab1 = 0;
	countCab2 = 0;
	countCab3 = 0;

	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Cable")){
		    double temp = roundDBL (ei.getEps1()*10000000);
		    boolean flag = true;
		    for(int i = 0; i<countCab1;i++)
			    if(stEps1Cab[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stEps1Cab[countCab1++] = temp;
		    temp = roundDBL (ei.getS1()*100);
		    flag = true;
		    for(int i = 0; i<countCab2;i++)
			    if(stS1Cab[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stS1Cab[countCab2++] = temp;
		    temp = roundDBL (ei.getS1()*ei.parameter(1)*1000);
		    flag = true;
		    for(int i = 0; i<countCab3;i++)
			    if(stForceCab[i] == temp){
				    flag = false;
				    break;
			    }
		    if(flag)
			    stForceCab[countCab3++] = temp;
	    }
	}
	tmp = 0;

	for (int i=0; i<countCab1 -1; i++) {
	  for (int j=0; j<countCab1-1-i; j++){
	    if (stEps1Cab[j+1] < stEps1Cab[j]) {
	      tmp = stEps1Cab[j];
	      stEps1Cab[j] = stEps1Cab[j+1];
	      stEps1Cab[j+1] = tmp;
	    }
	  }
	}
	for (int i=0; i<countCab2 -1; i++) {
	  for (int j=0; j<countCab2-1-i; j++){
	    if (stS1Cab[j+1] < stS1Cab[j]) {

	    	tmp = stS1Cab[j];
		stS1Cab[j] = stS1Cab[j+1];
		stS1Cab[j+1] = tmp;
	    }
	  }
	}
	for (int i=0; i<countCab3 -1; i++) {
	  for (int j=0; j<countCab3-1-i; j++){
	    if (stForceCab[j+1] < stForceCab[j]) {

	    	tmp = stForceCab[j];
		stForceCab[j] = stForceCab[j+1];
		stForceCab[j+1] = tmp;
	    }
	  }
	}
    }

    private void drawEps (Map mElems, String par) {
	Color grey = new Color(0.8f,0.8f,0.8f);
	int count1 = 0;
	int count2 = 0;
	int count3 = 0;
	int count4 = 0;
	for(int i = 0; i<countConc; i++){
		if(stEps1Conc[i]<0)
			count1++;
		if(stEps2[i]<0)
			count2++;
		if(stS1Conc[i]<0)
			count3++;
		if(stS2Conc[i]<0)
			count4++;

	}
	Iterator itElems = ((Collection)mElems.values()).iterator();
	while (itElems.hasNext()){

	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Conc3N")) {

		double eps1 = 0;
		double eps2 = 0;
		eps1 = roundDBL(ei.getEps1()*10000000);
		eps2 = roundDBL(ei.getEps2()*10000000);
		double s1 = roundDBL(ei.getS1()*100);
		double s2 = roundDBL(ei.getS2()*100);

		float r = 1;
		float b = 1;
		float g = 1;
		if(par.equals("eps1")){
			for(int i = (countConc1-1); i>=0;i--)
				if(eps1 == stEps1Conc[i]){

					if(i<count1){
						r = (i/(float)count1)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count1)/(float)(countConc1-count1)*1f));
						g = b;
						r = 1;

					}
					break;
				}
		}
		else if (par.equals("eps2")){
			for(int i = (countConc2-1); i>=0;i--)
				if((float)eps2 == (float)stEps2[i]){

					if(i<count2){
						r = (i/(float)count2)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count2)/(float)(countConc2-count2)*1f));
						g = b;
						r = 1;
					}
					break;
				}
		}
		else if (par.equals("s1")){
			for(int i = (countConc3-1); i>=0;i--)
				if((float)s1 == (float)stS1Conc[i]){

					if(i<count3){
						r = (i/(float)count3)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count3)/(float)(countConc3-count3)*1f));
						g = b;
						r = 1;
					}
					break;
				}
			}
		else if (par.equals("s2")){
			for(int i = (countConc4-1); i>=0;i--)
				if((float)s2 == (float)stS2Conc[i]){

					if(i<count4){
						r = (i/(float)count4)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count4)/(float)(countConc4-count4)*1f));
						g = b;
						r = 1;
					}
					break;
				}
		}
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};
		Color ci = new Color(r,g,b);
		polygon(ci,nodes[0].x(),nodes[0].y(),
			nodes[1].x(),nodes[1].y(),
			nodes[2].x(),nodes[2].y());
		closedPolyline(grey,nodes[0].x(),nodes[0].y(),
			       nodes[1].x(),nodes[1].y(),
			       nodes[2].x(),nodes[2].y());
	    }
	}
/*	//WRITES THE MINIMUM VALUE
	Element eMin = (Element) mElems.get(new Integer(eleMin));
	Node[] nMin = {eMin.node(0), eMin.node(1), eMin.node(2)};
	NumberFormat nf = new DecimalFormat("#.##");
	drawString (Color.black, nf.format(valMin),
		    (nMin[0].x() + nMin[1].x() + nMin[2].x())/3,
		    (nMin[0].y() + nMin[1].y() + nMin[2].y())/3);*/
    }
    private void drawBars (Map mElems, String par) {
	Color grey = new Color(0.8f,0.8f,0.8f);
	int count1 = 0;
	int count2 = 0;
	int count3 = 0;
	for(int i = 0; i<countBar; i++){
		if(stEps1Bar[i]<0)
			count1++;
		if(stS1Bar[i]<0)
			count2++;
		if(stForceBar[i]<0)
			count3++;

	}

	Iterator itElems = ((Collection)mElems.values()).iterator();
	while (itElems.hasNext()){

	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Bar")) {

		double eps1 = 0;
		double s1 = 0;
		double force = 0;
		eps1 = ei.getEps1()*10000000;
		s1 = ei.getS1()*100;
		force = ei.getS1()*ei.parameter(1)*1000;
		eps1 = roundDBL(eps1);
		s1 = roundDBL (s1);
		force = roundDBL(force);

		float r = 1;
		float g = 1;
		float b = 1;
		if(par.equals("eps1")){
			for(int i = (countBar1-1); i>=0;i--)
				if(eps1==stEps1Bar[i]){

					if(i<count1){
						r = (i/(float)count1)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count1)/(float)(countBar1-count1)*1f));
						g = b;
						r = 1;
					}
					break;
				}
		}
		else if (par.equals("s1")){
			for(int i = (countBar2-1); i>=0;i--)
				if(s1 == stS1Bar[i]){

					if(i<count2){
						r = (i/(float)count2)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count2)/(float)(countBar2-count2)*1f));
						g = b;
						r = 1;
					}
					break;
				}
		}
		else if (par.equals("force")){
			for(int i = (countBar3-1); i>=0;i--)
				if(force ==stForceBar[i]){

					if(i<count3){
						r = (i/(float)count3)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count3)/(float)(countBar3-count3)*1f));
						g = b;
						r = 1;
					}

					break;
				}
		}
		Node[] nodes = {ei.node(0), ei.node(1)};
		Color ci = new Color(r,g,b);
		double e = 0.0025 * dist_max;
		double ct = Math.cos(ei.getAlpha());
		double st = Math.sin(ei.getAlpha());
		polygon(ci,(float) (nodes[0].x()+e*st),
				(float) (nodes[0].y()-e*ct),
				(float) (nodes[1].x()+e*st),
				(float) (nodes[1].y()-e*ct),
				(float) (nodes[1].x()-e*st),
				(float) (nodes[1].y()+e*ct),
				(float) (nodes[0].x()-e*st),
				(float) (nodes[0].y()+e*ct));
	    }
	    else if (ei.type().equals("Conc3N")){
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};
		closedPolyline(grey,nodes[0].x(),nodes[0].y(),
		       nodes[1].x(),nodes[1].y(),
		       nodes[2].x(),nodes[2].y());
	    }

	}
/*	//WRITES THE MINIMUM VALUE
	Element eMin = (Element) mElems.get(new Integer(eleMin));
	Node[] nMin = {eMin.node(0), eMin.node(1), eMin.node(2)};
	NumberFormat nf = new DecimalFormat("#.##");
	drawString (Color.black, nf.format(valMin),
		    (nMin[0].x() + nMin[1].x() + nMin[2].x())/3,
		    (nMin[0].y() + nMin[1].y() + nMin[2].y())/3);*/
    }
	public double roundDBL(double targetDBL){
		int decimalPlace = 2;
		BigDecimal bd = new BigDecimal(targetDBL);
		bd = bd.setScale(decimalPlace,BigDecimal.ROUND_UP);

		return (bd.doubleValue());
	}


    private void drawCables (Map mElems, String par) {
	Color grey = new Color(0.8f,0.8f,0.8f);
	int count1 = 0;
	int count2 = 0;
	int count3 = 0;
	for(int i = 0; i<countCab; i++){
		if(stEps1Cab[i]<0)
			count1++;
		if(stS1Cab[i]<0)
			count2++;
		if(stForceCab[i]<0)
			count3++;

	}

	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()){

	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Cable")) {

		double eps1 = 0;
		double s1 = 0;
		double force = 0;
		eps1 = ei.getEps1()*10000000;
		s1 = ei.getS1()*100;
		force = ei.getS1()*ei.parameter(1)*1000;
		eps1 = roundDBL(eps1);
		s1 = roundDBL(s1);
		force = roundDBL(force);

		float r = 1;
		float g = 1;
		float b = 1;
		if(par.equals("eps1")){
			for(int i = (countCab1-1); i>=0;i--)
				if(eps1==stEps1Cab[i]){

					if(i<count1){
						r = (i/(float)count1)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count1)/(float)(countCab1-count1)*1f));
						g = b;
						r = 1;
					}
					break;
				}
		}
		else if (par.equals("s1")){
			for(int i = (countCab2-1); i>=0;i--)
				if(s1 == stS1Cab[i]){
					if(i<count2){
						r = (i/(float)count2)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count2)/(float)(countCab2-count2)*1f));
						g = b;
						r = 1;
					}
					break;
				}
		}
		else if (par.equals("force")){
			for(int i = (countCab3-1); i>=0;i--)
				if(force == stForceCab[i]){
					if(i<count3){
						r = (i/(float)count3)*1f;
						g = r;
						b = 1;
					}
					else{

						b = (1f - ((i+1-count3)/(float)(countCab3-count3)*1f));
						g = b;
						r = 1;
					}

					break;
				}
		}
		Node[] nodes = {ei.node(0), ei.node(1)};
		Color ci = new Color(r,g,b);
		double e = 0.0025 * dist_max;
		double ct = Math.cos(ei.getAlpha());
		double st = Math.sin(ei.getAlpha());
		polygon(ci,(float) (nodes[0].x()+e*st),
				(float) (nodes[0].y()-e*ct),
				(float) (nodes[1].x()+e*st),
				(float) (nodes[1].y()-e*ct),
				(float) (nodes[1].x()-e*st),
				(float) (nodes[1].y()+e*ct),
				(float) (nodes[0].x()-e*st),
				(float) (nodes[0].y()+e*ct));
	    }
	    else if (ei.type().equals("Conc3N")){
		Node[] nodes = {ei.node(0), ei.node(1), ei.node(2)};
		closedPolyline(grey,nodes[0].x(),nodes[0].y(),
		       nodes[1].x(),nodes[1].y(),
		       nodes[2].x(),nodes[2].y());
	    }

	}
/*	//WRITES THE MINIMUM VALUE
	Element eMin = (Element) mElems.get(new Integer(eleMin));
	Node[] nMin = {eMin.node(0), eMin.node(1), eMin.node(2)};
	NumberFormat nf = new DecimalFormat("#.##");
	drawString (Color.black, nf.format(valMin),
		    (nMin[0].x() + nMin[1].x() + nMin[2].x())/3,
		    (nMin[0].y() + nMin[1].y() + nMin[2].y())/3);*/
    }

   private void drawBoundaries (Map mBouns) {
	Color colBoun = Color.RED;
	Color colBoun2 = new Color(0.2f, 0.8f, 0.1f);
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {
	    Boun bi = (Boun) itBouns.next();
	    Node ni = model.getNode(bi.node());
	    if ((bi.type().equals("all")) | (bi.type().equals("ux"))) {
		Color colB;
		if (bi.disp()==0) {
		    colB = colBoun;
		}
		else {
		    colB = colBoun2;
		}
		closedPolyline(colB,ni.x(),ni.y(),
			       (float)(ni.x()+0.05*dist_max),
			       (float)(ni.y()-0.025*dist_max),
			       (float)(ni.x()+0.05*dist_max),
			       (float)(ni.y()+0.025*dist_max));
	    }
	    if ((bi.type().equals("all")) | (bi.type().equals("uy"))) {
		Color colB;
		if (bi.disp()==0) {
		    colB = colBoun;
		}
		else {
		    colB = colBoun2;
		}
		closedPolyline(colB,ni.x(),ni.y(),
			       (float)(ni.x()+0.025*dist_max),
			       (float)(ni.y()-0.05*dist_max),
			       (float)(ni.x()-0.025*dist_max),
			       (float)(ni.y()-0.05*dist_max));
	    }
	}
    }

    private void drawLoads (Map mLoads) {
	double scaleF = 0.0;
	Color colLoad = new Color(1.0f, 0.1f, 0.1f);
	Iterator itLoads = ((Collection) mLoads.values()).iterator();
	while (itLoads.hasNext()) {
	    Load li = (Load) itLoads.next();
	    if ((Math.sqrt(li.fx()*li.fx()+li.fy()*li.fy())) > scaleF)
		scaleF = Math.sqrt(li.fx()*li.fx()+li.fy()*li.fy());
	}
	scaleF = 0.25*dist_max/scaleF;
	itLoads = ((Collection) mLoads.values()).iterator();
	while (itLoads.hasNext()) {
	    Load li = (Load) itLoads.next();
	    Node ni = model.getNode(li.node());
	    openLine(colLoad,ni.x(),ni.y(),
		     (float)(ni.x()-scaleF*li.fx()),
		     (float)(ni.y()-scaleF*li.fy()));
	    float p1_x = (float)(ni.x()-scaleF*0.2*li.fx()-scaleF*0.1*li.fy());
	    float p1_y = (float)(ni.y()+scaleF*0.1*li.fx()-scaleF*0.2*li.fy());
	    float p2_x = (float)(ni.x()-scaleF*0.2*li.fx()+scaleF*0.1*li.fy());
	    float p2_y = (float)(ni.y()-scaleF*0.1*li.fx()-scaleF*0.2*li.fy());
	    polygon(colLoad,ni.x(),ni.y(),p1_x,p1_y,p2_x,p2_y);
	}
    }
    private void closedPolyline (Color color, float x0, float y0,
			   float x1, float y1,
			   float x2, float y2) {
	g2d.setColor(color);
	int xd0 = (int) ((x0-x_c)*scale + xd_c);
	int yd0 = (int) (-(y0-y_c)*scale + yd_c);
	int xd1 = (int) ((x1-x_c)*scale + xd_c);
	int yd1 = (int) (-(y1-y_c)*scale + yd_c);
	int xd2 = (int) ((x2-x_c)*scale + xd_c);
	int yd2 = (int) (-(y2-y_c)*scale + yd_c);
	int[] coorx = {xd0,xd1,xd2};
	int[] coory = {yd0,yd1,yd2};
	g2d.drawPolygon(coorx,coory,3);
    }

    private void closedPolyline (Color color, float x0, float y0,
			   float x1, float y1,
			   float x2, float y2,
			   float x3, float y3) {
	g2d.setColor(color);
	int xd0 = (int) ((x0-x_c)*scale + xd_c);
	int yd0 = (int) (-(y0-y_c)*scale + yd_c);
	int xd1 = (int) ((x1-x_c)*scale + xd_c);
	int yd1 = (int) (-(y1-y_c)*scale + yd_c);
	int xd2 = (int) ((x2-x_c)*scale + xd_c);
	int yd2 = (int) (-(y2-y_c)*scale + yd_c);
	int xd3 = (int) ((x3-x_c)*scale + xd_c);
	int yd3 = (int) (-(y3-y_c)*scale + yd_c);
	int[] coorx = {xd0,xd1,xd2,xd3};
	int[] coory = {yd0,yd1,yd2,yd3};
	g2d.drawPolygon(coorx,coory,4);
    }

    private void openLine (Color color, float x0, float y0,
			   float x1, float y1) {
	g2d.setColor(color);
	int xd0 = (int) ((x0-x_c)*scale + xd_c);
	int yd0 = (int) (-(y0-y_c)*scale + yd_c);
	int xd1 = (int) ((x1-x_c)*scale + xd_c);
	int yd1 = (int) (-(y1-y_c)*scale + yd_c);

	g2d.drawLine(xd0,yd0,xd1,yd1);
    }

    //Introduced code --------------------- Rohit


    private double getLength(float x0, float y0, float x1, float y1) {

	int xd0 = (int) ((x0-x_c)*scale + xd_c);
	int yd0 = (int) (-(y0-y_c)*scale + yd_c);
	int xd1 = (int) ((x1-x_c)*scale + xd_c);
	int yd1 = (int) (-(y1-y_c)*scale + yd_c);
	return (Math.sqrt(Math.pow(xd0-xd1,2)+Math.pow(yd0-yd1,2)));

    }
/*    private void openLine (Color color, float x0, float y0,
			   float x1, float y1, float counter, double scaleStore) {
	g2d.setColor(color);
	g2d.setStroke(new BasicStroke(1+counter));
	int xd0 = (int) ((x0-x_c)*scaleStore + xd_c);
	int yd0 = (int) (-(y0-y_c)*scaleStore + yd_c);
	int xd1 = (int) ((x1-x_c)*scaleStore + xd_c);
	int yd1 = (int) (-(y1-y_c)*scaleStore + yd_c);
	g2d.drawLine(xd0,yd0,xd1,yd1);
	g2d.setStroke(new BasicStroke(1));
    }
    //End of introduced code ------------------- Rohit
*/
    private void drawString (Color color, String cad, float x0, float y0) {
	g2d.setColor(color);
	int xd0 = (int) ((x0-x_c)*scale + xd_c);
	int yd0 = (int) (-(y0-y_c)*scale + yd_c);
	g2d.drawString(cad, xd0, yd0);
    }

    private void polygon (Color color, float x0, float y0,
			   float x1, float y1,
			   float x2, float y2) {
	g2d.setColor(color);
	int xd0 = (int) ((x0-x_c)*scale + xd_c);
	int yd0 = (int) (-(y0-y_c)*scale + yd_c);
	int xd1 = (int) ((x1-x_c)*scale + xd_c);
	int yd1 = (int) (-(y1-y_c)*scale + yd_c);
	int xd2 = (int) ((x2-x_c)*scale + xd_c);
	int yd2 = (int) (-(y2-y_c)*scale + yd_c);
	int[] coorx = {xd0,xd1,xd2};
	int[] coory = {yd0,yd1,yd2};
	g2d.fillPolygon(coorx,coory,3);
    }

    private void polygon (Color color, float x0, float y0,
			   float x1, float y1,
			   float x2, float y2,
			   float x3, float y3) {
	g2d.setColor(color);
	int xd0 = (int) ((x0-x_c)*scale + xd_c);
	int yd0 = (int) (-(y0-y_c)*scale + yd_c);
	int xd1 = (int) ((x1-x_c)*scale + xd_c);
	int yd1 = (int) (-(y1-y_c)*scale + yd_c);
	int xd2 = (int) ((x2-x_c)*scale + xd_c);
	int yd2 = (int) (-(y2-y_c)*scale + yd_c);
	int xd3 = (int) ((x3-x_c)*scale + xd_c);
	int yd3 = (int) (-(y3-y_c)*scale + yd_c);
	int[] coorx = {xd0,xd1,xd2,xd3};
	int[] coory = {yd0,yd1,yd2,yd3};
	g2d.fillPolygon(coorx,coory,4);
    }
//Introduced code --------------------- Rohit
    private void polygon (Color color, float x0, float y0, float x1, float y1, float counter, double scaleStore) {

	g2d.setColor(color);
	int xd0 = (int) ((x0-x_c)*scaleStore + xd_c);
	int yd0 = (int) (-(y0-y_c)*scaleStore + yd_c);
	int xd1 = (int) ((x1-x_c)*scaleStore + xd_c);
	int yd1 = (int) (-(y1-y_c)*scaleStore + yd_c);
	x1 = xd0; y1 = yd0;
	float x2 = xd1; float y2 = yd1;
	int dX = (int)(x2 - x1);
	int dY = (int)(y2 - y1);
	// line length
	double lineLength = Math.sqrt(dX * dX + dY * dY);
	double sc = (double)(counter) / (2 * lineLength);
	// The x and y increments from an endpoint needed to create a rectangle...
  	double ddx = -sc * (double)dY;
  	double ddy = sc * (double)dX;
  	ddx += (ddx > 0) ? 0.5 : -0.5;
  	ddy += (ddy > 0) ? 0.5 : -0.5;
  	int dx = (int)ddx;
  	int dy = (int)ddy;

	// Now we can compute the corner points...
  	int xPoints[] = new int[4];
  	int yPoints[] = new int[4];

	xPoints[0] = (int)x1 + dx; yPoints[0] = (int)y1 + dy;
  	xPoints[1] = (int)x1 - dx; yPoints[1] = (int)y1 - dy;
  	xPoints[2] =(int) x2 - dx; yPoints[2] = (int)y2 - dy;
  	xPoints[3] = (int)x2 + dx; yPoints[3] = (int)y2 + dy;
	g2d.fillPolygon(xPoints, yPoints, 4);
    }
//End of introduced code -------------- Rohit
    public RenderedImage getCanvasImage () {
	BufferedImage bi =
	    new BufferedImage(getSize().width,getSize().height,
			      BufferedImage.TYPE_INT_RGB);
	Graphics gbi = bi.createGraphics();
	paint(gbi);
	gbi.dispose();
	RenderedImage rendImage = bi;
	return rendImage;
    }

    public class StrucStore {

    	public float cosAlpha;
	public float sinAlpha;
	public float defScale;
	public float dist_max;
	public double scale;
	public double intS1;
	public double intS2;

	public StrucStore() {

		cosAlpha = 0;
		sinAlpha = 0;
		defScale = 0;
		dist_max = 0;
		scale = 0;
		intS1 = 0;
		intS2 = 0;
	}

    }
}

