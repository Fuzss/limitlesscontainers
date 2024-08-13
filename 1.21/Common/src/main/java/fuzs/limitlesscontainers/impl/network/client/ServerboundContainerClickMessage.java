package fuzs.limitlesscontainers.impl.network.client;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessByteBufUtils;
import fuzs.puzzleslib.api.network.v2.WritableMessage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ServerboundContainerClickMessage implements WritableMessage<ServerboundContainerClickMessage> {
    private final ServerboundContainerClickPacket packet;

    public ServerboundContainerClickMessage(int containerId, int stateId, int slotNum, int buttonNum, ClickType clickType, ItemStack carriedItem, Int2ObjectMap<ItemStack> changedSlots) {
        this.packet = new ServerboundContainerClickPacket(containerId, stateId, slotNum, buttonNum, clickType,
                carriedItem, changedSlots
        );
    }

    public ServerboundContainerClickMessage(FriendlyByteBuf friendlyByteBuf) {
        ServerboundContainerClickPacket packet = ServerboundContainerClickPacket.STREAM_CODEC.decode(
                (RegistryFriendlyByteBuf) friendlyByteBuf);
        Int2ObjectMap<ItemStack> changedSlots = Int2ObjectMaps.unmodifiable(friendlyByteBuf.readMap(
                FriendlyByteBuf.<Int2ObjectOpenHashMap<ItemStack>>limitValue(Int2ObjectOpenHashMap::new, 128),
                (FriendlyByteBuf friendlyByteBufx) -> {
                    return Integer.valueOf(friendlyByteBufx.readShort());
                }, LimitlessByteBufUtils::readItem
        ));
        ItemStack carriedItem = LimitlessByteBufUtils.readItem(friendlyByteBuf);
        this.packet = new ServerboundContainerClickPacket(packet.getContainerId(), packet.getStateId(),
                packet.getSlotNum(), packet.getButtonNum(), packet.getClickType(), carriedItem, changedSlots
        );
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        ServerboundContainerClickPacket.STREAM_CODEC.encode((RegistryFriendlyByteBuf) friendlyByteBuf, this.packet);
        friendlyByteBuf.writeMap(this.packet.getChangedSlots(), FriendlyByteBuf::writeShort,
                LimitlessByteBufUtils::writeItem
        );
        LimitlessByteBufUtils.writeItem(friendlyByteBuf, this.packet.getCarriedItem());
    }

    @Override
    public MessageHandler<ServerboundContainerClickMessage> makeHandler() {
        return new MessageHandler<>() {

            @Override
            public void handle(ServerboundContainerClickMessage message, Player player, Object instance) {
                ((ServerPlayer) player).connection.handleContainerClick(message.packet);
            }
        };
    }
}
