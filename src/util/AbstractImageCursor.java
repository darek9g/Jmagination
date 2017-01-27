package util;

public abstract class AbstractImageCursor<E> {

    public static final int COMPLETE_MIN = 0;
    public static final int COMPLETE_MAX = 1;
    public static final int COMPLETE_COPY = 2;
    public static final int COMPLETE_SKIP = 4;
    public static final int COMPLETE_MINUS = 5;

    protected int posX;
    protected int posY;

    protected int height;
    protected int width;

    public void reset() {
        posX=0;
        posY=0;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public boolean goTo(int x, int y) throws Exception {
        if(x<0 || x>width-1 || y<0 || y>height-1 ) {
            return false;
        }

        posX=x;
        posY=y;

        return true;
    }

    public boolean forward (int step) {

        int pixels = width * height;
        int pixels_accumulated = posY * width + posX + 1 + step;

        if(pixels_accumulated>pixels) { return false; }

        int mod = pixels_accumulated % width;
        int div = pixels_accumulated / width;
        if(div > 0 && mod == 0) {
            posX = width - 1;
            posY = div - 1;
        } else {
            posX = mod - 1;
            posY = div;
        }

        return true;
    }

    public boolean forward() {
        return forward(1);
    }

    public boolean hasNextPixelHood() {

        int savePosX = posX;
        int savePosY = posY;

        boolean test = forward();

        posX = savePosX;
        posY = savePosY;

        return test;
    }

    public void fillPixelHood(PixelHood<E> pixelHood, int commonCompleteMode) {
        fillPixelHood(pixelHood, commonCompleteMode, commonCompleteMode, commonCompleteMode, commonCompleteMode);
    }

    public void fillPixelHood(PixelHood<E> pixelHood, int bufferId, int commonCompleteMode) {
        fillPixelHood(pixelHood, bufferId, commonCompleteMode, commonCompleteMode, commonCompleteMode, commonCompleteMode);
    }

    public void fillPixelHood(PixelHood<E> pixelHood, int leftCompleteMode, int rightCompleteMode, int topCompleteMode, int bottomCompleteMode) {
        fillPixelHood(pixelHood, 0, leftCompleteMode, rightCompleteMode, topCompleteMode, bottomCompleteMode);
    }

    public void fillPixelHood(PixelHood<E> pixelHood, int bufferId, int leftCompleteMode, int rightCompleteMode, int topCompleteMode, int bottomCompleteMode) {

        // single pixel acceleration
        if( pixelHood.getHorizontalBorderSize() == 0 || pixelHood.getVerticalBorderSize() == 0 ) {
            pixelHood.setPixel(0, 0, getPixelValue(posX, posY, bufferId));
            return;
        }

        int[] signs = { -1, 1};
        for(int y=0; y<=pixelHood.getVerticalBorderSize(); ++y) {
            for(int x=0; x<=pixelHood.getHorizontalBorderSize(); ++x) {
                {
                    for(int mody: signs ) {

                        int sy = y * mody;

                        for(int modx: signs ) {
                            int sx = x * modx;

                            int globalX = posX + sx;
                            int globalY = posY + sy;
                            if (checkXYinRange(globalX, globalY) == true) {
                                pixelHood.setPixel(sx, sy, getPixelValue(globalX, globalY, bufferId));
                            } else {

//                                System.out.printf("Y X MODY MODX SY SX: %d %d %d %d %d %d\n", y, x, mody, modx,sy, sx);

                                // tylko gdy sx!=0 lub-rozłącznie sy!=0 to nastąpi

                                int completeModeTest = leftCompleteMode;
                                if(sy==0) {
                                    if(sx>0) {
                                        completeModeTest = rightCompleteMode;
                                    } else {
                                        completeModeTest = leftCompleteMode;
                                    }
                                } else {
                                    if(sx==0) {
                                        if(sy>0) {
                                            completeModeTest = bottomCompleteMode;
                                        } else {
                                            completeModeTest = topCompleteMode;
                                        }
                                    }
                                }

                                switch(completeModeTest) {
                                    case COMPLETE_MINUS:
                                        pixelHood.setPixel(sx, sy, getSkipValue());
                                    case COMPLETE_SKIP:
                                        break;
                                    case COMPLETE_MIN:
                                        pixelHood.setPixel(sx, sy, getPixelMinPossibleValue());
                                        break;
                                    case COMPLETE_MAX:
                                        pixelHood.setPixel(sx, sy, getPixelMaxPossibleValue());
                                        break;
                                    case COMPLETE_COPY:
                                    default:
                                        if(sy==0) {
                                            pixelHood.setPixel(sx, sy, pixelHood.getPixel(sx - 1 * modx, sy));
                                        } else {
                                            pixelHood.setPixel(sx, sy, pixelHood.getPixel(sx, sy - 1 * mody));
                                        }
                                }
                            }

                            if(sx==0) break;
                        }
                        if( sy == 0 ) break;
                    }
                }
            }
        }
    }

    private E getPixelValue() {
        return getPixelValue(posX, posY);
    }

    private E getPixelValue(int bufferId) {
        return getPixelValue(posX, posY, bufferId);
    }

    private E getPixelValue(int x, int y) {
        return getPixelValue(x, y, 0);
    }

    protected abstract E getPixelValue(int x, int y, int bufferId);

    public boolean checkXYinRange(int x, int y) {
        if( x>=0 && x<width && y>=0 && y<height ) {
            return true;
        } else {
            return false;
        }
    }

    protected abstract E getPixelMinPossibleValue();

    protected abstract E getPixelMaxPossibleValue();

    protected abstract E getSkipValue();



    public boolean itIsEdge(int hoodSize) {
        return itIsEdge(posX, posY, width, height, hoodSize);
    }

    public static boolean itIsEdge(int posX, int posY, int width, int height, int hoodSize) {
        if (posX < hoodSize || posY < hoodSize || posX >= width - hoodSize || posY >= height - hoodSize) {
            return true;
        }
        return false;
    }

}
