/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cercis.jconc.fem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.io.FileWriter;
import java.io.IOException;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.Arrays;
import java.util.List;


import com.google.gson.Gson;

/**
 *
 * @author Damien Scantamburlo
 */
public class Dumper {
    private ArrayList u_fieldList;
    private String date_calculation_begin;
    private String jconc_descriptor;
    private String date_calculation_end;
    private JSONArray errors;
    private String desktopPath;
    private double [][] P_mat;
    
    public Dumper (Model model) {
        jconc_descriptor = model.fileString();
         
	u_fieldList = new ArrayList();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
        date_calculation_begin = dateFormat.format(date);
        
        errors = new JSONArray();
        
        desktopPath = System.getProperty("user.home") + "/Desktop";
        
    }
    public void add_P(double[][] P){
        P_mat = P;
        
    }
    public void add_u (double[] u) {
        List u_lst = Arrays.asList(u);
        u_fieldList.addAll(u_lst);

    }
    
    public void add_Error(float error_current){
        errors.add(error_current);
       
    }
    
    public void set_end_time () {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
        date_calculation_end = dateFormat.format(date);

    }
    
    public void resume(){
        JSONObject obj = new JSONObject();
        obj.put("date_begin", date_calculation_begin);
        obj.put("date_end", date_calculation_end);
        obj.put("errors_list", errors);
        obj.put("jconc_input",jconc_descriptor);
        
        Gson gson2 = new Gson();
        String P_matjson = gson2.toJson(P_mat);
        obj.put("P_mat",P_matjson);
                
        Gson gson = new Gson();
        String u_fieldList_Jsn = gson.toJson(u_fieldList);
        obj.put("disps_list", u_fieldList_Jsn);
        
        BufferedWriter bw = null;
        FileWriter fw = null;
        
        try {
            
            fw = new FileWriter(desktopPath + "/dumped_fromJconc.info");
            bw = new BufferedWriter(fw);
            bw.write(obj.toJSONString());        

            System.out.println("Successfully Copied JSON Object to File...");
            bw.close();
            fw.close();
            
        } catch (Exception e) {
            System.out.println("Exception occurred");
        
        }
 
    }
    
    
    
    
    
    
    
    
    
    
}
