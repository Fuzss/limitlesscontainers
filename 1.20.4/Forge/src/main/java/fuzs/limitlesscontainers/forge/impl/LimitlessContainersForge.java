package fuzs.limitlesscontainers.forge.impl;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(LimitlessContainers.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LimitlessContainersForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainers::new);
    }
}
