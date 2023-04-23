package amigo.app.assisted;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import amigo.app.R;
import amigo.app.auth.RegisterActivity;

/**
 * This is the main page for assisted person.
 */
public class MainActivity extends AppCompatActivity {

    FirebaseDatabase fb = FirebaseDatabase.getInstance();
    DatabaseReference def = fb.getReference();
    DatabaseReference users = def.child("users");
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onStart() {
        super.onStart();
        String uid = FirebaseAuth.getInstance().getUid();
        users.child(uid).child("onlinestatus").setValue(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String uid = FirebaseAuth.getInstance().getUid();
        users.child(uid).child("onlinestatus").setValue(true);

        //initialize notification setting
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel =
                    new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);


            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);

            mNotificationManager.createNotificationChannel(mChannel);
        }

        //Start listen to video request
        startVideoNotificationService();
        //start listen to trip activity.
        Intent i = new Intent(MainActivity.this, TripNotificationService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        MainActivity.this.startService(i);

        Button button_plan = (Button) findViewById(R.id.button_plan);
        Button myTrips = findViewById(R.id.button11);

        button_plan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserContactActivity.class);
                startActivity(intent);
            }
        });
        myTrips.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyTripLists.class));
            }
        });

        getDeviceLocation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.so_signout) {
            String uid = FirebaseAuth.getInstance().getUid();
            users.child(uid).child("onlinestatus").setValue(false);
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return true;
    }

    //The video chat notification service will be initialized as the app is open
    public void startVideoNotificationService() {
        Intent j = new Intent(MainActivity.this, VideoNotification.class);
        // potentially add data to the intent
        j.putExtra("KEY1", "Value to be used by the service");
        MainActivity.this.startService(j);
    }


    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        String uid = FirebaseAuth.getInstance().getUid();
                        if (uid != null) {
                            users.child(uid).child("currentlocation").child("latitude").setValue(currentLocation.getLatitude());
                            users.child(uid).child("currentlocation").child("longitude").setValue(currentLocation.getLongitude());
                            users.child(uid).child("currentlocation").child("time").setValue(System.currentTimeMillis());
                        }

                    } else {
                    }
                }
            });
        } catch (SecurityException e) {
        }
    }
}
