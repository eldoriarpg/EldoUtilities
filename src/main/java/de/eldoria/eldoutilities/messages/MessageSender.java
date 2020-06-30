package de.eldoria.eldoutilities.messages;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class MessageSender {

    private String prefix;
    private String defaultMessageColor;
    private String defaultErrorColor;
    private static final MessageSender DEFAULT_SENDER = new MessageSender("", "", "§c");

    private static final Map<String, MessageSender> PLUGIN_SENDER = new HashMap<>();

    private MessageSender(String prefix, String defaultMessageColor, String defaultErrorColor) {
        this.prefix = prefix;
        this.defaultMessageColor = defaultMessageColor;
        this.defaultErrorColor = defaultErrorColor;
    }

    public static MessageSender create(Plugin plugin, String prefix, char defaultMessageColor, char defaultErrorColor) {
        return create(plugin, prefix, new char[] {defaultMessageColor}, new char[] {defaultErrorColor});
    }

    public static MessageSender create(Plugin plugin, String prefix, char[] messageColor, char[] errorColor) {
        if(plugin == null) return DEFAULT_SENDER;

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
                        ? new MessageSender(prefix, defMessageColor, defErrorColor)
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
     * Send a message to a player
     *
     * @param player  receiver of the message
     * @param message message with optinal color codes
     */
    public void sendMessage(Player player, String message) {
        player.sendMessage(prefix + defaultMessageColor + message.replaceAll("§r", defaultMessageColor));
    }

    /**
     * Sends a error to a player
     *
     * @param player  receiver of the message
     * @param message message with optinal color codes
     */
    public void sendError(Player player, String message) {
        player.sendMessage(prefix + defaultMessageColor + message.replaceAll("§r", defaultErrorColor));
    }
}
