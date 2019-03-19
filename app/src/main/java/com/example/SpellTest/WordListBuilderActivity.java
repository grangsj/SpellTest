package com.example.SpellTest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class WordListBuilderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_builder);

        Button listChooserButton = findViewById(R.id.list_builder_button_list_chooser);
        listChooserButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WordListBuilderActivity.this, WordListChooserActivity.class);
                startActivity(intent);
                finish();    //Prevents user from coming back to this screen via back button
            }
        });
    }
}
