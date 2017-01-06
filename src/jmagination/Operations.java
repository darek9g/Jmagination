package jmagination;

import jmagination.ImageServer;
import jmagination.operations.*;

import java.util.ArrayList;

/**
 * Created by darek on 06.11.2016.
 */
public class Operations {


    public static ArrayList<Operation> registerOperations() {
        ArrayList<Operation>availableOperations = new ArrayList<Operation>();

        availableOperations.add(new OperationDuplicate());
        availableOperations.add(new OperationConvertToGray());
        availableOperations.add(new OperationEqualizeHistogram());
        availableOperations.add(new OperationStrechHistogram());

//        availableOperations.add(new OperationThresholdSlider(imageServer));

        availableOperations.add(new OperationLUTPointOperation());
        availableOperations.add(new OperationLUTThreshold());
        availableOperations.add(new OperationLUTBinarization());
        availableOperations.add(new OperationLUTStretchInRanges());
        availableOperations.add(new OperationLUTScaleDown());
        availableOperations.add(new OperationLUTScale());
        availableOperations.add(new OperationLUTNegation());

        availableOperations.add(new OperationTwoImagesTest());

        return availableOperations;
    }

    private Operations() {
        throw new AssertionError();
    }
}
