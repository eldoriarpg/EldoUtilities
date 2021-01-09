package de.eldoria.eldoutilities.inventory;

import de.eldoria.eldoutilities.items.ItemStackBuilder;
import de.eldoria.eldoutilities.utils.DataContainerUtil;
import de.eldoria.eldoutilities.utils.EMath;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class ActionConsumer {
    public static Consumer<InventoryClickEvent> getIntRange(NamespacedKey key, int min, int max) {
        return clickEvent -> {
            int amount = 0;
            switch (clickEvent.getClick()) {
                case LEFT:
                    amount = 1;
                    break;
                case SHIFT_LEFT:
                    amount = 10;
                    break;
                case RIGHT:
                    amount = -1;
                    break;
                case SHIFT_RIGHT:
                    amount = -10;
                    break;
                case WINDOW_BORDER_LEFT:
                case WINDOW_BORDER_RIGHT:
                case MIDDLE:
                case NUMBER_KEY:
                case DOUBLE_CLICK:
                case DROP:
                case CONTROL_DROP:
                case CREATIVE:
                case SWAP_OFFHAND:
                case UNKNOWN:
                    return;
            }

            int finalAmount = amount;
            int curr = DataContainerUtil.compute(clickEvent.getCurrentItem(), key, PersistentDataType.INTEGER,
                    integer -> EMath.clamp(min, max, integer + finalAmount));
            ItemStackBuilder.of(clickEvent.getCurrentItem(), false).withLore(String.valueOf(curr));
        };
    }

    public static Consumer<InventoryClickEvent> booleanToggle(NamespacedKey key) {
        return clickEvent -> {
            switch (clickEvent.getClick()) {
                case LEFT:
                case SHIFT_LEFT:
                case RIGHT:
                case SHIFT_RIGHT:
                    break;
                case WINDOW_BORDER_LEFT:
                case WINDOW_BORDER_RIGHT:
                case MIDDLE:
                case NUMBER_KEY:
                case DOUBLE_CLICK:
                case DROP:
                case CONTROL_DROP:
                case CREATIVE:
                case SWAP_OFFHAND:
                case UNKNOWN:
                    return;
            }

            Byte curr = DataContainerUtil.compute(
                    clickEvent.getCurrentItem(),
                    key,
                    PersistentDataType.BYTE,
                    aByte -> DataContainerUtil.booleanToByte(!DataContainerUtil.byteToBoolean(aByte)));
            boolean b = DataContainerUtil.byteToBoolean(curr);
            ItemStackBuilder.of(clickEvent.getCurrentItem(), false).withLore(b ? "§2true" : "§cfalse");
        };
    }
}

