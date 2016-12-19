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
    Operation that = this;

    RunOperation runOperation;

    JButton jButtonApply  = new JButton("Wykonaj");




    ActionListener runOperationTrigger = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            jButtonApply.setEnabled(false);
            RunOperation(that);
        }
    };

    public Operation() {
        categories.add("Wszystkie");
        jButtonApply.addActionListener(runOperationTrigger);
    }

    public ArrayList<String> getCategories () {
        return categories;
    }


    public void setRunOperation(RunOperation runOperation) {
        this.runOperation = runOperation;
    }

    public String getLabel() {
        return label;
    }

    public String toString() {
        return label;
    }

    public abstract BufferedImage RunOperationFunction(BufferedImage bufferedImage, Histogram histogram);

    public void RunOperation(Operation operation) {
        runOperation.runOperation(operation);
    };

    public abstract void drawConfigurationPanel(JPanel panel);

    public abstract Operation Clone();

}