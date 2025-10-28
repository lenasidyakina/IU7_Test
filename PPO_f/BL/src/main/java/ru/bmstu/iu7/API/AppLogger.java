package ru.bmstu.iu7.API;

public interface AppLogger {
    void info(String msg, Object... args);
    void warn(String msg, Object... args);
    void error(String msg, Object... args);
    void error(String msg, Throwable t, Object... args);
}
