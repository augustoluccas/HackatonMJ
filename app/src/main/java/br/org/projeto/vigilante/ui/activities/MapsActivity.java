package br.org.projeto.vigilante.ui.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;

import br.org.projeto.vigilante.R;
import br.org.projeto.vigilante.util.AppUtil;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        View.OnClickListener {

    private static final int REQUEST_GPS_PERMISSION = 12;

    private static final int REQUEST_TURN_ON_GPS = 13;

    private Toolbar mToolbar;

    private Button mButton;

    private GoogleMap mMap;

    private LatLng mLatLng;

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mToolbar = (Toolbar) findViewById(R.id.maps_toolbar);
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButton = (Button) findViewById(R.id.maps_confirm);
        mButton.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maps_map_fragment);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
            mGoogleApiClient.connect();
        }

        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Ask for permission to use GPS on Android M or later.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            askForGPS();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_GPS_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_GPS_PERMISSION: {
                // If user authorized, ask know to turn on GPS.
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    askForGPS();
                } else {
                    finish();
                }
                break;
            }
        }
    }

    private void askForGPS() {

        if (((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getMyCurrentLocation();
        } else {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // GPS already on.
                            getMyCurrentLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Request for GPS on.
                            try {
                                status.startResolutionForResult(MapsActivity.this, REQUEST_TURN_ON_GPS);
                            } catch (IntentSender.SendIntentException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Device doesnt have GPS, there's nothing to do.
                            finish();
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TURN_ON_GPS && resultCode == RESULT_OK) {
            getMyCurrentLocation();
        } else {
            supportFinishAfterTransition();
        }
    }

    private void getMyCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            moveCamera();

            mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerClickListener(this);
        }
    }

    private void moveCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                mLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Remove all markers and set a new one, using LatLng.
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));

        mLatLng = latLng;
    }

    private Address getAddress() {
        final Geocoder geo = new Geocoder(this);
        Address address = null;
        try {
            final List<Address> addresses = geo.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
            if (addresses.size() > 0) {

                address = addresses.get(0);
                address.getCountryName(); // Pais - Brasil
                address.getAdminArea(); // Estado - São Paulo
                address.getLocality(); // Cidade - Campinas
                address.getSubLocality(); // Bairro - Jd Carlos...
                address.getThoroughfare(); // Rua
                address.getSubThoroughfare(); // Número
                address.getPostalCode(); // CEP 1310...
                address.getLatitude();
                address.getLongitude();

            }
        } catch (Exception e) {
            return null;
        }
        return address;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        // Disable possible infos like route of marker.
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        moveCamera();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Do nothing. User doesnt have Google Play Service.
        supportFinishAfterTransition();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Do nothing. User doesnt have Google Play Service.
        supportFinishAfterTransition();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        final Intent output = new Intent();
        final Address address = getAddress();
        if (address != null) {
            output.putExtra(ReportActivity.REPORT_MAP_ADDRESS, new Gson().toJson(address));
            setResult(RESULT_OK, output);
            supportFinishAfterTransition();
        } else {
            AppUtil.showToast(this, R.string.maps_try_again);
        }
    }
}

