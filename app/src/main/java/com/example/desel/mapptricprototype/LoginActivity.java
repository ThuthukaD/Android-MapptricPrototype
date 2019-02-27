package com.example.desel.mapptricprototype;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity
{
    // VARIABLES

    // Text View
    TextView tvUser;
    TextView tvEmail;
    TextView tvSwitchUser;

    // Edit Texts
    EditText etPin;
    EditText etEmail;
    EditText etLoginNumber;
    EditText etLoginNotification;

    // Buttons
    Button btnLogin;

    // Strings
    String stLogin;
    String stEmail;

    // Other
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Login - Started");

        // INSTANCING

        // Text Views
        tvUser = findViewById(R.id.tvUser);
//        tvEmail = findViewById(R.id.tvEmail);
        tvSwitchUser = findViewById(R.id.tvSwitchUser);

        // Edit Texts
        etEmail = findViewById(R.id.etEmail);
        etPin = findViewById(R.id.etPin);
        etLoginNumber = findViewById(R.id.etLoginNumber);
        etLoginNotification = findViewById(R.id.etLoginNotification);

        // Buttons
        btnLogin = findViewById(R.id.btnLogin);

        // Other
        mAuth = FirebaseAuth.getInstance();

        // ---------------------------------------------------------------------------------
        // WORK

        // Calling text method
        try
        {
            Log.d(TAG, "onCreate: Login - Getting Text");
            Text();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.d(TAG, "onCreate: Login - Failed to get text");
        }

        btnLogin.setEnabled(false);

        Log.d(TAG, "onCreate: Login - SMS permissions check");
        // SMS Permissions check
        if (checkPermission(Manifest.permission.SEND_SMS))
        {
            try
            {
                Log.d(TAG, "onCreate: Login - Button Set to enabled");
                btnLogin.setEnabled(true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                btnLogin.setEnabled(true);
                Log.d(TAG, "onCreate: Login - Error setting Button to enabled but still set");
            }
            Log.d(TAG, "onCreate: Login - Permission check complete");
        }
        else
        {
            // Asking user for permission if not set
            ActivityCompat.requestPermissions(this,
                    new String [] {Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }
        Log.d(TAG, "onCreate: Login - Login Page loaded");
    }

    // Login Method
    public void Login(View view)
    {
        Log.d(TAG, "Login: Login - Button Clicked");
        String email = etEmail.getText().toString().trim();
        String pin = etPin.getText().toString().trim();

        // Checks if email and password is empty
        Log.d(TAG, "Login: Login - Checking Details");
        if (TextUtils.isEmpty(email))
        {
            Log.d(TAG, "Login: Login - Account Error");
            Toast.makeText(this, "Account Error, account not found",
                    Toast.LENGTH_LONG).show();
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pin))
        {
            Log.d(TAG, "Login: Login - Pin Not Detected");
            Toast.makeText(this, "Please enter your pin",
                    Toast.LENGTH_SHORT).show();
            etPin.requestFocus();

            // Notification
            return;
        }

        // Constraint for password type or length
        if (pin.length() < 6)
        {
            Log.d(TAG, "Login: Login - Pin Requirements Not Met");
            Toast.makeText(this, "Pin must be 6 digits",
                    Toast.LENGTH_SHORT).show();
            etPin.requestFocus();
            return;
        }

        // Sign in allowed if all is good
        mAuth.signInWithEmailAndPassword(email,pin).addOnCompleteListener(this, new
                OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "onComplete: Login - Access Granted");

                            // Passing text to main screen for the user text view
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);

                            Log.d(TAG, "onComplete: Login - Copying Text To Relevant Page");
                            // Initialising Keys
                            stLogin = tvUser.getText().toString();
                            intent.putExtra("UserLogin", stLogin);

//                            stEmail = tvEmail.getText().toString();
//                            intent.putExtra("EmailLogin", stEmail);

                            String number = etLoginNumber.getText().toString();
                            String currentDateTimeString = DateFormat.getDateTimeInstance()
                                    .format(new Date());

                            if  (checkPermission(Manifest.permission.SEND_SMS))
                            {
                                Log.d(TAG, "onComplete: Login - Checking Manifest Permissions");
                                SmsManager smsManager = SmsManager.getDefault();

                                smsManager.sendTextMessage(number, null,
                                        "Bank App : Account login " +
                                                "On: " + currentDateTimeString + ". Is this you?",
                                        null, null);

                                Toast.makeText(LoginActivity.this,
                                        "Logged In", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Log.d(TAG, "onComplete: Login - Checking Manifest Permissions");
                                SmsManager smsManager = SmsManager.getDefault();

                                smsManager.sendTextMessage(number, null,
                                        "Bank App : Account login failed " +
                                                "On: " + currentDateTimeString + ". Is this you?",
                                        null, null);

                                Toast.makeText(LoginActivity.this,
                                        "Permission Denied", Toast.LENGTH_SHORT).show();
                            }
                            startActivity(intent);

                            Log.d(TAG, "onComplete: Login - Clearing Text Fields");
                            emptyInputs();
                            //finish();
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: Login - Access Declined");

                            String number = etLoginNumber.getText().toString();
                            String currentDateTimeString = DateFormat.getDateTimeInstance()
                                    .format(new Date());

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(number, null,
                                    "Bank App : Account login failed " +
                                            "On: " + currentDateTimeString + ". Is this you?",
                                    null, null);

                            emptyInputs();
                            Toast.makeText(LoginActivity.this,
                                    "Unexpected Error, pleas try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Get text method
    public void Text()
    {
        Log.d(TAG, "Text: Login - Attempting to write Text");
        // Calling prefs from RegisterActivity
        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);

        // Passed string for user
        String username = prefs.getString("username", "Username");

        // Passed string for email
        String email = prefs.getString("email", "Email");

        // Passed string for phone number
        String phone = prefs.getString("phone", "PhoneNumber");

        // Setting the current fields to the stored text
        tvUser.setText(username);
        etEmail.setText(email);
        etLoginNumber.setText(phone);
    }

    // Empties pin field
    private void emptyInputs()
    {
        Log.d(TAG, "emptyInputs: Login - Clearing PIN Field");
        etPin.setText(null);
    }

    // Open Register Page Method
    public void SwitchUser(View view)
    {
        Log.d(TAG, "SwitchUser: Login - Opening Register Page");

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean checkPermission(String permission)
    {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}