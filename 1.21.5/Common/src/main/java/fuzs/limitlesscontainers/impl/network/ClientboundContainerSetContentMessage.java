package fuzs.limitlesscontainers.impl.network;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessByteBufCodecs;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ClientboundContainerSetContentMessage(ClientboundContainerSetContentPacket packet) implements ClientboundPlayMessage {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundContainerSetContentMessage> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.CONTAINER_ID,
                    ClientboundContainerSetContentPacket::containerId,
                    ByteBufCodecs.VAR_INT,
                    ClientboundContainerSetContentPacket::stateId,
                    LimitlessByteBufCodecs.OPTIONAL_LIST_ITEM_STACK,
                    ClientboundContainerSetContentPacket::items,
                    LimitlessByteBufCodecs.OPTIONAL_ITEM_STACK,
                    ClientboundContainerSetContentPacket::carriedItem,
                    ClientboundContainerSetContentPacket::new)
            .map(ClientboundContainerSetContentMessage::new, ClientboundContainerSetContentMessage::packet);

    public ClientboundContainerSetContentMessage(int containerId, int stateId, List<ItemStack> items, ItemStack carriedItem) {
        this(new ClientboundContainerSetContentPacket(containerId, stateId, items, carriedItem));
    }

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                context.packetListener().handleContainerContent(ClientboundContainerSetContentMessage.this.packet);
            }
        };
    }
}
