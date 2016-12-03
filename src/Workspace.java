import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

    Jmagination jmagination;

    public Workspace(Jmagination jmagination) {

        this.jmagination = jmagination;

        window = new JFrame("Jmagination Workspace");
        window.setPreferredSize(ConstantsInitializers.GUI_IMAGEWINDOW_SIZE);
        window.setMinimumSize(ConstantsInitializers.GUI_IMAGEWINDOW_SIZE);
        window.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));

        layout = new SpringLayout();

        mainPanel = new JPanel(layout);
        window.setContentPane(mainPanel);

        mainPanel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        this.srcImageServer = null;


        imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createLineBorder(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR));

        JLabel imagePanelLabel = new JLabel("Image");
        imagePanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(imagePanelLabel, BorderLayout.NORTH);

        JPanel imagePanelCentral = new GUIStyler.ImagePanel2(this.srcImageServer.getImg());
        imagePanel.add(imagePanelCentral, BorderLayout.CENTER);

        histogramPanel = new JPanel();
        histogramPanel.setLayout(new BorderLayout());

        JLabel histogramPanelLabel = new JLabel("Histogram");
        histogramPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        histogramPanelLabel.setFont(ConstantsInitializers.GUI_FONT_TITLE);
        histogramPanel.add(histogramPanelLabel, BorderLayout.NORTH);

        JPanel histogramPanelCentral = new GUIStyler.ImagePanel2(srcImageServer.getHistogram().createImg2());
        histogramPanelCentral.setBackground(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
        histogramPanelCentral.setBorder(BorderFactory.createLineBorder(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR));

        histogramPanel.add(histogramPanelCentral, BorderLayout.CENTER);

        JPanel operationsPanelCentral=new GUIStyler.PresenterTabOperations(Operations.registerOperationsForImageServer(srcImageServer, jmagination));


        operationsPanel = new JPanel();
        operationsPanel.setLayout(new BorderLayout());

        JLabel operationsPanelLabel = new JLabel("Image Operations");
        operationsPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        operationsPanelLabel.setFont(ConstantsInitializers.GUI_FONT_TITLE);
        operationsPanel.add(operationsPanelLabel, BorderLayout.NORTH);

        operationsPanel.add(operationsPanelCentral, BorderLayout.CENTER);

        operationsPanel.setBorder(BorderFactory.createLineBorder(ConstantsInitializers.GUI_CHARTS_CONSTR_COLOR));
        operationsPanel.setMinimumSize(ConstantsInitializers.GUI_WORKSCACE_OPER_PANEL_SIZE);
        operationsPanel.setPreferredSize(ConstantsInitializers.GUI_WORKSCACE_OPER_PANEL_SIZE);

    }

    public Workspace(Jmagination jmagination, ImageServer srcImageServer) {
        this(jmagination);
        setImageserver(srcImageServer);
    }

    public void setImageserver(ImageServer srcImageServer) {
        this.srcImageServer = srcImageServer;

        mainPanel.removeAll();

        BufferedImage img = OperationDuplicate.duplicateImageFunction(srcImageServer.getImg());




        rebuildPanel();

        fillMainPanel();

        System.out.println(mainPanel.getSize());

    }

    public void rebuildPanel() {

        mainPanel.add(imagePanel);
        mainPanel.add(histogramPanel);
        mainPanel.add(operationsPanel);


        layout.putConstraint(SpringLayout.WEST, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.WEST, mainPanel);
        layout.putConstraint(SpringLayout.WEST, operationsPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.WEST, mainPanel);

        layout.putConstraint(SpringLayout.NORTH, operationsPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.NORTH, histogramPanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.SOUTH, operationsPanel);

        layout.putConstraint(SpringLayout.WEST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, operationsPanel);
        layout.putConstraint(SpringLayout.WEST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, histogramPanel);
        layout.putConstraint(SpringLayout.EAST, imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.EAST, mainPanel);
        layout.putConstraint(SpringLayout.NORTH , imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.NORTH, mainPanel);
        layout.putConstraint(SpringLayout.SOUTH , imagePanel, ConstantsInitializers.GUI_WORKSPACEWINDOW_GAP_SIZE, SpringLayout.SOUTH, mainPanel);


        window.pack();

        window.setPreferredSize(layout.preferredLayoutSize(mainPanel));
        window.setMinimumSize(layout.preferredLayoutSize(mainPanel));

        window.repaint();

        window.setVisible(true);


        window.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension newWindowSize = window.getSize();
                double leftPanelWidth = histogramPanel.getWidth();
                double leftPanelHeight = operationsPanel.getHeight() + histogramPanel.getHeight();

                Dimension newImagePanelSize = new Dimension((int) ( newWindowSize.getWidth() - leftPanelWidth), (int) leftPanelHeight);
                imagePanel.setMinimumSize(newImagePanelSize);
                imagePanel.setPreferredSize(newImagePanelSize);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }


    private void fillMainPanel() {

        Dimension operationsPanelSize = operationsPanel.getSize();
        Dimension histogramPanelSize = histogramPanel.getSize();

        double leftPaneWidth = operationsPanelSize.getWidth() > histogramPanelSize.getWidth() ? operationsPanelSize.getWidth() : histogramPanelSize.getWidth();
        double leftPanelHeight = operationsPanelSize.getHeight() + histogramPanelSize.getHeight();

        Dimension newOperationsPanelSize = new Dimension((int)leftPaneWidth, (int) operationsPanelSize.getHeight());
        System.out.println("newOperatoinsPanelSize :" + newOperationsPanelSize);

        Dimension newHistogramPanelSize = new Dimension((int)leftPaneWidth, (int) histogramPanelSize.getHeight());
        System.out.println("newHistogramPanelSize :" + newHistogramPanelSize);


        operationsPanel.setPreferredSize(newOperationsPanelSize);
        operationsPanel.setMinimumSize(newOperationsPanelSize);
        histogramPanel.setPreferredSize(newHistogramPanelSize);
        histogramPanel.setMinimumSize(newHistogramPanelSize);

        Dimension mainPanelSize = mainPanel.getSize();
        Dimension windowSize = window.getSize();
        Dimension newImagePanelSize = new Dimension( (int) (windowSize.getWidth()-leftPaneWidth), (int) leftPanelHeight);

        imagePanel.setPreferredSize(newImagePanelSize);
        imagePanel.setMinimumSize(newImagePanelSize);


        Dimension newMainPanelSize = new Dimension((int) (leftPaneWidth + newImagePanelSize.getWidth()), (int) leftPanelHeight);
        System.out.println("newMainPanelSize :" + newMainPanelSize);

        System.out.println("newImagePanelSize :" + newImagePanelSize);

        window.pack();
        window.repaint();

        window.setMinimumSize(newMainPanelSize);
        window.setPreferredSize(newMainPanelSize);
        System.out.println("window.getMinimumSize() :" + window.getMinimumSize());
        System.out.println("window.getPreferredSize() :" + window.getPreferredSize());
        System.out.println("window.getBounds() :" + window.getBounds());
        System.out.println("mainPanel.getSize() :" + mainPanel.getSize());

    }

}
