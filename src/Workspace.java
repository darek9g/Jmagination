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


        JTree imageManagerTree = imageManager.getTree();


        Workspace workspace = this;
        imageManagerTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {


                JTree tree = (JTree)e.getSource();

                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1 && e.getClickCount() == 1 && selPath != null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

                    System.out.println("Selected node" + selectedNode.toString());
                    System.out.println("UserObject: " + selectedNode.getUserObject().toString());
                    ImageServer imageServer = (ImageServer) selectedNode.getUserObject();

                    if(e.getButton() == MouseEvent.BUTTON1) {
                        if(imageServer.windowIsVisible() == true) {
                            imageServer.windowToFront();
                        }
                    }

                    if(e.getButton() == MouseEvent.BUTTON3) {
                        JPopupMenu jPopupMenu = new JPopupMenu("Popup menu");

                        JMenuItem jMenuItemShowImage = new JMenuItem("Show/Hide Image");
                        jMenuItemShowImage.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                imageServer.toogleWindow();
                            }
                        });


                        jPopupMenu.add(jMenuItemShowImage);

                        JMenuItem jMenuItemPlaceInWorkspace = new JMenuItem("Place in Workspace");
                        jMenuItemPlaceInWorkspace.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                workspace.setImageServer(imageServer);
                            }
                        });

                        jPopupMenu.add(new JMenuItem("Place in workspace"));
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
                        ImageServer imageServer;
                        imageServer = ImageServer.createLoadedImageServer(loaded, chooser.getSelectedFile().getAbsolutePath(), imageManager);
                        setImageServer(imageServer);
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
