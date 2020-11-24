package itp341.otegbade.opeoluwa.myfinal.project.app.model;

import java.util.*;

public class SaleItem {

    private String item; //Sale Item

    private int quantity; //Quantity

    private double price; //Price

    private ArrayList<Ingredient> Ingredients; //List of ingredients


    //Empty constructor
    public SaleItem(){

    }


    //Constructor for creating Sale items for purchase
    public SaleItem(String item, int quantity, double price, ArrayList<Ingredient> Ingredients){
        super();
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.Ingredients = Ingredients;
    }

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public ArrayList<Ingredient> getIngredients(){
        return Ingredients;
    }

    public void addIngredient(String name, int quantity, String unit){
        //Create ingredient
        Ingredient tempIngredient = new Ingredient(name,quantity,unit);
        //Add to arrayList
        Ingredients.add(tempIngredient);
    }
}
