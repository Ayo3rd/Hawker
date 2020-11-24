package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import itp341.otegbade.opeoluwa.myfinal.project.app.model.Ingredient;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuSingleton;


public class IngPopActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = IngPopActivity.class.getPackage().getName() + "position";

    private int position = -1;

    private ListView ingListView;

    private IngredientListAdapter ingredientListAdapter;

    private  ArrayList<Ingredient> ingredients;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_ing_pop);

        //Initialize ListView
        ingListView = findViewById(R.id.ingListView);


        //Get Intent, Default = -1 if empty
        Intent intent = getIntent();
        if(intent != null){
            position = intent.getIntExtra(EXTRA_POSITION, -1);
            //If position was passed
            if(position > -1){
                //Get ArrayList of Ingredients for menu item at this position
                ingredients = MenuSingleton.get(this).getMenuItem(position).getIngredients();
            }
        }

        //Apply List view
        ingredientListAdapter = new IngredientListAdapter(this, ingredients);
        ingListView.setAdapter(ingredientListAdapter);


        //Create resized window for activity
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //Get dimensions
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getWindow().setLayout((int)(width*0.7), (int)(height*0.5));
    }


    //Custom adapter for inventory
    private class IngredientListAdapter extends ArrayAdapter<Ingredient> {
        private ArrayList<Ingredient> ingredients;
        public IngredientListAdapter(Context context, ArrayList<Ingredient> ingredients){
            super(context, R.layout.layout_list_menu_item_ings, ingredients);
            this.ingredients = ingredients;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Check if convertView exists
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_menu_item_ings, parent, false);
            }

            //Movie for specific position
            final Ingredient ingredient = ingredients.get(position);

            //Update convertView
            final TextView ingName = convertView.findViewById(R.id.ingName);
            final TextView ingQty = convertView.findViewById(R.id.ingQty);
            final TextView ingUnit = convertView.findViewById(R.id.ingUnit);

            ingName.setText(ingredient.getName());
            ingQty.setText(String.valueOf(ingredient.getQuantity()));
            ingUnit.setText(ingredient.getUnit());

            return convertView;
        }
    }
}