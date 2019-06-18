package com.project.hawfarmbusiness;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    EditText nameField, emailField, passField, cpassField, numberField, pinCodeField, addressField;
    Button createAccount;
    String name, email, password, cPassword, number, pinCode, address, user_type;
    Spinner userSpinner;

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

        userSpinner = findViewById(R.id.user_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);

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

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidData()) {
                    submitData();
                }
            }
        });
    }

    private void submitData() {

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
