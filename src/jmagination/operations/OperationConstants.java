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

    public static final int[][] MASK_ROBERTS_GRADIENT_X_SAMPLE =
            { {  1,  0},
              {  0, -1} };

    public static final int[][] MASK_ROBERTS_GRADIENT_Y_SAMPLE =
            { {  0, -1},
              {  1,  0} };

    public static final int[][] MASK_SOBEL_GRADIENT_X_SAMPLE =
            { { -1,  0,  1},
              { -2,  0,  2},
              { -1,  0,  1} };

    public static final int[][] MASK_SOBEL_GRADIENT_Y_SAMPLE =
            { {  1,  2,  1},
              {  0,  0,  0},
              { -1, -2, -1} };

    public static final int[][] MASK_PREWITT_N_SAMPLE = {
            {  1,  1,  1},
            {  1, -2,  1},
            { -1, -1, -1},
    };

    public static final int[][] MASK_PREWITT_NE_SAMPLE = {
            {  1,  1,  1},
            { -1, -2,  1},
            { -1, -1,  1},
    };

    public static final int[][] MASK_PREWITT_E_SAMPLE = {
            { -1,  1,  1},
            { -1, -2,  1},
            { -1,  1,  1},
    };

    public static final int[][] MASK_PREWITT_SE_SAMPLE = {
            { -1, -1,  1},
            { -1, -2,  1},
            {  1,  1,  1},
    };

    public static final int[][] MASK_PREWITT_S_SAMPLE = {
            { -1, -1, -1},
            {  1, -2,  1},
            {  1,  1,  1},
    };

    public static final int[][] MASK_PREWITT_SW_SAMPLE = {
            {  1, -1, -1},
            {  1, -2, -1},
            {  1,  1,  1},
    };

    public static final int[][] MASK_PREWITT_W_SAMPLE = {
            {  1,  1, -1},
            {  1, -2, -1},
            {  1,  1, -1},
    };

    public static final int[][] MASK_PREWITT_NW_SAMPLE = {
            {  1,  1,  1},
            {  1, -2, -1},
            {  1, -1, -1},
    };

    public static final int[][] MASK_KIRCH_N_SAMPLE = {
            {  3,  3,  3},
            {  3,  0,  3},
            { -5, -5, -5},
    };

    public static final int[][] MASK_KIRCH_NE_SAMPLE = {
            {  3,  3,  3},
            { -5,  0,  3},
            { -5, -5,  3},
    };

    public static final int[][] MASK_KIRCH_E_SAMPLE = {
            { -5,  3,  3},
            { -5,  0,  3},
            { -5,  3,  3},
    };

    public static final int[][] MASK_KIRCH_SE_SAMPLE = {
            { -5, -5,  3},
            { -5,  0,  3},
            {  3,  3,  3},
    };

    public static final int[][] MASK_KIRCH_S_SAMPLE = {
            { -5, -5, -5},
            {  3,  0,  3},
            {  3,  3,  3},
    };

    public static final int[][] MASK_KIRCH_SW_SAMPLE = {
            {  3, -5, -5},
            {  3,  0, -5},
            {  3,  3,  3},
    };

    public static final int[][] MASK_KIRCH_W_SAMPLE = {
            {  3,  3, -5},
            {  3,  0, -5},
            {  3,  3, -5},
    };

    public static final int[][] MASK_KIRCH_NW_SAMPLE = {
            {  3,  3,  3},
            {  3,  0, -5},
            {  3, -5, -5},
    };

    public static final String[]  MASK_GEO_DIRECTIONS = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };

    public static final int[][][]  MASKS_PREWITT = { MASK_PREWITT_N_SAMPLE, MASK_PREWITT_NE_SAMPLE, MASK_PREWITT_E_SAMPLE, MASK_PREWITT_SE_SAMPLE, MASK_PREWITT_S_SAMPLE, MASK_PREWITT_SW_SAMPLE, MASK_PREWITT_W_SAMPLE, MASK_PREWITT_NW_SAMPLE };

    public static final int[][][]  MASKS_KIRCH = { MASK_KIRCH_N_SAMPLE, MASK_KIRCH_NE_SAMPLE, MASK_KIRCH_E_SAMPLE, MASK_KIRCH_SE_SAMPLE, MASK_KIRCH_S_SAMPLE, MASK_KIRCH_SW_SAMPLE, MASK_KIRCH_W_SAMPLE, MASK_KIRCH_NW_SAMPLE };

    private OperationConstants() {
        throw new AssertionError();
    }
}
