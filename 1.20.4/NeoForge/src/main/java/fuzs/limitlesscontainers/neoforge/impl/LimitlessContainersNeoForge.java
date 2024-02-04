package fuzs.limitlesscontainers.neoforge.impl;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(LimitlessContainers.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LimitlessContainersNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainers::new);
    }
}
