package com.transo.facerecognition.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.transo.facerecognition.R;
import com.transo.facerecognitionlibrary.Helpers.FileHelper;

import java.io.File;

public class MainActivity extends Activity {

    private Button callSettings, callAddPerson, callTraining, callTest, callRecognition;
    private FileHelper fh = null;
    public int RUNTIME_PERMISSIONS_REQUEST = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String training = intent.getStringExtra("training");
        if (training != null && !training.isEmpty()) {
            Toast.makeText(getApplicationContext(), training, Toast.LENGTH_SHORT).show();
            intent.removeExtra("training");
        }

        double accuracy = intent.getDoubleExtra("accuracy", 0);
        if (accuracy != 0) {
            Toast.makeText(getApplicationContext(), "The accuracy was " + accuracy * 100 + " %", Toast.LENGTH_LONG).show();
            intent.removeExtra("accuracy");
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        fh = new FileHelper();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, RUNTIME_PERMISSIONS_REQUEST);

            } else {
                initlize();
                declare();
            }
        } else {
            initlize();
            declare();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RUNTIME_PERMISSIONS_REQUEST) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                initlize();
                declare();
            }
        }
    }

    private void initlize() {
        callSettings = findViewById(R.id.button_settings);
        callAddPerson = findViewById(R.id.button_addPerson);
        callTraining = findViewById(R.id.button_recognition_training);
        callTest = findViewById(R.id.button_recognition_test);
        callRecognition = findViewById(R.id.button_recognition_view);
    }

    private void declare() {
        callSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SettingsActivity.class));
            }
        });

        callAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), AddPersonActivity.class), 100);
            }
        });

        if (fh.getTrainingList().length == 0) callTraining.setEnabled(false);
        callTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), TrainingActivity.class), 101);
            }
        });

        if (fh.getTestList().length == 0 || !((new File(fh.DATA_PATH)).exists()))
            callTest.setEnabled(false);
        callTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), TestActivity.class));
            }
        });

        if (!((new File(fh.DATA_PATH)).exists())) callRecognition.setEnabled(false);
        callRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(v.getContext(), RecognitionActivity.class), 102);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            declare();
        }
    }
}
