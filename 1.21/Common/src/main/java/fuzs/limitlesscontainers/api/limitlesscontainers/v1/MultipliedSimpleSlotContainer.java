package fuzs.limitlesscontainers.api.limitlesscontainers.v1;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

/**
 * An extension to {@link MultipliedSimpleContainer} that stores slot numbers together with the contained item stack when serializing.
 */
public class MultipliedSimpleSlotContainer extends MultipliedSimpleContainer {

    public MultipliedSimpleSlotContainer(int stackSizeMultiplier, int size) {
        super(stackSizeMultiplier, size);
    }

    public MultipliedSimpleSlotContainer(int stackSizeMultiplier, ItemStack... items) {
        super(stackSizeMultiplier, items);
    }

    @Override
    public void fromTag(ListTag containerNbt, HolderLookup.Provider registries) {
        LimitlessContainerUtils.loadAllItems(containerNbt, this::setItem, this.getContainerSize(), registries);
    }

    @Override
    public ListTag createTag(HolderLookup.Provider registries) {
        return LimitlessContainerUtils.saveAllItems(this::getItem, this.getContainerSize(), registries);
    }
}
