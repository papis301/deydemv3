package com.pisco.deydemv3;

import static com.pisco.deydemv3.Constants.BASE_URL;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CourseDetailActivity extends AppCompatActivity {

    TextView tvPickup, tvDropoff, tvPrice, tvStatus, tvDate;
    MaterialButton btnCall, btnMap, btnCancel;
    MaterialCardView statusCard;

    CourseModel course;
    int courseId;
    String phonerecupe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        course = getIntent().getParcelableExtra("course");
        if (course == null) {
            finish();
            return;
        }

        courseId = course.id;


        Toast.makeText(this, "Course driver "+course.driverId, Toast.LENGTH_SHORT).show();



        tvPickup = findViewById(R.id.tvPickup);
        tvDropoff = findViewById(R.id.tvDropoff);
        tvPrice = findViewById(R.id.tvPrice);
        tvStatus = findViewById(R.id.tvStatus);
        tvDate = findViewById(R.id.tvDate);

        btnCall = findViewById(R.id.btnCall);
        btnMap = findViewById(R.id.btnMap);
        btnCancel = findViewById(R.id.btnCancel);
        statusCard = findViewById(R.id.statusCard);

        tvPickup.setText(course.pickup);
        tvDropoff.setText(course.dropoff);
        tvPrice.setText(course.price + " FCFA");
        tvStatus.setText(course.status.toUpperCase());




        // 🎨 Couleur du statut
        switch (course.status) {
            case "pending":
                statusCard.setCardBackgroundColor(Color.LTGRAY);
                btnCancel.setVisibility(View.VISIBLE);
                btnCall.setVisibility(View.GONE);
                break;

            case "accepted":
                statusCard.setCardBackgroundColor(Color.parseColor("#2196F3"));
                btnCall.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                break;

            case "ongoing":
                statusCard.setCardBackgroundColor(Color.parseColor("#FFC107"));
                btnCall.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                break;

            case "completed":
                statusCard.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                btnCall.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                break;

            case "cancelled":
                statusCard.setCardBackgroundColor(Color.RED);
                btnCall.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                break;
        }



        // 📞 Appeler chauffeur
        btnCall.setOnClickListener(v -> {
            recupphonedriver(course.driverId);
        });

        // 🗺 Voir sur la carte
        btnMap.setOnClickListener(v -> {
            Intent i = new Intent(this, CourseMapActivity.class);
            i.putExtra("course", course);
            startActivity(i);
        });

        // ❌ Annuler
        btnCancel.setOnClickListener(v -> {
            // 👉 Appel API annulation (déjà géré dans l’adapter si tu veux)
            finish();
        });
    }

//    private String recupphonedriver(int driverId){
//        final String[] phonerecup = {null};
//        StringRequest req = new StringRequest(Request.Method.POST,
//                BASE_URL + "get_user_by_id.php",
//                response -> {
//                    Log.d("numero", response);
//                    try {
//                        JSONObject obj = new JSONObject(response);
//                        if (obj.getBoolean("success")) {
//                            JSONObject user = obj.getJSONObject("user");
//                            phonerecupe = phonerecup[0] = user.getString("phone");
//                            Log.d("numero", phonerecup[0]);
//                            Toast.makeText(this, ""+phonerecupe, Toast.LENGTH_LONG).show();
//                            tvDate.setText(phonerecupe);
//                            Intent i = new Intent(Intent.ACTION_DIAL);
//                            i.setData(Uri.parse("tel:" + phonerecupe));
//                            startActivity(i);
//                        }
//                    } catch (Exception e) {}
//                },
//                error -> {}
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> p = new HashMap<>();
//                p.put("user_id", String.valueOf(driverId));
//                return p;
//            }
//        };
//        Volley.newRequestQueue(this).add(req);
//        return phonerecup[0];
//    }

    private void recupphonedriver(int driverId) {

        StringRequest req = new StringRequest(
                Request.Method.POST,
                BASE_URL + "get_user_by_id.php",
                response -> {
                    Log.d("numero", response);
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getBoolean("success")) {
                            JSONObject user = obj.getJSONObject("user");

                            String phone = user.getString("phone");

                            // ✅ AFFICHER LE NUMÉRO
                            tvDate.setText("📞 Chauffeur : " + phone);

                            // ✅ APPELER
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Numéro indisponible", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur parsing", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("user_id", String.valueOf(driverId));
                return p;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }

}
