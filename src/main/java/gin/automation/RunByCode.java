package gin.automation;

import gin.LocalSearch;

/**
 * Run the LocalSearch.main() method with the specified program,
 * instead of using command line
 * Require: JDK 11. Not work with JDK 8
 */
public class RunByCode {
    public static void main(String[] args) {
        // one parameter run won't save results
        String[] program1 = new String[]{"examples/locoGP/SortBubbleDouble.java"};
        String[] program2 = new String[]{"examples/locoGP/SortBubbleLoops.java", "SortBubbleLoops.java", "src/main/java/gin/automation/results/task3/SortBubbleLoops/round0/"};
        String[] program3 = new String[]{"examples/locoGP/SortInsertion.java"};
        String[] program4 = new String[]{"examples/locoGP/SortCocktail.java"};
        String[] program5 = new String[]{"examples/locoGP/SortHeap.java"};
        String[] task5Program = new String[]{"examples/locoGP/FindExtremum.java"};

        // LocalSearch.main(program1);
        // LocalSearch.main(program2);
        // LocalSearch.main(program3);
        // LocalSearch.main(program4);
        // LocalSearch.main(program5);
        LocalSearch.main(task5Program);         
    }
}