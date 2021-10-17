package solver.bruteforce;

import model.Point;
import model.PowerPoint;
import model.Station;
import solver.Solver;

import java.util.List;

/**
 * O(n) solution, where n is the number of stations.
 * Given m query points, the whole algorithm will run in O(n*m).
 */
public class BruteForceSolver implements Solver {
    List<? extends Station> stations;

    public BruteForceSolver(List<? extends Station> stations) {
        this.stations = stations;
    }

    public String solve(Point point) {
        PowerPoint bestStation = new PowerPoint();
        for (Station station : stations) {
            double power = getPower(getDistance(station, point), station.reach);
            if (power > bestStation.power) {
                bestStation.point = station;
                bestStation.power = power;
            }
        }
        return outputMessage(point, bestStation);
    }

    private double getDistance(Point A, Point B) {
        return Math.sqrt(Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2));
    }

    private double getPower(double distance, double reach) {
        return (distance > reach) ? 0 : Math.pow((reach - distance), 2);
    }

    private String outputMessage(Point point, PowerPoint bestStation) {
        if (bestStation.power > 0) {
            return  "Best link station for point " + point.x + "," + point.y + " is " +
                    bestStation.point.x + "," + bestStation.point.y + " with power " + bestStation.power;
        } else {
            return "No link station within reach for point " + point.x + "," + point.y;
        }
    }
}
