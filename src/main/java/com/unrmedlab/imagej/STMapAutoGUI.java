package com.unrmedlab.imagej;

import ij.ImageJ;
import ij.plugin.PlugIn;

import java.awt.datatransfer.DataFlavor;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

//TODO: make it so that images open correctly in gui, maybe add RUN button,

public class STMapAutoGUI implements PlugIn {
    public static String[] modes = {"Choose mode", "Single Image Processing", "Batch Image Processing"};
    public static String min = "0";
    public static String max = "999999";
    public static String width = ".0303";
    public static String height = ".0303";
    public static String depth = "0";
    public static String unit = "um";

    private JFrame frame = new JFrame("ImageJ Plugin UI");

    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JPanel modePanel = new JPanel();
    private JPanel leftFillerPanel = new JPanel();
    private JPanel rigthFillerPanel = new JPanel();

        static JComboBox<String> modeDropdown = new JComboBox<>(modes);
    private JPanel parameterPanel = new JPanel();
        JLabel minLabel = new JLabel("Minimum: ");
        JLabel maxLabel = new JLabel("Maximum: ");
        JLabel unitLabel = new JLabel("Units: ");
        JLabel widthLabel = new JLabel("Pixel Width: ");
        JLabel heightLabel = new JLabel("Pixel Height: ");
        JLabel depthLabel = new JLabel("Voxel Depth: ");
        static JTextField minField = new JTextField(min);
        static JTextField maxField = new JTextField(max);
        static JTextField unitField = new JTextField(unit);
        static JTextField widthField = new JTextField(width);
        static JTextField heightField = new JTextField(height);
        static JTextField depthField = new JTextField(depth);
    private JPanel imagePanel = new JPanel();
    private JPanel filePanel = new JPanel();
        JButton saveDataButton = new JButton("Save Data");

    public void run(String arg) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        //TODO: maybe realign modeDropdown size-changing code

        // Set up the dropdown combo box used for selecting the mode
        modeDropdown.setSelectedIndex(0);
        modeDropdown.setBackground(Color.WHITE);

        // Custom renderer to keep "choose mode" small while allowing wider items in dropdown
        modeDropdown.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                // Enforce different widths for selected item vs dropdown items
                if (index == -1) { // Index -1 means it's the selected item
                    label.setPreferredSize(new Dimension(120, label.getPreferredSize().height));
                } else {
                    label.setPreferredSize(new Dimension(250, label.getPreferredSize().height));
                }
                return label;
            }
        });

        // Force popup menu width adjustment
        modeDropdown.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                Object popup = modeDropdown.getUI().getAccessibleChild(modeDropdown, 0);
                if (popup instanceof JPopupMenu) {
                    JPopupMenu popupMenu = (JPopupMenu) popup;
                    popupMenu.setPreferredSize(new Dimension(147, 56));

                    // Also ensure the list inside the popup gets resized
                    Component[] comps = popupMenu.getComponents();
                    for (Component comp : comps) {
                        if (comp instanceof JScrollPane) {
                            JScrollPane scrollPane = (JScrollPane) comp;
                            JViewport viewport = scrollPane.getViewport();
                            Component view = viewport.getView();
                            if (view instanceof JList) {
                                JList<?> list = (JList<?>) view;
                                list.setFixedCellWidth(250); // Ensure full width
                            }
                        }
                    }
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        // Create modePanel and set its constraints and layout
        modePanel.setBorder(BorderFactory.createTitledBorder("Mode"));
        GridBagLayout modeLayout = new GridBagLayout();
        GridBagConstraints modeConstraints = new GridBagConstraints();
        modeConstraints.anchor = GridBagConstraints.NORTHWEST;
        modeConstraints.fill = GridBagConstraints.HORIZONTAL;
        modeConstraints.gridwidth = 1;
        modeConstraints.gridheight = 1;
        modeConstraints.gridx = 0;
        modeConstraints.gridy = 0;
        modeConstraints.insets = new Insets(5, 5, 6, 6);
        modePanel.setLayout(modeLayout);

        // Add the mode-choosing combo box to the mode panel
        modePanel.add(modeDropdown, modeConstraints);

        // Set up save data button
        saveDataButton.setToolTipText("Save current result into a TIF file");
        saveDataButton.setBackground(Color.WHITE);
        saveDataButton.setFocusable(false);
        saveDataButton.setEnabled(true);

        saveDataButton.addActionListener(listener);


        // Create filePanel and set its constraints and layout
        filePanel.setBorder(BorderFactory.createTitledBorder("File"));
        GridBagLayout fileLayout = new GridBagLayout();
        GridBagConstraints fileConstraints = new GridBagConstraints();
        fileConstraints.anchor = GridBagConstraints.NORTHWEST;
        fileConstraints.fill = GridBagConstraints.HORIZONTAL;
        fileConstraints.gridwidth = 1;
        fileConstraints.gridheight = 1;
        fileConstraints.weightx = 1.0;
        fileConstraints.gridx = 0;
        fileConstraints.gridy = 0;
        fileConstraints.insets = new Insets(5, 5, 6, 6);
        filePanel.setLayout(fileLayout);

        // Add the save data button to the file panel
        filePanel.add(saveDataButton, fileConstraints);

        // Create parameter panel and set its constraints and layout
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

        // Add text fields for each parameter into the parameter panel
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

        //TODO: reorder imagePanel with gridbaglayout if needed and put it into a new centerPanel
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        imagePanel.setPreferredSize(new Dimension(300, 300));
        imagePanel.setLayout(new BorderLayout());
        JLabel dropFileLabel = new JLabel("Drop image here or click to select file", SwingConstants.CENTER);
        imagePanel.add(dropFileLabel, BorderLayout.CENTER);

        imagePanel.setTransferHandler(new TransferHandler() {
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            public boolean importData(TransferSupport support) {
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

        // Create constraints for a filler panel that will take up all unused space beneath other panels.
        GridBagConstraints fillerConstraints = new GridBagConstraints();
        fillerConstraints.gridx = 0;
        fillerConstraints.gridy = 2;
        fillerConstraints.weighty = 1.0;

        // Create layout and constraints for the left main panel
        GridBagLayout leftPanelLayout = new GridBagLayout();
        GridBagConstraints leftPanelConstraints = new GridBagConstraints();
        leftPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        leftPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        leftPanelConstraints.gridwidth = 1;
        leftPanelConstraints.gridheight = 1;
        leftPanelConstraints.gridx = 0;
        leftPanelConstraints.gridy = 0;
        leftPanelConstraints.insets = new Insets(1, 5, 1, 6);
        leftPanel.setLayout(leftPanelLayout);

        // Add the mode and file panels to the left main panel
        leftPanel.add(modePanel, leftPanelConstraints);
        leftPanelConstraints.gridy++;
        leftPanel.add(filePanel, leftPanelConstraints);
        leftPanel.add(leftFillerPanel, fillerConstraints);

        // Create the layout and constraints for the right main panel
        GridBagLayout rightPanelLayout = new GridBagLayout();
        GridBagConstraints rightPanelConstraints = new GridBagConstraints();
        rightPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        rightPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        rightPanelConstraints.gridwidth = 1;
        rightPanelConstraints.gridheight = 1;
        rightPanelConstraints.gridx = 0;
        rightPanelConstraints.gridy = 0;
        rightPanelConstraints.insets = new Insets(5, 5, 6, 6);
        rightPanel.setLayout(rightPanelLayout);

        // Add the parameter panel to the right main panel
        rightPanel.add(parameterPanel, rightPanelConstraints);
        rightPanel.add(rigthFillerPanel, fillerConstraints);

        // add the left, right, and center panels to the main frame
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // TODO: JOptionPane.showMessageDialog(null, "HELLO THERE");
    }

    private ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            final String command = e.getActionCommand();

            if(e.getSource() == saveDataButton) {
                STMapAutoPlugin.runPlugin();
            }
        }
    };

    public static int getMinVal() {
        return convertToInt(minField);
    }
    public static int getMaxVal() {
        return convertToInt(maxField);
    }
    public static String getUnit() {
        return unitField.getText();
    }
    public static double getWidthVal() {
        return convertToDouble(widthField);
    }
    public static double getHeightVal() {
        return convertToDouble(heightField);
    }
    public static double getDepthVal() {
        return convertToDouble(depthField);
    }
    public static String getMode() {
        return modeDropdown.getItemAt(modeDropdown.getSelectedIndex());
    }

    public static int convertToInt(JTextField textField) {
        String text = textField.getText();
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            // Handle the exception, e.g., show an error message or use a default value
            System.err.println("Invalid input: " + text + ". Please enter a valid number.");
            value = Integer.MIN_VALUE; // Or any other default value as needed
        }
        return value;
    }
    public static double convertToDouble(JTextField textField) {
        String text = textField.getText();
        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            // Handle the exception, e.g., show an error message or use a default value
            System.err.println("Invalid input: " + text + ". Please enter a valid number.");
            value = Double.NaN; // Or any other default value as needed
        }
        return value;
    }

    public static void main(String[] args) {
        new ImageJ();
        new STMapAutoGUI().run("");
    }
}
