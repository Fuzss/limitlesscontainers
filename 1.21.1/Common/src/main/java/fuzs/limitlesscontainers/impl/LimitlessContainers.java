package fuzs.limitlesscontainers.impl;

import fuzs.limitlesscontainers.impl.network.ClientboundContainerSetContentMessage;
import fuzs.limitlesscontainers.impl.network.ClientboundContainerSetSlotMessage;
import fuzs.limitlesscontainers.impl.network.client.ServerboundContainerClickMessage;
import fuzs.limitlesscontainers.impl.world.inventory.LimitlessChestMenu;
import fuzs.limitlesscontainers.impl.world.level.block.LimitlessChestBlock;
import fuzs.limitlesscontainers.impl.world.level.block.entity.LimitlessChestBlockEntity;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitlessContainers implements ModConstructor {
    public static final String MOD_ID = "limitlesscontainers";
    public static final String MOD_NAME = "Limitless Containers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .registerLegacyServerbound(ServerboundContainerClickMessage.class, ServerboundContainerClickMessage::new)
            .registerLegacyClientbound(ClientboundContainerSetSlotMessage.class,
                    ClientboundContainerSetSlotMessage::new
            )
            .registerLegacyClientbound(ClientboundContainerSetContentMessage.class,
                    ClientboundContainerSetContentMessage::new
            );

    public static final ResourceLocation LIMITLESS_CHEST_IDENTIFIER = id("limitless_chest");

    @Override
    public void onConstructMod() {
        setupDevelopmentEnvironment();
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) return;
        RegistryManager registryManager = RegistryManager.from(MOD_ID);
        Holder.Reference<Block> limitlessChestBlock = registryManager.registerBlock(
                LIMITLESS_CHEST_IDENTIFIER.getPath(),
                () -> new LimitlessChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ENDER_CHEST))
        );
        registryManager.registerBlockItem(limitlessChestBlock);
        registryManager.registerBlockEntityType(LIMITLESS_CHEST_IDENTIFIER.getPath(),
                () -> BlockEntityType.Builder.of(LimitlessChestBlockEntity::new, limitlessChestBlock.value())
        );
        registryManager.registerMenuType(LIMITLESS_CHEST_IDENTIFIER.getPath(), () -> LimitlessChestMenu::new);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
