package jmagination.gui;

import jmagination.ConstantsInitializers;
import jmagination.ImageServer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by darek on 06.01.2017.
 * Modifications to the following:
 *
 * http://docs.oracle.com/javase/tutorial/uiswing/examples/components/CustomComboBoxDemoProject/src/components/CustomComboBoxDemo.java
 *
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class ImagesComboBox extends JPanel{

    JComboBox jComboBox;
    Integer[] indexes;
    ImageIcon[] icons;
    String[] labels;

    public ImagesComboBox(ImageServer[] imageServers, int width, int height) {

        if(imageServers.length == 0) {
            indexes = new Integer[1];
            indexes[0] = new Integer(0);
            icons = new ImageIcon[1];
            icons[0] = null;
            labels = new String[1];
            labels[0] = "Nie wczytano żadnego obrazu";
        } else {

            indexes = new Integer[imageServers.length];
            icons = new ImageIcon[imageServers.length];
            labels = new String[imageServers.length];
        }


        for(int i=0;i<imageServers.length;++i) {
            indexes[i] = new Integer(i);
            icons[i] = new ImageIcon(resizeBufferedImageToFit(imageServers[i].getImg(), width, height));
            labels[i] = new String(imageServers[i].toString());
        }

        jComboBox = new JComboBox(indexes);

        ComboBoxRenderer renderer= new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(2 * width, 2 + height));
        jComboBox.setRenderer(renderer);
        jComboBox.setMaximumRowCount(ConstantsInitializers.GUI_LARGE_IMAGEICON_COMBOBOX_DISPLAYED);

        //Lay out the demo.
        add(jComboBox);
//        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

    }

    public JComboBox getjComboBox() {
        return jComboBox;
    }

    private BufferedImage resizeBufferedImageToFit(BufferedImage inputImage, int maxWidth, int maxHeight) {
        int inputWidth = inputImage.getWidth();
        int inputHeight = inputImage.getHeight();

        double scale;

        double scaleX = inputWidth / ( maxWidth + 0.0d);
        double scaleY = inputHeight / ( maxHeight + 0.0d);

        if( scaleX > scaleY) {
            scale = scaleX;
        } else {
            scale = scaleY;
        }

        int newWidth = (int) Math.round( inputWidth / scale );
        int newHeight = (int) Math.round( inputHeight / scale );

        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, inputImage.getType());

        Graphics2D graphics = outputImage.createGraphics();
        graphics.drawImage(inputImage, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        return outputImage;
    }

    class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        public ComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(CENTER);
        }

        /*
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            //Get the selected index. (The index param isn't
            //always valid, so just use the value.)

            list.setFont(ConstantsInitializers.GUI_SMALL_FONT);

            int selectedIndex = ((Integer)value).intValue();

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            //Set the icon and text.  If icon was null, say so.
            ImageIcon icon = icons[selectedIndex];
            String label = labels[selectedIndex];
            setIcon(icon);
            setText(label);
            setFont(list.getFont());


            return this;
        }
    }
}
