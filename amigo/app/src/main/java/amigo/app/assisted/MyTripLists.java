package amigo.app.assisted;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import amigo.app.R;


/**
 * This class is written by @Fei Bao and main purpose is to list all trips of assisted person and
 * make list clickable
 */
public class MyTripLists extends AppCompatActivity {


    private ListView mapLists;
    private ArrayList<String> endAddresses = new ArrayList<>();
    private ArrayList<String> route = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public static Intent newMyTripLists(Context context) {
        return new Intent(context, MyTripLists.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_lists);
        // fetch all trips from firebase
        fetchTrips();

    }

    /**
     * This method will fetch all existent trip plans from current user
     */
    private void fetchTrips() {
        // get current user's userID and fetch this user's trips
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String userID = FirebaseAuth.getInstance().getUid();
        if (userID != null) {
            DatabaseReference trips = ref.child("trips").child(userID);
            trips.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // add destinations of each trip to endAddresses list
                    for (DataSnapshot trip : dataSnapshot.getChildren()) {
                        DataSnapshot end_p = trip.child("routes").child("0").child("legs")
                                .child("0").child("end_address");

                        // Convert routeID and end address to string
                        String routeID = trip.getKey();
                        String end_point = end_p.getValue(String.class);
                        if (end_point != null && routeID != null) {
                            endAddresses.add(end_point);
                            route.add(routeID);
                        }
                    }
                    listTrips();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Log.d("fetchTrip", "current userID is null");
        }
    }

    /**
     * add each destination to list and make them clickable, start corresponding activity
     */
    private void listTrips() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, endAddresses);
        mapLists = findViewById(R.id.trip_lists);
        mapLists.setAdapter(adapter);
        mapLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent clickMap = new Intent(MyTripLists.this, MapsActivity.class);
                // pass current route id and use this route id to find corresponding route
                clickMap.putExtra("routeId", route.get(position));
                startActivity(clickMap);
            }
        });
    }
}
