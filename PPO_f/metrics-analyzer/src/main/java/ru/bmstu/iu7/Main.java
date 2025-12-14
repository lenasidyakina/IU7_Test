package ru.bmstu.iu7;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        Path projectRoot = Path.of(".."); // root проекта

        System.out.println("Analyzing Cyclomatic Complexity...");
        Map<Path, Map<String, Integer>> ccResult = analyzer.analyzeCC(projectRoot);

        Path ccFile = Path.of("cyclomatic_complexity.txt");
        Files.writeString(ccFile, "File -> Method -> CC\n");
        for (var fileEntry : ccResult.entrySet()) {
            for (var methodEntry : fileEntry.getValue().entrySet()) {
                String line = fileEntry.getKey() + " -> " + methodEntry.getKey() +
                        " -> " + methodEntry.getValue() + "\n";
                Files.writeString(ccFile, line, StandardOpenOption.APPEND);
            }
        }


        System.out.println("Analyzing Halstead metrics...");
        Map<String, HalsteadMetrics> halResult = analyzer.analyzeHalstead(projectRoot);
        Path halFile = Path.of("halstead_complexity.txt");
        Files.writeString(halFile, "File:Method -> Volume | Difficulty | Effort\n");
        for (var entry : halResult.entrySet()) {
            HalsteadMetrics hm = entry.getValue();
            String line = String.format("%s -> %.2f | %.2f | %.2f%n",
                    entry.getKey(), hm.getVolume(), hm.getDifficulty(), hm.getEffort());
            Files.writeString(halFile, line, StandardOpenOption.APPEND);
        }

        System.out.println("Analysis complete. Results saved to:");
        System.out.println("- " + ccFile.toAbsolutePath());
        System.out.println("- " + halFile.toAbsolutePath());
    }
}
