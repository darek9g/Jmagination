package jmagination;

import jmagination.gui.*;
import jmagination.histogram.Histogram;
import jmagination.operations.Operation;
import jmagination.util.SimpleHSVBufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.gui.ImagesComboBox.resizeBufferedImageToFit;


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


    SimpleHSVBufferedImage img;
    BufferedImage imgPreview;
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

    ImageServer(SimpleHSVBufferedImage img, ImageManager imageManager, String filePath, boolean fromFile) {
        this(imageManager);
        this.img = img;
        this.fromFile = fromFile;
        this.srcFilePath = filePath;

        if(this.fromFile == true) {
            configure("Id: " + this.id + " z pliku: " + filePath);
        } else {
            configure("Id: " + this.id + " - nowy obraz");
        }

        setPreview();
    }

    ImageServer(BufferedImage img, ImageManager imageManager, String filePath, boolean fromFile) {
        this(new SimpleHSVBufferedImage(img), imageManager, filePath, fromFile);
    }

    public static ImageServer createLoadedImageServer(BufferedImage img, String filePath, ImageManager imageManager) {
        ImageServer child = new ImageServer(img, imageManager, filePath, true);
        imageManager.addLoadedImage(child);
        return child;
    }

    public ImageServer createChildImageServer(SimpleHSVBufferedImage img) {
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

        operationsTabPane = new PresenterTabOperations(Operations.registerOperations(), this);
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

    private void setPreview() {
        imgPreview = resizeBufferedImageToFit(img, ConstantsInitializers.GUI_PREVIEW_IMAGE_HEIGHT, ConstantsInitializers.GUI_PREVIEW_IMAGE_HEIGHT);
    }

    public int getId() {
        return id;
    }

    public String getSaveFileName() {
        String filename = Paths.get(this.srcFilePath).getFileName().toString();
        if(fromFile == false) {
            if(filename.matches(".*[^.]+\\.[^.]+$")) {
                Matcher m = Pattern.compile("(.*)\\.([^.]*$)").matcher(filename);
                if(m.matches()) {
                    filename = m.group(1) + "-" + description.replace(' ', '_') + "." + m.group(2);
                }
            } else {
                filename = filename + "-" + description.replace(' ', '_');
            }
        }
        return filename;
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

    public SimpleHSVBufferedImage getImg() {
        return img;
    };

    public Histogram getHistogram() {
        return histogram;
    }

    public static BufferedImage LoadImageFromFile(String filePath, JFrame frame) {
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.out.println("Błąd otwarcia obrazu z pliku");
            JOptionPane.showMessageDialog(frame,
                    "Błąd otwarcia obrazu z pliku: " + e.getLocalizedMessage(),
                    "Nie wczytano pliku",
                    JOptionPane.PLAIN_MESSAGE);

        }

        return bufferedImage;
    }

    public static void SaveImageToFile(BufferedImage bufferedImage, String filePath, JFrame frame) {

        File outputfile = new File(filePath);
        String filename = outputfile.getName();

        String extension = "";
        String format = "png";

        Matcher m = Pattern.compile(".*\\.([^.]*)").matcher(filename);
        if(m.matches()) {
            extension = m.group(1);
        }

        if(extension.matches("^$")) {
            format = "png";
            JOptionPane.showMessageDialog(frame,
                    "W nazwie pliku nie ma rozszerzenia" + BR + "Użyty będzie format " + format,
                    "Informacja o formacie pliku",
                    JOptionPane.PLAIN_MESSAGE);
        } else {
            if(extension.matches("[pP][nN][gG]")) {
                format = "png";
            } else {
                if(extension.matches("[jJ][pP][eE]{0,1}[gG]")) {
                    format = "jpg";
                } else {
                    if(extension.matches("[gG][iI][fF]")) {
                        format = "gif";
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Brak wsparcia dla zapisu w formacie: " + extension + BR + "Użyty będzie format " + format,
                                "Informacja o formacie pliku",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        }

        try {
            ImageIO.write(bufferedImage, format, outputfile);
        } catch (IOException e) {
            System.out.println("Błąd zapisu obrazu do pliku");
            JOptionPane.showMessageDialog(frame,
                    "Błąd zapisu obrazu do pliku: " + e.getLocalizedMessage(),
                    "Nie wykonano zapisu do pliku",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    public void runOperation(Operation operation) {

        SimpleHSVBufferedImage newBufferedImage;

        newBufferedImage = operation.RunOperationFunction(img, histogram);

        imageTab.setImage(newBufferedImage);

        Histogram histogram = new Histogram(newBufferedImage);
        historgamTab.setImage(histogram.createImg("INTERLACED", new Dimension(img.getWidth(),img.getHeight())));

        operationsTabPane.updateControls(true);
    }

    public void discardOperation(Operation operation) {
        if(imageTab.getImage() != img) {
            imageTab.setImage(img);
            historgamTab.setImage(histogram.createImg("INTERLACED", new Dimension(img.getWidth(),img.getHeight())));
        }
        operation.jButtonApply.setEnabled(true);
        operationsTabPane.updateControls(false);
    }

    public void saveOperationsOutput(Operation operation) {
        ImageServer newImageServer = this.createChildImageServer((SimpleHSVBufferedImage) imageTab.getImage());
        newImageServer.toogleWindow();
        operation.jButtonApply.setEnabled(true);
        operationsTabPane.updateControls(false);
    }

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
