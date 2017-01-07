package util.test;

import util.MaskGenerator;

/**
 * Created by Rideau on 2017-01-07.
 */
public class MaskGeneratorTest {
    public static void  main(String[] args){
        int[][] piramide = MaskGenerator.getMask(9, MaskGenerator.MaskType.PIRAMIDE);
        int[][] cross = MaskGenerator.getMask(9, MaskGenerator.MaskType.CROSS);
        int[][] usrednienie = MaskGenerator.getMask(9, MaskGenerator.MaskType.AVERAGING);
        return ;
    }
}
