package fuzs.limitlesscontainers.impl.world.inventory;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerMenu;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedContainer;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedSimpleContainer;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedSlot;
import fuzs.limitlesscontainers.impl.init.ModRegistry;
import fuzs.limitlesscontainers.impl.world.level.block.entity.LimitlessChestBlockEntity;
import fuzs.puzzleslib.api.container.v1.ContainerMenuHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LimitlessChestMenu extends LimitlessContainerMenu {
    private final int containerRows = 6;
    private final Container container;

    public LimitlessChestMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new MultipliedSimpleContainer(8, LimitlessChestBlockEntity.CONTAINER_SIZE));
    }

    public LimitlessChestMenu(int containerId, Inventory inventory, MultipliedContainer container) {
        super(ModRegistry.LIMITLESS_CHEST_MENU.value(), containerId);
        checkContainerSize(container, this.containerRows * 9);
        this.container = container;
        container.startOpen(inventory.player);
        this.addContainerSlots(container);
        ContainerMenuHelper.addInventorySlots(this, inventory, 103 + (this.containerRows - 4) * 18);
    }

    private void addContainerSlots(MultipliedContainer container) {
        for (int l = 0; l < this.containerRows; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new MultipliedSlot(container, m + l * 9, 8 + m * 18, 18 + l * 18));
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (index < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemStack2, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }
}