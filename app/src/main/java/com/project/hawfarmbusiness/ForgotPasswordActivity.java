package com.project.hawfarmbusiness;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setAnimation();
    }

    private void setAnimation() {
        CardView forgotPassword=findViewById(R.id.forgot_pass_cardview);
        Animation fromBottom=AnimationUtils.loadAnimation(this,R.anim.frombottom);
        forgotPassword.setAnimation(fromBottom);

        ImageView logoImageView=findViewById(R.id.company_logo);
        Animation fromtop=AnimationUtils.loadAnimation(this,R.anim.fromtop);
        logoImageView.setAnimation(fromtop);
    }

}
