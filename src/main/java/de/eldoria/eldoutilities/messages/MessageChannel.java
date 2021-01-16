package de.eldoria.eldoutilities.messages;

import de.eldoria.eldoutilities.messages.channeldata.ChannelData;
import de.eldoria.eldoutilities.messages.channeldata.TitleData;
import de.eldoria.eldoutilities.utils.ObjUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MessageChannel<T extends ChannelData> {
    /**
     * Default implementation for a chat message
     */
    public static MessageChannel<? extends ChannelData> CHAT = (message, target, data) -> target.sendMessage(message);

    /**
     * Default implementation for a title message
     */
    public static MessageChannel<TitleData> TITLE = (message, target, data) -> {
        TitleData titleData = data;
        if (titleData == null) titleData = TitleData.DEFAULT;
        if (target instanceof Player) {
            ((Player) target).sendTitle(message, titleData.getOtherLine(), titleData.getFadeIn(), titleData.getStay(), titleData.getFadeOut());
        } else {
            target.sendMessage(message);
        }
    };

    /**
     * Default implementation for a subtitle message
     */
    public static MessageChannel<TitleData> SUBTITLE = (message, target, data) -> {
        TitleData titleData = data;
        if (titleData == null) titleData = TitleData.DEFAULT;
        if (target instanceof Player) {
            ((Player) target).sendTitle(titleData.getOtherLine(), message, titleData.getFadeIn(), titleData.getStay(), titleData.getFadeOut());
        } else {
            target.sendMessage(message);
        }
    };

    /**
     * Default implementation for a action bar message
     */
    public static MessageChannel<? extends ChannelData> ACTION_BAR = (message, target, data) -> {
        if (target instanceof Player) {
            ((Player) target).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        } else {
            target.sendMessage(message);
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
     * @param data    Additional data for the channel
     */
    void sendMessage(String message, CommandSender target, T data);

    /**
     * Send a message via this channel to a target with the delivered message sender instance.
     *
     * @param message message to send
     * @param target  target of message
     */
    default void sendMessage(String message, CommandSender target) {
        sendMessage(message, target, null);
    }
}
