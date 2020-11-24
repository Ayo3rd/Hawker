package itp341.otegbade.opeoluwa.myfinal.project.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocPopActivity extends AppCompatActivity {


    private Button yesButton;

    private Button noButton;

    private Location currLoc;

    public static  final String LOC_QUERY = "https://www.google.com/maps/search/?api=1&query=";

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_loc_pop);


        //Initialize Button Views
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currLoc = location;
                        }
                    }
                });

        //On yes option
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create message to tweet
                String message = LOC_QUERY+ currLoc.getLatitude() + "," +  currLoc.getLongitude();
                //Initialize Intent
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                //Start Intent and send message(Link to current location)
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                finish();
            }
        });

        //On no option
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //resize window
        resize();
    }

    public void resize()
    {
        //Create resized window for activity
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //Get dimensions
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getWindow().setLayout((int)(width*0.7), (int)(height*0.4));
    }

}
