package de.eldoria.eldoutilities.debug.payload;

import de.eldoria.eldoutilities.debug.data.LogData;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogMeta extends LogData {
    private static final Pattern IP = Pattern.compile("/([0-9]{1,3}\\.){3}[0-9]{1,3}(:[0-9]{1,5})");
    private static final Pattern EXCEPTION = Pattern.compile(
            "^(\\[[0-9]{2}:[0-9]{2}:[0-9]{2}] \\[Server thread/(?:ERROR|WARN)]:.*?)\\[[0-9:].+?]",
            Pattern.DOTALL + Pattern.MULTILINE);

    public LogMeta(String log, String[] internalExceptions, String[] exceptions) {
        super(log, internalExceptions, exceptions);
    }

    /**
     * Gets the latest log from the logs directory.
     *
     * @param plugin plugin for pure lazyness and logging purposes
     * @return Log as string.
     */
    public static LogData create(Plugin plugin) {
        Path root = plugin.getDataFolder().toPath().toAbsolutePath().getParent().getParent();
        File logFile = Paths.get(root.toString(), "logs", "latest.log").toFile();

        String fullLog = "";
        String latestLog = "Could not read latest log.";
        if (logFile.exists()) {
            try {
                List<String> lines = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8);
                if (lines.size() <= 5000) {
                    latestLog = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8).stream()
                            .collect(Collectors.joining(System.lineSeparator()));
                    fullLog = latestLog;
                } else {
                    String start = String.join("\n", lines.subList(0, 2500));
                    String end = String.join("\n", lines.subList(lines.size() - 2500, lines.size()));
                    latestLog = start + "\n\n[...]\n\n" + end;
                    fullLog = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8).stream()
                            .collect(Collectors.joining(System.lineSeparator()));
                }
            } catch (IOException e) {
                plugin.getLogger().info("Could not read log file");
            }
        }
        latestLog = latestLog.replaceAll(IP.pattern(), "/127.0.0.1");

        Set<String> exceptions = new HashSet<>();
        Set<String> internalExceptions = new HashSet<>();
        Matcher matcher = EXCEPTION.matcher(fullLog);
        while (matcher.find()) {
            String match = matcher.group(1);
            if (match.contains("de.eldoria")) {
                internalExceptions.add(match);
            } else {
                exceptions.add(match);
            }
        }

        return new LogMeta(latestLog, internalExceptions.toArray(new String[0]), exceptions.toArray(new String[0]));
    }
}
