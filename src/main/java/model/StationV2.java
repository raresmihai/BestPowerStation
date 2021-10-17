package model;

/**
 * Contains the minimum and maximum coordinates within the reach.
 */
public class StationV2 extends Station {
    public int xMin;
    public int xMax;
    public int yMin;
    public int yMax;

    public StationV2(int x, int y, int reach) {
        super(x, y, reach);
        this.xMin = x - reach;
        this.xMax = x + reach;
        this.yMin = y - reach;
        this.yMax = y + reach;
    }
}
