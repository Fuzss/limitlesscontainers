package fuzs.limitlesscontainers.impl.init;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.world.inventory.LimitlessChestMenu;
import fuzs.limitlesscontainers.impl.world.level.block.LimitlessChestBlock;
import fuzs.limitlesscontainers.impl.world.level.block.entity.LimitlessChestBlockEntity;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(LimitlessContainers.MOD_ID);
    public static final Holder.Reference<Block> LIMITLESS_CHEST_BLOCK = REGISTRIES.registerLazily(Registries.BLOCK,
            "limitless_chest");
    public static final Holder.Reference<Item> LIMITLESS_CHEST_ITEM = REGISTRIES.registerLazily(Registries.ITEM,
            "limitless_chest");
    public static final Holder.Reference<BlockEntityType<LimitlessChestBlockEntity>> LIMITLESS_CHEST_BLOCK_ENTITY_TYPE = REGISTRIES.registerLazily(
            Registries.BLOCK_ENTITY_TYPE,
            "limitless_chest");
    public static final Holder.Reference<MenuType<LimitlessChestMenu>> LIMITLESS_CHEST_MENU = REGISTRIES.registerLazily(
            Registries.MENU,
            "limitless_chest");

    public static void bootstrap() {
        REGISTRIES.registerBlock(LIMITLESS_CHEST_BLOCK.key().location().getPath(),
                LimitlessChestBlock::new,
                () -> BlockBehaviour.Properties.ofFullCopy(Blocks.ENDER_CHEST));
        REGISTRIES.registerBlockItem(LIMITLESS_CHEST_BLOCK);
        REGISTRIES.registerBlockEntityType(LIMITLESS_CHEST_BLOCK_ENTITY_TYPE.key().location().getPath(),
                LimitlessChestBlockEntity::new,
                LIMITLESS_CHEST_BLOCK);
        REGISTRIES.registerMenuType(LIMITLESS_CHEST_MENU.key().location().getPath(), LimitlessChestMenu::new);
    }
}
