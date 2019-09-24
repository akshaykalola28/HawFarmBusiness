package com.project.hawfarmbusiness;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

public class AddStockFragment extends Fragment {

    private static final String TAG = "AddStockFragment";
    View mainView;

    EditText totalStockField, gram1Field, price1Field, descriptionField;
    Button submitButton;
    AutoCompleteTextView vegNameAutoComplete;
    ImageView imagePick;

    String vegName, totalStockString, gram1String, price1String, description;
    JSONObject userDataJson;
    String[] texts;
    Uri fileUri;
    String baseImg;

    ProgressDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.add_stock_fragment, container, false);
        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.add_stock_background);

        userDataJson = ((HomeActivity) getActivity()).getUser();

        totalStockField = mainView.findViewById(R.id.input_total_stock);
        gram1Field = mainView.findViewById(R.id.input_grams_1);
        price1Field = mainView.findViewById(R.id.input_price_1);
        vegNameAutoComplete = mainView.findViewById(R.id.input_veg_name);
        descriptionField = mainView.findViewById(R.id.input_Description);

        imagePick = mainView.findViewById(R.id.input_image_submit);

        getProductName();

        imagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, 100);
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        try {
            // When an Image is picked
            if (requestCode == 100 && resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    imagePick.setImageBitmap(imageBitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    baseImg = Base64.encodeToString(imageBytes, Base64.DEFAULT); //Or use Base64.DEFAULT
                }
            } else {
                //TODO: remove resultCode on release
                Toast.makeText(getActivity(), "You haven't picked up Image: " + resultCode, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    private void addStock() {
        JSONObject reqParams = new JSONObject();
        try {
            reqParams.put("email", userDataJson.getString("email"));
            reqParams.put("userId", userDataJson.getString("userId"));
            reqParams.put("veg_name", vegName);
            reqParams.put("total_stock", totalStockString);
            reqParams.put("description", description);
            reqParams.put("base64Str", baseImg);

            JSONArray priceArray = new JSONArray();
            JSONObject priceSingleObject = new JSONObject();
            priceSingleObject.put("weight", gram1String);
            priceSingleObject.put("price", price1String);
            priceArray.put(priceSingleObject);

            reqParams.put("price", priceArray);
            Log.d(TAG, "addStock: Request Data: " + reqParams);

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
                    mDialog.dismiss();
                    error.printStackTrace();
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
        vegName = vegNameAutoComplete.getText().toString().trim();
        totalStockString = totalStockField.getText().toString().trim();
        gram1String = gram1Field.getText().toString().trim();
        price1String = price1Field.getText().toString().trim();
        description = descriptionField.getText().toString().trim();

        Log.d("value of", Arrays.toString(texts));

        if (!vegName.isEmpty()) {
            for (String text : texts) {
                if (vegName.equals(text)) {
                    vegIsValid = true;
                    break;
                }
            }
        }
        if (!vegIsValid) {
            vegNameAutoComplete.setError("Enter Vegetable Name");
            vegNameAutoComplete.requestFocus();
        } else if (totalStockString.isEmpty()) {
            totalStockField.setError("Enter Total Stock");
            totalStockField.requestFocus();
        } else if (description.isEmpty()) {
            descriptionField.setError("Enter Description");
            descriptionField.requestFocus();
        } else if (gram1String.isEmpty()) {
            gram1Field.setError("Enter Grams");
            gram1Field.requestFocus();
        } else if (price1String.isEmpty()) {
            price1Field.setError("Enter Price");
            price1Field.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }
}
