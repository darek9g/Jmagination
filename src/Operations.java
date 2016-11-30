import com.sun.corba.se.spi.orb.Operation;

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

    public static abstract class Operation {

        ImageServer srcImageServer;
        String label = "Dummy";
        Jmagination jmagination;

        public Operation(Jmagination jmagination) {
            this.jmagination = jmagination;
        }

        public Operation(ImageServer srcImageServer, Jmagination jmagination) {
            this(jmagination);
            this.srcImageServer = srcImageServer;
        }

        public String getLabel() {
            return label;
        }

        public abstract BufferedImage RunOperation(ImageServer srcImageServer);

        public abstract JPanel getConfiguratorPanel();

        public void Run() {
            jmagination.addImage(new ImageServer(RunOperation(srcImageServer),srcImageServer, jmagination));
        }
    }

    private Operations() {
        throw new AssertionError();
    }
}
