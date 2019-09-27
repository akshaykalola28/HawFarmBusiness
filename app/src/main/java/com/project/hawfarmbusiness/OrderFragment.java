package com.project.hawfarmbusiness;


import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.project.hawfarmbusiness.adapter.OrderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    private static final String TAG = "OrderFragment";

    View mainView;
    RecyclerView allOrderRecycler;

    List<JSONObject> orderList;
    OrderAdapter mAdapter;

    JSONObject userDataJson;
    String userEmail;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_order, container, false);

        userDataJson = ((HomeActivity) getActivity()).getUser();
        try {
            userEmail = userDataJson.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initUI();
        fetchOrderList();

        return mainView;
    }

    private void initUI() {
        allOrderRecycler = mainView.findViewById(R.id.all_order_recycler);
        allOrderRecycler.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        allOrderRecycler.setLayoutManager(manager);

        orderList = new ArrayList<>();
        mAdapter = new OrderAdapter(getActivity(), this, orderList);
        allOrderRecycler.setAdapter(mAdapter);
    }

    private void fetchOrderList() {
        Log.d(TAG, "fetchOrderList: ");

        String extraString = "";
        if (getArguments() != null && !getArguments().getString("status").isEmpty()) {
            extraString = "?status=" + getArguments().getString("status");
        }
        Log.d(TAG, "fetchOrderList: ExtraString: " + extraString);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                ServerData.GET_ORDER_URL + userEmail + extraString,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: ");
                        try {
                            if (response.getBoolean("responseSuccess")) {
                                JSONArray responseArray = response.getJSONArray("data");
                                for (int i = 0; i < responseArray.length(); i++) {
                                    JSONObject order = responseArray.getJSONObject(i);
                                    orderList.add(order);
                                }
                            } else {
                                Toast.makeText(getActivity(),
                                        "Order Not Available.", Toast.LENGTH_SHORT).show();
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ");
                        Toast.makeText(getActivity(),
                                "Something is wrong ! Please, Try Again...", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);
    }
}
