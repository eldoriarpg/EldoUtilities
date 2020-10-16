package de.eldoria.eldoutilities;

import de.eldoria.eldoutilities.utils.ObjUtil;
import org.bukkit.Bukkit;
import org.bukkit.Server;

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
        private static final Logger INTERNAL_LOGGER = ObjUtil.nonNullOrElse(Bukkit.getServer(), Server::getLogger, null);

        private InternalLogger() {
        }

        public void log(Level level, String msg) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.log(level, PREFIX + msg);
            }, () -> System.out.println(msg));
        }

        public void log(Level level, String msg, Object param1) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.log(level, PREFIX + msg, param1);
            }, () -> System.out.println(msg));
        }

        public void log(Level level, String msg, Object[] params) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.log(level, PREFIX + msg, params);
            }, () -> System.out.println(msg));
        }

        public void log(Level level, String msg, Throwable thrown) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.log(level, PREFIX + msg, thrown);
            }, () -> System.out.println(msg));
        }

        public void severe(String msg) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.severe(PREFIX + msg);
            }, () -> System.out.println(msg));
        }

        public void warning(String msg) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.warning(PREFIX + msg);
            }, () -> System.out.println(msg));

            INTERNAL_LOGGER.warning(PREFIX + msg);
        }

        public void info(String msg) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.info(PREFIX + msg);
            }, () -> System.out.println(msg));
        }

        public void config(String msg) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.config(PREFIX + msg);
            }, () -> System.out.println(msg));
        }

        public void fine(String msg) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.fine(PREFIX + msg);
            }, () -> System.out.println(msg));
        }

        public void finer(String msg) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.finer(PREFIX + msg);
            }, () -> System.out.println(msg));
        }

        public void finest(String msg) {
            ObjUtil.nonNull(INTERNAL_LOGGER, logger -> {
                logger.finest(PREFIX + msg);
            }, () -> System.out.println(msg));
        }
    }
}
