package fuzs.limitlesscontainers.fabric.impl;

import fuzs.limitlesscontainers.fabric.api.limitlesscontainers.v1.LimitlessSlotStorage;
import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.init.ModRegistry;
import fuzs.limitlesscontainers.impl.world.level.block.entity.LimitlessChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.fabricmc.api.ModInitializer;

public class LimitlessContainersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainers::new);
        setupDevelopmentEnvironment();
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(LimitlessContainers.MOD_ID)) return;
        LimitlessSlotStorage.registerForBlockEntity((LimitlessChestBlockEntity blockEntity) -> {
            return blockEntity.container;
        }, ModRegistry.LIMITLESS_CHEST_BLOCK_ENTITY_TYPE);
    }
}
