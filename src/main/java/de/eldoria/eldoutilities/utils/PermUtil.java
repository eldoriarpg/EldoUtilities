package de.eldoria.eldoutilities.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;
import java.util.function.Function;

public class PermUtil {
    public static int findHighestIntPermission(Player player, String prefix, int defaultValue) {
        List<Integer> permission = findPermissions(player, prefix, true, (string) -> {
            OptionalInt optionalInt = Parser.parseInt(string);
            if (optionalInt.isPresent()) return optionalInt.getAsInt();
            return null;
        });

        int max = defaultValue;
        for (int num : permission) {
            max = Math.max(max, num);
        }
        return max;
    }

    public static double findHighestDoublePermission(Player player, String prefix, double defaultValue) {
        List<Double> permission = findPermissions(player, prefix, true, (string) -> {
            OptionalDouble optionalInt = Parser.parseDouble(string);
            if (optionalInt.isPresent()) return optionalInt.getAsDouble();
            return null;
        });

        double max = defaultValue;
        for (double num : permission) {
            max = Math.max(max, num);
        }
        return max;
    }

    public static List<String> findPermissions(Player player, String prefix, boolean truncate) {
        return findPermissions(player, prefix, truncate, s -> s);
    }

    public static <T> List<T> findPermissions(Player player, String prefix, boolean truncate, Function<String, T> parse) {
        Set<PermissionAttachmentInfo> permissions = player.getEffectivePermissions();

        List<T> matches = new ArrayList<>();

        for (PermissionAttachmentInfo permission : permissions) {
            if (!permission.getValue()) continue;
            String perm = permission.getPermission();
            if (perm.toLowerCase().startsWith(prefix)) {
                if (truncate) {
                    perm = perm.replace(prefix, "");
                }
                matches.add(parse.apply(perm));
            }
        }
        matches.removeIf(Objects::isNull);
        return matches;
    }
}
