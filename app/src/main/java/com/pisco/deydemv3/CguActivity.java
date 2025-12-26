package com.pisco.deydemv3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class CguActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cgu);

        CheckBox cbAccept = findViewById(R.id.cbAccept);
        Button btnAccept = findViewById(R.id.btnAccept);

        cbAccept.setOnCheckedChangeListener((buttonView, isChecked) ->
                btnAccept.setEnabled(isChecked)
        );

        btnAccept.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences("DeydemUser", MODE_PRIVATE);
            sp.edit().putBoolean("cgu_accepted", true).apply();

            startActivity(new Intent(this, StartActivity.class));
            finish();
        });
    }
}
