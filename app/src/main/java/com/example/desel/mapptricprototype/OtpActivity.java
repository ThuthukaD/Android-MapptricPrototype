package com.example.desel.mapptricprototype;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class OtpActivity extends AppCompatActivity
{
    // VARIABLES

    // Edit Texts
    EditText etMin;
    EditText etMax;
    EditText etVerifyNumber;
    EditText etOtp;

    // Text Views
    TextView tvOutput;
    TextView tvResend;

    // Buttons
    Button btnVerify;

    // Other
    Random r;
    int min;
    int max;
    int output;
    private final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "OtpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Log.d(TAG, "onCreate: Otp - Starting");

        // INSTANCING
        // Other
        r = new Random();

        // Edit Texts
        etMin = findViewById(R.id.etMin);
        etMax = findViewById(R.id.etMax);
        etVerifyNumber = findViewById(R.id.etVerifyNumber);
        etOtp = findViewById(R.id.etOtp);

        // Text Views
        tvOutput = findViewById(R.id.tvOutput);
        tvResend = findViewById(R.id.tvResend);

        // Buttons
        btnVerify = findViewById(R.id.btnVerify);

        // ----------------------------------------------------------------------------
        // WORK
        min = Integer.parseInt(etMin.getText().toString());
        max = Integer.parseInt(etMax.getText().toString());

        output = r.nextInt((max - min) + 1) + min;

        tvOutput.setText("" + output);

        // Calling text method
        try
        {
            Log.d(TAG, "onCreate: Otp - Getting Text");
            Text();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        btnVerify.setEnabled(false);

        // SMS Permissions check
        if (checkPermission(Manifest.permission.SEND_SMS))
        {
            try
            {
                Log.d(TAG, "onCreate: Otp - Button Set to enabled");
                btnVerify.setEnabled(true);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                btnVerify.setEnabled(true);
                Log.d(TAG, "onCreate: Otp - Error setting Button to enabled but still set");
            }
            Log.d(TAG, "onCreate: Otp - Permission check complete");
        }
        else
        {
            Log.d(TAG, "onCreate: Otp - Asking for permission");
            // Asking user for permission if not set
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        String number = etVerifyNumber.getText().toString();
        String otp = tvOutput.getText().toString();

        if (checkPermission(Manifest.permission.SEND_SMS))
        {
            Log.d(TAG, "onCreate: Otp - Sending SMS");
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null,
                    "Bank App : Your OTP Code is : " + otp + ".",
                    null, null);
        }
    }

    public void Text()
    {
        Log.d(TAG, "Text: Otp - Shared preferences for Text");
        // Calling prefs from RegisterActivity
        SharedPreferences prefs = getSharedPreferences("login", MODE_PRIVATE);

        // Passed string for phone number
        String phone = prefs.getString("phone", "PhoneNumber");

        // Setting the current fields to the stored text
        etVerifyNumber.setText(phone);
    }

    public void Verify(View view)
    {
        Log.d(TAG, "Verify: Otp - Verifying OTP");

        if (Integer.parseInt(etOtp.getText().toString().trim()) ==
                Integer.parseInt(tvOutput.getText().toString().trim()))
        {
            if (checkPermission(Manifest.permission.SEND_SMS))
            {
                String number = etVerifyNumber.getText().toString();
                String currentDateTimeString = DateFormat.getDateTimeInstance()
                        .format(new Date());

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null,
                        "Bank App : Your account was created on : " +
                                currentDateTimeString + ".",
                        null, null);
            }

            Log.d(TAG, "Verify: Otp - Account created");

            Toast.makeText(this, "Account Successfully Created",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(OtpActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Log.d(TAG, "Verify: Otp - OTP did not match");
            Toast.makeText(this,
                    "OTP Code did not match, try again or quit?"
                    , Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkPermission(String permission)
    {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    public  void Resend(View view)
    {
        Log.d(TAG, "Resend: Otp - Resend button clicked");
        if (checkPermission(Manifest.permission.SEND_SMS))
        {
            String number = etVerifyNumber.getText().toString();
            String otp = tvOutput.getText().toString();

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null,
                    "Bank App : Your resent OTP Code is : " + otp + ".",
                    null, null);
        }
        Log.d(TAG, "Resend: OTP resent");
    }
}
