import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by darek on 04.12.2016.
 */
public class ImageManager {

    DefaultMutableTreeNode top;

    public ImageManager() {
        top = new DefaultMutableTreeNode("Images");


/*
        jButtonForNewImage = new GUIStyler.JButtonS("New buffer");

        Jmagination selfHandle = this;

        this.operationManager = new Operations.OperationManager();

        jButtonForNewImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.setBackground(ConstantsInitializers.GUI_CONTROLS_BG_COLOR);
                chooser.setCurrentDirectory(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Pictures"));
                int chooserResult = chooser.showOpenDialog(Jmagination.this);
                if(chooserResult == JFileChooser.APPROVE_OPTION) {

                    BufferedImage loaded = ImageServer.LoadImageFromFile(chooser.getSelectedFile().getAbsolutePath());

                    if(loaded!=null) {
                        ImageServer iS = workspace.top.createChildImageServer(loaded, chooser.getSelectedFile().getAbsolutePath(), selfHandle);
                        addImage(iS);
                    }
                }
            }
        });*/


    }
}
