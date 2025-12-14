package ru.bmstu.iu7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ProjectAnalyzer {

    private final CyclomaticComplexityCalculator ccCalc = new CyclomaticComplexityCalculator();
    private final HalsteadCalculator halCalc = new HalsteadCalculator();

    public Map<Path, Map<String, Integer>> analyzeCC(Path projectRoot) throws IOException {
        Map<Path, Map<String, Integer>> result = new LinkedHashMap<>();

        try (var paths = Files.walk(projectRoot)) {
            paths.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString().contains("metrics-analyzer"))
                    .filter(p -> !p.toString().contains("build"))
                    .forEach(file -> {
                        try {
                            Map<String, Integer> ccPerMethod = ccCalc.calculateCCPerMethod(file);

                            // Проверка порога CC
                            for (var entry : ccPerMethod.entrySet()) {
                                if (entry.getValue() > 10) {
                                    System.err.printf("ERROR: CC=%d -> %s in %s%n",
                                            entry.getValue(), entry.getKey(), file);
                                    System.exit(1);
                                }
                            }

                            result.put(file, ccPerMethod);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        return result;
    }

    // Метод для анализа Halstead
    public Map<String, HalsteadMetrics> analyzeHalstead(Path projectRoot) throws IOException {
        Map<String, HalsteadMetrics> result = new LinkedHashMap<>();

        try (var paths = Files.walk(projectRoot)) {
            paths.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString().contains("metrics-analyzer"))
                    .filter(p -> !p.toString().contains("build"))
                    .forEach(file -> {
                        try {
                            Map<String, String> methodBodies = ccCalc.extractMethods(file);
                            for (var entry : methodBodies.entrySet()) {
                                HalsteadMetrics hm = halCalc.calculateFromString(entry.getValue());
                                String key = file + ":" + entry.getKey();
                                result.put(key, hm);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        return result;
    }
}
