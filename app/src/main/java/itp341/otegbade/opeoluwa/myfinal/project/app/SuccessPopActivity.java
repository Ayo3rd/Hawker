package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SuccessPopActivity extends AppCompatActivity {

    public static final String EXTRA_SALE = SuccessPopActivity.class.getPackage().getName() + "sale";
    private double totalPrice = -1;
    private TextView successTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_success_pop);

        //Initialize View
        successTextView = findViewById(R.id.successTextView);
        String finalText = "";

        //Get Intent, Default = -1 if empty
        Intent intent = getIntent();
        if(intent != null){
            totalPrice = intent.getDoubleExtra(EXTRA_SALE, -1);

            //If position was passed
            if(totalPrice > -1){
                //Set Text for the view
                successTextView.setText(this.getResources().getString(R.string.tran_added) + moneyFormat(String.valueOf(totalPrice)));
            }
        }
        //Resize window
        resize();
    }

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

    public void resize()
    {
        //Create resized window for activity
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //Get dimensions
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.6));
    }
}
