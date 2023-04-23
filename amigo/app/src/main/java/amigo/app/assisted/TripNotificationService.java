package amigo.app.assisted;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//This is for receiving notification on trips ready.
public class TripNotificationService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        listentoTrip();

        return Service.START_NOT_STICKY;
    }


    public void listentoTrip() {

        FirebaseDatabase fb = FirebaseDatabase.getInstance();

        DatabaseReference def = fb.getReference();

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference trips = def.child("trips").child(userid);

        trips.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                MyNotificiationManager.getInstance(getApplicationContext()).TripReady("hello", "there is a trip ready");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
