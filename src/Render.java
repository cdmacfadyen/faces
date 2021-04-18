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
    float lightIntensity = 1.0f;
    PVector lightIncidence = new PVector(0,0,1); 
    float diffuseCoefficient = 1f;

    ReflectanceModel reflectanceModel;
    ShadingModel shadingModel;

    PVector weightTriangleP1;
    PVector weightTriangleP2;
    PVector weightTriangleP3;

    PVector interpolationPoint;

    float UNIT_SIZE;
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

        UNIT_SIZE = width / 1000f;

        float triangleWidth = 250 * UNIT_SIZE;
        // float triangleHeight = 250 * UNIT_SIZE;

        float bottomX = UNIT_SIZE * 50f;
        float bottomY = UNIT_SIZE * 50f;
        
        weightTriangleP1 = new PVector(bottomX,                        height - bottomY);
        weightTriangleP2 = new PVector(bottomX + triangleWidth,        height - bottomY);
        float p3x = weightTriangleP1.x + cos(-PI/3) * weightTriangleP1.dist(weightTriangleP2);
        float p3y = weightTriangleP1.y + sin(-PI/3) * weightTriangleP1.dist(weightTriangleP2);
        weightTriangleP3 = new PVector(p3x, p3y);
        // weightTriangleP3
        // weightTriangleP3 = new PVector(bottomX + triangleWidth / 2,    height - bottomY -  triangleHeight);

        interpolationPoint = (weightTriangleP1.copy().add(weightTriangleP2).add(weightTriangleP3)).mult(0.333f);
        
        shadingModel = ShadingModel.FLAT_SHADING;
        reflectanceModel = ReflectanceModel.LAMBERT;

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
        // faceWeights.add(10f); // for giggles
        faceWeights.add(0.333f);
        faceWeights.add(0.333f);
        faceWeights.add(0.333f);
    }

    @Override
    public void draw() {
        background(255);

        fill(0,127,0,127);
        stroke(0);
        

        triangle(
            weightTriangleP1.x, weightTriangleP1.y,
            weightTriangleP2.x, weightTriangleP2.y,
            weightTriangleP3.x, weightTriangleP3.y
            );

        fill(255,0,0);
        
        circle(interpolationPoint.x, interpolationPoint.y, UNIT_SIZE * 5);
        if (drawingAverage) {
            testAverageFace.draw();
            // System.out.println("Average");
        } else {
            // testFace.draw();
            Face interpolated = new Face(this, averageFace, facesToInterpolate, faceWeights);
            interpolated.scale(0.003f);
            interpolated.translate(new PVector(width/2, height/2));
            // interpolated.draw();
            interpolated.draw(
                reflectanceModel,
                shadingModel,
                lightIncidence,
                lightIntensity, 
                diffuseCoefficient
                );
            // System.out.println("Interpolated");
        }
        System.out.println("Rendered.");
    }

    @Override
    public void mouseClicked() {
        // drawingAverage = !drawingAverage;
        if(pointInTriangle(new PVector(mouseX, mouseY))) {
            interpolationPoint = new PVector(mouseX, mouseY);

            float distFace1 = interpolationPoint.dist(weightTriangleP1);
            float distFace2 = interpolationPoint.dist(weightTriangleP2);
            float distFace3 = interpolationPoint.dist(weightTriangleP3);

            // float maxDist = (weightTriangleP3.dist(weightTriangleP2) * sqrt(3) / 2f) ;
            // a^2 + b^2 = c^2
            // width ^ 2 + height ^2 = (width/2)^2
            // height = sqrt(width^2 - (width/2)^2)
            float edgeLength = weightTriangleP3.dist(weightTriangleP2);
            
            float maxDist = weightTriangleP3.dist(weightTriangleP2);
            // float maxDist = sqrt(pow(edgeLength,2) - pow(edgeLength / 2,2));

            float face1Weight = 1f - (distFace1 / maxDist);
            float face2Weight = 1f - (distFace2 / maxDist);
            float face3Weight = 1f - (distFace3 / maxDist);

            float sum = face1Weight + face2Weight + face3Weight;
            face1Weight /= sum;
            face2Weight /= sum;
            face3Weight /= sum;

            faceWeights.set(0, face1Weight);
            faceWeights.set(1, face2Weight);
            faceWeights.set(2, face3Weight);
            
            String weightMsg = "1: " + face1Weight + " 2: " + face2Weight + " 3: " + face3Weight;
            System.out.println(weightMsg);
            System.out.println(face1Weight + face2Weight + face3Weight);

            String dists = "1-2: " + weightTriangleP1.dist(weightTriangleP2) + " 1-3: " +
                weightTriangleP1.dist(weightTriangleP3) + " 2-3: " + weightTriangleP2.dist(weightTriangleP3);

            System.out.println(dists);
            
            redraw();
        }
    }

    // from https://blackpawn.com/texts/pointinpoly/
    public boolean pointInTriangle(PVector point) {
        if(
            sameSide(   point, weightTriangleP1, weightTriangleP2, weightTriangleP3)
            && sameSide(point, weightTriangleP2, weightTriangleP1, weightTriangleP3)
            && sameSide(point, weightTriangleP3, weightTriangleP1, weightTriangleP2)
        ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean sameSide(PVector p1,PVector p2,PVector a,PVector b) {
        PVector cp1 = b.copy().sub(a).cross(p1.copy().sub(a));
        PVector cp2 = b.copy().sub(a).cross(p2.copy().sub(a));

        if (cp1.dot(cp2) >= 0) {
            return true;
        } else {
            return false;
        }
    }



}