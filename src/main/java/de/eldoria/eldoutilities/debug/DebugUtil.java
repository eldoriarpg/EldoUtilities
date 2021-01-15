package de.eldoria.eldoutilities.debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.eldoutilities.core.EldoUtilities;
import de.eldoria.eldoutilities.debug.data.DebugPayloadData;
import de.eldoria.eldoutilities.debug.data.DebugResponse;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import org.bukkit.command.CommandSender;
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

    public static void dispatchDebug(CommandSender sender, Plugin plugin) {
        EldoConfig config = EldoConfig.getMainConfig(EldoUtilities.class);
        MessageSender messageSender = MessageSender.getPluginMessageSender(EldoUtilities.class);
        if (!config.getConfig().getBoolean("debugConsens", false)) {
            String message = "By using this command you agree that we will send data belonging to you to §lour server§r.\n"
                    + "We will only send data when someone executes this command.\n"
                    + "The data will be handled confidential from our side and will be only available by a hashed key.\n"
                    + "Unless you share this key no one can access it. §cEveryone who receives this key will have access to your data.§r\n"
                    + "You can delete your data at every time with the deletion key. §cIf you lose or didnt saved your key we cant help you.§r\n"
                    + "Your data will be deleted after §l§c14 days§r.\n"
                    + "This data includes but is §l§cnot§r limited to:\n"
                    + "  - Installed Plugins and their meta data\n"
                    + "  - Latest log\n"
                    + "  - Server Informations like Worldnames and Playercount\n"
                    + "  - The configuration file or files of the debugged plugin\n"
                    + "  - Additional Data provided by our own plugins.\n"
                    + "We will filter sensitive data like IPs before sending.\n"
                    + "However we §l§ccan not§r and §l§cwill not§r gurantee that we can remove all data which is considered as confidential by you.\n"
                    + "§2If you agree please execute this command once again.\n"
                    + "§2This is a one time opt in.\n"
                    + "You can opt out again in the EldoUtilities config file.";
            messageSender.send(MessageChannel.CHAT, () -> "§6", sender, message);
            config.getConfig().set("debugConsens", true);
            config.save();
            return;
        }

        Optional<DebugResponse> response = sendDebug(plugin, DebugPayload.create(plugin));

        if (response.isPresent()) {
            messageSender.send(MessageChannel.CHAT, MessageType.NORMAL, sender,
                    "Your data is available here:\n§6" + ButlerUpdateData.HOST + "/debug/v1/read/" + response.get().getHash()
                            + "§r\nYou can delete it via this link:\n§c" + ButlerUpdateData.HOST + "/debug/v1/delete/" + response.get().getDeletionHash());
        } else {
            messageSender.send(MessageChannel.CHAT, MessageType.ERROR, sender, "Could not send data. Please try again later");
        }
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

    public static EntryData[] getAdditionalPluginMeta(Plugin plugin) {
        EntryData[] meta = new EntryData[0];
        if (plugin instanceof EldoPlugin) {
            meta = ((EldoPlugin) plugin).getDebugInformations();
        }
        return meta;
    }

    private static Optional<DebugResponse> sendDebug(Plugin plugin, DebugPayloadData payload) {
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
            return Optional.of(GSON.fromJson(builder.toString(), DebugResponse.class));
        } catch (IOException e) {
            plugin.getLogger().log(Level.FINEST, "Could not read response.", e);

            return Optional.empty();
        }
    }
}
