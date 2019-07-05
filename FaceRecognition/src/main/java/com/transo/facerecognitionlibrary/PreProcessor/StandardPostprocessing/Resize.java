package com.transo.facerecognitionlibrary.PreProcessor.StandardPostprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import com.transo.facerecognitionlibrary.Helpers.PreferencesHelper;
import com.transo.facerecognitionlibrary.PreProcessor.Command;
import com.transo.facerecognitionlibrary.PreProcessor.PreProcessor;

public class Resize implements Command {

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        PreferencesHelper preferencesHelper = new PreferencesHelper(preProcessor.getContext());
        Size size = new Size(preferencesHelper.getN(), preferencesHelper.getN());
        preProcessor.setImages(preprocessImages(images, size));
        return preProcessor;
    }

    public static List<Mat> preprocessImage(List<Mat> images, int n){
        Size size = new Size(n, n);
        return preprocessImages(images,size);
    }

    private static List<Mat> preprocessImages(List<Mat> images, Size size){
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            Imgproc.resize(img, img, size);
            processed.add(img);
        }
        return processed;
    }
}
