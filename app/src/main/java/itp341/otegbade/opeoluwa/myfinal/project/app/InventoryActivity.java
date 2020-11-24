package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import itp341.otegbade.opeoluwa.myfinal.project.app.model.Ingredient;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.InventorySingleton;


public class InventoryActivity extends AppCompatActivity {

    private ListView listView;

    private InventoryListAdapter inventoryListAdapter;
    private Button addButton;
    private Button saveButton;


    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  ArrayList<Ingredient> ingredientsMain;

    //Request Codes
    private static final int REQUEST_CODE_ADD_ING = 400;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //Initialize listView
        listView = findViewById(R.id.listViewMain);
        addButton = findViewById(R.id.addButton);
        saveButton = findViewById(R.id.saveButton);


        //Initialize Adapter
        inventoryListAdapter = new InventoryListAdapter(this, new ArrayList<Ingredient>());
        listView.setAdapter(inventoryListAdapter);


        //load ingredients and fill adapter
        superUpdater();

        //OnClick on listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //
                Intent intent = new Intent(InventoryActivity.this, CreateIngredientActivity.class);
                intent.putExtra(CreateIngredientActivity.EXTRA_POSITION, position);
                startActivityForResult(intent, REQUEST_CODE_ADD_ING);
            }
        });


        //Take to add ingredient page
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send to add activity
                Intent intent = new Intent(InventoryActivity.this, CreateIngredientActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_ING);
            }
        });


        //Save current values for ingredients
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //updateIng();
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        superUpdater();
    }

    //Update AdapterList and Singleton
    private void superUpdater()
    {
        db.collection("ingredients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ingredientsMain = new ArrayList<>();

                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc: task.getResult())
                            {
                                Ingredient ingredient = doc.toObject(Ingredient.class);
                                ingredientsMain.add(ingredient);
                            }
                            inventoryListAdapter.clear();
                            inventoryListAdapter.addAll(ingredientsMain);
                            inventoryListAdapter.notifyDataSetChanged();
                            //update Singleton
                            InventorySingleton.get(InventoryActivity.this).setAllIngredients(ingredientsMain);
                        }
                    }
                });
    }


    //Custom adapter for inventory
    private class InventoryListAdapter extends ArrayAdapter<Ingredient> {
        private ArrayList<Ingredient> ingredients;
        public InventoryListAdapter(Context context, ArrayList<Ingredient> ingredients){
            super(context, R.layout.layout_list_ingredient, ingredients);
            this.ingredients = ingredients;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Check if convertView exists
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_ingredient, parent, false);
            }

            //Ingredient for specific position
            final Ingredient ingredient = ingredients.get(position);

            //Initialize convertView
            final TextView ingName = convertView.findViewById(R.id.ingName);
            final TextView ingQty = convertView.findViewById(R.id.ingQty);
            final TextView ingUnit = convertView.findViewById(R.id.ingUnit);

            //Update convertView
            ingName.setText(ingredient.getName());
            ingQty.setText(twoDPFormat(String.valueOf(ingredient.getQuantity())));
            ingUnit.setText(ingredient.getUnit());

            //Set Warning Color if ingredient quantity is 0 or less
            if(ingredient.getQuantity() <= 0)
            {
                ingQty.setTextColor(getResources().getColor(R.color.red));
            }
            else
            {
                ingQty.setTextColor(getResources().getColor(R.color.black));
            }
            //Return convertView
            return convertView;
        }
    }

    private String twoDPFormat(String money)
    {
        if(money.indexOf('.') == -1)
        {
            return money;
        }
        else
        {
            String dec = money.substring(money.indexOf('.')+1 , money.length());
            if(dec.length() == 1)
            {
                return money.substring(0, money.indexOf('.')+1) + dec + "0";
            }
            else
            {
                return money.substring(0, money.indexOf('.')+1) + dec.substring(0,2);
            }
        }
    }

}
