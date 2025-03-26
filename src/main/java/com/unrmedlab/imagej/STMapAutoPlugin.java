package com.unrmedlab.imagej;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import trainableSegmentation.WekaSegmentation;
import trainableSegmentation.Weka_Segmentation;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.OpenDialog;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;


import java.io.File;
import java.nio.file.*;

import ij.plugin.filter.Analyzer;
import ij.plugin.filter.ParticleAnalyzer;
import ij.measure.ResultsTable;

import javax.swing.*;

@Plugin(type = Command.class, menuPath = "Plugins>STMapAuto2.0.1")

public class STMapAutoPlugin implements Command{
    public static ImagePlus imp;
    public static RoiManager rm;
    public static WekaSegmentation weka;
    public static ParticleAnalyzer pa;
    public static ResultsTable rt;
    public static ImageStatistics is;
    public static Analyzer a;

    public static int min = 0;
    public static int max = 999999;
    public static double width = .0303;
    public static double height = .0303;
    public static double depth = 0;
    public static String unit = "um";

    public static String imagePath;
    public static String imageFile;
    public static String imageDirectory;
    public static String outputDirectory;

    public static void runPlugin() {

        imp = new ImagePlus();
        rt = new ResultsTable();
        is = new ImageStatistics();

        min = STMapAutoGUI.getMinVal();
        max = STMapAutoGUI.getMaxVal();
        width = STMapAutoGUI.getWidthVal();
        height = STMapAutoGUI.getHeightVal();
        depth = STMapAutoGUI.getDepthVal();

        ParticleAnalyzer.setRoiManager(rm);

        String modeChoice = STMapAutoGUI.getMode();

        if(modeChoice.equals("Single Image Processing")) {
            singleImageProcessing();
        }
    }

    // TODO: Fix WekaSegmentation command not being recognized
    private static void singleImageProcessing() {
        OpenDialog dialog = new OpenDialog("Choose an image", null);
        outputDirectory = IJ.getDirectory("Choose output folder");//this is where they are stored
        if (outputDirectory == null) {
            return;
        }
        imagePath = dialog.getPath();
        imageFile = dialog.getFileName();
        imageDirectory = dialog.getDirectory();
        if (imagePath == null) {
            return;
        }

        trainClassifier(imagePath);
        //Weka_Segmentation segment = new Weka_Segmentation();
        Weka_Segmentation.getResult();
        ImagePlus imp1 = IJ.getImage();
        imp = IJ.openImage(imagePath);
        processSingleImage(imp1, outputDirectory);
    }

    public static void trainClassifier(String imgPath) {
        IJ.run("Trainable Weka Segmentation", "open=" + imgPath);
    }

    public static void processSingleImage(ImagePlus imp1, String output_folder) {
        properties();
        rm = new RoiManager(true);
        ParticleAnalyzer.setRoiManager(rm);
        pa = new ParticleAnalyzer(2048, 0, new ResultsTable(), min, max);
        Path path = Paths.get(output_folder + getOutputFolderName(imp));
        if (!Files.exists(path)) {
            File dir = new File(output_folder + getOutputFolderName(imp));
            dir.mkdir();
        }
        IJ.run("Enhance Contrast", "saturated=0.3");
        IJ.setAutoThreshold(imp1, "Intermodes");
        IJ.run("Set Measurements...", "area mean bounding fit limit redirect=None decimal=3");
        pa.analyze(imp1);
        imp1.close();
        imp1 = null;
        Analyzer.setMeasurements(1 + 2048 + 256 + 2 + 512);
        a = new Analyzer(imp, rt);
        Roi[] rois = rm.getRoisAsArray();
        for (int i = 0; i < rois.length; i++) {
            imp.setRoi(rois[i]);
            IJ.run(imp, "Add Selection...", "");
            is = imp.getStatistics(1 + 2048 + 256 + 2 + 512);
            imp.saveRoi();
            a.saveResults(is, rois[i]);
        }
        ImagePlus imp2 = imp.flatten();
        IJ.saveAsTiff(imp2, output_folder + getOutputFolderName(imp) + "/Mask");
        rt.setHeading(rt.getColumnIndex("Width"), "W-spatial");
        rt.setHeading(rt.getColumnIndex("Height"), "H-duration");
        rt.save(output_folder + getOutputFolderName(imp) + "/Results.xls");
        nullify();
        return;
    }

    public static boolean properties() {
        IJ.run(imp, "Properties...",
                "channels=1 slices=1 frames=1 unit=" + unit + " pixel_width=" + width + " pixel_height="
                        + height + " voxel_depth=" + depth);
        return true;
    }

    private static String getOutputFolderName(ImagePlus imp) {
        return imp.getTitle() + imp.getID() + imp.getBitDepth() + "_bit_" + imp.getHeight()
                + "x" + imp.getHeight() + "x" + imp.getNChannels() + "x" + imp.getNSlices() + "x" + imp
                .getNFrames();
    }

    public static void nullify() {
        rt.reset();
        rm.reset();
        rm.close();
        imp.close();
        a = null;
        is = null;
        pa = null;
        rm = null;
        imp = null;
        weka = null;
        return;
    }

    @Override
    public void run() {

    }
}
