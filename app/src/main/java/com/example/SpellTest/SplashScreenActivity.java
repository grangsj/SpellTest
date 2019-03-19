package com.example.SpellTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button forwardButton = this.findViewById(R.id.splash_button_user_selection);
        forwardButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(SplashScreenActivity.this, UserSelectionActivity.class);
                startActivity(i);
                finish();    //Prevents user from coming back to this screen via back button
            }
        });
    }
}
