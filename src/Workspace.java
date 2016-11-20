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

        imagePanel = new JPanel();
//        imagePanel.setPreferredSize(new Dimension(300,400));
        mainPanel.add(imagePanel, BorderLayout.CENTER);



        histogramPanel = new JPanel();
//        histogramPanel.setPreferredSize(new Dimension(300,400));
        mainPanel.add(histogramPanel);

        operationsPanel = new JPanel();
//        operationsPanel.setPreferredSize(new Dimension(300,400));
        mainPanel.add(operationsPanel);

        layout.putConstraint(SpringLayout.WEST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.WEST, mainPanel);
        layout.putConstraint(SpringLayout.EAST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.WEST, histogramPanel);
        layout.putConstraint(SpringLayout.EAST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.WEST, operationsPanel);
        layout.putConstraint(SpringLayout.WEST, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, imagePanel);
        layout.putConstraint(SpringLayout.EAST, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, mainPanel);
        layout.putConstraint(SpringLayout.EAST, operationsPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, mainPanel);

        layout.putConstraint(SpringLayout.NORTH, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.SOUTH, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.NORTH, operationsPanel);
        layout.putConstraint(SpringLayout.SOUTH, operationsPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.SOUTH, mainPanel);

        layout.putConstraint(SpringLayout.NORTH, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.SOUTH, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.SOUTH, mainPanel);

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
        imagePanel.add(new GUIStyler.ImagePanel(img));

        histogramPanel.removeAll();
        histogramPanel.add(new GUIStyler.ImagePanel(srcImageServer.getHistogram().createImg2()));

        operationsPanel.removeAll();
        operationsPanel.add(new GUIStyler.PresenterTabOperations(srcImageServer.operationManager));

        window.pack();
        window.repaint();

    }


}
