package com.project.hawfarmbusiness;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "LogInActivity";

    Button linkSignUp;
    TextView linkForgotPassword;
    Button LogInButton;
    EditText email_field, password_field;
    String email, pass;
    SharedPreferences mPreferences;
    ProgressDialog mDialog;
    String notificationToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linkSignUp = findViewById(R.id.link_signup);
        linkForgotPassword = findViewById(R.id.forgot_pass);
        LogInButton = findViewById(R.id.btn_login);
        email_field = findViewById(R.id.input_email);
        password_field = findViewById(R.id.input_password);

        generateFCMToken();
        // shared preferences
        mPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        checkSharedPreferences();

        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, ForgotPasswordActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidData()) {
                    mDialog = new ProgressDialog(LogInActivity.this);
                    mDialog.setMessage("Please Wait..");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    userLogin();
                }
            }
        });
        setAnimation();
        changeStatusBarColor();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    private void setAnimation() {
        CardView loginCardView = findViewById(R.id.login_CardView);
        Animation fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        loginCardView.setAnimation(fromBottom);

        ImageView logoImageView = findViewById(R.id.company_logo);
        Animation fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        logoImageView.setAnimation(fromtop);
    }

    private boolean getValidData() {
        boolean valid = false;
        email = email_field.getText().toString().trim();
        pass = password_field.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.isEmpty() || !email.matches(emailPattern)) {
            email_field.setError("Enter Valid E-mail");
            email_field.requestFocus();
        } else if (pass.isEmpty() || pass.length() < 7) {
            password_field.setError("Enter Valid Password");
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
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("responseSuccess");
                            if (success.equals("true")) {
                                JSONObject data = new JSONObject(jsonObject.getString("data"));
                                Log.d(TAG, "data: " + data);
                                if (data.getString("user_type").equals("farmer") || data.getString("user_type").equals("hawker")) {
                                    savePreferences(data.toString());
                                    mDialog.dismiss();
                                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                                    intent.putExtra("userData", data.toString());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    mDialog.dismiss();
                                    Toast.makeText(LogInActivity.this,
                                            "You Doesn't have an access for this App.", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                String data = jsonObject.getString("data");
                                mDialog.dismiss();
                                Toast.makeText(LogInActivity.this, data, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LogInActivity.this,
                        "Something is Wrong! Please try again.", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", pass);
                params.put("notificationToken", notificationToken);
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

    private void generateFCMToken() {
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("Token", token);
                        notificationToken = task.getResult().getToken();
                    }
                });
        // [END retrieve_current_token]
    }
}