import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by darek on 06.11.2016.
 */
public class Operations {


    public static ArrayList<Operation> registerOperationsForImageServer(ImageServer imageServer, Jmagination jmagination) {
        ArrayList<Operation>availableOperations = new ArrayList<Operation>();

        availableOperations.add(new OperationDuplicate(imageServer, jmagination));
        availableOperations.add(new OperationConvertToGray(imageServer, jmagination));
        availableOperations.add(new OperationNormalizeHistogram(imageServer, jmagination));
        availableOperations.add(new OperationEqualizeHistogram(imageServer, jmagination));

        return availableOperations;
    }

    public static class OperationManager {

        ArrayList<Operation> doneOperations;

        public OperationManager() {

            doneOperations = new ArrayList<Operation>();
        }
    }

    private Operations() {
        throw new AssertionError();
    }
}
