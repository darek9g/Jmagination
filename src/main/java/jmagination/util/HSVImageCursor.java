package jmagination.util;

/**
 * Created by Rideau on 2017-01-20.
 */
public class HSVImageCursor extends AbstractImageCursor<float[]> {

    private static int HUE=0, SAT=1, VAL=2;

    private float[][][] hsvMatrix;

    public HSVImageCursor(float[][][] matrix, int width, int height) {
        this.hsvMatrix = matrix;
        posX = 0;
        posY = 0;
        this.width = width;
        this.height = height;
    }

    @Override
    protected float[] getPixelValue(int x, int y, int bufferId) {
        return new float[]{hsvMatrix[x][y][HUE], hsvMatrix[x][y][SAT], hsvMatrix[x][y][VAL]};
    }

    @Override
    protected float[] getPixelMinPossibleValue() {
        return new float[]{0F, 0F, 0F};
    }

    @Override
    protected float[] getPixelMaxPossibleValue() {
        return new float[]{1F, 1F, 1F};
    }

    @Override
    protected float[] getSkipValue() {
        return new float[]{-1F, -1F, -1F};
    }
}
