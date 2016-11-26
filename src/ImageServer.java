import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by darek on 26.10.16.
 */
public class ImageServer extends JComponent{

    JFrame window;
    int id;
    ImageServer parent;
    GUIStyler.JButtonS callUpButton;
    GUIStyler.Presenter tpanel = new GUIStyler.Presenter();

    BufferedImage img;

    Histogram histogram;

    Jmagination master;

    Operations.OperationManager operationManager;


    ImageServer(Jmagination master) {
        this.id = master.nextId();


        window = new JFrame("Empty buffer");
        window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        window.setContentPane(tpanel);

        this.parent = null;



        callUpButton = new GUIStyler.JButtonS(String.valueOf(this.id));

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

    ImageServer(BufferedImage img, String filePath, Jmagination master) {
        this(master);
        this.img = img;

        configure("Id: " + this.id + " from file: " + filePath);
    }

    ImageServer(BufferedImage img, ImageServer parent, Jmagination master) {
        this(master);
        this.img = img;

        this.parent = parent;

        configure("Id: " + this.id + " from image " + parent.getId());
    }

    private void configure(String description) {
        GUIStyler.PresenterTabImage imageTab = new GUIStyler.PresenterTabImage(img);
        tpanel.addTab("Image", imageTab);

        GUIStyler.PresenterTabProperties propertiesTab = new GUIStyler.PresenterTabProperties(img);
        tpanel.addTab("Properties", propertiesTab);

        histogram = new Histogram(img);
        GUIStyler.PresenterTabImage historgamTab = new GUIStyler.PresenterTabImage(histogram.createImg2());
        tpanel.addTab("Histogram", historgamTab);

        GUIStyler.PresenterTabOperations2 operationsTab = new GUIStyler.PresenterTabOperations2(Operations.registerOperationsForImageServer(this, master));
        tpanel.addTab("Operations", operationsTab);

        window.setTitle("Id: " + id);
        callUpButton.setText(description);

        window.pack();

        repaint();

        master.loadImageToWorkspace(this);
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
