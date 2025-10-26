package ru.bmstu.iu7;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bmstu.iu7.API.AppLogger;

public class AppLoggerImpl implements AppLogger {

    private final Logger logger;

    public AppLoggerImpl(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public AppLoggerImpl(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }

    @Override
    public void info(String msg, Object... args) {
        logger.info(msg, args);
    }

    @Override
    public void warn(String msg, Object... args) {
        logger.warn(msg, args);
    }

    @Override
    public void error(String msg, Object... args) {
        logger.error(msg, args);
    }

    @Override
    public void error(String msg, Throwable t, Object... args) {
        if (args != null && args.length > 0) {
            logger.error(String.format(msg.replace("{}", "%s"), args), t);
        } else {
            logger.error(msg, t);
        }
    }
}
