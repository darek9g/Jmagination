import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by darek on 25.10.16.
 */
public class Jmagination {

    Workspace workspace;
    ImageManager imageManager;


    public Jmagination() {

        imageManager = new ImageManager();
        workspace = new Workspace(imageManager);

    }

    public static void main(String[] args) {
        Jmagination j_obj = new Jmagination();
    }

}
