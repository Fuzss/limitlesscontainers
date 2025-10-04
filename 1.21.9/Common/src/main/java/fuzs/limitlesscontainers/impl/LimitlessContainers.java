package fuzs.limitlesscontainers.impl;

import fuzs.limitlesscontainers.impl.init.ModRegistry;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitlessContainers implements ModConstructor {
    public static final String MOD_ID = "limitlesscontainers";
    public static final String MOD_NAME = "Limitless Containers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        setupDevelopmentEnvironment();
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(MOD_ID)) return;
        ModRegistry.bootstrap();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
