package com.example.SpellTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class UserSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        Button listChooserButton = findViewById(R.id.user_selection_button_list_chooser);
        Button userStatsButton = findViewById(R.id.user_selection_button_user_stats);
        Button newUserDialogButton = findViewById(R.id.user_selection_button_user_creation);

        listChooserButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSelectionActivity.this, WordListChooserActivity.class);
                startActivity(intent);
            }
        });

        userStatsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSelectionActivity.this, UserStatsActivity.class);
                startActivity(intent);
            }
        });

        newUserDialogButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //TO DO:  CODE FOR DIALOG BUTTON
            }
        });

    }

}
