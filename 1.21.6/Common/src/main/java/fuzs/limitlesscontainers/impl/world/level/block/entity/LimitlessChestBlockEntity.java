package fuzs.limitlesscontainers.impl.world.level.block.entity;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.MultipliedContainer;
import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.world.inventory.LimitlessChestMenu;
import fuzs.puzzleslib.api.block.v1.entity.TickingBlockEntity;
import fuzs.puzzleslib.api.container.v1.ListBackedContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public class LimitlessChestBlockEntity extends ChestBlockEntity implements TickingBlockEntity {
    public static final int CONTAINER_SIZE = 54;

    private final ContainerOpenersCounter openersCounter = new NetherChestOpenersCounter();
    private final ChestLidController chestLidController = new ChestLidController();
    public final MultipliedContainer container = new NetherChestContainer();
    private final NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    public LimitlessChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(LimitlessContainers.LIMITLESS_CHEST_IDENTIFIER),
                blockPos,
                blockState);
    }

    @Override
    public void clientTick() {
        this.chestLidController.tickLid();
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return this.chestLidController.getOpenness(partialTicks);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.chestLidController.shouldBeOpen(type > 0);
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items.clear();
        LimitlessContainerUtils.loadAllItems(tag, this.items, registries);

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.remove("Items");
        LimitlessContainerUtils.saveAllItems(tag, this.items, true, registries);
    }

    @Override
    public void preRemoveSideEffects(BlockPos blockPos, BlockState blockState) {
        if (this.level != null) {
            LimitlessContainerUtils.dropContents(this.level, blockPos, this.container);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new LimitlessChestMenu(containerId, inventory, this.container);
    }

    @Override
    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    private class NetherChestContainer implements ListBackedContainer, MultipliedContainer {

        @Override
        public NonNullList<ItemStack> getContainerItems() {
            return LimitlessChestBlockEntity.this.items;
        }

        @Override
        public void startOpen(Player player) {
            if (!LimitlessChestBlockEntity.this.remove && !player.isSpectator()) {
                LimitlessChestBlockEntity.this.openersCounter.incrementOpeners(player,
                        LimitlessChestBlockEntity.this.getLevel(),
                        LimitlessChestBlockEntity.this.getBlockPos(),
                        LimitlessChestBlockEntity.this.getBlockState());
            }
        }

        @Override
        public void stopOpen(Player player) {
            if (!LimitlessChestBlockEntity.this.remove && !player.isSpectator()) {
                LimitlessChestBlockEntity.this.openersCounter.decrementOpeners(player,
                        LimitlessChestBlockEntity.this.getLevel(),
                        LimitlessChestBlockEntity.this.getBlockPos(),
                        LimitlessChestBlockEntity.this.getBlockState());
            }
        }

        @Override
        public void setChanged() {
            LimitlessChestBlockEntity.this.setChanged();
        }

        @Override
        public int getStackSizeMultiplier() {
            return 8;
        }
    }

    private class NetherChestOpenersCounter extends ContainerOpenersCounter {

        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            level.playSound(null,
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    SoundEvents.ENDER_CHEST_OPEN,
                    SoundSource.BLOCKS,
                    0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            level.playSound(null,
                    (double) pos.getX() + 0.5,
                    (double) pos.getY() + 0.5,
                    (double) pos.getZ() + 0.5,
                    SoundEvents.ENDER_CHEST_CLOSE,
                    SoundSource.BLOCKS,
                    0.5F,
                    level.random.nextFloat() * 0.1F + 0.9F);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
            level.blockEvent(pos, state.getBlock(), 1, openCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof LimitlessChestMenu netherChestMenu) {
                return netherChestMenu.getContainer() == LimitlessChestBlockEntity.this.container;
            } else {
                return false;
            }
        }
    }
}
