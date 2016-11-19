import com.sun.deploy.panel.JavaPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Created by darek on 27.10.16.
 */
public class GUIStyler {

    public static class JButtonS extends JButton {

        public JButtonS() {
            super();
            doStyling();
        }

        public JButtonS(String s) {
            super(s);
            doStyling();
        }

        private void doStyling() {
            setPreferredSize(ConstantsInitializers.GUI_BUTTON_SIZE_LONG);
            setMinimumSize(ConstantsInitializers.GUI_BUTTON_SIZE_LONG);
            setMaximumSize(ConstantsInitializers.GUI_BUTTON_SIZE_LONG);
        }

    }

    public static class ParamsGrid extends GridBagConstraints {

        public ParamsGrid() {
            super();
            do_styling();
        }

        public ParamsGrid(int gridx, int gridy) {
            super();
            do_styling();

            this.gridx = gridx;
            this.gridy = gridy;
        }

        public ParamsGrid(int gridx, int gridy, int gridwidth, int gridheight) {
            super();
            do_styling();

            this.gridx = gridx;
            this.gridy = gridy;

            this.gridwidth = gridwidth;
            this.gridheight = gridheight;
        }

        public ParamsGrid(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets, int ipadx, int ipady) {
            super(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets, ipadx, ipady);
        }

        private void do_styling() {

        }
    }

    public static class Presenter extends JPanel {

        private JTabbedPane jTabbedPane;

        public Presenter() {
            super(new GridLayout(1,1));

            jTabbedPane = new JTabbedPane();
            add(jTabbedPane);

        }

        public void addTab(String s, JComponent jComponent) {
            jTabbedPane.addTab(s, jComponent);
        }

        public void remTab(JComponent jComponent) {

        }

    }

    public static class PresenterTab extends JPanel {

        public PresenterTab() {
            super();
            setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
        }
    }

    public static class PresenterTabProperties extends PresenterTab {

        BufferedImage img;

        JPanel controlsPanel;
        JPanel propertiesPanel;

        public PresenterTabProperties(BufferedImage img) {
            super();
            this.img = img;
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

            controlsPanel = new JPanel();
            controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
            controlsPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);

            propertiesPanel = new JPanel();
            //parametersPanel.setLayout(new BoxLayout(parametersPanel,BoxLayout.LINE_AXIS));
            propertiesPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);

            drawProperties();

            add(controlsPanel);
            add(propertiesPanel);
        }

        private void drawProperties() {

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);



            int panelX = 0;
            int panelY = 0;

            {
                JLabel label = new JLabel("Height");
                panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

                JTextArea value = new JTextArea(String.valueOf(img.getHeight()));
                value.setEditable(false);
                panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
            }

            {
                panelX=0;
                JLabel label = new JLabel("Width");
                panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

                String s = String.valueOf(img.getWidth());
                JTextArea value = new JTextArea(s);
                value.setEditable(false);
                panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
            }

            {
                panelX=0;
                JLabel label = new JLabel("Type");
                panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

                int input = img.getColorModel().getColorSpace().getType();
                String s = "Color Space Type";

                switch(input) {
                    case java.awt.color.ColorSpace.CS_CIEXYZ:
                        s = "CS_CIEXYZ";
                        break;
                    case java.awt.color.ColorSpace.CS_GRAY:
                        s = "CS_GRAY";
                        break;
                    case java.awt.color.ColorSpace.CS_LINEAR_RGB:
                        s = "CS_LINEAR_RGB";
                        break;
                    case java.awt.color.ColorSpace.CS_PYCC:
                        s = "CS_PYCC";
                        break;
                    case java.awt.color.ColorSpace.CS_sRGB:
                        s = "CS_sRGB";
                        break;
                    case java.awt.color.ColorSpace.TYPE_2CLR:
                        s = "2CLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_3CLR:
                        s = "3CLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_4CLR:
                        s = "4CLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_5CLR:
                        s = "5CLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_6CLR:
                        s = "6CLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_7CLR:
                        s = "7CLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_8CLR:
                        s = "8CLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_9CLR:
                        s = "9CLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_ACLR:
                        s = "ACLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_BCLR:
                        s = "BCLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_CCLR:
                        s = "CCLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_CMY:
                        s = "CMY";
                        break;
                    case java.awt.color.ColorSpace.TYPE_CMYK:
                        s = "CMYK";
                        break;
                    case java.awt.color.ColorSpace.TYPE_DCLR:
                        s = "DCLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_ECLR:
                        s = "ECLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_FCLR:
                        s = "FCLR";
                        break;
                    case java.awt.color.ColorSpace.TYPE_GRAY:
                        s = "GRAY";
                        break;
                    case java.awt.color.ColorSpace.TYPE_HLS:
                        s = "HLS";
                        break;
                    case java.awt.color.ColorSpace.TYPE_HSV:
                        s = "HSV";
                        break;
                    case java.awt.color.ColorSpace.TYPE_Lab:
                        s = "Lab";
                        break;
                    case java.awt.color.ColorSpace.TYPE_Luv:
                        s = "Luv";
                        break;
                    case java.awt.color.ColorSpace.TYPE_RGB:
                        s = "RGB";
                        break;
                    case java.awt.color.ColorSpace.TYPE_XYZ:
                        s = "XYZ";
                        break;
                    case java.awt.color.ColorSpace.TYPE_YCbCr:
                        s = "YCbCr";
                        break;
                    case java.awt.color.ColorSpace.TYPE_Yxy:
                        s = "Yxy";
                        break;
                }

                JTextArea value = new JTextArea(s);
                value.setEditable(false);
                panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
            }

            {
                panelX=0;
                JLabel label = new JLabel("Components");
                panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

                int input = img.getColorModel().getColorSpace().getNumComponents();
                String s = String.valueOf(input);


                JTextArea value = new JTextArea(s);
                value.setEditable(false);
                panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
            }

            {
                panelX=0;
                JLabel label = new JLabel("Colors");
                panel.add(label, new GUIStyler.ParamsGrid(panelX++, panelY));

                int input = img.getColorModel().getColorSpace().getNumComponents();
                String s = String.valueOf(input);

                JTextArea value = new JTextArea(s);
                value.setEditable(false);
                panel.add(value, new GUIStyler.ParamsGrid(panelX, panelY++));
            }


            propertiesPanel.add(panel);

        }
    }

    public static class PresenterTabOperations extends PresenterTab {

        JPanel controlsPanel;
        JPanel parametersPanel;

        HashMap<JButton, Operations.Operation> activatorPanelMap;
        JButton lastKey = null;

        Operations.OperationManager operationManager;

        public PresenterTabOperations(Operations.OperationManager operationManager) {
            super();
            this.operationManager = operationManager;
            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
            activatorPanelMap = new HashMap<>();

            controlsPanel = new JPanel();
            controlsPanel.setLayout(new BoxLayout(controlsPanel,BoxLayout.Y_AXIS));
            controlsPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);

            parametersPanel = new JPanel();
            //parametersPanel.setLayout(new BoxLayout(parametersPanel,BoxLayout.LINE_AXIS));
            parametersPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_ALT_COLOR);

            drawControls();

            add(controlsPanel);
            add(parametersPanel);
        }

        private void drawControls() {

            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton jb = (JButton)e.getSource();
                    if(jb == lastKey) {
                        return;
                    }

                    Operations.Operation op_old = activatorPanelMap.get(lastKey);
                    lastKey = jb;
                    if(op_old != null) {
                        parametersPanel.remove(op_old.getConfiguratorPanel());
                    }

                    Operations.Operation op = activatorPanelMap.get(jb);
                    parametersPanel.add(op.getConfiguratorPanel());

                    getParent().repaint();
                }
            };


            boolean init = true;
            for (Operations.Operation op: operationManager.getNewOperations()) {
                JButton jbCmd = new JButton(op.getLabel());
                jbCmd.setMinimumSize(ConstantsInitializers.GUI_BUTTON_SIZE_SHORT);
                jbCmd.setMaximumSize(ConstantsInitializers.GUI_BUTTON_SIZE_SHORT);
                jbCmd.setPreferredSize(ConstantsInitializers.GUI_BUTTON_SIZE_SHORT);
                jbCmd.setOpaque(true);
                controlsPanel.add(jbCmd);

                activatorPanelMap.put(jbCmd, op);

                jbCmd.addActionListener(al);
                if(init == true) {
                    init = false;


                    parametersPanel.add(op.getConfiguratorPanel());
                    lastKey=jbCmd;
                }
            }
        }

    }


    public static class PresenterTabImage extends PresenterTab {

        BufferedImage img;
        ImagePanel imagePanel;
        JPanel controlsPanel;

        static final String CMD_CE = "Center";
        static final String CMD_TL = "Top-Left";
        static final String CMD_TR = "Top-Right";
        static final String CMD_BL = "Bottom-Left";
        static final String CMD_BR = "Bottom-Right";
        static final String CMD_AT = "Align-Top";
        static final String CMD_AB = "Align-Bottom";
        static final String CMD_AL = "Align-Left";
        static final String CMD_AR = "Align-Right";

        static final String[] commands = { CMD_TL, CMD_TR, CMD_CE, CMD_BL, CMD_BR, CMD_AT, CMD_AB, CMD_AL, CMD_AR };


        public PresenterTabImage(BufferedImage img) {
            super();
            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
            this.img = img;

            imagePanel = new ImagePanel(img);
            imagePanel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

            controlsPanel = new JPanel();
            controlsPanel.setLayout(new BoxLayout(controlsPanel,BoxLayout.Y_AXIS));
            controlsPanel.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
            drawControls();

            add(controlsPanel);
            add(imagePanel);
        }

        private void drawControls() {

            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton jb = (JButton)e.getSource();
                    imagePanel.setOriginCmd(jb.getText());
                }
            };


            for (String s: commands) {
                JButton jbCmd = new JButton(s);
                jbCmd.setMinimumSize(ConstantsInitializers.GUI_BUTTON_SIZE_SHORT);
                jbCmd.setMaximumSize(ConstantsInitializers.GUI_BUTTON_SIZE_SHORT);
                jbCmd.setPreferredSize(ConstantsInitializers.GUI_BUTTON_SIZE_SHORT);
                jbCmd.setOpaque(true);
                controlsPanel.add(jbCmd);

                jbCmd.addActionListener(al);
            }
        }

    }

    public static class ImagePanel extends JPanel implements MouseListener, MouseMotionListener{

        BufferedImage img;
        Point origin = new Point(0,0);

        ScrollView scrollView;

        public ImagePanel(BufferedImage img) {
            super();
            this.img = img;

            Dimension dim = new Dimension(img.getWidth(), img.getHeight());
            setMinimumSize(dim);
            setPreferredSize(dim);
            addMouseMotionListener(this);
            addMouseListener(this);
        }

        public void setOriginCmd(String command) {

            double paneWidth = getSize().getWidth();
            double paneHeight = getSize().getHeight();
            double imgWidth = img.getWidth();
            double imgHeight = img.getHeight();

            double oriX = origin.getX();
            double oriY = origin.getY();

            switch(command) {
                case PresenterTabImage.CMD_TL:
                    origin.setLocation(0,0);
                    break;
                case PresenterTabImage.CMD_TR:
                    origin.setLocation( paneWidth - imgWidth, 0 );
                    break;
                case PresenterTabImage.CMD_CE:
                    origin.setLocation( paneWidth / 2 - imgWidth / 2, paneHeight / 2 - imgHeight / 2 );
                    break;
                case PresenterTabImage.CMD_BL:
                    origin.setLocation( 0 , paneHeight - imgHeight );
                    break;
                case PresenterTabImage.CMD_BR:
                    origin.setLocation( paneWidth - imgWidth , paneHeight - imgHeight );
                    break;
                case PresenterTabImage.CMD_AT:
                    origin.setLocation( oriX , 0 );
                    break;
                case PresenterTabImage.CMD_AB:
                    origin.setLocation( oriX , paneHeight - imgHeight );
                    break;
                case PresenterTabImage.CMD_AL:
                    origin.setLocation( 0 , oriY );
                    break;
                case PresenterTabImage.CMD_AR:
                    origin.setLocation( paneWidth - imgWidth , oriY );
                    break;
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, (int)origin.getX(), (int)origin.getY(), null);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            origin.setLocation( origin.getX() + e.getPoint().getX() - scrollView.getMouseInitialPosition().getX(), origin.getY() + e.getPoint().getY() - scrollView.getMouseInitialPosition().getY());
//            System.out.printf("Drag: %s\n",origin.toString());
            repaint();
            scrollView.setMouseInitialPosition(e.getPoint());
            scrollView.updateSymbols(origin);
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            scrollView = new ScrollView(this.getSize(),new Dimension(img.getWidth(), img.getHeight()), e.getPoint(), new Point( (int) (e.getX() - origin.getX()) , (int) (e.getY() - origin.getY()) ));
            scrollView.updateSymbols(origin);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            scrollView.closeWindow();
            scrollView = null;

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public static class ScrollView extends JComponent {

        double scaler = 1.0;

        Rectangle graphicsRectangle;
        Point graphicsRectangleHandle;

        JFrame window = new JFrame();

        Point mouseInitialPosition;

        public ScrollView(Dimension viewDimension, Dimension imageDimension, Point mouseInitialPosition, Point mousePictureOriginOffset) {

            this.mouseInitialPosition = new Point(mouseInitialPosition);

            graphicsRectangle = new Rectangle();

            window.setUndecorated(true);
            window.setOpacity(ConstantsInitializers.GUI_SCROLLWINDOW_OPACITY);
            window.setLocation(MouseInfo.getPointerInfo().getLocation());
            window.getContentPane().add(this);
            window.getContentPane().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

            graphicsRectangleHandle = new Point();

            applyGeometricFeatures(viewDimension, imageDimension, mousePictureOriginOffset);

            window.setVisible(true);
        }

        private void applyGeometricFeatures(Dimension viewDimension, Dimension imageDimension, Point mousePictureOriginOffset) {
            GraphicsConfiguration gc = window.getGraphicsConfiguration();
            GraphicsDevice gd = gc.getDevice();
            GraphicsConfiguration gdc = gd.getDefaultConfiguration();
            Rectangle  gcBounds = gdc.getBounds();
            Dimension gcDimension = new Dimension(gcBounds.getSize());

            int windowWidth;
            int windowHeight;

            double imageWidth = imageDimension.getWidth();
            double imageHeight = imageDimension.getHeight();

            if(imageWidth/gcDimension.getWidth() > imageHeight/gcDimension.getHeight()) {
                scaler = gcDimension.getHeight() / ( imageHeight * ConstantsInitializers.GUI_DISPLAY_TO_SCROLLWINDOW_SIZE_RATIO);
            } else {
                scaler = gcDimension.getWidth() / ( imageWidth * ConstantsInitializers.GUI_DISPLAY_TO_SCROLLWINDOW_SIZE_RATIO);
            }

            windowWidth = (int) (imageWidth * scaler);
            windowHeight = (int)(imageHeight * scaler);

            window.setSize(windowWidth, windowHeight);
//            System.out.printf("Scroll window size: %s\n",window.getSize().toString());



            graphicsRectangle.setSize((int)(viewDimension.getWidth() * scaler) - 1, (int)(viewDimension.getHeight() * scaler) - 1);

            graphicsRectangleHandle.setLocation(mousePictureOriginOffset.getX() * scaler, mousePictureOriginOffset.getY() * scaler);

        }

        public void closeWindow() {
            window.setVisible(false);
            window.dispose();
        }

        public Point getMouseInitialPosition() {
            return mouseInitialPosition;
        }

        public void setMouseInitialPosition(Point p) {
            mouseInitialPosition = new Point(p);
        }

        public void calculateDrawingAnchor(Point p) {
            graphicsRectangle.setLocation( (int) (p.getX() * scaler), (int) (p.getY() * scaler));
        }

        public void updateSymbols(Point p) {
            calculateDrawingAnchor(p);
            Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
            window.setLocation((int) ( mouseLoc.getX() - graphicsRectangle.getX() - graphicsRectangleHandle.getX()), (int) ( mouseLoc.getY() - graphicsRectangle.getY() - graphicsRectangleHandle.getY()));

//            System.out.printf("Scroll window position: %s\n",window.getLocation().toString());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(ConstantsInitializers.GUI_CHARTS_BG_COLOR);
            g.fillRect( 0, 0, getWidth() - 1, getHeight() -1 );
            g.setColor(Color.RED);
            g.drawRect( 0, 0, getWidth() - 1, getHeight() -1 );
            g.setColor(Color.BLUE);
/*            g.drawRect( (int) ( graphicsRectangle.getLocation().getX() ),
                        (int) ( graphicsRectangle.getLocation().getY() ),
                        (int) ( graphicsRectangle.getLocation().getX() > 0 ? graphicsRectangle.getWidth() - graphicsRectangle.getLocation().getX() : graphicsRectangle.getWidth() + graphicsRectangle.getLocation().getX() ),
                        (int) ( graphicsRectangle.getLocation().getY() > graphicsRectangle.getHeight() - 1 ? graphicsRectangle.getHeight() - graphicsRectangle.getLocation().getY() : graphicsRectangle.getHeight() + graphicsRectangle.getLocation().getY() )
            );*/

            g.drawRect( (int) ( graphicsRectangle.getLocation().getX() ),
                        (int) ( graphicsRectangle.getLocation().getY() ),
                        (int) ( graphicsRectangle.getWidth() ),
                        (int) ( graphicsRectangle.getHeight() )
            );

//            System.out.printf("Rectangle location %s\n", graphicsRectangle.getLocation().toString());
        }

    }

    private GUIStyler() {
        throw new AssertionError();
    }

}
