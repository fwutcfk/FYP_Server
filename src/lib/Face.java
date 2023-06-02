package lib;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Face {
    public static double compareFace(String localFace, byte[] textFace){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat image1 = Imgcodecs.imread(localFace, Imgcodecs.IMREAD_GRAYSCALE);
        Mat image2 = Imgcodecs.imdecode(new MatOfByte(textFace), Imgcodecs.IMREAD_GRAYSCALE);

        CascadeClassifier faceDetector = new CascadeClassifier("src/haarcascade/haarcascade_frontalface_default.xml");

        MatOfRect faceDetections1 = new MatOfRect();
        MatOfRect faceDetections2 = new MatOfRect();
        faceDetector.detectMultiScale(image1, faceDetections1);
        faceDetector.detectMultiScale(image2, faceDetections2);

        if (faceDetections1.toArray().length > 0 && faceDetections2.toArray().length > 0) {
            Rect face1 = faceDetections1.toArray()[0];
            Rect face2 = faceDetections2.toArray()[0];
            Mat feature1 = image1.submat(face1);
            Mat feature2 = image2.submat(face2);
            double similarity = compareFeatures(feature1, feature2);
            return similarity;
        } else {
            System.out.println("No faces detected.");
            return 0;
        }
    }

    private static double compareFeatures(Mat feature1, Mat feature2) {
        double similarity = 0;

        Imgproc.resize(feature1, feature1, feature2.size());

        feature1.convertTo(feature1, CvType.CV_32F);
        feature2.convertTo(feature2, CvType.CV_32F);

        similarity = Imgproc.compareHist(feature1, feature2, Imgproc.CV_COMP_CORREL);

        return similarity;
    }
}

