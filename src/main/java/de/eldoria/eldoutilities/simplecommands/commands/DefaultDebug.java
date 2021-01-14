package de.eldoria.eldoutilities.simplecommands.commands;

import de.eldoria.eldoutilities.debug.DebugPayload;
import de.eldoria.eldoutilities.debug.DebugUtil;
import de.eldoria.eldoutilities.messages.MessageChannel;
import de.eldoria.eldoutilities.messages.MessageType;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import de.eldoria.eldoutilities.updater.butlerupdater.ButlerUpdateData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DefaultDebug extends EldoCommand {

    private final String permission;

    public DefaultDebug(EldoPlugin plugin, String permission) {
        super(plugin);
        this.permission = permission;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (denyAccess(sender, permission, "eldoutilities.debug")) {
            return true;
        }

        Optional<String> s = DebugUtil.dispatchDebug(getPlugin());

        if (s.isPresent()) {
            messageSender().send(MessageChannel.CHAT, MessageType.NORMAL, sender,
                    "Your data is available here: §6" + ButlerUpdateData.HOST + "/debug/v1/read/" + s.get());
        } else {
            messageSender().send(MessageChannel.CHAT, MessageType.ERROR, sender, "Could not send data. Please try again later");
        }
        return true;
    }
}
