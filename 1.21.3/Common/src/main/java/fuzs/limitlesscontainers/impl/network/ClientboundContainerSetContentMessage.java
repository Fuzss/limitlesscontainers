package fuzs.limitlesscontainers.impl.network;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessByteBufUtils;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetContentMessage implements WritableMessage<ClientboundContainerSetContentMessage> {
    private final ClientboundContainerSetContentPacket packet;

    public ClientboundContainerSetContentMessage(int containerId, int stateId, NonNullList<ItemStack> items, ItemStack carriedItem) {
        this.packet = new ClientboundContainerSetContentPacket(containerId, stateId, items, carriedItem);
    }

    public ClientboundContainerSetContentMessage(FriendlyByteBuf friendlyByteBuf) {
        ClientboundContainerSetContentPacket packet = ClientboundContainerSetContentPacket.STREAM_CODEC.decode(
                (RegistryFriendlyByteBuf) friendlyByteBuf);
        NonNullList<ItemStack> items = friendlyByteBuf.readCollection(NonNullList::createWithCapacity,
                LimitlessByteBufUtils::readItem
        );
        ItemStack carriedItem = LimitlessByteBufUtils.readItem(friendlyByteBuf);
        this.packet = new ClientboundContainerSetContentPacket(packet.getContainerId(), packet.getStateId(), items,
                carriedItem
        );
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        ClientboundContainerSetContentPacket.STREAM_CODEC.encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.packet);
        friendlyByteBuf.writeCollection(this.packet.getItems(), LimitlessByteBufUtils::writeItem);
        LimitlessByteBufUtils.writeItem(friendlyByteBuf, this.packet.getCarriedItem());
    }

    @Override
    public MessageHandler<ClientboundContainerSetContentMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(ClientboundContainerSetContentMessage message, Player player, Object instance) {
                ((LocalPlayer) player).connection.handleContainerContent(message.packet);
            }
        };
    }
}
