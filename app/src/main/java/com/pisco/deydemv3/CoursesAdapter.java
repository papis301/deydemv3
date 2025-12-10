package com.pisco.deydemv3;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.pisco.deydemv3.CourseModel;
import com.pisco.deydemv3.R;

import java.util.ArrayList;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.ViewHolder> {

    ArrayList<CourseModel> list;

    public CoursesAdapter(ArrayList<CourseModel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_courses, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        CourseModel c = list.get(position);

        h.tvPickup.setText(c.pickup);
        h.tvDropoff.setText(c.dropoff);
        h.tvStatus.setText(c.status);
        h.tvPrice.setText(c.price + " FCFA");
        h.tvPhone.setText("Client : " + c.phone);

        // Couleurs selon statut
        switch (c.status) {
            case "pending": h.card.setStrokeColor(Color.GRAY); break;
            case "accepted": h.card.setStrokeColor(Color.BLUE); break;
            case "ongoing": h.card.setStrokeColor(Color.YELLOW); break;
            case "completed": h.card.setStrokeColor(Color.GREEN); break;
            case "cancelled": h.card.setStrokeColor(Color.RED); break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPickup, tvDropoff, tvPrice, tvStatus, tvPhone;
        MaterialCardView card;

        public ViewHolder(View v) {
            super(v);

            card = v.findViewById(R.id.cardCourse);
            tvPickup = v.findViewById(R.id.tvPickup);
            tvDropoff = v.findViewById(R.id.tvDropoff);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvPhone = v.findViewById(R.id.tvPhone);
        }
    }
}
