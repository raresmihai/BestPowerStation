package solver.binarysearch;

import model.Point;

public class ReachInterval extends Point {
    BoundaryType boundaryType;

    int size;

    public ReachInterval(int x, int y, BoundaryType boundaryType) {
        super(x, y);
        this.boundaryType = boundaryType;
        this.size = y - x;
    }
}
