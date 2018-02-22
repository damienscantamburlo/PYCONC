package net.cercis.jconc.fem;
import net.cercis.jconc.ui.*;

public class Conc4N4T implements Element {

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
    private double[] epsxN, epsyN, gammaN, eps1N, eps2N, alphaN;   

    public Conc4N4T (int id, Node n1 ,Node n2, Node n3, Node n4,
		   double thick, Material mate) {
	type = "Conc4N4T";
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
	epsxN = new double[2];
	epsyN = new double[2];
	gammaN = new double[2];
	eps1N = new double[2];
	eps2N = new double[2];
	alphaN = new double[2];
    }

    public String type () {
	return type;
    }

    public int id () {
	return id;
    }

    public int material () {
	return mate.id();
    }

    public double parameter (int k) {
	return thick;
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

    public double getS1 () {
	return mate.stress(eps1);
    }    

    public double getEta1 () {
	return 1.0;	
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

	//Triangle 1
	epsxN[0] = (u[3]-u[2])/lx;
	epsyN[0] = (v[0]-v[3])/ly;
	gammaN[0] = 0.5*(((u[0]+u[1])-(u[2]+u[3]))/(2*ly) + ((v[0]+v[3])-(v[1]+v[2]))/(2*lx));
	//(u[0]-u[3])/ly + (v[3]-v[2])/lx; //CST
	//((u[0]+u[1])-(u[2]+u[3]))/(2*ly) + ((v[0]+v[3])-(v[1]+v[2]))/(2*lx);
	//Triangle 2
	epsxN[1] = (u[0]-u[1])/lx;
	epsyN[1] = (v[1]-v[2])/ly;
	gammaN[1] = gammaN[1];
	//(u[1]-u[2])/ly + (v[0]-v[1])/lx; //CST
	//gammaN[1];
	
// 	System.out.println("u" + u[0] + " " + u[1] + " " + u[2] + " " + u[3]);
// 	System.out.println("v" + v[0] + " " + v[1] + " " + v[2] + " " + v[3]);

	double epsmN[] = new double[2];
	double radiusN[] = new double[2];
	for (int i=0; i<2; i++) {
	    epsmN[i] = (epsxN[i]+epsyN[i])/2.0;
	    radiusN[i] = Math.sqrt(gammaN[i]*gammaN[i] + 
				  (epsxN[i]-epsmN[i])*(epsxN[i]-epsmN[i]));
	    eps1N[i] = epsmN[i] + radiusN[i];
	    eps2N[i] = epsmN[i] - radiusN[i];
	    if (epsxN[i] == epsmN[i]) {
		if (gammaN[i] == 0.0) {alphaN[i] = 0.0;}
		else {alphaN[i]=0.5*(gammaN[i]/Math.abs(gammaN[i])*Math.PI/2);}
	    }
	    else {	    
		alphaN[i] = 0.5 * Math.atan2(gammaN[i],(epsxN[i]-epsmN[i]));
	    }
	}

	epsx = (epsxN[0]+epsxN[1])/2;
	epsy = (epsyN[0]+epsyN[1])/2;
	gamma = gammaN[0];
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

	double beta12[] = {Math.atan2(-ly,-lx) , Math.atan2(ly,lx)};
	double beta23[] = {0 , Math.PI};
	double beta31[] = {Math.PI*0.5, -Math.PI*0.5};
	double l12[] = {Math.sqrt(lx*lx + ly*ly) , Math.sqrt(lx*lx + ly*ly)};
	double l23[] = {lx , lx};
	double l31[] = {ly , ly};

// 	System.out.println("eps1[0] = " + eps1N[0] + "\t" + "eps2[0] = " + eps2N[0] );
// 	System.out.println("eps1[1] = " + eps1N[1] + "\t" + "eps2[1] = " + eps2N[1] );

	//Triangle 1
	double aux1_1 = 0.5 * thick * mate.stress(eps1N[0]) *
	    (l12[0] * Math.sin(beta12[0]-alphaN[0]) +  
	     l31[0] * Math.sin(beta31[0]-alphaN[0]));
	double aux2_1 = 0.5 * thick * mate.stress(eps2N[0]) * 
	    (l12[0] * Math.sin(beta12[0]-alphaN[0]-Math.PI*0.5) + 
	     l31[0] * Math.sin(beta31[0]-alphaN[0]-Math.PI*0.5));
	fx[0] = aux1_1*Math.cos(alphaN[0]) + 
	    aux2_1*Math.cos(alphaN[0]+Math.PI*0.5);
	fy[0] = aux1_1*Math.sin(alphaN[0]) + 
	    aux2_1*Math.sin(alphaN[0]+Math.PI*0.5);
	double aux1_2 = 0.5 * thick * mate.stress(eps1N[0]) *
	    (l12[0] * Math.sin(beta12[0]-alphaN[0]) + 
	     l23[0] * Math.sin(beta23[0]-alphaN[0]));
	double aux2_2 = 0.5 * thick * mate.stress(eps2N[0]) *
	    (l12[0] * Math.sin(beta12[0]-alphaN[0]-Math.PI*0.5) + 
	     l23[0] * Math.sin(beta23[0]-alphaN[0]-Math.PI*0.5));
	fx[2] = aux1_2*Math.cos(alphaN[0]) + 
	    aux2_2*Math.cos(alphaN[0]+Math.PI*0.5);
	fy[2] = aux1_2*Math.sin(alphaN[0]) + 
	    aux2_2*Math.sin(alphaN[0]+Math.PI*0.5);
	double aux1_3 = 0.5 * thick * mate.stress(eps1N[0]) *
	    (l31[0] * Math.sin(beta31[0]-alphaN[0]) + 
	     l23[0] * Math.sin(beta23[0]-alphaN[0]));
	double aux2_3 = 0.5 * thick * mate.stress(eps2N[0]) *
	    (l31[0] * Math.sin(beta31[0]-alphaN[0]-Math.PI*0.5) + 
	     l23[0] * Math.sin(beta23[0]-alphaN[0]-Math.PI*0.5));
	fx[3] = aux1_3*Math.cos(alphaN[0]) + 
	    aux2_3*Math.cos(alphaN[0]+Math.PI*0.5);
	fy[3] = aux1_3*Math.sin(alphaN[0]) + 
	    aux2_3*Math.sin(alphaN[0]+Math.PI*0.5);


	//Triangle 2
	aux1_1 = 0.5 * thick * mate.stress(eps1N[1]) *
	    (l12[1] * Math.sin(beta12[1]-alphaN[1]) +  
	     l31[1] * Math.sin(beta31[1]-alphaN[1]));
	aux2_1 = 0.5 * thick * mate.stress(eps2N[1]) * 
	    (l12[1] * Math.sin(beta12[1]-alphaN[1]-Math.PI*0.5) + 
	     l31[1] * Math.sin(beta31[1]-alphaN[1]-Math.PI*0.5));
	fx[2] = fx[2] + aux1_1*Math.cos(alphaN[1]) + 
	    aux2_1*Math.cos(alphaN[1]+Math.PI*0.5);
	fy[2] = fy[2] + aux1_1*Math.sin(alphaN[1]) + 
	    aux2_1*Math.sin(alphaN[1]+Math.PI*0.5);
	aux1_2 = 0.5 * thick * mate.stress(eps1N[1]) *
	    (l12[1] * Math.sin(beta12[1]-alphaN[1]) + 
	     l23[1] * Math.sin(beta23[1]-alphaN[1]));
	aux2_2 = 0.5 * thick * mate.stress(eps2N[1]) *
	    (l12[1] * Math.sin(beta12[1]-alphaN[1]-Math.PI*0.5) + 
	     l23[1] * Math.sin(beta23[1]-alphaN[1]-Math.PI*0.5));
	fx[0] = fx[0] + aux1_2*Math.cos(alphaN[1]) + 
	    aux2_2*Math.cos(alphaN[1]+Math.PI*0.5);
	fy[0] = fy[0] + aux1_2*Math.sin(alphaN[1]) + 
	    aux2_2*Math.sin(alphaN[1]+Math.PI*0.5);
	aux1_3 = 0.5 * thick * mate.stress(eps1N[1]) *
	    (l31[1] * Math.sin(beta31[1]-alphaN[1]) + 
	     l23[1] * Math.sin(beta23[1]-alphaN[1]));
	aux2_3 = 0.5 * thick * mate.stress(eps2N[1]) *
	    (l31[1] * Math.sin(beta31[1]-alphaN[1]-Math.PI*0.5) + 
	     l23[1] * Math.sin(beta23[1]-alphaN[1]-Math.PI*0.5));
	fx[1] = aux1_3*Math.cos(alphaN[1]) + 
	    aux2_3*Math.cos(alphaN[1]+Math.PI*0.5);
	fy[1] = aux1_3*Math.sin(alphaN[1]) + 
	    aux2_3*Math.sin(alphaN[1]+Math.PI*0.5);

// 	System.out.println("FUERZAS");
// 	for (int i=0; i<4; i++) {
// 	    System.out.println(fx[i] + "\t" + fy[i]);
// 	}

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
	return " Concrete Square 4P: " + id + "\n   Nodes : " + n1.id() + 
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
