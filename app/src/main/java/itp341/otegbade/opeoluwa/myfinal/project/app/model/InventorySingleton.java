package itp341.otegbade.opeoluwa.myfinal.project.app.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;

public class InventorySingleton {

    private static InventorySingleton singleton;

    private ArrayList<Ingredient> allIngredients;
    private Context context;


    private InventorySingleton(Context context){
        this.context = context;
        allIngredients = new ArrayList<>();

    }

    //Get instance of singleton
    public static InventorySingleton get(Context context){
        if(singleton == null){
            singleton = new InventorySingleton(context);
        }
        return singleton;
    }

    public ArrayList<Ingredient> getAllIngredients(){
        return allIngredients;
    }

    public void setAllIngredients(ArrayList<Ingredient> ingredients){
        this.allIngredients = ingredients;
    }

    public void addIngredient(String name, double quantity, String unit){
        //Create ingredient
        Ingredient newIngredient = new Ingredient(name,quantity,unit);
        //Add to singleton
        allIngredients.add(newIngredient);
        //update
    }

    public Ingredient getIngredient(int i){
        return allIngredients.get(i);
    }


    public void setIngredientQuantity(String name, double newQty){
        for(int i = 0; i < allIngredients.size(); i++)
        {
            if(name.equals(allIngredients.get(i).getName()))
            {
                allIngredients.get(i).setQuantity(newQty);
            }
        }
    }

    public double getIngredientQuantity(String name){
        for(int i = 0; i < allIngredients.size(); i++)
        {
            if(name.equals(allIngredients.get(i).getName()))
            {
                return allIngredients.get(i).getQuantity();
            }
        }
        return -1;
    }

    public void removeIngredient(int i){
        allIngredients.remove(i);
    }

    public void updateIngredient(int i, String name, double quantity, String unit){
        allIngredients.set(i,new Ingredient(name, quantity, unit));
    }
}
