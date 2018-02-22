package net.cercis.jconc.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.GradientPaint;

import javax.swing.JComponent;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
public class Gradient extends JComponent{

	private Graphics2D g2d;
	String str;
	boolean smooth;
	boolean type;
	private boolean erase;

	public Gradient(String str, boolean type) {
	
		this.str = str;
		erase = false;
		this.type = type;
		setBackground(Color.white);
		setBorder(BorderFactory.createLineBorder(Color.black));
		smooth = true;
		repaint();
	}

	public void setType(boolean type){
	
		this.type = type;
	}

	public void setSmooth(boolean smooth){
	
		this.smooth = smooth;
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
	   final int digits = Jconc.getDigits();
		g2d = (Graphics2D) g;
		int step = (getHeight()-90)/6;
		int count = 6;
		g2d.setColor(getBackground());
		g2d.fillRect(getWidth()-100, 0, 100, getHeight()); //To get a white background for the bar
		g2d.setColor(Color.black);
		if(!erase)
			g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
		g2d.drawLine(getWidth()-100,getHeight()-1,99,getHeight()-1);
		int posH = 60;
		if(!smooth){
			for(int i=0; i<count; i++){
				String str = "";
				float val = (i*0.2f);
				if(type)
					str += (float)val;
				else 
					str += (Jconc.formatNumber(1-val, digits));
			//	g2d.setColor(Color.black);
			//	g2d.drawString(str, getWidth()-80,posH+15);
				g2d.setColor(new Color(i*0.2f,i*0.2f,i*0.2f));
				g2d.fillRect(getWidth()-80,posH,20,step);
				g2d.setColor(Color.black);
				g2d.drawRect(getWidth()-80,posH,20,step);
				posH+=step;
			}
			count = 7;
			posH = 60;
			for(int i=0; i<count; i++){
				String str = "";
				float val = (i*(1/6f));
				if(type)
					str += Jconc.formatNumber(val, digits);
				else
					str += Jconc.formatNumber(1-val, digits);
				g2d.setColor(Color.black);
				g2d.drawString(str, getWidth()-60,posH);
				posH+=step;
			}

		}
		else {
			
			GradientPaint gd = new GradientPaint(getWidth()-100, 60, Color.black, getWidth()-80,getHeight()-90,Color.white);
			g2d.setPaint(gd);
			g2d.fillRect(getWidth()-80,60,20,getHeight()-90);
			g2d.setColor(Color.black);
			g2d.drawRect(getWidth()-80,60,20,getHeight()-90);
			count = 7;
			for(int i=0; i<count; i++){
				String str = "";
				float val = (i*(1/6f));
				if(type)
					str += Jconc.formatNumber(val, digits);
				else
					str += Jconc.formatNumber(1-val, digits);
				g2d.setColor(Color.black);
				g2d.drawString(str, getWidth()-60,posH);
				posH+=step;
			}

		}
	

	}

}
