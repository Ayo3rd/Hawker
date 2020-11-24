package itp341.otegbade.opeoluwa.myfinal.project.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import itp341.otegbade.opeoluwa.myfinal.project.app.model.Ingredient;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.InventorySingleton;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuItem;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.MenuSingleton;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.Sale;
import itp341.otegbade.opeoluwa.myfinal.project.app.model.SaleSingleton;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

   private Spinner showSpinner;
   private EditText yearText;
   private Button showButton;

   private ListView listViewMain;

    private Button inventoryButton;
    private Button menuButton;
    private Button addButton;

    //Get default Month and Year
    private Date date = new Date();// Current date
    private LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    private int month;//
    private int year;//

    //Get Number of days in month
    private YearMonth yearMonthObject;


    //Array of sales
    private ArrayList<Sale> salesMain = new ArrayList<>();

    //Preference constants
    public static final String PREF_MY_DATE = "month_year_date";
    public static final String PREF_MONTH = "month";
    public static final String PREF_YEAR = "year";


    //Request Codes
    private static final int REQUEST_CODE_MENU = 100;
    private static final int REQUEST_CODE_INVENTORY = 200;
    private static final int REQUEST_CODE_MAKE_SALE = 1200;
    private static final int REQUEST_CODE_SEE_DAY = 2200;

    //Initialize Database
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //List Adapter
    private SaleListAdapter saleListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize views
        showSpinner= findViewById(R.id.showSpinner);
        yearText= findViewById(R.id.yearText);
        showButton= findViewById(R.id.showButton);

        listViewMain = findViewById(R.id.listViewMain);

        inventoryButton = findViewById(R.id.inventoryButton);
        menuButton = findViewById(R.id.menuButton);
        addButton = findViewById(R.id.addButton);

        //Load inventory
        loadInventory();
        //Load Menu
        loadMenu();

        //Month Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.months , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showSpinner.setAdapter(adapter);
        //Spinner listener to update Month
        showSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                month = pos+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });


        //Initialize Adapter and listView
        saleListAdapter = new SaleListAdapter(this, new ArrayList<Sale>());
        listViewMain.setAdapter(saleListAdapter);

        //Load date based on shared preferences
        loadDate();

        //startup adapter
        updateSales();

        //Show Button
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Set Year if not empty
                if(yearText.getText().toString().length() > 0)
                {
                    year = Integer.parseInt(yearText.getText().toString());
                }
                //Update sales array list
                updateSales();
                //Save Date as preference
                saveDate();
                //Hide keyboard
                hideKeyboard((Button)view);
                //Clear Year
                yearText.setText("");
            }
        });


        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_INVENTORY);
            }
        });


        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MENU);
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MakeSaleActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MAKE_SALE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateSales();
    }

    //load Inventory from database
    private void loadInventory()
    {
        db.collection("ingredients")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<Ingredient> ingredients = new ArrayList<>();

                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc: task.getResult())
                            {
                                Ingredient ingredient = doc.toObject(Ingredient.class);
                                ingredients.add(ingredient);
                            }
                            //update Singleton
                            InventorySingleton.get(MainActivity.this).setAllIngredients(ingredients);
                        }
                    }
                });
    }

    //Load Menu from database
    private void loadMenu()
    {
        db.collection("menuItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<MenuItem> menuItemsMain = new ArrayList<>();

                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot doc: task.getResult())
                            {
                                MenuItem menuItem = doc.toObject(MenuItem.class);
                                menuItemsMain.add(menuItem);
                            }
                            //update Singleton
                            MenuSingleton.get(MainActivity.this).setAllMenuItems(menuItemsMain);
                        }
                    }
                });
    }

    //Load Date
    private void loadDate()
    {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_MY_DATE, MODE_PRIVATE);
        final int month = sharedPreferences.getInt(PREF_MONTH,localDate.getMonthValue());
        final int year = sharedPreferences.getInt(PREF_YEAR, localDate.getYear());
        //Set Views
        showSpinner.setSelection(month-1);
        yearText.setText(String.valueOf(year));
    }

    //Save Date
    private void saveDate()
    {
        final SharedPreferences sharedPreferences = getSharedPreferences(PREF_MY_DATE, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_MONTH, month);
        editor.putInt(PREF_YEAR,year);
        editor.commit();
    }


    private void nullifyArray()
    {
        //get current number of days in the month
        yearMonthObject = YearMonth.of(year, month);
        int newSize = yearMonthObject.lengthOfMonth();
        salesMain.clear();
        for(int i = 0; i < newSize; i++)
        {
            salesMain.add(null);
        }
    }


    private void updateSales()
    {
        //Get sales from database
        db.collection("sales")
                .whereEqualTo("dateMonthNum", String.valueOf(month))
                .whereEqualTo("dateYearNum", String.valueOf(year)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        nullifyArray();

                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                Sale sale = doc.toObject(Sale.class);
                                //Set Sale to sale main
                                salesMain.set((Integer.parseInt(sale.getDateDayNum())-1),sale);
                            }
                            saleListAdapter.clear();
                            saleListAdapter.addAll(salesMain);
                            saleListAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }


    //Hide keyboard
    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch(Exception ignored) {
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //month = i+1;
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    //Custom adapter for Sales
    private class SaleListAdapter extends ArrayAdapter<Sale> {
        private ArrayList<Sale> sales;

        public SaleListAdapter(Context context, ArrayList<Sale> sales){
            super(context, R.layout.layout_list_month_grid, sales );
            this.sales = sales;

        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Check if convertView exists
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_month_grid, parent, false);
            }

            //Load calendar
            //Calendar
            String[]  calendar = { "0", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};

            //Sale
            final Sale sale = sales.get(position);

            //Initialize convertViews
            final TextView box = convertView.findViewById(R.id.box1);
            final Button seeButton = convertView.findViewById(R.id.seeButton);

            //Set Text
            box.setText(calendar[month] + " " + String.valueOf(position+1));

            //if Sales where made in that day
            if(sale != null)
            {
                //change color
                box.setBackground(getResources().getDrawable(R.drawable.rounded_grid_green));
                //Set Button listener
                seeButton.setTag(position);
                seeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Create string
                        String theDay = sale.getDateShow();
                        //String to pass with intent
                        Intent intent = new Intent(MainActivity.this, DaySalesActivity.class);
                        intent.putExtra(DaySalesActivity.EXTRA_DATE, theDay);
                        startActivityForResult(intent, REQUEST_CODE_SEE_DAY);
                    }
                });
            }
            else
            {
                box.setBackground(getResources().getDrawable(R.drawable.rounded_grid_format));
                //Set Button listener
                seeButton.setTag(position);
                seeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.no_sales), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return convertView;
        }
    }

}



