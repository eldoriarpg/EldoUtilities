package de.eldoria.eldoutilities.debug.payload;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.eldoutilities.debug.data.EntryData;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigDump extends EntryData {

    public ConfigDump(String path, String content) {
        super(path, content);
    }

    /**
     * Creates a new config dump. This dump will include external configs as well if the plugin is a {@link EldoCommand}.
     *
     * @param plugin plugin to dump teh configs
     * @return configs as an array.
     */
    public static EntryData[] create(Plugin plugin) {
        Path root = plugin.getDataFolder().toPath().toAbsolutePath().getParent().getParent();

        EldoConfig mainConfig = EldoConfig.getMainConfig(plugin.getClass());

        Set<String> configs = new HashSet<>();
        if (mainConfig != null) {
            mainConfig.save();
            configs.addAll(mainConfig.getConfigs().keySet());
        } else {
            configs.add(Paths.get(plugin.getDataFolder().toPath().toString(), "config.yml").toString());
        }

        List<ConfigDump> dumps = new ArrayList<>();
        for (String config : configs) {
            File currentConfig = Paths.get(root.toString(), config).toFile();
            String content = "Could not read";
            if (currentConfig.exists()) {
                try {
                    content = Files.readAllLines(currentConfig.toPath(), StandardCharsets.UTF_8).stream()
                            .collect(Collectors.joining(System.lineSeparator()));
                } catch (IOException e) {
                    plugin.getLogger().info("Could not read config file " + config);
                }
            }
            dumps.add(new ConfigDump(config, content));
        }

        return dumps.toArray(new ConfigDump[0]);
    }
}
