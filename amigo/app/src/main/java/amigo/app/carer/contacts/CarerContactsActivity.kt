package amigo.app

import amigo.app.auth.RegisterActivity
import amigo.app.auth.User
import amigo.app.carer.ExpandableListAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ExpandableListView
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun Context.newCarerContactsIntent(): Intent {
    return Intent(this, CarerContactsActivity::class.java)
}

class CarerContactsActivity : AppCompatActivity() {

    val ref = FirebaseDatabase.getInstance().reference
    val users = ref.child("users")

    // list of contacts to populate listView
    var people = arrayListOf<User>()
    private lateinit var listView: ListView

    override fun onStart() {
        super.onStart()
        val uid = FirebaseAuth.getInstance().uid
        users.child(uid!!).child("onlinestatus").setValue(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = "Assisted persons"
        setContentView(R.layout.activity_carer_contacts)

        listView = findViewById(R.id.list_view) as ExpandableListView
        val adapter = ExpandableListAdapter(this, people)
        (listView as ExpandableListView).setAdapter(adapter)

        // add listener to users table in db
        users.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snapshot: DataSnapshot?) {

                // update list of assisted persons
                people.clear()
                adapter.notifyDataSetChanged()
                snapshot?.children?.forEach {
                    val user = it.getValue(User::class.java) ?: User()
                    if (!user.iscarer) {
                        people.add(user)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.signout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.so_signout) {
            val uid = FirebaseAuth.getInstance().uid
            users.child(uid).child("onlinestatus").setValue(false)
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onDestroy() {
        super.onDestroy()
    }
}


