package automation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class run5JavaFiles {
    String command = "java -jar build/gin.jar ";
    String path = "examples/locoGP/";
    String[] files = {"SortBubbleDouble.java", 
                        "SortBubbleLoops.java", 
                        "SortInsertion.java"};
                        // "SortCocktail.java",
                        // "SortHeap.java"};

    public static void main(String[] args) {
        String parentDir = getParentDir();
        System.out.println(parentDir);
        new run5JavaFiles().run();
    }

    public void run() {
        Runtime runtime = Runtime.getRuntime();
        for (String file : files) {
            String commandString = command + path + file;
            System.out.println(commandString);
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
            } finally {
                System.out.println("Finished running " + file);
            }
        }
        runtime.exit(0);
        System.out.println(command);
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
