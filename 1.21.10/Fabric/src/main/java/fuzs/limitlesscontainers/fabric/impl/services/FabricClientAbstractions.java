package fuzs.limitlesscontainers.fabric.impl.services;

import fuzs.limitlesscontainers.impl.services.ClientAbstractions;
import net.fabricmc.fabric.api.client.rendering.v1.DrawItemStackOverlayCallback;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public final class FabricClientAbstractions implements ClientAbstractions {
    @Override
    public void renderItemOverlay(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int posX, int posY) {
        DrawItemStackOverlayCallback.EVENT.invoker().onDrawItemStackOverlay(guiGraphics, font, itemStack, posX, posY);
    }
}
