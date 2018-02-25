package net.cercis.jconc.fem;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.ListIterator;
import net.cercis.jconc.ui.Jconc;
import java.util.Set;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Model {
    
    private Map mNodes = new TreeMap();
    private Map mElems = new TreeMap();
    private Map mMates = new TreeMap();
    private Map mBouns = new TreeMap();    
    private Map mLoads = new TreeMap();
    private String version; //introduced variable --- rohit
    private int steps; //Introduced variable ---- rohit
    private double scaleForces = 1;
    public int bondslip = 0;

    public Model () {

	    version = "1,0.01"; //Starting version set for all --- Introduced by Rohit
    }

    public Map getMapOfNodes () {
	return mNodes;
    }

    public int getNumberOfNodes () {
	return mNodes.size();
    }

    public Map getMapOfElems () {
	return mElems;
    }

   public int getNumberOfElems () {
	return mElems.size();
    }

    public Map getMapOfMates () {
	return mMates;
    }

    public int getNumberOfMates () {
	return mMates.size();
    }

    public Map getMapOfBouns () {
	return mBouns;
    }

    public int getNumberOfBouns () {
	return mBouns.size();
    }

    public Map getMapOfLoads () {
	return mLoads;
    }

    public int getNumberOfLoads () {
	return mLoads.size();
    }

    public String getJobInfo() {
       String s;
       s = mNodes.size() + "\tnodes\n";
       s += mElems.size() + "\telements\n";
       s += mMates.size() + "\tmaterials\n";
       s += mLoads.size() + "\tloads\n";
       s += mBouns.size() + "\tboundary conditions\n";
       s += "----------------------------------------\n";
       return s;
    }

    public String getMemInfo() {
       int numNodes = mNodes.size();
       double reqMem = (int)((2 * numNodes) * (2 * numNodes + 3) * 8 / 1024 / 1024 * 100) / 100;
       String s = "Estimated required memory = " + reqMem + " MB\n";
       return s;
    }

    //INtroduced code --- Rohit
    public String getVer() {
    
    	return version;
    }

    public void setVer  (String ver) {
    
    	this.version = ver;
    }
    public void setSteps(int steps){
    
    	this.steps = steps;
    }

    public int getSteps(){
    
    	return steps;
    }
    ///End of introduced code

    public void setScaleForces (double sf) {
       scaleForces = sf;
    }

    public double getScaleForces () {
       return scaleForces;
    }
                
    public void addNode (int id, float x, float y) {
	mNodes.put(new Integer (id), new Node(id,x,y));
    }

    public void addNode (double id, double x, double y) {
	int idc = (int) id;
	float xc = (float) x;
	float yc = (float) y;
	mNodes.put(new Integer (idc), new Node(idc,xc,yc));
    }

    public void addNode (int id, double x, double y) {
	mNodes.put(new Integer (id), new Node(id,(float)(x),(float)(y)));
    }

    public Node getNode (int id) {
	return (Node) mNodes.get(new Integer (id));
    }
public boolean checkNode (int id) {
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element elem = (Element) itElems.next();
		int num_nodes = elem.getNumNodes();
		for (int i=0; i <= (elem.getNumNodes()-1); i++) {
			if (elem.getNode(i) == id) return true;
		}
	}
	return false;
    }

	public String checkNodes () {
   String s = "";
	Iterator itNodes = ((Collection) mNodes.values()).iterator();
	while (itNodes.hasNext()) {
	    Node ni = (Node) itNodes.next();
		if (!checkNode(ni.id())) {
         s += "Node " + ni.id() + " is unconnected\n";
      }
	}
   return s;
}
        
        
   public void duplicate_nodes(){
       
       //Init container
       List<Node> list_specialElements = new ArrayList<Node>();
       int numNodes = mNodes.size();
       int j = 0;
       Set keys = mNodes.keySet();
       
       //Get the maximum key (original point are not necessarly sorted!)
       int max_key = -10;
       for (Iterator i = keys.iterator(); i.hasNext();) {
          Integer key = (Integer) i.next();
          Node node = (Node) mNodes.get(key);
          //Check key
          if (key > max_key){
              max_key = key;
          }
       }

       //Iterate through nodes and collect the ones that need to be doubled
       for (Iterator i = keys.iterator(); i.hasNext();) {
          Integer key = (Integer) i.next();
          Node node = (Node) mNodes.get(key);
          boolean result = node.is_connectedToBar();
            if (result == true){
                ((Node) mNodes.get(key)).doublon_id = max_key + j + 1;
                list_specialElements.add(node);
                j = j  + 1; 
            }
       }

       // Create the new needed node
       for(int i = 0; i < list_specialElements.size(); i++){        
           mNodes.put(new Integer (max_key + i  + 1),new Node(max_key + i + 1,(float) list_specialElements.get(i).getX(),(float)list_specialElements.get(i).getY()));
           ((Node) (mNodes.get(new Integer (max_key + i + 1)))).clearConcreteElement();
       }
       
       // Delete useless information on concrete node
       for (Iterator i = keys.iterator(); i.hasNext();) {
          Integer key = (Integer) i.next();
          Node node = (Node) mNodes.get(key);
          if (node.is_concreteNode() == true){
               ((Node) mNodes.get(key)).clearBarElement();
           } 
       }

       // See the result
       Iterator itNodes_3 = ((Collection) mNodes.values()).iterator();
       while (itNodes_3.hasNext()) {
           Node node = (Node) itNodes_3.next();
           System.out.println(node);
           System.out.println("---------------");
       }
       
      
   }
   
       

    
   public void addMate (int id, String ident, double fc, double modEl) {
 Material mi = null;
 if (ident.equalsIgnoreCase("concrete")) {
     mi = new MaterialConc (id, fc, modEl);
 }
 else if (ident.equalsIgnoreCase("steel")) {
     mi = new MaterialSteel (id, fc, modEl);
 }
 else if (ident.equalsIgnoreCase("strucSteel")) {
     mi = new MaterialStrucSteel (id, fc, modEl);
 }
 else if (ident.equalsIgnoreCase("steelTC")) {
     mi = new MaterialStrucSteel (id, fc, modEl);
 }
 else if (ident.equalsIgnoreCase("compOnly")) {
     mi = new MaterialCompOnly (id, fc, modEl);
 }
 else if (ident.equalsIgnoreCase("tensOnly")){
     mi = new MaterialTensOnly (id, fc, modEl);
 }
 else
    System.out.println("Cannot add material " + id + " '" + ident + "'");
 mMates.put(new Integer (id), mi);
    }

    public void addMate (int id, String ident, double param1,
    double param2, double param3) {
 Material mi = null;
 if (ident.equalsIgnoreCase("steel")) {
    mi = new MaterialSteel (id, param1, param2, param3);
//  param1 = strength f, param2 = Emod, param3 = Ehard (hardening modulus)
 }
else if (ident.equals("steelTC")) {
    mi = new MaterialSteelTC (id, param1, param2, param3);
}
 else if (ident.equalsIgnoreCase("cable")) {
    mi = new MaterialCable (id, param1, param2, param3);
//  param1 = strength f, param2 = sigma 0, param3 = Emod
 }
 else if (ident.equalsIgnoreCase("concWithfct")) {
    mi = new MaterialConcWithfct (id, param1, param2, param3);
//  param1 = strength f, param2 = fct, param3 = Emod
 }
 else if (ident.equalsIgnoreCase("strucSteel")) {
    mi = new MaterialStrucSteel (id, param1, param2, param3);
//  param1 = strength f, param2 = Emod, param3 = Ehard
 }
 else if (ident.equalsIgnoreCase("steelTC")) {
     mi = new MaterialStrucSteel (id, param1, param2, param3);
//  param1 = strength f, param2 = Emod, param3 = Ehard
 }
 else if (ident.equalsIgnoreCase("compOnly")) {
    mi = new MaterialCompOnly (id, param1, param2, param3);
//  param1 = strength f, param2 = Emod, param3 = Ehard
        }
else if (ident.equalsIgnoreCase("tensOnly")) {
    mi = new MaterialTensOnly (id, param1, param2, param3);
//  param1 = strength f, param2 = Emod, param3 = Ehard
        }
//matafalc MODIFIED 140816 UNTIL NEXT //matafalc
else if (ident.equalsIgnoreCase("steelExpSoft")) {
    mi = new MaterialSteelExpSoft (id, param1, param2, param3);
    //  param1 = strength f, param2 = Emod, param3 = K - coefficient for exponential law sigma=f*exp(-(eps-f/Emod)/K)
        }
else if (ident.equalsIgnoreCase("steelLinSoft")) {
    mi = new MaterialSteelLinSoft (id, param1, param2, param3);
    //  param1 = strength f, param2 = Emod, param3 = Ehard
        }
//matafalc MODIFIED 140816
else
    System.out.println("Cannot add material " + id + " '" + ident + "'");

 mMates.put(new Integer (id), mi);
    }

    public void addMate (int id, String ident, double param1, 
			 double param2, double param3, double param4) {
	Material mi = null;
	if (ident.equalsIgnoreCase("concrete")) {
	    mi = new MaterialConc (id, param1, param2, param3, param4);
//  param1 = strength f, param2 = Emod, param3 = coeff eta1, param4 = coeff eta2
	}
    else if (ident.equalsIgnoreCase("cable")) {
        mi = new MaterialCable (id, param1, param2, param3, param4);
    }
//matafalc MODIFIED 140816 UNTIL NEXT //matafalc
    else if (ident.equalsIgnoreCase("steelLinSoft")) {
        mi = new MaterialSteelLinSoft (id, param1, param2, param3, param4);
	    //  param1 = strength f, param2 = Emod, param3 = Ehard, param4 = epsmax
    }
//matafalc MODIFIED 140816
//matafalc MODIFIED 141123 UNITL NEXT //matafalc
    else if (ident.equalsIgnoreCase("concWithfct")) {
        mi = new MaterialConcWithfct (id, param1, param2, param3, param4);
	    //  param1 = strength f, param2 = fct, param3 = Emod, param4 = epsut
    }
//matafalc MODIFIED 141123
	else 
        System.out.println("Impossible to add material " + id + " " + ident + " with 4 parameters");
	mMates.put(new Integer (id), mi);
    }

//matafalc MODIFIED 140828 UNTIL NEXT //matafalc
    public void addMate (int id, String ident, double param1, double param2, 
			 double param3, double param4, double param5) {
Material mi = null;
if (ident.equalsIgnoreCase("steelLinSoft")) {
    mi = new MaterialSteelLinSoft (id, param1, param2, param3, param4, param5);
    //  param1 = strength f, param2 = Emod, param3 = Ehard, param4 = epsmax, param5 = sigma_res
}
else
    System.out.println("Impossible to add material " + id + " " + ident + " with 5 parameters");
mMates.put(new Integer (id), mi);
}
//matafalc MODIFIED 140828
	
    public void addBoun (int node, String type, double disp) {
	int size = mBouns.size();
	mBouns.put(new Integer (size+1), new Boun(node,type,disp));
    }

    public void addBoun (double node, String type, double disp) {
	int nodec = (int) node;
	addBoun(nodec,type,disp);
    }

      public void addLoad (int node, double fx, double fy) {
	if (mLoads.get(new Integer (node)) == null) {
	    mLoads.put(new Integer (node), new Load(node,fx,fy));
	}
	else {
	    Load li = (Load) (mLoads.get(new Integer (node)));
	    Load li_new = new Load (node,fx+li.fx(),fy+li.fy());
	    mLoads.put(new Integer (node), li_new);
	}
    }
    
    public void addLoad (double nodec, double fx, double fy) {
	int node = (int) nodec;
	addLoad(node,fx,fy);
    }
    
    public void addLoadPres (int node, double fx, double fy) {
	if (mLoads.get(new Integer (node)) == null) {
	    mLoads.put(new Integer (node), new Load(node,fx,fy,0.0,0.0,fx,fy));
	}
	else {
	    Load li = (Load) (mLoads.get(new Integer (node)));
	    Load li_new = new Load (node,fx+li.fx(),fy+li.fy(),li.fxInput(), li.fyInput(),fx+li.fx(),fy+li.fy());
	    mLoads.put(new Integer (node), li_new);
	}
    }

    
    public void addElem (int id, String type, int n1, int n2, int n3, 
			 double thick, int mate) {
	Element ei = null;
	if (type.equalsIgnoreCase("conc3N")) {
	    ei = new Conc3N(id, (Node) mNodes.get(new Integer (n1)), 
			    (Node) mNodes.get(new Integer (n2)),
			    (Node) mNodes.get(new Integer (n3)),
			    thick, (Material) mMates.get(new Integer (mate)));
	}
	mElems.put(new Integer (id), ei);
	((Node) (mNodes.get(new Integer (n1)))).
	    addElement((Element) (mElems.get(new Integer (id))));
	((Node) (mNodes.get(new Integer (n2)))).
	    addElement((Element) (mElems.get(new Integer (id))));
	((Node) (mNodes.get(new Integer (n3)))).
	    addElement((Element) (mElems.get(new Integer (id))));
    }

    public void addElem (double idc, String type, double n1c, double n2c,
			 double n3c, double thick, double matec) {
	int id = (int) idc;
	int n1 = (int) n1c;
	int n2 = (int) n2c;
	int n3 = (int) n3c;
	int mate = (int) matec;
	addElem (id,type,n1,n2,n3,thick,mate);
    }


    public void addElem (int id, String type, int n1, int n2, int n3, int n4,
			 double thick, int mate) {
	Element ei = null;
	if (type.equalsIgnoreCase("conc4N")) {
	    ei = new Conc4N(id, (Node) mNodes.get(new Integer (n1)), 
			    (Node) mNodes.get(new Integer (n2)),
			    (Node) mNodes.get(new Integer (n3)),
			    (Node) mNodes.get(new Integer (n4)),
			    thick, (Material) mMates.get(new Integer (mate)));
	}
	if (type.equalsIgnoreCase("conc4N4T")) {
	    ei = new Conc4N4T(id, (Node) mNodes.get(new Integer (n1)), 
			    (Node) mNodes.get(new Integer (n2)),
			    (Node) mNodes.get(new Integer (n3)),
			    (Node) mNodes.get(new Integer (n4)),
			    thick, (Material) mMates.get(new Integer (mate)));
	}
	mElems.put(new Integer (id), ei);
	((Node) (mNodes.get(new Integer (n1)))).
	    addElement((Element) (mElems.get(new Integer (id))));
	((Node) (mNodes.get(new Integer (n2)))).
	    addElement((Element) (mElems.get(new Integer (id))));
	((Node) (mNodes.get(new Integer (n3)))).
	    addElement((Element) (mElems.get(new Integer (id))));
	((Node) (mNodes.get(new Integer (n4)))).
	    addElement((Element) (mElems.get(new Integer (id))));
    }

    public void addElem (int id, String type, int n1, int n2, 
			 double area, int mate) {
	Element ei = null;
	if (type.equalsIgnoreCase("bar")) {
           Material locMat = (Material) mMates.get(new Integer (mate));
           if (locMat == null) {
              System.out.println("Error for element " + id + ", material " + mate + " not found");
           }
	    ei = new Bar (id, (Node) mNodes.get(new Integer (n1)), 
			  (Node) mNodes.get(new Integer (n2)),
			  area, locMat);
	}
	else if (type.equalsIgnoreCase("cable")) {
	    Material mi = (Material) mMates.get(new Integer (mate));
            if (mi == null) 
               System.out.println("ERROR: cable material " + new Integer (mate) + " is null");
	    //Compute of istrain

		double e1 = 0;
                double e2 = mi.fc()/mi.modEl()/10000;
		double s1 = mi.stress(e1);
                double s2 = mi.stress(e2);
		double e3 = mi.fc()/mi.modEl()*2;
                double e4 = mi.fc()/mi.modEl()*3;
		double s3 = mi.stress(e3);
                double s4 = mi.stress(e4);
		double epsyt = ((s3-s1)+e1*(s2-s1)/(e2-e1)-e3*(s4-s3)/(e4-e3))/((s2-s1)/(e2-e1)-(s4-s3)/(e4-e3));
		double istrain = mi.fc()/mi.modEl()-epsyt;
		
	    //double ft = mi.stress(2*mi.fc()/mi.modEl());
	    //double istrain = (mi.fc()-ft)/mi.modEl();
	    Node node1 = (Node) mNodes.get(new Integer (n1));
	    Node node2 = (Node) mNodes.get(new Integer (n2));
	    ei = new Cable (id,node1,node2,area,istrain,mi);
	    double fP = istrain*mi.modEl()*area;
	    double incx = node2.x() - node1.x();
	    double incy = node2.y() - node1.y();
	    double theta = Math.atan2(incy,incx);
	    this.addLoadPres(n1,fP*Math.cos(theta),fP*Math.sin(theta));
	    this.addLoadPres(n2,-fP*Math.cos(theta),-fP*Math.sin(theta));
	}
	mElems.put(new Integer (id), ei);
	((Node) (mNodes.get(new Integer (n1)))).
	    addElement((Element) (mElems.get(new Integer (id))));
	((Node) (mNodes.get(new Integer (n2)))).
	    addElement((Element) (mElems.get(new Integer (id))));
    }

    public void addElem (double idc, String type, double n1c, double n2c, 
			 double area, double matec) {
	int id = (int) idc;
	int n1 = (int) n1c;
	int n2 = (int) n2c;
	int mate = (int) matec;
	addElem(id,type,n1,n2,area,mate);
    }


    public double getCoorNode(String dir, int ni) {
	Node node = (Node) mNodes.get(new Integer (ni));    
	if (dir.equalsIgnoreCase("x")) return node.x();
	else return node.y();
    }
    
    public String printNodes () {
	String ret = "";
	Iterator itNodes = ((Collection) mNodes.entrySet()).iterator();
	while (itNodes.hasNext()) ret = ret + "\n " + itNodes.next();
	return ret;
    }

    public String printNode (int ni) {
	return ((Node) mNodes.get(new Integer (ni))).toString();
    }

    public String printElement (int ei) {
	Element elem = (Element) mElems.get(new Integer (ei));
	return elem.toString();
    }

    public String printEpsSigForSteel (int mi) {
	String ret = "\n\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element elem = (Element) itElems.next();
	    if (elem.material() == mi) {
		Node n1 = (Node) mNodes.get(new Integer (elem.getNode(0)));
		Node n2 = (Node) mNodes.get(new Integer (elem.getNode(1)));
		ret = ret + "\n " + ((n1.x()+n2.x())/2) + "\t" + 
		    ((n1.y()+n2.y())/2) + "\t" + elem.getEps1() + 
		    "\t" + elem.getS1();
	    }
	}
	return ret;
    }

    public String printForcesForCable (int mi) {
	String ret = "\n\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element elem = (Element) itElems.next();
	    if (elem.material() == mi) {
		Node n1 = (Node) mNodes.get(new Integer (elem.getNode(0)));
		Node n2 = (Node) mNodes.get(new Integer (elem.getNode(1)));
		ret = ret + "\n " + ((n1.x()+n2.x())/2) + "\t" + 
		    ((n1.y()+n2.y())/2) + "\t" + elem.getNodalForce("x",1) 
		    + "\t" + elem.getNodalForce("y",1);
	    }
	}
	return ret;
    }

    public String printBarResults () {
	String ret = "\n\n";
	ret = ret + "Id\tForce\tStress\tcoor\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element elem = (Element) itElems.next();
	    if (elem.type().equalsIgnoreCase("Bar")) {
		Node n1 = (Node) mNodes.get(new Integer (elem.getNode(0)));
		Node n2 = (Node) mNodes.get(new Integer (elem.getNode(1)));
		ret = ret + "\n " + elem.id() + "\t" + 
		    elem.getS1()*elem.parameter(0) + "\t" +
		    elem.getS1() +  "\t" +
		    n1.x() + "\t" + n1.y() + "\n \t \t \t" +
		    n2.x() + "\t" + n2.y() + "\n";
	    }
	}
	return ret;
    }

    public String printBarAtY (double y0, double yf) {
	String ret = "\n\n";
	ret = ret + "##Id\tXm\tYm\tStrain\tStress\tForce\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element elem = (Element) itElems.next();
	    if (elem.type().equals("Bar")) {
		Node n1 = (Node) mNodes.get(new Integer (elem.getNode(0)));
		Node n2 = (Node) mNodes.get(new Integer (elem.getNode(1)));
		double xm = (n1.x()+n2.x())/2;
		double ym = (n1.y()+n2.y())/2;
		if ((ym<=yf)&(ym>=y0)) {
		    ret = ret + "\n " + elem.id() + "\t" + xm + "\t" + ym + 
			elem.getEps1() +  "\t" + elem.getS1() +  "\t" + 
			"\t" + elem.getS1()*elem.parameter(0);
		}
	    }
	}
	return ret;
    }

    public String printMeanBars (String dir, float coor, double fyd) {
	String ret = "";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	double area_req = 0.0;
	double area_max = 0.0;
	int num_ele = 0;
	while (itElems.hasNext()) {
	    Element elem = (Element) itElems.next();
	    if (elem.type().equalsIgnoreCase("Bar")) {
		Node n1 = (Node) mNodes.get(new Integer (elem.getNode(0)));
		Node n2 = (Node) mNodes.get(new Integer (elem.getNode(1)));
		if (dir.equalsIgnoreCase("y")) {
		    if ((n1.x() == coor) & (n2.x() == coor)) {
			area_req = area_req + 
			    Math.abs(elem.getS1()*elem.parameter(0)/fyd);
			if (area_req > area_max) area_max = area_req;
			num_ele++;
		    }
		}
		else {
		    if ((n1.y() == coor) & (n2.y() == coor)) {
			area_req = area_req + 
			    Math.abs(elem.getS1()*elem.parameter(0)/fyd);
			if (area_req > area_max) area_max = area_req;
			num_ele++;		    
		    }
		}
	    }
	}
	//Printed: Position + Mean Surface + Max Surface
	if (num_ele != 0) {
	    ret = ret + coor + "\t" + area_req/num_ele + "\t" + area_max;
	}
	return ret;
    }


    public String printAdjustedAreas (String dir, float coor,
				      double area_min, double fyd, int mate) {
	String ret = "";
	double area_max = 0.0;
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element elem = (Element) itElems.next();
	    if (elem.type().equalsIgnoreCase("Bar")) {
		double area_req = 0.0;
		Node n1 = (Node) mNodes.get(new Integer (elem.getNode(0)));
		Node n2 = (Node) mNodes.get(new Integer (elem.getNode(1)));
		if (dir.equalsIgnoreCase("y")) {
		    if ((Math.abs(n1.x()-coor) < 0.001) & 
			(Math.abs(n2.x()-coor) < 0.001)) {
			area_req = 
			    Math.abs(elem.getS1()*elem.parameter(0)/fyd);
			if (area_req > area_max) area_max = area_req;
		    }
		}
		else {
		    if ((Math.abs(n1.y()-coor) < 0.001) & 
			(Math.abs(n2.y()-coor) < 0.001)) {
			area_req = 
			    Math.abs(elem.getS1()*elem.parameter(0)/fyd);
			if (area_req > area_max) area_max = area_req;
		    }
		}
	    }
	}
	//Segunda vuelta
	itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element elem = (Element) itElems.next();
	    if (elem.type().equalsIgnoreCase("Bar")) {
		double area_req = 0.0;
		Node n1 = (Node) mNodes.get(new Integer (elem.getNode(0)));
		Node n2 = (Node) mNodes.get(new Integer (elem.getNode(1)));
		if (dir.equalsIgnoreCase("y")) {
		    if ((Math.abs(n1.x()-coor) < 0.001) & 
			(Math.abs(n2.x()-coor) < 0.001)) {
			area_req = 
			    Math.abs(elem.getS1()*elem.parameter(0)/fyd);
			if (area_req < area_min) { area_req = area_min; }
			else { area_req = area_max; }
			ret = ret + "\nmodel.addElem(" + elem.id() + 
			    ",\"bar\"," + elem.getNode(0) + "," + 
			    elem.getNode(1) + "," + area_req + "," + mate
			    + ");";
		    }
		}
		else {
		    if ((Math.abs(n1.y()-coor) < 0.001) & 
			(Math.abs(n2.y()-coor) < 0.001)) {
			area_req = 
			    Math.abs(elem.getS1()*elem.parameter(0)/fyd);
			if (area_req < area_min) { area_req = area_min; }
			else { area_req = area_max; }
			ret = ret + "\nmodel.addElem(" + elem.id() + 
			    ",\"bar\"," + elem.getNode(0) + "," + 
			    elem.getNode(1) + "," + area_req + "," + mate
			    + ");";
		    }
		}
	    }
	}
	return ret;
    }

    public String printForcesForX (double xp, double tol, double x0,
				   double x1, double y0, double y1) {
	//x0-y1 limitate the area where the sum of forces is performed
	String ret = "\n\n";
	//String ret = ret + "Node \t x \t y \t fx \t fy";
	Map mNodalForces = new TreeMap();

	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    Node[] nod = new Node[ei.getNumNodes()];
	    double coorX = 0.0;
	    double coorY = 0.0;
	    for (int i=0; i <= (ei.getNumNodes()-1); i++) {
		nod [i] = ei.node(i);
		coorX = coorX + nod[i].x();
		coorY = coorY + nod[i].y();
	    }
	    coorX = coorX / ei.getNumNodes();
	    coorY = coorY / ei.getNumNodes();
	    if ( (coorX>=x0-tol) & (coorX<=x1+tol) & 
		 (coorY>=y0-tol) & (coorY<=y1+tol) ) {
		for (int i=0; i <= (ei.getNumNodes()-1); i++) {
		    Node ni = ei.node(i);
		    if ( Math.abs(xp-ni.x()) <= tol ) {
			if (mNodalForces.containsKey(new Integer(ni.id()))) {
			    NodalForce nf = (NodalForce) 
				mNodalForces.get(new Integer (ni.id()));
			    nf.addForces(ei.getNodalForce("x",i),
					 ei.getNodalForce("y",i));
			}
			else {
			    mNodalForces.put(new Integer (ni.id()),new NodalForce(ni.id(),ni.x(),ni.y(),ei.getNodalForce("x",i),ei.getNodalForce("y",i)));
			}
		    }
		}
	    }
	}
	Iterator itNF = ((Collection)mNodalForces.values()).iterator();
	while (itNF.hasNext()) {
	    NodalForce nf = (NodalForce) itNF.next();
	    ret = ret + "\n" + nf.id() + "\t" + nf.x() + "\t" + nf.y() + 
		"\t" + nf.fx() + "\t" + nf.fy();
	}
	return ret;
    }

    public double getForcesForNode (int nNum, String dir, double tol, double x0
				    , double x1, double y0, double y1) {
	//x0-y1 limitate the area where the sum of forces is performed
	Node node = (Node) mNodes.get(new Integer(nNum));
	NodalForce nf = new NodalForce(node.id(),node.x(),node.y(),0,0);
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    Node[] nod = new Node[ei.getNumNodes()];
	    double coorX = 0.0;
	    double coorY = 0.0;
	    for (int i=0; i <= (ei.getNumNodes()-1); i++) {
		nod [i] = ei.node(i);
		coorX = coorX + nod[i].x();
		coorY = coorY + nod[i].y();
	    }
	    coorX = coorX / ei.getNumNodes();
	    coorY = coorY / ei.getNumNodes();
	    if ( (coorX>=x0-tol) & (coorX<=x1+tol) & 
		 (coorY>=y0-tol) & (coorY<=y1+tol) ) {
		for (int i=0; i <= (ei.getNumNodes()-1); i++) {
		    Node ni = ei.node(i);
		    if ( Math.abs(node.x()-ni.x()) + 
			 Math.abs(node.y()-ni.y())<= tol ) {
			nf.addForces(ei.getNodalForce("x",i),
				     ei.getNodalForce("y",i));
		    }
		}
	    }
	}
	if (dir.equalsIgnoreCase("x")) return nf.fx();
	else return nf.fy();
    }

    private class NodalForce {
	private final int id;
	private final float x;
	private final float y;
	private double fx;
	private double fy;
	public NodalForce (int id, float x, float y, double fx, double fy) {
	    this.id = id;
	    this.x = x;
	    this.y = y;
	    this.fx = fx;
	    this.fy = fy;
	}
	public void addForces (double fx, double fy) {
	    this.fx = this.fx + fx;
	    this.fy = this.fy + fy;	    
	}
	public int id () { return id; }
	public float x () { return x; }
	public float y () { return y; }
	public double fx () { return fx; }
	public double fy () { return fy; }
    }

    public String printConcreteCot2 (double y0, double y1) {
	String ret = "\n\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equalsIgnoreCase("Conc3N")) {
		Node[] nod = {ei.node(0), ei.node(1), ei.node(2)};
		double coorY = (nod[0].y() + nod[1].y() + nod[2].y())/3;
		if ((coorY >= y0) & (coorY <= y1)) {
		    double coorX = (nod[0].x() + nod[1].x() + nod[2].x())/3;
		    double cot2;
		    if ((ei.getAlpha() + Math.PI/2 == 0.0)) {cot2 = 10000;}
		    else if ((ei.getAlpha() == 0.0)) {cot2 = 0;}
		    else {cot2 = 1.0/Math.tan(ei.getAlpha() + Math.PI/2);}
		    ret = ret + "\n " + coorX + "\t" + coorY + "\t" + cot2;
		}
	    }
	}
	return ret;
    }

    public String printConcreteTheta2 (double y0, double y1) {
	String ret = "\n\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equals("Conc3N")) {
		Node[] nod = {ei.node(0), ei.node(1), ei.node(2)};
		double coorY = (nod[0].y() + nod[1].y() + nod[2].y())/3;
		if ((coorY >= y0) & (coorY <= y1)) {
		    double coorX = (nod[0].x() + nod[1].x() + nod[2].x())/3;
		    double theta2;
		    theta2 = (ei.getAlpha() + Math.PI/2)*180/Math.PI;
		    if (theta2 > 90) theta2 = theta2-2*(theta2-90);
		    ret = ret + "\n " + coorX + "\t" + coorY + "\t" + theta2;
		}
	    }
	}
	return ret;
    }

    public String printConcreteEta2 (double y0, double y1) {
	String ret = "\n\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equalsIgnoreCase("Conc3N")) {
		Node[] nod = {ei.node(0), ei.node(1), ei.node(2)};
		double coorY = (nod[0].y() + nod[1].y() + nod[2].y())/3;
		if ((coorY >= y0) & (coorY <= y1)) {
		    double coorX = (nod[0].x() + nod[1].x() + nod[2].x())/3;
		    ret = ret + "\n " + coorX + "\t" + coorY + 
			"\t" + ei.getEta2();
		}
	    }
	}
	return ret;
    }

    public String printConcreteSigmas (double y0, double y1) {
	String ret = "\n\n";
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equalsIgnoreCase("Conc3N")) {
		Node[] nod = {ei.node(0), ei.node(1), ei.node(2)};
		double coorY = (nod[0].y() + nod[1].y() + nod[2].y())/3;
		if ((coorY >= y0) & (coorY <= y1)) {
		    double coorX = (nod[0].x() + nod[1].x() + nod[2].x())/3;
		    ret = ret + "\n " + coorX + "\t" + coorY +
			"\t" + ei.getS1()+ "\t" + ei.getS2();
		}
	    }
	}
	return ret;
    }

    public String printReaction (int node) {
	String ret = "";
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {
	    Boun bi = (Boun) itBouns.next();
	    if (bi.node() == node) {
		ret = ret + bi.toString();
	    }
	}
	return ret;
    }
    
    
    
    public void set_bondslipState(int state){
        if (state == 1){
            bondslip = 1;
        }else if (state == 0){
            bondslip = 0;
        }else{
            throw new java.lang.Error("Invalid choice.");
        }

    }

    public String printReactions () {
	String ret = "";
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {
	    Boun bi = (Boun) itBouns.next();
	    ret = ret + "\n" + bi.toString();
	}
	return ret;
    }

    public String toString () {
	String ret = "\n-MODEL PROPERTIES-";
	ret = ret + "\n\nNODES :";
	Iterator itNodes = ((Collection) mNodes.entrySet()).iterator();
	while (itNodes.hasNext()) ret = ret + "\n " + itNodes.next();
	ret = ret + "\n\nMATERIALS :";
	Iterator itMates = ((Collection) mMates.entrySet()).iterator();
	while (itMates.hasNext()) ret = ret + "\n " + itMates.next();
	ret = ret + "\n\nBOUNDARY CONDITIONS :";
	Iterator itBouns = ((Collection) mBouns.entrySet()).iterator();
	while (itBouns.hasNext()) ret = ret + "\n Node " + itBouns.next();
	ret = ret + "\n\nLOADS :";
	Iterator itLoads = ((Collection) mLoads.entrySet()).iterator();
	while (itLoads.hasNext()) ret = ret + "\n Node " + itLoads.next();
	ret = ret + "\n\nELEMENTS :";
	Iterator itElems = ((Collection) mElems.entrySet()).iterator();
	while (itElems.hasNext()) ret = ret + "\n " + itElems.next();
	return ret;
    }
    
    public String fileString () {
	String file = "";
	file+= "version,"+this.getVer()+"*"+'\n';
	file+="steps,"+this.getSteps()+"*"+'\n';
        file += "scaleforces," + this.getScaleForces() + "*\n\n";
        int numPos = 8; // number of characters user to output real numbers
        
	Map mMates = this.getMapOfMates();
	Iterator itMates = ((Collection) mMates.values()).iterator();
	while (itMates.hasNext()) {
	    Material mi = (Material) itMates.next();
            String matName = mi.type();
	    file = file + "m," + mi.id() + "," + matName + ","
                    + Jconc.formatNumber(mi.fc(), numPos) + ","
                    + Jconc.formatNumber(mi.modEl(), numPos);
            if (matName.equalsIgnoreCase("concrete")) {
               MaterialConc mc = (MaterialConc) mi;
               if (mc.getInputCoefEtas())
                  file += "," + mc.getCoefEta1() + "," + mc.getCoefEta2();
            }
            else if (matName.equalsIgnoreCase("steel")) {
               MaterialSteel ms = (MaterialSteel) mi;
               if (ms.modHard() != ms.modEl() / 10000) 
                  file += "," + ms.modHard();
            }
            else if (matName.equalsIgnoreCase("cable")) {
               MaterialCable mcab = (MaterialCable) mi;
               file += "," + Jconc.formatNumber(mcab.sig0(), numPos);
               if (mcab.modHard() != mcab.modEl() / 10000)
                  file += "," + Jconc.formatNumber(mcab.modHard(), numPos);
            }
            else if (matName.equalsIgnoreCase("concWithfct")) {
               file += "," + Jconc.formatNumber(((MaterialConcWithfct) mi).fct(), numPos);
            }
	    file += "*\n";
	}
        file += "\n";
	Map mBouns = this.getMapOfBouns();
	Iterator itBouns = ((Collection) mBouns.values()).iterator();
	while (itBouns.hasNext()) {
	    Boun bi = (Boun) itBouns.next();
	    file = file + "b," + bi.node() + "," + bi.type() + ","
                    + Jconc.formatNumber(bi.disp(), numPos) + "*\n";
	}
        file += "\n";
	Map mLoads = this.getMapOfLoads();
	Iterator itLoads = ((Collection) mLoads.values()).iterator();
        double scalLoc = this.getScaleForces();
        if (scalLoc == 0) scalLoc = 1.0;
	while (itLoads.hasNext()) {
	    Load li = (Load) itLoads.next();
            if (!li.noInput()) {
	       file = file + "f," + li.node() + ","
                       + Jconc.formatNumber(li.fxInput() / scalLoc, numPos) + ","
                       + Jconc.formatNumber(li.fyInput() / scalLoc, numPos) + "*\n";
            }
	}
        file += "\n";
	Map mNodes = this.getMapOfNodes();
	Iterator itNodes = ((Collection) mNodes.values()).iterator();
	while (itNodes.hasNext()) {
	    Node ni = (Node) itNodes.next();
	    file = file + "n," + ni.id() + ","
                    + Jconc.formatNumber(ni.x(), numPos) + ","
                    + Jconc.formatNumber(ni.y(), numPos) + "*\n";
	}
        file += "\n";
	Map mElems = this.getMapOfElems();
	Iterator itElems = ((Collection) mElems.values()).iterator();
	while (itElems.hasNext()) {
	    Element ei = (Element) itElems.next();
	    if (ei.type().equalsIgnoreCase("Conc3N")) {
		file = file + "e," + ei.id() + ",conc3N," + 
		    ei.getNode(0) + "," + ei.getNode(1) + "," + ei.getNode(2)
		    + "," +  Jconc.formatNumber(ei.parameter(1), numPos) + "," +  
		    ei.material() + "*";
			file += '\n';//modified code
	    }
	    else if (ei.type().equalsIgnoreCase("Bar")) {
		file = file + "e," + ei.id() + ",bar," + 
		    ei.getNode(0) + "," + ei.getNode(1)
		    + "," +  Jconc.formatNumber(ei.parameter(1), numPos) + "," +
		    ei.material() + "*";
			file += '\n';//modified code
	    }
	    else if (ei.type().equalsIgnoreCase("Cable")) {
               Cable c = (Cable) ei;
               file = file + "e," + ei.id() + ",cable," +
               ei.getNode(0) + "," + ei.getNode(1) + "," +
               Jconc.formatNumber(ei.parameter(1), numPos) + "," +
               ei.material() + "*";
               file += '\n';
	    }
	}	
	return file;
    }

}
