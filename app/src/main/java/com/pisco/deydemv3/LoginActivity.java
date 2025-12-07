package com.pisco.deydemv3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etPhone, etPassword;
    Button btnLogin, btnregister;

    String URL = "http://192.168.1.4/deydemlivraisonphpmysql/login.php"; // 🔥 remplace par ton URL API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnregister = findViewById(R.id.btnregister);

        btnLogin.setOnClickListener(v -> loginUser());
        btnregister.setOnClickListener(view -> register());

    }

    private void register() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void loginUser() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Connexion...");
        pd.show();

        StringRequest req = new StringRequest(Request.Method.POST, URL,
                response -> {
                    pd.dismiss();
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean success = json.getBoolean("success");
                        if (success) {
                            JSONObject userObj = json.getJSONObject("user");

                            String userId = userObj.getString("id");
                            String userType = userObj.getString("type");
                            Log.d("reponse api", response);
                            Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                            // TODO: récupérer les infos utilisateur

                            String phonee = userObj.getString("phone");

                            // 🔥 SAUVEGARDE EN SESSION
                            SharedPreferences sp = getSharedPreferences("DeydemUser", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("user_id", userId);
                            editor.putString("type", userType);
                            editor.putString("phone", phonee);
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, PickupDeliveryActivity.class);
                            startActivity(intent);

                        } else {
                            String msg = json.getString("message");
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    pd.dismiss();
                    Toast.makeText(this, "Erreur réseau : " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(req);
    }
}
