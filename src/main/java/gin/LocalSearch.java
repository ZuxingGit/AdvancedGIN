package gin;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import gin.edit.Edit;

/**
 * Simple local search.
 */
public class LocalSearch {

    private static final int seed = 5678;
    private static final int NUM_STEPS = 1000;
    private static final int WARMUP_REPS = 10;

    protected SourceFile sourceFile;
    protected TestRunner testRunner;
    protected Random rng;

    private static boolean saveEachBest = false;
    private static String fileName = "";
    /**
     * Main method. Take a source code filename, instantiate a search instance and execute the search.
     * @param args A single source code filename, .java
     */
    public static void main(String[] args) {
        // System.out.println(args.length);
        if (args.length == 0) {
            System.out.println("Please specify a source file to optimise.");
        } else if (args.length == 1) {
            String sourceFilename = args[0];
            System.out.println("Optimising source file: " + sourceFilename + "\n");

            LocalSearch localSearch = new LocalSearch(sourceFilename);
            localSearch.search();
        } else if (args.length == 3) {
            String sourceFilename = args[0];
            String programName = args[1];
            String folderPath = args[2];
            // System.out.println("programName: " + programName + ", folderPath: " + folderPath + "\n");
            saveEachBest = true;
            fileName = folderPath + programName;
            // check folder, if exists, delete all files in the folder
            File folder = new File(folderPath);
            if (folder.exists()) {
                File[] files = folder.listFiles();
                for (File file : files) {
                    file.delete();
                }
            }
        
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

        HashMap<Patch, String> bestPatchHistory = new HashMap<>();
        for (int step = 1; step <= NUM_STEPS; step++) {

            Patch neighbour = neighbour(bestPatch, rng);
            // if patch is: | , useless, re-generate a new patch
            if (!containEnoughVB(neighbour.toString(), 1)) {
                step--;
                continue;
            }

            System.out.print("Step " + step + " ");

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
                // save every optimised code of bestPatch to a file (xxxx.optimised_step)
                if (saveEachBest) {
                    bestPatch.writePatchedSourceToFile(fileName + ".optimised_" + step);
                }
                // save every best patch to a HashMap
                bestPatchHistory.put(bestPatch, bestPatch.apply().getSource());
            } else {
                System.out.println("Time: " + testResult.executionTime);
            }

        }

        System.out.println("\nBest patch found: " + bestPatch);
        // 1. Minimise the patch by removing one edit at a time 
        // and check if the result is the same as bestPatchCode
        bestPatch = minimisePatch(bestPatch, bestPatch.apply().getSource());
        // 2. iterate the bestPatchHistory to find a shorter patch that can generate the same result
        for (Entry<Patch, String> entry : bestPatchHistory.entrySet()) {
            Patch patch = entry.getKey();
            String patchCode = entry.getValue();
            if (patch.size() < bestPatch.size() && patchCode.equals(bestPatch.apply().getSource())) {
                bestPatch = patch;
            }
        }
        if (saveEachBest) {
            bestPatch.writePatchedSourceToFile(fileName + ".optimised_minimised");
        }
        System.out.println("Minimised best patch: " + bestPatch);
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

    /**
     * Check if the patch contains enough Verticla Bar '|' e.g. 'VB
     * @param patch content of the patch
     * @param N threshold number of VB
     * @return true if the patch contains enough VB
     * @author Zuxing
     */
    public boolean containEnoughVB (String patch, int N) {
        int count = 0;
        for (int i = 0; i < patch.length(); i++) {
            if (patch.charAt(i) == '|') {
                count++;
                if (count > N) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Minimise the patch by removing one edit at a time and check if the result is the same as bestPatchCode
     * If the result is the same, recursively minimise the new patch
     * @param bestPatch
     * @param bestPatchCode
     * @return the minimised patch
     * @author Zuxing
     */
    public Patch minimisePatch(Patch bestPatch, String bestPatchCode) {
        LinkedList<Edit> edits = bestPatch.getEdits();
        int size = edits.size();
        if (size == 1) { // only one edit, cannot minimize more
            return bestPatch;
        }
        Patch newBestPatch = bestPatch.clone();
        // delete one edit and check if result is the same as bestPatchCode
        for (int i = 0; i < bestPatch.size(); i++) {
            Patch newPatch = bestPatch.clone();
            newPatch.remove(i);
            String newPatchCode = newPatch.apply().getSource();
            if (newPatchCode.equals(bestPatchCode)) {
                // minimise the newPatch recursively
                newPatch = minimisePatch(newPatch, bestPatchCode);
                if (newPatch.size() < newBestPatch.size()) {
                    newBestPatch = newPatch;
                }
            }
        }
        return newBestPatch;
    }
}
