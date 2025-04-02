package fuzs.limitlesscontainers.api.limitlesscontainers.v1;

import fuzs.puzzleslib.api.network.v3.codec.ExtraStreamCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class LimitlessByteBufUtils {

    public static ItemStack readItem(FriendlyByteBuf friendlyByteBuf) {
        ItemStack itemStack = ExtraStreamCodecs.readItem(friendlyByteBuf);
        itemStack.setCount(friendlyByteBuf.readInt());
        return itemStack;
    }

    public static void writeItem(FriendlyByteBuf friendlyByteBuf, ItemStack itemStack) {
        ExtraStreamCodecs.writeItem(friendlyByteBuf, itemStack);
        friendlyByteBuf.writeInt(itemStack.getCount());
    }
}
