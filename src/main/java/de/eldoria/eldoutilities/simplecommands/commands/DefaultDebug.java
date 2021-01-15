package de.eldoria.eldoutilities.simplecommands.commands;

import de.eldoria.eldoutilities.debug.DebugUtil;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.simplecommands.EldoCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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

        DebugUtil.dispatchDebug(sender, getPlugin());
        return true;
    }
}
