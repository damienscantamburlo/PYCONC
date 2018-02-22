package net.cercis.jconc.ui;

import java.net.URL;
import java.net.URLConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.DataInputStream;
import java.net.MalformedURLException;


public class GetFileList{

	String str[];
	int length;
        String  postString  = "";
	String  block;
    	String  beginOfBlock;
    	int     part;                        // part of the saving
    	public  int     nbFiles;
    	public  String message = "";
    	boolean nameOK=false;
	URL     url;
	String urlBase;
    	URL     urlImages;
    	URL     urlLists;
    	String parameters;
    	int     id;
    	int reponse;
	public GetFileList(String urlBase){
		

		this.urlBase = urlBase;
		id =0;

		readURL();
	
	}

	public int getLength(){
	
		return length;
	}

	public void parameters (String ip, String fileName,String programName, String version,boolean all, boolean group, boolean ecrase){
        	part            = 0;
		parameters      = "type=save&id=" + id +"&filename="+fileName+"&IPadress="+ip+"&programName="+programName+"&version="+version+"&all="+all+"&group="+group+"&ecrase="+ecrase+"&fieldSep=\n";
		block           = parameters + "&part=" + part + "&memo=";
    	}


	public String readURL(){

		String str="";
		
		try{
		
			URL urlini;
			urlini = new URL(urlBase+"isbetonpost.ini");
			URLConnection urlConn = urlini.openConnection();
			urlConn.setDoInput (true);
			urlConn.setDoOutput (true);
			urlConn.setUseCaches(false);
			BufferedReader br = new BufferedReader(new InputStreamReader (urlConn.getInputStream()));
			str = br.readLine();
			url = new URL(str);
			br.close();
		}
		catch(IOException e){
			System.out.println("Wrong IO");
		}

		return str;
	}
      	public String readURLFile() throws Exception {
		String data = "";
		URL myurl = null;
		URLConnection file = null;

		try {
			myurl = new URL(urlBase+"text.ini");
			file = myurl.openConnection();    }
		    	catch (Exception e) {
			  	System.out.println("Creation or connexion error to URL " + e.getMessage());
		    	}
		    	int count = 0;
		    	int i = 0;
		    	try {
			  	InputStream is = file.getInputStream();
			  	byte[] b = new byte[1000]; // max 1000 caracteres par ligne...
			  	int c = 0;
			  	while ( (c = is.read()) > -1) {
					switch (c) {
				      		case '\r':
					    		break; // sauter, pour ne pas faire doublon avec les \n
				      		case '\n': // fin de ligne : concatener le tampon b avec data
					    		b[i++] = (byte) c;
					    		String s = new String(b);
					    		data += s.substring(0, i);
					    		b = new byte[1000];
					    		i = 0;
					    		break;
				      		default:
					    		b[i++] = (byte) c;
					    		break;
					}
					        count++;
						
			  	}
				
			  	if (is != null) is.close();
		    	}
		    	catch (Exception e) {
			  	System.out.println("Error reading text.ini file " + " ligne " + i);
		    	}
		    	if (data.length() == 0) {
			  	System.out.println("Throwing exception");
				Exception e = new Exception();
			  	throw e;
		    	}
		    	return data;
      	}
	
	public String readFile(String type, String program, String fileName){

	    try{
		    URLConnection urlConn;

		    String query = "type="+type+"&id="+id+"&filename="+fileName;
		    //System.out.println(urlStr);

		   // System.out.println("Connection openeed");
			//url = new URL();
		    urlConn = url.openConnection();
		    urlConn.setDoInput (true);
		    urlConn.setDoOutput(true);
		    urlConn.setUseCaches(false);
		    urlConn.setDefaultUseCaches(false);
		    //using code from connectToDb.java
		    OutputStream outStream = urlConn.getOutputStream();
		    byte [] bites = query.getBytes();
		    outStream.write(bites);
		    outStream.flush();
		    outStream.close();
		    String str = "";
		    //end of the used code
		    BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		    String st = br.readLine();
		    while(st!=null){
			    str+=st;
			    st = br.readLine();
		    }
		    str = str.substring(5);
		    return str;
 	    }
	    catch(MalformedURLException e){
    		    System.out.println("URL is incorrect");
	    }
	    catch(IOException e){
    		    System.out.println("IO Error");
	    }
	    return "";
	}

	public String[] getFileList(int group, String program){
	    try{
		    URLConnection urlConn;
		    String query = "type=liste&id=0&program="+program+"&group="+group;
		   // System.out.println("query is : "+query);
		    //System.out.println(urlStr);
		    str = new String[100000];
		    length = 0;
		    //System.out.println("Connection openeed");
			//url = new URL();
		    urlConn = url.openConnection();
		    urlConn.setDoInput (true);
		    urlConn.setDoOutput(true);
		    urlConn.setUseCaches(false);
		    urlConn.setDefaultUseCaches(false);
		    //using code from connectToDb.java
		    OutputStream outStream = urlConn.getOutputStream();
		    byte [] bites = query.getBytes();
		    outStream.write(bites);
		    outStream.flush();
		    outStream.close();
		    BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		    String st= br.readLine();
		    while(st!=null){
		    
		    	str[length++]=st;
			st = br.readLine();
			//System.out.println(str[length-1]);
		    }
		    return str;
	    }
	    catch(MalformedURLException e){
    		    System.out.println("URL is incorrect");
	    }
	    catch(IOException e){
    		    System.out.println("IO Error");
	    }
	    return str;
	} 
    	private void saveByPost (String s)  {
		try {
	    		URLConnection      urlConn;
			DataOutputStream   dos;
	    		DataInputStream    dis;
//			System.out.println("Entering saveByPost");
//			System.out.println(url.toString());
//			System.out.println("Rading url");
	    		urlConn = url.openConnection();
	    		urlConn.setDoInput(true);
	    		urlConn.setDoOutput(true);
	    		urlConn.setUseCaches(false);
	    		urlConn.setDefaultUseCaches(false);
	    		OutputStream outStream = urlConn.getOutputStream();
	    		byte [] bites = s.getBytes();
	    		outStream.write(bites);
	    		outStream.flush();
	    		outStream.close();
	    		BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
	    		String inputLine;
	    		while ((inputLine = br.readLine()) != null) {
			//	System.out.println("Voici la valeur retournee = "+ inputLine);
				reponse = Integer.valueOf(inputLine).intValue();
			//	System.out.println("Response is "+reponse);
				gestionErreurs(reponse);			
	    		}
	    		br.close();	
		} // end of "try"
		catch (MalformedURLException mue) {
	    		System.out.println("mue error in saveByPost");
		}
		catch (IOException ioe) {
	    		System.out.println("IO Exception in saveByPost\n"+ioe);
		}
    	}	
    	public boolean write(String s){    // Creation of the string to send to the database
//	  	System.out.println(block);
//		System.out.println(s);
		block = block + s;
	//	System.out.println("Save by post called");
	  	saveByPost(block);
		return nameOK;
    	}
    	public String getSeparatorInBase () {
	  	String value = "";
	  	String s = "type=fieldsep";
	  	try {
	       		URLConnection urlConn;
	       		DataOutputStream    dos;
			DataInputStream     dis;
	       		urlConn = url.openConnection();
	       		urlConn.setDoInput(true);
	       		urlConn.setDoOutput(true);
	       		urlConn.setUseCaches(false);
	       		urlConn.setDefaultUseCaches(false);
	       		OutputStream outStream = urlConn.getOutputStream();
	       		byte[] bites = s.getBytes();
	       		outStream.write(bites);
	       		outStream.flush();
	       		outStream.close();
	       		BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
	       		String inputLine;
	       		while ( (inputLine = in.readLine()) != null) {
		     		value = value + inputLine;
	       		}
	       		in.close();
	  
		}
	  	catch (MalformedURLException mue) {
	      		System.out.println("mue error in getSeparatorInBase");
	      	//	System.out.println(url.toString() + s);
	  }
	  
    	  catch (IOException ioe) {
		  System.out.println("IO Exception");
    	  }
     	  return value;
    }
    public boolean isConnect () {
	  	String value = "";
	  	String s = "type=connect";
	  	try {
	       		URLConnection urlConn;
	       		DataOutputStream    dos;
			DataInputStream     dis;
	       		urlConn = url.openConnection();
	       		urlConn.setDoInput(true);
	       		urlConn.setDoOutput(true);
	       		urlConn.setUseCaches(false);
	       		urlConn.setDefaultUseCaches(false);
	       		OutputStream outStream = urlConn.getOutputStream();
	       		byte[] bites = s.getBytes();
	       		outStream.write(bites);
	       		outStream.flush();
	       		outStream.close();
	       		BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
	       		String inputLine;
	       		while ( (inputLine = in.readLine()) != null) {
		     		value = value + inputLine;
	       		}
	       		in.close();
	  
		}
	  	catch (MalformedURLException mue) {
			System.out.println("mue error in getSeparatorInBase");
			//	System.out.println(url.toString() + s);
	  	}
      		catch (IOException ioe) {
      			System.out.println("IO Exception");
      		}
		if(value.equals("0"))
			return true;
		else 
			return false;		
    }

    public void gestionErreurs(int reponse){
    	    if (reponse==0) {
		    nameOK = true;		    
		    message = "file saved";
    	    }
    	    else if (reponse==1){
		    nameOK = false;
		    message = "this file already exists";
    	    }
    	    else if (reponse==2){
		    nameOK = false;
		    message = "file not saved because it was not found back, retry!!!";
    	    }
	    else if (reponse==3){
		    nameOK = false;
		    message = "file not saved because the name is not authorized";
	    }
    	    else if (reponse==10){
		    nameOK = false;
		    message = "you're not connected!!!";
    	    }
   }


}






