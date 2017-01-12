package jmagination.operations;

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;
import jmagination.gui.ImagesComboBox;
import jmagination.histogram.Histogram;
import util.ImageCursor;
import util.PixelHood;
import util.SimpleHSVBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import static jmagination.ConstantsInitializers.BR;

/**
 * Created by darek on 06.01.2017.
 */
public abstract class OperationTwoImagesAbstract extends Operation{

    Parameters parameters;

    ImagesComboBox imagesComboBox;

    ButtonGroup buttonGroupOperationMode;
    JRadioButton jRadioButtonCutToLeft;
    JRadioButton jRadioButtonCutToRight;
    JRadioButton jRadioButtonCutToMinimum;
    JRadioButton jRadioButtonCutToMaximum;

    {
        label = "Dummy - dwuobrazowa";
        header = "Dummy - oparacja arytmetyczna lub logiczna";
        description = "Dummy - wyznaczanie wartości według odpowiadających" + BR +
                "sobie pikesli dwóch obrazów.";

        parameters = new Parameters();

    }

    public OperationTwoImagesAbstract() {
        super();

        categories.add("LAB 2");
        categories.add("Dwuobrazowe");

        buttonGroupOperationMode = new ButtonGroup();

        jRadioButtonCutToLeft = new JRadioButton("Do wielkości głównego obrazu (lewy operand)");
        jRadioButtonCutToRight = new JRadioButton("Do wielkości wybranego obrazu (prawy operand)");
        jRadioButtonCutToMinimum = new JRadioButton("Do minimalnej szerokości i wysokości");
        jRadioButtonCutToMaximum = new JRadioButton("Do maksymalnej szerokości i wysokości");

        buttonGroupOperationMode.add(jRadioButtonCutToLeft);
        buttonGroupOperationMode.add(jRadioButtonCutToRight);
        buttonGroupOperationMode.add(jRadioButtonCutToMinimum);
        buttonGroupOperationMode.add(jRadioButtonCutToMaximum);

        jRadioButtonCutToLeft.setSelected(true);
    }

    @Override
    public SimpleHSVBufferedImage RunOperationFunction(SimpleHSVBufferedImage bufferedImage, Histogram histogram) {

        if(jRadioButtonCutToLeft.isSelected()) {
            parameters.cutMode = Parameters.CUT_TO_LEFT;
        }
        if(jRadioButtonCutToRight.isSelected()) {
            parameters.cutMode = Parameters.CUT_TO_RIGHT;
        }
        if(jRadioButtonCutToMinimum.isSelected()) {
            parameters.cutMode = Parameters.CUT_TO_MINIMUM;
        }
        if(jRadioButtonCutToMaximum.isSelected()) {
            parameters.cutMode = Parameters.CUT_TO_MAXIMUM;
        }

        parameters.operands.clear();
        parameters.operands.add(imagesComboBox.getSelectedImageServer());

        return twoImagesFunction(bufferedImage);
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
        JLabel title = new JLabel(header);
        panel.add(title, c);

        // opis
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 16;
        JTextArea jTextAreadescription = new JTextArea(description);
        jTextAreadescription.setEditable(false);
        panel.add(jTextAreadescription, c);

        // etykieta
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(new JLabel("Wybierz prawy operand"), c);

        // wybór obrazu
        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = GridBagConstraints.REMAINDER;
        imagesComboBox = new ImagesComboBox(runOperation.supplyAvailableImages(), ConstantsInitializers.GUI_LARGE_IMAGEICON_SIZE.width, ConstantsInitializers.GUI_LARGE_IMAGEICON_SIZE.height);
        panel.add(imagesComboBox, c);

        // wybór sposobu wyznaczenia rozmiaru wynikowego obrazu
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonCutToLeft, c);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonCutToRight, c);

        c.gridx = 0;
        c.gridy = 8;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonCutToMinimum, c);

        c.gridx = 0;
        c.gridy = 9;
        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(jRadioButtonCutToMaximum, c);


        // wiersz sterowania wykonaniem
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jLabelColorMode, c);

        c.gridx+= c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeRGB, c);

        c.gridx+= c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jRadioButtonColorModeHSV, c);

        c.gridx+= c.gridwidth;
        c.gridy = 12;
        c.gridwidth = 4;
        panel.add(jButtonApply, c);


        configureColorModeControls();
    }

    public SimpleHSVBufferedImage twoImagesFunction(SimpleHSVBufferedImage leftImage) {
        int leftWidth = leftImage.getWidth();
        int leftHeight = leftImage.getHeight();

        SimpleHSVBufferedImage rightImage = parameters.operands.get(0).getImg();

        int rightWidth = rightImage.getWidth();
        int rightHeigth = rightImage.getHeight();

        int smallerWidth = leftWidth < rightWidth ? leftWidth : rightWidth;
        int smallerHeight = leftHeight < rightWidth ? leftHeight : rightHeigth;
        int largerWidth = leftWidth > rightWidth ? leftWidth : rightWidth;
        int largerHeight = leftHeight > rightWidth ? leftHeight : rightHeigth;

        int outWidth = 0;
        int outHeight = 0;

        switch(parameters.cutMode) {
            case Parameters.CUT_TO_LEFT:
                outWidth = leftWidth;
                outHeight = leftHeight;
                break;
            case Parameters.CUT_TO_RIGHT:
                outWidth = rightWidth;
                outHeight = rightHeigth;
                break;
            case Parameters.CUT_TO_MINIMUM:
                outWidth = smallerWidth;
                outHeight = smallerHeight;
                break;
            case Parameters.CUT_TO_MAXIMUM:
                outWidth = largerWidth;
                outHeight = largerHeight;
                break;
            default:
                throw new IllegalStateException("Nieobsłużona tryb wyznaczania nowej wartości obrazu.");
        }

        SimpleHSVBufferedImage outImage = new SimpleHSVBufferedImage(outWidth, outHeight, leftImage.getType());
        WritableRaster outRaster = outImage.getRaster();
        WritableRaster leftRaster = leftImage.getRaster();
        WritableRaster rightRaster = rightImage.getRaster();


        int supplementaryValue = parameters.fillValue;
        int[] fakePixel = new int[outRaster.getNumBands()];
        for(int i=0;i<fakePixel.length;++i) {
            fakePixel[i] = supplementaryValue;
        }

        PixelHood<int[]> leftPixelHood = new PixelHood<>(0,0, new int[leftRaster.getNumBands()]);
        PixelHood<int[]> rightPixelHood = new PixelHood<>(0,0, new int[rightRaster.getNumBands()]);
        ImageCursor leftImageCursor = new ImageCursor(leftImage);
        ImageCursor rightImageCursor = new ImageCursor(rightImage);


        switch (parameters.cutMode) {
            case Parameters.CUT_TO_MINIMUM:
                break;
            default:

                for(int i = 0; i<outHeight; ++i) {
                    for(int j = 0; j < outWidth; ++j) {

                        int[] pixel = new int[outImage.getRaster().getNumBands()];
                        int[] leadingPixel;

                        if (i > smallerHeight || j > smallerWidth) {
                            if(leftImageCursor.checkXYinRange(j, i) == true) {
                                try {
                                    leftImageCursor.goTo(j, i);
                                    leftImageCursor.fillPixelHood(leftPixelHood, ImageCursor.COMPLETE_COPY);
                                    leadingPixel = leftPixelHood.getPixel(0);

                                } catch (Exception e) {
                                    throw new IllegalStateException("Współrzędne (" + j + "," + i + ") spoza zakresu obrazka leftImage");
                                }
                            } else {

                                if(rightImageCursor.checkXYinRange(j, i) == true) {

                                    try {
                                        rightImageCursor.goTo(j, i);
                                        rightImageCursor.fillPixelHood(rightPixelHood, ImageCursor.COMPLETE_COPY);
                                        leadingPixel = rightPixelHood.getPixel(0);
                                    } catch (Exception e) {
                                        throw new IllegalStateException("Współrzędne (" + j + "," + i + ") spoza zakresu obrazka rightImage");
                                    }
                                } else {
                                    leadingPixel = fakePixel;
                                }
                            }

                            for(int b = 0; b < pixel.length; ++b) {
                                pixel[b] = fakePixel[b];
                            }
                            for (int b = 0; b < (pixel.length < leadingPixel.length ? pixel.length : leadingPixel.length); ++b) {
                                pixel[b] = leadingPixel[b];
                            }


                            outRaster.setPixel(j, i, pixel);
                        }
                    }
                }
        }

        leftImageCursor.reset();
        rightImageCursor.reset();

        for(int i = 0; i<largerHeight; ++i) {
            if(i>=smallerHeight) { break; }
            for(int j = 0; j < largerWidth; ++j) {
                if(j>=smallerWidth) { break; }

                try {
                    leftImageCursor.goTo(j, i);
                } catch (Exception e)
                {
                    throw new IllegalStateException("Współrzędne (" + j + "," + i + ") spoza zakresu obrazka leftImage");
                }
                leftImageCursor.fillPixelHood(leftPixelHood, ImageCursor.COMPLETE_COPY);
                int[] leftPixel = leftPixelHood.getPixel(0);

                try {
                    rightImageCursor.goTo(j,i);
                } catch (Exception e)
                {
                    throw new IllegalStateException("Współrzędne (" + j + "," + i + ") spoza zakresu obrazka rightImage");
                }
                rightImageCursor.fillPixelHood(rightPixelHood, ImageCursor.COMPLETE_COPY);
                int[] rightPixel = rightPixelHood.getPixel(0);

                int[] pixel = twoImagesPixelFunction(outRaster, leftPixel, rightPixel, parameters.fillValue);
                outRaster.setPixel(j,i,pixel);
            }
        }

        return outImage;

    }

    public abstract int[] twoImagesPixelFunction(WritableRaster outRaster, int[] leftPixel, int[] rightPixel, int fillValue);

    protected class Parameters {

        public static final int CUT_TO_LEFT = 0;
        public static final int CUT_TO_RIGHT = 1;
        public static final int CUT_TO_MINIMUM = 2;
        public static final int CUT_TO_MAXIMUM = 3;

        int cutMode;
        int fillValue;
        ArrayList<ImageServer> operands;


        {
            cutMode = CUT_TO_LEFT;
            fillValue = 255;
            operands = new ArrayList<>();
        }

        public Parameters() {}
    }

}
