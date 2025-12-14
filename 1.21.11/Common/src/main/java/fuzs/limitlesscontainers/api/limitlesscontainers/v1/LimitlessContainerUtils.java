package fuzs.limitlesscontainers.api.limitlesscontainers.v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.container.v1.ContainerSerializationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.OptionalInt;
import java.util.Set;

public class LimitlessContainerUtils {
    /**
     * @see ItemStack#MAP_CODEC
     */
    public static final MapCodec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Item.CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
            ExtraCodecs.POSITIVE_INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY)
                    .forGetter(ItemStack::getComponentsPatch)).apply(instance, ItemStack::new));
    /**
     * @see ItemStackWithSlot#CODEC
     */
    public static final Codec<ItemStackWithSlot> ITEM_STACK_WITH_SLOT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.UNSIGNED_BYTE.fieldOf("Slot").orElse(0).forGetter(ItemStackWithSlot::slot),
            ITEM_STACK_CODEC.forGetter(ItemStackWithSlot::stack)).apply(instance, ItemStackWithSlot::new));

    public static void saveAllItems(ValueOutput valueOutput, NonNullList<ItemStack> itemStacks) {
        ContainerSerializationHelper.storeAsSlots(itemStacks, valueOutput.list("Items", ITEM_STACK_WITH_SLOT_CODEC));
    }

    public static void loadAllItems(ValueInput valueInput, NonNullList<ItemStack> itemStacks) {
        ContainerSerializationHelper.fromSlots(itemStacks, valueInput.listOrEmpty("Items", ITEM_STACK_WITH_SLOT_CODEC));
    }

    public static void dropContents(Level level, BlockPos pos, Container inventory) {
        dropContents(level, pos.getX(), pos.getY(), pos.getZ(), inventory);
    }

    private static void dropContents(Level level, double x, double y, double z, Container inventory) {
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack item = inventory.getItem(i);
            dropItemStack(level, x, y, z, item);
        }
    }

    public static void dropItemStack(Level level, double x, double y, double z, ItemStack stack) {
        double d = EntityType.ITEM.getWidth();
        double e = 1.0 - d;
        double f = d / 2.0;
        double g = Math.floor(x) + level.random.nextDouble() * e + f;
        double h = Math.floor(y) + level.random.nextDouble() * e;
        double i = Math.floor(z) + level.random.nextDouble() * e + f;

        while (!stack.isEmpty()) {
            // don't split stacks into smaller parts like vanilla, keep them as big as possible
            ItemEntity itemEntity = new ItemEntity(level, g, h, i, stack.split(stack.getMaxStackSize()));
            // remove any motion, will help with lag
            itemEntity.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(itemEntity);
        }
    }

    public static int getMaxStackSizeOrDefault(ItemStack stack, int stackSizeMultiplier) {
        return getMaxStackSize(stack, stackSizeMultiplier).orElseGet(stack::getMaxStackSize);
    }

    public static OptionalInt getMaxStackSize(ItemStack stack, int stackSizeMultiplier) {
        return stack.getMaxStackSize() > 1 || !stack.isDamageableItem() ?
                OptionalInt.of(stack.getMaxStackSize() * stackSizeMultiplier) : OptionalInt.empty();
    }

    public static int getQuickCraftPlaceCount(Set<Slot> slots, int dragMode, ItemStack itemStack, Slot slot) {
        return switch (dragMode) {
            case 0 -> Mth.floor((float) itemStack.getCount() / slots.size());
            case 1 -> 1;
            case 2 -> slot.getMaxStackSize(itemStack);
            default -> itemStack.getCount();
        };
    }

    public static boolean canItemQuickReplace(@Nullable Slot slot, ItemStack stack, boolean stackSizeMatters) {
        boolean bl = slot == null || !slot.hasItem();
        if (!bl && ItemStack.isSameItemSameComponents(stack, slot.getItem())) {
            return slot.getItem().getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= slot.getMaxStackSize(stack);
        } else {
            return bl;
        }
    }

    public static int getRedstoneSignalFromBlockEntity(@Nullable BlockEntity blockEntity) {
        return blockEntity instanceof MultipliedContainer container ? getRedstoneSignalFromContainer(container) : 0;
    }

    public static int getRedstoneSignalFromContainer(@Nullable MultipliedContainer container) {
        if (container == null) {
            return 0;
        } else {
            int i = 0;
            float f = 0.0F;

            for (int j = 0; j < container.getContainerSize(); ++j) {
                ItemStack itemStack = container.getItem(j);
                if (!itemStack.isEmpty()) {
                    f += (float) itemStack.getCount() / Math.min(container.getMaxStackSize(),
                            getMaxStackSizeOrDefault(itemStack, container.getStackSizeMultiplier()));
                    ++i;
                }
            }

            f /= (float) container.getContainerSize();
            return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
        }
    }

    public static void dropOrPlaceInInventory(Player player, ItemStack itemStack) {
        boolean bl = player.isRemoved() && player.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;
        boolean bl2 = player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected();
        if (bl || bl2) {
            drop(player, itemStack, false);
        } else if (player instanceof ServerPlayer) {
            placeItemBackInInventory(player.getInventory(), itemStack);
        }
    }

    public static void placeItemBackInInventory(Inventory inventory, ItemStack itemStack) {
        while (itemStack.getCount() > itemStack.getMaxStackSize()) {
            inventory.placeItemBackInInventory(itemStack.split(itemStack.getMaxStackSize()));
        }
        inventory.placeItemBackInInventory(itemStack);
    }

    public static void drop(Player player, ItemStack itemStack, boolean includeThrowerName) {
        while (itemStack.getCount() > itemStack.getMaxStackSize()) {
            player.drop(itemStack.split(itemStack.getMaxStackSize()), includeThrowerName);
        }
        player.drop(itemStack, includeThrowerName);
    }
}
