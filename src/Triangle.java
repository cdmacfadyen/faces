import processing.core.PVector;

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

        viewpoint = new PVector(0,0,0);
    
    
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

    // public float back() {
    //     float back = p1.z;
        
    //     if(p2.z < back) {
    //         back = p2.z;
    //     }
        
    //     if(p3.z < back) {
    //         back = p3.z;
    //     }

    //     return back;
    // }

    /**
     * What we really want is the distance 
     * from the viewing point to the centre, but
     * this will do for orthographic projection.
     */
    public PVector centre() {
        // PVector centre = new PVector();

        // centre.x = (1/3) * (p1.x + p2.x + p3.x);
        // centre.y = (1/3) * (p1.y + p2.y + p3.y);
        // centre.z = (1/3) * (p1.z + p2.z + p3.z);
        PVector centre = (p1.copy().add(p2).add(p3)).mult(0.333f);
        return centre;
    }

    @Override
    public int compareTo(Triangle o) {
        if(viewpoint.equals(new PVector(0,0,0))) {
            return Float.compare(this.centre().z, o.centre().z);
        } else {
            // if I want to actually set the viewpoint at some point.
            return 0;
        }
    }

    public PVector colour() {
        PVector colour = (c1.copy().add(c2).add(c3)).mult(0.333f);
        return colour;
    }

    public void scale(float scaleFactor) {
        p1.mult(scaleFactor);
        p2.mult(scaleFactor);
        p3.mult(scaleFactor);
    }

    public void translate(PVector translateVector) {
        p1.add(translateVector);
        p2.add(translateVector);
        p3.add(translateVector);
    }
    
}
