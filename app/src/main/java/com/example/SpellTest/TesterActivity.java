package com.example.SpellTest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class TesterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);

        Button statsButton = findViewById(R.id.tester_button_test_stats);
        statsButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent i = new Intent(TesterActivity.this, TestStatsActivity.class);
                startActivity(i);
                finish();    //Prevents user from coming back to this screen via back button
            }
        });
    }
}
