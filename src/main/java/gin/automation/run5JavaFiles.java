package gin.automation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class run5JavaFiles {
    /* 
    command sample:
    java -jar build/gin.jar examples/locoGP/SortCocktail.java | grep -E ".*(best|Initial)" > sc1.txt 

    bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised");
    */
    String command = "java -jar build/gin.jar ";
    static final String suffix1 = " | grep -E \".*(best|Initial)\"";
    String path = "examples/locoGP/";
    static final String outputPath = "src/main/java/gin/automation/results/";
    String parentDir = getParentDir();

    /* "SortBubbleDouble.java", 
        "SortBubbleLoops.java", 
        "SortInsertion.java", 
        "SortCocktail.java", 
        "SortHeap.java" 
    */
    String[] files = {"SortBubbleDouble.java", 
                        "SortBubbleLoops.java", 
                        "SortInsertion.java", 
                        "SortCocktail.java", 
                        "SortHeap.java" };

    public static void main(String[] args) {
        new run5JavaFiles().runProgram();
    }

    public void runProgram() {
        System.out.println(parentDir);
        Runtime runtime = Runtime.getRuntime();
        for (String file : files) {
            String commandString = command + path + file + suffix1;
            System.out.println(commandString);
            String programName = file.replace(".java", "");
            // run the command 15 times
            for (int i = 0; i < 15; i++) {
                System.out.println("===================round " + (i + 1) + "===============================");
                try {
                    Process process = runtime.exec(commandString);
                    // show output in terminal
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    StringBuilder output = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        // contains keywords
                        if (line.contains("best") || line.contains("Best execution") || line.contains("Initial") 
                        || line.contains("Found") || line.contains("Speedup")) {
                            System.out.println(line); // print to terminal
                            output.append(line + "\n"); // save to output
                        } else if (line.contains("Best patch")) {
                            System.out.println("\n" + line); // print to terminal
                            output.append("\n" + line + "\n"); // save to output
                        }
                    }
                    reader.close();
                    // save output to .txt file
                    saveOutputToFile(output.toString(), programName, String.valueOf(i + 1));

                    process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        runtime.exit(0);
        System.out.println(command); //useless
    }

    /**
     * Save the output to a file
     * @param content
     * @param program
     * @param round
     * @author Zuxing
     */
    private void saveOutputToFile(String content, String program, String round) {
        String subFolder = "task2/".concat(program).concat("/");
        try {
            // create the directory if not exist
            File dir = new File(parentDir + outputPath + subFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // create the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir, program + "_" + round + ".txt")));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
