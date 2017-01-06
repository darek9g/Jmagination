package jmagination.operations;

import jmagination.guitools.LineEditor;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationLUTBinarization extends OperationLUTPointOperation {

    {

        label = "Proguj piksele binarnie";
        header = "Binaryzacja";
        description = "Piksele ze wskazanych zakresów jasności otrzymuja maksymalna wartości jasności.\nPozostałe otrzymują wartość minimalną.";

        parameters = new Parameters(256);

        thresholdLineEditor = new LineEditor(LineEditor.MIN_MAX_MODE, 0, 255, 0, 255);
        thresholdLineEditor.addActionListener(runOperationTrigger);

    }

    @Override
    public Operation Clone() {
        return new OperationLUTBinarization();
    }
}
