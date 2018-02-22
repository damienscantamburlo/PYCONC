package net.cercis.jconc.fem;
import net.cercis.jconc.ui.Jconc;

public class Load {

    private final int node;
    private final double fx, fy;
    private final double fxInput, fyInput; // loads input in the input file or
                                           // in Launcher programming
    private final double fxp, fyp;         // loads resulting from prestressing
    private final boolean noInput;
    private final int digits = Jconc.getDigits();

    public Load (int node, double fx, double fy) { // called when creating "normal" loads
	this.node = node;
	this.fx = fx;
	this.fy = fy;
        fxInput = fx;
        fyInput = fy;
        fxp = 0.0;
        fyp = 0.0;
        noInput = false;
        
//        System.out.println("Initial load on node" + this.node + " (" + this.fx + "," + this.fy + ") Initial load (" + this.fxInput + "," + this.fyInput + ")");
    }

     public Load (int node, double fx, double fy, double fxInput,
             double fyInput, double fxp, double fyp) { // called when adding post-tensioning loads
	this.node = node;
	this.fx = fx;
	this.fy = fy;
        this.fxInput = fxInput;
        this.fyInput = fyInput;
        this.fxp = fxp;
        this.fyp = fyp;
        if ((Math.abs(fxInput) < 1E-9) && (Math.abs(fyInput) < 1E-9))
           noInput = true;
        else
           noInput = false;
//        System.out.println("New load on node" + this.node + " (" + this.fx + "," + this.fy + ") Initial load (" + this.fxInput + "," + this.fyInput + ") (" + fxp + "," + fyp + ")");
    }


    public int node () {
	return node;
    }

    public double fx () {
	return fx;
    }

    public double fy () {
	return fy;
    }

    public double fxInput () {
       if (Math.abs(fxInput) < 1.0E-9)
          return 0.0;
       else
	   return fxInput;
    }

    public double fyInput () {
       if (Math.abs(fyInput) < 1.0E-9)
          return 0.0;
       else
	   return fyInput;
    }
    
    public boolean noInput () {
       return ((Math.abs(fyInput) < 1.0E-9) && (Math.abs(fyInput) < 1.0E-9));
    }

    public double fxp () {
	return fxp;
    }

    public double fyp () {
	return fyp;
    }

    public String toString () {
	return " ( " + Jconc.formatNumber(fx, digits) + 
	    " , " + Jconc.formatNumber(fy, digits) + " )";
    }

}
