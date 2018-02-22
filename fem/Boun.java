package net.cercis.jconc.fem;
import net.cercis.jconc.ui.*;

public class Boun {

    private final int node;
    private final String type;
    private final double disp;
    private double reacX, reacY;

    public Boun (int node, String type ,double disp) {
	this.node = node;
	this.type = type;
	this.disp = disp;
    }

    public int node () {
	return node;
    }

    public String type () {
	return type;
    }

    public double disp () {
	return disp;
    }

    public void setReacX (double reacX) {
	this.reacX = reacX;
    }

    public void setReacY (double reacY) {
	this.reacY = reacY;
    }
	
	//Code ROHIT____
	public double getReacX () {
	return reacX;
    }

	public double getReacY () {
	return reacY;
    }
	//End of Code introduced by ROHIT____


    public String toString () {
	final int digits = Jconc.getDigits();
	return " Boundary on Node " + node + " ( " + type + 
	    " , " + Jconc.formatNumber(disp, digits) + " )" + " Reac = ( " +
	    Jconc.formatNumber(reacX, digits) + " , " + Jconc.formatNumber(reacY, digits) + " )";
    }

}
