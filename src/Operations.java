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
        availableOperations.add(new OperationNegation(imageServer));
        availableOperations.add(new OperationThreshold(imageServer));

        return availableOperations;
    }

    private Operations() {
        throw new AssertionError();
    }
}
