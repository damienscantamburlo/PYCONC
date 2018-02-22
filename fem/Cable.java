package net.cercis.jconc.fem;
import net.cercis.jconc.ui.*;

public class Cable implements Element {

    private final String type;
    private final int id;
    private final Node n1, n2;
    private final double length, area, istrain, theta;
    private final Material mate;

    private double[] u;
    private double[] v;
    private double[] fx; //Forces for a given disp
    private double[] fy; //Forces for a given disp
    private double[][] fn; //Incremental forces for incremental displ

    private double eps;     

    public Cable (int id, Node n1 ,Node n2, double area, 
		  double istrain, Material mate) {
	type = "Cable";
	this.id = id;
	this.n1 = n1;
	this.n2 = n2;
	this.area = area;
	this.istrain = istrain;
	eps = 0;//istrain;
	this.mate = mate;
	u = new double[2];
	v = new double[2];
	fx = new double[2];
	fy = new double[2];
	fn = new double[2][2];
	double incx = n2.x() - n1.x();
	double incy = n2.y() - n1.y();
	length = Math.sqrt(incx*incx + incy*incy);
	theta = Math.atan2(incy,incx);
    }

    public String type () {
	return type;
    }

    public int material () {
	return mate.id();
    }
   //Introduced code ---- Rohit
    public double getStrain() {
    	return istrain;
    } 
    //end of Introduced code ---- Rohit
	
    public double parameter (int k) {
	return area;
    }

    public int id () {
	return id;
    }

    public int getNumNodes () {
	return 2;
    }

    public int getNode (int k) {
	int node = 0;
	if (k==0) node = n1.id();
	if (k==1) node = n2.id();
	return node;
    }

    public Node node (int k) {
	Node node = null;
	if (k==0) node = n1;
	if (k==1) node = n2;
	return node;
    }

    public double getPercentS1 () {
	return mate.stress(eps)/mate.fc();
    }

    public double getS1 () {
	return mate.stress(eps);
    }    

    public double getEps1 () {
	return eps;
    }    

    public double getEta1 () {
	return 1.0;	
    }

    public double getPercentS2 () {
	return 0.0;
    }

    public double getS2 () {
	return 0.0;
    }    

    public double getEps2 () {
	return 0.0;
    }    

    public double getEta2 () {
	return 1.0;	
    }

    public double getAlpha () {
	return theta;
    }

    public double getForceDif (String dir, int node) {
	double force = 0;
	if (dir == "x") {
	    if (node == 0) force = fn[0][0];
	    if (node == 1) force = fn[0][1];
	}
	else {
	    if (node == 0) force = fn[1][0];
	    if (node == 1) force = fn[1][1];
	}
	return force;	
    }

    public double getNodalForce (String dir, int node) {
	computePrincipalEps ();
	computeNodalForces ();
	double force = 0;
	if (dir == "x") {
	    if (node == 0) force = fx[0];
	    if (node == 1) force = fx[1];
	}
	else {
	    if (node == 0) force = fy[0];
	    if (node == 1) force = fy[1];
	}
	return force;	
    }
    
    private void computePrincipalEps () {
	eps = ((u[1]-u[0])*Math.cos(theta) + (v[1]-v[0])*Math.sin(theta))
	    /length; // + istrain ;
    }

    private void computeNodalForces () {
	fx[0] = -mate.stress(eps)*area*Math.cos(theta);
	fy[0] = -mate.stress(eps)*area*Math.sin(theta);
	fx[1] = mate.stress(eps)*area*Math.cos(theta);
	fy[1] = mate.stress(eps)*area*Math.sin(theta);
    }
    
    double mSec;
    private void computeSecModuli () {
	if (eps != 0) {
	    mSec = mate.stress(eps)/eps;
 	}
 	else {
 	    mSec = mate.modEl();
 	}
    }

    private void computeSecNodalForces () {
	fx[0] = -mSec*eps*area*Math.cos(theta);
	fy[0] = -mSec*eps*area*Math.sin(theta);
	fx[1] = mSec*eps*area*Math.cos(theta);
	fy[1] = mSec*eps*area*Math.sin(theta);
    }

    public void imposeDisp (double[] u, double[] v) {
	this.u = u;
	this.v = v;
	computePrincipalEps ();
	computeNodalForces ();
    }

    public void imposeIncrementalDisp (int node,String direction,double disp) {

	computeSecModuli ();

	int dispNode = -1;
	if (node == n1.id()) dispNode = 0;
	if (node == n2.id()) dispNode = 1;
	if (direction == "x") u[dispNode] = u[dispNode] + disp;
	if (direction == "y") v[dispNode] = v[dispNode] + disp;
	computePrincipalEps ();
	computeSecNodalForces ();
	for (int i=0; i<2; i++) {
	    fn[0][i] = fx[i];
	    fn[1][i] = fy[i];
	}
 	if (direction == "x") u[dispNode] = u[dispNode] - disp;
 	if (direction == "y") v[dispNode] = v[dispNode] - disp;	
 	computePrincipalEps ();
 	computeSecNodalForces ();
	for (int i=0; i<2; i++) {
	    fn[0][i] = fn[0][i] - fx[i];
	    fn[1][i] = fn[1][i] - fy[i];
	}
    }

    public String toString () {
       final int digits = Jconc.getDigits();
	return " Cable : " + id + "\n   Nodes : " + n1.id() + 
	    " " +  n2.id() + "\t Material : " + mate.id() + 
	    "\n   eps = " + Jconc.formatNumber(eps, digits) + " alpha = " + Jconc.formatNumber(theta, digits);
    }

}
