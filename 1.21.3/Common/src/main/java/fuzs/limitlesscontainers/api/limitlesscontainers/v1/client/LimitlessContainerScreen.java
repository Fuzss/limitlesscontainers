package fuzs.limitlesscontainers.api.limitlesscontainers.v1.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import fuzs.limitlesscontainers.impl.LimitlessContainers;
import fuzs.limitlesscontainers.impl.client.gui.AdvancedItemRenderer;
import fuzs.limitlesscontainers.impl.network.client.ServerboundContainerClickMessage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class LimitlessContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public LimitlessContainerScreen(T menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack itemStack) {
        List<Component> lines = super.getTooltipFromContainerItem(itemStack);
        AdvancedItemRenderer.getStackSizeComponent(itemStack).ifPresent(component -> {
            final int index = 1;
            if (index <= lines.size()) {
                lines.add(1, component);
            } else {
                lines.add(component);
            }
        });

        return lines;
    }

    @Override
    protected void renderFloatingItem(GuiGraphics guiGraphics, ItemStack itemStack, int i, int j, String string) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 232.0F);
        guiGraphics.renderItem(itemStack, i, j);
        AdvancedItemRenderer.renderItemDecorations(guiGraphics, this.font, itemStack, i, j - (this.draggingItem.isEmpty() ? 0 : 8), string);
        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        int posX = slot.x;
        int posY = slot.y;
        ItemStack itemStack = slot.getItem();
        boolean bl = false;
        boolean bl2 = slot == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
        ItemStack itemStack2 = this.menu.getCarried();
        String string = null;
        if (slot == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(itemStack.getCount() / 2);
        } else if (this.isQuickCrafting && this.quickCraftSlots.contains(slot) && !itemStack2.isEmpty()) {
            if (this.quickCraftSlots.size() == 1) {
                return;
            }

            if (LimitlessContainerUtils.canItemQuickReplace(slot, itemStack2, true) && this.menu.canDragTo(slot)) {
                itemStack = itemStack2.copy();
                bl = true;
                LimitlessContainerUtils.getQuickCraftSlotCount(this.quickCraftSlots,
                        this.quickCraftingType, itemStack, slot.getItem().isEmpty() ? 0 : slot.getItem().getCount(), slot);
                int k = slot.getMaxStackSize(itemStack);
                if (itemStack.getCount() > k) {
                    string = ChatFormatting.YELLOW.toString() + k;
                    itemStack.setCount(k);
                }
            } else {
                this.quickCraftSlots.remove(slot);
                this.recalculateQuickCraftRemaining(slot);
            }
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 100.0F);
        if (itemStack.isEmpty() && slot.isActive()) {
            Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
            if (pair != null) {
                TextureAtlasSprite textureAtlasSprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
                RenderSystem.setShaderTexture(0, textureAtlasSprite.atlasLocation());
                guiGraphics.blitSprite(RenderType::guiTextured, textureAtlasSprite, posX, posY, 16, 16);
                bl2 = true;
            }
        }

        if (!bl2) {
            if (bl) {
                guiGraphics.fill(posX, posY, posX + 16, posY + 16, -2130706433);
            }

            guiGraphics.renderItem(this.minecraft.player, itemStack, posX, posY, slot.x + slot.y * this.imageWidth);
            AdvancedItemRenderer.renderItemDecorations(guiGraphics, this.font, itemStack, posX, posY, string);
        }

        guiGraphics.pose().popPose();
    }

    private void recalculateQuickCraftRemaining(Slot slot) {
        ItemStack itemStack = this.menu.getCarried();
        if (!itemStack.isEmpty() && this.isQuickCrafting) {
            if (this.quickCraftingType == 2) {
                int quickCraftingRemainder1 = slot.getMaxStackSize(itemStack);
                this.quickCraftingRemainder = quickCraftingRemainder1;
            } else {
                int quickCraftingRemainder2 = itemStack.getCount();
                this.quickCraftingRemainder = quickCraftingRemainder2;

                for (Slot quickCraftSlot : this.quickCraftSlots) {
                    ItemStack itemStack2 = itemStack.copy();
                    ItemStack itemStack3 = quickCraftSlot.getItem();
                    int i = itemStack3.isEmpty() ? 0 : itemStack3.getCount();
                    LimitlessContainerUtils.getQuickCraftSlotCount(this.quickCraftSlots,
                            this.quickCraftingType, itemStack2, i, quickCraftSlot);
                    int j = quickCraftSlot.getMaxStackSize(itemStack2);
                    if (itemStack2.getCount() > j) {
                        itemStack2.setCount(j);
                    }

                    int quickCraftingRemainder1 = this.quickCraftingRemainder - (itemStack2.getCount() - i);
                    this.quickCraftingRemainder = quickCraftingRemainder1;
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Slot slot = this.getHoveredSlot(mouseX, mouseY);
        ItemStack itemStack = this.menu.getCarried();
        if (this.clickedSlot != null && this.minecraft.options.touchscreen().get()) {
            if (button == 0 || button == 1) {
                if (this.draggingItem.isEmpty()) {
                    if (slot != this.clickedSlot) {
                        if (!this.clickedSlot.getItem().isEmpty()) {
                            ItemStack draggingItem1 = this.clickedSlot.getItem().copy();
                            this.draggingItem = draggingItem1;
                        }
                    }
                } else {
                    if (this.draggingItem.getCount() > 1 && slot != null) {
                        if (LimitlessContainerUtils.canItemQuickReplace(slot, this.draggingItem, false)) {
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
                }
            }
        } else if (this.isQuickCrafting && slot != null && !itemStack.isEmpty() && (itemStack.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && LimitlessContainerUtils.canItemQuickReplace(slot, itemStack, true) && slot.mayPlace(itemStack) && this.menu.canDragTo(slot)) {
            this.quickCraftSlots.add(slot);
            this.recalculateQuickCraftRemaining(slot);
        }

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Slot slot = this.getHoveredSlot(mouseX, mouseY);
        int i = this.leftPos;
        int j = this.topPos;
        boolean bl = this.hasClickedOutside(mouseX, mouseY, i, j, button);
        int k = -1;
        if (slot != null) {
            k = slot.index;
        }

        if (bl) {
            k = -999;
        }

        if (this.doubleclick && slot != null && button == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
            if (hasShiftDown()) {
                if (!this.lastQuickMoved.isEmpty()) {
                    for (Slot slot2 : this.menu.slots) {
                        if (slot2 != null &&
                                slot2.mayPickup(this.minecraft.player) &&
                                slot2.hasItem() &&
                                slot2.container == slot.container &&
                                LimitlessContainerUtils.canItemQuickReplace(slot2, this.lastQuickMoved, true)) {
                            this.slotClicked(slot2, slot2.index, button, ClickType.QUICK_MOVE);
                        }
                    }
                }
            } else {
                this.slotClicked(slot, k, button, ClickType.PICKUP_ALL);
            }

            this.doubleclick = false;
            this.lastClickTime = 0L;
        } else {
            if (this.isQuickCrafting && this.quickCraftingButton != button) {
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
                if (button == 0 || button == 1) {
                    if (this.draggingItem.isEmpty()) {
                        if (slot != this.clickedSlot) {
                            ItemStack draggingItem1 = this.clickedSlot.getItem();
                            this.draggingItem = draggingItem1;
                        }
                    }

                    boolean bl2 = LimitlessContainerUtils.canItemQuickReplace(slot, this.draggingItem, false);
                    if (k != -1 && !this.draggingItem.isEmpty() && bl2) {
                        this.slotClicked(this.clickedSlot, this.clickedSlot.index, button, ClickType.PICKUP);
                        this.slotClicked(slot, k, 0, ClickType.PICKUP);
                        if (this.menu.getCarried().isEmpty()) {
                            this.snapbackItem = ItemStack.EMPTY;
                        } else {
                            this.slotClicked(this.clickedSlot, this.clickedSlot.index, button, ClickType.PICKUP);
                            this.snapbackStartX = Mth.floor(mouseX - (double) i);
                            this.snapbackStartY = Mth.floor(mouseY - (double) j);
                            this.snapbackEnd = this.clickedSlot;
                            this.snapbackItem = this.draggingItem;
                            long snapbackTime1 = Util.getMillis();
                            this.snapbackTime = snapbackTime1;
                        }
                    } else {
                        if (!this.draggingItem.isEmpty()) {
                            this.snapbackStartX = Mth.floor(mouseX - (double) i);
                            this.snapbackStartY = Mth.floor(mouseY - (double) j);
                            this.snapbackEnd = this.clickedSlot;
                            this.snapbackItem = this.draggingItem;
                            long snapbackTime1 = Util.getMillis();
                            this.snapbackTime = snapbackTime1;
                        }
                    }

                    this.clearDraggingState();
                }
            } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);

                for (Slot slot2 : this.quickCraftSlots) {
                    this.slotClicked(slot2, slot2.index, AbstractContainerMenu.getQuickcraftMask(1,
                            this.quickCraftingType
                    ), ClickType.QUICK_CRAFT);
                }

                this.slotClicked(null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
            } else if (!this.menu.getCarried().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse(button)) {
                    this.slotClicked(slot, k, button, ClickType.CLONE);
                } else {
                    boolean bl2 = k != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                    if (bl2) {
                        ItemStack lastQuickMoved1 = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                        this.lastQuickMoved = lastQuickMoved1;
                    }

                    this.slotClicked(slot, k, button, bl2 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }

        if (this.menu.getCarried().isEmpty()) {
            this.lastClickTime = 0L;
        }

        this.isQuickCrafting = false;
        return true;
    }

    @Override
    public void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot != null) {
            slotId = slot.index;
        }

        this.handleInventoryMouseClick(this.menu.containerId, slotId, mouseButton, type, this.minecraft.player);
    }

    private void handleInventoryMouseClick(int containerId, int slotId, int mouseButton, ClickType clickType, Player player) {
        AbstractContainerMenu abstractContainerMenu = player.containerMenu;
        if (containerId != abstractContainerMenu.containerId) {
            LimitlessContainers.LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", containerId, abstractContainerMenu.containerId);
        } else {
            NonNullList<Slot> nonNullList = abstractContainerMenu.slots;
            int i = nonNullList.size();
            List<ItemStack> list = Lists.newArrayListWithCapacity(i);

            for (Slot slot : nonNullList) {
                list.add(slot.getItem().copy());
            }

            abstractContainerMenu.clicked(slotId, mouseButton, clickType, player);
            Int2ObjectMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();

            for (int j = 0; j < i; ++j) {
                ItemStack itemStack = list.get(j);
                ItemStack itemStack2 = nonNullList.get(j).getItem();
                if (!ItemStack.matches(itemStack, itemStack2)) {
                    int2ObjectMap.put(j, itemStack2.copy());
                }
            }

            LimitlessContainers.NETWORK.sendToServer(new ServerboundContainerClickMessage(containerId, abstractContainerMenu.getStateId(), slotId, mouseButton, clickType, abstractContainerMenu.getCarried().copy(), int2ObjectMap).toServerboundMessage());
        }
    }
}
