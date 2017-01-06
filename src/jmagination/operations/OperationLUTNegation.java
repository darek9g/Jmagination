package jmagination.operations;

import jmagination.guitools.LineEditor;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationLUTNegation extends OperationLUTPointOperation {

    {

        label = "Negowanie";
        header = "Negowanie wartości jasności pikseli";
        description = "Nadaje pikselom nowe wartości dopełniające wartości bieżące\ndo maksimum.";

        parameters = new Parameters(256);

        thresholdLineEditor = new LineEditor(LineEditor.NEGATE_MODE, 0, 255, 0, 255);
        thresholdLineEditor.addActionListener(runOperationTrigger);

    }

    @Override
    public Operation Clone() {
        return new OperationLUTNegation();
    }
}
