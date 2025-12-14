package ru.bmstu.iu7;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnalysisResult {

    public static class FileMetrics {
        int cc;
        HalsteadMetrics hal;

        FileMetrics(int cc, HalsteadMetrics hal) {
            this.cc = cc;
            this.hal = hal;
        }
    }

    private final Map<Path, FileMetrics> fileMetrics = new LinkedHashMap<>();

    public void addFile(Path file, int cc, HalsteadMetrics hal) {
        fileMetrics.put(file, new FileMetrics(cc, hal));
    }

    public Map<Path, FileMetrics> getFileMetrics() { return fileMetrics; }

    public int getTotalCC() {
        return fileMetrics.values().stream().mapToInt(f -> f.cc).sum();
    }

    public double getTotalHalsteadVolume() {
        return fileMetrics.values().stream().mapToDouble(f -> f.hal.getVolume()).sum();
    }
}
