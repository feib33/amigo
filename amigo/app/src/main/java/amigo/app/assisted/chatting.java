package amigo.app.assisted;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import amigo.app.BuildTripActivity;
import amigo.app.R;


/**
 * This is the main part of chatting page, including all the functionality and data retrieval
 *
 * @author Huiyuan Meng
 * The basic approach is constantly retreive data from the database and notify our adapter to
 * achieve the chat functionality.
 * This activity use recylerView for chat layout implementation.
 */
public class chatting extends AppCompatActivity {

    //a declaration of whether the current user is a carer, to modify page content.
    private static boolean iscarer;
    //initialize of firebase
    FirebaseDatabase fb = FirebaseDatabase.getInstance();
    DatabaseReference def = fb.getReference();
    DatabaseReference user = def.child("users");
    //message list to hold all message.
    private ArrayList<MessageBody> msgList = new ArrayList<MessageBody>();
    private MessageAdapter adapter;

    //Current user's uid.
    private String usertoken = FirebaseAuth.getInstance().getUid();

    public static Intent createIntent(Context context) {

        return new Intent(context, chatting.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setTitle(Listener.Companion.getListenerName());


        //initialize the chat notification service.
        Intent i = new Intent(chatting.this, NotificationService.class);
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service");
        chatting.this.startService(i);

        //initialize the personOnArrival notification service.
        Intent j = new Intent(chatting.this, PersonOnArrivalService.class);
        j.putExtra("key1", "whatever");
        chatting.this.startService(j);

        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("/users").child(uid);

        //determine whether the cuurent user is carer.
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Boolean db_iscarer = dataSnapshot.child("iscarer").getValue(Boolean.class);
                    if (db_iscarer != null) {
                        chatting.iscarer = db_iscarer;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        /**
         * To use the interface method readData to actually store the data from Firebase.
         */
        readData(new MyCallback() {

            @Override
            public void onCallback(ArrayList<MessageBody> messages) {

                msgList.clear();
                msgList.addAll(messages);

                /**
                 * Sort the message list to give realtime chat feeling.
                 */
                Collections.sort(msgList, new Comparator<MessageBody>() {
                    public int compare(MessageBody o1, MessageBody o2) {
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }
                });


            }
        });
        setContentView(R.layout.activity_chatting);

        Log.d("msg list", msgList.toString());


        //set components
        final EditText inputText = (EditText) findViewById(R.id.input);
        Button sendBtn = (Button) findViewById(R.id.send);

        final RecyclerView msgRecyclerView = (RecyclerView) findViewById(R.id.msg);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);

        //sent button use.
        sendBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if ("".equals(content))
                    return;

                //send the message to database.
                def.child("userchats").child(usertoken).child(Listener.Companion.getListenertoken()).child(new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())).setValue(new MessageBody(content, MessageBody.TYPE.Sent, new Date()));
                msgList.add(new MessageBody(content, MessageBody.TYPE.Sent, new Date()));


                //if there is a new message, decrement msglist size and redirect to last line.
                int newSize = msgList.size() - 1;

                adapter.notifyItemInserted(newSize);

                msgRecyclerView.scrollToPosition(newSize);
                adapter.notifyDataSetChanged();

                //reset the input text part.
                inputText.setText("");


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (this.iscarer) {
            inflater.inflate(R.menu.buildtrip, menu);
        } else {
            inflater.inflate(R.menu.mytrip, menu);
        }
        return true;
    }

    /**
     * This method is for handling different user could do differnt thing on chatting page,
     * for instance an assisted person could view his trip and a carer could build trip for AP.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_buildtrip:
                Intent newIntent = new Intent(chatting.this, BuildTripActivity.class);
                String uid = Listener.Companion.getListenertoken();
                String name = Listener.Companion.getListenerName();
                newIntent.putExtra("userfirstname", name);
                newIntent.putExtra("uid", uid);
                startActivity(newIntent);
                return true;
            case R.id.mt_mytrip:
                startActivity(new Intent(chatting.this, MyTripLists.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Main method to retrieve data from firebase.
     *
     * @param myCallback
     */
    public void readData(final MyCallback myCallback) {


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        myRef.keepSynced(true);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Log.d("the uid of app", Listener.Companion.getListenertoken());
                ArrayList<MessageBody> messages = new ArrayList<MessageBody>();


                //retrieve all chats sent by user.
                for (DataSnapshot child : dataSnapshot.child("userchats").child(usertoken).getChildren()) {

                    if (child.getKey().equals(Listener.Companion.getListenertoken())) {
                        for (DataSnapshot user1chats : child.getChildren()) {

                            messages.add(new MessageBody(user1chats.child("message").getValue(String.class), MessageBody.TYPE.Sent, user1chats.child("timestamp").getValue(Date.class)));

                        }

                    }
                }
                //retrieve all chats sent by the listener of the chat.
                for (DataSnapshot child : dataSnapshot.child("userchats").child(Listener.Companion.getListenertoken()).getChildren()) {
                    if (child.getKey().equals(usertoken)) {
                        for (DataSnapshot user1chats : child.getChildren()) {

                            messages.add(new MessageBody(user1chats.child("message").getValue(String.class), MessageBody.TYPE.Received, user1chats.child("timestamp").getValue(Date.class)));

                        }

                    }
                }


                //store values and
                myCallback.onCallback(messages);

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Wooops", "Failed to read value.", error.toException());
            }
        });
    }


}
