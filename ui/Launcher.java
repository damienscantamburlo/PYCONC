
package net.cercis.jconc.ui;

import net.cercis.jconc.fem.Model;
import net.cercis.jconc.fem.Node;
import net.cercis.jconc.fem.Solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.StringTokenizer;

class Launcher {

//    private static double k0, k1, k2, k3, k4, k5, k6, k7, k8, k9, k10, k11, k12, k13, k14, k15, k16, k17, k18, k19, k20, lambda;
// used in input of "tranchée couverte" by MFR
   
    private static Model model = null;
    private static boolean isApplet;
    private static String defaultAppletView;

    private static String tmp = "";
    private static String stmp = "";
    private static String fileName = "";
    private static String fileRoot = "";
    private static double scaleForces = (float) 1.0;
    private static String curLang = "";
    private static int numIter = -1;
    private static boolean printResults = false;
    private static boolean printAllResults = false;
    private static boolean noDisplay = false;
    private static boolean closePlot = false;
    private static String plot = "";
    private static String plotType = "";
    private static double minStressLevel = 0;
    private static boolean noShade = false;
    private static int maxLenVec = -1;
    private static int numGroups = -1;
    private static String[] groupNames;
    private static double[] forceFactors;
    private static int width = 800;
    private static int height = 600;
    private static String messages = "";

    public static void main(String args[]){
	isApplet = false;

   messages += "Jconc version " + Jconc.getVersion() + "\n";
   java.util.Date theDate = new java.util.Date();
   java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
   messages += dateFormatter.format(theDate) + "\n";
   messages += "----------------------------------------\n";


	//Introduced Code ----- Rohit
	ParseInput pi = new ParseInput(args,args.length);
	if(!pi.isValidInput()){

		System.out.println("Invalid input");
	}
	// Jconc.setLang(pi.getInputPara()[0]);
   curLang = pi.getParamByName("-l");
   if (curLang == null) curLang = "English";
   Jconc.setLang(curLang);
   fileName = pi.getParamByName("-i");

    
   if (fileName.indexOf(".") > 0)
      fileRoot = fileName.substring(0,fileName.indexOf("."));
   else
      fileRoot = fileName;

   tmp = pi.getParamByName("-showargs");
   stmp = pi.showAllParams();
   messages += stmp;
   if (tmp != null) System.out.println(stmp);

   tmp = pi.getParamByName("-name");
    if ((tmp != null))
       if ((tmp.indexOf("\\") > 0) || (!(fileName.indexOf("\\") > 0))) // consider it is a valid path
          fileRoot = tmp;
       else
          fileRoot = fileName.substring (0, fileName.lastIndexOf("\\")) + "\\" + tmp;

   tmp = pi.getParamByName("-scaleforces");
   if (tmp != null) scaleForces = Double.parseDouble(tmp);

   tmp = pi.getParamByName("-scaleforcegroups");
   if (tmp != null) {
         if (!tmp.endsWith(";")) tmp += ";";
         String[] grp = tmp.split(";");
         numGroups = grp.length;
         groupNames = new String[numGroups];
         forceFactors = new double[numGroups];
         for (int i = 0; i < grp.length; i++) {
            String[] sp = grp[i].split(",");
            groupNames[i] = sp[0];
            forceFactors[i] = Double.parseDouble(sp[1]);
            System.out.println("Load group '" + groupNames[i] + "' factor " + forceFactors[i]);
         }
   }

    tmp = pi.getParamByName("-iterations");
    if (tmp != null) numIter = Integer.parseInt(tmp);

    tmp = pi.getParamByName("-stroke");
    if (tmp != null) {
       int strokeWidth = Integer.parseInt(tmp);
       Jconc.setStrokeWidth(strokeWidth);
    }

    tmp = pi.getParamByName("-print");
    if ((tmp != null) && tmp.equals("true")) printResults = true;

    tmp = pi.getParamByName("-printall");
    if ((tmp != null) && tmp.equals("true")) printAllResults = true;

    tmp = pi.getParamByName("-closeplot");
    if ((tmp != null) && tmp.equals("true")) closePlot = true;

    tmp = pi.getParamByName("-plot");
    if ((tmp != null)) plot = tmp;

    tmp = pi.getParamByName("-plottype");
    if ((tmp != null)) plotType = tmp;

    tmp = pi.getParamByName("-width");
    if (tmp != null) width = Integer.parseInt(tmp);

        tmp = pi.getParamByName("-height");
    if (tmp != null) height = Integer.parseInt(tmp);

    tmp = pi.getParamByName("-minStressLevel");
    if (tmp != null)
       minStressLevel = Double.parseDouble(tmp);
    Jconc.setMinStressLevel(minStressLevel);

    tmp = pi.getParamByName("-noShade");
    if ((tmp != null) && tmp.equals("true")) noShade = true;
    Jconc.setNoShade(noShade);

    tmp = pi.getParamByName("-maxLenVec");
    if (tmp != null) maxLenVec = Integer.parseInt(tmp);
    Jconc.setMaxLenVec(maxLenVec);

    tmp = pi.getParamByName("-nodisplay");
    if ((tmp != null) && tmp.equals("true")) {
       noDisplay = true;
       closePlot = true;
    }

    if(fileName.equals("Default")){
                //Please put your corresponding java file in the INPUT folder
                String name = "simple_column.jconc";
                
                String fileName = System.getProperty("user.dir") + "\\src\\net\\cercis\\jconc\\input\\" + name;
                System.out.println("FORCED JCONC FILE : " + fileName);
                readModelbyFile(fileName);

    }else{
                readModelbyFile(fileName);
	}
	//End of Introduced Code
    }

    public Launcher (String data) {
	isApplet = true;
//	Jconc.appApp = 0;
	createModel(data);
    }

    public static Model getModel () {
	return model;
    }
    
    public static void readModelbyFile(String fileName){
        String str = "";
                try{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String string = br.readLine();
			while(string!=null){
				str+=string+"\n";
				string = br.readLine();
			}
		}
		catch(IOException e){

			System.out.println("Error reading file " + fileName);
		}
                
		createModel(str);
		if(isApplet == false)
			loadViewer();
                
    }

    public static void createModel (String data) {
        int numPar = 0;
        int numLines = 1;
        double par[] = new double [50]; // 50 parameters should be enough !
	model = new Model();
	int loadSteps = 0;
	StringTokenizer st = new StringTokenizer(data, "*\n£");

	while (st.hasMoreTokens()) {
           numLines++;
	    StringTokenizer st2 = new StringTokenizer(st.nextToken(),",\t");
	    String comp = st2.nextToken();
	    if (comp.equals("n")) {

		model.addNode(Integer.parseInt(st2.nextToken()),
			      Double.parseDouble(st2.nextToken()),
			      Double.parseDouble(st2.nextToken()));
	    }
	   else if (comp.equals("m")) {
               int numMat = Integer.parseInt(st2.nextToken());
               numPar = 0;
               double res = 0;
               double fct = 0;
               double Emod = 0;
               double sig0 = 0;
               double hardMod = 0;
               //matafalc MODIFIED 141123 UNTIL NEXT //matafalc
               double epsut = 0;
               //matafalc MODIFIED 141123
               //matafalc MODIFIED 140816 UNTIL NEXT //matafalc
               double parSoft = 0;
               double maxEps = 0;
               double residualTens = 0;
               //matafalc MODIFIED
               String typeMat = st2.nextToken();
               for (int iPar = 1; iPar < 100; iPar++) {
                  try {
                     par[iPar] = Double.parseDouble(st2.nextToken());
                     numPar++; }
                  catch (Exception exc) { // at end of line, ignore
                     //System.out.println ("Line " + numLines + ": Cannot read parameter " + iPar + ", total " + numPar + " parameters for material " + numMat + " (" + typeMat + ")");
                     break;
                  }
               }
               if (false) { // activate to see details of material properties
                  System.out.println("Material " + numMat + " " + typeMat);
                  for (int i = 1; i <= numPar; i++) 
                     System.out.println("Param " + i + " = " + par[i]);
               }
               if (typeMat.compareToIgnoreCase("concrete") == 0) {
                  res = par[1];
                  Emod = par[2];
                  if (numPar == 2) {
                     model.addMate(numMat, typeMat, res, Emod);
                  }
                  else if (numPar == 3) { //old iMesh format : param 3 is Poisson's ratio (not used here)
                     System.out.println ("Line " + numLines + " 3rd material parameter ignored : " + par[3]);
                     model.addMate(numMat, typeMat, res, Emod);
                  }
                  else if (numPar == 4)  {
                     double coefEta1 = par[3];
                     double coefEta2 = par[4];
                     model.addMate(numMat, typeMat, res, Emod, coefEta1, coefEta2);
                  }
                  else {
                     System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                  }
               }
               else
                  if (typeMat.compareToIgnoreCase("steel") == 0) {
                     res = par[1];
                     Emod = par[2];
                     if (numPar == 2) {
                        model.addMate(numMat, typeMat, res, Emod);
                     }
                     else if (numPar == 3) {
                        double Ehard = par[3];
                        model.addMate(numMat, typeMat, res, Emod, Ehard);                     
                     }
                     else {
                        System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
               else
                  if (typeMat.compareToIgnoreCase("strucSteel") == 0) {
                     res = par[1];
                     Emod = par[2];
                     if (numPar == 2) {
                        model.addMate(numMat, typeMat, res, Emod);
                     }
                     else if (numPar == 3) {
                        double Ehard = par[3];
                        model.addMate(numMat, typeMat, res, Emod, Ehard);                     
                     }
                     else {
                        System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
               else
                  if (typeMat.compareToIgnoreCase("steelTC") == 0) {
                     res = par[1];
                     Emod = par[2];
                     if (numPar == 2) {
                        model.addMate(numMat, typeMat, res, Emod);
                     }
                     else if (numPar == 3) {
                        double Ehard = par[3];
                        model.addMate(numMat, typeMat, res, Emod, Ehard);                     
                     }
                     else {
                        System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
               else
                  if (typeMat.compareToIgnoreCase("compOnly") == 0) {
                     res  = par[1];
                     Emod = par[2];
                     if (numPar == 2) {
                        model.addMate(numMat, typeMat, res, Emod);
                     }
                     else if (numPar == 3) {
                        hardMod = par[3];
                        model.addMate(numMat, typeMat, res, Emod, hardMod);
                     }
                     else {
                        System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
               else
                  if (typeMat.compareToIgnoreCase("tensOnly") == 0) {
                     res  = par[1];
                     Emod = par[2];
                     if (numPar == 2) {
                        model.addMate(numMat, typeMat, res, Emod);
                     }
                     else if (numPar == 3) {
                        hardMod = par[3];
                        model.addMate(numMat, typeMat, res, Emod, hardMod);
                     }
                     else {
                        System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
               else
                  if (typeMat.compareToIgnoreCase("cable") == 0) {
                     res  = par[1];
                     Emod = par[2];
                     sig0 = par[3];
                     if (numPar == 3) { // sig0 only
                        model.addMate(numMat, typeMat, res, sig0, Emod);
                     }
                     else if (numPar == 4) { // sig0 + hardening
                        hardMod = par[4];
                        model.addMate(numMat, typeMat, res, sig0, Emod, hardMod);                     
                     }
                     else {
                        System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
               else
                  if (typeMat.compareToIgnoreCase("concWithfct") == 0) {
                     res  = par[1];
                     Emod = par[2];
                     fct  = par[3];
                     if (numPar == 3) {
                        model.addMate(numMat, typeMat, res, fct, Emod);
                     }
            //matafalc MODIFIED 141123 UNTIL NEXT //matafalc
                     else if (numPar == 4) {
                         epsut = par[4];
                         model.addMate(numMat, typeMat, res, fct, Emod, epsut);
                     }
            //matafalc MODIFIED 141123
                     else {
                         System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
	//matafalc MODIFIED 140816 UNTIL NEXT //matafalc
               else
                  if (typeMat.compareToIgnoreCase("steelExpSoft") == 0) {
                     res  = par[1];
                     Emod = par[2];
                     parSoft = par[3];
                     if (numPar == 3) {
                        model.addMate(numMat, typeMat, res, Emod, parSoft);
                     }
                     else {
                     System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
               else
                  if (typeMat.compareToIgnoreCase("steelLinSoft") == 0) {
                     res  = par[1];
                     Emod = par[2];
                     hardMod = par[3];
                     if (numPar == 3) {
                        model.addMate(numMat, typeMat, res, Emod, hardMod);
                     }
                     else if (numPar == 4) {
                         maxEps = par[4];
                         model.addMate(numMat, typeMat, res, Emod, hardMod, maxEps);
                     }
                     else if (numPar == 5) {
                         maxEps = par[4];
                         residualTens = par[5];
                         model.addMate(numMat, typeMat, res, Emod, hardMod, maxEps, residualTens);
                     }
                     else {
                         System.out.println ("Line " + numLines + ": Incorrect material input, material # " + numMat + " (" + typeMat + ") " + numPar + " parameters");
                     }
                  }
	//matafalc MODIFIED 140816
               else
                 System.out.println ("Unknown material type '" + typeMat + "'");
           }
         else if (comp.equals("e")) {
		int elemNumber = Integer.parseInt(st2.nextToken());
		String elemType = st2.nextToken();
		if (elemType.equals("conc3N")) {
		    model.addElem(elemNumber,elemType,
				  Integer.parseInt(st2.nextToken()),
				  Integer.parseInt(st2.nextToken()),
				  Integer.parseInt(st2.nextToken()),
				  Float.parseFloat(st2.nextToken()),
				  (int) Float.parseFloat(st2.nextToken()));
		}
		else if (elemType.equals("bar")) {
		    model.addElem(elemNumber, elemType,
				  Integer.parseInt(st2.nextToken()),
				  Integer.parseInt(st2.nextToken()),
				  Float.parseFloat(st2.nextToken()),
				  (int) Float.parseFloat(st2.nextToken()));
		}
		//Modified code ----- rohit
		else if (elemType.equals("cable")) {
		    model.addElem(elemNumber, elemType,
				  Integer.parseInt(st2.nextToken()),
				  Integer.parseInt(st2.nextToken()),
				  Float.parseFloat(st2.nextToken()),
				  (int) Float.parseFloat(st2.nextToken()));


		}
		//End of modified code ----- rohit
	    }
	    else if (comp.equals("b")) {

		model.addBoun(Integer.parseInt(st2.nextToken()),
			      st2.nextToken(),
			      Float.parseFloat(st2.nextToken()));
	    }
	    else if (comp.equals("f")) {
      int nodeNumber = Integer.parseInt(st2.nextToken());
      double fX = Double.parseDouble(st2.nextToken());
      double fY = Double.parseDouble(st2.nextToken());
      String forceGroupName = "";
      if (st2.hasMoreTokens()) forceGroupName = st2.nextToken();
      double factor = scaleForces;
      for (int i = 0; i < numGroups;i++)
         if (groupNames[i].equalsIgnoreCase(forceGroupName)) {
            factor = forceFactors[i];
            break;
         }
		model.addLoad(nodeNumber,factor * fX, factor * fY);
	    }
	    else if (comp.equals("steps")) {
		loadSteps = Integer.parseInt(st2.nextToken());
      if (numIter > 0) loadSteps = numIter;
		model.setSteps(loadSteps); //Introduced ---- rohit
	    }
	    //Introduced code ---- rohit
	    else if (comp.equals("version")) {
	    	if(st2.countTokens() ==2)
			model.setVer((st2.nextToken()+","+st2.nextToken()));
		else
			model.setVer("1,0.01");
	    }
	    //End of introduced code ---- rohit
	    else if (comp.equals("view")) {
		defaultAppletView = st2.nextToken();
	    }
       else if (comp.equals("scaleforces")) {
          scaleForces = Double.parseDouble(st2.nextToken());
          model.setScaleForces(scaleForces);
       }
       else if (comp.equals("bondslip")){
           String bondslip_state = st2.nextToken();
           if (bondslip_state.equals("on") || bondslip_state.equals("off")){
               //The choice is valid
               if (bondslip_state.equals("on")){
                   System.out.println("ADVANCED MODEL OPTION : The bondslip effect will be taken into consideration.");
                   model.set_bondslipState(1);
               }else{
                   System.out.println("ADVANCED MODEL OPTION : The bondslip effect will NOT be taken into consideration.");
                   model.set_bondslipState(0);
               }
           }else{
               //That's not permitted
               throw new java.lang.Error("Please provid a valid option for the bondslip status -> on/off.");
           }

       }
	}
        
        // Threat the model if necessary (for bondslip effect consideration)
        if (model.bondslip == 1){
            model.duplicate_nodes();
        }
            
        

	if(loadSteps != 0) //modified code --- rohit
		messages += solveModel(loadSteps);
   Jconc.setMessages(messages);

	//System.out.println(model.toString());

    }

    public String getDefaultAppletView () {
	return defaultAppletView;
    }

    public static String solveModel (int loadSteps) {

   String s = model.checkNodes();
   if (s.length() > 0) {
      String stmp = "Error : unconnected nodes detected\n" + s;
      System.out.println (stmp);
      s += stmp;
   }
   else {
	   Solver solver = new Solver(model);
	   s += solver.fullNR(loadSteps, 1e-11);
   }
   String stmp = model.getJobInfo();
   stmp += Jconc.memStatus();
   stmp += model.getMemInfo();
   return stmp + s;
    }

    public static void loadViewer () { //***********Warning************changed the scope of loadViewer from private to public
 	Viewer viewer = new Viewer(model);
   Jconc.setMessages(messages);
   viewer.displayResults("mesh", width, height, noDisplay);
   String[] plotReq;
   if (plot.length() != 0) {
     if (plotType.equals("")) plotType = "png";
      if (!plot.endsWith(";")) plot += ";";
      plotReq = plot.split(";");
      //System.out.println("plotReq : " + plotReq.length + " commandes");
      for (int i = 0; i < (plotReq.length); i++) {
         if (!plotReq[i].endsWith(",")) plotReq[i] += ",";

         String[] plotOpt = plotReq[i].split(",");
         String plotCmd = plotOpt[0];
         double plotScale1 = 0;
         double plotScale2 = 0;
         if (plotOpt.length > 1 ) {
               plotScale1 = Double.parseDouble(plotOpt[1]);
               if (plotOpt.length > 2) plotScale2 = Double.parseDouble(plotOpt[2]);
            }
         System.out.println("Plotting " + plotCmd + ", " + plotScale1 + ", " + plotScale2);
         if (plotType.equalsIgnoreCase("all")) {
            viewer.saveResults(plotCmd, plotScale1,  plotScale2, "eps", fileRoot + "_" + plotCmd + "." + "eps");
            viewer.saveResults(plotCmd, plotScale1,  plotScale2, "png", fileRoot + "_" + plotCmd + "." + "png");
         }
         else
            viewer.saveResults(plotCmd, plotScale1,  plotScale2, plotType, fileRoot + "_" + plotCmd + "." + plotType);

      }
   }
  //String reqPlot = "defo";
   //viewer.saveResults(reqPlot,plotType, fileRoot + "_" + reqPlot + "." + plotType);
	//OPTIONS POSSIBLES
	if (printResults) viewer.printResults(fileRoot); //Imprimer resultats
	if (printAllResults) viewer.getJc().printAllResults(fileRoot); //Imprimer tous les resultats dans un seul fichier
	if (closePlot) viewer.close(); //Fermer la fenetre
    }
    public static void computeModel () {

	model = new Model();

model.setSteps(20);
model.addMate(1,"concrete",40,30000);
model.addMate(2,"steel",480,205000);

//NODES

double posX[] = new double [9+1]; //No uso el indice cero
double posY[] = new double [7+1];

posX[1] = 0;
posX[2] = 0.05;
posX[3] = 0.1;
for (int i=4; i<=8; i++) {
    posX[i] = posX[i-1] + 0.054;
}
posX[9] = posX[8] + 0.05;


posY[1] = 0;
posY[2] = 0.05;
for (int i=3; i<=7; i++) {
      posY[i] = posY[i-1] + 0.054;
}

for (int i=1; i<=9; i++) {
    for (int j=1; j<=7; j++) {
	model.addNode(j*1000+i,posX[i],posY[j]);
    }
}


//ELEMENTS
int numEle = 1;

for (int i=1; i<=8; i++) {

    for (int j=1; j<=6; j++) {

	double espe = 0.19;

	//Elementos de hormigon
	model.addElem(numEle++,"conc3N",1000*j+i,1000*j+i+1,1000*(j+1)+i,espe,1);
	model.addElem(numEle++,"conc3N",1000*j+i+1,1000*(j+1)+i+1,1000*(j+1)+i,espe,1);


    }

    //Barras de cierre
    double a_hor = 1.0618e-3;
    model.addElem(numEle++,"bar",2000+i,2000+(i+1),a_hor,2);

}



//BOUNDARIES

model.addBoun(1003,"uy",0);

  for (int j=1; j<=7; j++) {
    model.addBoun(j*1000 + 9, "ux", 0);
  }

//LOADS

double reac = 0.357; //MN, reaccion por alma

model.addLoad(1002,0.0,0.25*reac);
model.addLoad(1004,0.0,0.25*reac);

//Refuerzo
model.addLoad(7009,0.0,-0.25*reac);
model.addLoad(7008,0.0,-0.5*reac);
model.addLoad(7007,0.0,-0.25*reac);


   messages += model.getJobInfo();
   String s = model.checkNodes();
   if (s.length() > 0) {
      String stmp = "Error : unconnected nodes detected\n" + s;
      System.out.println (stmp);
      messages += stmp;
   }
   else
      messages += solveModel(model.getSteps()); //ARG = LOAD STEPS
   Jconc.setMessages(messages);
	if (isApplet == false) loadViewer();

    }
}