package net.cercis.jconc.ui;

import net.cercis.jconc.fem.Element;
import net.cercis.jconc.fem.Model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GradientPaint;
import java.awt.Dimension;
import java.awt.BasicStroke;
import java.awt.image.RenderedImage;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
public class Thickness extends JComponent{

    private Graphics2D g2d;
    private Model model;
    private double mIndexC[];
    private double mIndexB[];
    private Color mColorC[];
    private Color mColorB[];
    private int count1 = 0;
    private int count2 = 0;
    private List listCount;
    private boolean state;
    private StringFile sf;
    private boolean erase;


    public Thickness(Model model) {
	
	listCount = new LinkedList();
	state = true;
	erase = false;
	setBackground(Color.white);
	setBorder(BorderFactory.createLineBorder(Color.black));
	this.model = model;
	count1 = 0; //stores how many conc thicknesses there are
	count2 = 0; //stores how many bar thicknesses there are
	sf = Jconc.sf;
	countThick(model.getMapOfElems());
	generateColor();
	repaint();
    }

    public void updateModel(Model model) {
	
	count1 = 0;
	count2 = 0;
	this.model = model;
	//countThick(model.getMapOfElems());
	generateColor();
    }

    public void setState(boolean state) {
	
	this.state = state;
    }

    public void countThick(Map mElems){

	Iterator itElems = ((Collection) mElems.values()).iterator();

	while(itElems.hasNext()) {
		
	    Element ei = (Element) itElems.next();
	    if(ei.type().equals("Conc3N"))
		if(count1 == 0){
		    listCount.add(new Float(ei.parameter(1)));
		    count1++;
		}
		else {
		    boolean flag = true;
		    for(int i = 0; i<count1+count2; i++){
			if(listCount.indexOf(new Float(ei.parameter(1)))!=-1)
			    flag = false;
		    }
		    if(flag) {
					
			listCount.add(new Float(ei.parameter(1)));
			count1++;
		    }
		}
	    else if(ei.type().equals("Bar"))
		if(count2 == 0){
		    listCount.add(new Float(ei.parameter(1)));
		    count2++;
		}
		else {
		    boolean flag = true;
		    for(int i = 0; i<count2+count1; i++){
			if(listCount.indexOf(new Float(ei.parameter(1)))!=-1)
			    flag = false;
		    }
		    if(flag) {
					
			listCount.add(new Float(ei.parameter(1)));
			count2++;
		    }
		}
	}
	if (count1 > 0 ) 
      mIndexC = new double[count1];
   else
      mIndexC = new double[count1 + 1];
	mIndexB = new double[count2];
	count1 = 0;
	count2 = 0;
	itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if(ei.type().equals("Conc3N")){
		    if(count1 == 0)
		       mIndexC[count1++]=(float)ei.parameter(1);
		    else {
		       boolean flag = true;
		       for(int i=0; i<count1;i++){
			    if((mIndexC[i]-(float)ei.parameter(1))==0)
			       flag = false;
		       }
		       if(flag){
			       mIndexC[count1++] = (float)ei.parameter(1);
		       }
		    }
	    }
	    else if (ei.type().equals("Bar")){
		if(count2 == 0)
		    mIndexB[count2++]=(float)ei.parameter(1);
		else {
	
		    boolean flag = true;
		    for(int i=0; i<count2;i++){
			if((mIndexB[i]-(float)ei.parameter(1))==0)
			    flag = false;
		    }
		    if(flag){
			mIndexB[count2++] = (float)ei.parameter(1);
		    }	
		}
	    }
	}
    }

    public Color getColor(Element ei) {
	
	int pos = -1;
	if(ei.type().equals("Conc3N")){
	    pos = -1;
	    for(int i=0; i<count1; i++)
		if((float)mIndexC[i]==(float)ei.parameter(1))
		    pos = i;
	    if(pos!=-1)
		return mColorC[pos];
	    else 
		return Color.black;
	}
	else if(ei.type().equals("Bar")){
	    pos = -1;
	    for(int i=0; i<count2; i++)
		if((float)mIndexB[i]==(float)ei.parameter(1))
		    pos = i;
	    if(pos!=-1)
		return mColorB[pos];
	    else 
		return Color.black;
	}
	return Color.black;

    }

    private void generateColor() {
		
	mColorC = new Color[count1];
	mColorB = new Color[count2];
	float r = 0;
	float b = 1;
	float steps = 0;
	if(count1!=1)
	    steps = (float)1/(count1 - 1);
	else
	    steps = 0;
	r -= steps;
	b += steps;
	for(int i=0; i<count1;i++){
			
	    b -= steps;
	    r += steps;
	    if(r<0) r = 0;
	    if(b<0) b = 0;
	    if(r>1) r = 1;
	    if(b>1) b = 1;
	    mColorC[i] = new Color(r,0,b);
	}
	r = 0;
	b = 1;
	steps = 0;
	if(count2!=1)
	    steps = (float)1/(count2 - 1);
	else
	    steps = 0;
	r -= steps;
	b += steps;
	for(int i=0; i<count2;i++){			
	    b -= steps;
	    r += steps;
	    if(r<0) r = 0;
	    if(b<0) b = 0;
	    if(r>1) r = 1;
	    if(b>1) b = 1;
	    mColorB[i] = new Color(r,0,b);
	}	
    }
    
    public void paint(Graphics g) {

	g2d = (Graphics2D) g;
	int step  = 0;
	this.model = model;
	g2d.setColor(getBackground());
	g2d.fillRect(getWidth()-100, 0, 100, getHeight()); //To get a white background for the bar
	g2d.setColor(Color.black);
	if(!erase)
	    g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
	g2d.drawLine(getWidth()-100,getHeight()-1,99,getHeight()-1);

	//		g2d.drawRect(getWidth()-100, 0, 99, getHeight()-1); //To get a white background for the bar

	int posH =60;
	if(state)
	    for(int i=0; i<count1; i++){
		step = (getHeight()-90)/count1;
		String str = "";
				
		float temp = (float) mIndexC[i];
		str+=temp;
		g2d.setStroke(new BasicStroke(4));
		g2d.setColor(Color.black);
		g2d.drawString(str, getWidth()-60, posH+15);			
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(mColorC[i]);
		g2d.fillRect(getWidth()-80,posH,20,step);
		posH+=step;
	    }
	else
	    for(int i=0; i<count2; i++){
		step = (getHeight()-90)/count2;
		String str = "";
		float temp = (float) mIndexB[i];
		str+=temp;
		g2d.setStroke(new BasicStroke(4));
		g2d.setColor(Color.black);
		g2d.drawString(str, getWidth()-60, posH+15);			
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(mColorB[i]);
		g2d.fillRect(getWidth()-80,posH,20,step);
		posH+=step;
	    }
		
    }

    public void eraseLine(){
	
	g2d.setColor(Color.white);
	g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);

	g2d.setColor(Color.black);
	erase = true;
	repaint();
    }
    public void drawLine(){
	
	g2d.setColor(Color.black);
	g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
	g2d.setColor(Color.white);
	erase = false;
	repaint();
    }


    public void display() {
	
	repaint();	
    }

}
