import processing.core.PVector;

/**
 * Represents a triangle in a mesh. Holds the 
 * coordinates and the colours for each point. 
 */
public class Triangle implements Comparable<Triangle> {
    // points
    protected PVector p1;
    protected PVector p2;
    protected PVector p3;

    // colours
    protected PVector c1;
    protected PVector c2;
    protected PVector c3;


    PVector viewpoint;

    public Triangle(PVector p1, PVector p2, PVector p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        p1.y = -p1.y;
        p2.y = -p2.y;
        p3.y = -p3.y;

        viewpoint = new PVector(0,0,0); // default viewpoint
    }

    public Triangle(
        PVector p1,
        PVector p2, 
        PVector p3,
        PVector c1,
        PVector c2,
        PVector c3
        ) {
            this(p1, p2, p3);
            this.c1 = c1;
            this.c2 = c2;
            this.c3 = c3;
        }

    /**
     * Used for deciding which point is furthest back. 
     * What we really want is the distance 
     * from the viewing point to the centre, but
     * this will do for a static viewpoint.
     */
    public PVector centre() {
        PVector centre = (p1.copy().add(p2).add(p3)).mult(0.333f);
        return centre;
    }

    /** 
     * Checks which triangle is furthest back 
     * by comparing the z coordinate of their centres. 
     * Again, what we really want is the distance
     * from the viewing point to the centre, but
     * this will do for a static viewpoint.
     */
    @Override
    public int compareTo(Triangle o) {
        if(viewpoint.equals(new PVector(0,0,0))) {
            return Float.compare(this.centre().z, o.centre().z);
        } else {
            // if I want to actually set the viewpoint at some point.
            return 0;
        }
    }

    /**
     * Find the average colour of the three points in 
     * the triangle.  
     */
    public PVector colour() {
        PVector colour = (c1.copy().add(c2).add(c3)).mult(0.333f);
        return colour;
    }

    /**
     * Scale the triangle in all directions by a given 
     * factor. 
     */
    public void scale(float scaleFactor) {
        p1.mult(scaleFactor);
        p2.mult(scaleFactor);
        p3.mult(scaleFactor);
    }

    /**
     * Move the triangle by 
     * a vector. 
     */
    public void translate(PVector translateVector) {
        p1.add(translateVector);
        p2.add(translateVector);
        p3.add(translateVector);
    }

    /**
     * Find the normal to the triangle. 
     */
    public PVector normal() {
        PVector tangent1 = p2.copy().sub(p1);
        PVector tangent2 = p3.copy().sub(p1);

        PVector normal = tangent1.cross(tangent2);
        normal.normalize(); // unit normal
        
        return normal;
    }

    /**
     * Lambert shading calculation. 
     */
    public PVector getLambertColour(PVector incidence, float lightIntensity, float diffuseCoefficient) {
        // find normal
        // find normal.incidence
        // return result * intensity * diffusecoeff

        PVector normal = this.normal(); // this. unnecessary but for clarity

        float resultIntensity = normal.dot(incidence);
        resultIntensity *= lightIntensity;
        resultIntensity *= diffuseCoefficient;

        return this.colour().mult(resultIntensity);
    }
}
