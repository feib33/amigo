package amigo.app.auth

import amigo.app.CarerContactsActivity
import amigo.app.R
import amigo.app.assisted.MainActivity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_auth_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var ref: DatabaseReference
    lateinit var user: DatabaseReference
    lateinit var listener: ValueEventListener
    lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setTitle("Sign up")
        setContentView(R.layout.activity_auth_register)

        // check if phone has active user log in session
        verifyUserSession()

        // go to login if selected
        textView_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // perform register if register button pressed
        button_createacc.setOnClickListener {
            performRegister()
        }

        // display information text of what a carer is
        textView_whatcarer.setOnClickListener {
            Toast.makeText(this, "A carer uses AmiGo to help others in need of assistance to navigate in their daily life", Toast.LENGTH_LONG).show()
        }
    }

    fun performRegister() {
        // get user detail from input
        var firstname = editText_firstname.text.toString()
        var lastname = editText_lastname.text.toString()
        var email = editText_email.text.toString()
        var password = editText_password.text.toString()
        var iscarer = checkBox_carer.isChecked

        // check if has all information
        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter firstname, lastname, email and password", Toast.LENGTH_LONG).show()
            return
        }

        // create a user with given information
        createUser(firstname, lastname, email, password, iscarer)
    }

    private fun createUser(firstname: String, lastname: String, email: String, password: String, iscarer: Boolean) {
        // create a user on firebase with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // display success
                    Toast.makeText(this, "Register successful", Toast.LENGTH_SHORT).show()
                    // save user profile info on firebase database
                    saveUserToFirebaseDatabase(firstname, lastname, iscarer)
                }
                // display information if fail to create user
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_LONG).show()
                }

    }

    private fun saveUserToFirebaseDatabase(firstname: String, lastname: String, iscarer: Boolean) {
        uid = FirebaseAuth.getInstance().uid ?: ""
        ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        // create a user object locally
        val user = User(uid, firstname, lastname, iscarer)

        // and upload to firebase database with given user unique id
        ref.setValue(user)
                .addOnSuccessListener {
                    // and once succesfully added to firebase
                    enterHomeScreen(iscarer)
                }
    }

    private fun enterHomeScreen(iscarer: Boolean) {
        val intent: Intent
        // check if user is a carer -> carer home page
        if (iscarer) {
            intent = Intent(this, CarerContactsActivity::class.java)
        }
        // if user is assisted person -> assisted person home page (main activity)
        else {
            intent = Intent(this, MainActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun verifyUserSession() {
        // if no user session, continue to register
        if (FirebaseAuth.getInstance().uid == null) {
            return
        }
        // if has user session, check if user is carer and enter home screen accordingly
        else {
            var uid = FirebaseAuth.getInstance().uid
            ref = FirebaseDatabase.getInstance().getReference()
            user = ref.child("users").child(uid)

            if (user != null) {
                user.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0 != null) {
                            var iscarer = p0.child("iscarer").getValue(Boolean::class.java)!!
                            enterHomeScreen(iscarer)
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {

                    }
                })
            }

        }
    }
}
