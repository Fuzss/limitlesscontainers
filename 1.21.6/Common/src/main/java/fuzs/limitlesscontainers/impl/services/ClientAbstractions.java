package fuzs.limitlesscontainers.impl.services;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public interface ClientAbstractions {
    ClientAbstractions INSTANCE = ServiceProviderHelper.load(ClientAbstractions.class);

    void renderItemOverlay(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int posX, int posY);
}
