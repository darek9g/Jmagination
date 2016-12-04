import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by darek on 04.12.2016.
 */
public class ImageManager {

    int nextImageServerId;
    DefaultMutableTreeNode top;
    JTree tree;


    public ImageManager() {

        nextImageServerId = 0;
        top = new DefaultMutableTreeNode("Images");
        tree = new JTree(top);




/*        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);*/


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

    public int nextImageId() {
        return nextImageServerId++;
    }

    public JTree getTree() {
        return tree;
    }

/*    public void addImage(ImageServer imageServer) {
        images.add(imageServer);
        window.getContentPane().add(imageServer.getCallUpButton());
        System.out.println("Button text: " + imageServer.getCallUpButton().getText());
        window.pack();
        repaint();
    }

    public void removeImage(ImageServer imageServer) {
        images.remove(imageServer);
    }



    public void loadImageToWorkspace(ImageServer imageServer) {
        workspace.setImageserver(imageServer);
    }*/
}
