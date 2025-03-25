package com.unrmedlab.imagej;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import trainableSegmentation.WekaSegmentation;
import trainableSegmentation.Weka_Segmentation;
import trainableSegmentation.utils.Utils;



import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NonBlockingGenericDialog;
import ij.gui.Roi;
import ij.io.OpenDialog;
import ij.plugin.frame.Recorder;
import ij.plugin.frame.RoiManager;
import ij.process.ImageStatistics;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.io.IOException;
import java.util.logging.Logger;

import ij.plugin.filter.Analyzer;
import ij.plugin.filter.ParticleAnalyzer;
import ij.measure.ResultsTable;

import javax.swing.*;

@Plugin(type = Command.class, menuPath = "Plugins>STMapAuto2.0.1")

public class STMapAutoPlugin implements Command{
    public ImagePlus imp;
    public RoiManager rm;
    public WekaSegmentation weka;
    public ParticleAnalyzer pa;
    public ResultsTable rt;
    public ImageStatistics is;
    public Analyzer a;

    public int min = 0;
    public int max = 999999;
    public double width = .0303;
    public double height = .0303;
    public double depth = 0;
    public String unit = "um";

    @Override
    public void run() {
        imp = new ImagePlus();
        rt = new ResultsTable();
        is = new ImageStatistics();

        min = STMapAutoGUI.getMinVal();
        max = STMapAutoGUI.getMaxVal();
        width = STMapAutoGUI.getWidthVal();
        height = STMapAutoGUI.getHeightVal();
        depth = STMapAutoGUI.getDepthVal();
        height = STMapAutoGUI.getHeightVal();

        ParticleAnalyzer.setRoiManager(rm);

        String modeChoice = STMapAutoGUI.getMode();

        if(modeChoice.equals("Single Image Processing")) {
            singleImageProcessing();
        }
    }

    // TODO: do the rest of the implementation from stmapauto code
    private void singleImageProcessing() {

    }
}
