package jmagination.operations;

import jmagination.guitools.LineEditor;

/**
 * Created by darek on 06.01.2017.
 */
public class OperationLUTThreshold extends OperationLUTPointOperation {

    {

        label = "Proguj piksele";
        header = "Progowanie";
        description = "Ustawienie minimalnej jasności pikselom spoza określonego \nzakresu jasności.";

        parameters = new Parameters(256);

        thresholdLineEditor = new LineEditor(LineEditor.MIN_ORG_MODE, 0, 255, 0, 255);
        thresholdLineEditor.addActionListener(runOperationTrigger);

    }

    @Override
    public Operation Clone() {
        return new OperationLUTThreshold();
    }
}
