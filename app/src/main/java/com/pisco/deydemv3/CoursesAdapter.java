package com.pisco.deydemv3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

    ArrayList<CourseModel> list;
    Context context;

    public CoursesAdapter(Context context, ArrayList<CourseModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_courses, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        CourseModel c = list.get(position);

        h.tvPickup.setText(c.pickup);
        h.tvDropoff.setText(c.dropoff);
        h.tvStatus.setText(c.status);
        h.tvPrice.setText(c.price + " FCFA");
        h.tvPhone.setText("Date : " + c.phone);

        // 🎨 Couleur selon statut
        switch (c.status) {
            case "pending":
                h.card.setStrokeColor(Color.GRAY);
                h.btnCancel.setVisibility(View.VISIBLE);
                break;

            case "accepted":
                h.card.setStrokeColor(Color.BLUE);
                h.btnCancel.setVisibility(View.GONE);
                break;

            case "ongoing":
                h.card.setStrokeColor(Color.YELLOW);
                h.btnCancel.setVisibility(View.GONE);
                break;

            case "completed":
                h.card.setStrokeColor(Color.GREEN);
                h.btnCancel.setVisibility(View.GONE);
                break;

            case "cancelled":
                h.card.setStrokeColor(Color.RED);
                h.btnCancel.setVisibility(View.GONE);
                break;
        }

        // ❌ ANNULER COURSE
        h.btnCancel.setOnClickListener(v -> {
            confirmCancel(c.id);
        });

        h.itemView.setOnClickListener(v -> {

            BottomSheetDialog dialog = new BottomSheetDialog(context);
            View sheet = LayoutInflater.from(context)
                    .inflate(R.layout.bottom_sheet_course, null);

            TextView tvPickup = sheet.findViewById(R.id.tvPickup);
            TextView tvDropoff = sheet.findViewById(R.id.tvDropoff);
            TextView tvPrice = sheet.findViewById(R.id.tvPrice);
            TextView tvStatus = sheet.findViewById(R.id.tvStatus);

            MaterialButton btnMap = sheet.findViewById(R.id.btnMap);
            MaterialButton btnDetails = sheet.findViewById(R.id.btnDetails);
            MaterialButton btnCancel = sheet.findViewById(R.id.btnCancel);

            tvPickup.setText("📍 " + c.pickup);
            tvDropoff.setText("➡ " + c.dropoff);
            tvPrice.setText("💰 " + c.price + " FCFA");
            tvStatus.setText("Statut : " + c.status);

            // Annuler visible seulement si pending
            btnCancel.setVisibility(
                    c.status.equals("pending") ? View.VISIBLE : View.GONE
            );

            btnDetails.setOnClickListener(x -> {
                Intent i = new Intent(context, CourseDetailActivity.class);
                i.putExtra("course", (Parcelable) c);
                context.startActivity(i);
                dialog.dismiss();
            });

            btnCancel.setOnClickListener(x -> {
                // TODO : appel API annulation
                dialog.dismiss();
            });

            dialog.setContentView(sheet);
            dialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 🔔 CONFIRMATION
    private void confirmCancel(int courseId) {
        new AlertDialog.Builder(context)
                .setTitle("Annuler la course")
                .setMessage("Voulez-vous vraiment annuler cette course ?")
                .setPositiveButton("Oui", (d, w) -> cancelCourse(courseId))
                .setNegativeButton("Non", null)
                .show();
    }

    // 🔥 APPEL API ANNULATION
    private void cancelCourse(int courseId) {
        String url = "http://192.168.1.7/deydemlivraisonphpmysql/cancel_course.php";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.contains("success")) {
                        Toast.makeText(context, "Course annulée", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Erreur annulation", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("course_id", String.valueOf(courseId));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(req);
    }

    // 🔗 VIEW HOLDER
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPickup, tvDropoff, tvPrice, tvStatus, tvPhone;
        MaterialCardView card;
        MaterialButton btnCancel;

        public ViewHolder(@NonNull View v) {
            super(v);

            card = v.findViewById(R.id.cardCourse);
            tvPickup = v.findViewById(R.id.tvPickup);
            tvDropoff = v.findViewById(R.id.tvDropoff);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvPhone = v.findViewById(R.id.tvPhone);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }
}
