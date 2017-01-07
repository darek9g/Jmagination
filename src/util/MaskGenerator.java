package util;

/**
 * Created by Rideau on 2017-01-07.
 */
public class MaskGenerator {
    public enum MaskType{
        AVERAGING(1),
        CROSS(2),
        PIRAMIDE(3)
        ;
        int type;
        MaskType(int i) {
            type = i;
        }
    }
    public static int[][] getMask(int size, MaskType type) {
        int[][] values = new int[size][size];
        switch (type) {
            case CROSS:
                fillCrossTable(size, values);
                break;
            case PIRAMIDE:
                fillPiramideTable(size, values);
                break;
            default:
                for(int i = 0; i < size; i++) for(int j = 0; j < size; j++) values[i][j] = 1;
                break;
        }

        return values;
    }

    public static int pow2(int b) {
        int a = 2;
        if(b==0) return 1;
        for(int i = 1; i < b; i++) a*=2;
        return a;
    }

    public static void fillPiramideTable(int size, int[][] table) {
        int pol = (size-1)/2;
        for(int i = 0; i <= pol; i++){
            for(int j = 0; j <= pol; j++)
                    table[i][j] = i<j ? i+1 : j+1;
        }
        rightMirror(size, table);
        downMirror(size, table);
    }

    public static void fillCrossTable(int size, int[][] table) {
        int pol = (size-1)/2;
        for(int i = 0; i <= pol; i++){
            for(int j = 0; j <= pol; j++)
                if(i+j < pol){
                    table[i][j] = 0;
                } else {
                    table[i][j] = pow2(i+j-pol);
                }
        }
        rightMirror(size, table);
        downMirror(size, table);
    }
    public static void rightMirror(int size, int[][] table) {
        int pol= (size-1)/2;
        for (int i = pol+1; i < size; i++) {
            for (int j = 0; j <= pol; j++){
                table[i][j] = table[size-1-i][j];
            }
        }
    }
    public static void downMirror(int size, int[][] table) {
        int pol= (size-1)/2;
        for (int i = 0; i < size; i++) {
            for (int j = pol+1; j < size; j++){
                table[i][j] = table[i][size-1-j];
            }
        }
    }
}
