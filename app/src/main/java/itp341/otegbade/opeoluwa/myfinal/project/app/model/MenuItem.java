package itp341.otegbade.opeoluwa.myfinal.project.app.model;

import java.util.ArrayList;

public class MenuItem {


    private String item; //Menu Item

    private double price; //Price

    private ArrayList<Ingredient> Ingredients; //List of ingredients

    //Empty Constructor
    public MenuItem()
    {

    }
    //Constructor for creating Menu items
    public MenuItem(String item, double price, ArrayList<Ingredient> Ingredients){
        super();
        this.item = item;
        this.price = price;
        this.Ingredients = Ingredients;
    }


    public String getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        Ingredients = ingredients;
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
