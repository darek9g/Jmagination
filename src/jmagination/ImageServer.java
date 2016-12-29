package jmagination;

import jmagination.gui.*;
import jmagination.histogram.Histogram;
import jmagination.operations.Operation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Created by darek on 26.10.16.
 */
public class ImageServer implements RunOperation {

    ImageManager imageManager;
    int id;
    boolean fromFile;
    String description;
    String srcFilePath;

    JFrame window;

    Presenter tpanel = new Presenter();
    PresenterTabImage imageTab;
    PresenterTabProperties propertiesTab;
    PresenterTabImage historgamTab;
    JScrollPane operationsTab;
    PresenterTabOperations operationsTabPane;


    BufferedImage img;
    Histogram histogram;


    ImageServer(ImageManager imageManager) {
        this.imageManager = imageManager;
        this.id = imageManager.nextImageId();
        this.description = "Pusty";


        window = new JFrame("Pusty bufor");
        window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        window.setContentPane(tpanel);

        img = null;

        setDrawingCapabilities();

        window.setLocation(MouseInfo.getPointerInfo().getLocation());
        window.setVisible(false);

    }

    ImageServer(BufferedImage img, ImageManager imageManager, String filePath, boolean fromFile) {
        this(imageManager);
        this.img = img;
        this.fromFile = fromFile;
        this.srcFilePath = filePath;

        if(this.fromFile == true) {
            configure("Id: " + this.id + " z pliku: " + filePath);
        } else {
            configure("Id: " + this.id + " - nowy obraz ");
        }
    }

    public static ImageServer createLoadedImageServer(BufferedImage img, String filePath, ImageManager imageManager) {
        ImageServer child = new ImageServer(img, imageManager, filePath, true);
        imageManager.addLoadedImage(child);
        return child;
    }

    public ImageServer createChildImageServer(BufferedImage img) {
        ImageServer child = new ImageServer(img, imageManager, this.srcFilePath, false);
        imageManager.addCreatedImage(child, this);
        return child;
    }

    private void configure(String description) {
        this.description = description;
        imageTab = new PresenterTabImage(img);
        tpanel.addTab("Obraz", imageTab);

        propertiesTab = new PresenterTabProperties(img);
        tpanel.addTab("Cechy", propertiesTab);

        histogram = new Histogram(img);

        Dimension histogramDimension = new Dimension(img.getWidth(), img.getHeight());

        historgamTab = new PresenterTabImage(histogram.createImg("INTERLACED", histogramDimension));
        tpanel.addTab("Histogram", historgamTab);

        operationsTabPane = new PresenterTabOperations(Operations.registerOperationsForImageServer(this), this);
        operationsTab = new JScrollPane(operationsTabPane);
        tpanel.addTab("Operacje", operationsTab);

        window.setTitle(description);

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
        if(fromFile == true) {
            Path p = Paths.get(this.srcFilePath);
            return p.getFileName().toString();
        } else {
            return this.description;
        }
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
            System.out.println("Błąd otwarcia obrazu z pliku");
        }

        return bufferedImage;
    }


    @Override
    public void runOperation(Operation operation) {

        BufferedImage newBufferedImage;

        newBufferedImage = operation.RunOperationFunction(img, histogram);

        imageTab.setImage(newBufferedImage);

        Histogram histogram = new Histogram(newBufferedImage);
        historgamTab.setImage(histogram.createImg("INTERLACED", new Dimension(img.getWidth(),img.getHeight())));

        operationsTabPane.updateControls(true);
    }

    @Override
    public void discardOperation(Operation operation) {
        if(imageTab.getImage() != img) {
            imageTab.setImage(img);
            historgamTab.setImage(histogram.createImg("INTERLACED", new Dimension(img.getWidth(),img.getHeight())));
        }
        operation.jButtonApply.setEnabled(true);
        operationsTabPane.updateControls(false);
    }

    @Override
    public void saveOperationsOutput(Operation operation) {
        ImageServer newImageServer = this.createChildImageServer(imageTab.getImage());
        newImageServer.toogleWindow();
        operation.jButtonApply.setEnabled(true);
        operationsTabPane.updateControls(false);
    }
}
