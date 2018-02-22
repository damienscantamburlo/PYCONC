package net.cercis.jconc.fem;

import java.util.ArrayList;
import net.cercis.jconc.ui.Jconc;

public class Node {

    private final int id;
    private final float x, y;
    private double u, v;
    private ArrayList elements;
    private final int digits = Jconc.getDigits();

    public Node (int id, float x ,float y) {
	this.id = id;
	this.x = x;
	this.y = y;
	elements = new ArrayList();
	u = 0.0;
	v = 0.0;
    }
    
    public double getX(){
        return x;
        
    }
    
    public double getY(){
        return y;
        
    }

    public double getU() {
	return u;
    }

    public double getV() {
	return v;
    }

    public void setDisp (double u, double v) {
	this.u = u;
	this.v = v;
    }

    public int id () {
	return id;
    }

    public float x () {
	return x;
    }

    public float y () {
	return y;
    }

    public void addElement (Element elem) {
	elements.add(elem);
    }

    public ArrayList getElements () {
	return elements;
    }

    public String toString () {
	return " node = " + id + " ( " + Jconc.formatNumber(x, digits) +
                " , " + Jconc.formatNumber(y, digits) + 
	        " )\t u= ( " + Jconc.formatNumber(u, digits) +
                " , " + Jconc.formatNumber(v, digits) + " )";
    }

}
