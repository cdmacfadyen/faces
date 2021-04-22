import java.util.ArrayList;
import java.util.Collections;

import processing.core.PVector;

/**
 * Class for holding the face to be 
 * rendered. 
 */

public class Face {
    protected ArrayList<Triangle> triangles;
    private Render parent;

    /**
     * Construction combines
     * the average face, any number of 
     * other faces and their respective weights. 
     */
    public Face(
        Render parent,
        AverageFace average,
        ArrayList<FaceParser> faces, 
        ArrayList<Float> weights
        ) {
        if(faces.size() != weights.size()) {
            System.out.println("Size of faces and weights does not match!");
            System.exit(1);
        }

        triangles = new ArrayList<>();
        this.parent = parent;

        /**
         * For every triangle in the mesh, get the 
         * points in the triangle, find the point and colour 
         * offsets and combine them with the average face 
         * to create the actual face. 
         */
        for(Triple indices : average.mesh) {
            PVector p1 = average.pointOffsets.get(indices.first).copy();    // starting point
            PVector p2 = average.pointOffsets.get(indices.second).copy();   // starting point
            PVector p3 = average.pointOffsets.get(indices.third).copy();    // starting point

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

        Collections.sort(triangles);    // make them in the order we'll draw them for the painter's algorithm. 
    }

    // Constructor when we only want to draw the average face. 
    public Face(Render parent, AverageFace average) { 
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

    /**
     * Helper function for calculating the position of a 
     * point given its weight. 
     */
    PVector getWeightedPointOffset(FaceParser face, float faceWeight, int index) {
        PVector offset = new PVector();
        offset = face.pointOffsets.get(index).copy()
            .mult(face.pointOffsetWeight)
            .mult(faceWeight);
        
            return offset;
    }

    /**
     * Helper function for calculating the colour of a 
     * point given its weight. 
     */
    PVector getWeightedColourOffset(FaceParser face, float faceWeight, int index) {
        PVector offset = new PVector();
        offset = face.colourOffsets.get(index).copy()
            .mult(face.colourOffsetWeight)
            .mult(faceWeight);
        return offset;
    }

    /**
     * General drawing function, 
     * takes information about what shading and reflectance 
     * model to use and then gets the colour Draw every triangle, with no them from 
     * back to front, as in the painter's algorithms. 
     */
    public void draw(
        ReflectanceModel reflectanceModel,
        ShadingModel shadingModel,
        PVector incidence,
        float lightIntensity,
        float diffuseCoefficient
        ) {
        if(reflectanceModel == ReflectanceModel.MESH_ONLY) {
            for(int i = 0; i < triangles.size(); i++) {
                Triangle triangle = triangles.get(i);
                parent.stroke(0);
                parent.noFill();
                parent.triangle(
                    triangle.p1.x, triangle.p1.y,
                    triangle.p2.x, triangle.p2.y,
                    triangle.p3.x, triangle.p3.y
                    );
            }
        } else if (reflectanceModel == ReflectanceModel.NONE) {
            // flat shading
            for(int i = 0; i < triangles.size(); i++) {
                Triangle triangle = triangles.get(i);
                PVector colour = triangle.colour();
                parent.stroke(  colour.x, colour.y, colour.z);
                parent.fill(    colour.x, colour.y, colour.z);
                parent.triangle(
                    triangle.p1.x, triangle.p1.y,
                    triangle.p2.x, triangle.p2.y,
                    triangle.p3.x, triangle.p3.y
                    );
            }
        } else if (reflectanceModel == ReflectanceModel.LAMBERT) {
            // flat shading
            for(int i = 0; i < triangles.size(); i++) {
                Triangle triangle = triangles.get(i);
                PVector lambertColour = triangle.getLambertColour(incidence, lightIntensity, diffuseCoefficient);
                parent.stroke(  lambertColour.x, lambertColour.y, lambertColour.z);
                parent.fill(    lambertColour.x, lambertColour.y, lambertColour.z);
                parent.triangle(
                    triangle.p1.x, triangle.p1.y,
                    triangle.p2.x, triangle.p2.y,
                    triangle.p3.x, triangle.p3.y
                    );
            }
        }

    }


    /**
     * Basic drawing function, not used
     * in final submission.   
     */
    public void draw() {
        for(int i = 0; i < triangles.size(); i++) {
            Triangle triangle = triangles.get(i);
            PVector colour = triangle.colour();
            parent.stroke(  colour.x, colour.y, colour.z);
            parent.fill(    colour.x, colour.y, colour.z);
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
