import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    JPanel managerPanelNorth;
    JPanel managerPanelCentral;
    JPanel managerPanelSouth;
    JPanel imagePanel;
    JPanel imagePanelNorth;
    JPanel imagePanelCentral;
    JPanel imagePanelSouth;
    JPanel histogramPanel;
    JPanel histogramPanelNorth;
    JPanel histogramPanelCentral;
    JPanel histogramPanelSouth;
    JPanel operationsPanel;
    JPanel operationsPanelNorth;
    JPanel operationsPanelCentral;
    JPanel operationsPanelSouth;

    JScrollPane managerScroller;

    /* WINDOW PANELING SCHEMA

        *************************************************************************
        *                                              *                        *
        *                                              *                        *
        *                                      level0SplitPane                  *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        * level0Left                                   * level0Right            *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *                                              *                        *
        *************************************************************************


        *************************************************************************
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                  level0Left                  *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        * left1Left           * left1Right             *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *************************************************************************


        *************************************************************************
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                  level0Left                  *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        * left1Left           * left1Right             *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *************************************************************************


        *************************************************************************
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     * operationsPanel        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        * operationsPanel     ******* level1Right ****** imagePanel             *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     * histogramPanel         *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *************************************************************************


        *************************************************************************
        * managerPanelNorth   * operationsPanelNorth   * imagePanelNorth        *
        *************************************************************************
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     * operationsPanelCentral *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     **************************                        *
        *                     * operationsPanelSouth   *                        *
        * managerPanelCentral **************************                        *
        *                     * histogramPanelNorth    *                        *
        *                     ************************** imagePanelCentral      *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     * histogramPanelCentral  *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *************************************************************************
        * managerPanelSouth   * histogramPanelSouth    * imagePanelSouth        *
        *************************************************************************

     */

    Dimension windowDimension = new Dimension();

    Dimension level0SplitPaneDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_level0SplitPane);
    Dimension level0LeftDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_level0Left);
    Dimension level0RightDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_level0Right);

    Dimension level1LeftDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_level1Left);
    Dimension level1RightDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_level1Right);

    Dimension managerPanelDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_managerPanel);
    Dimension managerPanelNorthDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_managerPanelNorth);
    Dimension managerPanelCentralDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_managerPanelCentral);
    Dimension managerPanelSouthDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_managerPanelSouth);
    Dimension imagePanelDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_imagePanel);
    Dimension imagePanelNorthDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_imagePanelNorth);
    Dimension imagePanelCentralDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_imagePanelCentral);
    Dimension imagePanelSouthDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_imagePanelSouth);
    Dimension histogramPanelDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_histogramPanel);
    Dimension histogramPanelNorthDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_histogramPanelNorth);
    Dimension histogramPanelCentralDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_histogramPanelCentral);
    Dimension histogramPanelSouthDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_histogramPanelSouth);
    Dimension operationsPanelDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_operationsPanel);
    Dimension operationsPanelNorthDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_operationsPanelNorth);
    Dimension operationsPanelCentralDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_operationsPanelCentral);
    Dimension operationsPanelSouthDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_operationsPanelSouth);

    Dimension managerScrollerDimension = new Dimension(ConstantsInitializers.GUI_DIMENSION_managerPanelCentral);

    ImageManager imageManager;

    JButton jButtonForNewImage;





    public Workspace(ImageManager imageManager) {

        this.imageManager = imageManager;
        this.srcImageServer = null;

        managerScroller = new JScrollPane(imageManager.getTree());
        managerScroller.setPreferredSize(ConstantsInitializers.GUI_DIMENSION_managerPanelCentral);
//        managerScroller.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_managerPanelCentral);


        supplyLoadFromFileButton();

        buildWindow();


        JTree imageManagerTree = imageManager.getTree();


        Workspace workspace = this;
        imageManagerTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {


                JTree tree = (JTree)e.getSource();

                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1 && e.getClickCount() == 1 && selPath != null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

//                    System.out.println("Selected node" + selectedNode.toString());
//                    System.out.println("UserObject: " + selectedNode.getUserObject().toString());
                    ImageServer imageServer = (ImageServer) selectedNode.getUserObject();

                    if(e.getButton() == MouseEvent.BUTTON1) {
                        if(imageServer.windowIsVisible() == true) {
                            imageServer.windowToFront();
                        }
                    }

                    if(e.getButton() == MouseEvent.BUTTON3) {
                        JPopupMenu jPopupMenu = new JPopupMenu("Popup menu");

                        JMenuItem jMenuItemShowImage = new JMenuItem("Pokaż/Ukryj okno obrazu");
                        jMenuItemShowImage.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                imageServer.toogleWindow();
                            }
                        });


                        jPopupMenu.add(jMenuItemShowImage);

                        JMenuItem jMenuItemPlaceInWorkspace = new JMenuItem("Umieść na biurku");
                        jMenuItemPlaceInWorkspace.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                workspace.setImageServer(imageServer);
                            }
                        });

                        jPopupMenu.add(jMenuItemPlaceInWorkspace);
                        jPopupMenu.show(tree, e.getX(), e.getY());

                    }
                }
            }
        });
    }

    public Workspace(ImageManager imageManager, ImageServer srcImageServer) {
        this(imageManager);

        buildWindow();
        setImageServer(srcImageServer);
        window.repaint();
    }

    public void setImageServer(ImageServer srcImageServer) {
        this.srcImageServer = srcImageServer;

        imagePanelCentral.removeAll();
        imagePanelCentral.add(new GUIStyler.ImagePanel2(this.srcImageServer.getImg()));


        histogramPanelCentral.removeAll();
        histogramPanelCentral.add(new GUIStyler.ImagePanel2(srcImageServer.getHistogram().createImg("INTERLACED", ConstantsInitializers.GUI_DIMENSION_histogramPanelCentral)));

        operationsPanelCentral.removeAll();
        operationsPanelCentral.add(new GUIStyler.PresenterTabOperations(Operations.registerOperationsForImageServer(srcImageServer), ConstantsInitializers.GUI_DIMENSION_operationsPanelCentral));

        window.pack();
        window.repaint();

        window.pack();
        Insets windowInsets = window.getInsets();
        int windowWidth = (int) level0SplitPane.getWidth() + windowInsets.left + windowInsets.right;
        int windowHeight = (int) level0SplitPane.getHeight() + windowInsets.top + windowInsets.bottom;
        window.setPreferredSize(new Dimension(windowWidth, windowHeight));
        window.pack();
        window.setVisible(true);

    }

    private void supplyLoadFromFileButton() {

        jButtonForNewImage = new JButton("Otwórz plik");

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
                        ImageServer imageServer;
                        imageServer = ImageServer.createLoadedImageServer(loaded, chooser.getSelectedFile().getAbsolutePath(), imageManager);
                        setImageServer(imageServer);
                    }
                }
            }
        });

    }

    private void updateComponentsDimensions() {

        //TODO
        windowDimension = window.getSize();
        level0SplitPaneDimension = level0SplitPaneDimension.getSize();
        level0LeftDimension = level0LeftDimension.getSize();
        level0RightDimension = level0RightDimension.getSize();
        level1LeftDimension = level1LeftDimension.getSize();
        level1RightDimension = level1RightDimension.getSize();
        managerPanelDimension = managerPanelDimension.getSize();
        managerPanelCentralDimension = managerPanelCentralDimension.getSize();
        managerPanelSouthDimension = managerPanelSouthDimension.getSize();
        imagePanelDimension = imagePanelDimension.getSize();
        imagePanelNorthDimension = imagePanelNorth.getSize();
        imagePanelCentralDimension = imagePanelCentralDimension.getSize();
        imagePanelCentralDimension = imagePanelCentralDimension.getSize();
        histogramPanelDimension = histogramPanelDimension.getSize();
        histogramPanelCentralDimension = histogramPanelCentralDimension.getSize();
        operationsPanelDimension = operationsPanelDimension.getSize();
        operationsPanelCentralDimension = operationsPanelCentralDimension.getSize();
        managerScrollerDimension = managerScrollerDimension.getSize();

    }

    private void buildWindow() {

        window = new JFrame("Jmagination - Biurko");


        window.setPreferredSize(windowDimension);
        window.setMinimumSize(windowDimension);
        window.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));

        // content panels init
        managerPanel = new JPanel(new BorderLayout());
        imagePanel = new JPanel(new BorderLayout());
        histogramPanel = new JPanel(new BorderLayout());
        operationsPanel = new JPanel(new BorderLayout());


/*        managerPanel.setMinimumSize(managerPanelDimension);
        imagePanel.setMinimumSize(imagePanelDimension);
        operationsPanel.setMinimumSize(operationsPanelDimension);
        histogramPanel.setMinimumSize(histogramPanelDimension);*/

        managerPanel.setPreferredSize(managerPanelDimension);
        imagePanel.setPreferredSize(imagePanelDimension);
        operationsPanel.setPreferredSize(operationsPanelDimension);
        histogramPanel.setPreferredSize(histogramPanelDimension);


        // content direct holders
        managerPanelNorth = new JPanel();
        managerPanelCentral = new JPanel();
        managerPanelSouth = new JPanel();
        imagePanelNorth = new JPanel();
        imagePanelCentral = new JPanel();
        imagePanelSouth = new JPanel();
        histogramPanelNorth = new JPanel();
        histogramPanelCentral = new JPanel();
        histogramPanelSouth = new JPanel();
        operationsPanelNorth = new JPanel();
        operationsPanelCentral = new JPanel();
        operationsPanelSouth = new JPanel();

        managerPanelNorth.setPreferredSize(managerPanelNorthDimension);
        managerPanelCentral.setPreferredSize(managerPanelCentralDimension);
        managerPanelSouth.setPreferredSize(managerPanelSouthDimension);
        imagePanelNorth.setPreferredSize(imagePanelNorthDimension);
        imagePanelCentral.setPreferredSize(imagePanelCentralDimension);
        imagePanelSouth.setPreferredSize(imagePanelSouthDimension);
        histogramPanelNorth.setPreferredSize(histogramPanelNorthDimension);
        histogramPanelCentral.setPreferredSize(histogramPanelCentralDimension);
        histogramPanelSouth.setPreferredSize(histogramPanelSouthDimension);
        operationsPanelNorth.setPreferredSize(operationsPanelNorthDimension);
        operationsPanelCentral.setPreferredSize(operationsPanelCentralDimension);
        operationsPanelSouth.setPreferredSize(operationsPanelSouthDimension);

/*        managerPanelNorth.setMinimumSize(managerPanelNorthDimension);
        managerPanelCentral.setMinimumSize(managerPanelCentralDimension);
        managerPanelSouth.setMinimumSize(managerPanelSouthDimension);
        imagePanelNorth.setMinimumSize(imagePanelNorthDimension);
        imagePanelCentral.setMinimumSize(imagePanelCentralDimension);
        imagePanelSouth.setMinimumSize(imagePanelSouthDimension);
        histogramPanelNorth.setMinimumSize(histogramPanelNorthDimension);
        histogramPanelCentral.setMinimumSize(histogramPanelCentralDimension);
        histogramPanelSouth.setMinimumSize(histogramPanelSouthDimension);
        operationsPanelNorth.setMinimumSize(operationsPanelNorthDimension);
        operationsPanelCentral.setMinimumSize(operationsPanelCentralDimension);
        operationsPanelSouth.setMinimumSize(operationsPanelSouthDimension);*/


        managerPanel.add(managerPanelNorth, BorderLayout.NORTH);
        managerPanel.add(managerPanelCentral, BorderLayout.CENTER);
        managerPanel.add(managerPanelSouth, BorderLayout.SOUTH);

        imagePanel.add(imagePanelNorth, BorderLayout.NORTH);
        imagePanel.add(imagePanelCentral, BorderLayout.CENTER);
        imagePanel.add(imagePanelSouth, BorderLayout.SOUTH);

        histogramPanel.add(histogramPanelNorth, BorderLayout.NORTH);
        histogramPanel.add(histogramPanelCentral, BorderLayout.CENTER);
        histogramPanel.add(histogramPanelSouth, BorderLayout.SOUTH);

        operationsPanel.add(operationsPanelNorth, BorderLayout.NORTH);
        operationsPanel.add(operationsPanelCentral, BorderLayout.CENTER);
        operationsPanel.add(operationsPanelSouth, BorderLayout.SOUTH);

        // content panel labels
        JLabel managerPanelLabel = new JLabel("Menedżer obrazów");
        managerPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        managerPanelNorth.add(managerPanelLabel);

        JLabel imagePanelLabel = new JLabel("Obraz");
        imagePanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanelNorth.add(imagePanelLabel);

        JLabel histogramPanelLabel = new JLabel("Histogram");
        histogramPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        histogramPanelNorth.add(histogramPanelLabel);

        JLabel operationsPanelLabel = new JLabel("Operacje");
        operationsPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        operationsPanelNorth.add(operationsPanelLabel);

        // static content
        managerPanelSouth.add(jButtonForNewImage);
        managerPanelCentral.add(managerScroller);

        level1Left = new JPanel(new BorderLayout());
        level1Left.setPreferredSize(ConstantsInitializers.GUI_DIMENSION_level1Left);
//        level1Left.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level1Left);
        level1Left.add(managerPanel);

        level1Right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, operationsPanel, histogramPanel);
        level1Right.setPreferredSize(ConstantsInitializers.GUI_DIMENSION_level1Right);
//        level1Right.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level1Right);

        level0Left = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, level1Left, level1Right);
        level0Left.setPreferredSize(ConstantsInitializers.GUI_DIMENSION_level0Left);
//        level0Left.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level0Left);

        level0Right = new JPanel(new BorderLayout());
        level0Right.setPreferredSize(ConstantsInitializers.GUI_DIMENSION_level0Right);
//        level0Right.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level0Right);

        level1Right.setResizeWeight(0.5);
        level1Right.setDividerSize(ConstantsInitializers.GUI_DIMENSION_splitPaneDividerSize);

        level0Left.setResizeWeight(0.5);
        level0Left.setDividerSize(ConstantsInitializers.GUI_DIMENSION_splitPaneDividerSize);

        level0Right.add(imagePanel);

        level0SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, level0Left, level0Right);
        level0SplitPane.setPreferredSize(ConstantsInitializers.GUI_DIMENSION_level0SplitPane);
//        level0SplitPane.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level0SplitPane);

        level0SplitPane.setResizeWeight(0.25);
        level0SplitPane.setDividerSize(ConstantsInitializers.GUI_DIMENSION_splitPaneDividerSize);



        window.add(level0SplitPane);

        window.pack();
        Insets windowInsets = window.getInsets();
        int windowWidth = (int) ConstantsInitializers.GUI_DIMENSION_level0SplitPane.getWidth() + windowInsets.left + windowInsets.right;
        int windowHeight = (int) ConstantsInitializers.GUI_DIMENSION_level0SplitPane.getHeight() + windowInsets.top + windowInsets.bottom;
        window.setPreferredSize(new Dimension(windowWidth, windowHeight));
        window.pack();
        window.setVisible(true);
    }




}
