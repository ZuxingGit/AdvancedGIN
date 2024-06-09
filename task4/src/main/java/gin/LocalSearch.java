package gin;

import gin.edit.Edit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Simple local search.
 */
public class LocalSearch {

    private static final int seed = 5678;
    private static final int NUM_STEPS = 500;
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

            System.out.println("Please specify a source file to optimise.");

        } else {

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
            System.err.print("Step " + step + " ");

            Patch neighbour = neighbour(bestPatch, rng);

//            System.out.print(neighbour);

            TestRunner.TestResult testResult = testRunner.test(neighbour);

            if (!testResult.patchSuccess) {
                System.out.println("Patch invalid");
                System.err.println("Patch invalid");
                continue;
            }

            if (!testResult.compiled) {
                System.out.println("Failed to compile");
                System.err.println("Failed to compile");
                continue;
            }

            if (!testResult.junitResult.wasSuccessful()) {
                System.out.println("Failed to pass all tests");
                System.err.println("Failed to pass all tests");
                continue;
            }

            if (testResult.executionTime < bestTime) {
                bestPatch = neighbour;
                bestTime = testResult.executionTime;
                bestStep = step;
                bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised" + bestStep);
                System.out.println("*** New best *** Time: " + bestTime + "(ns)");
                System.err.println("*** New best *** Time: " + bestTime + "(ns)");
            } else {
                System.out.println("Time: " + testResult.executionTime);
                System.err.println("Time: " + testResult.executionTime);
            }

        }

        System.out.println("\nBest patch found: " + bestPatch);
        System.err.println("\nBest patch found: " + bestPatch);
        System.out.println("Found at step: " + bestStep);
        System.err.println("Found at step: " + bestStep);
        System.out.println("Best execution time: " + bestTime + " (ns) ");
        System.err.println("Best execution time: " + bestTime + " (ns) ");
        System.out.println("Speedup (%): " + (origTime - bestTime)/origTime);
        System.err.println("Speedup (%): " + (origTime - bestTime)/origTime);
        bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised");

        Patch best = optimise(bestPatch);
        System.err.println(best.toString());
        System.err.println(bestPatch);

        return best;
    }

    private Patch optimise(Patch foundPatch) {

        Patch tempPatch = foundPatch.clone();
        LinkedList<Edit> pEdits = tempPatch.getEdits();

        while (!tempPatch.edits.isEmpty()) {
            tempPatch.remove(0);
        }

        TestRunner.TestResult tResult = testRunner.test(tempPatch);

        if (!tResult.patchSuccess && !tResult.compiled && !tResult.junitResult.wasSuccessful()) {
            if (tempPatch.getPatchedSource().equals(foundPatch.getPatchedSource())) {
                System.err.println("hi");
                return tempPatch;
            }
        }

        Patch p = tempPatch.clone();

        // Search from the start
        for (int i = 0; i < pEdits.size(); i++) {
            p.add(pEdits.get(i));

            TestRunner.TestResult pResult = testRunner.test(p);

            if (!pResult.patchSuccess && !pResult.compiled && !pResult.junitResult.wasSuccessful()) {
                if (p.getPatchedSource().equals(foundPatch.getPatchedSource())) {
                    break;
                }
            }
        }


        ArrayList<Patch> children = new ArrayList<Patch>();
        children.add(p);
        System.err.println(children.size());
        Patch lowest = p;
        int end = 0;
        // Search backwards
        while (end == 0 && !children.isEmpty()) {
            Patch child = children.get(0).clone();
            LinkedList<Edit> cEdits = child.getEdits();
            children.remove(0);

            for (int i = cEdits.size() - 1; i > 0; i--) {
                Patch k = p.clone();
                for (int j = i - 1; i > 0; i--) {
                    k.remove(j);
                    TestRunner.TestResult kResult = testRunner.test(k);
                    if (!kResult.patchSuccess && !kResult.compiled && !kResult.junitResult.wasSuccessful()) {
                        if (k.getPatchedSource().equals(foundPatch.getPatchedSource())) {
                            children.add(k);
                        }
                    }
                }
            }
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < children.size(); i++) {
                if (min > children.get(i).getEdits().size()) {
                    min = children.get(i).getEdits().size();
                }
            }
            int counter = 0;
            end = 1;
            System.err.println(end);
            while (counter < children.size()) {
                if (children.get(counter).getEdits().size() > min) {
                    end = 0;
                    children.remove(counter);
                } else {
                    lowest = children.get(counter);
                    counter++;
                }
            }

        }
        return lowest;
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
