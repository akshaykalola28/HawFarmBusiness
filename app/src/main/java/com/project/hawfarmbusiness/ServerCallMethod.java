package com.project.hawfarmbusiness;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ServerCallMethod {

    private static final String TAG = "ServerCallMethod";
    private static boolean isUpdated;

    public static boolean orderStatusUpdate(final Context context, String orderId, final String status) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ServerData.STATUS_UPDATE_URL + orderId + "?status=" + status,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: ");
                        Toast.makeText(context, "Order is " + status + " successfully.", Toast.LENGTH_SHORT).show();
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, new HomeMainFragment()).commit();
                        isUpdated = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ");
                        Toast.makeText(context, "Status is not updated.", Toast.LENGTH_SHORT).show();
                        isUpdated = false;
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        return isUpdated;
    }
}
