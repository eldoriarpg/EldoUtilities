package de.eldoria.eldoutilities.messages;

import de.eldoria.eldoutilities.EldoUtil;
import de.eldoria.eldoutilities.localization.ILocalizer;
import de.eldoria.eldoutilities.localization.Replacement;
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

public final class MessageSender {
    private static final Pattern LOCALIZATION_CODE = Pattern.compile("\\$([a-zA-Z.]+?)\\$");
    private static final MessageSender DEFAULT_SENDER = new MessageSender(null, "", "", "§c");
    private static final Map<String, MessageSender> PLUGIN_SENDER = new HashMap<>();
    private Class<? extends Plugin> ownerPlugin;
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
     *
     * @return message sender instance or default sender if plugin is null
     */
    public static MessageSender create(Class<? extends Plugin> plugin, String prefix, char messageColor, char errorColor) {
        return create(plugin, prefix, new char[] {messageColor}, new char[] {errorColor});
    }

    /**
     * Creates a new message sender for the plugin
     *
     * @param plugin       plugin to create the message sender for
     * @param prefix       plugin prefix with color code
     * @param messageColor default message color
     * @param errorColor   default error color
     *
     * @return message sender instance or default sender if plugin is null
     */
    public static MessageSender create(Plugin plugin, String prefix, char messageColor, char errorColor) {
        return create(plugin.getClass(), prefix, new char[] {messageColor}, new char[] {errorColor});
    }

    /**
     * Creates a new message sender for the plugin
     *
     * @param plugin       plugin to create the message sender for
     * @param prefix       plugin prefix with color code
     * @param messageColor default message color
     * @param errorColor   default error color
     *
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
     *
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
     *
     * @return message sender of plugin or default sender if plugin is null
     */
    public static MessageSender get(@Nullable Plugin plugin) {
        if (plugin == null) return DEFAULT_SENDER;
        return get(plugin.getClass());
    }

    /**
     * Get the message sender created for this plugin.
     *
     * @param plugin plugin
     *
     * @return message sender of plugin or default sender if plugin is null
     */
    public static MessageSender get(@Nullable Class<? extends Plugin> plugin) {
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
        String repMessage = message.replaceAll("§r", defaultMessageColor);
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage("[INFO]" + defaultMessageColor + repMessage);
            return;
        }
        player.sendMessage(prefix + defaultMessageColor + repMessage);
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
        String repMessage = message.replaceAll("§r", defaultErrorColor);
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage("[INFO]" + defaultMessageColor + repMessage);
            return;
        }
        player.sendMessage(prefix + defaultErrorColor + repMessage);
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
     * @param sender  receiver of the message
     * @param message message with optinal color codes
     */
    public void sendLocalizedMessage(CommandSender sender, String message, Replacement... replacements) {
        sendMessage(sender, localize(message, replacements));
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
     * @param sender  receiver of the message
     * @param message message with optinal color codes
     */
    public void sendLocalizedError(CommandSender sender, String message, Replacement... replacements) {
        sendError(sender, localize(message, replacements));
    }

    public void sendTitle(Player player, String defaultColor, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        String repTitle = title.replaceAll("§r", "§r" + defaultColor);
        String repSubTitle = subtitle.replaceAll("§r", "§r" + defaultColor);
        player.sendTitle(repTitle, repSubTitle, fadeIn, stay, fadeOut);
    }

    public void sendTitle(Player player, String defaultColor, String title, String subtitle) {
        sendTitle(player, defaultColor, title, subtitle, 10, 70, 20);
    }

    public void sendLocalizedTitle(Player player, String defaultColor, String title, String subtitle, int fadeIn, int stay, int fadeOut, Replacement... replacements) {
        sendTitle(player, defaultColor, localize(title, replacements), localize(subtitle, replacements), fadeIn, stay, fadeOut);
    }

    public void sendLocalizedTitle(Player player, String defaultColor, String title, String subtitle, Replacement... replacements) {
        sendLocalizedTitle(player, defaultColor, title, subtitle, 10, 70, 20, replacements);
    }

    private ILocalizer loc() {
        return ILocalizer.getPluginLocalizer(ownerPlugin);
    }

    /**
     * Translates a String with Placeholders. Can handle multiple messages with replacements. Add replacements in the
     * right order.
     *
     * @param message      Message to translate
     * @param replacements Replacements in the right order.
     *
     * @return Replaced Messages
     */
    private String localize(String message, Replacement[] replacements) {
        if (message == null) {
            return null;
        }

        // If the matcher doesn't find any key we assume its a simple message.
        if (!LOCALIZATION_CODE.matcher(message).find()) {
            return loc().getMessage(message, replacements);
        }

        // find locale codes in message
        Matcher matcher = LOCALIZATION_CODE.matcher(message);
        List<String> keys = new ArrayList<>();
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }


        String result = message;
        for (String match : keys) {
            //Replace current locale code with result
            result = result.replace("$" + match + "$", loc().getMessage(match, replacements));
        }
        return result;
    }
}
