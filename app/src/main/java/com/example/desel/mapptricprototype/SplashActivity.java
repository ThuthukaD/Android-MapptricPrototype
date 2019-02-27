package com.example.desel.mapptricprototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {
    // VARIABLES

    // Other
    public ProgressBar progressBar;
    public int progressStatus = 0;
    public Handler handler;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // TYPE CASTING
        progressBar = findViewById(R.id.progressBar);

        Log.d(TAG, "onCreate: Splash - Starting");

        // -----------------------------------------------------------------------------
        // WORK

        Log.d(TAG, "onCreate: Splash - ProgressBar Starting");
        // Progress Bar Code
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (progressStatus < 100)
                {
                    progressStatus++;
                    SystemClock.sleep(20);
                    progressBar.setProgress(progressStatus);
                }

                Log.d(TAG, "run: Splash - Auto login Starting");
                // Auto Login Code
                SharedPreferences prefs = getSharedPreferences("login",
                        MODE_PRIVATE);
                final Boolean isLogged = prefs.getBoolean("isLogged",
                        false);

                Thread timer = new Thread()
                {
                    public void run()
                    {
                        Log.d(TAG, "run: Splash - Timer Started");
                        try
                        {
                            sleep(10);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        finally
                        {
                            Log.d(TAG, "run: Splash - Timer Complete");
                            if (isLogged)
                            {
                                Log.d(TAG, "run: Splash - Starting loginActivity");
                                Intent intent = new Intent
                                        (SplashActivity.this,
                                                LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Log.d(TAG, "run: Splash - Closing splashActivity");
                            }
                            else
                            {
                                Log.d(TAG, "run: Splash - Starting RegisterActivity");
                                Intent intent = new Intent
                                        (SplashActivity.this,
                                                RegisterActivity.class);
                                startActivity(intent);
                                finish();
                                Log.d(TAG, "run: Splash - Closing splashActivity");
                            }
                        }
                        Log.d(TAG, "run: Splash - Autologin Complete");
                    }
                };
                timer.start();
            }
        }).start();
        Log.d(TAG, "onCreate: Splash Complete");
    }
}