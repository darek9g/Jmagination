package jmagination;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import jmagination.gui.*;
import jmagination.histogram.Histogram;
import jmagination.histogram.LineProfile;
import jmagination.operations.Operation;
import jmagination.operations.OperationConvertToGray;
import jmagination.operations.OperationDuplicate;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.FileDialog;
import util.SimpleHSVBufferedImage;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.ConstantsInitializers.GUI_PREVIEW_IMAGE_HEIGHT;
import static jmagination.ConstantsInitializers.GUI_PREVIEW_IMAGE_WIDTH;

/**
 * Created by darek on 19.11.2016.
 */
public class Workspace implements RunOperation {

    ImageServer srcImageServer;


    JFrame window;
    JSplitPane level0SplitPane;
    JSplitPane level0Left;
    JPanel level0Right;

    JPanel level1Left;
    JSplitPane level1Right;

    JPanel managerPanel;
    JPanel managerPanelNorth;
    JScrollPane managerPanelCentral;
    JPanel managerPanelSouth;
    ImagePanel2 managerPanelSouthCont;
    AboutPanel managerPanelSouthAbout;
    JPanel imagePanel;
    JPanel imagePanelNorth;
    JScrollPane imagePanelCentral;
    JPanel imagePanelSouth;
    JPanel histogramPanel;
    JPanel histogramPanelNorth;
    JTabbedPane histogramPanelCentral;
    JPanel histogramPanelSouth;
    JPanel operationsPanel;
    JPanel operationsPanelNorth;
    JScrollPane operationsPanelCentral;
    PresenterTabOperations operationsPanelCentralPane;
    JPanel operationsPanelSouth;

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
        * managerPanel     ******* level1Right ****** imagePanel             *
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
        ***********************                        *                        *
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
        ***********************                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     *                        *                        *
        *                     ***************************************************
        * managerPanelSouth   * histogramPanelSouth    * imagePanelSouth        *
        *************************************************************************

     */

    Dimension windowDimension = new Dimension(1200,700);

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


    ImageManager imageManager;

    JButton jButtonOpenFile;
    JButton jButtonOpenGrayFile;
    JButton jButtonSaveFile;
    JToggleButton jButtonAbout;

    SimpleHSVBufferedImage histogramImage;

    ImagePanel4 imagePanelCont = new ImagePanel4(null);
    ImagePanel3 histogramPanelCont = new ImagePanel3(null);
    ImagePanel3 lineProfileRBGPanelCont = new ImagePanel3(null);
    ImagePanel3 lineProfileHSVPanelCont = new ImagePanel3(null);
    SimpleHSVBufferedImage originalBufferedImage = null;

    LineProfile lineProfile = new LineProfile(imagePanelCont);


    // statyczna nazwa pliku do zapisu
    public static String outputFileName = "nowy_obraz";

    public Workspace(ImageManager imageManager) {

        this.imageManager = imageManager;
        this.srcImageServer = null;


        setupJButtonOpenFile();
        setupJButtonOpenGrayFile();
        setupJButtonSaveFile();
        setupJButtonAbout();

        JTree imageManagerTree = imageManager.getTree();

        lineProfile.addDataDrawer(lineProfileRBGPanelCont, new int[]{0,1,2}, LineProfile.RGB_MODE);
        lineProfile.addDataDrawer(lineProfileHSVPanelCont, new int[]{3,4,5}, LineProfile.HSV_MODE);
        buildWindow(imageManagerTree);

        Color color = imagePanelCont.getBackground();
        imagePanelCont.setFakeImage(crateAltImage(imagePanelCont.getSize(), "Nie otwarto jeszcze żadnego pliku", color));
        color = histogramPanelCont.getBackground();
        histogramPanelCont.setImage(crateAltImage(imagePanelCont.getSize(), "Dane nie dostępne", color));


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

                        JMenuItem jMenuItemPlaceInWorkspace = new JMenuItem("Umieść na biurku");
                        jMenuItemPlaceInWorkspace.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                workspace.setImageServer(imageServer);
                            }
                        });
                        jPopupMenu.add(jMenuItemPlaceInWorkspace);

                        JMenuItem jMenuItemShowImage = new JMenuItem("Pokaż/Ukryj okno obrazu");
                        jMenuItemShowImage.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                imageServer.toogleWindow();
                            }
                        });
                        jPopupMenu.add(jMenuItemShowImage);

/*                        JMenuItem jMenuItemRemoveImage = new JMenuItem("Zamknij");
                        jMenuItemRemoveImage.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                imageServer.getMainWindow().dispose();
                            }
                        });
                        jPopupMenu.add(jMenuItemRemoveImage);*/

                        jPopupMenu.show(tree, e.getX(), e.getY());

                    }
                }
            }
        });

        imageManagerTree.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                JTree tree = (JTree)e.getSource();

                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1 && selPath != null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

                    ImageServer imageServer = (ImageServer) selectedNode.getUserObject();

                    managerPanelSouthCont.setImage(imageServer.imgPreview);
                } else {
                    managerPanelSouthCont.setImage(null);
                }


            }
        });
    }

    public Workspace(ImageManager imageManager, ImageServer srcImageServer) {
        this(imageManager);

        buildWindow(imageManager.getTree());
        setImageServer(srcImageServer);
        window.repaint();
    }

    public void setImageServer(ImageServer srcImageServer) {
        this.srcImageServer = srcImageServer;


        originalBufferedImage = OperationDuplicate.duplicateImageFunction(this.srcImageServer.getImg());
        imagePanelCont.setImage(originalBufferedImage);

        histogramImage = srcImageServer.getHistogram().createImg("INTERLACED", ConstantsInitializers.GUI_DIMENSION_histogramPanelCentral);
        histogramPanelCont.setImage(histogramImage);

        lineProfile.update();

//        operation.jButtonApply.setEnabled(true);
//        operationsPanelCentralPane.updateControls(false);

        jButtonSaveFile.setEnabled(true);

        window.repaint();
        window.setVisible(true);

    }

    private void setupJButtonOpenFile() {

        jButtonOpenFile = new JButton("Otwórz z pliku");

        jButtonOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String[] src = selectFiles();
                for(int i=0;i<src.length; i++) {
                    File file = new File(src[i]);
                    BufferedImage loaded = ImageServer.LoadImageFromFile(file.getAbsolutePath(), window);
                    if(loaded!=null) {

                        ImageServer imageServer;
                        imageServer = ImageServer.createLoadedImageServer(loaded, file.getAbsolutePath(), imageManager);
                        setImageServer(imageServer);
                    }
                }
            }
        });

    }

    private void setupJButtonOpenGrayFile() {

        jButtonOpenGrayFile = new JButton("Otwórz z pliku jako szary");

        jButtonOpenGrayFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String[] src = selectFiles();
                for(int i=0;i<src.length; i++) {
                    File file = new File(src[i]);
                    BufferedImage loaded = ImageServer.LoadImageFromFile(file.getAbsolutePath(), window);
                    if(loaded!=null) {

                        ImageServer imageServer;
                        imageServer = ImageServer.createLoadedImageServer(loaded, file.getAbsolutePath(), imageManager);
                        setImageServer(imageServer);

                        OperationConvertToGray toGray = new OperationConvertToGray();
                        runOperation(toGray);
                        saveOperationsOutput(toGray);

                    }
                }
            }
        });

    }

    private void setupJButtonSaveFile() {

        jButtonSaveFile = new JButton("Zapisz do pliku");
        jButtonSaveFile.setEnabled(false);

        jButtonSaveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String src = selectOutputFile();
                if(src != null) {
                    File file = new File(src);
                    ImageServer.SaveImageToFile(srcImageServer.getImg(), file.getAbsolutePath(), window);
                }
            }
        });

    }

    private void setupJButtonAbout() {

        jButtonAbout = new JToggleButton("O programie");

        jButtonAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                GridBagLayout layout = (GridBagLayout) managerPanelSouth.getLayout();
                GridBagConstraints c = layout.getConstraints(managerPanelSouth);

                c.gridx = 0;
                c.gridy = 0;
                c.gridwidth = GridBagConstraints.REMAINDER;
                c.gridheight = 1;

                if(jButtonAbout.isSelected()) {

                    managerPanelSouth.remove(managerPanelSouthCont);
                    managerPanelSouth.add(managerPanelSouthAbout, c);
                } else {
                    managerPanelSouth.remove(managerPanelSouthAbout);
                    managerPanelSouth.add(managerPanelSouthCont, c);
                }

                managerPanelSouth.revalidate();
                managerPanelSouth.repaint();

            }
        });

    }


    private String[] selectFiles() {
        Display display = new Display ();
        Shell shell = new Shell (display);
        org.eclipse.swt.widgets.FileDialog tDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
        String [] filterNames = new String [] {"All Files (*)"};
        String [] filterExtensions = new String [] {"*"};
        String filterPath = "C:\\Users\\\" + System.getProperty(\"user.name\") + \"\\Pictures";
        tDialog.setFilterNames (filterNames);
        tDialog.setFilterExtensions (filterExtensions);
        tDialog.setFilterPath (filterPath);
        tDialog.open();
        String[] src = tDialog.getFileNames();
        String path = tDialog.getFilterPath();

        for(int i=0; i<src.length; i++) {
            src[i] = path + System.getProperty("file.separator")+ src[i];
        }
        display.close();
        return src;
    }

    private String selectOutputFile() {
        Display display = new Display ();
        Shell shell = new Shell (display);
        org.eclipse.swt.widgets.FileDialog tDialog = new FileDialog(shell, SWT.SAVE | SWT.SINGLE);
        tDialog.setOverwrite(true);
        String [] filterNames = new String [] {"Image Files", "All Files (*)"};
        String [] filterExtensions = new String [] {"*.gif;*.png;*.jpg;*.jpeg", "*"};
        String filterPath = "~";

        String platform = SWT.getPlatform();
        if (platform.equals("win32")) {
            filterNames = new String [] {"Image Files", "All Files (*.*)"};
            filterExtensions = new String [] {"*.gif;*.png;*.jpg;*.jpeg", "*.*"};
            filterPath = "C:\\Users\\\" + System.getProperty(\"user.name\") + \"\\Pictures";
        }

        tDialog.setFilterNames (filterNames);
        tDialog.setFilterExtensions (filterExtensions);
        tDialog.setFilterPath (filterPath);
        tDialog.setFileName(srcImageServer.srcFilePath);
        String src = tDialog.open();

        display.close();
        return src;
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

    }



/*    private void buildFileButtonPanel(JPanel panel) {

        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel managerPanelLabel = new JLabel("Menedżer obrazów");
        managerPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        panel.add(managerPanelLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        panel.add(jButtonOpenFile, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        panel.add(jButtonOpenGrayFile, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        panel.add(jButtonSaveFile, c);

    }*/

    private void buildFileButtonPanel(JPanel panel) {

        panel.setLayout(new GridLayout(4,1));

        JLabel managerPanelLabel = new JLabel("Menedżer obrazów");
        managerPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(managerPanelLabel);
        panel.add(jButtonOpenFile);
        panel.add(jButtonOpenGrayFile);
        panel.add(jButtonSaveFile);

    }


    private void buildWindow(JTree managerTree) {

        window = new JFrame("Jmagination - Biurko");


        window.setPreferredSize(windowDimension);
        window.setMinimumSize(windowDimension);
        window.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));

        // content panels init
        managerPanel = new JPanel(new BorderLayout());
        imagePanel = new JPanel(new BorderLayout());
        histogramPanel = new JPanel(new BorderLayout());
        operationsPanel = new JPanel(new BorderLayout());


        managerPanel.setMinimumSize(managerPanelDimension);
        imagePanel.setMinimumSize(imagePanelDimension);
        operationsPanel.setMinimumSize(operationsPanelDimension);
        histogramPanel.setMinimumSize(histogramPanelDimension);

        // content direct holders
        managerPanelNorth = new JPanel();
        managerPanelCentral = new JScrollPane();
        managerPanelSouth = new JPanel(new GridBagLayout());
        managerPanelSouthCont = new ImagePanel2();
        managerPanelSouthAbout = new AboutPanel();
        imagePanelNorth = new JPanel();
        imagePanelCentral = new JScrollPane(imagePanelCont);
        imagePanelSouth = new JPanel();
//        histogramPanelNorth = new JPanel();
        histogramPanelCentral = new JTabbedPane();


        Dimension previewImage = new Dimension(GUI_PREVIEW_IMAGE_WIDTH,GUI_PREVIEW_IMAGE_HEIGHT);
        managerPanelSouthCont.setMinimumSize(previewImage);
        managerPanelSouthCont.setPreferredSize(previewImage);
        managerPanelSouthAbout.setMinimumSize(previewImage);
        managerPanelSouthAbout.setPreferredSize(previewImage);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        managerPanelSouth.add(managerPanelSouthCont, c);
//        managerPanelSouth.add(managerPanelSouthAbout, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 1;
        managerPanelSouth.add(jButtonAbout, c);



        histogramPanelCentral.addTab("Histogram", new JScrollPane(histogramPanelCont));
        histogramPanelCentral.addTab("Linia profilu RGB", new JScrollPane(lineProfileRBGPanelCont));
        histogramPanelCentral.addTab("Linia profilu HSV", new JScrollPane(lineProfileHSVPanelCont));
        histogramPanelSouth = new JPanel();
        operationsPanelNorth = new JPanel();
        operationsPanelCentralPane = new PresenterTabOperations(Operations.registerOperations(), this);
        operationsPanelCentral = new JScrollPane(operationsPanelCentralPane);
        operationsPanelSouth = new JPanel();

        managerPanelNorth.setMinimumSize(managerPanelNorthDimension);
        managerPanelCentral.setMinimumSize(managerPanelCentralDimension);
        managerPanelSouth.setMinimumSize(managerPanelSouthDimension);
        managerPanelSouth.setPreferredSize(managerPanelSouthDimension);
        imagePanelNorth.setMinimumSize(imagePanelNorthDimension);
        imagePanelCentral.setMinimumSize(imagePanelCentralDimension);
        imagePanelSouth.setMinimumSize(imagePanelSouthDimension);
//        histogramPanelNorth.setMinimumSize(histogramPanelNorthDimension);
        histogramPanelCentral.setMinimumSize(histogramPanelCentralDimension);
        histogramPanelSouth.setMinimumSize(histogramPanelSouthDimension);
        operationsPanelNorth.setMinimumSize(operationsPanelNorthDimension);
        operationsPanelCentral.setMinimumSize(operationsPanelCentralDimension);
        operationsPanelSouth.setMinimumSize(operationsPanelSouthDimension);


        managerPanel.add(managerPanelNorth, BorderLayout.NORTH);
        managerPanel.add(managerPanelCentral, BorderLayout.CENTER);
        managerPanel.add(managerPanelSouth, BorderLayout.SOUTH);

        imagePanel.add(imagePanelNorth, BorderLayout.NORTH);
        imagePanel.add(imagePanelCentral, BorderLayout.CENTER);
        imagePanel.add(imagePanelSouth, BorderLayout.SOUTH);

//        histogramPanel.add(histogramPanelNorth, BorderLayout.NORTH);
        histogramPanel.add(histogramPanelCentral, BorderLayout.CENTER);
        histogramPanel.add(histogramPanelSouth, BorderLayout.SOUTH);

        operationsPanel.add(operationsPanelNorth, BorderLayout.NORTH);
        operationsPanel.add(operationsPanelCentral, BorderLayout.CENTER);
        operationsPanel.add(operationsPanelSouth, BorderLayout.SOUTH);

        // content panel labels


        JLabel imagePanelLabel = new JLabel("Obraz");
        imagePanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanelNorth.add(imagePanelLabel);

/*        JLabel histogramPanelLabel = new JLabel("Histogram");
        histogramPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        histogramPanelNorth.add(histogramPanelLabel);*/

        JLabel operationsPanelLabel = new JLabel("Operacje");
        operationsPanelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        operationsPanelNorth.add(operationsPanelLabel);

        // static content
        buildFileButtonPanel(managerPanelNorth);

        managerPanelCentral.setViewportView(managerTree);

        level1Left = new JPanel(new BorderLayout());
//        level1Left.setPreferredSize(jmagination.ConstantsInitializers.GUI_DIMENSION_level1Left);
        level1Left.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level1Left);
        level1Left.add(managerPanel);

        level1Right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, operationsPanel, histogramPanel);
//        level1Right.setPreferredSize(jmagination.ConstantsInitializers.GUI_DIMENSION_level1Right);
        level1Right.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level1Right);
        level1Right.setOneTouchExpandable(true);

        level0Left = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, level1Left, level1Right);
//        level0Left.setPreferredSize(jmagination.ConstantsInitializers.GUI_DIMENSION_level0Left);
        level0Left.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level0Left);
        level0Left.setOneTouchExpandable(true);

        level0Right = new JPanel(new BorderLayout());
//        level0Right.setPreferredSize(jmagination.ConstantsInitializers.GUI_DIMENSION_level0Right);
        level0Right.setMinimumSize(ConstantsInitializers.GUI_DIMENSION_level0Right);

        level1Right.setResizeWeight(0.5);
        level1Right.setDividerSize(ConstantsInitializers.GUI_DIMENSION_splitPaneDividerSize);
        level1Right.setDividerLocation(0.2d);

        level0Left.setResizeWeight(0.5);
        level0Left.setDividerSize(ConstantsInitializers.GUI_DIMENSION_splitPaneDividerSize);

        level0Right.add(imagePanel);

        level0SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, level0Left, level0Right);
        level0SplitPane.setPreferredSize(ConstantsInitializers.GUI_DIMENSION_level0SplitPane);
//        level0SplitPane.setMinimumSize(jmagination.ConstantsInitializers.GUI_DIMENSION_level0SplitPane);
        level0SplitPane.setOneTouchExpandable(true);

        level0SplitPane.setResizeWeight(0.25);
        level0SplitPane.setDividerSize(ConstantsInitializers.GUI_DIMENSION_splitPaneDividerSize);



        window.add(level0SplitPane);

        window.pack();
        Insets windowInsets = window.getInsets();
        int windowWidth = (int) ConstantsInitializers.GUI_DIMENSION_level0SplitPane.getWidth() + windowInsets.left + windowInsets.right;
        int windowHeight = (int) ConstantsInitializers.GUI_DIMENSION_level0SplitPane.getHeight() + windowInsets.top + windowInsets.bottom;
        //window.setPreferredSize(new Dimension(windowWidth, windowHeight));
        window.pack();
        window.setVisible(true);

        histogramPanelCentral.addChangeListener(lineProfile);
    }

    public static SimpleHSVBufferedImage crateAltImage(Dimension dimension, String message, Color color) {
        SimpleHSVBufferedImage img = new SimpleHSVBufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = img.getGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, dimension.width, dimension.height);
        graphics.setColor(Color.BLACK);
        graphics.drawString(message, 0, graphics.getFont().getSize());
        graphics.dispose();
        return img;
    }

    @Override
    public void runOperation(Operation operation) {
        if (imagePanelCont.getImage() == null) {
            return;
        }

        SimpleHSVBufferedImage newBufferedImage;

        newBufferedImage = operation.RunOperationFunction(originalBufferedImage, srcImageServer.getHistogram());

        imagePanelCont.setImage(newBufferedImage);

        Histogram histogram = new Histogram(newBufferedImage);
        histogramPanelCont.setImage(histogram.createImg("INTERLACED", ConstantsInitializers.GUI_DIMENSION_histogramPanelCentral));
        lineProfile.update();

        operationsPanelCentralPane.updateControls(true);
    }

    @Override
    public void discardOperation(Operation operation) {
        if(imagePanelCont.getImage() != originalBufferedImage) {
            if(srcImageServer != null) {
                setImageServer(srcImageServer);
            }
        }
        operation.jButtonApply.setEnabled(true);
        operationsPanelCentralPane.updateControls(false);
    }

    @Override
    public void saveOperationsOutput(Operation operation) {
        ImageServer newImageServer = srcImageServer.createChildImageServer((SimpleHSVBufferedImage) imagePanelCont.getImage());
        setImageServer(newImageServer);
        operation.jButtonApply.setEnabled(true);
        operationsPanelCentralPane.updateControls(false);
    }

    @Override
    public ImageServer[] supplyAvailableImages() {
        ArrayList<ImageServer> imageServersList = new ArrayList<>();
        for(DefaultMutableTreeNode n: imageManager.getNodesArray()) {
            imageServersList.add((ImageServer)n.getUserObject());
        }
        return imageServersList.toArray(new ImageServer[0]);
    }

    @Override
    public JFrame getMainWindow() {
        return window;
    }

}
