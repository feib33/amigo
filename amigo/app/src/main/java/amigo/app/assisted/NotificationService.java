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
 * This class is designed for initializing chat notification,
 * and at what circumstances the notification would be created.
 */
public class NotificationService extends Service {


    //This service is only used for push notification
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        listentoChats();


        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    /**
     * When there is a change in our firebase chatlog, create a new message notification.
     */
    public void listentoChats() {

        FirebaseDatabase fb = FirebaseDatabase.getInstance();

        DatabaseReference def = fb.getReference();

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference chats = def.child("userchats").child(Listener.Companion.getListenertoken()).child(userid);

        chats.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("onChildAdded", "Child " + dataSnapshot.getKey() + " was added after " + s);


                String messagebody = dataSnapshot.child("message").getValue(String.class);


                MyNotificiationManager.getInstance(getApplicationContext()).displayNotification(Listener.Companion.getListenerName(), messagebody);

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


}
