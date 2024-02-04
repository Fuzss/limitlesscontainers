package fuzs.limitlesscontainers.impl.client.gui.screens.inventory;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.client.LimitlessContainerScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class LimitlessChestScreen<T extends AbstractContainerMenu> extends LimitlessContainerScreen<T> {
    private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
    private final int containerRows;

    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> MenuScreens.ScreenConstructor<M, S> containerRows(int containerRows) {
        return (M abstractContainerMenu, Inventory inventory, Component component) -> {
            return (S) new LimitlessChestScreen<>(abstractContainerMenu, inventory, component, containerRows);
        };
    }

    public LimitlessChestScreen(T menu, Inventory playerInventory, Component title, int containerRows) {
        super(menu, playerInventory, title);
        this.containerRows = containerRows;
        this.imageHeight = 114 + this.containerRows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
    }
}
