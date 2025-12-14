package fuzs.limitlesscontainers.api.limitlesscontainers.v1.client;

import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import fuzs.limitlesscontainers.impl.client.gui.AdvancedItemRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class LimitlessContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public LimitlessContainerScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack itemStack) {
        List<Component> tooltipLines = super.getTooltipFromContainerItem(itemStack);
        AdvancedItemRenderer.getStackSizeComponent(itemStack).ifPresent((Component component) -> {
            tooltipLines.add(tooltipLines.isEmpty() ? 0 : 1, component);
        });

        return tooltipLines;
    }

    @Override
    protected void renderFloatingItem(GuiGraphics guiGraphics, ItemStack itemStack, int x, int y, @Nullable String string) {
        guiGraphics.renderItem(itemStack, x, y);
        AdvancedItemRenderer.renderItemDecorations(guiGraphics,
                this.font,
                itemStack,
                x,
                y - (this.draggingItem.isEmpty() ? 0 : 8),
                string);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY) {
        int i = slot.x;
        int j = slot.y;
        ItemStack itemStack = slot.getItem();
        boolean bl = false;
        boolean bl2 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemStack2 = this.menu.getCarried();
        String string = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemStack.isEmpty()) {
            itemStack = itemStack.copyWithCount(itemStack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (LimitlessContainerUtils.canItemQuickReplace(slot, itemStack2, true) && this.menu.canDragTo(slot)) {
                bl = true;
//                int k = Math.min(itemStack2.getMaxStackSize(), slot.getMaxStackSize(itemStack2));
                int k = slot.getMaxStackSize(itemStack2);
                int l = slot.getItem().isEmpty() ? 0 : slot.getItem().getCount();
                int m = LimitlessContainerUtils.getQuickCraftPlaceCount(this.quickCraftSlots,
                        this.quickCraftingType,
                        itemStack2,
                        slot) + l;
                if (m > k) {
                    m = k;
                    string = ChatFormatting.YELLOW.toString() + k;
                }

                itemStack = itemStack2.copyWithCount(m);
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining(slot);
            }
        }

        if (itemStack.isEmpty() && slot.isActive()) {
            Identifier identifier = slot.getNoItemIcon();
            if (identifier != null) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, i, j, 16, 16);
                bl2 = true;
            }
        }

        if (!bl2) {
            if (bl) {
                guiGraphics.fill(i, j, i + 16, j + 16, -2130706433);
            }

            int k = slot.x + slot.y * this.imageWidth;
            if (slot.isFake()) {
                guiGraphics.renderFakeItem(itemStack, i, j, k);
            } else {
                guiGraphics.renderItem(itemStack, i, j, k);
            }

            AdvancedItemRenderer.renderItemDecorations(guiGraphics, this.font, itemStack, i, j, string);
        }
    }

    private void recalculateQuickCraftRemaining(Slot slot) {
        ItemStack itemStack = this.menu.getCarried();
        if (!itemStack.isEmpty() && this.isQuickCrafting) {
            if (this.quickCraftingType == 2) {
                this.quickCraftingRemainder = slot.getMaxStackSize(itemStack);
            } else {
                this.quickCraftingRemainder = itemStack.getCount();

                for (Slot quickCraftSlot : this.quickCraftSlots) {
                    ItemStack itemStack2 = quickCraftSlot.getItem();
                    int i = itemStack2.isEmpty() ? 0 : itemStack2.getCount();
                    int j = Math.min(itemStack.getMaxStackSize(), quickCraftSlot.getMaxStackSize(itemStack));
                    int k = Math.min(LimitlessContainerUtils.getQuickCraftPlaceCount(this.quickCraftSlots,
                            this.quickCraftingType,
                            itemStack,
                            quickCraftSlot) + i, j);
                    this.quickCraftingRemainder -= k - i;
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dragX, double dragY) {
        Slot slot = this.getHoveredSlot(mouseButtonEvent.x(), mouseButtonEvent.y());
        ItemStack itemStack = this.menu.getCarried();
        if (this.clickedSlot != null && this.minecraft.options.touchscreen().get()) {
            if (mouseButtonEvent.button() == 0 || mouseButtonEvent.button() == 1) {
                if (this.draggingItem.isEmpty()) {
                    if (slot != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                        this.draggingItem = this.clickedSlot.getItem().copy();
                    }
                } else if (this.draggingItem.getCount() > 1 && slot != null
                        && LimitlessContainerUtils.canItemQuickReplace(slot, this.draggingItem, false)) {
                    long l = Util.getMillis();
                    if (this.quickdropSlot == slot) {
                        if (l - this.quickdropTime > 500L) {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.slotClicked(slot, slot.index, 1, ClickType.PICKUP);
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                            this.quickdropTime = l + 750L;
                            this.draggingItem.shrink(1);
                        }
                    } else {
                        this.quickdropSlot = slot;
                        this.quickdropTime = l;
                    }
                }
            }
        } else if (this.isQuickCrafting && slot != null && !itemStack.isEmpty() && (
                itemStack.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2)
                && LimitlessContainerUtils.canItemQuickReplace(slot, itemStack, true) && slot.mayPlace(itemStack)
                && this.menu.canDragTo(slot)) {
            this.quickCraftSlots.add(slot);
            this.recalculateQuickCraftRemaining(slot);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        Slot slot = this.getHoveredSlot(mouseButtonEvent.x(), mouseButtonEvent.y());
        int i = this.leftPos;
        int j = this.topPos;
        boolean bl = this.hasClickedOutside(mouseButtonEvent.x(), mouseButtonEvent.y(), i, j);
        int k = -1;
        if (slot != null) {
            k = slot.index;
        }

        if (bl) {
            k = -999;
        }

        if (this.doubleclick && slot != null && mouseButtonEvent.button() == 0 && this.menu.canTakeItemForPickAll(
                ItemStack.EMPTY,
                slot)) {
            if (mouseButtonEvent.hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    for (Slot slot2 : this.menu.slots) {
                        if (slot2 != null && slot2.mayPickup(this.minecraft.player) && slot2.hasItem()
                                && slot2.container == slot.container && LimitlessContainerUtils.canItemQuickReplace(
                                slot2,
                                this.lastQuickMoved,
                                true)) {
                            this.slotClicked(slot2, slot2.index, mouseButtonEvent.button(), ClickType.QUICK_MOVE);
                        }
                    }
                }
            } else {
                this.slotClicked(slot, k, mouseButtonEvent.button(), ClickType.PICKUP_ALL);
            }

            this.doubleclick = false;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != mouseButtonEvent.button()) {
                this.isQuickCrafting = false;
                this.quickCraftSlots.clear();
                this.skipNextRelease = true;
                return true;
            }

            if (this.skipNextRelease) {
                this.skipNextRelease = false;
                return true;
            }

            if (this.clickedSlot != null && this.minecraft.options.touchscreen().get()) {
                if (mouseButtonEvent.button() == 0 || mouseButtonEvent.button() == 1) {
                    if (this.draggingItem.isEmpty() && slot != this.clickedSlot) {
                        this.draggingItem = this.clickedSlot.getItem();
                    }

                    boolean bl2 = LimitlessContainerUtils.canItemQuickReplace(slot, this.draggingItem, false);
                    if (k != -1 && !this.draggingItem.isEmpty() && bl2) {
                        this.slotClicked(this.clickedSlot,
                                this.clickedSlot.index,
                                mouseButtonEvent.button(),
                                ClickType.PICKUP);
                        this.slotClicked(slot, k, 0, ClickType.PICKUP);
                        if (this.menu.getCarried().isEmpty()) {
                            this.snapbackData = null;
                        } else {
                            this.slotClicked(this.clickedSlot,
                                    this.clickedSlot.index,
                                    mouseButtonEvent.button(),
                                    ClickType.PICKUP);
                            this.snapbackData = new AbstractContainerScreen.SnapbackData(this.draggingItem,
                                    new Vector2i((int) mouseButtonEvent.x(), (int) mouseButtonEvent.y()),
                                    new Vector2i(this.clickedSlot.x + i, this.clickedSlot.y + j),
                                    Util.getMillis());
                        }
                    } else if (!this.draggingItem.isEmpty()) {
                        this.snapbackData = new AbstractContainerScreen.SnapbackData(this.draggingItem,
                                new Vector2i((int) mouseButtonEvent.x(), (int) mouseButtonEvent.y()),
                                new Vector2i(this.clickedSlot.x + i, this.clickedSlot.y + j),
                                Util.getMillis());
                    }

                    this.clearDraggingState();
                }
            } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked(null,
                        -999,
                        AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType),
                        ClickType.QUICK_CRAFT);

                for (Slot slot2x : this.quickCraftSlots) {
                    this.slotClicked(slot2x,
                            slot2x.index,
                            AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType),
                            ClickType.QUICK_CRAFT);
                }

                this.slotClicked(null,
                        -999,
                        AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType),
                        ClickType.QUICK_CRAFT);
            } else if (!this.menu.getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse(mouseButtonEvent)) {
                    this.slotClicked(slot, k, mouseButtonEvent.button(), ClickType.CLONE);
                } else {
                    boolean bl2 = k != -999 && mouseButtonEvent.hasShiftDown();
                    if (bl2) {
                        this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                    }

                    this.slotClicked(slot, k, mouseButtonEvent.button(), bl2 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }

        this.isQuickCrafting = false;
        return true;
    }
}
