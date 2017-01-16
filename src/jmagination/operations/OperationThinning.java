package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.histogram.Histogram;
import util.ImageCursor;
import util.PixelHood;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static jmagination.ConstantsInitializers.BR;
import static jmagination.operations.OperationDuplicate.duplicateImageFunction;
import static util.SimpleHSVBufferedImage.NORMALIZATION_MODE_BINARY;
import static util.SimpleHSVBufferedImage.NORMALIZATION_MODE_PROPORTIONAL;
import static util.SimpleHSVBufferedImage.NORMALIZATION_MODE_VOID;

/**
 * Created by darek on 30.11.2016.
 */

public class OperationThinning extends Operation {

    public static final int COMPLETE_MIN = 0;
    public static final int COMPLETE_MAX = 1;
    public static final int COMPLETE_COPY = 2;
    public static final int COMPLETE_SKIP = 3;


    Parameters parameters;
    String[] edgeModes = { "Wartości minimalne", "Wartości maksymalne", "Powtórzenie piksela z obrazu", "Pominięcie brzegu"};

    JComboBox<String> methodSelect = new JComboBox<>(edgeModes);

    public OperationThinning() {
        super();
        this.label = "Ścienianie";
        categories.add("LAB 4");
        categories.add("Analiza otoczenia");

        parameters = new Parameters();

        methodSelect.setSelectedIndex(0);

    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {

        String method = (String) methodSelect.getSelectedItem();
        parameters.method = method;

        return thinningFunction(bufferedImage, histogram);
    }

    @Override
    public void drawConfigurationPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        panel.setBackground(ConstantsInitializers.GUI_DRAWING_BG_COLOR);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2,2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0f;
        c.weighty = 1.0f;

        //tytuł
        c.gridx =0;
        c.gridy =0;
        c.gridwidth = 16;
        JLabel title = new JLabel("Klasyczna metoda ścieniania");
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea description = new JTextArea("Znajduje szkielet obrazu" + BR + "metodą klasycznej szkieletyzacji");
        description.setEditable(false);
        panel.add(description, c);

        // parametryzacja
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        JLabel jLabelMethodSelect = new JLabel("Sąsiedztwo pikseli brzegowch:");
        panel.add(jLabelMethodSelect, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(methodSelect, c);

        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 4;
        panel.add(jLabelColorMode, c);

        c.gridx+= c.gridwidth;
        c.gridy = 6;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeRGB, c);

        c.gridx+= c.gridwidth;
        c.gridy = 6;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeHSV, c);

        c.gridx+= c.gridwidth;
        c.gridy = 6;
        c.gridwidth = 4;
        panel.add(jButtonApply, c);


        configureColorModeControls();
    }

    @Override
    public Operation Clone() {
        return new OperationThinning();
    }

    public SimpleHSVBufferedImage thinningFunction(SimpleHSVBufferedImage inImage, Histogram histogram) {

        SimpleHSVBufferedImage outImage = duplicateImageFunction(inImage);

        binarizeImage(outImage);

        ArrayList<PixelHood<int[]>> skelNeighborTemplates = new ArrayList<>();

        int[] aValue = new int[outImage.getRaster().getNumBands()];
        for(int b=0; b<aValue.length; b++) {
            aValue[b] = parameters.objectValue;
        }

        int[] zeroValue = new int[outImage.getRaster().getNumBands()];
        for(int b=0; b<zeroValue.length; b++) {
            zeroValue[b] = parameters.bgValue;
        }

        int[] twoValue = new int[outImage.getRaster().getNumBands()];
        for(int b=0; b<twoValue.length; b++) {
            twoValue[b] = parameters.keepValue;
        }

        for(int i=0; i<6; i++) {
            PixelHood pH = new PixelHood<>(1,1, aValue);

            switch (i) {
                case 0:
                    pH.setPixel(-1, 0, zeroValue);
                    pH.setPixel(1, 0, zeroValue);
                    break;
                case 1:
                    pH.setPixel(0, -1, zeroValue);
                    pH.setPixel(0, 1, zeroValue);
                    break;
                case 2:
                    pH.setPixel(0, 1, zeroValue);
                    pH.setPixel(1, 0, zeroValue);
                    pH.setPixel(1, 1, twoValue);
                    break;
                case 3:
                    pH.setPixel(-1, 0, zeroValue);
                    pH.setPixel(-1, 1, twoValue);
                    pH.setPixel(0, 1, zeroValue);
                    break;
                case 4:
                    pH.setPixel(-1, -1, twoValue);
                    pH.setPixel(-1, 0, zeroValue);
                    pH.setPixel(0, -1, zeroValue);
                    break;
                case 5:
                    pH.setPixel(0, -1, zeroValue);
                    pH.setPixel(1, -1, twoValue);
                    pH.setPixel(1, 0, zeroValue);
                    break;
                default:
            }

            skelNeighborTemplates.add(pH);
        }



        ArrayList<Point> essentialNeighbors = new ArrayList<>();
        essentialNeighbors.add(new Point( 1, 0));
        essentialNeighbors.add(new Point( 0, 1));
        essentialNeighbors.add(new Point( -1, 0));
        essentialNeighbors.add(new Point( 0, -1));



        for(int b = 0; b<outImage.getRaster().getNumBands(); ++b) {

            boolean remain = true;

            while (remain == true) {
                remain = false;



                for (Point n : essentialNeighbors) {

                    PixelHood<int[]> pixelHood = new PixelHood<>(1, 1, new int[outImage.getRaster().getNumBands()]);
                    ImageCursor imageCursor = new ImageCursor(outImage);

                    do {
                        imageCursor.fillPixelHood(pixelHood, 1, ImageCursor.COMPLETE_COPY);


                        int[] pixel = pixelHood.getPixel(0, 0);


                        if (pixel[b] == parameters.objectValue && pixelHood.getPixel(n.x, n.y)[b] == parameters.bgValue) {

                            boolean skel = false;
                            for(PixelHood<int[]> templ: skelNeighborTemplates) {
                                if(skel == true) break;

                                int nonBgNeighborsCount = 0;

                                mask:
                                for(int j=-1; j<2; j++) {
                                    for(int i=-1; i<2; i++) {
                                        if(j==i) continue;

                                        if(templ.getPixel(j, i)[b] == zeroValue[b] || templ.getPixel(j, i)[b] == twoValue[b]) {
                                            if(templ.getPixel(j, i)[b] != pixelHood.getPixel(j, i)[b]) {
                                                // to nie ta maska

                                                nonBgNeighborsCount = 0;
                                                break mask;
                                            }
                                        } else {
                                            if(pixelHood.getPixel(j, i)[b] != parameters.bgValue) {
                                                nonBgNeighborsCount++;
                                            }
                                        }
                                    }
                                }
                                if(nonBgNeighborsCount>0) {
                                    skel = true;
                                }

                            }

                            if(skel==true) {
                                pixel[b] = parameters.keepValue;
                            } else {
                                pixel[b] = parameters.deleteValue;
                                remain = true;
                            }

                            outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);
                        }


                    } while (imageCursor.forward());



                    PixelHood<int[]> pixelBuf = new PixelHood<>(0, 0, new int[outImage.getRaster().getNumBands()]);
                    imageCursor.reset();

                    do {
                        imageCursor.fillPixelHood(pixelBuf, 1, ImageCursor.COMPLETE_COPY);

                        int[] pixel = pixelBuf.getPixel(0,0);

                            if(pixel[b] == parameters.deleteValue) {
                                pixel[b] = parameters.bgValue;
                            }

                        outImage.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), pixel);

                    } while (imageCursor.forward());
                }
            }
        }


        outImage.normalize(NORMALIZATION_MODE_BINARY);

        return outImage;
    }

    public static void  binarizeImage(SimpleHSVBufferedImage image) {

        // Zapisuje obraz wartościami 0,1
        PixelHood<int[]> pixelHood = new PixelHood<>(0, 0, new int[image.getRaster().getNumBands()]);
        ImageCursor imageCursor = new ImageCursor(image);

        do {
            imageCursor.fillPixelHood(pixelHood, 0, ImageCursor.COMPLETE_COPY);

            int[] pixel = pixelHood.getPixel(0,0);
            int[] newPixel = new int[pixel.length];

            for(int b = 0; b<pixel.length; ++b) {
                if(pixel[b] > 0) {
                    newPixel[b] = 1;
                } else {
                    newPixel[b] = 0;
                }
            }

            image.setPixel(imageCursor.getPosX(), imageCursor.getPosY(), newPixel);

        } while (imageCursor.forward());
        image.normalize(NORMALIZATION_MODE_BINARY);

    }

    private class Parameters {
        String method = "Wartości minimalne";

        int objectValue = 1;
        int bgValue = 0;
        int keepValue = 2;
        int deleteValue = 3;

        public Parameters() {}
    }
}