package de.eldoria.eldoutilities.messages;

import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.localization.Replacement;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A message sender to manage message sending.
 * <p>
 * Allows definition of a plugin prefix, Message color, Error color.
 * <p>
 * Allows sending of messages, error messages, titles.
 * <p>
 * Allows sending of automatically localized messages in combination with a created localizer.
 *
 * @since 1.0.0
 */
public final class MessageSender {
    private static final MessageSender DEFAULT_SENDER = new MessageSender(null, "", "", "§c");
    private static final Map<String, MessageSender> PLUGIN_SENDER = new HashMap<>();
    private final Class<? extends Plugin> ownerPlugin;
    private String prefix;
    private String defaultMessageColor;
    private String defaultErrorColor;

    private MessageSender(Class<? extends Plugin> ownerPlugin, String prefix, String defaultMessageColor, String defaultErrorColor) {
        this.ownerPlugin = ownerPlugin;
        this.prefix = prefix;
        this.defaultMessageColor = defaultMessageColor;
        this.defaultErrorColor = defaultErrorColor;
    }

    /**
     * Creates a new message sender for the plugin
     *
     * @param plugin       plugin to create the message sender for
     * @param prefix       plugin prefix with color code
     * @param messageColor default message color
     * @param errorColor   default error color
     * @return message sender instance or default sender if plugin is null
     */
    public static MessageSender create(Class<? extends Plugin> plugin, String prefix, char messageColor, char errorColor) {
        return create(plugin, prefix, new char[]{messageColor}, new char[]{errorColor});
    }

    /**
     * Creates a new message sender for the plugin
     *
     * @param plugin       plugin to create the message sender for
     * @param prefix       plugin prefix with color code
     * @param messageColor default message color
     * @param errorColor   default error color
     * @return message sender instance or default sender if plugin is null
     */
    public static MessageSender create(Plugin plugin, String prefix, char messageColor, char errorColor) {
        return create(plugin.getClass(), prefix, new char[]{messageColor}, new char[]{errorColor});
    }

    /**
     * Creates a new message sender for the plugin
     *
     * @param plugin       plugin to create the message sender for
     * @param prefix       plugin prefix with color code
     * @param messageColor default message color
     * @param errorColor   default error color
     * @return message sender instance or default sender if plugin is null
     */
    public static MessageSender create(Plugin plugin, String prefix, char[] messageColor, char[] errorColor) {
        return create(plugin.getClass(), prefix, messageColor, errorColor);
    }

    /**
     * Creates a new message sender for the plugin
     *
     * @param plugin       plugin to create the message sender for
     * @param prefix       plugin prefix with color code
     * @param messageColor default message color
     * @param errorColor   default error color
     * @return message sender instance or default sender if plugin is null
     */
    public static MessageSender create(Class<? extends Plugin> plugin, String prefix, char[] messageColor, char[] errorColor) {
        if (plugin == null) return DEFAULT_SENDER;

        StringBuilder builder = new StringBuilder("§r");
        for (char aChar : messageColor) {
            builder.append("§").append(aChar);
        }
        String defMessageColor = builder.toString();

        builder.setLength(0);
        builder.append("§r");
        for (char aChar : errorColor) {
            builder.append("§").append(aChar);
        }
        String defErrorColor = builder.toString();

        PLUGIN_SENDER.compute(plugin.getName(),
                (k, v) -> (v == null)
                        ? new MessageSender(plugin, prefix.trim() + " ", defMessageColor, defErrorColor)
                        : v.update(prefix, defMessageColor, defErrorColor));
        return PLUGIN_SENDER.get(plugin.getName());
    }

    /**
     * Get the message sender created for this plugin.
     *
     * @param plugin plugin
     * @return message sender of plugin or default sender if plugin is null
     */
    public static MessageSender getPluginMessageSender(@Nullable Plugin plugin) {
        if (plugin == null) return DEFAULT_SENDER;
        return getPluginMessageSender(plugin.getClass());
    }

    /**
     * Get the message sender created for this plugin.
     *
     * @param plugin plugin
     * @return message sender of plugin or default sender if plugin is null
     */
    public static MessageSender getPluginMessageSender(@Nullable Class<? extends Plugin> plugin) {
        return plugin == null ? DEFAULT_SENDER
                : PLUGIN_SENDER.getOrDefault(plugin.getName(), DEFAULT_SENDER);
    }

    private MessageSender update(String prefix, String defaultMessageColor, String defaultErrorColor) {
        this.prefix = prefix;
        this.defaultMessageColor = defaultMessageColor;
        this.defaultErrorColor = defaultErrorColor;
        return this;
    }


    /**
     * Send a message to a sender
     *
     * @param sender  receiver of the message
     * @param message message with optinal color codes
     */
    public void sendMessage(CommandSender sender, String message) {
        if (!(sender instanceof Player)) {
            sendMessage(null, message);
            return;
        }
        sendMessage((Player) sender, message);
    }

    /**
     * Send a message to a player
     *
     * @param player  receiver of the message
     * @param message message with optinal color codes
     */
    public void sendMessage(Player player, String message) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage("[INFO]" + forceMessageColor(message));
            return;
        }
        player.sendMessage(prefix + forceMessageColor(message));
    }

    private String forceMessageColor(String message) {
        return forceColor(message, defaultMessageColor);
    }

    private String forceErrorColor(String message) {
        return forceColor(message, defaultErrorColor);
    }

    private String forceColor(String message, String defaultColor) {
        String repMessage = message.replaceAll("§r", defaultColor);
        return defaultColor + repMessage;
    }

    /**
     * Sends a error to a sender
     *
     * @param sender  receiver of the message
     * @param message message with optinal color codes
     */
    public void sendError(CommandSender sender, String message) {
        if (!(sender instanceof Player)) {
            sendError(null, message);
            return;
        }
        sendError((Player) sender, message);
    }

    /**
     * Sends a error to a player
     *
     * @param player  receiver of the message
     * @param message message with optinal color codes
     */
    public void sendError(Player player, String message) {
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage("[INFO]" + forceErrorColor(message));
            return;
        }
        player.sendMessage(prefix + forceErrorColor(message));
    }

    /**
     * Send a message to a sender
     * <p>
     * The message will be localized.
     * <p>
     * The message can be a simple locale code in the format "code" or "code.code....".
     * <p>
     * If multiple code should be used every code musst be surrounded by a {@code $} mark. Example {@code "$code.code$
     * and $code.more.code$}. You can write what you want between locale codes.
     *
     * @param sender       receiver of the message
     * @param message      message with optinal color codes
     * @param replacements replacements to apply on the message
     */
    public void sendLocalizedMessage(CommandSender sender, String message, Replacement... replacements) {
        sendMessage(sender, loc().localize(message, replacements));
    }

    /**
     * Sends a error to a sender
     * <p>
     * The message will be localized.
     * <p>
     * The message can be a simple locale code in the format "code" or "code.code....".
     * <p>
     * If multiple code should be used every code musst be surrounded by a {@code $} mark. Example {@code "$code.code$
     * and $code.more.code$}. You can write what you want between locale codes.
     *
     * @param sender       receiver of the message
     * @param message      message with optinal color codes
     * @param replacements replacements to apply on the message
     */
    public void sendLocalizedError(CommandSender sender, String message, Replacement... replacements) {
        sendError(sender, loc().localize(message, replacements));
    }

    @Deprecated
    public void sendTitle(Player player, String defaultColor, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        String repTitle = title.replaceAll("§r", "§r" + defaultColor);
        String repSubTitle = subtitle.replaceAll("§r", "§r" + defaultColor);
        sendTitle(player, repTitle, repSubTitle, fadeIn, stay, fadeOut);
    }

    @Deprecated
    public void sendTitle(Player player, String defaultColor, String title, String subtitle) {
        sendTitle(player, forceColor(title, defaultColor), forceColor(subtitle, defaultColor));
    }

    /**
     * Send a title to a player
     *
     * @param player   player to send
     * @param title    title to send
     * @param subtitle subtitle to send
     * @param fadeIn   fade in time of title
     * @param stay     stay time of title
     * @param fadeOut  fade out time of title
     */
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * Send a title to a player
     *
     * @param player   player to send
     * @param title    title to send
     * @param subtitle subtitle to send
     */
    public void sendTitle(Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 50, 20);
    }

    /**
     * Send a localized title to a player
     *
     * @param player       player to send
     * @param defaultColor default color of message
     * @param title        title to send
     * @param subtitle     subtitle to send
     * @param fadeIn       fade in time of title
     * @param stay         stay time of title
     * @param fadeOut      fade out time of title
     * @param replacements replacements for the localized message
     * @deprecated Default colors should not be used anymore. Use {@link #sendLocalizedTitle(Player, String, String, int, int, int, Replacement...)} instead.
     */
    @Deprecated
    public void sendLocalizedTitle(Player player, String defaultColor, String title, String subtitle, int fadeIn, int stay, int fadeOut, Replacement... replacements) {
        sendTitle(player, defaultColor, loc().localize(title, replacements), loc().localize(subtitle, replacements), fadeIn, stay, fadeOut);
    }

    /**
     * Send a localized title to a player
     *
     * @param player       player to send
     * @param title        title to send
     * @param subtitle     subtitle to send
     * @param fadeIn       fade in time of title
     * @param stay         stay time of title
     * @param fadeOut      fade out time of title
     * @param replacements replacements for the localized message
     */
    public void sendLocalizedTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut, Replacement... replacements) {
        sendTitle(player, loc().localize(title, replacements), loc().localize(subtitle, replacements), fadeIn, stay, fadeOut);
    }

    /**
     * Send a localized title to a player
     *
     * @param player       player to send
     * @param defaultColor default color of message
     * @param title        title to send
     * @param subtitle     subtitle to send
     * @param replacements replacements for the localized message
     * @deprecated Default colors should not be used anymore. Use {@link #sendLocalizedTitle(Player, String, String, Replacement...)} instead.
     */
    @Deprecated
    public void sendLocalizedTitle(Player player, String defaultColor, String title, String subtitle, Replacement... replacements) {
        sendTitle(player, defaultColor, loc().localize(title, replacements), loc().localize(subtitle, replacements));
    }

    /**
     * Send a localized title to a player
     *
     * @param player       player to send
     * @param title        title to send
     * @param subtitle     subtitle to send
     * @param replacements replacements for the localized message
     */
    public void sendLocalizedTitle(Player player, @Nullable String title, @Nullable String subtitle, Replacement... replacements) {
        sendTitle(player, loc().localize(title, replacements), loc().localize(subtitle, replacements));
    }

    /**
     * Send a localized action bar to a player
     *
     * @param player       player to send
     * @param message      message to send
     * @param replacements replacements for the localized message
     */
    public void sendLocalizedActionBar(Player player, String message, Replacement... replacements) {
        sendActionBar(player, loc().localize(message, replacements));
    }

    /**
     * Send a message to a player Action bar.
     *
     * @param player  player to send
     * @param message message to send
     */
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(forceMessageColor(message)));
    }

    private ILocalizer loc() {
        return ILocalizer.getPluginLocalizer(ownerPlugin);
    }

    /**
     * Send a localized message via a channel.
     *
     * @param channel      channel which should be used
     * @param type         type of message
     * @param sender       target of message
     * @param message      message locale codes
     * @param replacements replacements for messages in locale codes
     * @since 1.2.1
     */
    public void sendLocalized(MessageChannel channel, MessageType type, CommandSender sender, String message, Replacement... replacements) {
        send(channel, type, sender, loc().localize(message, replacements));
    }

    /**
     * Sends a message via a channel
     *
     * @param channel channel which should be used
     * @param type    type of message
     * @param target  target of message
     * @param message message locale codes
     * @since 1.2.1
     */
    public void send(MessageChannel channel, MessageType type, CommandSender target, String message) {
        String coloredMessage = type.forceColor(message);

        channel.sendMessage(coloredMessage, target, this);
    }

    public boolean isDefault() {
        return ownerPlugin == null;
    }
}
