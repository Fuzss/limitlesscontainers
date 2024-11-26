package fuzs.limitlesscontainers.impl.world.level.block;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerSynchronizer;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.world.level.block.entity.LimitlessChestBlockEntity;
import fuzs.puzzleslib.api.block.v1.entity.TickingEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LimitlessChestBlock extends EnderChestBlock implements TickingEntityBlock<LimitlessChestBlockEntity> {

    public LimitlessChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof LimitlessChestBlockEntity blockEntity) {
                LimitlessContainerUtils.dropContents(level, pos, blockEntity.container);
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
            if (menuProvider != null) {
                LimitlessContainerSynchronizer.setSynchronizerFor((ServerPlayer) player,
                        player.openMenu(menuProvider).orElse(-1)
                );
            }
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return TickingEntityBlock.super.newBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return TickingEntityBlock.super.getTicker(level, state, blockEntityType);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof LimitlessChestBlockEntity blockEntity) {
            blockEntity.recheckOpen();
        }
    }

    @Override
    public BlockEntityType<? extends LimitlessChestBlockEntity> getBlockEntityType() {
        return (BlockEntityType<? extends LimitlessChestBlockEntity>) BuiltInRegistries.BLOCK_ENTITY_TYPE.get(
                LimitlessContainers.LIMITLESS_CHEST_IDENTIFIER);
    }
}
