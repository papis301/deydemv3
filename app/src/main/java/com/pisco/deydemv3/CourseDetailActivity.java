package com.pisco.deydemv3;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CourseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        CourseModel c = (CourseModel) getIntent().getSerializableExtra("course");

        ((TextView)findViewById(R.id.tvPickup)).setText(c.pickup);
        ((TextView)findViewById(R.id.tvDropoff)).setText(c.dropoff);
        ((TextView)findViewById(R.id.tvPrice)).setText(c.price + " FCFA");
        ((TextView)findViewById(R.id.tvStatus)).setText(c.status);
        ((TextView)findViewById(R.id.tvDate)).setText(c.created);
    }
}
