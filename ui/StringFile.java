
package net.cercis.jconc.ui;

import java.awt.Color;
import java.util.StringTokenizer;


public class StringFile {
  private String contenu = "";
  private String[] blocs;
  private char codeC;

  public StringFile(String s) {
    char[] codeCs = "*".toCharArray();
    codeC = codeCs[0];
    contenu = "\n" + s.replace(codeC, '\u253C');
    blocs = contenu.split("\n" + '\u253C');
    for (int i = 1; i < blocs.length; i++) {
      blocs[i] = codeC + blocs[i].replace('\u253C', codeC);
      //System.out.println("Bloc no " + i + "\n" + blocs[i]);
    }
  }

  public StringFile(String s, String codeChar) { // permet de faire une
    // separation en blocs sur un autre caractere que l'etoile habituelle
    char[] codeCs = codeChar.toCharArray();
    codeC = codeCs[0];
    contenu = "\n" + s.replace(codeC, '\u253C');
    blocs = contenu.split("\n" + '\u253C');
    for (int i = 1; i < blocs.length; i++) {
      blocs[i] = codeC + blocs[i].replace('\u253C', codeC);
      //System.out.println("Bloc no " + i + "\n" + blocs[i]);
    }
  }
  public int countBlock(){
  
  	return blocs.length;
  }

  public String[] getBlock(String nom) {
    String s[] = new String[1];
    int num = 0;
    for (int i = 1; i < blocs.length; i++) {
      if (blocs[i].toLowerCase().startsWith(codeC + nom.toLowerCase())) {
        num = i;
        break;
      }
    }
    s = blocs[num].split("\n");
    return s;
  }

  public String getStringBlock(String nom) {
    String s = "";
    String searchS = codeC + nom.toLowerCase();
    int lenS = searchS.length();
    int num = 0;
    for (int i = 1; i < blocs.length; i++) {
      // System.out.println(blocs[i].toLowerCase() + " <==> " + codeC + nom.toLowerCase());
      if (blocs[i].toLowerCase().substring(0, lenS).compareTo(searchS) == 0) {
        num = i;
        break;
      }
    }
    s = blocs[num].toString();
    if (num == 0) System.out.println("Name " + nom +
                                     " not found by getStringBlock");
    return s;
  }

  public String getValue(String nom) {
    String s = "";
    String searchS = codeC + nom.toLowerCase();
    int lenS = searchS.length();
    int num = 0;
    for (int i = 1; i < blocs.length; i++) {
/*      if ( (blocs[i].length() >= lenS)
          && blocs[i].toLowerCase().substring(0, lenS).compareTo(searchS) == 0) {
        num = i;
        break;
      }*/
		      
	    if (blocs[i].length() >= lenS){
		    StringTokenizer st = new StringTokenizer(blocs[i],"=,");
		    String str = st.nextToken();
		    if(str.toLowerCase().compareTo(searchS) == 0) {
			  //  System.out.println(blocs[i]);
		      	    num = i;
		    	    break;
		    }
      	    }

    }
    s = blocs[num].toString();
    s = s.replaceAll("\n", "");
    s = s.replaceAll("\r", "");
    s = s.substring(s.indexOf("=") + 1, s.length());
    if (num == 0) System.out.println("Name " + nom +
                                     " not found by getStringBlock");
    return s;
  }

  public String getValue(String nom, int index) {
    String s = getValue(nom);
    //System.out.println("nom is "+nom);
    String[] values = s.split(",");
    try {
      return values[index - 1];
    }
    catch (Exception e) {
      return s;
    }
  }

  public String[] getAllValues(String nom) {
    String s = getValue(nom);
    String[] values = s.split(",");
    return values;
  }

  public String getValue(String nom, int index, String sep) {
    String s = getValue(nom);
    String[] values = s.split(sep);
    try {
      return values[index - 1];
    }
    catch (Exception e) {
      return s;
    }
  }

  public Color getColorValue(String nom, int index) {
    String s = getValue(nom);
    String[] values = s.split(",");
    String colCode = "";
    Color c = Color.BLUE;
    try {
      colCode = values[index - 1].toLowerCase();
      int pos = colCode.indexOf(";");
      if (pos >= 0) { // transparence ou code RGB
        pos = colCode.indexOf(";", pos + 1);
        if (pos < 0) { // deux parametres, le second est la transparence de 0 a 1
           String vals[] = colCode.split(";");
           c = decodeColor(vals[0], vals[1]);
        }
        else {
          String[] RGB = colCode.split(";");
          if (RGB.length == 3)
            c = new Color(Integer.valueOf(RGB[0]).intValue(),
                          Integer.valueOf(RGB[1]).intValue(),
                          Integer.valueOf(RGB[2]).intValue());
          else // on suppose qu'il y en a quatre: transparence en quatrieme
            c = new Color(Integer.valueOf(RGB[0]).intValue(),
                          Integer.valueOf(RGB[1]).intValue(),
                          Integer.valueOf(RGB[2]).intValue(),
                          Integer.valueOf(RGB[3]).intValue());
        }
      }
      else { // decoder le nom de la couleur demandee
        c = decodeColor(colCode, "1.0");
      }
      //System.out.println(nom + " est " + colCode);
      return c;
    }
    catch (Exception e) {
      System.out.println("Unknown Color, " + colCode + " (element " + nom +
                         " # " + index + ")");
      return c;
    }
  }

  public static Color decodeColor(String nom, String transp) {
    Color c;
    float alpha;
    try {
      alpha = Float.valueOf(transp).floatValue();
    }
    catch (Exception e) {
      alpha = 1.0f;
    }
    c = Color.blue;
    if (nom.compareTo("black") == 0) c = Color.black;
    else if (nom.compareTo("blue") == 0) c = Color.blue;
    else if (nom.compareTo("cyan") == 0) c = Color.cyan;
    else if (nom.compareTo("darkgray") == 0) c = Color.darkGray;
    else if (nom.compareTo("gray") == 0) c = Color.gray;
    else if (nom.compareTo("green") == 0) c = Color.green;
    else if (nom.compareTo("lightgray") == 0) c = Color.lightGray;
    else if (nom.compareTo("magenta") == 0) c = Color.magenta;
    else if (nom.compareTo("orange") == 0) c = Color.orange;
    else if (nom.compareTo("pink") == 0) c = Color.pink;
    else if (nom.compareTo("red") == 0) c = Color.red;
    else if (nom.compareTo("white") == 0) c = Color.white;
    else if (nom.compareTo("yellow") == 0) c = Color.yellow;
    else if (nom.compareTo("darkred") == 0) c = new Color(128,0,0);
    else if (nom.compareTo("darkgreen") == 0) c = new Color(0,128,0);
    else if (nom.compareTo("darkblue") == 0) c = new Color(0,0,128);
    else if (nom.compareTo("darkmagenta") == 0) c = new Color(128,0,128);
    else if (nom.compareTo("darkyellow") == 0) c = new Color(128,128,0);
    else if (nom.compareTo("darkcyan") == 0) c = new Color(0,128,128);

    c = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (alpha * 255));

    return c;
  }
}