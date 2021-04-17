import java.util.ArrayList;
import java.util.Collections;

import processing.core.PVector;

public class Face {
    protected ArrayList<Triangle> triangles;
    private Render parent;

    public Face(
        Render parent,
        AverageFace average,
        ArrayList<FaceParser> faces, 
        ArrayList<Float> weights
        ) {
        // combine the faces
        /**
         * For every triple in faces, 
         * make a new triangle where 
         * p1 is avgface.offsets[triple.first] + face.offsets[triple.first] * face.weight * weights[face]
         * easy
         * 
         * So if I'm just doing it for the average face
         * its just going to be p1 = avgface.offsets[triple.first]
         */
        if(faces.size() != weights.size()) {
            System.out.println("Size of faces and weights does not match!");
            System.exit(1);
        }

        triangles = new ArrayList<>();
        this.parent = parent;

        for(Triple indices : average.mesh) {
            PVector p1 = average.pointOffsets.get(indices.first).copy(); // starting point
            PVector p2 = average.pointOffsets.get(indices.second).copy(); // starting point
            PVector p3 = average.pointOffsets.get(indices.third).copy(); // starting point

            PVector c1 = average.colourOffsets.get(indices.first).copy();
            PVector c2 = average.colourOffsets.get(indices.second).copy();
            PVector c3 = average.colourOffsets.get(indices.third).copy();

            for(int i = 0; i < faces.size(); i++) {
                FaceParser face = faces.get(i);
                float faceWeight = weights.get(i);
                
                p1.add(getWeightedPointOffset(face, faceWeight, indices.first));
                p2.add(getWeightedPointOffset(face, faceWeight, indices.second));
                p3.add(getWeightedPointOffset(face, faceWeight, indices.third));

                c1.add(getWeightedColourOffset(face, faceWeight, indices.first));
                c2.add(getWeightedColourOffset(face, faceWeight, indices.second));
                c3.add(getWeightedColourOffset(face, faceWeight, indices.third));
            }

            Triangle triangle = new Triangle(p1, p2, p3, c1, c2, c3);
            triangles.add(triangle);
        }

        Collections.sort(triangles);    // make them in the order we'll draw them. 
    }

    public Face(Render parent, AverageFace average) {  // all we want to do is see the average
        triangles = new ArrayList<>();
        this.parent = parent;
        for(Triple indices : average.mesh) {
            PVector p1 = average.pointOffsets.get(indices.first).copy();
            PVector p2 = average.pointOffsets.get(indices.second).copy();
            PVector p3 = average.pointOffsets.get(indices.third).copy();

            PVector c1 = average.colourOffsets.get(indices.first).copy();
            PVector c2 = average.colourOffsets.get(indices.second).copy();
            PVector c3 = average.colourOffsets.get(indices.third).copy();

            Triangle triangle = new Triangle(p1, p2, p3, c1, c2, c3);
            triangles.add(triangle);
        }

        Collections.sort(triangles);    // make them in the order we'll draw them. 
    }

    PVector getWeightedPointOffset(FaceParser face, float faceWeight, int index) {
        PVector offset = new PVector();
        offset = face.pointOffsets.get(index).copy()
            .mult(face.pointOffsetWeight)
            .mult(faceWeight);
        
            return offset;
    }

    PVector getWeightedColourOffset(FaceParser face, float faceWeight, int index) {
        PVector offset = new PVector();
        offset = face.colourOffsets.get(index).copy()
            .mult(face.colourOffsetWeight)
            .mult(faceWeight);
        return offset;
    }



    public void draw() {
        for(int i = 0; i < triangles.size(); i++) {
            Triangle triangle = triangles.get(i);
            PVector colour = triangle.colour();
            // parent.noStroke();
            parent.stroke(  colour.x, colour.y, colour.z);
            parent.fill(    colour.x, colour.y, colour.z);
            // parent.noFill();
            parent.triangle(
                triangle.p1.x, triangle.p1.y,
                triangle.p2.x, triangle.p2.y,
                triangle.p3.x, triangle.p3.y
                );
        }
    }

    public void scale(float scaleFactor) {
        for(Triangle triangle : triangles) {
            triangle.scale(scaleFactor);
        }
    }

    public void translate(PVector translateVector) {
        for(Triangle triangle : triangles) {
            triangle.translate(translateVector);
        }
    }

    


}
