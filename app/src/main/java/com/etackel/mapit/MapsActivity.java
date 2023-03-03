package com.etackel.mapit;



import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.etackel.mapit.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public FloatingActionButton new_message,saved,info;
    private String m_Text = "  ";
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    public TextView your_address;
    String voice2text;
    public EditText message;
    public String message_string ;
    TextView okay_text, cancel_text;
    public ImageView profile;
    public LatLng latLng;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        com.etackel.mapit.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapsActivity.this);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);























        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            getLocation();
        } catch (IOException e) {
            e.printStackTrace();
        }


        profile = findViewById(R.id.profile);
        your_address = findViewById(R.id.address);
        Dialog dialog = new Dialog(MapsActivity.this);
        new_message = findViewById(R.id.floatingActionButton);
        saved = findViewById(R.id.saved);
        info = findViewById(R.id.info);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCurrentLocation(latLng,mMap);
            }
        });


        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_saved);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.show();
            }
        });


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.dialog_info);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.show();
            }
        });



        final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                //your code here
            }
        };
        new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.setContentView(R.layout.dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                //dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                okay_text = dialog.findViewById(R.id.okay_text);
                cancel_text = dialog.findViewById(R.id.cancel_text);
                your_address = dialog.findViewById(R.id.address);
                message = dialog.findViewById(R.id.message);
                EditText title = dialog.findViewById(R.id.title);
                your_address.setText(m_Text);
                okay_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        message_string = String.valueOf(message.getText());
                        String message_title = String.valueOf(title.getText());
                        String s_latlng = String.valueOf(latitude) + String.valueOf(latitude);
                        if(message_string.equals("")){
                            Toast.makeText(MapsActivity.this, "Please Enter Some Message", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            dialog.dismiss();
                            addDataToFirestore(message_title,message_string, String.valueOf(latLng));
                        }
                    }
                });

                cancel_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Toast.makeText(MapsActivity.this, "Cancel clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }

            private void SaveToFireBase(String message_string, double latitude, double longitude) {

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(latLng).title("Your current location"));
        moveToCurrentLocation(latLng,mMap);
    }

    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }


    protected void getLocation() throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (isLocationEnabled(MapsActivity.this)) {
                locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                criteria = new Criteria();
                bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

                //You can still do this if you like, you might get lucky:
                if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    Log.e("TAG", "GPS is on");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Toast.makeText(MapsActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                    searchNearestPlace(voice2text);

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());

                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    m_Text = address;
                    latLng = new LatLng(latitude,longitude);


                }
                else{
                    //This is what you need:
                    locationManager.requestLocationUpdates(bestProvider, 1000L, (float) 0, (LocationListener) this);
                }
            }
            else
            {

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


    }


    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    public void onProviderEnabled(String provider) {

    }


    public void onProviderDisabled(String provider) {

    }

    public void searchNearestPlace(String v2txt) {
        //.....
    }



    private void moveToCurrentLocation(LatLng currentLocation,GoogleMap mMap)
    {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
       // mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.



    }


    public class Notes {

        // variables for storing our data.
        public String title, notes, latlng;

        public Notes() {
            // empty constructor
            // required for Firebase.
        }

        // Constructor for all variables.
        public Notes(String title, String notes, String latlng) {
            this.title = title;
            this.notes = notes;
            this.latlng = String.valueOf(latlng);
        }

        // getter methods for all variables.
        public String gettitle() {
            return title;
        }

        public void settitle(String title) {
            this.title = title;
        }

        public String notes() {
            return notes;
        }

        // setter method for all variables.
        public void setnotes(String notes) {
            this.notes = notes;
        }

        public String getLatlng() {
            return latlng;
        }

        public void setLatlng(String latlng) {
            this.latlng = latlng;
        }

    }


    private void addDataToFirestore(String title, String notes, String latlng) {

        // creating a collection reference
        db = FirebaseFirestore.getInstance();
        // for our Firebase Firetore database.
        CollectionReference db_notes = db.collection("db_notes");

        // adding our data to our courses object class.
        MapsActivity.Notes courses = new MapsActivity.Notes(title, notes, latlng);

        // below method is use to add data to Firebase Firestore.
        db_notes.add(courses).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference ) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(MapsActivity.this, "Your Notes Have Been Saved", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(MapsActivity.this, "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }





}
