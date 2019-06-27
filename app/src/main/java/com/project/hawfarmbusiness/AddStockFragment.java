package com.project.hawfarmbusiness;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddStockFragment extends Fragment {

    View mainView;

    EditText vegNameField, totalStockField, gram1Field, price1Field;
    Button submitButton;
    AutoCompleteTextView vegNameAutoComplete;

    String vegName, totalStockString, gram1String, price1String;
    JSONObject userDataJson;
    String[] texts;

    ProgressDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.add_stock_fragment, container, false);

        userDataJson = ((HomeActivity) getActivity()).getUser();

        vegNameField = mainView.findViewById(R.id.input_veg_name);
        totalStockField = mainView.findViewById(R.id.input_total_stock);
        gram1Field = mainView.findViewById(R.id.input_grams_1);
        price1Field = mainView.findViewById(R.id.input_price_1);
        vegNameAutoComplete = mainView.findViewById(R.id.auto_veg_name);

        getProductName();

        submitButton = mainView.findViewById(R.id.input_stock_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getValidData()) {
                    mDialog = new ProgressDialog(getActivity());
                    mDialog.setMessage("Please Wait...");
                    mDialog.show();
                    addStock();
                }
            }
        });

        return mainView;
    }

    private void addStock() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerData.ADD_STOCK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("responseSuccess");
                            if (success.equals("true")) {
                                mDialog.dismiss();
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setTitle("Add Stock").setMessage("Stock Added Successfully")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //TODO: set on click work
                                            }
                                        }).setNegativeButton("Add new", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getFragmentManager().beginTransaction().replace(R.id.home_fragment, new AddStockFragment()).commit();
                                    }
                                }).show();
                            } else {
                                mDialog.dismiss();
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setTitle("Add Stock").setMessage("Failed to add Stock")
                                        .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                //TODO: set on click work
                                            }
                                        }).show();
                            }
                        } catch (JSONException e) {
                            mDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                Snackbar.make(mainView, "Something is Wrong! Please try again.", Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {
                    params.put("user_id", userDataJson.getString("user_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("veg_name", vegName);
                params.put("total_stock", totalStockString);
                params.put("weight", gram1String);
                params.put("price", price1String);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void getProductName() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServerData.PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                        //TODO: Split the array properly
                        texts = response.split(",");
                        Log.d("ARRAY", Arrays.toString(texts));

                        try {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                    android.R.layout.simple_dropdown_item_1line, texts);
                            vegNameAutoComplete.setAdapter(adapter);
                        } catch (Exception e) {
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

    private boolean getValidData() {
        boolean valid = false;

        vegName = vegNameField.getText().toString().trim();
        totalStockString = totalStockField.getText().toString().trim();
        gram1String = gram1Field.getText().toString().trim();
        price1String = price1Field.getText().toString().trim();

        if (vegName.isEmpty()) {
            vegNameField.setError("Enter Vegetable Name");
            vegNameField.requestFocus();
        } else if (totalStockString.isEmpty()) {
            totalStockField.setError("Enter Total Stock");
            totalStockField.requestFocus();
        } else if (gram1String.isEmpty()) {
            gram1Field.setError("Enter Grams");
            gram1Field.requestFocus();
        } else if (price1String.isEmpty()) {
            price1Field.setError("Entar Price");
            price1Field.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }
}
