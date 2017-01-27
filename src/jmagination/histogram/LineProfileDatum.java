package jmagination.histogram;

/**
 * Created by darek on 26.01.2017.
 */
public class LineProfileDatum {
    int distance;
    float channelData[];
    String label;

    public LineProfileDatum(int distance, float channelData[], String label) {
        this.distance =  distance;
        this.channelData = channelData;
        this.label = label;
    }
}
