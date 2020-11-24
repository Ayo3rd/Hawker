package itp341.otegbade.opeoluwa.myfinal.project.app.model;

import android.content.Context;

import java.util.*;

public class SaleSingleton {

    private static SaleSingleton singleton;


    private HashMap<String, ArrayList<Sale>> allSales; // String is format of date, Set of Sales
    private Context context;


    private SaleSingleton(Context context){
        this.context = context;
        //Initialize allSales Map
        allSales = new HashMap<String, ArrayList<Sale>>();
    }

    //Get instance of singleton
    public static SaleSingleton get(Context context){
        if(singleton == null){
            singleton = new SaleSingleton(context);
        }
        return singleton;
    }

    public void addSale(Sale mySale){
        //Check if date(key) does not exist yet
        if(!allSales.containsKey(mySale.getDateKey()))
        {
            //Create List and add Sale
            ArrayList<Sale> newList = new ArrayList<Sale>();
            newList.add(mySale);
            //Add new Map entry
            allSales.put(mySale.getDateKey(), newList);
        }
        else{// date(key) already exists
            //Add sale to correct date(key) ArrayList
            allSales.get(mySale.getDateKey()).add(mySale);
        }
    }


    public ArrayList<Sale> getSalesByDate(String date){
        //If date exists
        if(allSales.containsKey(date))
        {
            //return list at date
            return allSales.get(date);
        }
        else{//If it doesn't exist
            //return empty list
            ArrayList<Sale> newList = new ArrayList<Sale>();
            return newList;
        }
    }











}
