//Created by Siddharth Ramachandran, June 2014

//This is an alternate version of NerualNetworkDetector

/* This class is similar in implementation to NeuralNetworkDetector. 
 * Here, the training set used is from the MNIST database for handwritten digits.
 * It can be downloaded from yann.lecun.com/exdb/mnist/
 * 
 * 
 */

package com.Detector;

import java.io.IOException;

import mnist.tools.MnistManager;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.CvANN_MLP;
import org.opencv.ml.CvANN_MLP_TrainParams;

class NeuralDetectorMNIST {
	void neuralNet(Mat M, Mat label, Mat test) {
		System.out.println(M.height() + " " + M.width());
		System.out.println(label.height() + " " + label.width());
		System.out.println(test.height() + " " + test.width());
		Mat layerSizes = new Mat(3, 1, CvType.CV_32S); // Setting up the layers
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
		Mat sample = new Mat(5000, 1, CvType.CV_32FC1);
		// Random rand = new Random();
		for (int i = 0; i < 5000; i++) {
			sample.put(i, 0, 0);
		}
		Mat idx = new Mat(5000, 10, CvType.CV_32FC1);
		for (int i = 0; i < 5000; i++) {
			for (int j = 0; j < 10; j++)
				idx.put(i, j, 0);
		}
		System.out.println("Training.... ");

		int iter = nnet.train(M, label, new Mat(), new Mat(), param,
				CvANN_MLP.NO_OUTPUT_SCALE);
		System.out.println("Number of iteratins: " + iter);

		System.out.println("Running test...");

		// Mat testOut = new Mat(1,10,CvType.CV_32FC1);
		Mat T = new Mat(1, 784, CvType.CV_32FC1);
		System.out.println("The recognised symbols are:");
		for (int p = 0; p < 10; p++) {
			Mat testOut = new Mat(1, 10, CvType.CV_32FC1);
			for (int i = 0; i < 784; i++) {
				T.put(0, i, test.get(p, i));

			}

			nnet.predict(T, testOut);
			// System.out.println(testOut.dump());
			double[] t = null;

			double max = 0;
			int loc = -1;
			for (int j = 0; j < 10; j++) {
				t = testOut.get(0, j);

				if (max < t[0]) {
					max = t[0];
					loc = j;
				}
			}
			System.out.println(loc);
		}

	}

	public static void main(String args[]) {
		System.out.println("Running");
		NeuralDetectorMNIST obj = new NeuralDetectorMNIST();

	}

	NeuralDetectorMNIST() {
		MnistManager m = null;
		MnistManager y = null;
		int[][] image = null;

		Mat M = null;
		Mat label = null;
		Mat test = null;
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try {
			m = new MnistManager(
					"/Users/SidRama/Desktop/MNist/t10k-images-idx3-ubyte",
					"/Users/SidRama/Desktop/MNist/t10k-labels-idx1-ubyte");
			// y= new
			// MnistManager("/Users/SidRama/Desktop/MNist/train-images-idx3-ubyte",
			// "/Users/SidRama/Desktop/MNist/train-labels-idx1-ubyte");
			image = m.readImage();
			System.out.println("Label:" + m.readLabel());
			MnistManager
					.writeImageToPpm(image, "/Users/SidRama/Desktop/10.ppm");
		} catch (IOException e) {
			System.out.println("Not read");
		}
		System.out.println(image.length + " " + image[0].length);
		int count = 0;
		int row = image.length, lab;
		int col = image[0].length;
		int n = row * col;
		double data[][] = new double[5000][n];
		M = new Mat(7000, n, CvType.CV_32FC1);
		label = new Mat(7000, 10, CvType.CV_32FC1);
		test = new Mat(10, n, CvType.CV_32FC1);
		for (int i = 1; i <= 7000; i++) {
			count = 0;
			m.setCurrent(i);
			try {
				image = m.readImage();
				lab = m.readLabel();

				for (int z = 0; z < 10; z++) {
					if (z == lab)
						label.put(i - 1, lab, 1);
					else
						label.put(i - 1, z, 0);
				}

			} catch (IOException E) {
				System.out.println("Not read");
			}
			for (int j = 0; j < row; j++) {
				for (int k = 0; k < col; k++) {
					M.put(i - 1, count, image[j][k]);
					++count;
				}
			}

		}
		for (int i = 1; i <= 10; i++) {
			count = 0;
			m.setCurrent(i);
			try {
				image = m.readImage();
				System.out.println(m.readLabel());
				MnistManager.writeImageToPpm(image,
						"/Users/SidRama/Desktop/SymbolScanned/symbol" + i
								+ ".ppm");
			} catch (IOException E) {
				System.out.println("Not read");
			}
			for (int j = 0; j < row; j++) {
				for (int k = 0; k < col; k++) {
					test.put(i - 1, count, image[j][k]);
					++count;
					System.out.print(image[j][k] + " ");
				}
				System.out.println();
			}

		}
		System.out.println(M.height() + " " + M.width());
		// Displaying a random
		/*
		 * BufferedImage outImg = new
		 * BufferedImage(20,20,BufferedImage.TYPE_INT_RGB); int count=0; double
		 * []temp=null; for(int x=0;x<20;x++) { for(int y=0;y<20;y++) {
		 * 
		 * temp = M.get(1000, count); z=(int)Math.abs(Math.round(temp[0]));
		 * ++count;
		 * 
		 * 
		 * if(z==1) color = new Color(0,0,0); else color=new Color(255,255,255);
		 * outImg.setRGB(x,y,color.getRGB());
		 * 
		 * }
		 * 
		 * 
		 * } try{ ImageIO.write(outImg, "jpg", new
		 * File("/Users/SidRama/Desktop/test.jpg")); } catch(IOException E) {
		 * System.out.println("Error in rendering!");}
		 * System.out.println("Done");
		 */

		System.out.println("Calling neural network");
		// modifying labels
		neuralNet(M, label, test);

	}
}