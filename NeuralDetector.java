//Created by Siddharth Ramachandran, June 2014 



/* Trains a new Neural Network using the pre-processed samples. Saves as an XML file
 * The training set has one set of digits corresponding to a number in a folder. 
 * There will be ten folders which will be iterated through.
 * 
 */

package com.Detector;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.ml.CvANN_MLP;
import org.opencv.ml.CvANN_MLP_TrainParams;

class NeuralTrainer {
	void neuralNetTrain(Mat M, Mat label) {

		System.out.println(M.height() + " " + M.width());
		System.out.println(label.height() + " " + label.width());
		System.out.println(test.height()+ " "+ test.width());
		Mat layerSizes = new Mat(3, 1, CvType.CV_32S); // Setting up the layers
		//784 nodes in Input layer, 28 nodes in hidden layer, 10 nodes in output layer
		//784 nodes as one node for each pixel. 28x28 pixel image used to train
		layerSizes.put(0, 0, 784);
		layerSizes.put(1, 0, 28);
		layerSizes.put(2, 0, 10);
		// Creating the Neural Network
		CvANN_MLP nnet = new CvANN_MLP(layerSizes, CvANN_MLP.SIGMOID_SYM, 0.6,
				1);
		System.out.println("Neural Network created");
		// Create criteria
		TermCriteria criteria = new TermCriteria(TermCriteria.COUNT
				+ TermCriteria.EPS, 1000, 0.000001);
		// Create training parameters
		CvANN_MLP_TrainParams param = new CvANN_MLP_TrainParams();
		param.set_term_crit(criteria);
		param.set_train_method(CvANN_MLP_TrainParams.BACKPROP);
		param.set_bp_dw_scale(0.1);
		param.set_bp_moment_scale(0.1);

		System.out.println("TrainParams set up");
		//Setting up the Training parameters
		Mat sample = new Mat(5000, 1, CvType.CV_32FC1);
		for (int i = 0; i < 5000; i++) {
			sample.put(i, 0, 0);
		}
		Mat idx = new Mat(5000, 10, CvType.CV_32FC1);
		for (int i = 0; i < 5000; i++) {
			for (int j = 0; j < 10; j++)
				idx.put(i, j, 0);
		}
		System.out.println("Training.... ");
		//Train Nerual Network
		int iter = nnet.train(M, label, new Mat(), new Mat(), param,
				CvANN_MLP.NO_OUTPUT_SCALE);
		System.out.println("Number of iteratins: " + iter);
		System.out.println("Saving...");
		/*Export created Neural Network into a .xml 
		Allows to call upon a pre-trained Neural Network. 
		Saves time for training and allows pre-trained Neural Network to be used
		in iOS/Android device */
		nnet.save("/Users/SidRama/Desktop/NeuralNetwork.xml");
		System.out.println("Training Complete...");
	}

	public static void main(String args[]) {
		NeuralTrainer obj2 = new NeuralTrainer();
	}

	NeuralTrainer() {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		int mainCount = 0;
		//Must use Single channel 32bit floating point matrix
		Mat M = new Mat(1000, 784, CvType.CV_32FC1);
		Mat label = new Mat(1000, 10, CvType.CV_32FC1);
		for (int num = 1; num <= 100; num++) {
			for (int numVal = 0; numVal <= 9; numVal++) {
				//Iterate through custom training set 
				File f = new File("/Users/SidRama/Desktop/TrainingSet/"
						+ numVal + "/img" + num + ".png");
				//Read values into a Mat
				Mat init = Highgui.imread(f.getAbsolutePath(),
						Highgui.CV_LOAD_IMAGE_GRAYSCALE);
				int count = 0;
				for (int i = 0; i < init.height(); i++) {
					for (int j = 0; j < init.width(); j++) {
						M.put(mainCount, count, init.get(i, j));
						++count;
					}
				}
				//Set up label for training set
				for (int i = 0; i < 10; i++) {
					if (i == numVal) {
						label.put(mainCount, i, 1);
					} else {
						label.put(mainCount, i, 0);
					}
				}
				++mainCount;
			}
		}
		//Call to create Neural Network
		neuralNetTrain(M, label);

	}
}