package fuzs.limitlesscontainers.neoforge.impl;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(LimitlessContainers.MOD_ID)
public class LimitlessContainersNeoForge {

    public LimitlessContainersNeoForge() {
        ModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainers::new);
    }
}
