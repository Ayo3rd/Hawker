package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;

import itp341.otegbade.opeoluwa.myfinal.project.app.R;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.Ingredient;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.InventorySingleton;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuItem;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuSingleton;


public class MenuActivity extends AppCompatActivity {

    //Initialize Views
    private ListView listView;

    private MenuListAdapter menuListAdapter;

    private Button addButton;

    private Button saveButton;


    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  ArrayList<MenuItem> menuItemsMain;

    //Request Code
    private static final int REQUEST_CODE_ADD_MENU_ITEM = 500;
    private static final int REQUEST_ING_POP = 800;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        //Initialize Views
        listView = findViewById(R.id.listViewMain);
        addButton = findViewById(R.id.addButton);
        saveButton = findViewById(R.id.saveButton);

        //Make List View
        menuListAdapter = new MenuListAdapter(this, new ArrayList<MenuItem>());
        listView.setAdapter(menuListAdapter);

        //load menuItems and fill adapter
        superUpdater();


        //Take to add menu page
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, CreateMenuItemActivity.class);
                startActivityForResult(intent,REQUEST_CODE_ADD_MENU_ITEM);
            }
        });

        //Save menu and return
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        superUpdater();
    }


    //Custom adapter for menu
    private class MenuListAdapter extends ArrayAdapter<MenuItem> {
        private ArrayList<MenuItem> menuItems;
        public MenuListAdapter(Context context, ArrayList<MenuItem> menuItems){
            super(context, R.layout.layout_list_menu, menuItems);
            this.menuItems = menuItems;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Check if convertView exists
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_menu, parent, false);
            }

            //Movie for specific position
            final MenuItem menuItem = menuItems.get(position);

            //Update convertView
            final TextView itemName = convertView.findViewById(R.id.itemName);
            final TextView itemPrice = convertView.findViewById(R.id.itemPrice);
            final Button itemIngButton = convertView.findViewById(R.id.itemIngButton);
            final Button itemEditButton = convertView.findViewById(R.id.itemEditButton);

            itemName.setText(menuItem.getItem());
            itemPrice.setText(moneyFormat(String.valueOf(menuItem.getPrice())));


            //set tag
            itemIngButton.setTag(position);
            itemEditButton.setTag(position);

            //Button Listener
            itemIngButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //intent with position
                    Intent intent = new Intent(MenuActivity.this,IngPopActivity.class);
                    intent.putExtra(IngPopActivity.EXTRA_POSITION,position);
                    startActivityForResult(intent, REQUEST_ING_POP);
                }
            });

            itemEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //intent with position
                    Intent intent = new Intent(MenuActivity.this, CreateMenuItemActivity.class);
                    intent.putExtra(CreateMenuItemActivity.EXTRA_POSITION, position);
                    startActivityForResult(intent, REQUEST_CODE_ADD_MENU_ITEM);
                }
            });
            return convertView;
        }
    }

    //Convert double to to $0.00 format
    private String moneyFormat(String money)
    {
        if(money.indexOf('.') == -1)
        {
            return "$" +  money;
        }
        else
        {
            String dec = money.substring(money.indexOf('.')+1 , money.length());
            if(dec.length() == 1)
            {
                return "$" + money.substring(0, money.indexOf('.')+1) + dec + "0";
            }
            else
            {
                return "$" + money.substring(0, money.indexOf('.')+1) + dec.substring(0,2);
            }
        }
    }

    //Update AdapterList and Singleton
    private void superUpdater()
    {
        db.collection("menuItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        menuItemsMain = new ArrayList<>();

                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc: task.getResult())
                            {
                                MenuItem menuItem = doc.toObject(MenuItem.class);
                                menuItemsMain.add(menuItem);
                            }
                            menuListAdapter.clear();
                            menuListAdapter.addAll(menuItemsMain);
                            menuListAdapter.notifyDataSetChanged();
                            //update Singleton
                            MenuSingleton.get(MenuActivity.this).setAllMenuItems(menuItemsMain);
                        }
                    }
                });
    }

}