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
    public void run(String arg) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("ImageJ Plugin UI");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel modeLabel = new JLabel("Mode:");
        String[] modes = {"Single Image Processing", "Batch Image Processing"};
        JComboBox<String> modeDropdown = new JComboBox<>(modes);
        topPanel.add(modeLabel);
        topPanel.add(modeDropdown);

        JPanel rightPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        String[] labels = {"Minimum:", "Maximum:", "Units:", "Pixel Width:", "Pixel Height:", "Voxel Depth:"};
        JTextField[] textFields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            rightPanel.add(new JLabel(labels[i]));
            textFields[i] = new JTextField(10);
            rightPanel.add(textFields[i]);
        }

        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        centerPanel.setPreferredSize(new Dimension(300, 300));
        JLabel dropLabel = new JLabel("Drop files here or click to select", SwingConstants.CENTER);
        centerPanel.add(dropLabel);

        centerPanel.setTransferHandler(new TransferHandler() {
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            public boolean importData(TransferHandler.TransferSupport support) {
                try {
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        dropLabel.setText(files.get(0).getName());
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        centerPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    dropLabel.setText(file.getName());
                }
            }
        });

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new ImageJ();
        new STMapAutoGUI().run("");
    }
}
