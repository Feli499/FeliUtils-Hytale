package de.feli490.utils.hytale.utils;

import com.hypixel.hytale.logger.HytaleLogger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class HytaleLoggerAdapter extends Handler {

    private final HytaleLogger logger;

    private HytaleLoggerAdapter(HytaleLogger logger) {
        this.logger = logger;
        setLevel(Level.ALL);
    }

    @Override
    public void publish(LogRecord record) {

        if (record == null || !isLoggable(record)) {
            return;
        }

        Level level = record.getLevel() != null ? record.getLevel() : Level.INFO;

        logger.at(level)
              .withCause(record.getThrown())
              .log(record.getMessage());
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    public static Logger createLoggerAdapter(HytaleLogger hytaleLogger) {

        Logger julLogger = LogManager.getLogManager()
                                     .getLogger("");
        julLogger.setUseParentHandlers(false);
        for (Handler handler : julLogger.getHandlers()) {
            julLogger.removeHandler(handler);
        }
        julLogger.addHandler(new HytaleLoggerAdapter(hytaleLogger));
        return julLogger;
    }
}
