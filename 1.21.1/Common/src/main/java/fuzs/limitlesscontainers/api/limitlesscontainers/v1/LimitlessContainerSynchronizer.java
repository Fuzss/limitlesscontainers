package fuzs.limitlesscontainers.api.limitlesscontainers.v1;

import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.network.ClientboundContainerSetContentMessage;
import fuzs.limitlesscontainers.impl.network.ClientboundContainerSetSlotMessage;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.item.ItemStack;

public class LimitlessContainerSynchronizer implements ContainerSynchronizer {
    private final ServerPlayer player;

    public LimitlessContainerSynchronizer(ServerPlayer player) {
        this.player = player;
    }

    public static void setSynchronizerFor(ServerPlayer player, int containerId) {
        if (player.containerMenu instanceof LimitlessContainerMenu menu && menu.containerId == containerId) {
            menu.setActualSynchronizer(new LimitlessContainerSynchronizer(player));
        }
    }

    @Override
    public void sendInitialData(AbstractContainerMenu container, NonNullList<ItemStack> items, ItemStack carriedItem, int[] is) {
        LimitlessContainers.NETWORK.sendTo(this.player, new ClientboundContainerSetContentMessage(container.containerId, container.incrementStateId(), items, carriedItem).toClientboundMessage());

        for (int i = 0; i < is.length; ++i) {
            this.broadcastDataValue(container, i, is[i]);
        }
    }

    @Override
    public void sendSlotChange(AbstractContainerMenu container, int slot, ItemStack itemStack) {
        LimitlessContainers.NETWORK.sendTo(this.player, new ClientboundContainerSetSlotMessage(container.containerId, container.incrementStateId(), slot, itemStack).toClientboundMessage());
    }

    @Override
    public void sendCarriedChange(AbstractContainerMenu containerMenu, ItemStack stack) {
        LimitlessContainers.NETWORK.sendTo(this.player, new ClientboundContainerSetSlotMessage(-1, containerMenu.incrementStateId(), -1, stack).toClientboundMessage());
    }

    @Override
    public void sendDataChange(AbstractContainerMenu container, int id, int value) {
        this.broadcastDataValue(container, id, value);
    }

    private void broadcastDataValue(AbstractContainerMenu container, int id, int value) {
        this.player.connection.send(new ClientboundContainerSetDataPacket(container.containerId, id, value));
    }
}
