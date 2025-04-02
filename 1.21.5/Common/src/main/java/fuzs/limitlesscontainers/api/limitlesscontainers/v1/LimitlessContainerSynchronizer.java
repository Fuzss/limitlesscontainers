package fuzs.limitlesscontainers.api.limitlesscontainers.v1;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.mojang.serialization.DynamicOps;
import fuzs.limitlesscontainers.impl.network.ClientboundContainerSetContentMessage;
import fuzs.limitlesscontainers.impl.network.ClientboundContainerSetSlotMessage;
import fuzs.puzzleslib.api.network.v4.MessageSender;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.HashOps;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.RemoteSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LimitlessContainerSynchronizer implements ContainerSynchronizer {
    private final LoadingCache<TypedDataComponent<?>, Integer> cache;
    private final ServerPlayer serverPlayer;

    public LimitlessContainerSynchronizer(ServerPlayer serverPlayer) {
        this.cache = CacheBuilder.newBuilder().maximumSize(256L).build(new CacheLoader<>() {
            private final DynamicOps<HashCode> registryHashOps = serverPlayer.registryAccess()
                    .createSerializationContext(HashOps.CRC32C_INSTANCE);

            @Override
            public Integer load(TypedDataComponent<?> typedDataComponent) {
                return typedDataComponent.encodeValue(this.registryHashOps).getOrThrow((string) -> {
                    return new IllegalArgumentException("Failed to hash " + typedDataComponent + ": " + string);
                }).asInt();
            }
        });
        this.serverPlayer = serverPlayer;
    }

    public static void setSynchronizerFor(ServerPlayer player, int containerId) {
        if (player.containerMenu instanceof LimitlessContainerMenu menu && menu.containerId == containerId) {
            menu.setActualSynchronizer(new LimitlessContainerSynchronizer(player));
        }
    }

    @Override
    public void sendInitialData(AbstractContainerMenu container, List<ItemStack> items, ItemStack carriedItem, int[] is) {
        MessageSender.broadcast(PlayerSet.ofPlayer(this.serverPlayer),
                new ClientboundContainerSetContentMessage(container.containerId,
                        container.incrementStateId(),
                        items,
                        carriedItem));

        for (int i = 0; i < is.length; ++i) {
            this.broadcastDataValue(container, i, is[i]);
        }
    }

    @Override
    public void sendSlotChange(AbstractContainerMenu container, int slot, ItemStack itemStack) {
        MessageSender.broadcast(PlayerSet.ofPlayer(this.serverPlayer),
                new ClientboundContainerSetSlotMessage(container.containerId,
                        container.incrementStateId(),
                        slot,
                        itemStack));
    }

    @Override
    public void sendCarriedChange(AbstractContainerMenu containerMenu, ItemStack stack) {
        MessageSender.broadcast(PlayerSet.ofPlayer(this.serverPlayer),
                new ClientboundContainerSetSlotMessage(-1, containerMenu.incrementStateId(), -1, stack));
    }

    @Override
    public void sendDataChange(AbstractContainerMenu container, int id, int value) {
        this.broadcastDataValue(container, id, value);
    }

    @Override
    public RemoteSlot createSlot() {
        return new RemoteSlot.Synchronized(this.cache::getUnchecked);
    }

    private void broadcastDataValue(AbstractContainerMenu container, int id, int value) {
        this.serverPlayer.connection.send(new ClientboundContainerSetDataPacket(container.containerId, id, value));
    }
}
