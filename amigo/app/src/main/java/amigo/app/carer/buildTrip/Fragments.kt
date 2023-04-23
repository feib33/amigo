package amigo.app

import amigo.app.auth.User
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// Fragments for the BuildTrip ViewPager

// default values
val UNIMELB = LatLng(-37.798079, 144.959583)
val PLACE_PICKER_REQUEST = 3
val ZOOM = 16f

// Fragment for selecting starting location
class LocationFragment : Fragment() {

    lateinit var locationText: TextView
    lateinit var mMapView: MapView
    lateinit var googleMap: GoogleMap
    lateinit var addressText: String
    lateinit var locationPlace: Place
    lateinit var placeButton: FrameLayout
    lateinit var autocomplete_text: TextView
    lateinit var user: User
    lateinit var userPosition: LatLng


    // Inflate the view for the fragment based on layout XML
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.layout_location, container, false)

        val ref = FirebaseDatabase.getInstance().reference
        val users = ref.child("users")

        var pa = activity?.findViewById<ViewPager>(R.id.viewpager)?.adapter as BuildTripPagerAdapter
        val userName = pa.userName
        val userID = pa.userID

        placeButton = rootView.findViewById(R.id.placeButton)
        autocomplete_text = rootView.findViewById(R.id.place_autocomplete)
        locationText = rootView.findViewById(R.id.location_text)
        mMapView = rootView.findViewById(R.id.mapView) as MapView

        locationText.text = "Where will $userName be starting their journey?"

        placeButton.setOnClickListener {
            loadPlacePicker()
        }

        // initialise map
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()
        MapsInitializer.initialize(activity!!.getApplicationContext());
        mMapView.getMapAsync { mMap ->
            googleMap = mMap

            // if map intialised - get assisted person's last location as the default location
            users.child(userID).child("currentlocation").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}

                override fun onDataChange(snapshot: DataSnapshot?) {
                    val userLatitude = snapshot?.child("latitude")?.getValue(Double::class.java)
                            ?: UNIMELB.latitude
                    val userLongitude = snapshot?.child("longitude")?.getValue(Double::class.java)
                            ?: UNIMELB.longitude
                    userPosition = LatLng(userLatitude, userLongitude)
                    val cameraPosition = CameraPosition.Builder().target(userPosition).zoom(ZOOM).build()
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            })

        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    // Custom reader for Confirmation Activity
    // Returns selected address
    fun read(): CharSequence {
        return addressText
    }

    // Custom reader for Confirmation Activity
    // Returns selected address as LatLng
    fun readLatLng(): LatLng {
        return locationPlace.latLng
    }

    // acts like a static method in Java
    // returns a new instance when called
    companion object {
        fun newInstance(): LocationFragment {
            val fragment = LocationFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    // Called on return from Place Picker - adds marker to map at selected location
    // and updates the selected address in the view
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            val place = PlacePicker.getPlace(this.context, data)
            locationPlace = place
            addressText = place.address.toString()
            autocomplete_text.text = addressText
            placeMarkerOnMap(place.latLng)
        }

    }

    private fun placeMarkerOnMap(location: LatLng) {
        googleMap.clear()
        val markerOptions = MarkerOptions().position(location)
        googleMap.addMarker(markerOptions)
        val cameraPosition = CameraPosition.Builder().target(location).zoom(ZOOM).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    // Loads google placepicker with auto-complete
    private fun loadPlacePicker() {
        val builder = PlacePicker.IntentBuilder()

        // bounds builder sets location to assisted persons location as default
        val boundsBuilder = LatLngBounds.Builder()
        boundsBuilder.include(userPosition)

        val bounds = boundsBuilder.build()
        builder.setLatLngBounds(bounds)
        try {
            startActivityForResult(builder.build(this.activity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }
}

// Fragment for selecting destination
class DestinationFragment : Fragment() {

    lateinit var mMapView: MapView
    lateinit var googleMap: GoogleMap
    lateinit var addressText: String
    lateinit var destinationText: TextView
    lateinit var destinationPlace: Place
    lateinit var placeButton: FrameLayout
    lateinit var autocomplete_text: TextView
    lateinit var userPosition: LatLng

    val ref = FirebaseDatabase.getInstance().reference
    val users = ref.child("users")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.layout_destination, container, false)
        placeButton = rootView.findViewById(R.id.placeButton)
        autocomplete_text = rootView.findViewById(R.id.place_autocomplete)
        destinationText = rootView.findViewById(R.id.destination_text)

        var pa = activity?.findViewById<ViewPager>(R.id.viewpager)?.adapter as BuildTripPagerAdapter
        val userName = pa.userName
        val userID = pa.userID

        destinationText.text = "Where will $userName be going to on their journey?"

        placeButton.setOnClickListener {
            loadPlacePicker()
        }

        // initialise map
        mMapView = rootView.findViewById(R.id.mapView) as MapView
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()
        MapsInitializer.initialize(activity!!.getApplicationContext());
        mMapView.getMapAsync { mMap ->
            googleMap = mMap
            // if map intialised - get assisted person's last location as the default location
            users.child(userID).child("currentlocation").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {}

                override fun onDataChange(snapshot: DataSnapshot?) {
                    val userLatitude = snapshot?.child("latitude")?.getValue(Double::class.java)
                            ?: UNIMELB.latitude
                    val userLongitude = snapshot?.child("longitude")?.getValue(Double::class.java)
                            ?: UNIMELB.longitude
                    userPosition = LatLng(userLatitude, userLongitude)
                    val cameraPosition = CameraPosition.Builder().target(userPosition).zoom(ZOOM).build()
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            })
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    // Custom reader for Confirmation Activity
    // Returns selected address text
    fun read(): CharSequence {
        return addressText
    }

    // Custom reader for Confirmation Activity
    // Returns selected address as LatLng
    fun readLatLng(): LatLng {
        return destinationPlace.latLng
    }

    // acts like a static method in Java
    // returns a new instance when called
    companion object {
        fun newInstance(): DestinationFragment {
            val fragment = DestinationFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    // Called on return from Place Picker - adds marker to map at selected location
    // and updates the selected address in the view
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(this.context, data)
                destinationPlace = place
                addressText = place.address.toString()
                autocomplete_text.text = addressText
                placeMarkerOnMap(place.latLng)

            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        googleMap.clear()
        val markerOptions = MarkerOptions().position(location)
        googleMap.addMarker(markerOptions)
        val cameraPosition = CameraPosition.Builder().target(location).zoom(ZOOM).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    // Loads google placepicker with auto-complete
    private fun loadPlacePicker() {
        val builder = PlacePicker.IntentBuilder()

        // bounds builder sets location to assisted persons location as default
        val boundsBuilder = LatLngBounds.Builder()
        boundsBuilder.include(userPosition)

        val bounds = boundsBuilder.build()
        builder.setLatLngBounds(bounds)
        try {
            startActivityForResult(builder.build(this.activity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }
}

// Fragment for selecting transport preference
class TransportFragment : Fragment() {

    lateinit var carButton: ImageButton
    lateinit var busButton: ImageButton
    lateinit var walkButton: ImageButton
    lateinit var transportText: TextView
    lateinit var savedTransport: String

    // Inflate the view for the fragment based on layout XML
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var rootView = inflater.inflate(R.layout.layout_transport, container, false)

        carButton = rootView.findViewById(R.id.carButton)
        busButton = rootView.findViewById(R.id.busButton)
        walkButton = rootView.findViewById(R.id.walkButton)
        transportText = rootView.findViewById(R.id.transport_text)

        var pa = activity?.findViewById<ViewPager>(R.id.viewpager)?.adapter as BuildTripPagerAdapter
        val userName = pa.userName

        transportText.text = "What is the best way for $userName to travel?"

        // Depending on transport button clicked, set transport type to corresponding String
        // Strings match google directions api transport type so they
        // can be mapped straight into a HTTP call
        var buttons = hashMapOf(carButton to "Driving", busButton to "Transit", walkButton to "Walking")
        carButton.setOnClickListener {
            clearButtonSelections()
            carButton.setImageResource(R.drawable.cargreen)
            savedTransport = buttons[carButton]!!
        }
        busButton.setOnClickListener {
            clearButtonSelections()
            busButton.setImageResource(R.drawable.busgreen)
            savedTransport = buttons[busButton]!!
        }
        walkButton.setOnClickListener {
            clearButtonSelections()
            walkButton.setImageResource(R.drawable.walkgreen)
            savedTransport = buttons[walkButton]!!
        }

        return rootView

    }

    // Custom reader for Confirmation Activity
    // Returns selected transport text
    fun read(): CharSequence {
        return savedTransport
    }

    private fun clearButtonSelections() {
        carButton.setImageResource(R.drawable.car)
        busButton.setImageResource(R.drawable.bus)
        walkButton.setImageResource(R.drawable.walk)
    }

    // acts like a static method in Java
    // returns a new instance when called
    companion object {
        fun newInstance(): TransportFragment {
            val fragment = TransportFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}