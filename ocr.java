/* Loads created XML file that contains the trained Neural Network 
 * and identifies the detected symbols
 *
 *
 * Created by Siddharth Ramachandran, June 2014  */

package com.Detector;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.ml.CvANN_MLP;

class ocr {


	void neuralNet(Mat test) {
		//Create a Neural Network
		CvANN_MLP nnet = new CvANN_MLP();
		//Loading a saved Neural Network
		nnet.load("/Users/SidRama/Desktop/NeuralNetwork.xml");
		System.out.println("Running test...");
		//Loading a test
		//Actually, loading a scanned sample for detection. I'm loading 10 samples
		Mat T = new Mat(1, 784, CvType.CV_32FC1);
		System.out.println("The recognised symbols are:");
		//Extracting a digit sample from test Matrix
		for (int p = 0; p < 10; p++) {
			Mat testOut = new Mat(1, 10, CvType.CV_32FC1);
			for (int i = 0; i < 784; i++) {
				T.put(0, i, test.get(p, i));
			}
			nnet.predict(T, testOut);
			/* testOut has one row, 10 coloumns.
			 * nnet.predict() runs the Neural Network on T matrix
			 * and outputs the probability of the input digit sample belonging
			 * to either of the 10 classes (something like a multi-class classifier) */
			 
		
			// System.out.println(testOut.dump());
			double[] t = null;
			double max = 0;
			int loc = -1;
			/* Choosing the class that has the highest probabilty to be the 
		     * predicted digit */
			for (int j = 0; j < 10; j++) {
				t = testOut.get(0, j);
				if (max < t[0]) {
					max = t[0];
					loc = j;
				}
			}
			System.out.println(loc); //Display predicted digit
		}
	}

	public static void main(String args[]) {
		System.out.println("Running");

		ocr obj = new ocr();
	}

	ocr() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		int mainCount = 0;
		Mat test = new Mat(10, 784, CvType.CV_32FC1);
		//Reading the scanned digit sample for the OCR to detect 
		for (int i = 0; i < 10; i++) {
			File f = new File("/Users/SidRama/Desktop/scaled" + i + ".png");
			Mat init = Highgui.imread(f.getAbsolutePath(),
					Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			int count = 0;
			for (int j = 0; j < init.height(); j++) {
				for (int k = 0; k < init.width(); k++) {
					test.put(i, count, init.get(j, k));
					++count;
				}
			}
		}
		System.out.println("Set up complete...");
		System.out.println("Calling neural network");
		// modifying labels
		neuralNet(test);
	}
}