package com.project.hawfarmbusiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LogInActivity extends AppCompatActivity {
 Button log_in;
 TextView linkSignUp;
 TextView linkForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linkSignUp = (TextView)findViewById(R.id.link_signup);
        linkForgotPassword = (TextView)findViewById(R.id.forgot_pass);

        linkForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, forgotPasswordActivity.class));

            }
        });

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        });
    }
}
