package itp341.otegbade.opeoluwa.myfinal.project.app.model;

import java.util.*;
import java.time.*;

public class Sale {

    private ArrayList<SaleItem> saleItems; //List of SaleItems
    private Date date; //Date
    LocalDate localDate;
    String dateDayNum;
    String dateMonthNum;
    String dateYearNum;

    //Empty constructor
    public Sale(){
        date = new Date();// Current date
        localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        saleItems = new ArrayList<SaleItem>();
        dateDayNum  = String.valueOf(localDate.getDayOfMonth());
        dateMonthNum = String.valueOf(localDate.getMonthValue());
        dateYearNum = String.valueOf(localDate.getYear());
    }


    //Constructor initializes date and array
    public Sale(String buffer){
        date = new Date();// Current date
        localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        saleItems = new ArrayList<SaleItem>();
        dateDayNum  = String.valueOf(localDate.getDayOfMonth());
        dateMonthNum = String.valueOf(localDate.getMonthValue());
        dateYearNum = String.valueOf(localDate.getYear());
    }


    public void addSaleItem(SaleItem mySaleItem){
        saleItems.add(mySaleItem);
    }

    public ArrayList<SaleItem> getSaleItems(){return  saleItems;}

    public double getTotalPrice(){
        double total = 0;
        for(int i = 0; i < saleItems.size(); i++)
        {
           total += (saleItems.get(i).getPrice() * saleItems.get(i).getQuantity());
        }
        return total;
    }


    public String getDateKey(){
        return date.toString();
    }
    public String getDateDayNum(){
        return dateDayNum;
    }

    public String getDateMonthNum(){
        return dateMonthNum;
    }
    public String getDateYearNum(){
        return dateYearNum;
    }

    public String getDateShow(){
        //return localDate.getMonth()+ " " +String.valueOf(localDate.getDayOfMonth())+ ", " + String.valueOf(localDate.getYear());
        return Month.of(Integer.parseInt(dateMonthNum)).name()  + " "+ String.valueOf(dateDayNum)  + ", " + String.valueOf(dateYearNum);
    }

    public String showSale(){
        String finalPrint = "";
        for(int i = 0; i < saleItems.size(); i++)
        {
            if(saleItems.get(i).getQuantity() > 0)
            {
                finalPrint += String.valueOf(saleItems.get(i).getQuantity()) + " " +  saleItems.get(i).getItem() + "\n";
            }
        }
        return finalPrint;
    }
}

