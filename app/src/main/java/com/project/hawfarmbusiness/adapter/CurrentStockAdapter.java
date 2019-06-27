package com.project.hawfarmbusiness.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.hawfarmbusiness.R;

public class CurrentStockAdapter extends RecyclerView.Adapter<CurrentStockAdapter.CurrentStockViewHolder> {

    private Context context;

    @NonNull
    @Override
    public CurrentStockViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.current_stock_cardview, viewGroup, false);

        return new CurrentStockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentStockViewHolder currentStockViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CurrentStockViewHolder extends RecyclerView.ViewHolder {
        public CurrentStockViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
