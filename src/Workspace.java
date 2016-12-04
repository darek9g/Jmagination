import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 19.11.2016.
 */
public class Workspace{

    ImageServer srcImageServer;


    JFrame window;
    JSplitPane level0SplitPane;
    JSplitPane level0Left;
    JPanel level0Right;

    JPanel level1Left;
    JSplitPane level1Right;

    JPanel managerPanel;
    JPanel managerPanelCentral;
    JPanel imagePanel;
    JPanel imagePanelCentral;
    JPanel histogramPanel;
    JPanel histogramPanelCentral;
    JPanel operationsPanel;
    JPanel operationsPanelCentral;

    Jmagination jmagination;

    public Workspace(Jmagination jmagination) {

        this.jmagination = jmagination;
        this.srcImageServer = null;

        buildWindow();

    }

    public Workspace(Jmagination jmagination, ImageServer srcImageServer) {
        this(jmagination);
        setImageserver(srcImageServer);
        window.repaint();
    }

    public void setImageserver(ImageServer srcImageServer) {
        this.srcImageServer = srcImageServer;

        //BufferedImage img = OperationDuplicate.duplicateImageFunction(srcImageServer.getImg());

        imagePanelCentral.removeAll();
        imagePanelCentral.add(new GUIStyler.ImagePanel2(this.srcImageServer.getImg()));

        histogramPanelCentral.removeAll();
        histogramPanelCentral.add(new GUIStyler.ImagePanel2(srcImageServer.getHistogram().createImg2()));

        operationsPanelCentral.removeAll();
        operationsPanelCentral.add(new GUIStyler.PresenterTabOperations(Operations.registerOperationsForImageServer(srcImageServer, jmagination)));

    }


    private void buildWindow() {

        window = new JFrame("Jmagination Workspace");


        window.setPreferredSize(ConstantsInitializers.GUI_IMAGEWINDOW_SIZE);
        window.setMinimumSize(ConstantsInitializers.GUI_IMAGEWINDOW_SIZE);
        window.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));





        // content panels init
        managerPanel = new JPanel(new BorderLayout());
        imagePanel = new JPanel(new BorderLayout());
        histogramPanel = new JPanel(new BorderLayout());
        operationsPanel = new JPanel(new BorderLayout());

        // content panel labels
        JLabel managerPanelLabel = new JLabel("Manager");
        managerPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        managerPanel.add(managerPanelLabel, BorderLayout.NORTH);

        JLabel imagePanelLabel = new JLabel("Image");
        imagePanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imagePanelLabel, BorderLayout.NORTH);

        JLabel histogramPanelLabel = new JLabel("Histogram");
        histogramPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        histogramPanel.add(histogramPanelLabel, BorderLayout.NORTH);

        JLabel operationsPanelLabel = new JLabel("Operations");
        operationsPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        operationsPanel.add(operationsPanelLabel, BorderLayout.NORTH);

        // content direct holders
        managerPanelCentral = new JPanel();
        imagePanelCentral = new JPanel();
        histogramPanelCentral = new JPanel();
        operationsPanelCentral = new JPanel();

        managerPanel.add(managerPanelCentral, BorderLayout.CENTER);
        imagePanel.add(imagePanelCentral, BorderLayout.CENTER);
        histogramPanel.add(histogramPanelCentral, BorderLayout.CENTER);
        operationsPanel.add(operationsPanelCentral, BorderLayout.CENTER);




/*        operationsPanel.setBorder(BorderFactory.createLineBorder(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR));
        operationsPanel.setMinimumSize(ConstantsInitializers.GUI_WORKSCACE_OPER_PANEL_SIZE);
        operationsPanel.setPreferredSize(ConstantsInitializers.GUI_WORKSCACE_OPER_PANEL_SIZE);*/


        level1Left = new JPanel(new BorderLayout());
        level1Left.add(managerPanel);

        level1Right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, operationsPanel, histogramPanel);

        level0Left = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, level1Left, level1Right);
        level0Right = new JPanel(new BorderLayout());
        level0Right.add(imagePanel);

        level0SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, level0Left, level0Right);



        window.add(level0SplitPane);

        window.pack();
        window.setVisible(true);
    }




}
