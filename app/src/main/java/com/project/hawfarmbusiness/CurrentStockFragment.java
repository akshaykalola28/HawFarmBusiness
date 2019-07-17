package com.project.hawfarmbusiness;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.hawfarmbusiness.adapter.CurrentStockAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CurrentStockFragment extends Fragment {

    View mainView;
    CurrentStockAdapter mAdapter;
    List<JSONObject> currentStockList;

    JSONObject userDataJson;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.current_stock_fragment, container, false);

        userDataJson = ((HomeActivity) getActivity()).getUser();
        try {
            userId = userDataJson.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RecyclerView recyclerView = mainView.findViewById(R.id.current_stock_recycleview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        currentStockList = new ArrayList<>();
        mAdapter = new CurrentStockAdapter(getActivity().getApplicationContext(), currentStockList);
        recyclerView.setAdapter(mAdapter);
        fetchCurrentAllStock();

        return mainView;
    }

    private void fetchCurrentAllStock() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerData.ALL_STOCK_URL + "105",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject allResponse = new JSONObject(response);
                            if (allResponse.getString("responseSuccess").equals("true")) {
                                JSONObject data = allResponse.getJSONArray("data").getJSONObject(0);
                                JSONArray products = new JSONArray(data.getString("product"));
                                Log.d("dataArray", products.length() + "");
                                for (int i = 0; i < products.length(); i++) {
                                    JSONObject stockInfo = products.getJSONObject(i);
                                    currentStockList.add(stockInfo);
                                    Log.d("stockInfo", i + ": " + stockInfo.toString());
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Something is wrong ! Please, Try Again...", Toast.LENGTH_SHORT).show();
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
