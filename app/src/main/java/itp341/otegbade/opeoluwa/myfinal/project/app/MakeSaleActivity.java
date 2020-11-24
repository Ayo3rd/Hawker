package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

import itp341.otegbade.opeoluwa.myfinal.project.app.model.Ingredient;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.InventorySingleton;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuItem;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuSingleton;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.Sale;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.SaleItem;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.SaleSingleton;


public class MakeSaleActivity extends AppCompatActivity {

    //Initialize Views
    private ListView listView;

    private MenuListAdapter menuListAdapter;

    private TextView totalText;

    private Button backButton;

    private Button saveButton;

    private Button locButton;

    private Sale newSale = new Sale("buffer");


    private final ArrayList<MenuItem> menuItemsMain = MenuSingleton.get(this).getAllMenuItems();    //Get all MenuItems

    private ArrayList<SaleItem> saleItems = new ArrayList<>();  //Sale Item ArrayList

    final FirebaseFirestore db = FirebaseFirestore.getInstance();


    //Request Codes
    private static final int REQUEST_CODE_POP_SUCCESS = 1400;
    private static final int REQUEST_CODE_POP_LOC = 1800;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_sale);


        //Initialize Views
        listView = findViewById(R.id.listViewMain);
        totalText = findViewById(R.id.totalText);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);
        locButton = findViewById(R.id.locButton);

        //Initialize saleItems with null Values
        for(int i = 0; i< menuItemsMain.size(); i++)
        {
            saleItems.add(null);
        }

        //Make List View
        menuListAdapter = new MenuListAdapter(this, menuItemsMain);
        listView.setAdapter(menuListAdapter);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //Save Sale and restart
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If Save sale
                if(saveSale()) //true if saleItems have been selected
                {
                    //Go to success pop up activity to show image and success and total price
                    Intent intent = new Intent(MakeSaleActivity.this,SuccessPopActivity.class);
                    intent.putExtra(SuccessPopActivity.EXTRA_SALE, newSale.getTotalPrice());
                    startActivityForResult(intent, REQUEST_CODE_POP_SUCCESS);
                    recreate();
                }
            }
        });

        //Location Button
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to success pop up activity to show image and success and total price
                Intent intent = new Intent(MakeSaleActivity.this,LocPopActivity.class);
                startActivityForResult(intent, REQUEST_CODE_POP_LOC);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        menuListAdapter.notifyDataSetChanged();
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_sale_item, parent, false);
            }

            //MenuItem for specific position
            final MenuItem menuItem = menuItems.get(position);

            //Set saleItem variables
            final String fillName = menuItem.getItem();
            final double fillPrice = menuItem.getPrice();
            final ArrayList<Ingredient> fillIngArr = menuItem.getIngredients();

            //Update convertView
            final TextView itemName = convertView.findViewById(R.id.itemName);
            final Button minusButton = convertView.findViewById(R.id.minusButton);
            final EditText itemQty = convertView.findViewById(R.id.itemQty);
            final Button plusButton = convertView.findViewById(R.id.plusButton);

            //Set item Names
            itemName.setText(menuItem.getItem());


            //set tag buttons
            minusButton.setTag(position);
            plusButton.setTag(position);

            //Button Listeners
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Subtract 1 from number up to 0
                    int tempQty = Integer.parseInt(itemQty.getText().toString());
                    if(tempQty > 0)
                    {
                        tempQty = tempQty - 1;
                        itemQty.setText(String.valueOf(tempQty));
                    }
                    //get quantity
                    int fillQuantity = Integer.parseInt(itemQty.getText().toString());
                    //Create SaleItem
                    SaleItem tempSaleItem = new SaleItem(fillName, fillQuantity, fillPrice, fillIngArr);
                    //Update SaleItems Array
                    saleItems.set(position, tempSaleItem);
                    updateTotal();
                }
            });

            //Edit Text Listener
            itemQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    //get quantity
                    int fillQuantity = Integer.parseInt(itemQty.getText().toString());
                    //Create SaleItem
                    SaleItem tempSaleItem = new SaleItem(fillName, fillQuantity, fillPrice, fillIngArr);
                    //Update SaleItems Array
                    saleItems.set(position, tempSaleItem);
                    updateTotal();
                    return false;
                }
            });



            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Add 1 to number
                    int tempQty = Integer.parseInt(itemQty.getText().toString());
                    tempQty = tempQty + 1;
                    itemQty.setText(String.valueOf(tempQty));

                    //get quantity
                    int fillQuantity = Integer.parseInt(itemQty.getText().toString());
                    //Create SaleItem
                    SaleItem tempSaleItem = new SaleItem(fillName, fillQuantity, fillPrice, fillIngArr);
                    //Update SaleItems Array
                    saleItems.set(position, tempSaleItem);
                    updateTotal();
                }
            });
            return convertView;
        }
    }

    // return true if SaleItems are added
    private boolean saveSale()
    {
        boolean notEmpty = false;
        //Add SaleItems to sale
        for(int i = 0; i < saleItems.size(); i++)
        {
            if(saleItems.get(i) != null && saleItems.get(i).getQuantity() > 0)
            {
                newSale.addSaleItem(saleItems.get(i));
                notEmpty = true;
            }
        }
        if(notEmpty)
        {
            //Update InventorySingleton
            updateInventory(newSale);
            //Add sale to database
            db.collection("sales").add(newSale);
            //Add to sale singleton
            SaleSingleton.get(this).addSale(newSale);
        }
        return notEmpty;
    }

    private void updateInventory(Sale newSale)
    {
        //Loop through sale Items
        ArrayList<SaleItem> tempSaleItems = newSale.getSaleItems();
        for(int i = 0; i < tempSaleItems.size(); i++)
        {
            //Get saleItem quantity
            double saleNum = tempSaleItems.get(i).getQuantity();
            //Loop through ingredients
            ArrayList<Ingredient> tempIng = tempSaleItems.get(i).getIngredients();
            for(int j = 0; j < tempIng.size(); j++)
            {
                //get this ingredient name and quantity
                String thisName = tempIng.get(j).getName();
                double thisQty = tempIng.get(j).getQuantity();

                //get InventorySingleton qty
                double oldQty = InventorySingleton.get(this).getIngredientQuantity(thisName);
                double newQty = oldQty - (thisQty*saleNum);
                //Update Inventory Singleton and Database
                InventorySingleton.get(this).setIngredientQuantity(thisName, newQty);
                inventoryDBUpdate();
            }
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

    //Update TextView with total
    private void updateTotal()
    {
        if(saleItems != null)
        {
            double tempVal = 0;

            for(int i = 0; i < saleItems.size(); i++)
            {
                if(saleItems.get(i) != null)
                {
                    tempVal += saleItems.get(i).getPrice() * saleItems.get(i).getQuantity();
                }
            }
            totalText.setText(moneyFormat(String.valueOf(tempVal)));
        }
    }

    //Inventory Database Update
    private void inventoryDBUpdate(){
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
