package com.example.pokemon

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pokemon.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermisions()
    }

    val AccesLocation=123
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermisions(){
        if(Build.VERSION.SDK_INT <=23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), AccesLocation)
                return
            }
        }
        getUserLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            AccesLocation->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else{
                    Toast.makeText(this, "Location access is deny", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun getUserLocation(){
        Toast.makeText(this, "Location access now", Toast.LENGTH_LONG).show()
        //TODO:access user location
        val myLocation = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f, myLocation)
        val myThread = MyThread()
        myThread.start()

    }
//    fun checkPermisions(){}


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(31.630000, -8.008889)
        mMap.addMarker(MarkerOptions().position(sydney).title("Me").snippet("here is my location").icon(
            BitmapDescriptorFactory.fromResource(R.drawable.mario03)
        ))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))
    }

    var mylocation:Location?=null
     inner class MyLocationListener:LocationListener{
         constructor(){
             mylocation= Location("me")
             mylocation !!.longitude=0.0
             mylocation !!.latitude=0.0

         }
         override fun onLocationChanged(p0: Location) {
             mylocation = p0
         }
     }

    inner class MyThread:Thread{
        constructor():super(){
            //TODO: se old location
        }
        override fun run(){
            while (true){
                try {
                runOnUiThread { // Add a marker in Sydney and move the camera
                    mMap!!.clear()
                    val sydney = LatLng(mylocation!!.latitude, mylocation!!.longitude)
                    mMap!!.addMarker(MarkerOptions().position(sydney).title("Me").snippet("here is my location").icon(
                        BitmapDescriptorFactory.fromResource(R.drawable.mario03)
                    ))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f)) }

                    //show pokemons
                    for (i in 0.. listOfPokemon.size-1){
                        var newPokemon=listOfPokemon[i]

                        if (newPokemon.isCatch==false){
                            val sydney = LatLng(newPokemon.location!!.latitude,newPokemon.location!!.longitude)
                            mMap.addMarker(MarkerOptions()
                                .position(sydney)
                                .snippet(newPokemon.des)
                                .title(newPokemon.name).icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))

                            if (mylocation!!.distanceTo(newPokemon.location!!)<2) {
                                myPower = myPower + newPokemon.power!!
                                newPokemon.isCatch = true
                                listOfPokemon[i] = newPokemon
                                Toast.makeText(
                                    applicationContext,
                                    "Congrats! you catch new pokemon",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    Thread.sleep(1000)
                }catch (ex:Exception){}
            }
        }
    }
    var myPower:Double=0.0


    var listOfPokemon=ArrayList<Pockemon>()
    fun loadPokemon(){
        listOfPokemon.add(Pockemon(R.drawable.bulbasaur,"Pokemon 1","bulbasaur In ISGI Marrakech ",55.0,31.622007,-8.046868))
        listOfPokemon.add(Pockemon(R.drawable.charmander,"Pokemon 2","charmander living In Marjane massira",95.0, 31.629326,  -8.078316))
        listOfPokemon.add(Pockemon(R.drawable.squirtel,"Pokemon 3","squirtel living In Train station",33.0,31.630462,-8.017100))
    }
}