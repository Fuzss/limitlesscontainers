package fuzs.limitlesscontainers.impl.network;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessByteBufCodecs;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ClientboundPlayMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.ItemStack;

public record ClientboundContainerSetSlotMessage(ClientboundContainerSetSlotPacket packet) implements ClientboundPlayMessage {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundContainerSetSlotMessage> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.CONTAINER_ID,
                    ClientboundContainerSetSlotPacket::getContainerId,
                    ByteBufCodecs.VAR_INT,
                    ClientboundContainerSetSlotPacket::getStateId,
                    ByteBufCodecs.SHORT.map(Short::intValue, Integer::shortValue),
                    ClientboundContainerSetSlotPacket::getSlot,
                    LimitlessByteBufCodecs.OPTIONAL_ITEM_STACK,
                    ClientboundContainerSetSlotPacket::getItem,
                    ClientboundContainerSetSlotPacket::new)
            .map(ClientboundContainerSetSlotMessage::new, ClientboundContainerSetSlotMessage::packet);

    public ClientboundContainerSetSlotMessage(int containerId, int stateId, int slot, ItemStack itemStack) {
        this(new ClientboundContainerSetSlotPacket(containerId, stateId, slot, itemStack));
    }

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                context.packetListener().handleContainerSetSlot(ClientboundContainerSetSlotMessage.this.packet);
            }
        };
    }
}
