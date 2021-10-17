### How to run
#### Requirements
- Java 11+ installed on the machine

#### How to run
1. Download the jar locally
2. Run `java -jar BestPowerStation-1.0-SNAPSHOT.jar`

### Results

#### Results for the pre-defined test suite.
```
[BruteForceSolver] Best link station for point 0,0 is 0,0 with power 100.0
[BinarySearchSolver] Best link station for point 0,0 is 0,0 with power 100.0

[BruteForceSolver] No link station within reach for point 100,100
[BinarySearchSolver] No link station within reach for point 100,100

[BruteForceSolver] Best link station for point 15,10 is 10,0 with power 0.6718427000252355
[BinarySearchSolver] Best link station for point 15,10 is 10,0 with power 0.6718427000252355

[BruteForceSolver] Best link station for point 18,18 is 20,20 with power 4.715728752538098
[BinarySearchSolver] Best link station for point 18,18 is 20,20 with power 4.715728752538098
```

#### Performance comparison between the 2 algorithms
Based on `SolverComparisonTest.compareSolversExecutionTimes`
```$xslt
Solving for 1,000,000 stations and 10,000 points queries using the BruteForceSolver...
BruteForceResolver took 217 seconds.
Solving for 1,000,000 stations and 10,000 points queries using the BinarySearchSolver...
BinarySearchSolver took 78 seconds.
```