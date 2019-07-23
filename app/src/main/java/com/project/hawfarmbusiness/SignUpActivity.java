package com.project.hawfarmbusiness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText nameField, emailField, passField, cpassField, numberField, pinCodeField, addressField;
    Button createAccount;
    String name, email, password, cPassword, number, pinCode, address, user_type;
    Spinner userSpinner;
    TextView LinkLogin_btn;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameField = findViewById(R.id.input_name);
        emailField = findViewById(R.id.input_email);
        passField = findViewById(R.id.input_password);
        cpassField = findViewById(R.id.confirm_password);
        numberField = findViewById(R.id.mo_number);
        pinCodeField = findViewById(R.id.input_pincode);
        createAccount = findViewById(R.id.btn_signup);
        addressField = findViewById(R.id.input_address);
        LinkLogin_btn = findViewById(R.id.link_login);
        userSpinner = findViewById(R.id.user_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);

        LinkLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getValidData()) {
                    mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setMessage("Creating Account..");
                    mDialog.show();
                    submitData();
                }
            }
        });

        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    user_type = null;
                } else if (position == 1) {
                    user_type = "farmer";
                } else if (position == 2) {
                    user_type = "hawker";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setAnimation();
    }

    private void setAnimation() {
        CardView SignuoCardview = findViewById(R.id.sign_up_card_view);
        Animation fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        SignuoCardview.setAnimation(fromBottom);

        ImageView logoImageView = findViewById(R.id.company_logo);
        Animation fromtop=AnimationUtils.loadAnimation(this,R.anim.fromtop);
        logoImageView.setAnimation(fromtop);
    }

    private void submitData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerData.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("data");
                            if (data.equals("ER_DUP_ENTRY")) {
                                mDialog.dismiss();
                                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                        "User Already Exists. Please try to LogIn", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Log In", new View.OnClickListener() {
                                            public void onClick(View v) {
                                                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                                            }
                                        }).show();
                            } else {
                                mDialog.dismiss();
                                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                        "Registration Successful " + data, Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Log In", new View.OnClickListener() {
                                            public void onClick(View v) {
                                                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                                            }
                                        }).show();
                            }
                            Toast.makeText(SignUpActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            mDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                "Something is Wrong! Please try again.", Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("phone", number);
                params.put("password", password);
                //TODO: Change the user type
                params.put("user_type", user_type);
                params.put("address", address);
                params.put("pincode", pinCode);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(stringRequest);
    }


    private boolean getValidData() {
        boolean valid = false;

        name = nameField.getText().toString();
        email = emailField.getText().toString();
        password = passField.getText().toString();
        cPassword = cpassField.getText().toString();
        number = numberField.getText().toString();
        pinCode = pinCodeField.getText().toString();
        address = addressField.getText().toString();

        if (name.isEmpty()) {
            nameField.setError("Enter Name");
            nameField.requestFocus();
        } else if (email.isEmpty()) {
            emailField.setError("Enter Email");
            emailField.requestFocus();
        } else if (password.isEmpty()) {
            passField.setError("Enter Password");
            passField.requestFocus();
        } else if (cPassword.isEmpty() || !password.equals(cPassword)) {
            cpassField.setError("Password are not match");
            cpassField.requestFocus();
        } else if (number.isEmpty() || number.length() != 10) {
            numberField.setError("Enter valid Mobile Number");
            numberField.requestFocus();
        } else if (user_type == null) {
            TextView errorView = (TextView) userSpinner.getSelectedView();
            errorView.setError("Select User");
            errorView.setTextColor(Color.RED);
            errorView.setText("Select User");
            errorView.requestFocus();
        } else if (address.isEmpty()) {
            addressField.setError("Enter Address");
            addressField.requestFocus();
        } else if (pinCode.isEmpty()) {
            pinCodeField.setError("Enter Pincode");
            pinCodeField.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }
}
