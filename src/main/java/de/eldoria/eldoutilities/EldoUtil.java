package de.eldoria.eldoutilities;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class EldoUtil {
    private static final InternalLogger LOGGER = new InternalLogger();

    private EldoUtil() {
    }

    public static InternalLogger logger() {
        return LOGGER;
    }

    public static final class InternalLogger {
        private static final String PREFIX = "[EldoUtilities] ";
        private static final Logger INTERNAL_LOGGER = Bukkit.getLogger();

        private InternalLogger() {
        }

        public void log(Level level, String msg) {
            INTERNAL_LOGGER.log(level, PREFIX + msg);
        }

        public void log(Level level, String msg, Object param1) {
            INTERNAL_LOGGER.log(level, PREFIX + msg, param1);
        }

        public void log(Level level, String msg, Object[] params) {
            INTERNAL_LOGGER.log(level, PREFIX + msg, params);
        }

        public void log(Level level, String msg, Throwable thrown) {
            INTERNAL_LOGGER.log(level, PREFIX + msg, thrown);
        }

        public void severe(String msg) {
            INTERNAL_LOGGER.severe(PREFIX + msg);
        }

        public void warning(String msg) {
            INTERNAL_LOGGER.warning(PREFIX + msg);
        }

        public void info(String msg) {
            INTERNAL_LOGGER.info(PREFIX + msg);
        }

        public void config(String msg) {
            INTERNAL_LOGGER.config(PREFIX + msg);
        }

        public void fine(String msg) {
            INTERNAL_LOGGER.fine(PREFIX + msg);
        }

        public void finer(String msg) {
            INTERNAL_LOGGER.finer(PREFIX + msg);
        }

        public void finest(String msg) {
            INTERNAL_LOGGER.finest(PREFIX + msg);
        }
    }
}
