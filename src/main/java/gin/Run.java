package gin;

public class Run {
    public static void main(String[] args) {
        String[] program1 = new String[]{"examples/locoGP/SortBubbleDouble.java"};
        String[] program2 = new String[]{"examples/locoGP/SortBubbleLoops.java"};
        String[] program3 = new String[]{"examples/locoGP/SortInsertion.java"};
        String[] program4 = new String[]{"examples/locoGP/SortCocktail.java"};

        LocalSearch.main(program1);
        // LocalSearch.main(program2);
        // LocalSearch.main(program3);
        // LocalSearch.main(program4);                                                    
    }
}
