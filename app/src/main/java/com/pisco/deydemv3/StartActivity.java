package com.pisco.deydemv3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // 🔗 Bind views
        tvStatus = findViewById(R.id.tvStatus);
        btnRetry = findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> checkFlow());

        // ⏳ Splash de 2 secondes
        new Handler().postDelayed(this::startFlow, 2000);
    }

    /**
     * Point d’entrée logique de l’app
     * 1️⃣ Vérifie CGU
     * 2️⃣ Puis Internet + session
     */
    private void startFlow() {
        if (!isCguAccepted()) {
            startActivity(new Intent(this, CguActivity.class));
            finish();
            return;
        }

        checkFlow();
    }

    /**
     * Vérifie Internet + session utilisateur
     */
    private void checkFlow() {

        if (!isConnected()) {
            tvStatus.setText("Aucune connexion Internet !");
            btnRetry.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Veuillez activer Internet", Toast.LENGTH_LONG).show();
            return;
        }

        // ✅ Internet OK → vérifier session
        SharedPreferences sp = getSharedPreferences("DeydemUser", MODE_PRIVATE);
        String userId = sp.getString("user_id", "0");
        String tel = sp.getString("phone", "");

        if (userId.equals("0") || tel.isEmpty()) {
            // ❌ Pas connecté
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            // ✅ Déjà connecté
            startActivity(new Intent(this, PickupDeliveryActivity.class));
        }

        finish();
    }

    /**
     * Vérifie si les CGU sont acceptées
     */
    private boolean isCguAccepted() {
        SharedPreferences sp = getSharedPreferences("DeydemUser", MODE_PRIVATE);
        return sp.getBoolean("cgu_accepted", false);
    }

    /**
     * Vérifie la connexion Internet
     */
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}
