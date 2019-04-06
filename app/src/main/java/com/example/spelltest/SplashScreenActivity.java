/**
 * Filename:  SplashScreenActivity.java
 * Author:  Team SpellTest
 * Date:  05 April 2019
 *
 * Purpose:  This class represents the splash screen graphic for the application.  It appears on
 * initial application startup and remains there for a short period of time before sending
 * application control to the UserSelectionActivity class.
 *
 */


package com.example.spelltest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    //Timer variables - used to shut down splash screen when done.
    private Handler handler = new Handler();
    private Runnable timer = new Runnable(){
        @Override
        //When fired, this Runnable fires an intent to show the next screen and kill this activity.
        public void run() {
            Intent i = new Intent(SplashScreenActivity.this, UserSelectionActivity.class);
            startActivity(i);
            finish();    //Prevents user from coming back to this screen via back button
        }
    };

    private static final int SPLASH_SCREEN_DURATION = 10000;  //Duration of splash screen (in milliseconds).

    //Called when Splash screen is first displayed.  Note that the splash screen is built as a theme, so
    //we don't need a layout here - just going to show the theme.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Wait for defined time, then issue a Runnable to shut down the splash screen.
        handler.postDelayed(timer, SPLASH_SCREEN_DURATION);
    }



    /**
     * Called when user leaves splash screen.  Added this method to shut down the Runnable if the user hits the
     * back button while the splash screen is on - otherwise the Runnable will show the next screen even though
     * the user asked to leave program.
     */
    @Override
    protected void onPause() {

        //Removes any Runnable instances in the Handler (ie, if the timer is still running!)
        handler.removeCallbacks(timer);
        super.onPause();
    }
}
