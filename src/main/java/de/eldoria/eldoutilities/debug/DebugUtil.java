package de.eldoria.eldoutilities.debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.eldoria.eldoutilities.debug.data.DebugPayloadData;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class DebugUtil {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private DebugUtil() {
    }

    public static Optional<String> dispatchDebug(Plugin plugin) {
        return sendDebug(plugin, DebugPayload.create(plugin));
    }

    static String getLatestLog(Plugin plugin) {
        Path root = plugin.getDataFolder().toPath().toAbsolutePath().getParent().getParent();
        File logFile = Paths.get(root.toString(), "logs", "latest.log").toFile();

        String latestLog = "Could not read latest log.";
        if (logFile.exists()) {
            try {
                latestLog = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8).stream()
                        .collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {
                plugin.getLogger().info("Could not read log file");
            }
        }
        return latestLog;
    }

    public static String getAdditionalPluginMeta(Plugin plugin) {
        String meta = null;
        if (plugin instanceof EldoPlugin) {
            meta = ((EldoPlugin) plugin).getDebugInformations();
        }
        if (meta == null) {
            return "";
        }
        return meta;
    }

    private static Optional<String> sendDebug(Plugin plugin, DebugPayloadData payload) {
        HttpURLConnection con;
        try {
            URL url = new URL(ButlerUpdateData.HOST + "/debug/v1/submit");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
        } catch (IOException e) {
            plugin.getLogger().log(Level.FINEST, "Could not open connection.", e);
            return Optional.empty();
        }

        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try (OutputStream outputStream = con.getOutputStream()) {
            byte[] input = GSON.toJson(payload).getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
        } catch (IOException e) {
            plugin.getLogger().info("Could not write to connection.");
        }

        try {
            if (con.getResponseCode() != 200) {
                plugin.getLogger().log(Level.FINEST, "Received non 200 request for debug submission.");
                return Optional.empty();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.INFO, "Could not read response.", e);
            return Optional.empty();
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                builder.append(responseLine.trim());
            }
            return Optional.of(builder.toString());
        } catch (IOException e) {
            plugin.getLogger().log(Level.FINEST, "Could not read response.", e);

            return Optional.empty();
        }
    }
}
