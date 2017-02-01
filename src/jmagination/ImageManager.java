package jmagination;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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


        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

    }

    public int nextImageId() {
        return nextImageServerId++;
    }

    public JTree getTree() {
        return tree;
    }

    public void addLoadedImage(ImageServer newImageServer) {
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newImageServer);
        top.add(newNode);
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        model.nodeChanged(top);
        model.reload();
//        model.nodesWereInserted();
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
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(childImageServer);
        theNode.add(newNode);
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

        // TODO
//        model.reload();
        model.nodeChanged(theNode);
        tree.expandPath(new TreePath(theNode));
    }

    public DefaultMutableTreeNode[] getNodesArray() {

        ArrayList<DefaultMutableTreeNode> nodesList = new ArrayList<>();

        for(int i=0; i< top.getChildCount(); ++i) {

            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) top.getChildAt(i);
            extractTreeBranch(childNode, nodesList);
        }

        return nodesList.toArray(new DefaultMutableTreeNode[0]);
    }

    private void extractTreeBranch(DefaultMutableTreeNode node, ArrayList<DefaultMutableTreeNode> list) {

        list.add(node);

        for(int i=0; i< node.getChildCount(); ++i) {

            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            if (childNode.isLeaf() == false) {
                extractTreeBranch(childNode, list);
            } else {
                list.add(childNode);
            }
        }
    }


/*
    public void removeImage(jmagination.ImageServer imageServer) {
        images.remove(imageServer);
    }



    public void loadImageToWorkspace(jmagination.ImageServer imageServer) {
        workspace.setImageserver(imageServer);
    }*/
}
