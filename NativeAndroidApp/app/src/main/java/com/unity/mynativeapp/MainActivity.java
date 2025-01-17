package com.unity.mynativeapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.unity.mynativeapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    boolean isUnityLoaded = false;
    private ActivityMainBinding binding = null;
    private Class<? extends Activity> unityActivityClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        handleIntent(getIntent());

        setUpClickListeners();
    }

    private void setUpClickListeners() {
        binding.content.showDefault.setOnClickListener(this::showDefault);
        binding.content.showOverlaid.setOnClickListener(this::showOverlaid);
        binding.content.showContained.setOnClickListener(this::showOverlaid);
        binding.content.unload.setOnClickListener(view -> unloadUnity(false));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        setIntent(intent);
    }

    void handleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) return;

        if (intent.getExtras().containsKey("setColor")) {
            View v = binding.content.unload;

            switch (intent.getExtras().getString("setColor")) {
                case "yellow":
                    v.setBackgroundColor(Color.YELLOW);
                    break;
                case "red":
                    v.setBackgroundColor(Color.RED);
                    break;
                case "blue":
                    v.setBackgroundColor(Color.BLUE);
                    break;
                default:
                    break;
            }
        }
    }

    private void showOverlaid(View v) {
        unityActivityClass = OverlaidActivity.class;
        goToUnityActivity();
    }

    private void showContained(View v) {
        unityActivityClass = ContainedActivity.class;
        goToUnityActivity();
    }

    private void showDefault(View v) {
        unityActivityClass = MainUnityActivity.class;
        goToUnityActivity();
    }

    private void goToUnityActivity() {
        isUnityLoaded = true;
        Intent intent = new Intent(this, unityActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) isUnityLoaded = false;
    }

    public void unloadUnity(Boolean doShowToast) {
        if (isUnityLoaded) {
            Intent intent = new Intent(this, unityActivityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("doQuit", true);
            startActivity(intent);
            isUnityLoaded = false;
        } else if (doShowToast)
            Toast.makeText(getApplicationContext(), "Show Unity First", Toast.LENGTH_SHORT).show();
    }

    public void btnUnloadUnity(View v) {
        unloadUnity(true);
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
