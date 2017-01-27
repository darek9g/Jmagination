package jmagination.histogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by darek on 26.01.2017.
 */
public class LineProfileData {
    int length = 0;
    int channels;
    String[] channelNames;
    float[] minChannelValue;
    float[] maxChannelValue;

    ArrayList<LineProfileDatum> data;

    public LineProfileData(int channels, String[] channelNames) {
        this.channels = channels;
        this.channelNames = channelNames;
        minChannelValue = new float[channels];
        for(int i=0;i<channels;i++) {
            minChannelValue[i] = Float.MAX_VALUE;
        }
        maxChannelValue = new float[channels];
        for(int i=0;i<channels;i++) {
            maxChannelValue[i] = Float.MIN_VALUE;
        }
        data = new ArrayList<>();
    }

    public void addDatum(int distance,  float channelData[], String label) {
        data.add(new LineProfileDatum(distance, channelData, label));
        for(int i=0;i<channels;i++) {
            if(channelData[i]<minChannelValue[i]) {
                minChannelValue[i] = channelData[i];
            }
            if(channelData[i]>maxChannelValue[i]) {
                maxChannelValue[i] = channelData[i];
            }
        }
        length++;
    }

    public void printData() {

        System.out.println("Metrics:");

        System.out.println("Probes: " + length);
        System.out.println("Channels: " + channels);
        System.out.println("Channel names: ");
        for(int i=0;i<channels;i++) {
            System.out.printf("[ %s ] ", channelNames[i]);
        }
        System.out.printf("\n");
        System.out.println("Min/max values: ");
        for(int i=0;i<channels;i++) {
            System.out.printf("[ %f/%f ] ", minChannelValue[i], maxChannelValue[i]);
        }
        System.out.printf("\n\n");

        System.out.println("Data: ");

        for(LineProfileDatum d: data) {
            if(channels==1) {
                System.out.printf("%16s | %8d | %f\n", d.label, d.distance, d.channelData[0]);
            } else {
                System.out.printf("%16s | %8d | %f,%f,%f,%f,%f,%f\n", d.label, d.distance,
                        d.channelData[0],
                        d.channelData[1],
                        d.channelData[2],
                        d.channelData[3],
                        d.channelData[4],
                        d.channelData[5]);
            }
        }
        System.out.printf("Final");
    }
}
