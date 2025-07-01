package fuzs.limitlesscontainers.fabric.impl;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class LimitlessContainersFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainers::new);
    }
}
