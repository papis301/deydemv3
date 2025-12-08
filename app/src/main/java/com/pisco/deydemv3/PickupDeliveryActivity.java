package com.pisco.deydemv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PickupDeliveryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    TextView tvPickup, tvDropoff, tvDistance, tvPrice;
    Button btnSelectPickup, btnSelectDropoff, btnconfirme;
   // MaterialAutoCompleteTextView spinnerVehicle;
    String userId, tel;
    Button btnMoto, btnVoiture;


    LatLng pickupLatLng = null;
    LatLng dropoffLatLng = null;

    private static final int PICKUP_REQUEST = 1001;
    private static final int DROPOFF_REQUEST = 1002;
    String vehicle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup_delivery);

        SharedPreferences sp = getSharedPreferences("DeydemUser", MODE_PRIVATE);
         userId = sp.getString("user_id", "0");
        tel = sp.getString("phone", "2");

        Toast.makeText(this, "ID user connecté : " + userId + "\n"+tel, Toast.LENGTH_SHORT).show();

        btnMoto = findViewById(R.id.btnMoto);
        btnVoiture = findViewById(R.id.btnVoiture);


        tvPickup = findViewById(R.id.tvPickup);
        tvDropoff = findViewById(R.id.tvDropoff);
        tvDistance = findViewById(R.id.tvDistance);

        btnSelectPickup = findViewById(R.id.btnPickup);
        btnSelectDropoff = findViewById(R.id.btnDropoff);
        btnconfirme = findViewById(R.id.btnConfirm);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        btnSelectPickup.setOnClickListener(v -> openAutocomplete(PICKUP_REQUEST));
        btnSelectDropoff.setOnClickListener(v -> openAutocomplete(DROPOFF_REQUEST));

        btnconfirme.setOnClickListener(v -> {

            if (pickupLatLng == null) {
                showError("Veuillez choisir le lieu de recuperation.");
                return;
            }

            if (dropoffLatLng == null) {
                showError("Veuillez choisir la destination.");
                return;
            }

            sendCourseToServer();
        });


        // valeur par défaut
        vehicle = "Moto";
        highlightSelected(btnMoto, btnVoiture);

        btnMoto.setOnClickListener(v -> {
            vehicle = "Moto";
            highlightSelected(btnMoto, btnVoiture);
            updatePrice();
        });

        btnVoiture.setOnClickListener(v -> {
            vehicle = "Voiture";
            highlightSelected(btnVoiture, btnMoto);
            updatePrice();
        });

//         spinnerVehicle = findViewById(R.id.spinnerVehicle);
        tvPrice = findViewById(R.id.tvPrice);
//
//// Liste des véhicules
//        String[] vehicles = {"Moto","Voiture"};
//
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, vehicles);
//
//        spinnerVehicle.setAdapter(adapter);
//
//// 👉 Sélection par défaut
//        spinnerVehicle.setText("Moto", false);
//        vehicle = "Moto";  // Important !

//// 👉 Listener de sélection
//        spinnerVehicle.setOnItemClickListener((parent, view, position, id) -> {
//            vehicle = parent.getItemAtPosition(position).toString();
//            updatePrice();
//        });

    }

    private void highlightSelected(Button selected, Button other) {
        selected.setBackgroundColor(Color.parseColor("#4CAF50")); // VERT
        selected.setTextColor(Color.WHITE);

        other.setBackgroundResource(R.drawable.bg_vehicle);
        other.setTextColor(Color.BLACK);
    }

    private void showError(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Information requise")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }



    private void updatePrice() {
        if (pickupLatLng == null || dropoffLatLng == null) {
            tvPrice.setText("Prix : 0 FCFA");
            return;
        }

        // Distance en km
        double distanceKm = calculateDistance(
                pickupLatLng.latitude, pickupLatLng.longitude,
                dropoffLatLng.latitude, dropoffLatLng.longitude
        );

         //vehicle = spinnerVehicle.getText().toString();
        double pricePerKm = vehicle.equals("Moto") ? 500 : 700; // exemple en FCFA

        double totalPrice;

        if (distanceKm <= 10) {
            totalPrice = distanceKm * pricePerKm;
        } else {
            double first10km = 10 * pricePerKm;
            double remainingKm = (distanceKm - 10) * (pricePerKm / 2);
            totalPrice = first10km + remainingKm;
        }

        tvPrice.setText(String.format("Prix : %.0f FCFA", totalPrice));
    }

    private void sendCourseToServer() {

        SharedPreferences sp = getSharedPreferences("DeydemUser", MODE_PRIVATE);
        String clientId = sp.getString("user_id", "0");

        String url = "http://192.168.1.5/deydemlivraisonphpmysql/create_course.php";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("response : ", response);
                    if (response.contains("Course enregistr\\u00e9e")) {
                        // 👉 On va à CoursesActivity seulement si tout est OK
                        Intent intent = new Intent(PickupDeliveryActivity.this, CoursesActivity.class);
                        startActivity(intent);
                    }
                    },
                error -> {
                    Toast.makeText(this, "Erreur réseau : " + error.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("Message serveur", error.getMessage());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("client_id", clientId);
                params.put("pickup", tvPickup.getText().toString());
                params.put("dropoff", tvDropoff.getText().toString());
                params.put("pickup_lat", String.valueOf(pickupLatLng.latitude));
                params.put("pickup_lng", String.valueOf(pickupLatLng.longitude));
                params.put("drop_lat", String.valueOf(dropoffLatLng.latitude));
                params.put("drop_lng", String.valueOf(dropoffLatLng.longitude));
                params.put("distance_km", tvDistance.getText().toString().replace("Distance : ", "").replace(" km", ""));
                params.put("vehicle_type", vehicle);
                params.put("price", tvPrice.getText().toString());

                return params;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }



//    private void openAutocomplete(int requestCode) {
//        List<Place.Field> fields = Arrays.asList(
//                Place.Field.ID,
//                Place.Field.NAME,
//                Place.Field.ADDRESS,
//                Place.Field.LAT_LNG
//        );
//
//        Intent intent = new Autocomplete.IntentBuilder(
//                AutocompleteActivityMode.OVERLAY, fields
//        ).build(this);
//
//        startActivityForResult(intent, requestCode);
//    }

    private void openAutocomplete(int requestCode) {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
        );

        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields
        )
                .setCountries(Arrays.asList("SN"))   // 👉 Limite au Sénégal
                .build(this);

        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            if (requestCode == PICKUP_REQUEST) {
                pickupLatLng = place.getLatLng();
                tvPickup.setText(place.getAddress());
            }

            if (requestCode == DROPOFF_REQUEST) {
                dropoffLatLng = place.getLatLng();
                tvDropoff.setText(place.getAddress());
            }

            updateMap();

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
        }
    }

    // 🔥 Met à jour la carte avec les markers et la route OSRM
    private void updateMap() {
        if (mMap == null) return;

        mMap.clear();

        if (pickupLatLng != null)
            mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        if (dropoffLatLng != null)
            mMap.addMarker(new MarkerOptions().position(dropoffLatLng).title("Dropoff"));

        // Si les 2 endroits sont choisis → tracer route OSRM
        if (pickupLatLng != null && dropoffLatLng != null) {

            // Distance approximative (Haversine)
            double distanceKm = calculateDistance(
                    pickupLatLng.latitude, pickupLatLng.longitude,
                    dropoffLatLng.latitude, dropoffLatLng.longitude
            );

            tvDistance.setText(String.format("Distance : %.2f km", distanceKm));

            // 🔥 TRACE LA ROUTE OSRM
            drawOSRMRoute(pickupLatLng, dropoffLatLng);
            //calcul du prix
            updatePrice();


            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(pickupLatLng)
                    .include(dropoffLatLng)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        }
    }

    // ------------------------------------------------------------
    // 🔥 Appel OSRM + tracé de la polyline
    // ------------------------------------------------------------
    private void drawOSRMRoute(LatLng origin, LatLng destination) {

        String url = "http://router.project-osrm.org/route/v1/driving/"
                + origin.longitude + "," + origin.latitude + ";"
                + destination.longitude + "," + destination.latitude
                + "?overview=full&geometries=geojson";

        new Thread(() -> {
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray coords = json.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.width(12f);
                polylineOptions.color(Color.BLUE);

                for (int i = 0; i < coords.length(); i++) {
                    JSONArray c = coords.getJSONArray(i);
                    double lng = c.getDouble(0);
                    double lat = c.getDouble(1);
                    polylineOptions.add(new LatLng(lat, lng));
                }

                runOnUiThread(() -> {
                    if (mMap != null) {
                        mMap.addPolyline(polylineOptions);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    // 🔥 Calcul distance haversine
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng dakar = new LatLng(14.7167, -17.4677);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dakar, 12));
    }
}
