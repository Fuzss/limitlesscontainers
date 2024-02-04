package fuzs.limitlesscontainers.forge.impl.client;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.client.LimitlessContainersClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = LimitlessContainers.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LimitlessContainersForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainersClient::new);
    }
}
