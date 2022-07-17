package com.example.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


import java.util.concurrent.Executor;

public class FingerPrintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);

        TextView msgtex = findViewById(R.id.msgtext);
        final Button loginbutton = findViewById(R.id.login);
        SharedPreferences appSettingsPreferences = getSharedPreferences("FingerPrint", 0);
        boolean fingerPrintAuth = appSettingsPreferences.getBoolean("FingerPrintAuth", false);
        Log.i("finger", String.valueOf(fingerPrintAuth));

        if(fingerPrintAuth) {
            //check if user can use biometric sensor or not
            BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
            switch (biometricManager.canAuthenticate()) {

                case BiometricManager.BIOMETRIC_SUCCESS:
                    msgtex.setText("You can use the fingerprint sensor to login");
                    msgtex.setTextColor(Color.parseColor("#fafafa"));
                    break;

                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    msgtex.setText("This device doesnot have a fingerprint sensor");
                    loginbutton.setVisibility(View.GONE);
                    break;

                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    msgtex.setText("The biometric sensor is currently unavailable");
                    loginbutton.setVisibility(View.GONE);
                    break;

                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    msgtex.setText("Your device doesn't have fingerprint saved,please check your security settings");
                    loginbutton.setVisibility(View.GONE);
                    break;
            }

            Executor executor = ContextCompat.getMainExecutor(this);
            // result of AUTHENTICATION
            final BiometricPrompt biometricPrompt = new BiometricPrompt(FingerPrintActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                }

                // On AUTHENTICATION SUCCESS
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                    loginbutton.setText("Login Successful");

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
            });
            // BIOMETRIC DIALOG
            final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Notes")
                    .setDescription("Use your fingerprint to login ").setNegativeButtonText("Cancel").build();
            loginbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    biometricPrompt.authenticate(promptInfo);

                }
            });
        }else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
