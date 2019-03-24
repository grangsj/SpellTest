package com.example.SpellTest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class TestStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_stats);

        Button listChooserButton = findViewById(R.id.teststats_button_list_chooser);
        listChooserButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestStatsActivity.this, ListSelectionActivity.class);
                startActivity(intent);
            }
        });
    }
}
