package gin.automation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class run5JavaFiles {
    /* 
    command sample:
    java -jar build/gin.jar examples/locoGP/SortCocktail.java | grep -E ".*(best|Initial)" > sc1.txt 

    bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised");
    */
    String command = "java -jar build/gin.jar ";
    static final String suffix1 = " | grep -E  \".*(best|Initial)\"";
    static final String suffix2 = " > ";
    String path = "examples/locoGP/";

    /* "SortBubbleDouble.java", 
        "SortBubbleLoops.java", 
        "SortInsertion.java", 
        "SortCocktail.java", 
        "SortHeap.java" 
    */
    String[] files = {"SortBubbleLoops.java"};

    public static void main(String[] args) {
        String parentDir = getParentDir();
        System.out.println(parentDir);
        new run5JavaFiles().run();
    }

    public void run() {
        Runtime runtime = Runtime.getRuntime();
        for (String file : files) {
            String commandString = command + path + file + suffix1;
            System.out.println(commandString);
            // run the command 15 times
            for (int i = 0; i < 1; i++) {
                try {
                    Process process = runtime.exec(commandString);
                    // show output in terminal
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        runtime.exit(0);
        System.out.println(command); //useless
    }

    public static String getParentDir() {
        String dir = "";
        try {
            File temp = new File("temp");
            dir = temp.getAbsolutePath().replace("temp", "");
            //System.out.println(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir;
    }
}
