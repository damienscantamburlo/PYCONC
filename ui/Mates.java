package net.cercis.jconc.ui;

import net.cercis.jconc.fem.Model;
import net.cercis.jconc.fem.Material;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map;
import java.util.Collection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

import javax.swing.JComponent;

import java.lang.Math;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
public class Mates extends JComponent{

	private int mIndex[];
	private Model model;
	private Color mColor[];
	private int count;
	private int mCount;
	private int track;
	private Graphics2D g2d;
	private boolean erase;
	

	public Mates (Model model) {

		count = 0;
		track = 0;
		erase = false;
		mCount = 0;
		this.model = model;
		setBackground(Color.white);
		countMaterials(model.getMapOfMates());
		setBorder(BorderFactory.createLineBorder(Color.black));
		mapColorMaterials(model.getMapOfMates());
		repaint();
	}

	public void updateModel(Model model) {
	
		count = 0;
		track = 0;
		mCount = 0;
		this.model = model;
		countMaterials(model.getMapOfMates());
		mapColorMaterials(model.getMapOfMates());
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
		int step = (getHeight()-90)/mCount;
		g2d.setColor(getBackground());
		g2d.fillRect(getWidth()-100, 0, 100, getHeight());
		g2d.setColor(Color.black);
		if(!erase)
			g2d.drawLine(getWidth()-1,0,getWidth()-1,getHeight()-1);
		g2d.drawLine(getWidth()-100,getHeight()-1,99,getHeight()-1);

		int posH = 60;
		for(int i=0; i<mCount; i++){
			String str = "";
			str+="";
			str+=mIndex[i];
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Color.black);
			g2d.drawString(str, getWidth()-60, posH+15);			
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(mColor[i]);
			g2d.fillRect(getWidth()-80,posH,20,step);
			posH+=step;
		}
	}

	public Color getMaterial(int material) {
	
		if(checkMaterial(material)!=-1)
			return mColor[checkMaterial(material)];		
		else {
			
//			System.out.println("Material not found");
			return Color.black;
		}
	}

	public void countMaterials(Map mMates) {
	
		
		Iterator itMates = ((Collection) mMates.values()).iterator();
		while (itMates.hasNext()) {
			itMates.next();
			count++;		
		}
	}
	public void mapColorMaterials(Map mMates) {

		mIndex = new int[count];
		mColor = new Color[count];
		
		Iterator itMates = ((Collection) mMates.values()).iterator();
		while (itMates.hasNext()) {

			Material mt = (Material) itMates.next();
			if (checkMaterial(mt.id())==-1) {
				mIndex[mCount] = mt.id();
				mColor[mCount] = generateColor();
				mCount++;
			}
		}
	} 	
	
	public int checkMaterial(int id) {
	
		for(int i=0; i<count; i++) {
			
			if(mIndex[i]==id)
				return i;
		}

		return -1;

	}
	public Color generateColor() {

		float red = 0;
		float green = 0;
		float blue = 1;
		float steps;
	        if(count!=1)
			steps=(float)1/(count-1);
		else 
			steps=0;
		blue -= steps*track;
		red += steps*track;
		track++;
		return new Color(red, green, blue);
	}
}
