package com.example.SpellTest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class UserStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);


        Button userSelectionButton = findViewById(R.id.user_stats_button_user_selection);
        userSelectionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserStatsActivity.this, UserSelectionActivity.class);
                startActivity(intent);
                finish();    //Prevents user from coming back to this screen via back button
            }
        });
    }
}
