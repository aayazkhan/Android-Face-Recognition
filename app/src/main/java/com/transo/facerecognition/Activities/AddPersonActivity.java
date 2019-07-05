package com.transo.facerecognition.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.transo.facerecognition.R;
import com.transo.facerecognitionlibrary.Helpers.FileHelper;

import java.io.File;

public class AddPersonActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        final ToggleButton btnTrainingTest = (ToggleButton) findViewById(R.id.btnTrainingTest);
        final ToggleButton btnReferenceDeviation = (ToggleButton) findViewById(R.id.btnReferenceDeviation);
        final ToggleButton btnTimeManually = (ToggleButton) findViewById(R.id.btnTimeManually);
        btnTrainingTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnTrainingTest.isChecked()) {
                    btnReferenceDeviation.setEnabled(true);
                } else {
                    btnReferenceDeviation.setEnabled(false);
                }
            }
        });

        Button btn_Start = (Button) findViewById(R.id.btn_Start);
        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txt_Name = (EditText) findViewById(R.id.txt_Name);
                String name = txt_Name.getText().toString();
                Intent intent = new Intent(v.getContext(), AddPersonPreviewActivity.class);
                intent.putExtra("Name", name);

                if (btnTimeManually.isChecked()) {
                    intent.putExtra("Method", AddPersonPreviewActivity.MANUALLY);
                } else {
                    intent.putExtra("Method", AddPersonPreviewActivity.TIME);
                }

                if (btnTrainingTest.isChecked()) {
                    // Add photos to "Test" folder
                    if (isNameAlreadyUsed(new FileHelper().getTestList(), name)) {
                        Toast.makeText(getApplicationContext(), "This name is already used. Please choose another one.", Toast.LENGTH_SHORT).show();
                    } else {
                        intent.putExtra("Folder", "Test");
                        if (btnReferenceDeviation.isChecked()) {
                            intent.putExtra("Subfolder", "deviation");
                        } else {
                            intent.putExtra("Subfolder", "reference");
                        }
                        startActivityForResult(intent, 100);
                    }
                } else {
                    // Add photos to "Training" folder

                    if (isNameAlreadyUsed(new FileHelper().getTrainingList(), name)) {
                        Toast.makeText(getApplicationContext(), "This name is already used. Please choose another one.", Toast.LENGTH_SHORT).show();
                    } else {
                        intent.putExtra("Folder", "Training");
                        startActivityForResult(intent, 100);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean isNameAlreadyUsed(File[] list, String name) {
        boolean used = false;
        if (list != null && list.length > 0) {
            for (File person : list) {
                // The last token is the name --> Folder name = Person name
                String[] tokens = person.getAbsolutePath().split("/");
                final String foldername = tokens[tokens.length - 1];
                if (foldername.equals(name)) {
                    used = true;
                    break;
                }
            }
        }
        return used;
    }
}
