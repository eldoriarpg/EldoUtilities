package de.eldoria.eldoutilities.messages;

import de.eldoria.eldoutilities.utils.ObjUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessageChannel {
    /**
     * Default implementation for a chat message
     */
    public static MessageChannel CHAT = (message, target, sender) -> sender.sendMessage(target, message);

    /**
     * Default implementation for a title message
     */
    public static MessageChannel TITLE = (message, target, sender) -> {
        if (target instanceof Player) {
            sender.sendTitle((Player) target, message, null);
        } else {
            sender.sendMessage(target, message);
        }
    };

    /**
     * Default implementation for a subtitle message
     */
    public static MessageChannel SUBTITLE = (message, target, sender) -> {
        if (target instanceof Player) {
            sender.sendTitle((Player) target, null, message);
        } else {
            sender.sendMessage(target, message);
        }
    };

    /**
     * Default implementation for a action bar message
     */
    public static MessageChannel ACTION_BAR = (message, target, sender) -> {
        if (target instanceof Player) {
            sender.sendActionBar((Player) target, message);
        } else {
            sender.sendMessage(target, message);
        }
    };

    /**
     * Send a message via this channel to a target with the delivered message sender instance.
     *
     * @param message message to send
     * @param target  target of message
     * @param sender  message sender instance
     */
    void sendMessage(String message, CommandSender target, MessageSender sender);

    /**
     * Get a default channel by name.
     *
     * @param name name of channel not case sensitive
     * @return channel or {@link #CHAT} if channel is not found or name is null
     */
    public static @NotNull MessageChannel getChannelByNameOrDefault(@Nullable String name) {
        return ObjUtil.nonNull(getChannelByName(name), CHAT);
    }

    /**
     * Get a default channel by name.
     *
     * @param name name of channel not case sensitive
     * @return channel or null if name is null or no matching channel is found
     */
    public static @Nullable MessageChannel getChannelByName(@Nullable String name) {
        if ("CHAT".equalsIgnoreCase(name)) {
            return CHAT;
        }

        if ("TITLE".equalsIgnoreCase(name)) {
            return TITLE;
        }

        if ("SUBTITLE".equalsIgnoreCase(name)) {
            return SUBTITLE;
        }

        if ("ACTION_BAR".equalsIgnoreCase(name)) {
            return ACTION_BAR;
        }

        return null;
    }
}
