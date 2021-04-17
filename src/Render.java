import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;


public class Render extends PApplet {
    static String faceDataDir;
    FaceParser faceParserTest;
    AverageFace averageFace;
    Face testFace;

    int i;

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("First argument must be path to face data directory.");
            System.exit(1);
        }

        String[] appletArgs = new String[] {"Render"};
        faceDataDir = args[0];

        PApplet.main(appletArgs);
    }

    @Override
    public void settings() {
        size(1200, 800);
        
    }

    @Override
    public void setup() {
        noLoop();
        faceParserTest = new FaceParser();
        faceParserTest.loadFace(faceDataDir, "001");
        averageFace = new AverageFace();
        averageFace.loadFace(faceDataDir);
        ArrayList<Float> weights = new ArrayList<>();
        // ArrayList<FaceParser> faces;
        weights.add(1f);
        
        testFace = new Face(this, averageFace);


    }

    @Override
    public void draw() {
        background(255);
        
        testFace.scale(0.003f);
        testFace.translate(new PVector(width/2, height/2));
        testFace.draw();
    }

    @Override
    public void mouseClicked() {
        redraw();
    }

}