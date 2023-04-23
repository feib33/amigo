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

public class VideoNotification extends Service {


    //This service is only used for push notification
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        listentoVideoRequest();


        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }


    public void listentoVideoRequest() {

        FirebaseDatabase fb = FirebaseDatabase.getInstance();

        DatabaseReference def = fb.getReference();

        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference chats = def.child("videochat").child(userid);

        chats.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("onChildAdded", "Child " + dataSnapshot.getKey() + " was added after " + s);

                MyNotificiationManager.getInstance(getApplicationContext()).videoRequest("Hi", "Let's videochat");

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