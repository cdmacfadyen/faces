import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;


public class Render extends PApplet {
    static String faceDataDir;
    FaceParser faceParserTest;
    AverageFace averageFace;
    Face testFace;

    Face testAverageFace;
    Face testSingleFace;

    int i;

    boolean drawingAverage;

    ArrayList<FaceParser> facesToInterpolate;
    ArrayList<Float> faceWeights;
    Face faceToDraw;

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
        drawingAverage = false;
        // basic
        averageFace = new AverageFace();
        averageFace.loadFace(faceDataDir);
        testAverageFace = new Face(this, averageFace);

        testAverageFace.scale(0.003f);
        testAverageFace.translate(new PVector(width/2, height/2));
        // one face
        averageFace = new AverageFace();
        averageFace.loadFace(faceDataDir);

        faceParserTest = new FaceParser();
        faceParserTest.loadFace(faceDataDir, "001");

        ArrayList<Float> weights = new ArrayList<>();
        ArrayList<FaceParser> faces = new ArrayList<>();

        faces.add(faceParserTest);
        weights.add(1f);

        testFace = new Face(this, averageFace, faces, weights);

        testFace.scale(0.003f);
        testFace.translate(new PVector(width/2, height/2));

        // three faces
        facesToInterpolate = new ArrayList<>();
        faceWeights = new ArrayList<>();

        FaceParser first = new FaceParser();
        first.loadFace(faceDataDir, "001");
        facesToInterpolate.add(first);

        FaceParser second = new FaceParser();
        second.loadFace(faceDataDir, "002");
        facesToInterpolate.add(second);

        FaceParser third = new FaceParser();
        third.loadFace(faceDataDir, "003");
        facesToInterpolate.add(third);

        // all evenly weighted for now
        faceWeights.add(0.333f);
        faceWeights.add(0.333f);
        faceWeights.add(0.333f);
    }

    @Override
    public void draw() {
        background(255);

        if (drawingAverage) {
            testAverageFace.draw();
            System.out.println("Average");
        } else {
            // testFace.draw();
            Face interpolated = new Face(this, averageFace, facesToInterpolate, faceWeights);
            interpolated.scale(0.003f);
            interpolated.translate(new PVector(width/2, height/2));
            interpolated.draw();
            System.out.println("Interpolated");
        }
        System.out.println("Rendered.");
    }

    @Override
    public void mouseClicked() {
        drawingAverage = !drawingAverage;
        redraw();
    }

}