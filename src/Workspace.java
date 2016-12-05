import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

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
    JPanel managerPanelSouth;
    JPanel imagePanel;
    JPanel imagePanelCentral;
    JPanel histogramPanel;
    JPanel histogramPanelCentral;
    JPanel operationsPanel;
    JPanel operationsPanelCentral;

    JScrollPane managerScroller;

    ImageManager imageManager;

    JButton jButtonForNewImage;





    public Workspace(ImageManager imageManager) {

        this.imageManager = imageManager;
        this.srcImageServer = null;

        managerScroller = new JScrollPane(imageManager.getTree());


        supplyLoadFromFileButton();

        buildWindow();
    }

    public Workspace(ImageManager imageManager, ImageServer srcImageServer) {
        this(imageManager);

        buildWindow();
        setImageserver(srcImageServer);
        window.repaint();
    }

    public void setImageserver(ImageServer srcImageServer) {
        this.srcImageServer = srcImageServer;

        imagePanelCentral.removeAll();
        imagePanelCentral.add(new GUIStyler.ImagePanel2(this.srcImageServer.getImg()));

        histogramPanelCentral.removeAll();
        histogramPanelCentral.add(new GUIStyler.ImagePanel2(srcImageServer.getHistogram().createImg2()));

        operationsPanelCentral.removeAll();
        operationsPanelCentral.add(new GUIStyler.PresenterTabOperations(Operations.registerOperationsForImageServer(srcImageServer)));

        window.pack();
        window.repaint();

    }

    private void supplyLoadFromFileButton() {

        jButtonForNewImage = new JButton("Open file");
/*        jButtonForNewImage.setPreferredSize(new Dimension(200,100));
        jButtonForNewImage.setMinimumSize(new Dimension(200,100));*/

        jButtonForNewImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
                chooser.setCurrentDirectory(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Pictures"));
                int chooserResult = chooser.showOpenDialog(window);
                if(chooserResult == JFileChooser.APPROVE_OPTION) {

                    BufferedImage loaded = ImageServer.LoadImageFromFile(chooser.getSelectedFile().getAbsolutePath());

                    if(loaded!=null) {
                        ImageServer.createLoadedImageServer(loaded, chooser.getSelectedFile().getAbsolutePath(), imageManager);
                    }
                }
            }
        });

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
        managerPanelSouth = new JPanel();
        imagePanelCentral = new JPanel();
        histogramPanelCentral = new JPanel();
        operationsPanelCentral = new JPanel();

        managerPanel.add(managerPanelSouth, BorderLayout.CENTER);
        imagePanel.add(imagePanelCentral, BorderLayout.CENTER);
        histogramPanel.add(histogramPanelCentral, BorderLayout.CENTER);
        operationsPanel.add(operationsPanelCentral, BorderLayout.CENTER);


        // static content
        managerPanel.add(jButtonForNewImage, BorderLayout.SOUTH);
        managerPanelSouth.add(managerScroller);

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
