package jmagination;

import jmagination.ImageServer;
import jmagination.operations.*;

import java.util.ArrayList;

/**
 * Created by darek on 06.11.2016.
 */
public class Operations {


    public static ArrayList<Operation> registerOperationsForImageServer(ImageServer imageServer) {
        ArrayList<Operation>availableOperations = new ArrayList<Operation>();

        availableOperations.add(new OperationDuplicate(imageServer));
        availableOperations.add(new OperationConvertToGray(imageServer));
        availableOperations.add(new OperationEqualizeHistogram(imageServer));
        availableOperations.add(new OperationStrechHistogram(imageServer));
//        availableOperations.add(new OperationNegation(imageServer));
//        availableOperations.add(new OperationThreshold(imageServer));
//        availableOperations.add(new OperationThresholdSlider(imageServer));
//        availableOperations.add(new OperationStretchRanges(imageServer));
//        availableOperations.add(new OperationScale(imageServer));
        availableOperations.add(new OperationLUTPointOperation());
        availableOperations.add(new OperationLUTThreshold());
        availableOperations.add(new OperationLUTBinarization());
        availableOperations.add(new OperationLUTStretchInRanges());
        availableOperations.add(new OperationLUTScaleDown());
        availableOperations.add(new OperationLUTScale());
        availableOperations.add(new OperationLUTNegation());

        return availableOperations;
    }

    private Operations() {
        throw new AssertionError();
    }
}
