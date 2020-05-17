package com.example.foodtruckfinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class SecondActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    private TextView name, style, operation, rating, rec1, rec2, price1, price2;
    private ImageView head_bg, rec1_pic, rec2_pic;
    private BitmapDescriptor restaurant_icon;

    private Switch location, operating;
    private Button report_operating, report_location;
    private Boolean operating_ori, location_ori;

    private LatLng restaurant_location;

    private String restaurant_name, operation_time_string;
    private String[] start_time, end_time;

    private SupportMapFragment mapFragment;
    private MarkerOptions restaurant_marker;

    private ScrollView scroll;
    private View trans_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_truck_detail);

        name = (TextView) findViewById(R.id.restaurant_name);
        style = (TextView) findViewById(R.id.style);
        operation = (TextView) findViewById(R.id.operation_time);
        rating = (TextView) findViewById(R.id.rating);
        rec1 = (TextView) findViewById(R.id.recommendation1);
        price1 = (TextView) findViewById(R.id.price1);
        rec2 = (TextView) findViewById(R.id.recommendation2);
        price2 = (TextView) findViewById(R.id.price2);

        head_bg = (ImageView) findViewById(R.id.head_bg);
        rec1_pic = (ImageView) findViewById(R.id.rec1_image);
        rec2_pic = (ImageView) findViewById(R.id.rec2_image);

        location = (Switch) findViewById(R.id.location_switch);
        operating = (Switch) findViewById(R.id.operating_switch);

        report_operating = (Button) findViewById(R.id.report_operating_btn);
        report_location = (Button) findViewById(R.id.report_location_btn);

        //Resolve ScrollView and GoogleMap conflict
        scroll = (ScrollView) findViewById(R.id.scroll);
        trans_bg = (View) findViewById(R.id.trans_bg);
        trans_bg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scroll.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scroll.requestDisallowInterceptTouchEvent(false);
                        return true;

                    default:
                        return true;
                }
            }
        });

        getLocationPermission();

        String restaurant = getIntent().getStringExtra("restaurant");
        renderPage(restaurant);

        operating_ori = operating.isChecked();
        operating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (operating_ori != isChecked) {
                    report_operating.setVisibility(View.VISIBLE);
                }
                else {
                    report_operating.setVisibility(View.GONE);
                }
            }
        });

        location_ori = location.isChecked();
        location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (location_ori != isChecked) {
                    addNewLocation();
                }
                else {
                    mMap.setOnMapClickListener(null);
                    updateMap();
                    report_location.setVisibility(View.GONE);
                }
            }
        });

        report_operating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(SecondActivity.this, Html.fromHtml("<i><b>Thank you, we've got your report!</b></i>"), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        report_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(SecondActivity.this, Html.fromHtml("<i><b>Thank you, we've got your report!</b></i>"), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                updateMap();
                return false;
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

    public void renderPage(String restaurant) {
        Resources resources = getResources();
        int head_bg_id = resources.getIdentifier(restaurant + "_bg", "drawable", getPackageName());
        head_bg.setImageDrawable(getDrawable(head_bg_id));
        int rec1_pic_id = resources.getIdentifier(restaurant + "_rec1", "drawable", getPackageName());
        rec1_pic.setImageDrawable(getDrawable(rec1_pic_id));
        int rec2_pic_id = resources.getIdentifier(restaurant + "_rec2", "drawable", getPackageName());
        rec2_pic.setImageDrawable(getDrawable(rec2_pic_id));
        int index_bg_id = resources.getIdentifier(restaurant + "_index", "drawable", getPackageName());
        restaurant_icon = BitmapDescriptorFactory.fromResource(index_bg_id);

        InputStream inputStream = getResources().openRawResource(R.raw.truck_info);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        JSONObject jObject;
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // Get data from JSON
            jObject = new JSONObject(byteArrayOutputStream.toString());
            JSONObject target = jObject.getJSONObject(restaurant);

            //Render restaurant's name, style, and operation time.
            restaurant_name = target.getString("name");
            name.setText(restaurant_name);
            style.setText(target.getString("type"));
            JSONArray operation_time = target.getJSONArray("operation");
            start_time = operation_time.getString(0).split(":");
            if (Integer.parseInt(start_time[0]) <= 12) {
                operation_time_string = "Mon - Fri " + start_time[0] + ":" + start_time[1] + " am";
            }
            else {
                operation_time_string = "Mon - Fri " + String.valueOf(Integer.parseInt(start_time[0]) - 12) + ":" + start_time[1] + " pm";
            }
            operation_time_string += " - ";
            end_time = operation_time.getString(1).split(":");
            if (Integer.parseInt(end_time[0]) <= 12) {
                operation_time_string += end_time[0] + ":" + end_time[1] + " am";
            }
            else {
                operation_time_string += String.valueOf(Integer.parseInt(end_time[0]) - 12) + ":" + end_time[1] + " pm";
            }
            System.out.println(operation_time_string);
            operation.setText(operation_time_string);

            //Render restaurant rating and rating background
            double rating_num = target.getDouble("rating");
            rating.setText((String) target.getString("rating"));
            if (rating_num <= 3.5) {
                rating.setBackground(getDrawable(R.drawable.rating_mid_bg));
            }

            //Render recommendations' pictures and texts
            JSONObject rec = target.getJSONObject("recommendation");
            rec1.setText(rec.getJSONObject("r1").getString("name"));
            price1.setText(rec.getJSONObject("r1").getString("price"));
            rec2.setText(rec.getJSONObject("r2").getString("name"));
            price2.setText(rec.getJSONObject("r2").getString("price"));

            //Move map camera
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            restaurant_location = new LatLng(target.getJSONArray("location").getDouble(0), target.getJSONArray("location").getDouble(1));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Set operating switch button checked status
        Date now = new Date();
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE", Locale.US);
        String dayOfWeek = dayOfWeekFormat.format(now);
        if (dayOfWeek.equals("Sat") || dayOfWeek.equals("Sun")) {
            operating.setChecked(false);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
        String[] currentTime = dateFormat.format(now).split(":");
        if (Integer.parseInt(currentTime[0]) < Integer.parseInt(start_time[0])) {
            operating.setChecked(false);
        } else if (Integer.parseInt(currentTime[0]) == Integer.parseInt(start_time[0]) && Integer.parseInt(currentTime[1]) < Integer.parseInt(start_time[1])) {
            operating.setChecked(false);
        } else if (Integer.parseInt(currentTime[0]) > Integer.parseInt(end_time[0])) {
            operating.setChecked(false);
        } else if (Integer.parseInt(currentTime[0]) == Integer.parseInt(start_time[0]) && Integer.parseInt(currentTime[1]) > Integer.parseInt(end_time[1])) {
            operating.setChecked(false);
        }



    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1234);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Setup restaurant map marker
        restaurant_marker = new MarkerOptions();
        restaurant_marker.position(restaurant_location);
        restaurant_marker.title(restaurant_name);

        // Add a marker and move the camera
        mMap.addMarker(restaurant_marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurant_location, 16));

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1234);

    }

    private void updateMap() {
        if (mLocationPermissionGranted) {
            // App has permission to access location in the foreground. Start your
            // foreground service that has a foreground service type of "location".
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location current_location) {
                    // Got last known location. In some rare situations this can be null.
                    if (current_location != null) {
                        mMap.clear();
                        mMap.addMarker(restaurant_marker);

                        // Logic to handle location object
                        mLastKnownLocation = current_location;
                        LatLng cur = new LatLng(current_location.getLatitude(), current_location.getLongitude());
                        updateLocationUI();

                        // Update camera based on current location and restaurant location
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(cur);
                        builder.include(restaurant_location);
                        LatLngBounds bounds = builder.build();

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 180);
                        mMap.animateCamera(cameraUpdate);
                    }
                }
            });
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void addNewLocation() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurant_location, 18));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Setup new location map marker
                MarkerOptions markerOptions= new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Correct location");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                markerOptions.draggable(true);

                //Add marker to map
                mMap.clear();
                mMap.addMarker(restaurant_marker);
                mMap.addMarker(markerOptions);

                //Enable report
                report_location.setVisibility(View.VISIBLE);
            }
        });
    }
}


