package fuzs.limitlesscontainers.api.limitlesscontainers.v1;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * An extension of {@link Container} allowing for specifying a multiplier for the max stack size of items.
 */
public interface MultipliedContainer extends Container {

    @Override
    default int getMaxStackSize() {
        return Container.super.getMaxStackSize() * this.getStackSizeMultiplier();
    }

    default int getMaxStackSize(ItemStack itemStack) {
        return LimitlessContainerUtils.getMaxStackSize(itemStack, this.getStackSizeMultiplier()).orElseGet(this::getMaxStackSize);
    }

    int getStackSizeMultiplier();
}
