package fuzs.limitlesscontainers.neoforge.impl.client;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.client.LimitlessContainersClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = LimitlessContainers.MOD_ID, dist = Dist.CLIENT)
public class LimitlessContainersNeoForgeClient {

    public LimitlessContainersNeoForgeClient() {
        ClientModConstructor.construct(LimitlessContainers.MOD_ID, LimitlessContainersClient::new);
    }
}
