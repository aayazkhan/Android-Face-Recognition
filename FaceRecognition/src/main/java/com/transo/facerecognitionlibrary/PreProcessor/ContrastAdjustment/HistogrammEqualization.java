package com.transo.facerecognitionlibrary.PreProcessor.ContrastAdjustment;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import com.transo.facerecognitionlibrary.Helpers.FileHelper;
import com.transo.facerecognitionlibrary.Helpers.MatName;
import com.transo.facerecognitionlibrary.PreProcessor.Command;
import com.transo.facerecognitionlibrary.PreProcessor.PreProcessor;

public class HistogrammEqualization implements Command {

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            img.convertTo(img, CvType.CV_8U);
            Imgproc.equalizeHist(img, img);
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
