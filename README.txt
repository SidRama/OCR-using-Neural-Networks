This program was created to recognise digits using Neural Networks which is a Supervised Machine Learning algorithm. It can be modified suitably to recognise various pattens by providing an appropriate training set. This application uses the OpenCV library to implement the Neural Network and perform various image manipulations. 

The various files are:
1. NeuralDetector.java: This class trains a new Neural Network using the pre-processed samples. It saves it as an XML file. This can be modified depending on the training set set up.

2. NeuralDetectorMNIST.java: This class is similar in implementation to NeuralNetworkDetector. Here, the training set used is from the MNIST database for handwritten digits. It can be downloaded from yann.lecun.com/exdb/mnist/

3. imgPro.java: This class will detect possible digits of varying fonts and font sizes. However, this currently only works on a plain white background and digits in black. It can be easily plugged into ocr.java  

4. ocr.java: This class loads a created XML file that contains the trained Neural Network and identifies the detected symbols.

5. trainPro.java: This class pre-processes training set that will be used to train the Neural Network. This is just some image manipulation I was trying to pre-process input. The input can even be a custom training set. (Not MNIST)


“Pre-processing” steps followed for the input image are:
1. Identify the text region by first loading the image and converting it to grayscale.
2. Apply a Gaussian Blur to smooth the image and remove noise.
3. Threshold the input using Adaptive Thresholding. This creates an image matrix comprising of 0 and 255.
4. Apply the Canny Edge Detector to obtain the outline.
5. Segment the numbers by iterating through the matrix eliminate any noise.
6. After segmentation, either dilate the input or apply a flood fill.
7. Scale the segmented symbol so that fits in a 20 x 20 box. The aspect ration must be preserved.
8. Apply a border so that the scaled symbol is centred in a 28 x 28 box.



Information about the Neural Network used:
The Neural Network used in this application comprises of 3 layers. The input layer or activation layer ,which comprises of 784 nodes (because the input image comprises 28 x 28 pixels). Therefore there is one input node for each image pixel. The hidden layer comprises of 28 nodes and the output layer comprises of 10 nodes (10 possibilities, 10 digits in English).
It is a Feed-Forward Neural Network with Back Propogation.
I have observed that the error and over-fitting is reduced by adding an additional hidden layer. This however drastically increases the training time and therefore impacts computation load of the training program.


