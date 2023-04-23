package amigo.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

fun Context.newBuildTripIntent(): Intent {
    return Intent(this, BuildTripActivity::class.java)
}

class BuildTripActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var sp: SharedPreferences
    lateinit var userName: String
    lateinit var userID: String
    lateinit var thisUserID: String

    internal var fb = FirebaseDatabase.getInstance()
    internal var def = fb.reference
    internal var users = def.child("users")

    // onStart onResume onPause and onStop contain update methods for a variable
    // tripstatus in Shared Preferences which is used for handling which trip
    // attribute to edit when returning to the activity from the trip confirmation
    // page. Corresponding methods are found in ConfirmationActivity lines 96-115
    override fun onStart() {
        val data = sp.getInt("page", 0)
        viewPager.currentItem = data
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        // update user status to 'building trip' in db
        users.child(thisUserID).child("tripstatus").setValue(true)
    }

    override fun onPause() {
        val editor = sp.edit()
        editor.putInt("page", viewPager.currentItem)
        editor.commit()
        super.onPause()
    }

    override fun onStop() {
        val editor = sp.edit()
        editor.putInt("page", 0)
        editor.commit()
        // revert user status to 'Idle' in db
        users.child(thisUserID).child("tripstatus").setValue(false)
        super.onStop()
    }

    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v("action: ", "onCreate")
        super.onCreate(savedInstanceState)
        this.title = "Building a trip"
        setContentView(R.layout.activity_buildtrip)

        userName = intent.getStringExtra("userfirstname")
        userID = intent.getStringExtra("uid")
        sp = getSharedPreferences("Pages", 0)
        val editor = sp.edit()
        editor.putInt("page", 0)
        editor.commit()

        thisUserID = FirebaseAuth.getInstance().uid ?: ""

        // Set up with custom View Pager with fragments for each page
        viewPager = findViewById(R.id.viewpager)
        viewPager.adapter = BuildTripPagerAdapter(supportFragmentManager, userName, userID)
        val next = findViewById(R.id.next_button) as Button
        viewPager.currentItem = 0

        next.setOnClickListener {
            viewPager.nextPage()
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(position: Int) {
                when {
                    // show next button until final page
                    position != PagesModel.values().size - 1 -> next.text = "Next"
                    // show review button on final page
                    else -> next.text = "Review"
                }
            }
        });
    }

    private fun ViewPager.nextPage() {
        when {
            // iterate through pages until final page
            this.currentItem < (PagesModel.values().size - 1) -> this.currentItem = currentItem + 1
            // on final page, post values to confirmation Activity for review
            else -> {
                // get references to all fragments in the view pager which contain the trip details
                var viewPager = this.adapter as BuildTripPagerAdapter
                var locationFragment = viewPager.getItem(0) as LocationFragment
                var destinationFragment = viewPager.getItem(1) as DestinationFragment
                var transportFragment = viewPager.getItem(2) as TransportFragment

                var confirmationIntent = newConfirmationIntent()

                // put trip details from reach fragment into new activity intent with custom
                // read functions.
                // Values retrieved in Confirmation Activity
                confirmationIntent.putExtra("uid", userID)
                confirmationIntent.putExtra("location", locationFragment.read())
                confirmationIntent.putExtra("destination", destinationFragment.read())
                confirmationIntent.putExtra("transport", transportFragment.read())
                confirmationIntent.putExtra("locationLatLng", locationFragment.readLatLng())
                confirmationIntent.putExtra("destinationLatLng", destinationFragment.readLatLng())

                startActivity(confirmationIntent)
            }
        }
    }
}



