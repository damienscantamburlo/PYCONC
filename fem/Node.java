package net.cercis.jconc.fem;

import java.util.ArrayList;
import java.util.ListIterator;
import net.cercis.jconc.ui.Jconc;

public class Node {

    private final int id;
    private final float x, y;
    private double u, v;
    private ArrayList elements;
    private final int digits = Jconc.getDigits();
    
    
    public int steel_node = 0; // 0 = normal node, 1 = doubled node (suppport steel rebar)
    public int doublon_id = -1;    // if the node is the original, keep trace of the doublon (for steel support=
   
    public Node (int id, float x ,float y) {
	this.id = id;
	this.x = x;
	this.y = y;
	elements = new ArrayList();
	u = 0.0;
	v = 0.0;
    }
    
    
    public boolean is_concreteNode(){
        boolean result = true;
        if (steel_node == 1){
            result = false;
        }
        return result;
        
    }
    
    public boolean is_steelNode(){
        boolean result = false;
        if (steel_node == 1){
            result = true;
        }
        return result;
    }
    
    public String which_type(){
        String official_type = "None";
        
        if (is_concreteNode() == true){
            official_type = "Concrete node";
        }else{
            official_type = "Steel node";
        }
        
        return official_type;
        
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
    
    public void clearConcreteElement(){
        // Init counter
        steel_node = 1;
        int i = 0;
        ArrayList al = new ArrayList();
        
        //Count element to delete
        ListIterator itElems = elements.listIterator();
        while (itElems.hasNext()) {
            Element ei = (Element) itElems.next();
                if (ei.type() != "Bar"){
                   al.add(i); 
                }
            i = i + 1;
        }
        //Clear list
        for(int j = 0; j < al.size(); j++){
            elements.remove(al.get(j));
        }  
    }
    
    public void clearBarElement(){
        // Init counter
        steel_node = 0;
        int i = 0;
        ArrayList al = new ArrayList();
        
        //Count element to delete
        ListIterator itElems = elements.listIterator();
        while (itElems.hasNext()) {
            Element ei = (Element) itElems.next();
                if (ei.type() == "Bar"){
                   al.add(i); 
                }
            i = i + 1;
        }
        //Clear list
        for(int j = 0; j < al.size(); j++){
            elements.remove(al.get(j));
        }  
    }
    
    public boolean  is_connectedToBar(){
        boolean result = false;
        ListIterator itElems = elements.listIterator();
        while (itElems.hasNext()) {
            Element ei = (Element) itElems.next();
                if (ei.type() == "Bar"){
                    result = true;
                }
        }
    return result ;
    }
    
    

    public String toString () {
        if (doublon_id == -1){
            return " node = " + id + " ( " + Jconc.formatNumber(x, digits) +
                " , " + Jconc.formatNumber(y, digits) + 
	        " )\t u= ( " + Jconc.formatNumber(u, digits) +
                " , " + Jconc.formatNumber(v, digits) + " )\t type = " + which_type();
        }else{
            return " node = " + id + " ( " + Jconc.formatNumber(x, digits) +
                " , " + Jconc.formatNumber(y, digits) + 
	        " )\t u= ( " + Jconc.formatNumber(u, digits) +
                " , " + Jconc.formatNumber(v, digits) + " )\t type = " + which_type() + " )\t linked steel node index = " + doublon_id;
        }
	
    }

}
