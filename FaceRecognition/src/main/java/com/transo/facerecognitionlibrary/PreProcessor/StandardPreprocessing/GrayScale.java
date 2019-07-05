package com.transo.facerecognitionlibrary.PreProcessor.StandardPreprocessing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import com.transo.facerecognitionlibrary.PreProcessor.Command;
import com.transo.facerecognitionlibrary.PreProcessor.PreProcessor;

public class GrayScale implements Command {

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            if(img.channels()>1) {
                Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2GRAY);
            }
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
