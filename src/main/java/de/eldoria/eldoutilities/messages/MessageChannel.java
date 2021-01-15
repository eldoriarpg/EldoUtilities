package de.eldoria.eldoutilities.messages;

import de.eldoria.eldoutilities.messages.channeldata.ChannelData;
import de.eldoria.eldoutilities.messages.channeldata.TitleData;
import de.eldoria.eldoutilities.utils.ObjUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessageChannel<T extends ChannelData> {
    /**
     * Default implementation for a chat message
     */
    public static MessageChannel<? extends ChannelData> CHAT = (message, target, sender, data) -> sender.sendMessage(target, message);

    /**
     * Default implementation for a title message
     */
    public static MessageChannel<TitleData> TITLE = (message, target, sender, data) -> {
        TitleData titleData = data;
        if (titleData == null) titleData = TitleData.DEFAULT;
        if (target instanceof Player) {
            sender.sendTitle((Player) target, message, "", titleData.getFadeIn(), titleData.getStay(), titleData.getFadeOut());
        } else {
            sender.sendMessage(target, message);
        }
    };

    /**
     * Default implementation for a subtitle message
     */
    public static MessageChannel<TitleData> SUBTITLE = (message, target, sender, data) -> {
        if (target instanceof Player) {
            sender.sendTitle((Player) target, "", message, data.getFadeIn(), data.getStay(), data.getFadeOut());
        } else {
            sender.sendMessage(target, message);
        }
    };

    /**
     * Default implementation for a action bar message
     */
    public static MessageChannel<? extends ChannelData> ACTION_BAR = (message, target, sender, data) -> {
        if (target instanceof Player) {
            sender.sendActionBar((Player) target, message);
        } else {
            sender.sendMessage(target, message);
        }
    };

    /**
     * Get a default channel by name.
     *
     * @param name name of channel not case sensitive
     * @return channel or {@link #CHAT} if channel is not found or name is null
     */
    public static @NotNull MessageChannel<? extends ChannelData> getChannelByNameOrDefault(@Nullable String name) {
        return ObjUtil.nonNull(getChannelByName(name), CHAT);
    }

    /**
     * Get a default channel by name.
     *
     * @param name name of channel not case sensitive
     * @return channel or null if name is null or no matching channel is found
     */
    public static @Nullable MessageChannel<? extends ChannelData> getChannelByName(@Nullable String name) {
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

    /**
     * Send a message via this channel to a target with the delivered message sender instance.
     *
     * @param message message to send
     * @param target  target of message
     * @param sender  message sender instance
     * @param data    Additional data for the channel
     */
    void sendMessage(String message, CommandSender target, MessageSender sender, T data);

    /**
     * Send a message via this channel to a target with the delivered message sender instance.
     *
     * @param message message to send
     * @param target  target of message
     * @param sender  message sender instance
     */
    default void sendMessage(String message, CommandSender target, MessageSender sender) {
        sendMessage(message, target, sender, null);
    }
}
