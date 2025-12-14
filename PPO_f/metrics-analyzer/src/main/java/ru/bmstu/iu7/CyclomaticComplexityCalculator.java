package ru.bmstu.iu7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CyclomaticComplexityCalculator {

    public int calculateCC(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        int cc = 1; // минимальная сложность = 1

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("if") || line.startsWith("for") || line.startsWith("while") ||
                    line.startsWith("case") || line.contains("&&") || line.contains("||")) {
                cc++;
            }
        }

        return cc;
    }

    public int calculateForFile(Path javaFile) throws IOException {
        List<String> lines = Files.readAllLines(javaFile);
        int complexity = 1;

        for (String line : lines) {
            line = removeSingleLineComments(line);

            complexity += count(line, " if ");
            complexity += count(line, " for ");
            complexity += count(line, " while ");
            complexity += count(line, " catch ");
            complexity += count(line, " case ");

            complexity += count(line, "&&");
            complexity += count(line, "||");
            complexity += count(line, "?");
        }

        return complexity;
    }

    private String removeSingleLineComments(String line) {
        int idx = line.indexOf("//");
        return idx >= 0 ? line.substring(0, idx) : line;
    }

    private int countChar(String line, char c) {
        int count = 0;
        for (char ch : line.toCharArray()) {
            if (ch == c) count++;
        }
        return count;
    }


    private int count(String line, String token) {
        int c = 0, i = 0;
        while ((i = line.indexOf(token, i)) != -1) {
            c++;
            i += token.length();
        }
        return c;
    }

    public Map<String, String> extractMethods(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        Map<String, String> methods = new LinkedHashMap<>();

        Pattern methodPattern = Pattern.compile(
                "(public|private|protected|static|\\s)+[\\w<>\\[\\]]+\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{"
        );

        String currentMethod = null;
        int braceCount = 0;
        StringBuilder body = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            line = line.replaceAll("//.*", "").replaceAll("/\\*.*\\*/", "");

            Matcher matcher = methodPattern.matcher(line);
            if (matcher.find()) {
                if (currentMethod != null) {
                    methods.put(currentMethod, body.toString());
                }
                currentMethod = matcher.group(2);
                body = new StringBuilder();
                braceCount = 1;
                continue;
            }

            if (currentMethod != null) {
                body.append(line).append("\n");
                braceCount += countChar(line, '{');
                braceCount -= countChar(line, '}');

                if (braceCount == 0) {
                    methods.put(currentMethod, body.toString());
                    currentMethod = null;
                }
            }
        }
        return methods;
    }

    public Map<String, Integer> calculateCCPerMethod(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        return calculateCCFromLines(lines);
    }

    private Map<String, Integer> calculateCCFromLines(List<String> lines) {
        Map<String, Integer> ccPerMethod = new LinkedHashMap<>();
        String currentMethod = null;
        int cc = 1;
        int braceCount = 0;

        Pattern methodPattern = Pattern.compile(
                "(public|private|protected|static|\\s)+[\\w<>\\[\\]]+\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{"
        );

        for (String line : lines) {
            line = preprocessLine(line);

            Matcher matcher = methodPattern.matcher(line);
            if (matcher.find()) {
                if (currentMethod != null) {
                    ccPerMethod.put(currentMethod, cc);
                }
                currentMethod = matcher.group(2);
                cc = 1;
                braceCount = 1;
                continue;
            }

            if (currentMethod != null) {
                cc += countCCInLine(line);
                braceCount += countChar(line, '{');
                braceCount -= countChar(line, '}');

                if (braceCount == 0) {
                    ccPerMethod.put(currentMethod, cc);
                    currentMethod = null;
                }
            }
        }

        return ccPerMethod;
    }

    private String preprocessLine(String line) {
        line = line.trim();
        line = line.replaceAll("//.*", "").replaceAll("/\\*.*\\*/", "");
        return line;
    }

    private int countCCInLine(String line) {
        int cc = 0;
        if (line.startsWith("if") || line.startsWith("for") || line.startsWith("while") ||
                line.startsWith("case") || line.contains("&&") || line.contains("||") ||
                line.startsWith("catch")) {
            cc = 1;
        }
        return cc;
    }


}

