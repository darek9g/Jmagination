import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

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

    }

    public int nextImageId() {
        return nextImageServerId++;
    }

    public JTree getTree() {
        return tree;
    }

    public void addLoadedImage(ImageServer newImageServer) {
        top.add(new DefaultMutableTreeNode(newImageServer));
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.reload();
    }

    public void addCreatedImage(ImageServer childImageServer, ImageServer parentImageServer) {
        DefaultMutableTreeNode theNode = null;
        for (Enumeration e = top.depthFirstEnumeration(); e.hasMoreElements() && theNode == null;) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject() == parentImageServer) {
                theNode = node;
                break;
            }
        }
        theNode.add(new DefaultMutableTreeNode(childImageServer));
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.reload();
    }
/*
    public void removeImage(ImageServer imageServer) {
        images.remove(imageServer);
    }



    public void loadImageToWorkspace(ImageServer imageServer) {
        workspace.setImageserver(imageServer);
    }*/
}
