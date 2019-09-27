package com.project.hawfarmbusiness;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    EditText nameField, emailField, passField, cpassField, numberField, pinCodeField, addressField;
    Button createAccount;
    String name, email, password, cPassword, number, pinCode, address, user_type, baseImg = "";
    Spinner userSpinner;
    Button LinkLogin_btn;
    ProgressDialog mDialog;
    ImageView profileField;

    @SuppressLint("WrongThread")
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
        profileField = findViewById(R.id.input_profile_submit);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);

        profileField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });

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
                    mDialog.setMessage("Creating Account...");
                    mDialog.setCanceledOnTouchOutside(false);
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
        CardView SignupCardview = findViewById(R.id.sign_up_card_view);
        Animation fromBottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        SignupCardview.setAnimation(fromBottom);
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
                                                finish();
                                            }
                                        }).show();
                            } else {
                                mDialog.dismiss();
                                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content),
                                        data, Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Log In", new View.OnClickListener() {
                                            public void onClick(View v) {
                                                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                                                finish();
                                            }
                                        }).show();
                            }
                        } catch (Exception e) {
                            mDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ");
                        mDialog.dismiss();
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
                params.put("user_type", user_type);
                params.put("address", address);
                params.put("pincode", pinCode);
                params.put("base64Str", baseImg);
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
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (name.isEmpty()) {
            nameField.setError("Enter Name");
            nameField.requestFocus();
        } else if (email.isEmpty() || !email.matches(emailPattern)) {
            emailField.setError("Enter valid Email!");
            emailField.requestFocus();
        } else if (password.isEmpty() || password.length() < 8) {
            passField.setError("Password must be 8 char long!");
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
        } else if (pinCode.length() != 6) {
            pinCodeField.setError("Enter Valid Pincode!");
            pinCodeField.requestFocus();
        } else {
            valid = true;
        }
        return valid;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        try {
            // When an Image is picked
            if (requestCode == 100 && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                File file = new File(picturePath);
                long length = file.length() / 1024; // Size in KB
                cursor.close();
                Bitmap bitmapForProfile = BitmapFactory.decodeFile(picturePath);

               // int origWidth = bitmapForProfile.getWidth();
               // int origHeight = bitmapForProfile.getHeight();
               // int bitmapSize = (origHeight * origWidth) * 1;
               int  bitmapSize = bitmapForProfile.getByteCount();
                Log.d("Original", "onActivityResult: " + bitmapSize + " | " + length);
                final int idelSize = 500;//or the width you need

                if (idelSize<length) {
                    Toast.makeText(this, "Image is larger than 500kb", Toast.LENGTH_LONG).show();
                } else {
                    profileField.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    //Base-64
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmapForProfile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    baseImg = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                }

            } else {
                Toast.makeText(SignUpActivity.this, "You have't pick an image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
