package de.eldoria.eldoutilities.core.commands;

import de.eldoria.eldoutilities.debug.DebugPayload;
import de.eldoria.eldoutilities.debug.DebugUtil;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.simplecommands.TabCompleteUtil;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EldoDebug extends EldoCommand {

    private List<String> plugins;

    public EldoDebug(Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (denyAccess(sender, "eldoutilities.debug")) {
            return true;
        }

        if (argumentsInvalid(sender, args, 1, "<plugin name>")) {
            return true;
        }

        @NotNull Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        Plugin plugin = null;
        for (Plugin pl : plugins) {
            if (pl.getName().equalsIgnoreCase(args[0])) {
                plugin = pl;
                break;
            }
        }

        if (plugin == null) {
            messageSender().send(MessageChannel.CHAT, MessageType.ERROR, sender, "Invalid plugin");
            return true;
        }

        Optional<String> s = DebugUtil.dispatchDebug(plugin);

        if (s.isPresent()) {
            messageSender().send(MessageChannel.CHAT, MessageType.NORMAL, sender,
                    "Your data is available here: §6" + ButlerUpdateData.HOST + "/debug/v1/read/" + s.get());
        } else {
            messageSender().send(MessageChannel.CHAT, MessageType.ERROR, sender, "Could not send data. Please try again later");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return TabCompleteUtil.complete(args[0], getPlugins());
        }
        return Collections.emptyList();
    }

    private List<String> getPlugins() {
        if (plugins == null) {
            plugins = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(plugins);
    }
}
