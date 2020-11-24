package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import itp341.otegbade.opeoluwa.myfinal.project.app.model.Ingredient;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.InventorySingleton;

public class CreateIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = CreateIngredientActivity.class.getPackage().getName() + "position";

    private int passedPosition = -1;

    private EditText nameText;

    private EditText qtyText;

    private EditText unitText;

    private Button saveButton;

    private Button deleteButton;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        //Assign views
        nameText = findViewById(R.id.nameText);
        qtyText = findViewById(R.id.qtyText);
        unitText = findViewById(R.id.unitText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);


        //Get Intent
        Intent intent = getIntent();
        if (intent != null){
            passedPosition = intent.getIntExtra(EXTRA_POSITION, -1);

            if (passedPosition > -1){
                // Grab the ingredient at a given index
                Ingredient ingredient = InventorySingleton.get(this).getIngredient(passedPosition);
                loadData(ingredient);
            }
        }

        //Save Button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIng();
            }
        });

        //Delete Button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteIng();
            }
        });


    }

    //Save Ingredient
    private void saveIng()
    {
        //Get editText value
        String tempName = nameText.getText().toString();
        double tempQty = Double.parseDouble( qtyText.getText().toString());
        String tempUnit = unitText.getText().toString();

        //Create tempIng
        Ingredient tempIng = new Ingredient(tempName,tempQty,tempUnit);

        //Add or Update ingredient to inventory singleton
        if(passedPosition == -1)
        {
            InventorySingleton.get(this).addIngredient(tempName, tempQty, tempUnit);
            //db.collection("ingredients").add(tempIng);
        }
        else
        {
            InventorySingleton.get(this).updateIngredient(passedPosition, tempName,tempQty,tempUnit);
        }
        dbUpdate();
        finish();
    }

    //Delete Ingredient
    private void deleteIng()
    {
        if(passedPosition != -1)
        {
            //delete from db
            db.collection("ingredients").document(InventorySingleton.get(this).getIngredient(passedPosition).getName().toLowerCase()).delete();
            //delete from singleton
            InventorySingleton.get(this).removeIngredient(passedPosition);
        }
        dbUpdate();
        finish();
    }

    //Load ingredient
    private void loadData(Ingredient ingredient){
        nameText.setText(ingredient.getName());
        qtyText.setText(String.valueOf(ingredient.getQuantity()));
        unitText.setText(ingredient.getUnit());
    }

    //Database Update
    private void dbUpdate(){
        //Get Singleton
        ArrayList<Ingredient> tempIngs = InventorySingleton.get(this).getAllIngredients();
        //Loop through them
        for(int i = 0; i < tempIngs.size(); i++)
        {
            //Add Ingredients to db
            db.collection("ingredients")
                    .document(tempIngs.get(i).getName().toLowerCase())
                    .set(tempIngs.get(i));
        }
    }
}
