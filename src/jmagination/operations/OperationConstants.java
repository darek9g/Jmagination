package jmagination.operations;

/**
 * Created by darek on 23.01.2017.
 */
public final class OperationConstants {

    public static final int OP_MODE_RGB = 0;
    public static final int OP_MODE_HSV = 1;

    public static final int[][] MASK_GRADIENT_X_SAMPLE =
            { { -1, -2, -1},
            {  0,  0,  0},
            {  1,  2,  1} };

    public static final int[][] MASK_GRADIENT_Y_SAMPLE =
            { { -1,  0,  1},
            { -2,  0,  2},
            { -1,  0,  1} };

    public static final int[][] MASK_LAPLACE_SAMPLE =
            { {  0,  1,  0},
            {  1, -4,  1},
            {  0,  1,  0} };

    private OperationConstants() {
        throw new AssertionError();
    }
}
