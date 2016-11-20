import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 19.11.2016.
 */
public class Workspace{

    JFrame window;
    ImageServer srcImageServer;
    JPanel mainPanel;
    JPanel imagePanel;
    JPanel histogramPanel;
    JPanel operationsPanel;

    SpringLayout layout;

    public Workspace() {
        window = new JFrame("Workspace");
        window.setPreferredSize(ConstantsInitializers.GUI_IMAGEWINDOW_SIZE);
        //window.setMinimumSize(ConstantsInitializers.GUI_IMAGEWINDOW_SIZE);
        window.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));

        layout = new SpringLayout();

        mainPanel = new JPanel(layout);
        window.setContentPane(mainPanel);

        mainPanel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
        mainPanel.add(imagePanel);



        histogramPanel = new JPanel(new BorderLayout());
        histogramPanel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
//        histogramPanel.setPreferredSize(new Dimension(300,400));
        mainPanel.add(histogramPanel);

        operationsPanel = new JPanel(new GridBagLayout());
        operationsPanel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);
        mainPanel.add(operationsPanel);


        layout.putConstraint(SpringLayout.WEST, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.WEST, mainPanel);
        layout.putConstraint(SpringLayout.WEST, operationsPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.WEST, mainPanel);

        layout.putConstraint(SpringLayout.NORTH, operationsPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.NORTH, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.SOUTH, operationsPanel);
        layout.putConstraint(SpringLayout.SOUTH, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.SOUTH, mainPanel);

        layout.putConstraint(SpringLayout.WEST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, operationsPanel);
        layout.putConstraint(SpringLayout.WEST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, histogramPanel);
        layout.putConstraint(SpringLayout.EAST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, mainPanel);
//        layout.putConstraint(SpringLayout.NORTH, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER , imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.VERTICAL_CENTER, mainPanel);

        window.setVisible(true);

        this.srcImageServer = null;

    }

    public Workspace(ImageServer srcImageServer) {
        this();
        setImageserver(srcImageServer);
    }

    public void setImageserver(ImageServer srcImageServer) {
        this.srcImageServer = srcImageServer;

        BufferedImage img = Operations.duplicateImageFunction(srcImageServer.getImg());

        imagePanel.removeAll();
        imagePanel.add(new GUIStyler.ImagePanel2(img),BorderLayout.CENTER);

        //imagePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));

        histogramPanel.removeAll();
        histogramPanel.add(new GUIStyler.ImagePanel(srcImageServer.getHistogram().createImg2()),BorderLayout.CENTER);
        //histogramPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        histogramPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));

        operationsPanel.removeAll();
        operationsPanel.add(new GUIStyler.PresenterTabOperations2(srcImageServer.operationManager),new GUIStyler.ParamsGrid(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
//        operationsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        operationsPanel.setBorder(BorderFactory.createLineBorder(Color.CYAN));

        //fillMainPanel();

        window.pack();
        window.repaint();

        System.out.println(mainPanel.getSize());

    }


    private void fillMainPanel() {
        Dimension mainPanelSize = mainPanel.getSize();

        Dimension operationsPanelSize = operationsPanel.getSize();
        Dimension histogramPanelSize = histogramPanel.getSize();

        double leftPaneWidth = operationsPanelSize.getWidth() > histogramPanelSize.getWidth() ? operationsPanelSize.getWidth() : histogramPanelSize.getWidth();
        double leftPanelHeight = operationsPanelSize.getHeight() + histogramPanelSize.getHeight();

        Dimension newOperationsPanelSize = new Dimension((int)leftPaneWidth, (int) operationsPanelSize.getHeight());
        Dimension newHistogramPanelSize = new Dimension((int)leftPaneWidth, (int) histogramPanelSize.getHeight());

        Dimension newMainPanelSize = new Dimension((int)leftPaneWidth, (int) leftPanelHeight);


        operationsPanel.setSize(newOperationsPanelSize);
        histogramPanel.setSize(newHistogramPanelSize);

        mainPanel.setSize(newMainPanelSize);

        window.pack();
        window.repaint();

    }

}
