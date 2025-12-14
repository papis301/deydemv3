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
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CoursesAdapter adapter;
    ArrayList<CourseModel> displayedCourses = new ArrayList<>();
    ArrayList<CourseModel> allCourses = new ArrayList<>();

    MaterialButton btnAll, btnPending, btnOngoing;



    Handler handler = new Handler();
    String url = "http://192.168.1.7/deydemlivraisonphpmysql/get_courses.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.rvCourses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CoursesAdapter(displayedCourses);
        recyclerView.setAdapter(adapter);

        // Charger immédiatement
        fetchCourses();

        btnAll = findViewById(R.id.btnAll);
        btnPending = findViewById(R.id.btnPending);
        btnOngoing = findViewById(R.id.btnOngoing);

        // Rafraîchissement auto toutes les 3 secondes
        handler.postDelayed(refreshRunnable, 3000);

        btnAll.setOnClickListener(v -> {
            currentFilter = "all";
            applyFilter();
            selectButton(btnAll);
        });

        btnPending.setOnClickListener(v -> {
            currentFilter = "pending";
            applyFilter();
            selectButton(btnPending);
        });

        btnOngoing.setOnClickListener(v -> {
            currentFilter = "ongoing";
            applyFilter();
            selectButton(btnOngoing);
        });


    }

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            fetchCourses();
            handler.postDelayed(this, 3000);
        }
    };


    private String currentFilter = "all";

    private void fetchCourses() {
        StringRequest req = new StringRequest(url,
                response -> {
                    Log.e("JSON", response);
                    try {
                        JSONArray array = new JSONArray(response);

                        allCourses.clear();
                        displayedCourses.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject course = array.getJSONObject(i);

                            CourseModel model = new CourseModel(
                                    course.getInt("id"),
                                    course.getString("pickup_address"),
                                    course.getString("dropoff_address"),
                                    course.getInt("price"),
                                    course.getString("status"),
                                    course.getString("created_at")
                            );

                            allCourses.add(model);
                        }

                        // 🔥 Appliquer le filtre courant
                        applyFilter();

                    } catch (Exception e) {
                        Log.e("JSON ERROR", e.toString());
                    }
                },
                error -> Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(req);
    }

    private void applyFilter() {
        displayedCourses.clear();

        if (currentFilter.equals("all")) {
            displayedCourses.addAll(allCourses);
        } else {
            for (CourseModel c : allCourses) {
                if (c.status.equalsIgnoreCase(currentFilter)) {
                    displayedCourses.add(c);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }


    private void filterCourses(String status) {
        displayedCourses.clear();

        if (status.equals("all")) {
            displayedCourses.addAll(allCourses);
        } else {
            for (CourseModel c : allCourses) {
                if (c.status.equalsIgnoreCase(status)) {
                    displayedCourses.add(c);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void selectButton(MaterialButton selected) {
        btnAll.setChecked(false);
        btnPending.setChecked(false);
        btnOngoing.setChecked(false);

        selected.setChecked(true);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }
}
