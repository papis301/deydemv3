package com.pisco.deydemv3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CoursesAdapter adapter;
    ArrayList<CourseModel> list = new ArrayList<>();

    Handler handler = new Handler();
    String url = "http://192.168.1.5/deydemlivraisonphpmysql/get_courses.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.rvCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CoursesAdapter(list);
        recyclerView.setAdapter(adapter);

        // Charger immédiatement
        fetchCourses();

        // Rafraîchissement auto toutes les 3 secondes
        handler.postDelayed(refreshRunnable, 3000);
    }

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            fetchCourses();
            handler.postDelayed(this, 3000);
        }
    };


    private void fetchCourses() {
        StringRequest req = new StringRequest(url,
                response -> {
                    Log.e("JSON", response);
                    try {
                        JSONArray array = new JSONArray(response);

                        list.clear(); // vider l’ancienne liste

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject course = array.getJSONObject(i);

                            int id = course.getInt("id");
                            String pickup = course.getString("pickup_address");
                            String dropoff = course.getString("dropoff_address");
                            int price = course.getInt("price");
                            String status = course.getString("status");
                            String date = course.getString("created_at");

                            // ADD dans la liste
                            list.add(new CourseModel(
                                    id,
                                    pickup,
                                    dropoff,
                                    price,
                                    status,
                                    date
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Log.e("JSON ERROR", e.toString());
                    }
                },
                error -> Toast.makeText(this, "Erreur réseau : " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(req);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }
}
