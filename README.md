## **Calcium Imaging Analysis Plugin Fiji/Image J:**

Intracellular Ca<sup>2+</sup> signals occur in nearly all cells, and mediate a myriad of functions such as cell proliferation, secretion, fertilization, and muscle contraction. To investigate these phenomena, Ca^2+ signals are commonly recorded by using either traditional Ca^2+ dyes or genetically encoded Ca^2+ indicators. However, intracellular Ca^2+ signals encode specific responses via complex spatial and temporal patterns including localized oscillations and cell-wide waves, which can be arduous to decipher manually. Therefore, we aim to dramatically enhance current methods of Ca^2+ event detection and analysis. Thus far, we have developed an automated algorithmic solution to the analysis of Ca^2+ signal dynamics and have incorporated a machine learning approach into Ca^2+ Spatio-Temporal Map analysis.

We developed a Spatio-Temporal Map analysis plugin (STMapAuto) that is fully compatible with Fiji/Image J. This plugin allows for automatic extraction of key Ca2+ event information such as: frequency, propagation velocity, intensity, area, and spatial spread. The developed analysis methods will dramatically reduce opportunities for user error and will provide a fast, standardized and accurate analysis to allow for high throughput analysis of multiple datasets.

## **STMapAuto plugin manual:**

First time use:
-After downloading the plugin into Fiji/Image J https://drive.google.com/drive/folders/1ut73XbMOyv6L0WhXRT-2eq7AsvYdlwdM?usp=sharing.

-Create 2 folders on your computer (i.e. Input and output folders).

-Download classifiers to your PC  (LINK).

Now the software is ready to be used: 

1-Move STMaps to be analyzed into “input folder” 

2-Open STMapAuto 

3-Insert image calibration parameters

4-Select input folder 

5-Select output folder 

6-Select your optimized classifier 

7-All segmented images and excel file are located in the output folder 


For optimization of new classifiers and training of new set of STMaps, please refer to 
Dr. Baker Lab page: https://med.unr.edu/directory/sal-baker?v=bio#Biography


## **Explanation of results:**
The Excel file will have 10 columns. The information in each column is as follows.
* Col. A: The number corresponding to each ROI.
* Col. B: The area of the corresponding ROI.
* Col. C: The mean gray value of the corresponding ROI.
* Col. D: The x coordinate of the top left corner of the bounding rectangle of the corresponding ROI.
* Col. E: The y coordinate of the top left corner of the bounding rectangle of the corresponding ROI.
* Col. F: The width of the bounding rectangle of the corresponding ROI.
* Col. G: The height of the bounding rectangle of the corresponding ROI.
* Col. H: The major axis of the bounding ellipse of the corresponding ROI.
* Col. I: The minor axis of the bounding ellipse of the corresponding ROI.
* Col. J: The angle of the bounding ellipse of the corresponding ROI.

## **Supplemental Information**
Processes involved in automated analysis of Ca2+ spatio-temporal maps (STMaps) in Fiji/ImageJ.
1. Upon opening of the .tif image/STMap to be analyzed, the plugin prompts ImageJ to open a Properties window, where the user will be able to calibrate the image. The calibration will allow for both spatial and temporal calibration.
2. The plugin creates and initializes a RoiManager object. This object exists in the plugin and in ImageJ, and is where individual measurements of Ca2+ events take place.
3. The plugin creates and initializes an ImagePlus object to the currently open image. This is loaded directly into the plugin.
4. The plugin creates and initializes a WekaSegmentation object with the previously created ImagePlus object. 
5. Once the WekaSegmentation is initialized, the plugin creates and initializes an OpenDialog object, which prompts the user to pick a classifier of their choice. 
6. The plugin loads the classifier into the WekaSegmentation object, and it is applied. This will generate a classified image.
7. An ImagePlus object is created and initialized to the classified image generated by the WekaSegmentation object. The image will be then displayed by ImageJ.
8. The image will then be a black image. To fix this, the plugin applies the Enhance Contrast function in ImageJ. The image itself remains unchanged, this is for visibility purposes.
9. The plugin then sends another function to ImageJ, applying the Intermodes threshold. At this point, the image has been segmented.
10. Following this, the plugin will then send a command to ImageJ to measure Area, Bounding Rectangle, Fit Ellipse and Mean Gray Value.
11. The plugin will then send a following command to ImageJ, setting Analyze Particles menu to summarize the different objects and to add the objects to the open ROIManager. The plugin will also send command to close the result image.
12. With the original image, the ROIManager object will apply the mask created by the thresholding and particle analysis, and then measure. This will generate the results.
13. The plugin will send a command to save the results to an excel spreadsheet, and save it to the classifier folder.




## **Citation for STMapAuto plugin:**
Please note that STMapAuto plugin is based on a publication. We kindly ask users that utilize the plugin to cite our work:
* Wesley Leigh, Guillermo Del Valle, Sharif Amit Kamran, Alireza Tavakkoli, Kenton M. Sanders, Salah A. Baker (2020). A High Throughput Machine-Learning Driven Analysis of Ca2+ Spatio-temporal Maps (LINK)

## **License:**
The code is released under the MIT License, you can read the license file included in the repository for details.


