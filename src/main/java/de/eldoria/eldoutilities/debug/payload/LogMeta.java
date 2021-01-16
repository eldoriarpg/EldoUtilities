package de.eldoria.eldoutilities.debug.payload;

import de.eldoria.eldoutilities.debug.data.LogData;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogMeta extends LogData {
    private static final Pattern IP = Pattern.compile("/([0-9]{1,3}\\.){3}[0-9]{1,3}(:[0-9]{1,5})");
    private static final Pattern EXCEPTION = Pattern.compile(
            "^\\[[0-9]{2}:[0-9]{2}:[0-9]{2}] (\\[[^\\]]*?(?:ERROR|WARN)]:.*?)^\\[[0-9]{2}:[0-9]{2}:[0-9]{2}]",
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
        Path logPath = Paths.get(root.toString(), "logs", "latest.log");
        File logFile = logPath.toFile();

        String fullLog = "";
        String latestLog = "Could not read latest log.";

        ExceptionPair exceptionPair = new ExceptionPair();

        if (logFile.exists()) {
            if (logFile.length() / (1024 * 1024) > 50) {
                List<String> start = new LinkedList<>();
                FixedList<String> end = new FixedList<>(2500);
                // The log seems to be large we will read it partially.
                int linesReadSinceScan = 0;
                try (InputStream stream = new FileInputStream(logFile); Scanner reader = new Scanner(stream)) {
                    while (reader.hasNext()) {
                        if (start.size() < 2500) {
                            start.add(reader.nextLine());
                            continue;
                        }
                        end.add(reader.nextLine());
                        linesReadSinceScan++;
                        if (linesReadSinceScan == 2500) {
                            exceptionPair.combine(extractExceptions(end.getLinkedList(), plugin));
                            // we want a slight overlap
                            linesReadSinceScan = 250;
                        }
                    }
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not read log.", e);
                }
                String startLog = String.join("\n", start);
                String endLog = String.join("\n", end.linkedList);
                latestLog = startLog + "\n\n[...]\n\n" + endLog;
            } else {
                try {
                    List<String> lines = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8);
                    if (lines.size() <= 5000) {
                        latestLog = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8).stream()
                                .collect(Collectors.joining(System.lineSeparator()));
                        exceptionPair.combine(extractExceptions(latestLog, plugin));
                    } else {
                        String start = String.join("\n", lines.subList(0, 2500));
                        String end = String.join("\n", lines.subList(lines.size() - 2500, lines.size()));
                        latestLog = start + "\n\n[...]\n\n" + end;
                        exceptionPair.combine(extractExceptions(Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8), plugin));
                    }
                } catch (IOException e) {
                    plugin.getLogger().info("Could not read log file");
                }
            }
        }
        latestLog = latestLog.replaceAll(IP.pattern(), "/127.0.0.1");


        return new LogMeta(latestLog, exceptionPair.getInternalArray(), exceptionPair.getExternalArray());
    }

    private static ExceptionPair extractExceptions(String log, Plugin plugin) {
        String[] packages = plugin.getDescription().getMain().split("\\.");
        String project = String.join(".", Arrays.copyOfRange(packages, 0, Math.min(packages.length, 3)));

        Set<String> external = new LinkedHashSet<>();
        Set<String> internal = new LinkedHashSet<>();
        Matcher matcher = EXCEPTION.matcher(log);
        while (matcher.find()) {
            String match = matcher.group(1);
            if (match.contains(project)) {
                internal.add(match);
            } else {
                external.add(match);
            }
        }
        return new ExceptionPair(external, internal);
    }

    private static ExceptionPair extractExceptions(List<String> lines, Plugin plugin) {
        return extractExceptions(String.join("\n", lines), plugin);
    }

    private static class ExceptionPair {
        private final Set<String> external;
        private final Set<String> internal;

        public ExceptionPair() {
            external = new LinkedHashSet<>();
            internal = new LinkedHashSet<>();
        }

        public ExceptionPair(Set<String> external, Set<String> internal) {
            this.external = new LinkedHashSet<>(external);
            this.internal = new LinkedHashSet<>(internal);
        }

        public Set<String> getExternal() {
            return external;
        }

        public Set<String> getInternal() {
            return internal;
        }

        public void combine(ExceptionPair pair) {
            external.addAll(pair.external);
            internal.addAll(pair.internal);
        }

        public String[] getExternalArray() {
            return external.toArray(new String[0]);
        }

        public String[] getInternalArray() {
            return internal.toArray(new String[0]);
        }
    }

    private static class FixedList<E> {
        private final int size;
        private final LinkedList<E> linkedList = new LinkedList<>();

        public FixedList(int size) {
            this.size = size;
        }

        public boolean add(E e) {
            if (linkedList.size() > size) {
                linkedList.removeLast();
            }
            return linkedList.add(e);
        }

        public Iterator<E> iterator() {
            return linkedList.iterator();
        }

        public void clear() {
            linkedList.clear();
        }

        public ListIterator<E> listIterator() {
            return linkedList.listIterator();
        }

        public ListIterator<E> listIterator(int index) {
            return linkedList.listIterator(index);
        }

        public LinkedList<E> getLinkedList() {
            return linkedList;
        }
    }
}
