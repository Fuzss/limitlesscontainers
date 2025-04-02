package fuzs.limitlesscontainers.api.limitlesscontainers.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;

public class LimitlessContainerUtils {

    public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items, boolean saveEmpty, HolderLookup.Provider registries) {
        ListTag list = saveAllItems(items::get, items.size(), registries);

        if (!list.isEmpty() || saveEmpty) {
            tag.put("Items", list);
        }

        return tag;
    }

    public static ListTag saveAllItems(IntFunction<ItemStack> extractor, int containerSize, HolderLookup.Provider registries) {
        ListTag list = new ListTag();

        for (int i = 0; i < containerSize; ++i) {
            ItemStack itemStack = extractor.apply(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                compoundTag.putInt("Count", itemStack.getCount());
                // use single item, count is saved separately and would otherwise fail when exceeding 99
                Tag tag = ItemStack.SINGLE_ITEM_CODEC.encode(itemStack,
                        registries.createSerializationContext(NbtOps.INSTANCE),
                        compoundTag).getOrThrow();
                list.add(tag);
            }
        }

        return list;
    }

    public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> items, HolderLookup.Provider registries) {
        loadAllItems(tag.getListOrEmpty("Items"), items::set, items.size(), registries);
    }

    public static void loadAllItems(ListTag list, BiConsumer<Integer, ItemStack> consumer, int containerSize, HolderLookup.Provider registries) {
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag compoundTag = list.getCompoundOrEmpty(i);
            int j = compoundTag.getByteOr("Slot", (byte) 0) & 255;
            if (j < containerSize) {
                ItemStack itemStack = ItemStack.parse(registries, compoundTag).orElse(ItemStack.EMPTY);
                itemStack.setCount(compoundTag.getIntOr("Count", 0));
                consumer.accept(j, itemStack);
            }
        }
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
