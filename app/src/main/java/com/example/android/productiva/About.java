package com.example.android.productiva;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class About extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button feedbackButton = findViewById(R.id.send_feedback);

        feedbackButton.setOnClickListener(v -> {
            sendFeedback();
        });
    }

    //This will let users send feedback to the developers
    private void sendFeedback() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tausifahmed0235@yahoo.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Productiva: Feedback");
            startActivity(Intent.createChooser(intent, "Send email using"));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email client installed on your device.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}