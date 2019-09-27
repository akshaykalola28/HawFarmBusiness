package com.project.hawfarmbusiness.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.project.hawfarmbusiness.R;
import com.project.hawfarmbusiness.ServerCallMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private static final String TAG = "OrderAdapter";

    private Context context;
    private Fragment fragment;
    private List<JSONObject> items;

    public OrderAdapter(Context context, Fragment fragment, List<JSONObject> items) {
        this.context = context;
        this.fragment = fragment;
        this.items = items;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_order_list, viewGroup, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder orderViewHolder, int i) {
        final JSONObject item = items.get(i);

        try {
            final String orderId = item.getString("orderId");
            orderViewHolder.orderIdField.setText(orderId);
            orderViewHolder.dateField.setText(item.getString("orderDate"));
            orderViewHolder.buyerNameField.setText(item.getString("buyerName"));
            orderViewHolder.buyerAddressField.setText(item.getString("buyerAddress"));

            //Show Appropriate Button
            final String status = item.getString("status");
            if (status.equals("pending")) {
                orderViewHolder.acceptButton.setVisibility(View.VISIBLE);
                orderViewHolder.rejectButton.setVisibility(View.VISIBLE);

                orderViewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isUpdated = ServerCallMethod.orderStatusUpdate(context, orderId, "accepted");
                        if (isUpdated){
                            notifyDataSetChanged();
                            Log.d(TAG, "onClick: Success");
                        } else {
                            Log.d(TAG, "onClick: Failed");
                        }

                    }
                });

                orderViewHolder.rejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ServerCallMethod.orderStatusUpdate(context, orderId, "rejected");
                    }
                });
            } else if (status.equals("accepted")) {
                orderViewHolder.deliverButton.setVisibility(View.VISIBLE);
                orderViewHolder.deliverButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ServerCallMethod.orderStatusUpdate(context, orderId, "delivered");
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderIdField, dateField, buyerNameField, buyerAddressField;
        Button acceptButton, rejectButton, deliverButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderIdField = itemView.findViewById(R.id.order_id_field);
            dateField = itemView.findViewById(R.id.order_date_field);
            buyerNameField = itemView.findViewById(R.id.buyer_name_field);
            buyerAddressField = itemView.findViewById(R.id.buyer_address_field);
            acceptButton = itemView.findViewById(R.id.accept_order);
            rejectButton = itemView.findViewById(R.id.reject_order);
            deliverButton = itemView.findViewById(R.id.delivered_order);
        }
    }
}
