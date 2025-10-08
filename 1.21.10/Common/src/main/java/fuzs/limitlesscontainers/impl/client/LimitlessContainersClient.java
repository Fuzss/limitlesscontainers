package fuzs.limitlesscontainers.impl.client;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.client.gui.screens.inventory.LimitlessChestScreen;
import fuzs.limitlesscontainers.impl.init.ModRegistry;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;

public class LimitlessContainersClient implements ClientModConstructor {

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(LimitlessContainers.MOD_ID)) return;
        context.registerMenuScreen(ModRegistry.LIMITLESS_CHEST_MENU.value(),
                LimitlessChestScreen.containerRows(6));
    }
}
