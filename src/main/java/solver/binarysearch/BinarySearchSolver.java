package solver.binarysearch;

import model.Point;
import model.Station;
import model.StationV2;
import solver.Solver;
import solver.bruteforce.BruteForceSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Pre-compute 4 arrays, all sorted by the different boundary type given a point and a reach (xMin, xMax, yMin, yMax).
 * Using binary search, find the maxBoundary (when searching for xMin and yMin) and minBoundary (when searching for xMax and yMax)
 * that represents the interval of valid Stations given a boundary type and a Point.
 * Example.
 *      Given stations (0, 0, 10) , (20, 20, 5), (10, 0, 12), the array of StationV2 would be
 *          [(0, 0, 10, xMin = -10, xMax = 10, yMin = -10, yMax = 10),
 *          (20, 20, 5, xMin = 15, xMax = 25, yMin = 15, yMax = 25),
 *          (10, 0, 12, xMin = -2, xMax = 22, yMin =-12, yMax = 10)]
 *       Based on this, the 4 sorted arrays would be:
 *          stationsSortedByMinX = [-10, -2, 15],
 *          stationsSortedByMaxX = [10, 22, 25],
 *          stationsSortedByMinY = [-12, -10, 15],
 *          stationsSortedByMaxY = [10, 10, 25],
 *       Basically, stationsSortedByMinX would reduce to [-10, -2, 15] in the binary search, as we're only interested in the value.
 *
 *       Given a Point P(5, -11), only stations that have xMin at most 5 are within reach (-10, -2).
 *       (20, 20, 5) is in reach only for points starting at x = 15.
 *       Hence only points having xMin [-10, -2] would be valid candidates for a bestPower (if xMax, yMin and yMax are also within reach).
 *       To get [-10, -2] from [-10, -2, 15], or simply put the ReachInterval [0, 2), the brute force solution would be O(n).
 *       The optimised solution is O(logN) if the array is sorted (which it is due to pre-computation) and we use binary search.
 *       After the first step, the problem is reduced to (0, 0, 10), (10, 0, 12).
 *
 *       Given the same Point P(5, -11), all stations are withing reach as 5 < 10, 22, 25. This step is O(1) as it's enough to compare with the first element (5 < 10).
 *       Hence, minReachInterval will remain the one from the first step, as we have less candidate stations.
 *
 *       Given the same Point P(5, -11), only stations that have yMin at most -11 are within reach.
 *       ((10, 0, 12, xMin = -2, xMax = 22, yMin =-12, yMax = 10)] is the only station with yMin -12 < -11. The other 2 start after on the y coordinate.
 *       Similar, the reachInterval of [0, 1) is found in O(logN) using binary search.
 *       Hence, the problem is now reduced to [0,1).
 *
 *       Filtering by yMax, all stations are within reach as -11 < [10, 10, 25].
 *
 *       After all 4 steps, we are left with running a brute force solution on (10, 0, 12) only.
 *
 *       In the worst case, time complexity is still O(n), where n is the number of stations.
 *       This happens if all stations are within reach from current point, hence we have to run the brute force solution for all of them to find the maximum power.
 *       In a real life situation, given a large number of random distributed stations and query points, this solution should perform at least 2-3x better than the direct brute force.
 *       See @SolverComparisonTest for comparison and the results in README.
 */
public class BinarySearchSolver implements Solver {
    List<StationV2> stationsSortedByMinX;
    List<StationV2> stationsSortedByMaxX;
    List<StationV2> stationsSortedByMinY;
    List<StationV2> stationsSortedByMaxY;


    public BinarySearchSolver(List<StationV2> stations) {
        this.stationsSortedByMinX = new ArrayList<>(stations);
        Collections.sort(stationsSortedByMinX, Comparator.comparingInt(o -> o.xMin));

        this.stationsSortedByMaxX = new ArrayList<>(stations);
        Collections.sort(stationsSortedByMaxX, Comparator.comparingInt(o -> o.xMax));

        this.stationsSortedByMinY = new ArrayList<>(stations);
        Collections.sort(stationsSortedByMinY, Comparator.comparingInt(o -> o.yMin));

        this.stationsSortedByMaxY = new ArrayList<>(stations);
        Collections.sort(stationsSortedByMaxY, Comparator.comparingInt(o -> o.yMax));
    }

    public String solve(Point point) {
        ReachInterval minReachInterval = getStationsIntervalInReach(stationsSortedByMinX, point, BoundaryType.xMin);
        if (minReachInterval == null) return outputNoStationMessage(point);

        ReachInterval maxX = getStationsIntervalInReach(stationsSortedByMaxX, point, BoundaryType.xMax);
        if (maxX == null) return outputNoStationMessage(point);
        if (maxX.size < minReachInterval.size) minReachInterval = maxX;

        ReachInterval minY = getStationsIntervalInReach(stationsSortedByMinY, point, BoundaryType.yMin);
        if (minY == null) return outputNoStationMessage(point);
        if (minY.size < minReachInterval.size) minReachInterval = minY;

        ReachInterval maxY = getStationsIntervalInReach(stationsSortedByMaxY, point, BoundaryType.yMax);
        if (maxY == null) return outputNoStationMessage(point);
        if (maxY.size < minReachInterval.size) minReachInterval = maxY;

        List<? extends Station> smallestStationsSubset = getStationsSubset(minReachInterval);
        BruteForceSolver bruteForceSolver = new BruteForceSolver(smallestStationsSubset);
        return bruteForceSolver.solve(point);
    }

    // Use binary search to find the interval of stations within reach for current point, given a Boundary Type
    // The algorithm is the same for all 4 boundary types, only the direction and the condition is different,
    // hence use custom methods that switch on the boundary type to avoid code duplication.
    private ReachInterval getStationsIntervalInReach(List<StationV2> stations, Point point, BoundaryType boundaryType) {
        int pointValue = getPointValue(point, boundaryType);

        // case for when the whole list of stations is in reach for current boundary type
        if (pointValue >= getBoundaryValue(stations.get(stations.size() - 1), boundaryType) &&
                (boundaryType == BoundaryType.xMin || boundaryType == BoundaryType.yMin))
                    return new ReachInterval(0, stations.size(), boundaryType);
        if (pointValue <= getBoundaryValue(stations.get(0), boundaryType) &&
                (boundaryType == BoundaryType.xMax || boundaryType == BoundaryType.yMax))
            return new ReachInterval(0, stations.size(), boundaryType);

        int left = 0;
        int right = stations.size() - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int boundaryValueMid = getBoundaryValue(stations.get(mid), boundaryType);
            int boundaryValueMid_1 = Integer.MIN_VALUE;
            if (mid > 0) boundaryValueMid_1 = getBoundaryValue(stations.get(mid - 1), boundaryType);
            if (boundaryValueMid > pointValue && boundaryValueMid_1 <= pointValue) {
                if (boundaryType == BoundaryType.xMin || boundaryType == BoundaryType.yMin) {
                    // case for when there are duplicate points with the same boundary
                    while (mid < right - 1 && boundaryValueMid == getBoundaryValue(stations.get(mid + 1), boundaryType)) mid++;
                    return new ReachInterval(0, mid, boundaryType);
                } else {
                    while (mid > left + 1 && boundaryValueMid == getBoundaryValue(stations.get(mid - 1), boundaryType)) mid--;
                    return new ReachInterval(mid, stations.size(), boundaryType);
                }
            }
            if (boundaryValueMid > pointValue) right = mid - 1; // go left
            else left = mid + 1; // go right
        }
        return null;
    }

    int getBoundaryValue(StationV2 station, BoundaryType boundaryType) {
        switch (boundaryType) {
            case xMin: return station.xMin;
            case xMax: return station.xMax;
            case yMin: return station.yMin;
            case yMax: return station.yMax;
        }
        return -1;
    }

    int getPointValue(Point p, BoundaryType boundaryType) {
        switch (boundaryType) {
            case xMin:
            case xMax:
                return p.x;
            case yMin:
            case yMax:
                return p.y;
        }
        return -1;
    }

    List<StationV2> getStationsSubset(ReachInterval reachInterval) {
        switch (reachInterval.boundaryType) {
            case xMin: return stationsSortedByMinX.subList(reachInterval.x, reachInterval.y);
            case xMax: return stationsSortedByMaxX.subList(reachInterval.x, reachInterval.y);
            case yMin: return stationsSortedByMinY.subList(reachInterval.x, reachInterval.y);
            case yMax: return stationsSortedByMaxY.subList(reachInterval.x, reachInterval.y);
        };
        return null;
    }

    String outputNoStationMessage(Point point) {
        return "No link station within reach for point " + point.x + "," + point.y;
    }
}
