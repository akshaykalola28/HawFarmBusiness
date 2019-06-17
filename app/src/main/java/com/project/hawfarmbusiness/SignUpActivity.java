package com.project.hawfarmbusiness;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    EditText nameField, emailField, passField, cpassField, numberField, pinCodeField, addressField;
    Button createAccount;
    String name, email, password, cPassword, number, pinCode, address;

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

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getValidData()) {

                }
            }
        });
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
