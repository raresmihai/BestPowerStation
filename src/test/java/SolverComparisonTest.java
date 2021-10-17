import model.Point;
import model.StationV2;
import org.junit.jupiter.api.Test;
import solver.Solver;
import solver.binarysearch.BinarySearchSolver;
import solver.bruteforce.BruteForceSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolverComparisonTest {
    int min = -10000, max = 10000;

    @Test
    void testIfBothSolversReturnTheSame() {
        List<StationV2> stations = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            stations.add(new StationV2(random(), random(), random()));
        }

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            points.add(new Point(random(), random()));
        }

        Solver brute = new BruteForceSolver(stations);
        Solver binary = new BinarySearchSolver(stations);
        for (Point p : points) {
            assertEquals(brute.solve(p), binary.solve(p));
        }
    }

    @Test
    void compareSolversExecutionTimes() {
        List<StationV2> stations = new ArrayList<>();
        for (int i = 0; i < 1_000_000; i++) {
            stations.add(new StationV2(random(), random(), random()));
        }

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) {
            points.add(new Point(random(), random()));
        }

        Solver brute = new BruteForceSolver(stations);
        Solver binary = new BinarySearchSolver(stations);

        System.out.println("Solving for 1,000,000 stations and 10,000 points queries using the BruteForceSolver...");
        long startTime = System.nanoTime();
        for (Point p : points) {
            brute.solve(p);
        }
        long endTime   = System.nanoTime();
        long totalTimeInSeconds = (endTime - startTime)/1000/1000/1000;
        System.out.println("BruteForceResolver took " + totalTimeInSeconds + " seconds.");

        System.out.println("Solving for 1,000,000 stations and 10,000 points queries using the BinarySearchSolver...");
        startTime = System.nanoTime();
        for (Point p : points) {
            binary.solve(p);
        }
        endTime   = System.nanoTime();
        totalTimeInSeconds = (endTime - startTime)/1000/1000/1000;
        System.out.println("BinarySearchSolver took " + totalTimeInSeconds + " seconds.");
    }

    int random() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
