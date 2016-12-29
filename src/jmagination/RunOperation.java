package jmagination;

import jmagination.operations.Operation;

/**
 * Created by darek on 16.12.2016.
 */
public interface RunOperation {
    public void runOperation(Operation operation);
    public void discardOperation(Operation operation);
    public void saveOperationsOutput(Operation operation);

}
