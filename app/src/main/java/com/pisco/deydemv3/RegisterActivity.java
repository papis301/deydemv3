package com.pisco.deydemv3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etPhone, etPassword, etRole;
    Button btnRegister, seconnecter;

    String URL = "https://pisco.alwaysdata.net/register.php"; // 🔥 mets ton lien API

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etRole = findViewById(R.id.etRole); // "client" ou "driver"
        btnRegister = findViewById(R.id.btnRegister);
        seconnecter = findViewById(R.id.seconnecter);

        btnRegister.setOnClickListener(v -> registerUser());
        seconnecter.setOnClickListener(v -> seconnecter());
    }

    private void seconnecter() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void registerUser() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = "client"; //etRole.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Inscription...");
        pd.show();

        StringRequest req = new StringRequest(Request.Method.POST, URL,
                response -> {
                    pd.dismiss();
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show();
                    Log.d("Response", response);
                    if(response.contains("success")){
                        seconnecter();
                    }
                    //
                },
                error -> {
                    pd.dismiss();
                    Toast.makeText(this, "Erreur : " + error.toString(), Toast.LENGTH_LONG).show();
                    Log.d("Erreur", error.toString());
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("password", password);
                params.put("role", role); // chauffeur ou client
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "Mozilla/5.0");
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };


        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }
}
