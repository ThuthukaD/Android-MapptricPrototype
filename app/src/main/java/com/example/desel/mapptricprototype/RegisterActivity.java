package com.example.desel.mapptricprototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity
{
    // VARIABLES

    // Edit Texts
    EditText etUser;
    EditText etEmail;
    EditText etPin;
    EditText etConPin;
    EditText etNumber;

    // Buttons
    Button btnRegister;

    // TextViews
    TextView tvLogin;

    // Other
    private FirebaseAuth mAuth;
    SharedPreferences sp;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: Register - Starting");

        // INSTANCING

        // Edit Texts
        etUser = findViewById(R.id.etUser);
        etEmail = findViewById(R.id.etEmail);
        etPin = findViewById(R.id.etPin);
        etConPin = findViewById(R.id.etConPin);
        etNumber = findViewById(R.id.etNumber);

        // Buttons
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Other
        mAuth = FirebaseAuth.getInstance();
        sp = getSharedPreferences("login",MODE_PRIVATE);
    }

    public void Register (View view)
    {
        Log.d(TAG, "Register: Register - Button Clicked");

        // Instance Declaration
        String user = etUser.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pin = etPin.getText().toString().trim();
        String conPin = etConPin.getText().toString().trim();
        String number = etNumber.getText().toString().trim();

        Log.d(TAG, "Register: Register - Checking Credentials");
        // Checks if email and pin is empty
        if (TextUtils.isEmpty(user) && TextUtils.isEmpty(email)
                && TextUtils.isEmpty(pin) && TextUtils.isEmpty(conPin) &&
                TextUtils.isEmpty(number))
        {
            Log.d(TAG, "Register: Register - No Fields Detected");
            Toast.makeText(this, "Please fill in the required fields",
                    Toast.LENGTH_SHORT).show();

            // Need to keep the order reversed
            etNumber.requestFocus();
            etConPin.requestFocus();
            etPin.requestFocus();
            etEmail.requestFocus();
            etUser.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(user))
        {
            Log.d(TAG, "Register: Register - User Field Not Detected");
            Toast.makeText(this, "Please enter your new username",
                    Toast.LENGTH_LONG).show();
            etUser.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email))
        {
            Log.d(TAG, "Register: Register - Email Field Not Detected");
            Toast.makeText(this, "Please enter your new email address",
                    Toast.LENGTH_LONG).show();
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pin))
        {
            Log.d(TAG, "Register: Register - Pin Field Not Detected");
            Toast.makeText(this, "Please enter your new pin",
                    Toast.LENGTH_LONG).show();
            etPin.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(conPin))
        {
            Log.d(TAG, "Register: Register - ConPin Field Not Detected");
            Toast.makeText(this, "Please enter your confirmation pin",
                    Toast.LENGTH_LONG).show();
            etConPin.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(number))
        {
            Log.d(TAG, "Register: Register - Number Field Not Detected");
            Toast.makeText(this, "Please enter your phone number",
                    Toast.LENGTH_LONG).show();
            etNumber.requestFocus();
            return;
        }

        // Constraint for phone number length
        if (number.length() != 10)
        {
            Log.d(TAG, "Register: Register - Number Field Did Not Meet Requirements");
            Toast.makeText(this,
                    "Phone number must be 10 digits, REQUIRED for verification",
                    Toast.LENGTH_LONG).show();
            etNumber.requestFocus();
            return;
        }

        // Constraint for pin length
        if (pin.length() != 6 || conPin.length() != 6)
        {
            Log.d(TAG, "Register: Register - Pin Or ConPin Did Not Meet Requirements");
            Toast.makeText(this, "Pin and Confirmation Pin must be 6 digits",
                    Toast.LENGTH_LONG).show();
            etConPin.requestFocus();
            etPin.requestFocus();
            return;
        }

        // Checks if pin and conPin matches
        if (conPin.equals(pin))
        {
            mAuth.createUserWithEmailAndPassword(email, pin)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            Log.d(TAG, "onComplete: Register - Access Granted");
                            if (task.isSuccessful())
                            {
                                AutoLogin();
                                finish();
                                emptyInputs();
                            }
                            else
                            {
                                Log.d(TAG, "onComplete: Register - Access Declined");
                                emptyInputs();
                                Toast.makeText(RegisterActivity.this,
                                        "Authentication Failed, please try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Log.d(TAG, "Register: Register - Pin Or ConPin Did Not Match");

            etPin.setText(null);
            etConPin.setText(null);
            Toast.makeText(this, "Confirmation pin does not match pin",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void Login(View view)
    {
        Log.d(TAG, "Login: Register - Starting Login Activity");
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    // Empties the text fields
    private void emptyInputs()
    {
        Log.d(TAG, "emptyInputs: Register - Clearing Fields");
        etUser.setText(null);
        etEmail.setText(null);
        etPin.setText(null);
        etConPin.setText(null);
        etNumber.setText(null);
    }

    // Method uses prefs to send data to LoginActivity, and also auto "logs" in the user
    public void AutoLogin()
    {
        Log.d(TAG, "AutoLogin: Register - Setting up shared preferences and auto login");

        // Initialising strings to store user data
        String uname = etUser.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etNumber.getText().toString();

        // creating shared preferences database
        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // columns to store different values
        editor.putString("username", uname);
        editor.putString("email", email);
        editor.putString("phone", phone);

        // boolean to check if user created account before
        editor.putBoolean("isLogged", true);

        editor.apply();

        Intent intent = new Intent(RegisterActivity.this,
                OtpActivity.class);
        startActivity(intent);
    }
}
