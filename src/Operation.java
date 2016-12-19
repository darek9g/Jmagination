import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by darek on 03.12.2016.
 */
public abstract class Operation {

    String label = "Dummy";
    ArrayList<String> categories = new ArrayList<>();
    boolean hsvModeAllowed = false;
    GUIStyler.ImagePanel3 imageContainer = null;
    GUIStyler.ImagePanel3 histogramContainer = null;
    RunOperation runOperation;

    BufferedImage originalBufferedImage = null;

    ActionListener runOperationTrigger = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            RunOperation();
        }
    };

    public Operation() {
        categories.add("Wszystkie");
    }

    public ArrayList<String> getCategories () {
        return categories;
    }

    public void setOriginalImage() {
        if (imageContainer == null) {
            return;
        }

        if (imageContainer.getImage() == null) {
            return;
        }

        originalBufferedImage = OperationDuplicate.duplicateImageFunction(imageContainer.getImage());
    }

    public void releaseOriginalImage() {
        originalBufferedImage = null;
    }

    public void setRunOperation(RunOperation runOperation) {
        this.runOperation = runOperation;
    }

    public void setImageContainer(GUIStyler.ImagePanel3 imagePanel) {
        imageContainer = imagePanel;
    };

    public void setHistogramContainer(GUIStyler.ImagePanel3 histogramPanel) {
        histogramContainer = histogramPanel;
    };

    public String getLabel() {
        return label;
    }

    public String toString() {
        return label;
    }

    public abstract BufferedImage RunOperationFunction(BufferedImage bufferedImage);

    public void RunOperation() {
        if (imageContainer == null) {
            return;
        }

        if (imageContainer.getImage() == null) {
            return;
        }

        if (imageContainer.isImageChanged() == true) {
            setOriginalImage();
            imageContainer.setImageChanged(false);
        }

        BufferedImage newBufferedImage;

        newBufferedImage = RunOperationFunction(originalBufferedImage);

        imageContainer.replaceImage(newBufferedImage);


        if (histogramContainer!=null) {
            Histogram histogram = new Histogram(newBufferedImage);
            histogramContainer.replaceImage(histogram.createImg("INTERLACED", ConstantsInitializers.GUI_DIMENSION_histogramPanelCentral));
        }

        runOperation.postOperation();

    };

    public abstract void drawConfigurationPanel(JPanel panel);

    public abstract Operation Clone();

}