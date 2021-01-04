package de.eldoria.eldoutilities.inventory;

import de.eldoria.eldoutilities.utils.ObjUtil;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class InventoryActions {
    private final Map<Integer, ActionItem> actions = new HashMap<>();
    private final Inventory inventory;
    private final Consumer<InventoryCloseEvent> onClose;

    private InventoryActions(Inventory inventory, Consumer<InventoryCloseEvent> onClose) {
        this.inventory = inventory;
        this.onClose = onClose;
    }

    public static InventoryActions of(Inventory inventory) {
        return new InventoryActions(inventory, s -> {
        });
    }

    public static InventoryActions of(Inventory inventory, Consumer<InventoryCloseEvent> onClose) {
        return new InventoryActions(inventory, onClose);
    }

    /**
     * Adds an action to the inventory actions.
     * <p>
     * Accoding to the documentation of {@link ActionItem} the item stack will be added at the given pos to the inventory.
     *
     * @param action action to add
     */
    public void addAction(ActionItem action) {
        inventory.setItem(action.getSlot(), action.getItemStack());
        actions.put(action.getSlot(), action);
    }

    public void onInventoryClose(InventoryCloseEvent event) {
        for (Map.Entry<Integer, ActionItem> entry : actions.entrySet()) {
            entry.getValue().onInventoryClose(inventory.getItem(entry.getKey()));
        }
        onClose.accept(event);
    }

    public void onInventoryClick(InventoryClickEvent event) {
        ObjUtil.nonNull(actions.get(event.getSlot()), (Consumer<ActionItem>) s -> s.onInventoryClick(event));
    }
}
