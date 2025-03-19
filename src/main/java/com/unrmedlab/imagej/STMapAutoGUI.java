package com.unrmedlab.imagej;


import ij.ImageJ;
import ij.plugin.PlugIn;
import java.awt.datatransfer.DataFlavor;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class STMapAutoGUI implements PlugIn {
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JPanel modePanel = new JPanel();
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

        //TODO: put modeDropdown initialization up top with rest of j things and maybe realign modeDropdown size-changing code

        String[] modes = {"Choose mode", "Single Image Processing", "Batch Image Processing"};
        JComboBox<String> modeDropdown = new JComboBox<>(modes);
        modeDropdown.setSelectedIndex(0);

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
                    popupMenu.setPreferredSize(new Dimension(150, 70));

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
        modePanel.add(modeDropdown);

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

        // Add the mode panel to the left panel
        leftPanel.add(modePanel);
        // Add the parameter panel to the right panel
        rightPanel.add(parameterPanel);

        // add the left, right, and center panels to the frame
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new ImageJ();
        new STMapAutoGUI().run("");
    }
}
