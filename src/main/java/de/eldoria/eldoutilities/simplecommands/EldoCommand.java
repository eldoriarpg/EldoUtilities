package de.eldoria.eldoutilities.simplecommands;

import de.eldoria.eldoutilities.localization.Localizer;
import de.eldoria.eldoutilities.localization.Replacement;
import de.eldoria.eldoutilities.messages.MessageSender;
import de.eldoria.eldoutilities.utils.ArrayUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class EldoCommand implements TabExecutor {
    private final Map<String, TabExecutor> subCommands = new HashMap<>();
    private String[] registeredCommands = new String[0];
    private TabExecutor defaultCommand = null;
    private final Localizer localizer;
    private final MessageSender messageSender;

    public EldoCommand(Localizer localizer, MessageSender messageSender) {
        this.localizer = localizer;
        this.messageSender = messageSender;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (defaultCommand != null) {
                return defaultCommand.onCommand(sender, command, label, args);
            }
            return true;
        }

        final String[] newArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

        return getCommand(args[0]).map(c -> c.onCommand(sender, command, label, newArgs)).orElse(false);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return ArrayUtil.startingWithInArray(args[0], registeredCommands).collect(Collectors.toList());
        }
        final String[] newArgs = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];

        if (args.length == 0) return Collections.emptyList();

        return getCommand(args[0]).map(c -> c.onTabComplete(sender, command, alias, newArgs))
                .orElse(Collections.singletonList("invalid command"));
    }

    private Optional<TabExecutor> getCommand(String command) {
        for (Map.Entry<String, TabExecutor> entry : subCommands.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(command)) {
                return Optional.ofNullable(entry.getValue());
            }
        }
        return Optional.empty();
    }

    /**
     * Registers a command as sub command.
     *
     * @param command  name of the command.
     * @param executor executor
     */
    public void registerCommand(String command, TabExecutor executor) {
        subCommands.put(command, executor);
        registeredCommands = new String[subCommands.size()];
        subCommands.keySet().toArray(registeredCommands);
    }

    /**
     * Sets the default command of not arguments are present to determine a subcommand.
     *
     * @param defaultCommand executor for default command
     */
    public void setDefaultCommand(TabExecutor defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    /**
     * Get a instance of the localizer.
     *
     * @return localizer instance
     */
    protected Localizer localizer() {
        return localizer;
    }

    /**
     * Get a instance of the message sender.
     *
     * @return message sender instance
     */
    protected MessageSender messageSender() {
        return messageSender;
    }

    /**
     * Checks if the provided arguments are invalid.
     *
     * @param sender user which executed the command.
     * @param args   arguments to check
     * @param length min amount of arguments.
     * @param syntax correct syntax
     * @return true if the arguments are invalid
     */
    protected boolean argumentsInvalid(CommandSender sender, String[] args, int length, String syntax) {
        return argumentsInvalid(sender, messageSender, localizer, args, length, syntax);
    }

    /**
     * Checks if the provided arguments are invalid.
     *
     * @param sender        user which executed the command.
     * @param messageSender message sender for calling home.
     * @param localizer     localizer for localization stuff.
     * @param args          arguments to check
     * @param length        min amount of arguments.
     * @param syntax        correct syntax
     * @return true if the arguments are invalid
     */
    protected static boolean argumentsInvalid(CommandSender sender, MessageSender messageSender, Localizer localizer, String[] args, int length, String syntax) {
        if (args.length < length) {
            messageSender.sendError(sender, localizer.getMessage("error.invalidArguments",
                    Replacement.create("SYNTAX", syntax).addFormatting('6')));
            return true;
        }
        return false;
    }

    /**
     * Checks if the user has at least one of the provided permissions.
     * Will send a message to the user if a it lacks permission.
     *
     * @param actor       actor which wants to execute this action
     * @param permissions one or more permissions to check
     * @return true if the user has no of the required permission
     */
    protected boolean denyAccess(CommandSender actor, String... permissions) {
        return denyAccess(actor, false, permissions);
    }

    /**
     * Checks if the user has at least one of the provided permissions.
     *
     * @param actor       actor which wants to execute this action
     * @param silent      set to true if no message should be send to the player
     * @param permissions one or more permissions to check
     * @return true if the user has no of the required permission
     */
    protected boolean denyAccess(CommandSender actor, boolean silent, String... permissions) {
        if (actor == null) {
            return false;
        }

        Player player = null;

        if (actor instanceof Player) {
            player = (Player) actor;
        }

        if (player == null) {
            return false;
        }
        for (String permission : permissions) {
            if (player.hasPermission(permission)) {
                return false;
            }
        }
        if (!silent) {
            messageSender.sendMessage(player,
                    localizer.getMessage("error.permission",
                            Replacement.create("PERMISSION", String.join(", ", permissions)).addFormatting('6')));
        }
        return true;
    }

    protected Player getPlayerFromSender(CommandSender sender) {
        return sender instanceof Player ? (Player) sender : null;
    }

    protected boolean isConsole(CommandSender sender) {
        return (sender instanceof ConsoleCommandSender);
    }

    protected boolean isPlayer(CommandSender sender) {
        return (sender instanceof Player);
    }

    protected boolean invalidRange(CommandSender sender, double value, double min, double max) {
        if (value > max || value < min) {
            messageSender.sendError(sender, localizer.getMessage("error.invalidRange",
                    Replacement.create("MIN", min).addFormatting('6'),
                    Replacement.create("MAX", max).addFormatting('6')));
            return true;
        }
        return false;
    }

    protected <T extends Enum<T>> boolean invalidEnumValue(CommandSender sender, T value, Class<T> clazz) {
        if(value == null){
            messageSender.sendError(sender, localizer.getMessage("error.invalidEnumValue",
                    Replacement.create("VALUES",
                            Arrays.stream(clazz.getEnumConstants())
                                    .map(e -> e.name().toLowerCase())
                                    .collect(Collectors.joining(" ")))
                            .addFormatting('6')));
            return true;
        }
        return false;
    }

    protected boolean invalidRange(CommandSender sender, int value, int min, int max) {
        return invalidRange(sender, (double) value, min, max);
    }
}