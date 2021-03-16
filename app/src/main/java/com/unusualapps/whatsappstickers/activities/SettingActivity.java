package com.unusualapps.whatsappstickers.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.unusualapps.whatsappstickers.R;

import doubled.rate.RateDialog;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btnBack;
    private ConstraintLayout btnRate;
    private ConstraintLayout btnFeedback;
    private ConstraintLayout btnShareApp;
    private ConstraintLayout btnPrivacy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnBack = findViewById(R.id.btnBack);
        btnRate = findViewById(R.id.btnRate);
        btnFeedback = findViewById(R.id.btnFeedback);
        btnShareApp = findViewById(R.id.btnShareApp);
        btnPrivacy = findViewById(R.id.btnPrivacy);

        btnBack.setOnClickListener(this);
        btnRate.setOnClickListener(this);
        btnFeedback.setOnClickListener(this);
        btnShareApp.setOnClickListener(this);
        btnPrivacy.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnRate:
                rateApp(false);
                break;
            case R.id.btnFeedback:
                feedbackApp();
                break;
            case R.id.btnPrivacy:
                policyApp();
                break;
            case R.id.btnShareApp:
                shareApp();
                break;
        }
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    private void policyApp() {
        Uri uri = Uri.parse(getString(R.string.policy_link));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void feedbackApp() {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_feedback)});
        Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback App: " + getString(R.string.app_name));
        Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "");
        startActivity(Intent.createChooser(Email, "Send Feedback:"));
    }

    private void rateApp(boolean b) {
        RateDialog dialog = new RateDialog(this);
        if (!dialog.isRate()) {
            dialog.show(b);
        } else if (b) {
            finish();
        }

    }
}