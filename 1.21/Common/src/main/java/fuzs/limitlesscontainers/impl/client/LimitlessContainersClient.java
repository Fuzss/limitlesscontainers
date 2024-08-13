package fuzs.limitlesscontainers.impl.client;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.client.gui.screens.inventory.LimitlessChestScreen;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.core.registries.BuiltInRegistries;

public class LimitlessContainersClient implements ClientModConstructor {

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) return;
        context.registerMenuScreen(BuiltInRegistries.MENU.get(LimitlessContainers.LIMITLESS_CHEST_IDENTIFIER),
                LimitlessChestScreen.containerRows(6)
        );
    }
}
