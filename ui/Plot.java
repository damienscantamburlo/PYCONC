package net.cercis.jconc.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;

import javax.swing.JComponent;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.lang.Math;

public class Plot extends JComponent{

	private Graphics2D g2d;
	String values[];
	NumberFormat nf = new DecimalFormat("0.##E0");
	double noVal[];
	int count;
	int exp;
	String title;
	private boolean erase;
	public Plot(double values[], int count) {
	
		this.count = count;
		erase = false;
		this.values = new String[count];
		this.noVal = new double[count];
		noVal = values;
		for(int i=0;i<count;i++)
			this.values[i] = nf.format(values[i]);
		setBackground(Color.white);
		repaint();
	}
	public void setValues(double values[], int count,int exp, String title){
		
		
		this.count = count;
		this.exp = exp;
		this.title = title;
		noVal = new double[count];
		if(exp < 4)
			nf = new DecimalFormat("#.####");
		else
			nf = new DecimalFormat("0.##E0");
		this.values = new String[count];
		for(int i = 0; i<count;i++){
			this.noVal[i] = values[i];
			if(exp<4)
				this.values[i] = toString(values[i]/Math.pow(10,exp),3);
			else
				this.values[i] = nf.format(values[i]/Math.pow(10,exp));
		
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


	public void paint(Graphics g) {
	
		g2d = (Graphics2D) g;
		float step = 0;
		if(count>=1)
			step = (getHeight()-90)/(float)(count);
		g2d.setColor(getBackground());
		g2d.fillRect(getWidth()-100,0,100,getHeight());
		g2d.setColor(Color.black);
		if(!erase)
			g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
		g2d.drawLine(getWidth()-100,getHeight()-1,99,getHeight()-1);
		int count1 = 0;
		for (int i = 0;i<count;i++)
			if(noVal[i]<0)
				count1++;
		float posH = 60;
		g2d.setColor(Color.black);
	//	g2d.drawString(title, getWidth()-90,15);
		int textPos = count/10;
		if(textPos == 0)
			textPos = 1;
		boolean lastPos = ((count%textPos>(textPos/3))|(count%textPos == 0))?true :false;
		for(int i=0; i<count; i++){

			float r = 1;
			float b = 1;
			float gr = 1;			
			String str = "";
			str+=(values[i]);
			g2d.setColor(Color.black);
			if(i<count1) {
				r = (i/(float)count1)*1f;
				gr = r;
				b = 1;
			}
			else {
				b = (1f - ((i+1-count1)/(float)(count-count1)*1f));
				gr = b;
				r = 1;
			}
			if(i%textPos==0)
				g2d.drawString(str, getWidth()-60, posH + (step/2));
			if(lastPos && (i==(count-1)))
				g2d.drawString(str, getWidth()-60, posH + (step/2));
			g2d.setColor(new Color(r,gr,b));
			g2d.fillRect(getWidth()-80,(int)posH,20,(int)step+1);
			posH+=step;
		}
	}




	public static String toString(double x, int sigFigs) {
	    StringBuffer s;
	
	    if (x == 0)
	      s = new StringBuffer("0");
 	   else {
 	     boolean negative = false;

  	    if (x < 0) {
  	      negative = true;
  	      x = -x;
   	   }

   	   final double log10 = Math.log(10);
   	   int exp = (int) Math.floor(Math.log(x) / log10);
   	   s = new StringBuffer(sigFigs);    // might be bigger, but this
    	  // is a good first guess
	
    	  // scale up and round to appropriate place
   	   long intVal = Math.round(x * Math.pow(10, sigFigs - exp - 1));
    	  final String digits = Long.toString(intVal);

    	  int dIndex = 0;
    	  if (exp >= 0) {
    	    if (exp < sigFigs) {
     	     s.append(digits.substring(0, exp+1));
     	     dIndex += exp+1;
     	     if (dIndex < sigFigs) {
     	       s.append(".");
       	   }
      	    s.append(digits.substring(dIndex));
      	  }
     	   else {
      	    s.append(digits);
      	    for (int i = digits.length(); i <= exp; i++)
       	     s.append('0');
     	   }
    	  }
    	  else {
      	  s.append("0.");
      	  for (int i = -1; i > exp; i--) {
      	    s.append('0');
      	  }
     	   s.append(digits.substring(dIndex));
    	  }

   	   if (negative)
   	     s.insert(0, '-');
	   }

  	  return s.toString();
	} 
}

