package gin.automation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * Run the benchmark program 15 times and save the outputs
 * @benchmark {@code SortBubbleLoops.java}
 * @author Zuxing
 */
public class RunBenchmark {
    /* 
    command sample:
    java -jar build/gin.jar examples/locoGP/SortBubbleLoops.java 
    this part only works in command window: | grep -E ".*(best|Initial)" > sc1.txt 
    bestPatch.writePatchedSourceToFile(sourceFile.getFilename() + ".optimised");
    */
    static final String COMMAND = "java -jar build/gin.jar ";
    static final String suffix1 = " | grep -E \".*(best|Initial)\"";
    static final String PATH = "examples/locoGP/";
    static final String outputPath = "src/main/java/gin/automation/results/";
    String parentDir = getParentDir();

    String benchmark = "SortBubbleLoops.java";

    public static void main(String[] args) {
        new RunBenchmark().runProgram();
    }

    public void runProgram() {
        System.out.println(parentDir);
        Runtime runtime = Runtime.getRuntime();
        String file = benchmark;
        String commandString = COMMAND + PATH + file;
        System.out.println(commandString);
        String programName = file.replace(".java", "");
        // run the command 15 times
        for (int i = 0; i < 15; i++) {
            String round = String.valueOf(i + 1);
            System.out.println("===================round " + round + "===============================");
            String subFolder = "task3/".concat(programName).concat("/round").concat(round).concat("/");
            try {
                System.out.println("Running command: " + commandString + " " + file + " " + outputPath + subFolder);
                Process process = runtime.exec(commandString + " " + file + " " + outputPath + subFolder);
                // show output in terminal
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                boolean has2Edits = true;
                StringBuilder output = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    // contains keywords
                    if (line.contains("best") || line.contains("Best execution") || line.contains("Initial") 
                    || line.contains("Found") || line.contains("Speedup")) {
                        System.out.println(line); // print to terminal
                        output.append(line + "\n"); // save to output
                    } else if (line.contains("Best patch")) {
                        // if line has less than 3 '|', it means the best patch has <2 edits
                        // if (line.split("\\|").length < 3) {
                        //     has2Edits = false;
                        //     System.out.println("Best patch has less than 2 edits, discard this round.");
                        //     break;
                        // }
                        System.out.println("\n" + line); // print to terminal
                        output.append("\n" + line + "\n"); // save to output
                    } /* else {
                        System.out.println(line);
                    } */
                }
                reader.close();
                // if best patch has less than 2 edits, discard this round
                if (!has2Edits) {
                    i--;
                    continue;
                }
                // save output to .txt file
                saveOutputToFile(output.toString(), subFolder);

                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nProgram finished running.");
        runtime.exit(0);
    }

    /**
     * Save the output to a file
     * @param content
     * @param program
     * @param round
     * @author Zuxing
     */
    private void saveOutputToFile(String content, String subFolder) {
        // String subFolder = "task3/".concat(program).concat("/round").concat(round).concat("/");
        try {
            // create the directory if not exist
            File dir = new File(parentDir + outputPath + subFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // create the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(dir,"sbl.txt")));
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
