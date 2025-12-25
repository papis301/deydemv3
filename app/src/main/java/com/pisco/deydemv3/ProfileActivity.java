package com.pisco.deydemv3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvPhone;
    MaterialButton btnEdit, btnSupport, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        btnEdit = findViewById(R.id.btnEdit);
        btnSupport = findViewById(R.id.btnSupport);
        btnLogout = findViewById(R.id.btnLogout);

        loadClient();

        btnSupport.setOnClickListener(v -> openWhatsApp());
        btnLogout.setOnClickListener(v -> logout());

    }

    private void loadClient() {
        SharedPreferences sp = getSharedPreferences("client_session", MODE_PRIVATE);
        tvName.setText(sp.getString("name", "Client"));
        tvPhone.setText(sp.getString("phone", ""));
    }

    private void logout() {
        getSharedPreferences("client_session", MODE_PRIVATE)
                .edit().clear().apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void openWhatsApp() {
        String phone = "221770000000"; // Numéro support
        String msg = "Bonjour, j'ai besoin d'aide";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://wa.me/" + phone + "?text=" + Uri.encode(msg)));
        startActivity(i);
    }
}
