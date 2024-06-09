a1816653, a1816653@adelaide.edu.au, Zuxing Wu
a1770422, a1770422@adelaide.edu.au, Tom Zhu
a1899038, a8199038@adelaide.edu.au, Ishaan Verma
a1940959, a1940959@adelaide.edu.au, Hermanto

=======================Instructions to run the code=============================
Development environment:
1. JDK 1.8
2. IDE: VS Code
3. Build tool: Gradle 3.3
4. JDK 11 might be needed when trying to run GIN in code and compile always fail.

--------------------------------Task 2------------------------------------------
1. Our graphs in Task2 were generated from results by running the command 
"java -jar build/gin.jar examples/locoGP/<Algorithm>.java | grep -E ".*(best|Initial)" > <algorithm>.txt"
for each algorithm 15 times, recording the initial execution time and all subsequent best execution times.
These generated .txt files are saved in folder: /AdvancedGIN/task2.

2. We can execute these commands 15 times using for loops, and also save
the terminal outputs into files using java's BufferWriter. The java code is in the
folder: src/main/java/gin/automation/Run5JavaFiles.java (it works in JDK 11)
Running this java code, results will be automatically saved in this folder:
src/main/java/gin/automation/results/task2/...

--------------------------------Task 3------------------------------------------
Run this code: (it works in JDK 11)
src/main/java/gin/automation/RunBenchmark.java (Our benchmark is SortBubbleLoops.java)
Results will be saved in this folder:
src/main/java/gin/automation/results/task3/SortBubbleLoops/(round1 - round15)

--------------------------------Task 4------------------------------------------
Run this code: (it works in JDK 11)
src/main/java/gin/automation/RunByCode.java
Results will be saved in this folder:
src/main/java/gin/automation/results/task3/SortBubbleLoops/round0
Compare the last 2 .optimised files, u will find they are identical after patch was minimised.

Or run this command:
java -jar build/gin.jar examples/locoGP/SortBubbleLoops.java | grep -E ".*(best|Initial)" > sbl.txt"
Results will be saved in the root folder of this project.

You can see outputs appear near the end of each run:
    Best patch found: | DEL 3 | MOVE 1 -> 1:0 | DEL 9 | MOVE 7 -> 3:2 |
    Minimised best patch: | MOVE 1 -> 1:0 |
meaninng the bast patch is minimised.

--------------------------------Task 5------------------------------------------
