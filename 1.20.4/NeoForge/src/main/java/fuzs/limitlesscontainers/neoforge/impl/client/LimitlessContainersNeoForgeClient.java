package fuzs.limitlesscontainers.neoforge.impl.client;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.client.LimitlessContainersClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = LimitlessContainers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LimitlessContainersNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainersClient::new);
    }
}
