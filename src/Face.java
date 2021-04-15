import java.util.ArrayList;

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
    }

    public Face(Render parent, AverageFace average) {  // all we want to do is see the average
        triangles = new ArrayList<>();
        this.parent = parent;
        for(Triple indices : average.mesh) {
            PVector p1 = average.pointOffsets.get(indices.first).copy();
            PVector p2 = average.pointOffsets.get(indices.second).copy();
            PVector p3 = average.pointOffsets.get(indices.third).copy();

            Triangle triangle = new Triangle(p1, p2, p3);
            triangles.add(triangle);
        }
    }

    public void draw() {
        for(Triangle triangle : triangles) {
            parent.stroke(0);
            parent.noFill();
            parent.triangle(
                triangle.p1.x, triangle.p1.y,
                triangle.p2.x, triangle.p2.y,
                triangle.p3.x, triangle.p3.y);
        }
    }


}
