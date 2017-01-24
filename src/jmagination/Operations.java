package jmagination;

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
        availableOperations.add(new OperationLUTPosterize());
        availableOperations.add(new OperationLUTScale());
        availableOperations.add(new OperationLUTNegation());
        availableOperations.add(new OperationLlinearSmoothing());
        availableOperations.add(new OperationTwoImagesArithmAdd());
        availableOperations.add(new OperationTwoImagesArithmSub());
        availableOperations.add(new OperationTwoImagesArithmDiff());
        availableOperations.add(new OperationTwoImagesLogicOR());
        availableOperations.add(new OperationTwoImagesLogicAND());
        availableOperations.add(new OperationTwoImagesLogicXOR());

        availableOperations.add(new OperationThinning());
        availableOperations.add(new OperationErosion());
        availableOperations.add(new OperationDilatation());
        availableOperations.add(new OperationMorphOpenning());
        availableOperations.add(new OperationMorphClosing());
        availableOperations.add(new OperationSharpening());
        availableOperations.add(new OperationGradientSpecialRoberts());
        availableOperations.add(new OperationGradientSpecialSobel());
        availableOperations.add(new OperationGradientEdgeConvolution());

        return availableOperations;
    }

    private Operations() {
        throw new AssertionError();
    }
}
