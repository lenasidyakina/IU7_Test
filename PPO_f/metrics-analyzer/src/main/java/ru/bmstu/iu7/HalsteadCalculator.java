package ru.bmstu.iu7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HalsteadCalculator {

    private static final String[] OPERATORS = {
            "+", "-", "*", "/", "%", "=", "==", "!=", "<", ">", "<=", ">=",
            "&&", "||", "!", "++", "--",
            "if", "else", "for", "while", "do", "switch", "case", "catch", "try", "return"
    };

    public HalsteadMetrics calculateForFile(Path javaFile) throws IOException {
        List<String> lines = Files.readAllLines(javaFile);

        Set<String> uniqueOperators = new HashSet<>();
        Set<String> uniqueOperands = new HashSet<>();
        int totalOperators = 0;
        int totalOperands = 0;

        for (String line : lines) {
            line = line.replaceAll("//.*", "").trim(); // убрать комментарии

            for (String op : OPERATORS) {
                int count = countOccurrences(line, op);
                if (count > 0) {
                    uniqueOperators.add(op);
                    totalOperators += count;
                }
            }

            String[] tokens = line.split("\\W+");
            for (String token : tokens) {
                if (!token.isEmpty() && !isOperator(token)) {
                    uniqueOperands.add(token);
                    totalOperands++;
                }
            }
        }

        return new HalsteadMetrics(uniqueOperators, uniqueOperands, totalOperators, totalOperands);
    }

    private boolean isOperator(String token) {
        for (String op : OPERATORS) {
            if (op.equals(token)) return true;
        }
        return false;
    }

    private int countOccurrences(String line, String token) {
        int count = 0, idx = 0;
        while ((idx = line.indexOf(token, idx)) != -1) {
            count++;
            idx += token.length();
        }
        return count;
    }

    public HalsteadMetrics calculateFromString(String code) {
        Set<String> uniqueOperators = new HashSet<>();
        Set<String> uniqueOperands = new HashSet<>();
        int totalOperators = 0;
        int totalOperands = 0;

        String[] lines = code.split("\n");
        for (String line : lines) {
            for (String op : OPERATORS) {
                int count = countOccurrences(line, op);
                if (count > 0) {
                    uniqueOperators.add(op);
                    totalOperators += count;
                }
            }
            String[] tokens = line.split("\\W+");
            for (String token : tokens) {
                if (!token.isEmpty() && !isOperator(token)) {
                    uniqueOperands.add(token);
                    totalOperands++;
                }
            }
        }
        return new HalsteadMetrics(uniqueOperators, uniqueOperands, totalOperators, totalOperands);
    }

}
