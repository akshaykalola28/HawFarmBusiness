package com.project.hawfarmbusiness;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddStockFragment extends Fragment {

    View mainView;

    EditText vegNameField, totalStockField, gram1Field, price1Field,descriptionField;
    Button submitButton;
    AutoCompleteTextView vegNameAutoComplete;

    String vegName, totalStockString, gram1String, price1String,description;
    JSONObject userDataJson;
    String[] texts;

    ProgressDialog mDialog;

    List<String> responseList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.add_stock_fragment, container, false);

        userDataJson = ((HomeActivity) getActivity()).getUser();

        vegNameField = mainView.findViewById(R.id.input_veg_name);
        totalStockField = mainView.findViewById(R.id.input_total_stock);
        gram1Field = mainView.findViewById(R.id.input_grams_1);
        price1Field = mainView.findViewById(R.id.input_price_1);
        vegNameAutoComplete = mainView.findViewById(R.id.input_veg_name);
        descriptionField = mainView.findViewById(R.id.input_Description);

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
        JSONObject reqParams = new JSONObject();
        try {
            reqParams.put("email", userDataJson.getString("email"));
            reqParams.put("veg_name", vegName);
            reqParams.put("total_stock", totalStockString);
            reqParams.put("description",description);

            JSONArray priceArray = new JSONArray();
            JSONObject priceSingleObject = new JSONObject();
            priceSingleObject.put("weight", gram1String);
            priceSingleObject.put("price", price1String);
            priceArray.put(priceSingleObject);

            reqParams.put("price", priceArray);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ServerData.ADD_STOCK_URL, reqParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Log.d("DATA", response.toString());
                        String success = response.getString("responseSuccess");
                        if (success.equals("true")) {
                            mDialog.dismiss();
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            alertDialogBuilder.setTitle("Add Stock").setMessage("Stock Added Successfully").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                            alertDialogBuilder.setTitle("Add Stock").setMessage("Failed to add Stock").setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
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

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getProductName() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServerData.PRODUCT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("RESPONSE", response);

                try {
                    JSONArray productArray = new JSONArray(response);
                    texts = new String[productArray.length()];
                    for (int i = 0; i < productArray.length(); i++) {
                        texts[i] = productArray.getString(i);
                    }
                    Log.d("ARRAY", Arrays.toString(texts));

                    try {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, texts);
                        vegNameAutoComplete.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private boolean getValidData() {
        boolean valid = false;
        boolean vegIsValid = false;
        vegName = vegNameField.getText().toString().trim();
        totalStockString = totalStockField.getText().toString().trim();
        gram1String = gram1Field.getText().toString().trim();
        price1String = price1Field.getText().toString().trim();
        description = descriptionField.getText().toString().trim();

        Log.d("value of", Arrays.toString(texts));

        if (!vegName.isEmpty()) {
            for (int i = 0; i < texts.length; i++) {
                if (vegName.equals(texts[i])) {
                    vegIsValid = true;
                    break;
                }
            }
        }
        if (!vegIsValid){
            vegNameField.setError("Enter Vegetable Name");
            vegNameField.requestFocus();
        } else if (totalStockString.isEmpty()) {
            totalStockField.setError("Enter Total Stock");
            totalStockField.requestFocus();
        }
        else if (description.isEmpty())
        {
            descriptionField.setError("Enter Description");
            descriptionField.requestFocus();
        }
        else if (gram1String.isEmpty()) {
            gram1Field.setError("Enter Grams");
            gram1Field.requestFocus();
        }else if (price1String.isEmpty()) {
            price1Field.setError("Enter Price");
            price1Field.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }
}
