package com.project.hawfarmbusiness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "LogInActivity";

    TextView linkSignUp;
    TextView linkForgotPassword;
    Button LogIn;
    EditText email_field, password_field;
    String email, pass;
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linkSignUp = findViewById(R.id.link_signup);
        linkForgotPassword = findViewById(R.id.forgot_pass);
        LogIn = findViewById(R.id.btn_login);
        email_field = findViewById(R.id.input_email);
        password_field = findViewById(R.id.input_password);

        // shared preferences
        mPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        checkSharedPreferences();

        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, ForgotPasswordActivity.class));
            }
        });

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        });

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidData()) {
                    userLogin();
                }
            }
        });

    }

    private boolean getValidData() {
        boolean valid = false;
        email = email_field.getText().toString().trim();
        pass = password_field.getText().toString().trim();


        if (email.isEmpty()) {
            email_field.setError("Enter E-mail");
            email_field.requestFocus();
        } else if (pass.isEmpty()) {
            password_field.setError("Enter Password");
            password_field.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }

    private void userLogin() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerData.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d(TAG, "in Responce");
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("responseSuccess");
                            if (success.equals("true")) {
                                String data = jsonObject.getString("data");
                                Log.d(TAG, "data: " + data);
                                JSONArray jsonArray = new JSONArray(data);
                                JSONObject userJsonData = new JSONObject(jsonArray.getString(0));
                                savePreferences(data);
                                Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                                intent.putExtra("userData", data);
                                startActivity(intent);
                                finish();
                            } else {
                                String data = jsonObject.getString("data");
                                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                        data, Snackbar.LENGTH_INDEFINITE).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                        "Something is Wrong! Please try again.", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(LogInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", pass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LogInActivity.this);
        requestQueue.add(stringRequest);
    }

    private void savePreferences(String data) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("userDataStringKey", data);
        editor.apply();
    }

    private void checkSharedPreferences() {

        if (mPreferences.contains("userDataStringKey")) {
            String userDataString = mPreferences.getString("userDataStringKey", "");
            Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
            intent.putExtra("userData", userDataString);
            startActivity(intent);
            finish();
        }
    }

}
