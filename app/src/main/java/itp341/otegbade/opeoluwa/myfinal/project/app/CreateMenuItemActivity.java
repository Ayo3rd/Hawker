package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import itp341.otegbade.opeoluwa.myfinal.project.app.model.Ingredient;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.InventorySingleton;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuItem;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuSingleton;


public class CreateMenuItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_POSITION = CreateMenuItemActivity.class.getPackage().getName() + "position";

    private int passedPosition = -1;

    private EditText itemText;

    private EditText priceText;

    private Spinner spinnerIng;

    private EditText qtyText;

    private Button addButton;

    private Button saveButton;

    private Button deleteButton;

    private Ingredient currIng;


    private ListView ingListView;

    private IngredientListAdapter ingredientListAdapter;


    private ArrayList<Ingredient> itemIngs = new ArrayList<>();


    //Get All Ingredient List
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  ArrayList<Ingredient> ingredients = InventorySingleton.get(this).getAllIngredients();




    //Initialize String array of spinner options
    private String[] ingList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);


        //Assign views
        itemText = findViewById(R.id.itemText);
        priceText = findViewById(R.id.priceText);
        spinnerIng = findViewById(R.id.spinnerIng);
        qtyText = findViewById(R.id.qtyText);
        ingListView = findViewById(R.id.ingListView);
        addButton = findViewById(R.id.addButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);


        //Get Intent
        Intent intent = getIntent();
        if (intent != null){
            passedPosition = intent.getIntExtra(EXTRA_POSITION, -1);

            if (passedPosition > -1){
                // Grab the MenuItem at a given index
                MenuItem mItem = MenuSingleton.get(this).getMenuItem(passedPosition);
                loadData(mItem);
            }
        }


        //Handle Spinner
        //Load ingredient string array
        ingList = new String[ingredients.size()];
        for(int i = 0; i < ingredients.size(); i++)
        {
            ingList[i] = ingredients.get(i).getName() + " (" + ingredients.get(i).getUnit() + ")";
        }
        //Initialize spinner listener
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ingList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIng.setAdapter(adapter);
        spinnerIng.setOnItemSelectedListener(this);

        //Make added ingredients List View
        if(itemIngs != null)
        {
            ingredientListAdapter = new IngredientListAdapter(this, itemIngs);
            ingListView.setAdapter(ingredientListAdapter);
        }

        //Add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Hide keyboard
                hideKeyboard((Button)view);
                //add ingredient
                addIng();
            }
        });

        //Save Button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMenuItem();
            }
        });

        //Onclick tell them how to delete
        ingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(CreateMenuItemActivity.this, getResources().getString(R.string.long_press_delete), Toast.LENGTH_SHORT).show();
            }
        });

        //Delete ingredient On Long press
        ingListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteIng(i);
                Toast.makeText(CreateMenuItemActivity.this, getResources().getString(R.string.ing_deleted), Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //Delete MenuItem Button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteMenuItem();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Get Ingredient selected and set to current
        currIng = ingredients.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    //Add Ingredient to Menu Item
    private void addIng()
    {
        //Get index if it already exists
        int index = -1;
        for(int i = 0; i < itemIngs.size(); i++)
        {
            if(currIng.equals(itemIngs.get(i)))
            {
                index = i;
            }
        }
        //If the item doesn't exist
        if(index == -1)
        {
            Ingredient tempIng = new Ingredient(currIng.getName(), Double.parseDouble(qtyText.getText().toString()),currIng.getUnit() );
            itemIngs.add(tempIng);
        }
        else
        {
            Ingredient tempIng = new Ingredient(currIng.getName(), Double.parseDouble(qtyText.getText().toString()),currIng.getUnit() );
            itemIngs.set(index, tempIng);
        }

        //Clear editText and Reset List view
        qtyText.setText("");
        ingredientListAdapter.notifyDataSetChanged();
    }

    //Delete Ingredient from Menu Item
    private void deleteIng(int pos)
    {
        itemIngs.remove(pos);
        ingredientListAdapter.notifyDataSetChanged();
    }

    //Save MenuItem
    private void saveMenuItem()
    {
        //Get editText value
        String itemName = itemText.getText().toString();
        double itemPrice = Double.parseDouble( priceText.getText().toString());

        //Set MenuItem
        MenuItem tempMenuItem = new MenuItem(itemName,itemPrice,itemIngs);

        //Add  or update menuItem to Menu Singleton
        if(passedPosition == -1)
        {
            MenuSingleton.get(this).addMenuItem(tempMenuItem);
        }
        else
        {
            MenuSingleton.get(this).updateMenuItem(passedPosition, tempMenuItem);
        }
        dbUpdate();
        finish();
    }

    //Delete MenuItem
    private void deleteMenuItem()
    {
        if(passedPosition != -1)
        {
            //delete from db
            db.collection("menuItems").document(MenuSingleton.get(this).getMenuItem(passedPosition).getItem().toLowerCase()).delete();
            //delete from singleton
            MenuSingleton.get(this).removeMenuItem(passedPosition);
        }
        dbUpdate();
        finish();
    }

    //Database Update
    private void dbUpdate(){
        //Get Singleton
        ArrayList<MenuItem> tempMenuItems = MenuSingleton.get(this).getAllMenuItems();
        //Loop through them
        for(int i = 0; i < tempMenuItems.size(); i++)
        {
            //Add Ingredients to db
            db.collection("menuItems")
                    .document(tempMenuItems.get(i).getItem().toLowerCase())
                    .set(tempMenuItems.get(i));
        }
    }

    //Load MenuItem
    private void loadData(MenuItem mItem){
        //Set Text
        itemText.setText(mItem.getItem());
        priceText.setText(String.valueOf(mItem.getPrice()));

        //Set List of Ingredients
        itemIngs = mItem.getIngredients();
    }

    //Hide keyboard
    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch(Exception ignored) {
        }
    }

    //Custom adapter for inventory
    private class IngredientListAdapter extends ArrayAdapter<Ingredient> {
        private ArrayList<Ingredient> ingredients2;
        public IngredientListAdapter(Context context, ArrayList<Ingredient> ingredients2){
            super(context, R.layout.layout_list_menu_item_ings, ingredients2);
            this.ingredients2 = ingredients2;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Check if convertView exists
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_menu_item_ings, parent, false);
            }

            //Movie for specific position
            final Ingredient ingredient = ingredients2.get(position);

            //Initialize convertView
            final TextView ingName = convertView.findViewById(R.id.ingName);
            final TextView ingQty = convertView.findViewById(R.id.ingQty);
            final TextView ingUnit = convertView.findViewById(R.id.ingUnit);

            //Update convertView
            ingName.setText(ingredient.getName());
            ingQty.setText(String.valueOf(ingredient.getQuantity()));
            ingUnit.setText(ingredient.getUnit());

            return convertView;
        }
    }
}
