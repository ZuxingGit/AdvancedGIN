package gin;

import java.util.Random;

import gin.automation.run5JavaFiles;

/**
 * Simple local search.
 */
public class LocalSearch {

    private static final int seed = 5678;
    private static final int NUM_STEPS = 10;
    private static final int WARMUP_REPS = 10;

    protected SourceFile sourceFile;
    protected TestRunner testRunner;
    protected Random rng;

    /**
     * Main method. Take a source code filename, instantiate a search instance and execute the search.
     * @param args A single source code filename, .java
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Location: "+ run5JavaFiles.getParentDir());
            System.out.println("Please specify a source file to optimise.");
            
        } else {
            System.out.println("Location: "+ run5JavaFiles.getParentDir());
            
            String sourceFilename = args[0];
            System.out.println("Optimising source file: " + sourceFilename + "\n");

            LocalSearch localSearch = new LocalSearch(sourceFilename);
            localSearch.search();

        }

    }

    /**
     * Constructor: Create a sourceFile and a testRunner object based on the input filename.
     *              Initialise the RNG.
     * @param sourceFilename
     */
    public LocalSearch(String sourceFilename) {

        this.sourceFile = new SourceFile(sourceFilename);  // just parses the code and counts statements etc.
        this.testRunner = new TestRunner(this.sourceFile); // Utility class for running junits
        this.rng = new Random(); // use seed if we want same results each time

    }

    /**
     * Actual LocalSearch.
     * @return
     */
    private Patch search() {

        // start with the empty patch
        Patch bestPatch = new Patch(sourceFile);
        double bestTime = testRunner.test(bestPatch, WARMUP_REPS).executionTime;
        double origTime = bestTime;
        int bestStep = 0;

        System.out.println("Initial execution time: " + bestTime + " (ns) \n");

        for (int step = 1; step <= NUM_STEPS; step++) {

            System.out.print("Step " + step + " ");

            Patch neighbour = neighbour(bestPatch, rng);

            System.out.print(neighbour);

            TestRunner.TestResult testResult = testRunner.test(neighbour);

            if (!testResult.patchSuccess) {
                System.out.println("Patch invalid");
                continue;
            }

            if (!testResult.compiled) {
                System.out.println("Failed to compile");
                continue;
            }

            if (!testResult.junitResult.wasSuccessful()) {
                System.out.println("Failed to pass all tests");
                continue;
            }

            if (testResult.executionTime < bestTime) {
                bestPatch = neighbour;
                bestTime = testResult.executionTime;
                bestStep = step;
                System.out.println("*** New best *** Time: " + bestTime + "(ns)");
            } else {
                System.out.println("Time: " + testResult.executionTime);
            }

        }

        System.out.println("\nBest patch found: " + bestPatch);
        System.out.println("Found at step: " + bestStep);
        System.out.println("Best execution time: " + bestTime + " (ns) ");
        System.out.println("Speedup (%): " + (origTime - bestTime)/origTime);
        bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised");

        return bestPatch;

    }


    /**
     * Generate a neighbouring patch, by either deleting a randomly chosen edit, or adding a new random edit
     * @param patch Generate a neighbour of this patch.
     * @return A neighbouring patch.
     */
    public Patch neighbour(Patch patch, Random rng) {

        Patch neighbour = patch.clone();

        if (neighbour.size() > 0 && rng.nextFloat() > 0.5) {
            neighbour.remove(rng.nextInt(neighbour.size()));
        } else {
            neighbour.addRandomEdit(rng);
        }

        return neighbour;

    }


}
