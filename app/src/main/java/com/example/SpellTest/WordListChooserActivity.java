package com.example.SpellTest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class WordListChooserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_chooser);

        Button userSelectionButton = findViewById(R.id.list_chooser_button_user_selection);
        Button listBuilderButton = findViewById(R.id.list_chooser_button_list_builder);
        Button testerButton = findViewById(R.id.list_chooser_button_start_test);

        userSelectionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordListChooserActivity.this, UserSelectionActivity.class);
                startActivity(intent);
            }
        });

       listBuilderButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordListChooserActivity.this, WordListBuilderActivity.class);
                startActivity(intent);
            }
        });

        testerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordListChooserActivity.this, TesterActivity.class);
                startActivity(intent);
                finish();    //Prevents user from coming back to this screen via back button
            }
        });
    }

}
