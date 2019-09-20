package com.project.hawfarmbusiness.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.hawfarmbusiness.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CurrentStockAdapter extends RecyclerView.Adapter<CurrentStockAdapter.CurrentStockViewHolder> {

    private Context context;
    private List<JSONObject> currentStockList;

    public CurrentStockAdapter(Context context, List<JSONObject> currentStockList) {
        this.context = context;
        this.currentStockList = currentStockList;
    }

    @NonNull
    @Override
    public CurrentStockViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.current_stock_cardview, viewGroup, false);

        return new CurrentStockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentStockViewHolder currentStockViewHolder, int i) {
        JSONObject currentStock = currentStockList.get(i);
        try {
            //JSONArray productArray = currentStock.getJSONArray("product");
            Log.d("", String.valueOf(currentStockList.size()));
            currentStockViewHolder.vegNameField.setText(currentStock.getString("veg_name"));
            currentStockViewHolder.totalStockField.setText(currentStock.getString("total_stock"));
            currentStockViewHolder.currentStockField.setText(currentStock.getString("total_stock"));
            currentStockViewHolder.stockId.setText(currentStock.getString("stockId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return currentStockList.size();
    }

    public class CurrentStockViewHolder extends RecyclerView.ViewHolder {
        TextView vegNameField, dateField, totalStockField, currentStockField, stockId;

        public CurrentStockViewHolder(@NonNull View itemView) {
            super(itemView);
            vegNameField = itemView.findViewById(R.id.veg_name);
            //dateField = itemView.findViewById(R.id.veg_date);
            totalStockField = itemView.findViewById(R.id.total_stock_field);
            currentStockField = itemView.findViewById(R.id.current_stock_field);
            stockId = itemView.findViewById(R.id.stock_id_field);
        }
    }
}
