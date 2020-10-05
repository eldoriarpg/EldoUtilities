package de.eldoria.eldoutilities.messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class MessageSender {

    private final Plugin plugin;
    private String prefix;
    private String defaultMessageColor;
    private String defaultErrorColor;
    private static final MessageSender DEFAULT_SENDER = new MessageSender(null, "", "", "§c");

    private static final Map<String, MessageSender> PLUGIN_SENDER = new HashMap<>();

    private MessageSender(Plugin plugin, String prefix, String defaultMessageColor, String defaultErrorColor) {
        this.plugin = plugin;
        this.prefix = prefix;
        this.defaultMessageColor = defaultMessageColor;
        this.defaultErrorColor = defaultErrorColor;
    }

    public static MessageSender create(Plugin plugin, String prefix, char defaultMessageColor, char defaultErrorColor) {
        return create(plugin, prefix, new char[] {defaultMessageColor}, new char[] {defaultErrorColor});
    }

    public static MessageSender create(Plugin plugin, String prefix, char[] messageColor, char[] errorColor) {
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

        PLUGIN_SENDER.compute(plugin.getDescription().getName(),
                (k, v) -> (v == null)
                        ? new MessageSender(plugin, prefix, defMessageColor, defErrorColor)
                        : v.update(prefix, defMessageColor, defErrorColor));
        return PLUGIN_SENDER.get(plugin.getDescription().getName());
    }

    private MessageSender update(String prefix, String defaultMessageColor, String defaultErrorColor) {
        this.prefix = prefix;
        this.defaultMessageColor = defaultMessageColor;
        this.defaultErrorColor = defaultErrorColor;
        return this;
    }

    public static MessageSender get(Plugin plugin) {
        return plugin == null ? DEFAULT_SENDER
                : PLUGIN_SENDER.getOrDefault(plugin.getDescription().getName(), DEFAULT_SENDER);
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
        String s = message.replaceAll("§r", defaultMessageColor);
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage("[INFO]" + defaultMessageColor + s);
            return;
        }
        player.sendMessage(prefix + defaultMessageColor + s);
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
        String s = message.replaceAll("§r", defaultErrorColor);
        if (player == null) {
            Bukkit.getConsoleSender().sendMessage("[INFO]" + defaultMessageColor + s);
            return;
        }
        player.sendMessage(prefix + defaultErrorColor + s);
    }
}
