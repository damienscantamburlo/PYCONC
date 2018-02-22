package net.cercis.jconc.ui;

// OB / 02.09.2009 : new version of the class to handle a wide variety of
//                   input options

public class ParseInput{

   private String params[][];
   private int nParams = 0;
   private boolean calc;
   private boolean test;
	
	public boolean getCalc(){
	
		return calc;
	}
	public String[] getInputPara(){
   String str[] = new String[2];
      str[0] = getParamByName ("-l");
      str[1] = getParamByName ("-i");
		return str;
	}
  
	public boolean isValidInput(){
	
		return true;  // it should always be valid as far as I can tell
	}
  
  public String getParamByName (String param) {
     String val = null;
     if (param.equalsIgnoreCase("-i")) val = "Default";
     for (int i = 0; i < nParams; i++)
        if (params[i][0].equalsIgnoreCase(param)) {
           val = params[i][1];
           break;
        }
    return val;
  }

  public String showAllParams () {
     String s = "";
     s = "Parameters of the calculation:\n";
     for (int i = 0; i < nParams; i++)
        s += (i+1) + " : " + params[i][0] + " = " + params[i][1] + "\n";
     return s + "\n";
  }

  	public ParseInput(String[] args, int argv){

      if(argv == 0){
      params = new String[2] [2];
      params[0][0] = "-l";
      params[0][1] = "English";
      params[1][0] = "-i";
      params[1][1] = "Default";
      nParams = 2;
		}
    else {
      params = new String[argv] [2];
      int i = 0;
      nParams = 0;
//      System.out.println(argv + " arguments");
      while (i < argv) {
//        System.out.println("Arg " + i + "= '" + args[i] + "'");
        String c = args[i].substring(0, 1);
        if ((c.equals("-")) || (c.equals("/")))  {
          params [nParams][0] = args[i];
          if ((i+1) < argv) {
            if(args[i+1].startsWith("-") || args[i+1].startsWith("/"))
              params[nParams][1] = "true";
            else {
              params[nParams][1] = args[i+1];
              if (params[nParams][1].length() > 1)
                 if (params[nParams][1].startsWith("*")) params[nParams][1] = params[nParams][1].substring(1, params[nParams][1].length());
//              System.out.println("Value " + params[nParams][1]);
            }
            nParams++;
          }
          else {
             params [nParams][0] = args[i];
             params [nParams][1] = "true";
             nParams++;
          }
        }
        else { // old-style format : only one argument : file name
          if (argv == 1) {
            params[0][0] = "-l";
            params[0][1] = "English";
            params[1][0] = "-i";
            params[1][1] = args[0];
          }
        }
        i++;
      }
    }
  }
}
