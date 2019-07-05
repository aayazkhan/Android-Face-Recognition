package com.transo.facerecognitionlibrary.PreProcessor.Contours;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import com.transo.facerecognitionlibrary.PreProcessor.Command;
import com.transo.facerecognitionlibrary.PreProcessor.PreProcessor;

public class Masking implements Command {

    public PreProcessor preprocessImage(PreProcessor preProcessor) {
        List<Mat> images = preProcessor.getImages();
        List<Mat> processed = new ArrayList<Mat>();
        for (Mat img : images){
            preProcessor.normalize0255(img);

            /***************************************************************************************
             *    Title: Automatic calculation of low and high thresholds for the Canny operation in opencv
             *    Author: VP
             *    Date: 16.04.2013
             *    Code version: -
             *    Availability: http://stackoverflow.com
             *
             ***************************************************************************************/

            double otsu_thresh_val = Imgproc.threshold(img, img, 0, 255, Imgproc.THRESH_OTSU);
            Imgproc.Canny(img, img, otsu_thresh_val * 0.5, otsu_thresh_val);
            processed.add(img);
        }
        preProcessor.setImages(processed);
        return preProcessor;
    }
}
