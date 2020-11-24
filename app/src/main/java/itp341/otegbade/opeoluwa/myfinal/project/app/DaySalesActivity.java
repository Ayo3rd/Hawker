package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

import itp341.otegbade.opeoluwa.myfinal.project.app.model.Sale;

public class DaySalesActivity extends AppCompatActivity {


    public static final String EXTRA_DATE = DaySalesActivity.class.getPackage().getName() + "date";

    private String thisDate = "";

    private TextView totalText;

    private ListView listViewMain;

    private ArrayList<Sale> salesMain = new ArrayList<>(); //List of sales

    //Initialize Database
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    //List Adapter
    private SaleListAdapter saleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_sale);


        //Initialize Views
        totalText = findViewById(R.id.totalText);
        listViewMain = findViewById(R.id.listViewMain);

        //Initialize Adapter and listView
        saleListAdapter = new SaleListAdapter(this, new ArrayList<Sale>());
        listViewMain.setAdapter(saleListAdapter);


        //Get Intent, Default "" if empty
        Intent intent = getIntent();
        if(intent != null){
            thisDate = intent.getStringExtra(EXTRA_DATE);

            //If date was passed
            if(thisDate.length() > 0){
                //Get sales from database
                db.collection("sales")
                        .whereEqualTo("dateShow", thisDate).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                salesMain = new ArrayList<>();

                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot doc: task.getResult()){
                                        Sale sale = doc.toObject(Sale.class);
                                        //Set Sale to sale main
                                        salesMain.add(sale);
                                        Log.d("checker", "Here");
                                    }
                                    saleListAdapter.clear();
                                    saleListAdapter.addAll(salesMain);
                                    saleListAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
        }

        //Set Title
        setTitle(thisDate);
    }


    //Custom adapter for Sales
    private class SaleListAdapter extends ArrayAdapter<Sale> {
        private ArrayList<Sale> sales;

        public SaleListAdapter(Context context, ArrayList<Sale> sales){
            super(context, R.layout.layout_list_text_price, sales );
            this.sales = sales;

        }
        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //Check if convertView exists
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_text_price, parent, false);
            }

            //Sale
            final Sale sale = sales.get(position);

            //Initialize convertViews
            final TextView itemName = convertView.findViewById(R.id.itemName);
            final TextView itemPrice = convertView.findViewById(R.id.itemPrice);


            //Set Text
            if(sale != null)
            {
                itemName.setText(sale.showSale());
                itemPrice.setText(moneyFormat(String.valueOf(sale.getTotalPrice())));
            }

            //Update Total
            updateTotal();

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

    //Update TextView with total
    private void updateTotal()
    {
        if(salesMain != null)
        {
            double tempVal = 0;

            for(int i = 0; i < salesMain.size(); i++)
            {
                if(salesMain.get(i) != null)
                {
                    tempVal += salesMain.get(i).getTotalPrice();
                }
            }
            totalText.setText("Total: " + moneyFormat(String.valueOf(tempVal)));
        }
    }

}
