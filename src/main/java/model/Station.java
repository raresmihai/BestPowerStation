package model;

public class Station extends Point {
    public double reach;

    public Station(int x, int y, double reach) {
        super(x, y);
        this.reach = reach;
    }
}
