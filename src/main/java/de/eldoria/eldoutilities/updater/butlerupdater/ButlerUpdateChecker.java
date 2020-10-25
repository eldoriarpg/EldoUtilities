package de.eldoria.eldoutilities.updater.butlerupdater;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import de.eldoria.eldoutilities.updater.Updater;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Level;

public class ButlerUpdateChecker extends Updater<ButlerUpdateData> {
    UpdateCheckResponse response;

    public ButlerUpdateChecker(ButlerUpdateData data) {
        super(data);
    }

    @Override
    protected Optional<String> getLatestVersion(ButlerUpdateData data) {
        data.getHost();
        Plugin plugin = data.getPlugin();

        HttpURLConnection con;
        try {
            URL url = new URL(data.getHost() + "/check?version=" + plugin.getDescription().getVersion() + "&id=" + data.getButlerId() + "&devbuild=" + false);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not open connection.", e);
            return Optional.empty();
        }

        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try {
            if (con.getResponseCode() != 200) {
                plugin.getLogger().log(Level.WARNING, "Received non 200 request.");
                return Optional.empty();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                builder.append(responseLine.trim());
            }
            response = new Gson().fromJson(builder.toString(), UpdateCheckResponse.class);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not read response.", e);
            return Optional.empty();
        }

        return Optional.of(response.getLatestVersion());
    }

    @Override
    protected void update() {
        if (response == null) return;
        Plugin plugin = getData().getPlugin();
        plugin.getLogger().info("§2New update found. Performing auto update.");
        URL url;
        try {
            url = new URL(getData().getHost() + "/download?id=" + getData().getButlerId() + "&version=" + response.getLatestVersion());
        } catch (MalformedURLException e) {
            plugin.getLogger().log(Level.WARNING, "Could not create download url.", e);
            return;
        }

        String plugins = plugin.getDataFolder().getParent();
        File update = new File(Paths.get(plugins, "update").toString());
        if (!update.exists()) {
            update.mkdirs();
        }


        Path target = Paths.get(update.getAbsolutePath(), getData().getPlugin().getName() + ".jar");
        try (InputStream input = url.openStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not copy new file.", e);
        }

        String hash;
        plugin.getLogger().info("Calculate checksum.");
        try {
            hash = Hashing.sha256().hashBytes(Files.readAllBytes(target)).toString();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to create hash from update file. Deleting Update.", e);
            new File(target.toString()).delete();
            return;
        }

        if (!hash.equals(response.getHash())) {
            plugin.getLogger().warning("Checksums of update file is not as expected. Deleting update.");
            return;
        }
        plugin.getLogger().info("§2Checksums of update file is correct. Restart to apply update.");
    }
}
