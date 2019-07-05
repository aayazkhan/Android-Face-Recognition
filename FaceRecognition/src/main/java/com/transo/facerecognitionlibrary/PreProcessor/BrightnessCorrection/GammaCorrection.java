package com.transo.facerecognitionlibrary.PreProcessor.BrightnessCorrection;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

import com.transo.facerecognitionlibrary.Helpers.FileHelper;
import com.transo.facerecognitionlibrary.Helpers.MatName;
import com.transo.facerecognitionlibrary.PreProcessor.Command;
import com.transo.facerecognitionlibrary.PreProcessor.PreProcessor;

public class GammaCorrection implements Command {
    private double gamma;
    private static final Scalar INT_MAX = new Scalar(255);

    public GammaCorrection(double gamma) {
        this.gamma = gamma;
    }

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            img.convertTo(img, CvType.CV_32F);
            Core.divide(img, INT_MAX, img);
            Core.pow(img, gamma, img);
            Core.multiply(img, INT_MAX, img);
            img.convertTo(img, CvType.CV_8U);
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
