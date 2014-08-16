/* Image pre-processing for basic OCR application
 * Created by Siddharth Ramachandran, June 2014
 */

/* This class will detect possible digits of varying fonts and font sizes
 * However, this currently only works on a plain white background and digits in black 
 * Can be easily plugged into ocr.java  
 */
package com.Detector;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImgPro {
	int cf;

	/*
	 * public static void main(String args[]) { ImgPro obj = new ImgPro(); }
	 */
	void floodFill(Mat inp, int pr, int pc) {
		double[] z;

		z = inp.get(pr, pc);
		if (z[0] == 0) {
			inp.put(pr, pc, 255);
			floodFill(inp, pr + 1, pc);
			floodFill(inp, pr - 1, pc);
			floodFill(inp, pr, pc + 1);
			floodFill(inp, pr, pc - 1);
		}
	}

	ImgPro() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// Median med = new Median();
		BufferedImage image = null;
		int h, w, r, g, b, grey, alpha;
		Color color;
		Scanner scanner = new Scanner(System.in);
		String filename = new String();
		System.out.println("Enter the filename:");
		filename = scanner.next();
		File f = new File("/Users/SidRama/Desktop/" + filename);
		System.out.println(f);
		try {
			image = ImageIO.read(f);
		} catch (IOException E) {
			System.out.println("Error");
		}
		w = image.getWidth();
		h = image.getHeight();
		int[][] dat = new int[h][w];
		double[] val = new double[h * w];
		int count = 0;
		double[] x;
		Mat input = new Mat(h, w, CvType.CV_8UC1);
		count = 0;
		BufferedImage out = new BufferedImage(w, h,
				BufferedImage.TYPE_BYTE_GRAY);
		System.out.println(h + " " + w);
		System.out.println("Extracting... ");
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int pixel = image.getRGB(j, i);

				b = (pixel) & 0xff; // extract from Java integer
				g = (pixel >> 8) & 0xff;
				r = (pixel >> 16) & 0xff;
				alpha = (pixel >> 24) & 0xff;
				grey = (r + g + b) / 3;
				dat[i][j] = grey;
				val[count] = grey;
				++count;
				input.put(i, j, dat[i][j]);
				x = input.get(i, j);
				// System.out.print(x[0] + " ");
				// System.out.print(val[count-1] + " ");
			}
			// System.out.println();
		}
		// Imgproc.threshold(input, input, 170, 255, Imgproc.THRESH_BINARY);
		// Running Canny edge detector
		// double median = med.evaluate(val);
		// System.out.println(median);

		// Applying Gaussian Blur

		// Imgproc.GaussianBlur(input, input, new Size(5,5), 4);
		// Mean mean= new Mean();
		// double m = mean.evaluate(val,0,count);
		// System.out.println(m);
		Mat det = new Mat(h, w, CvType.CV_8UC1);
		Imgproc.Canny(input, det, 200, 250, 3, true);
		det = input.clone();
		System.out.println("Canny done..");
		System.out.println(det.height() + " " + det.width());
		double[] dl = null;
		BufferedImage can = new BufferedImage(w, h,
				BufferedImage.TYPE_BYTE_GRAY);
		int pt = 0;
		for (int i = 0; i < det.height(); i++) {

			for (int j = 0; j < det.width(); j++) {
				dl = det.get(i, j);
				pt = (int) dl[0];
				color = new Color(pt, pt, pt);
				can.setRGB(j, i, color.getRGB());
			}
		}
		try {
			ImageIO.write(can, "jpeg", new File(
					"/Users/SidRama/Desktop/Canny.jpeg"));
		} catch (IOException e) {
			System.out.println("Can't write");
		}
		Mat dst = new Mat(h, w, CvType.CV_8UC1);
		/*
		 * for(int i=0;i<h;i++) { for(int j=0;j<w;j++) { dst.put(i, j, 0); }
		 * 
		 * } Mat src = new Mat(h,w,CvType.CV_8UC1); // src.copyTo(dst, det);
		 * //make a box
		 */

		Mat temp = new Mat();
		temp = det.clone();
		// System.out.println(temp.height() +" "+ temp.height());
		int mainF = 0;

		// from left
		int f1 = 0, f2 = 0, fx = 0;
		int[] x1 = new int[30];
		int[] x2 = new int[30];
		int[] y1 = new int[30];
		int[] y2 = new int[30];
		// int cf=0;
		// int t=0;
		cf = 0;
		int px = 0, p1 = 0;
		int tcount = 0;
		do {
			mainF = 0;
			// finding width borders
			for (int u = 0; u < w; u++) {
				for (int t = 0; t < h; t++) {
					x = temp.get(t, u);
					if (x[0] != 0) {
						px = 1;
						if (p1 == 0) {
							p1 = 1;
							x1[cf] = u;
						}
					}
				}
				if ((p1 == 1) && (px == 0)) {
					x2[cf] = u;
					p1 = 0;
					System.out.println("Width: " + x1[cf] + " " + x2[cf]);// actually
																			// width
					++cf;
					p1 = 0;
					px = 0;
				}
				px = 0;
			}
			// finding height borders
			// int q=0;
			for (int i = tcount; i < cf; i++) {
				for (int k = 0; k < h; k++) {
					for (int q = x1[i]; q < x2[i]; q++) {
						x = temp.get(k, q);
						if (x[0] != 0) {
							fx = 1;
							if (f1 == 0) {
								f1 = 1;
								y1[i] = k;
							}
						}
					}
					if ((f1 == 1) && (fx == 0)) {
						y2[i] = k;
						f1 = 0;
						System.out.println("Height: " + y1[i] + " " + y2[i]);
						f1 = 0;
						fx = 0;
						break;
					}
					fx = 0;
				}
			}
			for (int q = 0; q < cf; q++) {
				for (int i = x1[q]; i < x2[q]; i++) {
					for (int j = y1[q]; j < y2[q]; j++) {
						temp.put(j, i, 0);
					}
				}
			}
			for (int i = 0; i < h; i++) {
				double[] tc;
				for (int j = 0; j < w; j++) {
					tc = temp.get(i, j);
					if (tc[0] == 255) {
						mainF = 1;
					}
				}
			}
			tcount = cf;
		} while (mainF == 1);
		for (int i = 0; i < cf; i++) {
			if (((x2[i] - x1[i]) < 10) || ((y2[i] - y1[i]) < 10)) {
				int t = i;
				for (int g1 = t; g1 < (cf - 1); g1++) {
					x1[g1] = x1[g1 + 1];
					x2[g1] = x2[g1 + 1];
					y1[g1] = y1[g1 + 1];
					y2[g1] = y2[g1 + 1];
				}
				--cf;
			}
		}
		double[] d;
		System.out.println(cf);
		int p = 0;
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				d = det.get(i, j);
				int flag = 0;
				for (int t = 0; t < cf; t++) {

					if ((j == x1[t]) || (j == x2[t])
							|| (i == y1[t] || (i == y2[t]))) {
						p = 255;
						flag = 1;
					}
				}
				if (flag == 0)
					p = (int) d[0];
				color = new Color(p, p, p);
				out.setRGB(j, i, color.getRGB());
			}
		}
		try {
			ImageIO.write(out, "jpg",
					new File("/Users/SidRama/Desktop/try.jpg"));
		} catch (IOException e) {
			System.out.println("Can't write");
		}

		System.out.println("Display individual");
		// The seperate values
		Mat[] inp = new Mat[cf];
		for (int i = 0; i < cf; i++) {
			inp[i] = new Mat(y2[i] - y1[i], x2[i] - x1[i], CvType.CV_8UC1);
		}
		BufferedImage disp[] = new BufferedImage[cf];
		for (int i = 0; i < cf; i++) {
			disp[i] = new BufferedImage(x2[i] - x1[i], y2[i] - y1[i],
					BufferedImage.TYPE_BYTE_GRAY);
		}
		for (int q = 0; q < cf; q++) {
			for (int i = x1[q]; i < x2[q]; i++) {
				for (int j = y1[q]; j < y2[q]; j++) {
					d = det.get(j, i);
					p = (int) d[0];
					color = new Color(p, p, p);
					disp[q].setRGB((i - x1[q]), (j - y1[q]), color.getRGB());
					inp[q].put(j - y1[q], i - x1[q], d[0]);
				}
				// System.out.println();
			}
			try {
				ImageIO.write(disp[q], "jpg", new File(
						"/Users/SidRama/Desktop/disp" + q + ".jpg"));
			} catch (IOException e) {
				System.out.println("Can't write individual");
			}

		}
		// Floodfill
		for (int t = 0; t < cf; t++) {
			int fx1 = 0, flag = 0;
			int pr = 0, pc = 0;
			double[] z;
			int vx1 = 0, vx2 = 0, fl = 0;
			for (int i = 0; i < inp[t].height(); i++) {
				for (int j = 0; j < inp[t].width(); j++) {
					z = inp[t].get(i, j);
					if ((z[0] == 255) && (fl == 0)) {
						fl = 1;
						vx1 = j;
					} else if ((z[0] == 0) && (fl == 1)) {
						fl = 2;
					} else if ((z[0] == 255) && (fl == 2)) {
						vx2 = j;
						fl = 0;
						for (int v = vx1; v < vx2; v++) {
							inp[t].put(i, v, 255);
						}
					}
				}

				fl = 0;
			}
		}

		System.out.println("Scaling");
		BufferedImage[] scale = new BufferedImage[cf];
		for (int i = 0; i < cf; i++) {
			scale[i] = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
		}

		for (int q = 0; q < cf; q++) {
			Mat output = new Mat();
			float scale_val = 0;
			// Size dsize = new Size(20,20);
			// Imgproc.resize(inp[q],output,dsize);
			if (inp[q].height() > inp[q].width()) {
				System.out.println(inp[q].height() + " " + inp[q].width());
				scale_val = (float) 20 / inp[q].height();
				System.out.println(scale_val);
				int scl = (int) (inp[q].width() * scale_val);
				System.out.println(scl);
				Imgproc.resize(inp[q], output, new Size(scl, 20), 0, 0,
						Imgproc.INTER_CUBIC);

			} else {
				scale_val = (float) 20 / inp[q].width();
				int scl = (int) (inp[q].height() * scale_val);
				Imgproc.resize(inp[q], output, new Size(20, scl), 0, 0,
						Imgproc.INTER_CUBIC);
			}
			System.out.println("Scaling complete");
			Mat borderd = new Mat(28, 28, CvType.CV_8UC1);
			int xval = (28 - output.height()) / 2;
			int yval = (28 - output.width()) / 2;
			System.out.println(xval + " " + yval);
			Imgproc.copyMakeBorder(output, borderd, xval, xval, yval, yval,
					Imgproc.BORDER_CONSTANT, new Scalar(0));
			for (int i = 0; i < borderd.height(); i++) {
				for (int j = 0; j < borderd.width(); j++) {
					d = borderd.get(i, j);
					p = (int) d[0];
					color = new Color(p, p, p);
					scale[q].setRGB(j, i, color.getRGB());
				}

			}
			try {
				ImageIO.write(scale[q], "png", new File(
						"/Users/SidRama/Desktop/scaled" + q + ".png"));
			} catch (IOException e) {
				System.out.println("Can't write");
			}
		}
	}
}
