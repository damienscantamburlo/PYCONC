package net.cercis.jconc.fem;
import net.cercis.jconc.ui.*;

public class Conc3N implements Element {

    private final String type;
    private final int id;
    private double thick;
    private final Node n1, n2, n3;
    private final float b1, b2, b3, c1, c2, c3, area;
    private final double beta12, l12, beta23, l23, beta31, l31;
    private final Material mate;

    private double[] u;
    private double[] v;
    private double[] fx; //Forces for a given disp
    private double[] fy; //Forces for a given disp
    private double[][] fn; //Incremental forces for incremental disp

    private double epsx, epsy, gamma, eps1, eps2, alpha;     

    public Conc3N (int id, Node n1 ,Node n2, Node n3, 
		   double thick, Material mate) {
	type = "Conc3N";
	this.id = id;
	this.n1 = n1;
	this.n2 = n2;
	this.n3 = n3;
	this.thick = thick;
	this.mate = mate;
	u = new double[3];
	v = new double[3];
	fx = new double[3];
	fy = new double[3];
	fn = new double[2][3];
	b1 = n2.y() - n3.y();
	b2 = n3.y() - n1.y();
	b3 = n1.y() - n2.y();
	c1 = n3.x() - n2.x();
	c2 = n1.x() - n3.x();
	c3 = n2.x() - n1.x();
	area = (float) (0.5 * ((n2.x()-n1.x())*(n3.y()-n1.y()) -
				(n3.x()-n1.x())*(n2.y()-n1.y())));
	beta12 = Math.atan2((n2.y()-n1.y()),(n2.x()-n1.x()));
	l12 = Math.sqrt((n2.y()-n1.y())*(n2.y()-n1.y()) + 
			       (n2.x()-n1.x())*(n2.x()-n1.x()));
	beta23 = Math.atan2((n3.y()-n2.y()),(n3.x()-n2.x()));
	l23 = Math.sqrt((n3.y()-n2.y())*(n3.y()-n2.y()) + 
			       (n3.x()-n2.x())*(n3.x()-n2.x()));
	beta31 = Math.atan2((n1.y()-n3.y()),(n1.x()-n3.x()));
	l31 = Math.sqrt((n1.y()-n3.y())*(n1.y()-n3.y()) + 
			       (n1.x()-n3.x())*(n1.x()-n3.x()));
	
    }

    public int id () {
	return id;
    }

    public String type () {
	return type;
    }

    public int material () {
	return mate.id();
    }

    public double parameter (int k) {
	return thick;
    }

    public int getNumNodes () {
	return 3;
    }

    public int getNode (int k) {
	int node = 0;
	if (k==0) node = n1.id();
	if (k==1) node = n2.id();
	if (k==2) node = n3.id();
	return node;
    }

    public Node node (int k) {
	Node node = null;
	if (k==0) node = n1;
	if (k==1) node = n2;
	if (k==2) node = n3;
	return node;
    }

    public double getPercentS1 () {
	double tens = -mate.stress(eps1,eps2);
	return tens/(mate.getEta()*mate.fc());
    }

    public double getEta1 () {
	double tens = -mate.stress(eps1,eps2);
	return mate.getEta();	
    }

    public double getS1 () {
	return mate.stress(eps1,eps2);
    }    

    public double getEps1 () {
	return eps1;
    }    

    public double getPercentS2 () {
	double tens = -mate.stress(eps2,eps1);
	return tens/(mate.getEta()*mate.fc());
    }

    public double getEta2 () {
	double tens = -mate.stress(eps2,eps1);
	return mate.getEta();	
    }

    public double getS2 () {
	return mate.stress(eps2,eps1);
    }    

    public double getEps2 () {
	return eps2;
    }    

    public double getAlpha () {
	return alpha;
    }

    public double getForceDif (String dir, int node) {
	double force = 0;
	if (dir == "x") {
	    if (node == 0) force = fn[0][0];
	    if (node == 1) force = fn[0][1];
	    if (node == 2) force = fn[0][2];
	}
	else {
	    if (node == 0) force = fn[1][0];
	    if (node == 1) force = fn[1][1];
	    if (node == 2) force = fn[1][2];
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
	    if (node == 2) force = fx[2];
	}
	else {
	    if (node == 0) force = fy[0];
	    if (node == 1) force = fy[1];
	    if (node == 2) force = fy[2];
	}
	return force;	
    }
    
    private void computePrincipalEps () {
	epsx = (0.5/area)*(b1*u[0]+b2*u[1]+b3*u[2]);
	epsy = (0.5/area)*(c1*v[0]+c2*v[1]+c3*v[2]);
	gamma = 0.5*(0.5/area)*(c1*u[0]+c2*u[1]+c3*u[2] + b1*v[0]+b2*v[1]+b3*v[2]);
	double epsm = (epsx + epsy)*0.5;
	double radius = Math.sqrt(gamma*gamma + (epsx-epsm)*(epsx-epsm));
	eps1 = epsm + radius;
	eps2 = epsm - radius;
	if (epsx == epsm) {
	    if (gamma == 0.0) {alpha = 0.0;}
	    else {alpha = 0.5 * (gamma/Math.abs(gamma)*Math.PI/2);}
	}
	else {	    
	    alpha = 0.5 * Math.atan2(gamma,(epsx-epsm));
	}
    }

    private void computeNodalForces () {

	double aux1_1 = 0.5 * thick * mate.stress(eps1,eps2) * 
	    (l12 * Math.sin(beta12-alpha) +  l31 * Math.sin(beta31-alpha));
	double aux2_1 = 0.5 * thick * mate.stress(eps2,eps1) * 
	    (l12 * Math.sin(beta12-alpha-Math.PI*0.5) + 
	     l31 * Math.sin(beta31-alpha-Math.PI*0.5));
	fx[0] = aux1_1*Math.cos(alpha) + aux2_1*Math.cos(alpha+Math.PI*0.5);
	fy[0] = aux1_1*Math.sin(alpha) + aux2_1*Math.sin(alpha+Math.PI*0.5);
	double aux1_2 = 0.5 * thick * mate.stress(eps1,eps2) * 
	    (l12 * Math.sin(beta12-alpha) + l23 * Math.sin(beta23-alpha));
	double aux2_2 = 0.5 * thick * mate.stress(eps2,eps1) * 
	    (l12 * Math.sin(beta12-alpha-Math.PI*0.5) + 
	     l23 * Math.sin(beta23-alpha-Math.PI*0.5));
	fx[1] = aux1_2*Math.cos(alpha) + aux2_2*Math.cos(alpha+Math.PI*0.5);
	fy[1] = aux1_2*Math.sin(alpha) + aux2_2*Math.sin(alpha+Math.PI*0.5);
	double aux1_3 = 0.5 * thick * mate.stress(eps1,eps2) * 
	    (l31 * Math.sin(beta31-alpha) + l23 * Math.sin(beta23-alpha));
	double aux2_3 = 0.5 * thick * mate.stress(eps2,eps1) * 
	    (l31 * Math.sin(beta31-alpha-Math.PI*0.5) + 
	     l23 * Math.sin(beta23-alpha-Math.PI*0.5));
	fx[2] = aux1_3*Math.cos(alpha) + aux2_3*Math.cos(alpha+Math.PI*0.5);
	fy[2] = aux1_3*Math.sin(alpha) + aux2_3*Math.sin(alpha+Math.PI*0.5);
// 	System.out.println(fx[0] + "\t" + fy[0]);
// 	System.out.println(fx[1] + "\t" + fy[1]);
// 	System.out.println(fx[2] + "\t" + fy[2] + "\n");
    }

    double mSec1;
    double mSec2;

    private void computeSecModuli () {
	if (eps1 != 0) {
	    mSec1 = mate.stress(eps1,eps2)/eps1;
 	}
 	else {
 	    mSec1 = mate.modEl();
 	}
	if (eps2 != 0) {
	    mSec2 = mate.stress(eps2,eps1)/eps2;
 	}
 	else {
 	    mSec2 = mate.modEl();
 	}
	//System.out.println("ms1="+mSec1+"\t ms2="+mSec2);
    }

    private void computeSecNodalForces () {
	double aux1_1 = 0.5 * thick * mSec1 * eps1 * 
	    (l12 * Math.sin(beta12-alpha) +  l31 * Math.sin(beta31-alpha));
	double aux2_1 = 0.5 * thick * mSec2 * eps2 * 
	    (l12 * Math.sin(beta12-alpha-Math.PI*0.5) + 
	     l31 * Math.sin(beta31-alpha-Math.PI*0.5));
	fx[0] = aux1_1*Math.cos(alpha) + aux2_1*Math.cos(alpha+Math.PI*0.5);
	fy[0] = aux1_1*Math.sin(alpha) + aux2_1*Math.sin(alpha+Math.PI*0.5);
	double aux1_2 = 0.5 * thick * mSec1 * eps1 * 
	    (l12 * Math.sin(beta12-alpha) + l23 * Math.sin(beta23-alpha));
	double aux2_2 = 0.5 * thick * mSec2 * eps2 * 
	    (l12 * Math.sin(beta12-alpha-Math.PI*0.5) + 
	     l23 * Math.sin(beta23-alpha-Math.PI*0.5));
	fx[1] = aux1_2*Math.cos(alpha) + aux2_2*Math.cos(alpha+Math.PI*0.5);
	fy[1] = aux1_2*Math.sin(alpha) + aux2_2*Math.sin(alpha+Math.PI*0.5);
	double aux1_3 = 0.5 * thick * mSec1 * eps1 * 
	    (l31 * Math.sin(beta31-alpha) + l23 * Math.sin(beta23-alpha));
	double aux2_3 = 0.5 * thick * mSec2 * eps2 * 
	    (l31 * Math.sin(beta31-alpha-Math.PI*0.5) + 
	     l23 * Math.sin(beta23-alpha-Math.PI*0.5));
	fx[2] = aux1_3*Math.cos(alpha) + aux2_3*Math.cos(alpha+Math.PI*0.5);
	fy[2] = aux1_3*Math.sin(alpha) + aux2_3*Math.sin(alpha+Math.PI*0.5);
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
	if (node == n3.id()) dispNode = 2;
	if (direction == "x") u[dispNode] = u[dispNode] + disp;
	if (direction == "y") v[dispNode] = v[dispNode] + disp;
	computePrincipalEps ();
	computeSecNodalForces ();
	for (int i=0; i<3; i++) {
	    fn[0][i] = fx[i];
	    fn[1][i] = fy[i];
	}
  	if (direction == "x") u[dispNode] = u[dispNode] - disp;
  	if (direction == "y") v[dispNode] = v[dispNode] - disp;	
  	computePrincipalEps ();
  	computeSecNodalForces ();
 	for (int i=0; i<3; i++) {
 	    fn[0][i] = fn[0][i] - fx[i];
 	    fn[1][i] = fn[1][i] - fy[i];
 	}

    }

    public String toString () {
        final int digits = Jconc.getDigits();
	return " Concrete Triangle : " + id + "\n   Nodes : " + n1.id() + 
	    " " +  n2.id() +
	    " " + n3.id() + "\t Material : " + mate.id() + 
	    "\n   epsx = " + Jconc.formatNumber(epsx, digits) + 
	    " epsy = "  + Jconc.formatNumber(epsy, digits) +
            " gamma = " + Jconc.formatNumber(gamma, digits) +
            "\n   eps1 = " + Jconc.formatNumber(eps1, digits) +
            " eps2 = " + Jconc.formatNumber(eps2, digits) + 
	    " alpha = " + Jconc.formatNumber(Math.toDegrees(alpha), digits) ;
    }

}
