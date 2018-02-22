package net.cercis.jconc.fem;

import net.cercis.jconc.jama.Matrix;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ListIterator;
//modified MATAFALC until next //matafalc (coming back to old numbering display)
import java.text.NumberFormat;
import java.text.DecimalFormat;
//matafalc
import java.lang.RuntimeException;
import net.cercis.jconc.ui.Jconc;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.File;
import java.io.*;
import java.util.HashSet;
import java.util.Set;



public class Solver {
    
    private Dumper recorder;
    
    private Map mNodes = new TreeMap();
    private Map mElems = new TreeMap();
    private Map mMates = new TreeMap();
    private Map mBouns = new TreeMap();    
    private Map mLoads = new TreeMap();
    private final int numNodes;
    private double kmax;
    private double inc;
    private ArrayList cone;
    private int digits = Jconc.getDigits();

    public Solver (Model model) {
	mNodes = model.getMapOfNodes ();
	mElems = model.getMapOfElems ();
	mMates = model.getMapOfMates ();
	mBouns = model.getMapOfBouns ();
	mLoads = model.getMapOfLoads ();
	numNodes = mNodes.size();
        
        recorder = new Dumper(model);
        
    }

    public String fullNR (int loadSteps, double inc)  {
 
    String s = "";
    s += "\n**** SOLUTION **************************\n\n";

    String stmp = "";
	this.inc = inc;

	//Dimensioning of arrays
   double [][] ktan;
   double[] f0;
   double[] u0;
   double[] u0_to_recorde;
   double[] reac;
   try {
      ktan = new double [2*numNodes][2*numNodes];
      f0 = new double [2*numNodes];
      u0 = new double [2*numNodes];
      u0_to_recorde = new double [2*numNodes];
      reac = new double [2*numNodes];
   }
   catch (Throwable e) {
      stmp = "Unable to obtain sufficient memory for solving " + numNodes + " nodes\n";
      double reqMem = (int)((2 * numNodes) * (2 * numNodes + 3) / 1024 / 1024 * 100) / 100;
      stmp += "Estimated required memory = " + reqMem + " MB\n";
      System.out.println (stmp);
      s += stmp;
      return s;
   }

	//Conectivity vector
	Iterator itN = ((Collection) mNodes.values()).iterator();
	cone = new ArrayList();
	while (itN.hasNext()) {
	    cone.add(new Integer (((Node) itN.next()).id()));
	}

	//Resolution auxiliary parameters
	boolean conv = false;
	int i = 0;
	double res0 = 0;

	//Number format for screen printout
	NumberFormat nf = new DecimalFormat("0.000");
	//Main loop
	while (conv == false) {
	    i++;
	    //System.out.println("Computing step " + i);
       stmp = new Integer(i).toString();
       stmp= "STEP\t" + pad(stmp, " ", 3);
	    //Assembly
	    ktan = assembleKtan (true);

       if (ktan == null) {
          System.out.println("Error assembling K");
          s += stmp + "\nError assembling K\n";
          return s;
       }
	    //System.out.println("\t K assembled");
       //s += "\t K assembled\n";
       Matrix A;
       try {
	       A = new Matrix(ktan);
       }
       catch (Exception e) {
          System.out.println("Error with matrix A");
          s += "Error with matrix A\n";
          return s;
       }
	    f0 = assembleF ();
	    Matrix b = new Matrix(f0, 2*numNodes);
	    u0 = assembleU ();
	    Matrix c = new Matrix(u0, 2*numNodes);
	    Matrix fres = A.times(c);
	    Matrix fapl = b.minus(fres);
	    double rnorm = fapl.normInf();
	    if (i==1) {
          stmp += "\t\tOK";
       }
       else {
          //matafalc 141212 (Back to old version) stmp += "\t Error = \t" + Jconc.formatNumber(Math.abs(rnorm/res0), digits);
          stmp += "\t Error = \t" + nf.format(Math.abs(rnorm/res0));
       }
       s += stmp + "\n";
       System.out.println(stmp);
    
	    if (i==1) res0 = rnorm;
	    if ((Math.abs(rnorm/res0) < 1e-5) | (i == loadSteps)) {
		conv = true;	
		ktan = assembleKtan (false);
		Matrix AR = new Matrix(ktan);
		fres = AR.times(c);
		reac = fres.getColumnPackedCopy();
		storeR(reac);
		stmp = "\nSTEP " + i + " Sum F-R = " + Jconc.formatNumber(Math.abs(rnorm/res0), digits);
      System.out.println (stmp);
      s += stmp + "\n";
      
      recorder.add_Error((float) Math.abs(rnorm/res0));
      u0_to_recorde = assembleU ();
      recorder.add_u(u0_to_recorde);
            
	    }
	    else {
         //System solution
         //System.out.println("\t Starting system solution ...");
         //s += "\t Starting system solution ...\n";
         Matrix x = A.solve(fapl);
         if (x == null) {
            stmp = "Matrix is singular. Check boundary conditions and unconnected nodes.\n";
            System.out.println (stmp);
            s += stmp;
         }
         else {
            //System.out.println("\t System solved!");
            //s += "\t System solved!\n";
            double[] incU = new double [2*numNodes];
            incU = x.getColumnPackedCopy();
            //Solution storage
            storeU(incU);
            
            recorder.add_Error((float) Math.abs(rnorm/res0));
            u0_to_recorde = assembleU ();
            recorder.add_u(u0_to_recorde);

         }
	   }
	}
recorder.set_end_time();
recorder.add_P(this.get_P_forPython());
recorder.resume();
return s;
    }

    public double[][] assembleKtan (boolean boundaries) {

   double [][] ktan;
   double memReq = 0.01 * (int)((2*numNodes)*(2*numNodes)*8/1024/1024*100);
   try {
	   ktan = new double [2*numNodes][2*numNodes];
      //System.out.println("Allocated " + memReq + " MB for Ktan");
   }
   catch (Throwable e) {
      System.out.println("Error allocating memory for " + numNodes + " nodes (" + memReq + " MB)");
      return null;
   }
	Iterator itNodes = ((Collection) mNodes.values()).iterator();
	while (itNodes.hasNext()) {
	    Node node = (Node) itNodes.next();
	    ArrayList al = node.getElements();
	    ListIterator itElems = al.listIterator();
	    while (itElems.hasNext()) {
		Element ei = (Element) itElems.next();
		int i = cone.indexOf(new Integer(node.id()));
		ei.imposeIncrementalDisp(node.id(),"x",inc);
		int numTotNodes = ei.getNumNodes ();
		for (int k=0; k<numTotNodes; k++) {
		    int j = cone.indexOf(new Integer(ei.getNode(k)));
		    ktan[2*j][2*i] = ktan[2*j][2*i] + 
			ei.getForceDif("x",k)/inc;
		    ktan[2*j+1][2*i] = ktan[2*j+1][2*i] + 
			ei.getForceDif("y",k)/inc;
		}
		ei.imposeIncrementalDisp(node.id(),"y",inc);
		for (int k=0; k<numTotNodes; k++) {
		    int j = cone.indexOf(new Integer(ei.getNode(k)));
		    ktan[2*j][2*i+1] = ktan[2*j][2*i+1] + 
			ei.getForceDif("x",k)/inc;
		    ktan[2*j+1][2*i+1] = ktan[2*j+1][2*i+1] + 
			ei.getForceDif("y",k)/inc;
		}		
	    }
	}
        
	//System.out.println(printK(ktan));
          
	if (boundaries == true) {
	    //BOUNDARIES
	    kmax = 0.;
	    for (int i=0; i<2*numNodes; i++) {
		for (int j=0; j<2*numNodes; j++) {
		    if (ktan[i][j] > kmax) kmax = ktan[i][j];
		}
	    }
            
	    kmax = kmax * 1e5;
	    Iterator itBouns = ((Collection) mBouns.values()).iterator();
	    while (itBouns.hasNext()) {	    
		Boun boun = (Boun) itBouns.next();
		int node = cone.indexOf(new Integer (boun.node()));
		if ((boun.type().equals("all")) | (boun.type().equals("ux"))) {
		    if (boun.disp() == 0) ktan[node*2][node*2] = kmax;
		    if (boun.disp() != 0) ktan[node*2][node*2] = kmax/100;
		}
		if ((boun.type().equals("all")) | (boun.type().equals("uy"))) {
		    if (boun.disp() == 0) ktan[node*2+1][node*2+1] = kmax;
		    if (boun.disp() != 0) ktan[node*2+1][node*2+1] = kmax/100;
		}
	    }
	    //System.out.println(printK(ktan));
	}
        
        //String str = printK(ktan);
        //System.out.println(str);
	return ktan;
    }

    public double[] assembleF () {
	double[] f = new double [2*numNodes];
	Iterator itLoads = ((Collection) mLoads.values()).iterator();
	while (itLoads.hasNext()) {
	    Load load = (Load) itLoads.next();
	    int node = cone.indexOf(new Integer (load.node()));
	    f [node*2] = (double) (load.fx());
	    f [node*2+1] = (double) (load.fy());
	}
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {	    
	    Boun boun = (Boun) itBouns.next();
	    int node = cone.indexOf(new Integer (boun.node()));
	    if ((boun.type().equals("all")) | (boun.type().equals("ux"))) {
		if (boun.disp() != 0) f [node*2] = kmax/100*boun.disp();
	    }
	    if ((boun.type().equals("all")) | (boun.type().equals("uy"))) {
		if (boun.disp() != 0) f [node*2+1] = kmax/100*boun.disp();
	    }
	}
	return f;
    }
    
    private double[][] get_P_forPython () {
        double[][] P_matrix_for_python = new double [2][numNodes];
        
	Iterator itNodes = ((Collection) mNodes.values()).iterator();
	while (itNodes.hasNext()) {
	    Node node = (Node) itNodes.next();
	    int nodi = cone.indexOf(new Integer (node.id()));
            P_matrix_for_python[0][nodi] = node.getX();
            P_matrix_for_python[1][nodi] = node.getY();
                               
	}
	return P_matrix_for_python;
        
    }

    public double[] assembleU () {
	double[] u = new double [2*numNodes];
	Iterator itNodes = ((Collection) mNodes.values()).iterator();
	while (itNodes.hasNext()) {
	    Node node = (Node) itNodes.next();
	    int nodi = cone.indexOf(new Integer (node.id()));
	    u [nodi*2] = (double) (node.getU());
	    u [nodi*2+1] = (double) (node.getV());
	}
	return u;
    }
    


    public void storeU (double[] incU) {
	for (int i=0; i<numNodes; i++) {
	    Node ni = (Node) mNodes.get (cone.get(i));
	    ni.setDisp((ni.getU() + incU[2*i]),(ni.getV() + incU[2*i+1]));
	}
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    int numTotNodes = ei.getNumNodes ();
	    double u[] = new double[numTotNodes];
	    double v[] = new double[numTotNodes];
	    for (int i=0; i<numTotNodes; i++) {
		u[i] = ((Node) mNodes.get(new Integer (ei.getNode(i)))).getU();
		v[i] = ((Node) mNodes.get(new Integer (ei.getNode(i)))).getV();
	    }
	    ei.imposeDisp(u,v);
	}
    }

    public void storeR (double[] reac) {
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {
	    Boun bi = (Boun) itBouns.next();
	    int nodi = cone.indexOf(new Integer (bi.node()));
	    bi.setReacX(reac[nodi*2]);
	    bi.setReacY(reac[nodi*2+1]);
	}
    }

    public String printK (double[][] k) {
	int size = 2*numNodes;
	String ret = "K = ";
	for (int i = 0; i<size; i++) {
	    ret = ret + "\n";
	    for (int j = 0; j<size; j++) {
		ret = ret + " " + Jconc.formatNumber(k[i][j], digits);
	    }
	}
	return ret;
    }

    public static String pad (String orig, String padWith, int n) {
       String s = "";
       for (int i = 0; i < n; i++)
          s += padWith.substring(0, 1);
       s += orig;
       s = s.substring(s.length() - n, s.length());
       return s;
    }
}
