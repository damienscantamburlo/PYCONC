package net.cercis.jconc.fem;
import net.cercis.jconc.ui.*;

public class Conc4N implements Element {

    private final String type;
    private final int id;
    private final double thick;
    private final Node n1, n2, n3, n4;
    private final Material mate;

    private double[] u;
    private double[] v;
    private double[] fx; //Forces for a given disp
    private double[] fy; //Forces for a given disp
    private double[][] fn; //Incremental forces for incremental disp

    private final double lx, ly;
    private double epsx, epsy, gamma, eps1, eps2, alpha;     

    public Conc4N (int id, Node n1 ,Node n2, Node n3, Node n4,
		   double thick, Material mate) {
	type = "Conc4N";
	this.id = id;
// 	Chooses the proper orientation to get
// 	2            1
// 	o------------o
// 	|            |
// 	|            |
//      o------------o
// 	3            4
	if (Math.abs(n1.x()-n4.x()) < Math.abs(n1.x()-n2.x())) {
	    if (n1.y() > n4.y()) {
		this.n1 = n1;
		this.n2 = n2;
		this.n3 = n3;
		this.n4 = n4;
	    }
	    else {
		this.n1 = n3;
		this.n2 = n4;
		this.n3 = n1;
		this.n4 = n2;
	    }
	}
	else {
	    if (n1.y() > n2.y()) {
		this.n1 = n4;
		this.n2 = n1;
		this.n3 = n2;
		this.n4 = n3;
	    }
	    else {
		this.n1 = n2;
		this.n2 = n3;
		this.n3 = n4;
		this.n4 = n1;
	    }
	}
	lx = Math.abs((this.n1).x()-(this.n2).x());
	ly = Math.abs((this.n1).y()-(this.n4).y());
	this.thick = thick;
	this.mate = mate;
	u = new double[4];
	v = new double[4];
	fx = new double[4];
	fy = new double[4];
	fn = new double[2][4];	
    }

    public String type () {
	return type;
    }

    public double parameter (int k) {
	return thick;
    }

    public int material () {
	return mate.id();
    }

    public int id () {
	return id;
    }

    public int getNumNodes () {
	return 4;
    }

    public int getNode (int k) {
	int node = 0;
	if (k==0) node = n1.id();
	if (k==1) node = n2.id();
	if (k==2) node = n3.id();
	if (k==3) node = n4.id();
	return node;
    }

    public Node node (int k) {
	Node node = null;
	if (k==0) node = n1;
	if (k==1) node = n2;
	if (k==2) node = n3;
	if (k==3) node = n4;
	return node;
    }

    public double getPercentS1 () {
	return -mate.stress(eps1)/mate.fc();
    }

    public double getEta1 () {
	return 1.0;	
    }

    public double getS1 () {
	return mate.stress(eps1);
    }    

    public double getEps1 () {
	return eps1;
    }    

    public double getPercentS2 () {
	return -mate.stress(eps2)/mate.fc();
    }

    public double getEta2 () {
	return 1.0;	
    }

    public double getS2 () {
	return mate.stress(eps2);
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
	    if (node == 3) force = fn[0][3];
	}
	else {
	    if (node == 0) force = fn[1][0];
	    if (node == 1) force = fn[1][1];
	    if (node == 2) force = fn[1][2];
	    if (node == 3) force = fn[1][3];
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
	epsx = ((u[0]+u[3])-(u[1]+u[2]))/(2*lx);
	epsy = ((v[0]+v[1])-(v[2]+v[3]))/(2*ly);
	gamma = 0.5*(((u[0]+u[1])-(u[2]+u[3]))/(2*ly) + 
	    ((v[0]+v[3])-(v[1]+v[2]))/(2*lx));
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
	double aux1_1 = 0.5 * thick * mate.stress(eps1) *
	    (lx * Math.sin(alpha) + ly * Math.cos(alpha));
	double aux2_1 = 0.5 * thick * mate.stress(eps2) *
	    (lx * Math.sin(alpha+Math.PI*0.5) + 
	     ly * Math.cos(alpha+Math.PI*0.5));
	fx[0] = aux1_1*Math.cos(alpha) + aux2_1*Math.cos(alpha+Math.PI*0.5);
	fy[0] = aux1_1*Math.sin(alpha) + aux2_1*Math.sin(alpha+Math.PI*0.5);

	double aux1_2 = 0.5 * thick * mate.stress(eps1) *
	    (lx * Math.sin(alpha) - ly * Math.cos(alpha));
	double aux2_2 = 0.5 * thick * mate.stress(eps2) *
	    (lx * Math.sin(alpha+Math.PI*0.5) + 
	     ly * Math.cos(alpha+Math.PI*0.5));
	fx[1] = aux1_2*Math.cos(alpha) + aux2_2*Math.cos(alpha+Math.PI*0.5);
	fy[1] = aux1_2*Math.sin(alpha) + aux2_2*Math.sin(alpha+Math.PI*0.5);

	double aux1_3 = -0.5 * thick * mate.stress(eps1) *
	    (lx * Math.sin(alpha) + ly * Math.cos(alpha));
	double aux2_3 = -0.5 * thick * mate.stress(eps2) *
	    (lx * Math.sin(alpha+Math.PI*0.5) + 
	     ly * Math.cos(alpha+Math.PI*0.5));
	fx[2] = aux1_3*Math.cos(alpha) + aux2_3*Math.cos(alpha+Math.PI*0.5);
	fy[2] = aux1_3*Math.sin(alpha) + aux2_3*Math.sin(alpha+Math.PI*0.5);

	double aux1_4 = -0.5 * thick * mate.stress(eps1) *
	    (lx * Math.sin(alpha) - ly * Math.cos(alpha));
	double aux2_4 = -0.5 * thick * mate.stress(eps2) *
	    (lx * Math.sin(alpha+Math.PI*0.5) + 
	     ly * Math.cos(alpha+Math.PI*0.5));
	fx[3] = aux1_4*Math.cos(alpha) + aux2_4*Math.cos(alpha+Math.PI*0.5);
	fy[3] = aux1_4*Math.sin(alpha) + aux2_4*Math.sin(alpha+Math.PI*0.5);

    }
    
    public void imposeDisp (double[] u, double[] v) {
	this.u = u;
	this.v = v;
	computePrincipalEps ();
	computeNodalForces ();
    }

    public void imposeIncrementalDisp (int node,String direction,double disp) {
	int dispNode = -1;
	if (node == n1.id()) dispNode = 0;
	if (node == n2.id()) dispNode = 1;
	if (node == n3.id()) dispNode = 2;
	if (node == n4.id()) dispNode = 3;
	if (direction == "x") u[dispNode] = u[dispNode] + disp;
	if (direction == "y") v[dispNode] = v[dispNode] + disp;
	computePrincipalEps ();
	computeNodalForces ();
	for (int i=0; i<4; i++) {
	    fn[0][i] = fx[i];
	    fn[1][i] = fy[i];
	}
 	if (direction == "x") u[dispNode] = u[dispNode] - disp;
 	if (direction == "y") v[dispNode] = v[dispNode] - disp;	
 	computePrincipalEps ();
 	computeNodalForces ();
	for (int i=0; i<4; i++) {
	    fn[0][i] = fn[0][i] - fx[i];
	    fn[1][i] = fn[1][i] - fy[i];
	}
    }

    public String toString () {
        final int digits = Jconc.getDigits();
	return " Concrete Square : " + id + "\n   Nodes : " + n1.id() + 
	    " " +  n2.id() + " " + n3.id() + " " + n4.id() + 
	    "\t Material : " + mate.id() + 
	    "\n   epsx = " + Jconc.formatNumber(epsx, digits) + 
	    " epsy = " + Jconc.formatNumber(epsy, digits) +
            " gamma = " + Jconc.formatNumber(gamma, digits) +  
	    "\n   eps1 = " + Jconc.formatNumber(eps1, digits) +
            " eps2 = " + Jconc.formatNumber(eps2, digits) + 
	    " alpha = " + Jconc.formatNumber(Math.toDegrees(alpha), digits) ;
    }

}
