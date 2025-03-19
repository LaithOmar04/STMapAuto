package com.unrmedlab.imagej;


import ij.ImageJ;
import ij.plugin.PlugIn;
import java.awt.datatransfer.DataFlavor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class STMapAutoGUI implements PlugIn {
    private JPanel leftPanel = new JPanel();
    private JPanel parameterPanel = new JPanel();
    private JPanel imagePanel = new JPanel();

    public String min = "0";
    public String max = "999999";
    public String width = ".0303";
    public String height = ".0303";
    public String depth = "0";
    public String unit = "um";

    JFrame frame = new JFrame("ImageJ Plugin UI");
        JLabel minLabel = new JLabel("Minimum: ");
        JLabel maxLabel = new JLabel("Maximum: ");
        JLabel unitLabel = new JLabel("Units: ");
        JLabel widthLabel = new JLabel("Pixel Width: ");
        JLabel heightLabel = new JLabel("Pixel Height: ");
        JLabel depthLabel = new JLabel("Voxel Depth: ");
        JTextField minField = new JTextField(min);
        JTextField maxField = new JTextField(max);
        JTextField unitField = new JTextField(unit);
        JTextField widthField = new JTextField(width);
        JTextField heightField = new JTextField(height);
        JTextField depthField = new JTextField(depth);

    public void run(String arg) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        leftPanel.setBounds(0,0,150,400);

        JLabel modeLabel = new JLabel("Mode:");
        String[] modes = {"Single Image Processing", "Batch Image Processing"};
        JComboBox<String> modeDropdown = new JComboBox<>(modes);
        modeDropdown.setPrototypeDisplayValue("choose mode");
        leftPanel.add(modeLabel);
        leftPanel.add(modeDropdown);
        leftPanel.add(new JButton("First"));
        leftPanel.setPreferredSize(new Dimension(150, 400));

        parameterPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
        GridBagLayout parameterLayout = new GridBagLayout();
        GridBagConstraints parameterConstraints = new GridBagConstraints();
        parameterConstraints.anchor = GridBagConstraints.NORTHWEST;
        parameterConstraints.fill = GridBagConstraints.HORIZONTAL;
        parameterConstraints.gridwidth = 1;
        parameterConstraints.gridheight = 1;
        parameterConstraints.gridx = 0;
        parameterConstraints.gridy = 0;
        parameterConstraints.insets = new Insets(5, 5, 6, 6);
        parameterPanel.setLayout(parameterLayout);

        JLabel label = new JLabel("Text 1");

        parameterPanel.add(minLabel, parameterConstraints);
        parameterConstraints.gridx++;
        parameterPanel.add(minField, parameterConstraints);
        parameterConstraints.gridy++;
        parameterConstraints.gridx--;
        parameterPanel.add(maxLabel, parameterConstraints);
        parameterConstraints.gridx++;
        parameterPanel.add(maxField, parameterConstraints);
        parameterConstraints.gridy++;
        parameterConstraints.gridx--;
        parameterPanel.add(unitLabel, parameterConstraints);
        parameterConstraints.gridx++;
        parameterPanel.add(unitField, parameterConstraints);
        parameterConstraints.gridy++;
        parameterConstraints.gridx--;
        parameterPanel.add(widthLabel, parameterConstraints);
        parameterConstraints.gridx++;
        parameterPanel.add(widthField, parameterConstraints);
        parameterConstraints.gridy++;
        parameterConstraints.gridx--;
        parameterPanel.add(heightLabel, parameterConstraints);
        parameterConstraints.gridx++;
        parameterPanel.add(heightField, parameterConstraints);
        parameterConstraints.gridy++;
        parameterConstraints.gridx--;
        parameterPanel.add(depthLabel, parameterConstraints);
        parameterConstraints.gridx++;
        parameterPanel.add(depthField, parameterConstraints);
        parameterConstraints.gridy++;


        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imagePanel.setPreferredSize(new Dimension(300, 300));
        imagePanel.setLayout(new BorderLayout());
        JLabel dropFileLabel = new JLabel("Drop image here or click to select file", SwingConstants.CENTER);
        imagePanel.add(dropFileLabel, BorderLayout.CENTER);




        imagePanel.setTransferHandler(new TransferHandler() {
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            public boolean importData(TransferHandler.TransferSupport support) {
                try {
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        dropFileLabel.setText(files.get(0).getName());
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        imagePanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    dropFileLabel.setText(file.getName());
                }
            }
        });

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(parameterPanel, BorderLayout.EAST);
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new ImageJ();
        new STMapAutoGUI().run("");
    }
}
