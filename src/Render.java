import java.util.ArrayList;

import org.graalvm.compiler.nodes.NodeView.Default;

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

    static ReflectanceModel reflectanceModel;
    static ShadingModel shadingModel;

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

        if (args.length > 1) {
            for(int i = 0; i < args.length; i++) {
                // in case i implement more shading. 
                // if(args[i] == "--shading") {
                //     switch (args[i+1]) {
                //         case "flat": {
                //             shadingModel = ShadingModel.FLAT_SHADING;
                //         }
                //     }

                // }
                if (args[i].equals("--reflectance")) {
                    switch(args[i+1]) {
                        case "mesh": {
                            reflectanceModel = ReflectanceModel.MESH_ONLY;
                            break;
                        }
                        case "none": {
                            reflectanceModel = ReflectanceModel.NONE;
                            break;
                        }
                        case "lambert": {
                            reflectanceModel = ReflectanceModel.LAMBERT;
                            break;
                        }
                        default: {
                            System.out.println("Invalid option for --reflectance: " + 
                                args[i+1]);
                            System.out.println("\tMust be one of mesh | none | lambert");
                            System.exit(1);
                        }
                    }
                }
            }
        } else {
            // default
            reflectanceModel = ReflectanceModel.LAMBERT;
        }
        // only have the option of flat shading anyway. 
        shadingModel = ShadingModel.FLAT_SHADING;

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
        


        // drawingAverage = false;
        // // basic
        // averageFace = new AverageFace();
        // averageFace.loadFace(faceDataDir);
        // testAverageFace = new Face(this, averageFace);

        // testAverageFace.scale(0.003f);
        // testAverageFace.translate(new PVector(width/2, height/2));
        // // one face
        // averageFace = new AverageFace();
        // averageFace.loadFace(faceDataDir);

        // faceParserTest = new FaceParser();
        // faceParserTest.loadFace(faceDataDir, "001");

        // ArrayList<Float> weights = new ArrayList<>();
        // ArrayList<FaceParser> faces = new ArrayList<>();

        // faces.add(faceParserTest);
        // weights.add(1f);

        // testFace = new Face(this, averageFace, faces, weights);

        // testFace.scale(0.003f);
        // testFace.translate(new PVector(width/2, height/2));

        // three faces
        averageFace = new AverageFace();
        averageFace.loadFace(faceDataDir);
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

        // all evenly weighted by default
        faceWeights.add(0.333f);
        faceWeights.add(0.333f);
        faceWeights.add(0.333f);
    }

    @Override
    public void draw() {
        background(255);

        fill(0,127,0,127);
        stroke(0);
        
        // draw the weight selection triangle.
        triangle(
            weightTriangleP1.x, weightTriangleP1.y,
            weightTriangleP2.x, weightTriangleP2.y,
            weightTriangleP3.x, weightTriangleP3.y
        );

        
        // draw where the user clicked in the triangle.
        fill(255,0,0);
        circle(interpolationPoint.x, interpolationPoint.y, UNIT_SIZE * 5);

        if (drawingAverage) {
            testAverageFace.draw();
        } else {
            // draw the face.
            Face interpolated = new Face(this, averageFace, facesToInterpolate, faceWeights);
            interpolated.scale(0.003f);
            interpolated.translate(new PVector(width/2, height/2));
            interpolated.draw(
                reflectanceModel,
                shadingModel,
                lightIncidence,
                lightIntensity, 
                diffuseCoefficient
                );
        }
        System.out.println("Rendered.");
    }

    @Override
    public void mouseClicked() {
        // drawingAverage = !drawingAverage;
        if(pointInTriangle(new PVector(mouseX, mouseY))) {
            interpolationPoint = new PVector(mouseX, mouseY);
            /**
             * We can work out the area of the 
             * triangles if we replaced each point with 
             * the new point and then divide it 
             * by the total area of the original triangle. 
             * Easy.
             */

            float face1Area = areaOfTriangle(interpolationPoint, weightTriangleP2, weightTriangleP3);
            float face2Area = areaOfTriangle(interpolationPoint, weightTriangleP1, weightTriangleP3);
            float face3Area = areaOfTriangle(interpolationPoint, weightTriangleP1, weightTriangleP2);

            float totalArea = areaOfTriangle(weightTriangleP1, weightTriangleP2, weightTriangleP3);

            float face1Weight = face1Area / totalArea;
            float face2Weight = face2Area / totalArea;
            float face3Weight = face3Area / totalArea;

            System.out.println("Weight 1: " + face1Weight + "  Weight 2: " + face2Weight + "Weight 3: " + face3Weight);
            System.out.println("\tSum of weights:" + (face1Weight + face2Weight + face3Weight));

            faceWeights.set(0, face1Weight);
            faceWeights.set(1, face2Weight);
            faceWeights.set(2, face3Weight);

            redraw();
        }
    }
    /**
     * The area of a triangle is half of the 
     * magnitude of the cross product of 
     * the vectors going from a point to the other two. 
     */
    public float areaOfTriangle(PVector p1, PVector p2, PVector p3) {
        PVector v1 = p1.copy().sub(p2);
        PVector v2 = p1.copy().sub(p3);

        float area = v1.cross(v2).mag() * 0.5f; 
        return area;
    }

    // from https://blackpawn.com/texts/pointinpoly/
    /**
     * Checks if a given point is in the 
     * weight interpolation triangle.
     * 
     */
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

    // from https://blackpawn.com/texts/pointinpoly/
    /**
     * Utility function for the pointInTriangle function.
     */
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