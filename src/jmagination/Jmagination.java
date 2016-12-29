package jmagination;

/**
 * Created by darek on 25.10.16.
 */
public class Jmagination {

    Workspace workspace;
    ImageManager imageManager;


    public Jmagination() {

        imageManager = new ImageManager();
        workspace = new Workspace(imageManager);

    }

    public static void main(String[] args) {
        Jmagination j_obj = new Jmagination();
    }

}
