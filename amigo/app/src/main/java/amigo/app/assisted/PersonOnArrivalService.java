package amigo.app.assisted;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class is designed for creating notification when the assisted person has arrived at
 * his destination.
 */
public class PersonOnArrivalService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        listenToStatus();


        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    /**
     * We use firebase listener to keep track current assisted person's tripstatus, if the trip status
     * is changing, we push notification based on situation.
     */
    public void listenToStatus() {
        FirebaseDatabase fb = FirebaseDatabase.getInstance();

        DatabaseReference def = fb.getReference();

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference status = def.child("users").child(Listener.Companion.getListenertoken()).child("tripstatus");

        status.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.getValue(boolean.class) == true) {
                    Log.d("user is ", "inthetrip");
                    MyNotificiationManager.getInstance(getApplicationContext()).personArrived("our user", "isintrip");
                } else {
                    Log.d("User is", "compeletetrip");
                    MyNotificiationManager.getInstance(getApplicationContext()).personArrived("theuser", "completetrip");
                }

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
}
