import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import static java.awt.color.ColorSpace.CS_GRAY;

/**
 * Created by darek on 26.10.16.
 */
public class ImageServer extends JComponent{

    JFrame window;
    String id;
    ImageServer parent;
    GUIStyler.JButtonS callUpButton;
    GUIStyler.Presenter tpanel = new GUIStyler.Presenter();

    BufferedImage img;

    BufferedImage hist;
    Histogram histogram;

    Jmagination master;

    Operations.OperationManager operationManager;


    ImageServer(Jmagination master) {
        window = new JFrame("Empty buffer");
        window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        window.setContentPane(tpanel);

        this.parent = null;

        this.operationManager = new Operations.OperationManager(this, master);

        callUpButton = new GUIStyler.JButtonS(id);

        this.master = master;

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
        window.setVisible(true);

    }

    ImageServer(ImageServer parent, Jmagination master) {
        this(master);
        this.parent = parent;
    }

    ImageServer(BufferedImage img, String filePath, Jmagination master) {
        this(master);
        this.img = img;
//        System.out.println("Color colors: " + img.getColorModel().getColorSpace().getNumComponents() );

        //BufferedImage imgG = Operations.convertToGrayFunction(img);

        //img = imgG;

//        System.out.println("Gray colors: " + img.getColorModel().getColorSpace().getNumComponents() );

        GUIStyler.PresenterTabImage imageTab = new GUIStyler.PresenterTabImage(img);
        tpanel.addTab("Image", imageTab);

        GUIStyler.PresenterTabProperties propertiesTab = new GUIStyler.PresenterTabProperties(img);
        tpanel.addTab("Properties", propertiesTab);



        histogram = new Histogram(img);
        GUIStyler.PresenterTabImage historgamTab = new GUIStyler.PresenterTabImage(histogram.createImg());
        tpanel.addTab("Histogram", historgamTab);

        GUIStyler.PresenterTabOperations operationsTab = new GUIStyler.PresenterTabOperations(operationManager);
        tpanel.addTab("Operations", operationsTab);

        window.setTitle(filePath);
        callUpButton.setText(filePath);

        window.pack();

        repaint();
    }

    public void setDrawingCapabilities() {
        GraphicsConfiguration gc = window.getGraphicsConfiguration();
        GraphicsDevice gd = gc.getDevice();
        GraphicsConfiguration gdc = gd.getDefaultConfiguration();
        Rectangle  gcBounds = gdc.getBounds();
        Dimension gcDimension = new Dimension(gcBounds.getSize());


//        System.out.println("Window width " + gcDimension.getWidth() + ", Window height " + gcDimension.getHeight());
        window.setMaximumSize(gcDimension);

        window.setResizable(false);

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
