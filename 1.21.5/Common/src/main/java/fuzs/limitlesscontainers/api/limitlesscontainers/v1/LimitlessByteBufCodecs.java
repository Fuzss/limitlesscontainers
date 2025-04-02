package fuzs.limitlesscontainers.api.limitlesscontainers.v1;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class LimitlessByteBufCodecs {
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStack> OPTIONAL_ITEM_STACK = StreamCodec.of((RegistryFriendlyByteBuf buf, ItemStack itemStack) -> {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, itemStack);
        buf.writeInt(itemStack.getCount());
    }, (RegistryFriendlyByteBuf buf) -> {
        ItemStack itemStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
        itemStack.setCount(buf.readInt());
        return itemStack;
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ItemStack>> OPTIONAL_LIST_ITEM_STACK = OPTIONAL_ITEM_STACK.apply(
            ByteBufCodecs.collection(NonNullList::createWithCapacity));
}
