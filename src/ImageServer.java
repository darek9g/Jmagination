import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by darek on 26.10.16.
 */
public class ImageServer {

    ImageManager imageManager;
    int id;
    String description;

    JFrame window;

    GUIStyler.JButtonS callUpButton;
    GUIStyler.Presenter tpanel = new GUIStyler.Presenter();

    BufferedImage img;
    Histogram histogram;


    ImageServer(ImageManager imageManager) {
        this.imageManager = imageManager;
        this.id = imageManager.nextImageId();
        this.description = "Empty";


        window = new JFrame("Empty buffer");
        window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        window.setContentPane(tpanel);

        callUpButton = new GUIStyler.JButtonS(String.valueOf(this.id));

        callUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(window.isVisible()==false) window.setVisible(true);
                if(window.getState()==JFrame.ICONIFIED) window.setState(JFrame.NORMAL);
                window.toFront();
            }
        });

        img = null;

        setDrawingCapabilities();

        window.setLocation(MouseInfo.getPointerInfo().getLocation());
        window.setVisible(false);

    }

    ImageServer(BufferedImage img, String filePath, ImageManager imageManager) {
        this(imageManager);
        this.img = img;

        configure("Id: " + this.id + " from file: " + filePath);
    }

    public static ImageServer createLoadedImageServer(BufferedImage img, String filePath, ImageManager imageManager) {
        ImageServer child = new ImageServer(img,  filePath, imageManager);
        imageManager.addLoadedImage(child);
        return child;
    }

    ImageServer(BufferedImage img, ImageManager imageManager) {
        this(imageManager);
        this.img = img;

        configure("Id: " + this.id + " from image " + this.getId());
    }

    public ImageServer createChildImageServer(BufferedImage img) {
        ImageServer child = new ImageServer(img, imageManager);
        imageManager.addCreatedImage(child, this);
        return child;
    }

    private void configure(String description) {
        this.description = description;
        GUIStyler.PresenterTabImage imageTab = new GUIStyler.PresenterTabImage(img);
        tpanel.addTab("Image", imageTab);

        GUIStyler.PresenterTabProperties propertiesTab = new GUIStyler.PresenterTabProperties(img);
        tpanel.addTab("Properties", propertiesTab);

        histogram = new Histogram(img);
        GUIStyler.PresenterTabImage historgamTab = new GUIStyler.PresenterTabImage(histogram.createImg2());
        tpanel.addTab("Histogram", historgamTab);

        GUIStyler.PresenterTabOperations operationsTab = new GUIStyler.PresenterTabOperations(Operations.registerOperationsForImageServer(this));
        tpanel.addTab("Operations", operationsTab);

        window.setTitle("Id: " + id);
        callUpButton.setText(description);

        window.pack();

        window.repaint();

    }

    public void setDrawingCapabilities() {
        GraphicsConfiguration gc = window.getGraphicsConfiguration();
        GraphicsDevice gd = gc.getDevice();
        GraphicsConfiguration gdc = gd.getDefaultConfiguration();
        Rectangle  gcBounds = gdc.getBounds();
        Dimension gcDimension = new Dimension(gcBounds.getSize());

        window.setMaximumSize(gcDimension);

        window.setResizable(false);

    }

    public int getId() {
        return id;
    }

    public String toString() {
        return this.description;
    }

    public void toogleWindow() {
        if(window.isVisible()==true) {
            window.setVisible(false);
        } else {
            window.setVisible(true);
        }
    }

    public boolean windowIsVisible() {
        return window.isVisible();
    }

    public  void windowToFront() {
        window.toFront();
    }

    public GUIStyler.JButtonS getCallUpButton() {
        return callUpButton;
    }

    public BufferedImage getImg() {
        return img;
    };

    public Histogram getHistogram() {
        return histogram;
    }

    public static BufferedImage LoadImageFromFile(String filePath) {
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.out.println("Error loading image");
        }

        return bufferedImage;
    }

}
