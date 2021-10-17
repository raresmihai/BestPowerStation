package main;

import model.Point;
import model.StationV2;
import solver.Solver;
import solver.binarysearch.BinarySearchSolver;
import solver.bruteforce.BruteForceSolver;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<StationV2> stations = new ArrayList<StationV2>();
        stations.add(new StationV2(0, 0, 10));
        stations.add(new StationV2(20, 20, 5));
        stations.add(new StationV2(10, 0, 12));

        List<Point> points = new ArrayList<Point>();
        points.add(new Point(0, 0));
        points.add(new Point(100, 100));
        points.add(new Point(15, 10));
        points.add(new Point(18, 18));

        Solver brute = new BruteForceSolver(stations);
        Solver binary = new BinarySearchSolver(stations);
        for (Point p : points) {
            System.out.println("[BruteForceSolver] " + brute.solve(p));
            System.out.println("[BinarySearchSolver] " + binary.solve(p));
        }
    }
}
