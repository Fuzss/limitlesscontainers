package fuzs.limitlesscontainers.impl.network;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessByteBufUtils;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetSlotMessage implements WritableMessage<ClientboundContainerSetSlotMessage> {
    private final ClientboundContainerSetSlotPacket packet;

    public ClientboundContainerSetSlotMessage(int containerId, int stateId, int slot, ItemStack itemStack) {
        this.packet = new ClientboundContainerSetSlotPacket(containerId, stateId, slot, itemStack);
    }

    public ClientboundContainerSetSlotMessage(FriendlyByteBuf friendlyByteBuf) {
        ClientboundContainerSetSlotPacket packet = ClientboundContainerSetSlotPacket.STREAM_CODEC.decode(
                (RegistryFriendlyByteBuf) friendlyByteBuf);
        ItemStack itemStack = LimitlessByteBufUtils.readItem(friendlyByteBuf);
        this.packet = new ClientboundContainerSetSlotPacket(packet.getContainerId(), packet.getStateId(),
                packet.getSlot(), itemStack
        );
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        ClientboundContainerSetSlotPacket.STREAM_CODEC.encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.packet);
        LimitlessByteBufUtils.writeItem(friendlyByteBuf, this.packet.getItem());
    }

    @Override
    public MessageHandler<ClientboundContainerSetSlotMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(ClientboundContainerSetSlotMessage message, Player player, Object instance) {
                ((LocalPlayer) player).connection.handleContainerSetSlot(message.packet);
            }
        };
    }
}
