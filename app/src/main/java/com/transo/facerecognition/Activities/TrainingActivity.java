package com.transo.facerecognition.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.transo.facerecognition.R;
import com.transo.facerecognitionlibrary.Helpers.FileHelper;
import com.transo.facerecognitionlibrary.Helpers.MatName;
import com.transo.facerecognitionlibrary.Helpers.PreferencesHelper;
import com.transo.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import com.transo.facerecognitionlibrary.Recognition.Recognition;
import com.transo.facerecognitionlibrary.Recognition.RecognitionFactory;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

public class TrainingActivity extends Activity {
    private static final String TAG = "Training";
    TextView progress;
    Thread thread;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        progress = (TextView) findViewById(R.id.progressText);
        progress.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onResume() {
        super.onResume();

        final Handler handler = new Handler(Looper.getMainLooper());
        thread = new Thread(new Runnable() {
            public void run() {
                if (!Thread.currentThread().isInterrupted()) {
                    PreProcessorFactory ppF = new PreProcessorFactory(getApplicationContext());
                    PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
                    String algorithm = preferencesHelper.getClassificationMethod();

                    FileHelper fileHelper = new FileHelper();
                    fileHelper.createDataFolderIfNotExsiting();
                    final File[] persons = fileHelper.getTrainingList();
                    if (persons.length > 0) {
                        Recognition rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(), Recognition.TRAINING, algorithm);
                        for (File person : persons) {
                            if (person.isDirectory()) {
                                File[] files = person.listFiles();
                                int counter = 1;
                                for (File file : files) {
                                    if (FileHelper.isFileAnImage(file)) {
                                        Mat imgRgb = Imgcodecs.imread(file.getAbsolutePath());
                                        Imgproc.cvtColor(imgRgb, imgRgb, Imgproc.COLOR_BGRA2RGBA);
                                        Mat processedImage = new Mat();
                                        imgRgb.copyTo(processedImage);
                                        List<Mat> images = ppF.getProcessedImage(processedImage, PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                        if (images == null || images.size() > 1) {
                                            // More than 1 face detected --> cannot use this file for training
                                            continue;
                                        } else {
                                            processedImage = images.get(0);
                                        }
                                        if (processedImage.empty()) {
                                            continue;
                                        }
                                        // The last token is the name --> Folder name = Person name
                                        String[] tokens = file.getParent().split("/");
                                        final String name = tokens[tokens.length - 1];

                                        MatName m = new MatName("processedImage", processedImage);
                                        fileHelper.saveMatToImage(m, FileHelper.DATA_PATH);

                                        rec.addImage(processedImage, name, false);

//                                      fileHelper.saveCroppedImage(imgRgb, ppF, file, name, counter);

                                        // Update screen to show the progress
                                        final int counterPost = counter;
                                        final int filesLength = files.length;
                                        progress.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                progress.append("Image " + counterPost + " of " + filesLength + " from " + name + " imported.\n");
                                            }
                                        });

                                        counter++;
                                    }
                                }
                            }
                        }
                        final Intent intent = new Intent();
                        if (rec.train()) {
                            intent.putExtra("training", "Training successful");
                        } else {
                            intent.putExtra("training", "Training failed");
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    } else {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thread.interrupt();
    }
}
