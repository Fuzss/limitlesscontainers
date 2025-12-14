package fuzs.limitlesscontainers.neoforge.impl;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.init.ModRegistry;
import fuzs.limitlesscontainers.impl.world.level.block.entity.LimitlessChestBlockEntity;
import fuzs.limitlesscontainers.neoforge.api.limitlesscontainers.v2.LimitlessSlotResourceHandler;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.neoforged.fml.common.Mod;

@Mod(LimitlessContainers.MOD_ID)
public class LimitlessContainersNeoForge {

    public LimitlessContainersNeoForge() {
        ModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainers::new);
        setupDevelopmentEnvironment();
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(LimitlessContainers.MOD_ID)) return;
        LimitlessSlotResourceHandler.registerLimitlessBlockEntityContainer((LimitlessChestBlockEntity blockEntity) -> {
            return blockEntity.container;
        }, ModRegistry.LIMITLESS_CHEST_BLOCK_ENTITY_TYPE);
    }
}
