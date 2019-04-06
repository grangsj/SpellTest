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

    //Class variables
    private static final int SPLASH_SCREEN_DURATION = 1000;  //Duration of splash screen (in milliseconds).

    //Instantiate a Handler and Runnable object.  These are used to automatically start the next activity
    //(ie the UserActivity) after a brief pause after app startup.
    private Handler mHandler = new Handler();
    private Runnable mTimer = new Runnable(){
        @Override
        //When fired, this Runnable fires an intent to show the next screen and kill this activity.
        public void run() {

            //Create a new Intent to start the UserActivity.
            Intent i = new Intent(SplashScreenActivity.this, UserSelectionActivity.class);

            //Start the activity.
            startActivity(i);

            finish();    //Prevents user from coming back to this screen via back button
        }
    };



    //Called when

    /**
     * Method called when the Splash screen is first displayed.  Note that the splash screen is built
     * as a theme, so we don't need a layout here - just going to show the theme.
     * @param savedInstanceState is the Bundle for any saved state info (not used in this activity.)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Call the super class constructor.
        super.onCreate(savedInstanceState);

        //Wait for defined time, then issue a Runnable to shut down the splash screen.
        mHandler.postDelayed(mTimer, SPLASH_SCREEN_DURATION);
    }



    /**
     * Called when user leaves splash screen by hitting the back button.  Added this method to shut
     * down the Runnable, otherwise the Runnable will show the next screen even though
     * the user asked to leave program.
     */
    @Override
    protected void onPause() {

        //Removes any Runnable instances in the Handler (ie, if the timer is still running!)
        mHandler.removeCallbacks(mTimer);

        //Call the Super class constructor.
        super.onPause();
    }
}
